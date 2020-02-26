/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.merge;

import net.sf.ehcache.CacheException;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.CacheUtils;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Merge;
import org.parabuild.ci.object.MergeChangeList;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.object.MergeServiceConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 */
public final class MergeManager {

  private static final Log log = LogFactory.getLog(MergeManager.class);
  private static final MergeManager instance = new MergeManager();

  /**
   * Holds a set of {@link MergeDaemon} objects.
   */
  private final Map mergeDaemons = new HashMap(33);


  /**
   * Singleton constructor.
   */
  private MergeManager() {
  }


  public static MergeManager getInstance() {
    return instance;
  }


  public MergeConfiguration getMergeConfiguration(final int id) {
    return MergeDAO.getInstance().getMergeConfiguration(id);
  }


  public MergeConfiguration save(final MergeConfiguration mergeConfiguration) {
    return MergeDAO.getInstance().save(mergeConfiguration);
  }


  public List getMergeNamesByBuildID(final int buildID) {
    return (List)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(
          " select m.name from ActiveMergeConfiguration m, MergeServiceConfiguration am " +
            " where am.ID = m.ID and am.deleted = no " +
            "   and m.sourceBuildID = ? or m.targetBuildID = ? ");
        q.setCacheable(true);
        q.setInteger(0, buildID);
        q.setInteger(1, buildID);
        return q.list();
      }
    });
  }


  /**
   * Saves merge service configuration. If it is a new
   * configuration it should be added to the list of online
   * merges.
   *
   * @param mergeServiceConfiguration
   *
   * @return saved {@link MergeServiceConfiguration}
   */
  public MergeServiceConfiguration save(final MergeServiceConfiguration mergeServiceConfiguration) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.saveOrUpdateCopy(mergeServiceConfiguration);
      }
    });

    // start daemon if necessary
    final int mergeConfigurationID = mergeServiceConfiguration.getID();
    if (!mergeDaemons.containsKey(new Integer(mergeConfigurationID))) {
      startMergeDaemon(getActiveMergeConfiguration(mergeConfigurationID));
    }
    return mergeServiceConfiguration;
  }


  public MergeServiceConfiguration getActiveMerge(final int activeMergeID) {
    return (MergeServiceConfiguration)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.load(MergeServiceConfiguration.class, new Integer(activeMergeID));
      }
    });
  }


  public ActiveMergeConfiguration getActiveMergeConfiguration(final int id) {
    return (ActiveMergeConfiguration)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.load(ActiveMergeConfiguration.class, new Integer(id));
      }
    });
  }


  public void resumeMerge(final int id) {
    if (log.isDebugEnabled()) log.debug("resuming merge id: " + id);
    final MergeDAO mergeDAO = MergeDAO.getInstance();
    final MergeServiceConfiguration mergeServiceConfiguration = mergeDAO.getMergeServiceConfiguration(id);
    mergeServiceConfiguration.setStartupMode(MergeServiceConfiguration.STARTUP_MODE_ACTIVE);
    mergeDAO.save(mergeServiceConfiguration);
    getMergeDaemon(id).start();
  }


  /**
   * Stops merge and moves it to the paused mode.
   *
   * @param id merge ID
   */
  public void stopMerge(final int id) {
    if (log.isDebugEnabled()) log.debug("stopping merge id: " + id);
    final MergeDAO mergeDAO = MergeDAO.getInstance();
    final MergeServiceConfiguration mergeServiceConfiguration = mergeDAO.getMergeServiceConfiguration(id);
    mergeServiceConfiguration.setStartupMode(MergeServiceConfiguration.STARTUP_MODE_DISABLED);
    mergeDAO.save(mergeServiceConfiguration);
    getMergeDaemon(id).stop();
  }


  /**
   * Returns MergeDaemon
   *
   * @param id merge daemon ID
   *
   * @return Returns MergeDaemon or null if not found.
   */
  private MergeDaemon getMergeDaemon(final int id) {
    return (MergeDaemon)mergeDaemons.get(new Integer(id));
  }


  /**
   * Returns current list of {@link MergeState} objects
   *
   * @return {@link List} of {@link MergeState} objects
   */
  public List getMergeStatuses() {
    final Collection collection = this.mergeDaemons.values();
    final List result = new ArrayList(collection.size());
    for (final Iterator i = collection.iterator(); i.hasNext();) {
      final MergeDaemon mergeDaemon = (MergeDaemon)i.next();
      final MergeState state = new MergeState();
      final MergeConfiguration mergeConfiguration = getActiveMergeConfiguration(mergeDaemon.getActiveMergeConfigurationID()); // REVIEWME: optimization?
      state.setName(mergeConfiguration.getName());
      state.setStatus(mergeDaemon.getStatus());
      state.setActiveMergeConfigurationID(mergeDaemon.getActiveMergeConfigurationID());
      state.setMarker(mergeConfiguration.getMarker());
      state.setDescription(mergeConfiguration.getDescription());
      result.add(state);
//      if (log.isDebugEnabled()) log.debug("state: " + state);
    }
    return result;
  }


  public void removeMerge(final int id) {

    // mark as deleted
    final MergeDAO mergeDAO = MergeDAO.getInstance();
    final MergeServiceConfiguration mergeServiceConfiguration = mergeDAO.getMergeServiceConfiguration(id);
    mergeServiceConfiguration.setStartupMode(MergeServiceConfiguration.STARTUP_MODE_DISABLED);
    mergeServiceConfiguration.setDeleted(true);
    mergeDAO.save(mergeServiceConfiguration);

    // stop daemon
    stopMerge(id);

    // remove the merge from the list of daemons
    mergeDaemons.remove(new Integer(id));
  }


  /**
   * Shuts down all merges. See {@link MergeServiceImpl#shutdownService()}
   */
  void shutdownMerges() {
    // REVIEWME: simeshev@parabuilci.org -> implement
  }


  /**
   * Starts up merge configurations.
   */
  void startupMerges() {

    if (!mergeDaemons.isEmpty()) throw new IllegalStateException("Startup for merges can be requested only once.");

    // get a list of merges
    final List mergeList = getActiveMergeList();

    // iterate and create threads
    for (int i = 0; i < mergeList.size(); i++) {
      final ActiveMergeConfiguration configuration = (ActiveMergeConfiguration)mergeList.get(i);
      try {
        startMergeDaemon(configuration);
      } catch (final Exception e) {
        // We catch an exception to let others merges to start.
        notifyErrorWhileStartingMergeDaemon(configuration, e);
      }
    }
  }


  /**
   * This method creates a merge daemon for the given
   * configuration and starts it.
   *
   * @param configuration to create a daemon for.
   *
   * @return created {@link MergeDaemon}
   */
  private void startMergeDaemon(final ActiveMergeConfiguration configuration) {
    final Integer configurationID = new Integer(configuration.getID());
    if (mergeDaemons.containsKey(configurationID)) {
      // issue a warning
      notifyMergeDaemonAlreadyStarted(configuration);
      mergeDaemons.get(configurationID);
    } else {
      if (log.isDebugEnabled()) log.debug("starting merge daemon configuration: " + configuration);
      final MergeDaemon mergeDaemon = new MergeDaemon(configuration);
      mergeDaemons.put(configurationID, mergeDaemon);
      if (log.isDebugEnabled()) log.debug("started mergeDaemon: " + mergeDaemon);
    }
  }


  private static List getActiveMergeList() {
    return (List)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(
          " select m from ActiveMergeConfiguration m, MergeServiceConfiguration msc " +
            " where msc.ID = m.ID and msc.deleted = no ");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Starts previously paused merge.
   */
  public void startMerge(final int id) {
    final MergeDAO mergeDAO = MergeDAO.getInstance();
    final MergeServiceConfiguration mergeServiceConfiguration = mergeDAO.getMergeServiceConfiguration(id);
    mergeServiceConfiguration.setStartupMode(MergeServiceConfiguration.STARTUP_MODE_ACTIVE);
    mergeDAO.save(mergeServiceConfiguration);
    getMergeDaemon(id).start();
  }


  public List getMergeReport(final int activeMergeID, final int startingFrom, final int maxResults) {
    return (List)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List list = session.createQuery(
          " select chl, bchl.ID, bchl.mergeStatus from MergeConfiguration mc, BranchChangeList bchl, ChangeList chl" +
            " where mc.activeMergeID = ? " +
            "   and bchl.mergeConfigurationID = mc.ID " +
            "   and bchl.changeListID = chl.changeListID" +
            " order by chl.number desc")
          .setCacheable(true)
          .setInteger(0, activeMergeID)
          .setMaxResults(maxResults)
          .setFirstResult(startingFrom)
          .list();
        final List result = new ArrayList(list.size());
        for (final Iterator i = list.iterator(); i.hasNext();) {
          final Object[] objects = (Object[])i.next();
          final ChangeList chl = (ChangeList)objects[0];
          final Integer branchChangeListID = (Integer)objects[1];
          final Byte mergeStatus = (Byte)objects[2];
          result.add(new MergeReportImpl(mergeStatus, branchChangeListID, chl.getChangeListID(), chl.getNumber(), chl.getUser(), chl.getCreatedAt(), chl.getDescription()));
        }
        return result;
      }
    });
  }


  public int getMergeReportCount(final int activeMergeID) {
    return (Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
                " select count(bchl) from MergeConfiguration mc, BranchChangeList bchl" +
                        " where mc.activeMergeID = ? " +
                        "   and bchl.mergeConfigurationID = mc.ID ")
                .setCacheable(true)
                .setInteger(0, activeMergeID)
                .uniqueResult();
      }
    });
  }


  public List getQueueReport(final int activeMergeID, final int startingFrom, final int maxResults) {
    return (List)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        final Query buildRunIdQuery = session.createQuery(
          "select mtbr.buildRunID from MergeTargetBuildRun mtbr " +
          " where mtbr.mergeID = ? ");

// NOTE: vimeshev - commented out to support displaying successful merges
//        final List list = session.createQuery(
//          " select m, chl, mchl from MergeConfiguration mc, Merge m, MergeChangeList mchl, BranchChangeList bchl, ChangeList chl" +
//            " where " +
//            "   mc.activeMergeID = ? " +
//            "   and m.mergeConfigurationID = mc.ID" +
//            "   and mchl.mergeID = m.ID" +
//            "   and mchl.resultCode != ?" +
//            "   and mchl.branchChangeListID = bchl.ID " +
//            "   and bchl.changeListID = chl.changeListID" +
//            " order by chl.number desc")
//          .setCacheable(true)
//          .setInteger(0, activeMergeID)
//          .setByte(1, MergeChangeList.RESULT_SUCCESS)
//          .setMaxResults(maxResults)
//          .setFirstResult(startingFrom)
//          .list();
        final List list = session.createQuery(
          " select m, chl, mchl from MergeConfiguration mc, Merge m, MergeChangeList mchl, BranchChangeList bchl, ChangeList chl" +
            " where " +
            "   mc.activeMergeID = ? " +
            "   and m.mergeConfigurationID = mc.ID" +
            "   and mchl.mergeID = m.ID" +
            "   and mchl.branchChangeListID = bchl.ID " +
            "   and bchl.changeListID = chl.changeListID" +
            " order by chl.number desc")
          .setCacheable(true)
          .setInteger(0, activeMergeID)
          .setMaxResults(maxResults)
          .setFirstResult(startingFrom)
          .list();
        final List result = new ArrayList(list.size());
        for (final Iterator i = list.iterator(); i.hasNext();) {
          final Object[] objects = (Object[])i.next();
          final Merge m = (Merge)objects[0];
          final ChangeList chl = (ChangeList)objects[1];
          final MergeChangeList mchl = (MergeChangeList)objects[2];
          Integer buildRunID = null;
          if (m.getResultCode() == Merge.RESULT_VALIDATION_FAILED || mchl.getResultCode() == MergeChangeList.RESULT_SUCCESS) {
            // means we may have a build run ID
            buildRunIdQuery.setInteger(0, m.getID());
            buildRunID = (Integer)buildRunIdQuery.uniqueResult();
          }
          result.add(new MergeQueueReportImpl(m.getID(), m.getResultCode(), mchl.getID(), mchl.getResultCode(),
            mchl.getMergeResultDescription(), chl.getChangeListID(), chl.getNumber(), chl.getUser(),
            chl.getCreatedAt(), chl.getDescription(), buildRunID, m.isValidated()));
        }
        return result;
      }
    });
  }


  public int getQueueReportCount(final int activeMergeID) {
// NOTE: vimeshev - commented out to support displaying successful merges
//    return ((Integer)ConfigurationManager.runInHibernate(new TransactionCallback() {
//      public Object runInTransaction() throws Exception {
//        return session.createQuery(
//          " select count(mchl) from MergeConfiguration mc, Merge m, MergeChangeList mchl" +
//            " where " +
//            "   mc.activeMergeID = ? " +
//            "   and m.mergeConfigurationID = mc.ID" +
//            "   and mchl.mergeID = m.ID" +
//            "   and mchl.resultCode != ?")
//          .setCacheable(true)
//          .setInteger(0, activeMergeID)
//          .setByte(1, MergeChangeList.RESULT_SUCCESS)
//          .uniqueResult();
//      }
//    })).intValue();
    return (Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
                " select count(mchl) from MergeConfiguration mc, Merge m, MergeChangeList mchl" +
                        " where " +
                        "   mc.activeMergeID = ? " +
                        "   and m.mergeConfigurationID = mc.ID" +
                        "   and mchl.mergeID = m.ID")
                .setCacheable(true)
                .setInteger(0, activeMergeID)
                .uniqueResult();
      }
    });
  }


  /**
   * Returns a list of nags.
   *
   * @param activeMergeID
   * @see MergeNag
   */
  public List getNagReport(final int activeMergeID) {
    return (List)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        final List result = new ArrayList(111);

        String currentUser = "";
        List currentChangeLists = Collections.emptyList();

        final Iterator iterator = session.createQuery(
          " select chl from MergeConfiguration mc, BranchChangeList bchl, ChangeList chl, MergeChangeList mqm" +
            " where mc.activeMergeID = ? " +
            "   and bchl.mergeConfigurationID = mc.ID " +
            "   and mqm.branchChangeListID = bchl.ID" +
            "   and chl.changeListID = bchl.changeListID " +
            " order by chl.user, chl.number")
          .setCacheable(true)
          .setInteger(0, activeMergeID)
          .iterate();

        while (iterator.hasNext()) {
          final ChangeList changeList = (ChangeList)iterator.next();
          if (!changeList.getUser().equals(currentUser)) {
            if (!currentChangeLists.isEmpty()) {
              result.add(new MergeNagImpl(currentUser, currentChangeLists));
            }
            currentUser = changeList.getUser();
            currentChangeLists = new ArrayList(11);
          }
          if (currentChangeLists.size() <= 100) {
            currentChangeLists.add(changeList);
          }
        }
        return result;
      }
    });
  }


  /**
   * Helper method.
   *
   * @param mergeConfiguration
   */
  private static void notifyMergeDaemonAlreadyStarted(final ActiveMergeConfiguration mergeConfiguration) {
    ErrorManagerFactory.getErrorManager().reportSystemError(new Error("Automerge \"" + mergeConfiguration.getName() + "\" has already been started", Error.ERROR_LEVEL_WARNING));
  }


  /**
   * Helper method.
   *
   * @param mergeConfiguration
   */
  private static void notifyErrorWhileStartingMergeDaemon(final ActiveMergeConfiguration mergeConfiguration, final Exception e) {
    ErrorManagerFactory.getErrorManager().reportSystemError(new Error("Error while starting automerge \"" + mergeConfiguration.getName() + "\": " + StringUtils.toString(e), Error.ERROR_LEVEL_WARNING));
  }


  public String toString() {
    return "MergeManager{" +
      "mergeDaemons=" + mergeDaemons +
      '}';
  }


  /**
   * Deletes all merge related data.
   *
   * @param activeMergeConfigurationID
   */
  public void resetMerge(final int activeMergeConfigurationID) throws IOException {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete("from BranchMergeConfiguration bmc where bmc.activeMergeID = ?", new Integer(activeMergeConfigurationID), Hibernate.INTEGER);
        return null;
      }
    });
    try {
      CacheUtils.resetAllCaches();
    } catch (final CacheException e) {
      throw IoUtils.createIOException("Error while resetting caches", e);
    }
  }


  public static Integer getMergeConfigarationIDByBranchChangeListID(final int branchChangeListID) {
    return MergeDAO.getInstance().getMergeConfigarationIDByBranchChangeListID(branchChangeListID);
  }


  public ChangeList getChangeList(final int branchChangeListID, final int changeListID) {
    return MergeDAO.getInstance().getChangeList(branchChangeListID, changeListID);
  }
}