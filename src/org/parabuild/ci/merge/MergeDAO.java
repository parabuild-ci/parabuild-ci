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

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Query;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.BranchBuildRunParticipant;
import org.parabuild.ci.object.BranchChangeList;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Merge;
import org.parabuild.ci.object.MergeChangeList;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.object.MergeConfigurationAttribute;
import org.parabuild.ci.object.MergeServiceConfiguration;
import org.parabuild.ci.object.MergeSourceBuildRun;
import org.parabuild.ci.object.MergeTargetBuildRun;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Data access object for merge subsystem.
 */
public class MergeDAO {

  private static final MergeDAO instance = new MergeDAO();


  private MergeDAO() {
  }


  public Collection getUnmergedChangeLists(final int activeMergeID, final int startNextBlockMergeID, final int maxUnmergedChangeListSweepBlockSize) {
    // REVIEWME: simeshev@parabuilci.org -> currently gets all of them instead of unmerged
    return (Collection)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
          " select bchl, chl from MergeConfiguration mc, BranchChangeList bchl, ChangeList chl " +
            " where mc.activeMergeID = ? " +
            "   and mc.ID = bchl.mergeConfigurationID " +
            "   and bchl.mergeStatus = ? " +
            "   and bchl.ID > ? " +
            "   and bchl.changeListID = chl.changeListID " +
            "order by bchl.ID")
          .setMaxResults(maxUnmergedChangeListSweepBlockSize)
          .setCacheable(true)
          .setInteger(0, activeMergeID)
          .setByte(1, BranchChangeList.MERGE_STATUS_NOT_MERGED)
          .setInteger(2, startNextBlockMergeID)
          .list()
          ;
      }
    });
  }


  /**
   * Adds a given branch change list to a merge queue. A
   * merge queue an a list of.
   * 
   * @param branchChangeList
   */
  public void addToUnvalidatedMergeQueue(final BranchChangeList branchChangeList) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // save merge queue
        final Merge merge = new Merge();
        merge.setMergeConfigurationID(branchChangeList.getMergeConfigurationID());
        merge.setValidated(false);
        merge.setCreated(new Date());
        session.save(merge);
        // save merge queue member
        final MergeChangeList mergeChangeList = new MergeChangeList();
        mergeChangeList.setMergeID(merge.getID());
        mergeChangeList.setBranchChangeListID(branchChangeList.getID());
        session.save(mergeChangeList);
        return null;
      }
    });
  }


  public static MergeDAO getInstance() {
    return instance;
  }


  public List findChangeList(final String number, final Date createdAt, final String user) {
    return (List)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
          " select chl from  ChangeList chl " +
            " where chl.number = ? " +
            "   and chl.createdAt = ? " +
            "   and chl.user = ?")
          .setCacheable(true)
          .setString(0, number)
          .setDate(1, createdAt)
          .setString(2, user)
          .list()
          ;
      }
    });
  }


  public BranchChangeList findBranchChangeList(final int activeMergeConfigurationID, final String number, final Date createdAt, final String user) {
    return (BranchChangeList)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
          " select bchl from MergeConfiguration mc, BranchChangeList bchl, ChangeList chl " +
            " where mc.activeMergeID = ? " +
            "   and mc.ID = bchl.mergeConfigurationID " +
            "   and bchl.changeListID = chl.changeListID " +
            "   and chl.number = ? " +
            "   and chl.createdAt = ? " +
            "   and chl.user = ?")
          .setCacheable(true)
          .setInteger(0, activeMergeConfigurationID)
          .setString(1, number)
          .setTimestamp(2, createdAt)
          .setString(3, user)
          .uniqueResult()
          ;
      }
    });
  }


  public int save(final BranchChangeList branchChangeList) {
    return (Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdateCopy(branchChangeList);
        return Integer.valueOf(branchChangeList.getID());
      }
    });
  }


  public BranchChangeList getBrachChangeList(final int mergeConfigurationID, final int changeListID) {
    return (BranchChangeList)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // REVIEWME: simeshev@parabuilci.org -> lookup for active
        return session.createQuery(
          " select bchl from  BranchChangeList bchl " +
            " where bchl.mergeConfigurationID = ? " +
            "   and bchl.changeListID = ? ")
          .setCacheable(true)
          .setInteger(0, mergeConfigurationID)
          .setInteger(1, changeListID)
          .uniqueResult()
          ;
      }
    });
  }


  public ChangeList findLatestBranchChangeList(final int activeMergeID) {
    return (ChangeList)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // REVIEWME: vimeshev - 2007-07-24 - maybe not the
        // most efficent way to get the topmost. Also, when
        // synthetic change list numbers are used this may
        // no work. Date may be better.
        return session.createQuery(
          " select chl from MergeConfiguration mc, BranchChangeList bchl, ChangeList chl" +
            " where mc.activeMergeID = ? " +
            "   and bchl.mergeConfigurationID = mc.ID " +
            "   and bchl.changeListID = chl.changeListID" +
            " order by chl.number desc")
          .setCacheable(true)
          .setInteger(0, activeMergeID)
          .setMaxResults(1)
          .uniqueResult()
          ;
      }
    });
  }


  public MergeConfigurationAttribute getMergeConfigurationAttribute(final int mergeConfigurationID, final String name) {
    return (MergeConfigurationAttribute)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
          " select mca from MergeConfigurationAttribute mca" +
            " where mca.mergeConfigurationID = ? " +
            "   and mca.name = ? ")
          .setCacheable(true)
          .setInteger(0, mergeConfigurationID)
          .setString(1, name)
          .uniqueResult()
          ;
      }
    });
  }


  public MergeConfiguration save(final MergeConfiguration mergeConfiguration) {
    if (mergeConfiguration instanceof ActiveMergeConfiguration) {
      if (mergeConfiguration.getID() == MergeConfiguration.UNSAVED_ID) {
        ConfigurationManager.getInstance().saveObject(mergeConfiguration);
        mergeConfiguration.setActiveMergeID(mergeConfiguration.getID());
        ConfigurationManager.getInstance().saveObject(mergeConfiguration);
      } else {
        ConfigurationManager.getInstance().saveObject(mergeConfiguration);
      }
    } else {
      if (mergeConfiguration.getActiveMergeID() == MergeConfiguration.UNSAVED_ID) {
        throw new IllegalStateException("Runtime merge configuration should have active merge configuration set.");
      }
      ConfigurationManager.getInstance().saveObject(mergeConfiguration);
    }
    return mergeConfiguration;
  }


  public BranchMergeConfiguration findSameRuntime(final ActiveMergeConfiguration activeConfiguration) {
    return (BranchMergeConfiguration)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List list = session.createQuery(
          " select mc from BranchMergeConfiguration mc " +
            " where " +
            "   mc.indirectMerge = ? " +
            "   and mc.preserveMarker = ? " +
            "   and mc.reverseBranchView = ? " +
            "   and mc.branchViewSource = ? " +
            "   and mc.conflictResolutionMode = ? " +
            "   and mc.mergeMode = ? " +
            "   and mc.activeMergeID = ? " +
            "   and mc.sourceBuildID = ? " +
            "   and mc.targetBuildID = ? " +
            "   and mc.branchViewName = ? " +
            "   and mc.description = ? " +
            "   and mc.marker = ? " +
            "   and mc.name = ? "
        )
          .setCacheable(true)
          .setBoolean(0, activeConfiguration.isIndirectMerge())
          .setBoolean(1, activeConfiguration.isPreserveMarker())
          .setBoolean(2, activeConfiguration.isReverseBranchView())
          .setByte(3, activeConfiguration.getBranchViewSource())
          .setByte(4, activeConfiguration.getConflictResolutionMode())
          .setByte(5, activeConfiguration.getMergeMode())
          .setInteger(6, activeConfiguration.getActiveMergeID())
          .setInteger(7, activeConfiguration.getSourceBuildID())
          .setInteger(8, activeConfiguration.getTargetBuildID())
//          .setString(9, activeConfiguration.getBranchView())
          .setString(9, activeConfiguration.getBranchViewName())
          .setString(10, activeConfiguration.getDescription())
          .setString(11, activeConfiguration.getMarker())
          .setString(12, activeConfiguration.getName())
          .list();
        return list.isEmpty() ? null : list.get(0);
      }
    });
  }


  public boolean branchChangeListIsInQueue(final int branchChangeListID) {
    final Integer queueMemberID = (Integer)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
          " select mcl.ID from MergeChangeList mcl" +
            " where mcl.branchChangeListID = ?")
          .setCacheable(true)
          .setInteger(0, branchChangeListID)
          .uniqueResult();
      }
    });
    return queueMemberID != null;
  }


  public MergeChangeList findUnvalidatedMergeQueueMember(final int branchChangeListID) {
    return (MergeChangeList)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
          " select mcl from MergeChangeList mcl, Merge mq " +
            " where mq.ID = mcl.mergeID  " +
            "   and mq.validated = no" +
            "   and mcl.branchChangeListID = ?")
          .setCacheable(true)
          .setInteger(0, branchChangeListID)
          .uniqueResult();
      }
    });
  }


  public void save(final MergeConfigurationAttribute mca) {
    ConfigurationManager.getInstance().saveObject(mca);
  }


  /**
   * Returns merge confiuration attribute value as Integer.
   *
   * @param mergeID merge ID
   * @param name attribute name
   * @param defaultValue value if attribute doesn't exist
   *
   * @return merge confiuration attribute value as Integer.
   */
  public Integer getMergeConfigurationAttributeValue(final int mergeID, final String name, final Integer defaultValue) {
    final MergeConfigurationAttribute attribute = getMergeConfigurationAttribute(mergeID, name);
    if (attribute == null) return defaultValue;
    return Integer.valueOf(attribute.getValueAsInt());
  }


  public BranchBuildRunParticipant getBranchBuildRunChangeList(final int branchChangeListID, final int buildRunParticipantID) {
    return (BranchBuildRunParticipant)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
          " select bbrp from BranchBuildRunParticipant bbrp " +
            " where bbrp.branchChangeListID = ? " +
            "   and bbrp.buildRunParticipantID = ? ")
          .setCacheable(true)
          .setInteger(0, branchChangeListID)
          .setInteger(1, buildRunParticipantID)
          .uniqueResult()
          ;
      }
    });
  }


  public void save(final BranchBuildRunParticipant branchBuildRunParticipant) {
    ConfigurationManager.getInstance().saveObject(branchBuildRunParticipant);
  }


  public void save(final Merge merge) {
    ConfigurationManager.getInstance().saveObject(merge);
  }


  public void deleteMergeQueueMember(final int id) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete(" from MergeChangeList where ID = ? ", Integer.valueOf(id), Hibernate.INTEGER);
        return null;
      }
    });
  }


  public void save(final MergeChangeList mergeChangeList) {
    ConfigurationManager.getInstance().saveObject(mergeChangeList);
  }


  public void save(final MergeSourceBuildRun mergeSourceBuildRun) {
    ConfigurationManager.getInstance().saveObject(mergeSourceBuildRun);
  }


  public MergeServiceConfiguration getMergeServiceConfiguration(final int id) {
    return (MergeServiceConfiguration)ConfigurationManager.getInstance().getObject(MergeServiceConfiguration.class, id);
  }


  public void save(final MergeServiceConfiguration mergeServiceConfiguration) {
    ConfigurationManager.getInstance().saveObject(mergeServiceConfiguration);
  }


  /**
   * @return validated merge IDs ordered by build run IDs.
   */
  public List getValidatedMergeIDs(final int activeMergeID) {
    return (List)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(
          " select mq.ID from MergeConfiguration mc, Merge mq, MergeSourceBuildRun mqbr, BuildRun br " +
            " where mc.activeMergeID = ? " +
            "   and mc.ID = mq.mergeConfigurationID " +
            "   and mq.ID = mqbr.mergeID " +
            "   and mqbr.buildRunID = br.buildRunID" +
            "   and mq.validated = yes " +
            " order by br.buildRunID "
        );
        q.setCacheable(true);
        q.setInteger(0, activeMergeID);
        return q.list();
      }
    });
  }


  /**
   * Returns list of change lists belonging to a merge queue ordered by change list number.
   *
   * @param mergeID
   *
   * @return list of change lists belonging to a merge queue ordered by change list number.
   */
  public List getAllMergeChangeLists(final int mergeID) {
    return (List)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(
          " select chl, mcl from ChangeList chl, MergeChangeList mcl, BranchChangeList bchl " +
            " where mcl.mergeID = ? " +
            "   and mcl.branchChangeListID = bchl.ID " +
            "   and bchl.changeListID = chl.changeListID " +
            " order by chl.number"
        );
        q.setCacheable(true);
        q.setInteger(0, mergeID);
        return q.list();
      }
    });
  }


  /**
   * Returns list of change lists belonging to a merge queue that are pending (not merged) ordered by change list number.
   *
   * @param mergeID
   *
   * @return list of change lists belonging to a merge queue ordered by change list number.
   */
  public List getPendingMergeChangeLists(final int mergeID) {
    return (List)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(
          " select chl, mcl from ChangeList chl, MergeChangeList mcl, BranchChangeList bchl " +
            " where mcl.mergeID = ? " +
            "   and mcl.resultCode = ?" +
            "   and mcl.branchChangeListID = bchl.ID " +
            "   and bchl.changeListID = chl.changeListID " +
            " order by chl.number"
        );
        q.setCacheable(true);
        q.setInteger(0, mergeID);
        q.setByte(1, MergeChangeList.RESULT_NOT_MERGED);
        return q.list();
      }
    });
  }


  /**
   * Returns target active build ID for merge queue
   *
   * @param mergeID
   *
   * @return target active build ID for merge queue
   */
  public Integer getTargetBuildConfigurationIDForMergeQueue(final int mergeID) {
    return (Integer)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(
          " select mc.targetBuildID from MergeConfiguration mc, Merge mq " +
            " where mq.ID = ? " +
            "   and mq.mergeConfigurationID = mc.ID"
        );
        q.setCacheable(true);
        q.setInteger(0, mergeID);
        return q.uniqueResult();
      }
    });
  }


  public MergeConfiguration getMergeConfiguration(final int id) {
    return (MergeConfiguration)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.load(MergeConfiguration.class, Integer.valueOf(id));
      }
    });
  }


  public BranchMergeConfiguration getMergeQueueConfiguration(final int mergeID) {
    return (BranchMergeConfiguration)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(
          " select mc from BranchMergeConfiguration mc, Merge mq " +
            " where mq.ID = ? " +
            "   and mq.mergeConfigurationID = mc.ID "
        );
        q.setCacheable(true);
        q.setInteger(0, mergeID);
        return q.uniqueResult();
      }
    });
  }


  public Merge getMergeByID(final int mergeID) {
    return (Merge)ConfigurationManager.getInstance().getObject(Merge.class, mergeID);
  }


  public void save(final MergeTargetBuildRun targetBuildRun) {
    ConfigurationManager.getInstance().saveObject(targetBuildRun);
  }


  public void setMergeResult(final int mergeID, final byte resultCode) {
    final Merge merge = getMergeByID(mergeID);
    merge.setResultCode(resultCode);
    save(merge);
  }


  public Integer getMergeConfigarationIDByBranchChangeListID(final int branchChangeListID) {
    return (Integer)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
          " select mc.activeMergeID from MergeConfiguration mc, BranchChangeList bchl " +
            " where mc.ID = bchl.mergeConfigurationID " +
            "   and bchl.ID = ?")
          .setCacheable(true)
          .setInteger(0, branchChangeListID)
          .uniqueResult()
          ;
      }
    });
  }


  public ChangeList getChangeList(final int branchChangeListID, final int changeListID) {
    return (ChangeList)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery(
          " select chl from BranchChangeList bchl, ChangeList chl " +
            " where bchl.ID = ? " +
            "   and bchl.changeListID = ? " +
            "   and bchl.changeListID = chl.changeListID ")
          .setCacheable(true)
          .setInteger(0, branchChangeListID)
          .setInteger(1, changeListID)
          .uniqueResult()
          ;
      }
    });
  }
}
