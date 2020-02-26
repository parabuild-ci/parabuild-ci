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
package org.parabuild.ci.configuration;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.type.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildChangeList;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunActionVO;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.BuildRunParticipant;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.BuildWatcher;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Issue;
import org.parabuild.ci.object.IssueAttribute;
import org.parabuild.ci.object.IssueChangeList;
import org.parabuild.ci.object.IssueTracker;
import org.parabuild.ci.object.IssueTrackerProperty;
import org.parabuild.ci.object.LabelProperty;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.LogConfigProperty;
import org.parabuild.ci.object.ParallelBuildRunVO;
import org.parabuild.ci.object.PendingIssue;
import org.parabuild.ci.object.ReleaseNote;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.ResultConfigProperty;
import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.StepRunAttribute;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.object.TestCaseName;
import org.parabuild.ci.object.TestSuiteName;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.object.VCSUserToEmailMap;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.versioncontrol.VersionControlSystem;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * This is a class responsible for build management
 *
 */
public final class ConfigurationManager implements Serializable {

  private static final long serialVersionUID = 2749534373687207758L; // NOPMD
  private static final Log LOG = LogFactory.getLog(ConfigurationManager.class); // NOPMD

  public static final boolean validateActiveID = Boolean.valueOf(System.getProperty("parabuild.active.build.id.validation.enabled"));
  public static final String STR_DIGESTED_ADMIN = "21232F297A57A5A743894A0E4A801FC3";
  public static final String STARTUP_USER = System.getProperty("user.name", "");
  public static final String PARABUILD_WORK_DIR = "build";
  public static final boolean BLOCK_ROOT_USER = "root".equalsIgnoreCase(STARTUP_USER);
  public static final boolean BLOCK_ADMIN_USER = "Administrator".equalsIgnoreCase(STARTUP_USER) && !ConfigurationFile.getInstance().isAdministratorUserAllowed();

  private static final boolean builderMode = !StringUtils.isBlank(ConfigurationFile.getInstance().getBuildManagerAddress());
  private static final String HTML_LOG_URL_PREFIX = "/parabuild/build/log/html/";

  /**
   * Singleton instance.
   */
  private static final ConfigurationManager configManager = new ConfigurationManager();

  private SessionFactory sessionFactory = null; // NOPMD


  /**
   * Hidden constructor
   */
  private ConfigurationManager() {
  }


  /**
   */
  public static boolean isBuilderMode() {
    return builderMode;
  }


  /**
   * @param buildID - configuration ID, either build run copy or
   *                active.
   * @return list of schedule properties associated with the
   *         given configuration
   */
  public List getScheduleSettings(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from ScheduleProperty as sp where sp.buildID = ?");
        q.setInteger(0, buildID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * @param buildID      - configuration ID, either build run copy or
   *                     active.
   * @param propertyName name of the property.
   * @return schedule setting by name, <code>null</code>
   */
  public ScheduleProperty getScheduleSetting(final int buildID, final String propertyName) {
    return (ScheduleProperty) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from ScheduleProperty as sp where sp.buildID = ? and sp.propertyName = ?");
        q.setInteger(0, buildID);
        q.setString(1, propertyName);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * @param buildID      - configuration ID, either build run copy or
   *                     active.
   * @param propertyName name of the property.
   * @return schedule setting value by name, <code>defaultValue</code>
   *         if not found
   */
  public String getScheduleSettingValue(final int buildID, final String propertyName, final String defaultValue) {
    final String value = (String) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(" select sp.propertyValue from ScheduleProperty as sp "
                + " where sp.buildID = ? and sp.propertyName = ?");
        q.setInteger(0, buildID);
        q.setString(1, propertyName);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
    return value == null ? defaultValue : value;
  }


  /**
   * @return build setting by name, <code>null</code>
   */
  public BuildConfigAttribute getBuildAttribute(final int buildID, final String name) {
    return (BuildConfigAttribute) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from BuildConfigAttribute as ba where ba.buildID = ? and ba.propertyName = ?");
        q.setInteger(0, buildID);
        q.setString(1, name);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * @return build setting by name, <code>null</code>
   */
  public ActiveBuildAttribute getActiveBuildAttribute(final int activeBuildID, final String name) {
    return (ActiveBuildAttribute) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from ActiveBuildAttribute as aba where aba.buildID = ? and aba.propertyName = ?");
        q.setInteger(0, activeBuildID);
        q.setString(1, name);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   */
  public void createOrUpdateActiveBuildAttribute(final int activeBuildID, final String name, final int value) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        ActiveBuildAttribute aba = getActiveBuildAttribute(activeBuildID, name);
        if (aba == null) {
          aba = new ActiveBuildAttribute(activeBuildID, name, value);
        } else {
          aba.setPropertyValue(value);
        }
        session.saveOrUpdate(aba);
        return null;
      }
    });
  }


  public void createOrUpdateActiveBuildAttribute(final int activeBuildID, final String name, final String value) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        ActiveBuildAttribute aba = getActiveBuildAttribute(activeBuildID, name);
        if (aba == null) {
          aba = new ActiveBuildAttribute(activeBuildID, name, value);
        } else {
          aba.setPropertyValue(value);
        }
        session.saveOrUpdate(aba);
        return null;
      }
    });
  }


  /**
   * @param buildID      - build ID.
   * @param name         - attribute name.
   * @param defaultValue - value to return if build attribute
   *                     does not exists or is blank.
   * @return build setting by name. If build attribute does not
   *         exists or is blank, returns default value.
   */
  public String getBuildAttributeValue(final int buildID, final String name, final String defaultValue) {
    final BuildConfigAttribute ba = getBuildAttribute(buildID, name);
    if (ba == null || StringUtils.isBlank(ba.getPropertyValue())) {
      return defaultValue;
    } else {
      return ba.getPropertyValue();
    }
  }


  /**
   * @param buildID      - build ID.
   * @param name         - attribute name.
   * @param defaultValue - value to return if build attribute
   *                     does not exists or is blank.
   * @return build setting by name. If build attribute does not
   *         exists or is blank, returns default value.
   */
  public Integer getBuildAttributeValue(final int buildID, final String name, final Integer defaultValue) {
    final BuildConfigAttribute ba = getBuildAttribute(buildID, name);
    if (ba == null) {
      return defaultValue;
    }
    try {
      return new Integer(Integer.parseInt(ba.getPropertyValue()));
    } catch (final NumberFormatException e) {
      return defaultValue;
    }
  }


  /**
   * @return label setting by name, <code>null</code>
   */
  public LabelProperty getLabelSetting(final int buildID, final String propertyName) {
    return (LabelProperty) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery("from LabelProperty as lp where lp.buildID = ? and lp.propertyName = ?")
                .setInteger(0, buildID)
                .setString(1, propertyName)
                .setCacheable(true)
                .uniqueResult();
      }
    });
  }


  /**
   * @return build label settings
   */
  public List getLabelSettings(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery("from LabelProperty as lp where lp.buildID = ?")
                .setInteger(0, buildID)
                .setCacheable(true)
                .list();
      }
    });
  }


  /**
   * @return ordered build sequence
   */
  public List getAllBuildSequences(final int buildID, final BuildStepType type) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("from BuildSequence as bs order by bs.lineNumber where bs.buildID = ? and bs.type = ?");
        query.setInteger(0, buildID);
        query.setByte(1, type.byteValue());
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * @return ordered build sequence
   */
  public List getEnabledBuildSequences(final int buildID, final BuildStepType type) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("from BuildSequence as bs order by bs.lineNumber where bs.buildID = ? and bs.type = ? and bs.disabled='N'");
        query.setInteger(0, buildID);
        query.setByte(1, type.byteValue());
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  public boolean isLastEnabledBuildSequence(final int buildID, final BuildStepType type, final String stepName) {
    final int lastBuildSequenceID = (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // get last sequence ID
        final Query queryLastSequenceID = session.createQuery(
                "select bs.sequenceID from BuildSequence as bs " +
                        " where bs.buildID = ? and bs.type = ? " +
                        " and bs.lineNumber = (select max(bs_ln.lineNumber) from BuildSequence as bs_ln where bs_ln.buildID = ? and bs_ln.type = ? and bs_ln.disabled = 'N')");
        queryLastSequenceID.setInteger(0, buildID);
        queryLastSequenceID.setByte(1, type.byteValue());
        queryLastSequenceID.setInteger(2, buildID);
        queryLastSequenceID.setByte(3, type.byteValue());
        queryLastSequenceID.setCacheable(true);
        return queryLastSequenceID.uniqueResult();
      }
    });
    final BuildSequence bs = (BuildSequence) getObject(BuildSequence.class, lastBuildSequenceID);
    return bs.getStepName().equals(stepName);
  }


  public boolean isFirstBuildSequence(final int buildID, final BuildStepType type, final String stepName) {
    final String firstStepName = (String) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select bs_res.stepName from BuildSequence bs_res " +
                "  where bs_res.buildID = ? and bs_res.type = ? and bs_res.lineNumber = (select min(bs.lineNumber) from BuildSequence as bs where bs.buildID = ? and bs.type = ?)");
        query.setInteger(0, buildID);
        query.setByte(1, type.byteValue());
        query.setInteger(2, buildID);
        query.setByte(3, type.byteValue());
        query.setCacheable(true);
        return query.uniqueResult();
      }
    });
    return firstStepName.equals(stepName);
  }


  /**
   * Returns list of issue trackers.
   *
   * @param buildID for that to return list of configured issue
   *                trackers associated with the given build id.
   * @return list of configured issue trackers.
   * @see IssueTracker
   */
  public List getIssueTrackers(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find("from IssueTracker as itr, order by itr.ID where itr.buildID = ?",
                new Integer(buildID), Hibernate.INTEGER);
      }
    });
  }


  public IssueTracker getIssueTracker(final int trackerID) {
    return (IssueTracker) getObject(IssueTracker.class, trackerID);
  }


  /**
   * Finds configured issue trackers
   */
  public List findIssueTrackersByProjectAndAffectedVersions(final byte trackerType, final String project, final String affectedVersions) {
    // NOTE: vimeshev - 05/21/2004 - We use these deep joins
    // because we don't expect many issue trackers configured
    // in the system - one or two per a build.
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find(" select itr from IssueTracker as itr, IssueTrackerProperty as itp1 , IssueTrackerProperty as itp2, ActiveBuildConfig as abc, ActiveBuild as ab" +
                " where " +
                " ab.id = abc.buildID " +
                " and itr.buildID = abc.buildID " +
                " and itr.type = ? " +
                " and itr.id = itp1.trackerID " +
                " and itr.id = itp2.trackerID " +
                " and itp1.name = ? " +
                " and itp1.value = ? " +
                " and itp2.name = ? " +
                " and itp2.value = ? ",
                new Object[]{new Byte(trackerType), IssueTrackerProperty.JIRA_PROJECT, project, IssueTrackerProperty.JIRA_VERSIONS, affectedVersions},
                new Type[]{Hibernate.BYTE, Hibernate.STRING, Hibernate.STRING, Hibernate.STRING, Hibernate.STRING});
      }
    });
  }


  /**
   * @return Returns list of VCS user to e-mail maps associated
   *         with the given configuration.
   */
  public List getVCSUserToEmailMaps(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find("from VCSUserToEmailMap as map order by map.mapID where map.buildID = ?",
                new Integer(buildID), Hibernate.INTEGER);
      }
    });
  }


  /**
   * @return Returns list of build watchers associated with the
   *         given configuration.
   */
  public List getWatchers(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find("from BuildWatcher as bw order by bw.watcherID where bw.buildID = ?",
                new Integer(buildID), Hibernate.INTEGER);
      }
    });
  }


  /**
   * Get issue properties for given issue tracker.
   *
   * @return List of IssueTrackerProperties
   */
  public List getIssueTrackerProperties(final int issueTrackerID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find("from IssueTrackerProperty as itp where itp.trackerID = ?",
                new Integer(issueTrackerID), Hibernate.INTEGER);
      }
    });
  }


  /**
   * Get issue property value for a given issue tracker.
   *
   * @return String issue property value for a given issue
   *         tracker.
   */
  public String getIssueTrackerPropertyValue(final int issueTrackerID, final String name, final String defaultValue) {
    return (String) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select itp from IssueTrackerProperty as itp where itp.trackerID = ? and itp.name = ?");
        query.setInteger(0, issueTrackerID);
        query.setString(1, name);
        final IssueTrackerProperty property = (IssueTrackerProperty) query.uniqueResult();
        if (property == null || StringUtils.isBlank(property.getValue())) {
          return defaultValue;
        }
        return property.getValue();
      }
    });
  }


  /**
   * Get issue properties for given issue tracker.
   *
   * @return Map of IssueTrackerProperties with property name as
   *         a key
   */
  public Map getIssueTrackerPropertiesAsMap(final int issueTrackerID) {
    final Map result = new HashMap(5);
    final List list = getIssueTrackerProperties(issueTrackerID);
    for (final Iterator iter = list.iterator(); iter.hasNext(); ) {
      final IssueTrackerProperty property = (IssueTrackerProperty) iter.next();
      result.put(property.getName(), property);
    }
    return result;
  }


  public List getPendingIssues(final int activeBuildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        return session.find("from PendingIssue as pi where pi.buildID = ?",
                new Integer(activeBuildID), Hibernate.INTEGER);
      }
    });
  }


  /**
   * This method is used by tests.
   */
  public int getPendingIssueCountWithURLStartingWith(final int activeBuildID, final String urlStartWith) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() {
        int counter = 0;
        final List pendingIssues = getPendingIssues(activeBuildID);
        for (final Iterator i = pendingIssues.iterator(); i.hasNext(); ) {
          final PendingIssue pendingIssue = (PendingIssue) i.next();
          final Issue issue = (Issue) getObject(Issue.class, pendingIssue.getIssueID());
          if (issue.getUrl().startsWith(urlStartWith)) {
            counter++;
          }
        }
        return new Integer(counter);
      }
    });
  }


  public PendingIssue findPendingIssue(final int buildID, final int issueID) {
    return (PendingIssue) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List result = session.find("from PendingIssue as pi where pi.buildID = ? and pi.issueID = ?",
                new Object[]{new Integer(buildID), new Integer(issueID)},
                new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
        if (result == null || result.isEmpty()) {
          return null;
        }
        return result.get(0);
      }
    });
  }


  public void savePendingIssue(final PendingIssue pendingIssue) {
    // cover our ass up - save only if don' exist
    if (findPendingIssue(pendingIssue.getBuildID(), pendingIssue.getIssueID()) == null) {
      save(pendingIssue);
    }
  }


  public void save(final PendingIssue pendingIssue) {
    if (validateActiveID) {
      validateIsActiveBuildID(pendingIssue.getBuildID());
    }
    saveObject(pendingIssue);
  }


  /**
   * Moves issues from pending list to build run release notes.
   * Sets up release note URLs and filters. Remembered issues are
   * used during display time to take out markers.
   *
   * @return number of issues that has been moved from pending to
   *         release notes.
   */
  public Integer attachPendingIssuesToBuildRun(final int activeBuildID, final int buildRunID) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("processing pending release notes for: " + activeBuildID + '/' + buildRunID);
    }
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List pending = getPendingIssues(activeBuildID);
        if (LOG.isDebugEnabled()) {
          LOG.debug("pending.size() = " + pending.size());
        }
        int count = 0;
        for (final Iterator i = pending.iterator(); i.hasNext(); ) {
          final PendingIssue pendingIssue = (PendingIssue) i.next();
          // NOTE: simeshev@parabuilci.org 2009-12-13 - Check if this release note exist.
          // Not sure how it is possible, but apparently there is a situation where
          // there is a duplicate of issueID and buildRunID. See PARABUILD-1408.
          final Query q = session.createQuery("select rn from ReleaseNote as rn " +
                  " where rn.issueID = ? and rn.buildRunID = ? ");
          q.setInteger(0, pendingIssue.getIssueID());
          q.setInteger(1, buildRunID);
          q.setCacheable(true);
          if (!q.list().isEmpty()) {
            continue;
          }
          final ReleaseNote releaseNote = new ReleaseNote();
          releaseNote.setIssueID(pendingIssue.getIssueID());
          releaseNote.setBuildRunID(buildRunID);
          saveObject(releaseNote);
          deleteObject(pendingIssue);
          count++;
        }
        return new Integer(count);
      }
    });
  }


  /**
   * @return Returns map of VCS user to e-mail associated with
   *         the given configuration.
   *         <p/>
   *         The key of the map is lower case user name. The
   *         value is VCSUserToEmailMap object
   * @see ConfigurationManager#getVCSUserToEmailMaps
   */
  public Map getVCSUserToEmailMap(final int buildID) {
    final Map result = new HashMap(11);
    final List list = getVCSUserToEmailMaps(buildID);
    for (final Iterator iter = list.iterator(); iter.hasNext(); ) {
      final VCSUserToEmailMap userToEmailMap = (VCSUserToEmailMap) iter.next();
      result.put(userToEmailMap.getUserName().trim().toLowerCase(), userToEmailMap);
    }
    return result;
  }


  /**
   */
  public BuildRun getBuildRun(final int buildRunID) {
    if (buildRunID == BuildRun.UNSAVED_ID) {
      return null;
    }
    return (BuildRun) getObject(BuildRun.class, buildRunID);
  }


  /**
   * Returns list of las build runs limited by number of runs.
   * Result is sorted by descending date
   */
  public List getBuildRuns(final int activeBuildID, final int maxCount) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("select br from BuildRun as br " +
                " where br.activeBuildID = ? " +
                "   and br.type = ? " +
                " order by br.startedAt desc");
        q.setMaxResults(maxCount);
        q.setInteger(0, activeBuildID);
        q.setByte(1, BuildRun.TYPE_BUILD_RUN);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns list of last build runs limited by number of runs.
   * Result is sorted by descending started date
   */
  public List getCompletedBuildRuns(final int activeBuildID, final int firstResult, final int maxCount) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("select br from BuildRun as br " +
                " where br.activeBuildID = ? " +
                "   and br.complete = ? " +
                "   and br.type = ? " +
                " order by br.startedAt desc");
        q.setFirstResult(firstResult);
        q.setMaxResults(maxCount);
        q.setInteger(0, activeBuildID);
        q.setInteger(1, BuildRun.RUN_COMPLETE);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns a list of last build runs limited by number of runs. The result is sorted by descending finished date.
   *
   * @return the list of last build runs limited by number of runs.
   */
  public List getCompletedBuildRuns(final List activeBuildIDs, final int maxCount) {

    if (activeBuildIDs == null || activeBuildIDs.isEmpty()) {
      return Collections.emptyList();
    }

    // To list
    final StringBuffer list = new StringBuffer(100);
    final int activeBuildIDsSize = activeBuildIDs.size();
    for (int i = 0; i < activeBuildIDsSize; i++) {
      list.append(((Integer) activeBuildIDs.get(i)).toString());
      if (i < activeBuildIDsSize - 1) {
        list.append(',');
      }
    }

    // Execute
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select br from BuildRun as br " +
                " where br.activeBuildID in (" + list + ") " +
                "   and br.complete = ? " +
                "   and br.type = ? " +
                " order by br.finishedAt desc");
        q.setFirstResult(0);
        q.setMaxResults(maxCount);
        q.setByte(0, BuildRun.RUN_COMPLETE);
        q.setByte(1, BuildRun.TYPE_BUILD_RUN);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns a count of completed build runs.
   * <p/>
   * This method is used in conjunction with {@link
   * #getCompletedBuildRuns(int, int, int)} to support
   * pagination for the build runs table.
   */
  public int getCompletedBuildRunsCount(final int activeBuildID) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("select count(br) from BuildRun as br " +
                " where br.activeBuildID = ? " +
                "   and br.complete = ? " +
                "   and br.type = ? ");
        q.setInteger(0, activeBuildID);
        q.setInteger(1, BuildRun.RUN_COMPLETE);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * Returns list of las build runs limited by number of runs.
   * Result is sorted by descending date
   */
  public List getCompletedSuccessfulBuildRuns(final int activeBuildID, final int firstResult, final int maxCount) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("select br from BuildRun as br " +
                " where br.activeBuildID = ? " +
                "   and br.complete = ? " +
                "   and br.type = ? " +
                "   and br.resultID = ? " +
                " order by br.startedAt desc");
        q.setFirstResult(firstResult);
        q.setMaxResults(maxCount);
        q.setInteger(0, activeBuildID);
        q.setInteger(1, BuildRun.RUN_COMPLETE);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setByte(3, BuildRun.BUILD_RESULT_SUCCESS);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns list of las build runs limited by number of runs.
   * Result is sorted by descending date
   */
  public int getCompletedSuccessfulBuildRunsCount(final int activeBuildID) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("select count(br) from BuildRun as br " +
                " where br.activeBuildID = ? " +
                "   and br.complete = ? " +
                "   and br.type = ? " +
                "   and br.resultID = ? ");
        q.setInteger(0, activeBuildID);
        q.setInteger(1, BuildRun.RUN_COMPLETE);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setByte(3, BuildRun.BUILD_RESULT_SUCCESS);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * Returns list of last build runs starting from a given build run, limited by number of runs.
   * Result is sorted by date
   */
  public List getCompletedSuccessfulBuildRunIDs(final int activeBuildID, final int sinceBuildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select br.buildRunID from BuildRun as br " +
                " where br.activeBuildID = ? " +
                "   and br.complete = ? " +
                "   and br.type = ? " +
                "   and br.resultID = ? " +
                "   and br.buildRunID > ? " +
                "   and br.reRun = no " +
                " order by br.startedAt");
        q.setInteger(0, activeBuildID);
        q.setInteger(1, BuildRun.RUN_COMPLETE);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setByte(3, BuildRun.BUILD_RESULT_SUCCESS);
        q.setInteger(4, sinceBuildRunID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns list of las build runs limited by number of runs.
   * Result is sorted by descending date
   */
  public List getCompletedUnsuccessfulBuildRuns(final int activeBuildID, final int firstResult, final int maxCount) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("select br from BuildRun as br " +
                " where br.activeBuildID = ? " +
                "   and br.complete = ? " +
                "   and br.type = ? " +
                "   and br.resultID <> ? " +
                " order by br.startedAt desc");
        q.setFirstResult(firstResult);
        q.setMaxResults(maxCount);
        q.setInteger(0, activeBuildID);
        q.setInteger(1, BuildRun.RUN_COMPLETE);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setByte(3, BuildRun.BUILD_RESULT_SUCCESS);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns a count of las build runs limited by number of runs.
   * Result is sorted by descending date
   */
  public int getCompletedUnsuccessfulBuildRunsCount(final int activeBuildID) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("select count(br) from BuildRun as br " +
                " where br.activeBuildID = ? " +
                "   and br.complete = ? " +
                "   and br.type = ? " +
                "   and br.resultID <> ? ");
        q.setInteger(0, activeBuildID);
        q.setInteger(1, BuildRun.RUN_COMPLETE);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setByte(3, BuildRun.BUILD_RESULT_SUCCESS);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   */
  public BuildRunAttribute getBuildRunAttribute(final int buildRunID, final String name) {
    return (BuildRunAttribute) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from BuildRunAttribute as bra " +
                "where bra.buildRunID = ? and bra.name = ?");
        q.setInteger(0, buildRunID);
        q.setString(1, name);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   */
  public StepRunAttribute getStepRunAttribute(final int stepRunID, final String name) {
    return (StepRunAttribute) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from StepRunAttribute as sra " +
                "where sra.stepRunID = ? and sra.name = ?");
        q.setInteger(0, stepRunID);
        q.setString(1, name);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   */
  public Long getBuildRunAttributeValue(final int buildRunID, final String name, final Long defaultValue) {
    final String value = getBuildRunAttributeValue(buildRunID, name);
    if (StringUtils.isBlank(value)) {
      return defaultValue;
    }
    return Long.valueOf(value);
  }


  /**
   */
  public Integer getBuildRunAttributeValue(final int buildRunID, final String name, final Integer defaultValue) {
    final String value = getBuildRunAttributeValue(buildRunID, name);
    if (StringUtils.isBlank(value)) {
      return defaultValue;
    }
    return Integer.valueOf(value);
  }


  /**
   */
  public int getBuildRunAttributeValue(final int buildRunID, final String name, final int defaultValue) {
    return getBuildRunAttributeValue(buildRunID, name, new Integer(defaultValue));
  }


  public String getBuildRunAttributeValue(final int buildRunID, final String name) {
    return (String) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select bra.value from BuildRunAttribute as bra " +
                "where bra.buildRunID = ? and bra.name = ?");
        q.setInteger(0, buildRunID);
        q.setString(1, name);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   */
  public List getBuildRunAttributes(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from BuildRunAttribute as bra " +
                "where bra.buildRunID = ?");
        q.setInteger(0, buildRunID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public List getChangeListsOrderedByUserAndCommentAndDate(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select chl from BuildRunParticipant as brp, ChangeList chl " +
                "where brp.buildRunID = ? and brp.changeListID = chl.changeListID " +
                "order by chl.user, chl.description, chl.createdAt");
        query.setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns changelists sorted by desc dates
   */
  public List getChangeListsOrderedByDate(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select chl from BuildRunParticipant as brp, ChangeList chl " +
                "where brp.buildRunID = ? and brp.changeListID = chl.changeListID " +
                "order by chl.createdAt desc");
        query.setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns BuildRunParticipantVOs sorted by desc dates
   *
   * @see BuildRunParticipantVO
   */
  public List getBuildRunParticipantsOrderedByDate(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        final Query query = session.createQuery(" select chl, brp.firstBuildRunID, brp.firstBuildRunNumber, brp.participantID from BuildRunParticipant as brp, ChangeList chl " +
                "   where brp.buildRunID = ? and brp.changeListID = chl.changeListID order by chl.createdAt desc");
        query.setInteger(0, buildRunID);
        query.setCacheable(true);

        // make a list of BuildRunParticipantVOs
        final List list = query.list();
        final List result = new ArrayList(list.size());
        for (final Iterator i = list.iterator(); i.hasNext(); ) {
          final Object[] objects = (Object[]) i.next();
          result.add(new BuildRunParticipantVO((ChangeList) objects[0],
                  (Integer) objects[1], (Integer) objects[2], (Integer) objects[3]));
        }
        return result;
      }
    });
  }


  /**
   * Finds CVS changes by build run, time, user and comment
   *
   * @return List of Changes
   */
  public List getChanges(final int changeListID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select ch from Change as ch " +
                "where ch.changeListID = ? order by ch.filePath");
        query.setInteger(0, changeListID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Finds CVS changes by build run, time, user and comment
   *
   * @return List of Changes
   */
  public List getChanges(final ChangeList changeList) {
    return getChanges(changeList.getChangeListID());
  }


  /**
   * Returns list of StepRun objects for a given buildRunID
   *
   * @param buildRunID for which to return list of sequence runs
   * @return list of StepRun objects for a given buildRunID
   */
  public List getStepRuns(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("from StepRun as sr " +
                "where sr.buildRunID = ? order by sr.ID");
        query.setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns list of names of StepRun objects for a given buildRunID
   *
   * @param buildRunID for which to return list of sequence runs
   * @return list of StepRun objects for a given buildRunID
   */
  public List getStepRunNames(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select sr.name from StepRun as sr " +
                "where sr.buildRunID = ? order by sr.ID");
        query.setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns StepRun object for a given stepRunID
   *
   * @param stepRunID for which to return sequence run
   * @return StepRun object
   */
  public StepRun getStepRun(final int stepRunID) {
    return (StepRun) getObject(StepRun.class, stepRunID);
  }


  /**
   * @return list of user names participating in this
   *         sequence/build run
   */
  public List getBuildParticipantsNames(final StepRun stepRun) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select distinct chl.user from BuildRunParticipant brp, ChangeList as chl " +
                "where brp.buildRunID = ? and brp.changeListID = chl.changeListID")
                .setInteger(0, stepRun.getBuildRunID());
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns String list of VCS user names participating in this
   * build run ID.
   *
   * @param buildRunID int build run ID.
   * @return String list of VCS user names participating in this
   *         build run ID.
   */
  public List getBuildParticipantsNames(final int buildRunID) {
    // check if it's a saved build run
    if (buildRunID == BuildRun.UNSAVED_ID) {
      return Collections.emptyList();
    }
    // process
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // get this run participants
        return session.find("select distinct chl.user from BuildRunParticipant brp, ChangeList as chl " +
                "where brp.buildRunID = ? and brp.changeListID = chl.changeListID",
                new Integer(buildRunID), Hibernate.INTEGER);
      }
    });
  }


  /**
   * @return list of {@link StepLog} objects.
   */
  public List getAllStepLogs(final int stepRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("from StepLog sl " +
                "where sl.stepRunID = ? and sl.found = ? " +
                "order by sl.ID");
        query.setInteger(0, stepRunID);
        query.setByte(1, (byte) 1);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * @return build config by Sequence Log
   */
  public BuildRunConfig getBuildRunConfig(final StepLog stepLog) {
    return (BuildRunConfig) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select brc from BuildRunConfig brc, StepRun sr, BuildRun br " +
                " where sr.ID = ? and sr.buildRunID = br.buildRunID and br.buildID = brc.buildID ");
        q.setInteger(0, stepLog.getStepRunID());
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * @return list of StepLogs of given log type
   */
  public List getStepLogs(final StepRun stepRun, final int type) {
    return getStepLogs(stepRun.getID(), type);
  }


  /**
   * @return list of StepLogs of given log type
   */
  public List getStepLogs(final int stepRunID, final int type) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select sl from StepLog sl " +
                " where sl.stepRunID = ? and sl.type = ?" +
                " order by sl.ID");
        query.setInteger(0, stepRunID);
        query.setInteger(1, type);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * @return list of StepLogs of given log type
   */
  public List getMainAndWindowStepLogs(final StepRun stepRun) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select sl from StepLog sl " +
                " where sl.stepRunID = ? and (sl.type = ? or sl.type = ?)" +
                " order by sl.ID");
        query.setInteger(0, stepRun.getID());
        query.setInteger(1, StepLog.TYPE_MAIN);
        query.setInteger(2, StepLog.TYPE_WINDOW);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns first broken sequence log.
   * <p/>
   * If not found, returns null.
   */
  public StepLog getFirstBokenLog(final int buildRunID) {
    // REVIEWME: vimeshev - may be it's better to do it via database?
    return (StepLog) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() {
        final List stepRuns = configManager.getStepRuns(buildRunID);
        for (final Iterator stepRunIter = stepRuns.iterator(); stepRunIter.hasNext(); ) {
          // get run
          final StepRun stepRun = (StepRun) stepRunIter.next();
          if (stepRun.getResultID() == BuildRun.BUILD_RESULT_SUCCESS) {
            continue;
          }
          // try error window
          List stepLogs = configManager.getStepLogs(stepRun, StepLog.TYPE_WINDOW);
          if (!stepLogs.isEmpty()) {
            return stepLogs.get(0);
          }
          // try main seq log
          stepLogs = configManager.getStepLogs(stepRun, StepLog.TYPE_MAIN);
          if (!stepLogs.isEmpty()) {
            return stepLogs.get(0);
          }
        }
        return null;
      }
    });
  }


  /**
   * @return list of StepLogs
   */
  public StepLog getStepLog(final int logID) {
    return (StepLog) getObject(StepLog.class, logID);
  }


  /**
   * Saves build config
   *
   * @param buildConfig to save
   */
  public int save(final BuildConfig buildConfig) {
    final BuildConfig updated = (BuildConfig) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(buildConfig);
        return buildConfig;
      }
    });
    return updated.getBuildID();
  }


  /**
   * Saves build sequence
   *
   * @param buildSequence
   */
  public int save(final BuildSequence buildSequence) {
    final BuildSequence updated = (BuildSequence) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(buildSequence);
        return buildSequence;
      }
    });
    return updated.getSequenceID();
  }


  /**
   * Saves user to email map
   *
   * @param userToEmail
   */
  public void save(final VCSUserToEmailMap userToEmail) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(userToEmail);
        return new Integer(userToEmail.getMapID());
      }
    });
  }


  /**
   * Saves list of change lists.
   * <p/>
   * This method expects changeList sorted in reverse date
   * order.
   * <p/>
   * Returns max new changeList ID
   */
  public int saveBuildChangeLists(final int activeBuildID, final List changeLists) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        // traverse to last
        final ListIterator li = changeLists.listIterator();
        while (li.hasNext()) {
          li.next();
        }
        // iterate in reverse order
        int maxBuildChangeListID = ChangeList.UNSAVED_ID;
        while (li.hasPrevious()) {
          //
          //
          // save change list and changes
          final ChangeList chl = (ChangeList) li.previous();
          final int changeListID = saveChangeList(chl, session);
          maxBuildChangeListID = Math.max(maxBuildChangeListID, changeListID);
          //
          //
          // put to new changelist table
          final BuildChangeList buildChangeList = new BuildChangeList();
          buildChangeList.setBuildID(activeBuildID);
          buildChangeList.setChangeListID(chl.getChangeListID());
          buildChangeList.setChangeListCreatedAt(chl.getCreatedAt()); // changeListCreatedAt is used for speed purposes
          session.save(buildChangeList);
        }
        return new Integer(maxBuildChangeListID);
      }
    });
  }


  private static int saveChangeList(final ChangeList chl, final Session session) throws HibernateException {
    session.saveOrUpdate(chl);
    // set synthetic change list number if necessary
    final int changeListID = chl.getChangeListID();
    if (StringUtils.isBlank(chl.getNumber())) {
      chl.setNumber(Integer.toString(changeListID));
      session.saveOrUpdate(chl);
    }
    for (final Iterator iter = chl.getChanges().iterator(); iter.hasNext(); ) {
      final Change ch = (Change) iter.next();
      if (ch.getChangeListID() == ChangeList.UNSAVED_ID) {
        ch.setChangeListID(changeListID);
      }
      session.saveOrUpdate(ch);
    }
    return changeListID;
  }


  /**
   * Saves list of new change lists and binds issues.
   *
   * @return max new changeList ID
   */
  public int saveChangeListsAndIssues(final int buildID, final ChangeListsAndIssues changeListsAndIssues) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // 1. save change lists
        final int result1 = saveBuildChangeLists(buildID, changeListsAndIssues.getChangeLists());
        // 2. save change list-fixed issue bindings
        final List bindings = changeListsAndIssues.getChangeListIssueBindings();
        for (final Iterator iter = bindings.iterator(); iter.hasNext(); ) {
          final ChangeListIssueBinding binding = (ChangeListIssueBinding) iter.next();
          final ChangeList changeList = binding.getChangeList();
          // REVIEWME: consider looking up an issue first,
          // may be it's already there; take in account that
          // different issue trackers use different "lookup keys".
          final Issue issue = binding.getIssue();
          session.saveOrUpdate(issue);
          final IssueChangeList issueChangeList = new IssueChangeList(issue.getID(), changeList.getChangeListID());
          session.saveOrUpdate(issueChangeList);
        }
        return new Integer(result1);
      }
    });
  }


  /**
   * Saves VCS change
   *
   * @param change
   */
  public int save(final Change change) {
    final Change result = (Change) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(change);
        return change;
      }
    });
    return result.getChangeID();
  }


  /**
   * Saves build watcher
   */
  public void save(final BuildWatcher watcher) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(watcher);
        return new Integer(watcher.getWatcherID());
      }
    });
  }


  /**
   * Delete build watcher
   */
  public void delete(final BuildWatcher watcher) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete(watcher);
        return null;
      }
    });
  }


  /**
   * Saves build run participant
   *
   * @param participant
   */
  public int save(final BuildRunParticipant participant) {
    final BuildRunParticipant result = (BuildRunParticipant) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(participant);
        return participant;
      }
    });
    return result.getParticipantID();
  }


  /**
   * Saves a single sequence log
   */
  public int save(final StepLog stepLog) {
    final StepLog result = (StepLog) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(stepLog);
        return stepLog;
      }
    });
    return result.getID();
  }


  /**
   * Saves build run result
   *
   * @param buildRun
   */
  public int save(final BuildRun buildRun) {
    final BuildRun result = (BuildRun) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, buildRun.getActiveBuildID());
          validateIsBuildRunBuildID(session, buildRun.getBuildID());
        }
        session.saveOrUpdate(buildRun);
        return buildRun;
      }
    });
    return result.getBuildRunID();
  }


  /**
   * Saves sequence run result
   *
   * @param stepRun
   */
  public int save(final StepRun stepRun) {
    final StepRun result = (StepRun) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(stepRun);
        return stepRun;
      }
    });
    return result.getID();
  }


  /**
   * Saves sequence run result
   *
   * @param logConfig
   */
  public int save(final LogConfig logConfig) {
    final LogConfig result = (LogConfig) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(logConfig);
        return logConfig;
      }
    });
    return result.getID();
  }


  /**
   * Saves an object
   */
  public Serializable saveObject(final Serializable object) {
    return (Serializable) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(object);
        return object;
      }
    });
  }


  /**
   * Loads an object in transaction
   */
  public Object getObject(final Class clazz, final int ID) {
    return runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.get(clazz, new Integer(ID));
      }
    });
  }


  /**
   * Saves list of version control settings.
   *
   * @param settings
   * @noinspection HardcodedLineSeparator
   */
  public void saveSourceControlSettings(final int buildID, final List settings) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (final Iterator iter = settings.iterator(); iter.hasNext(); ) {
          final SourceControlSetting scp = (SourceControlSetting) iter.next();
          try {
            //if (log.isDebugEnabled()) log.debug("scp = " + scp);
            saveSourceControlSetting(session, buildID, scp);
          } catch (final Exception e) {
            throw new Exception("Error saving version control setting: "
                    + StringUtils.toString(e) + "\n\t " + scp, e);
          }
        }
        return null;
      }
    });
  }


  private static void saveSourceControlSetting(final Session session, final int buildID, final SourceControlSetting scp) throws HibernateException {
    if (scp.getPropertyName().equals(VCSAttribute.P4_DEPOT_PATH)) {
      // split
      final List parts = StringUtils.split(scp.getPropertyValue(), 4095);
      if (LOG.isDebugEnabled()) {
        LOG.debug("parts.size: " + parts.size());
      }
      // remove parts
      if (LOG.isDebugEnabled()) {
        LOG.debug("delete parts ");
      }
      final int deleted = session.delete("select scs from SourceControlSetting scs where scs.buildID = ? and scs.propertyName like '" + VCSAttribute.P4_DEPOT_PATH + "%'", new Object[]{new Integer(buildID)}, new Type[]{Hibernate.INTEGER});
      session.flush();
      if (LOG.isDebugEnabled()) {
        LOG.debug("deleted: " + deleted);
      }
      // insert
      if (LOG.isDebugEnabled()) {
        LOG.debug("insert parts ");
      }
      for (int i = 0, n = parts.size(); i < n; i++) {
        //if (log.isDebugEnabled()) log.debug("i = " + i);
        final String partName;
        if (i == 0) {
          partName = VCSAttribute.P4_DEPOT_PATH;
        } else {
          partName = VCSAttribute.P4_DEPOT_PATH_PART_PREFIX + '.' + StringUtils.formatWithTrailingZeroes(i, 4);
        }
        //if (log.isDebugEnabled()) log.debug("saving partSCP + " + partSCP);
        session.saveOrUpdateCopy(new SourceControlSetting(buildID, partName, (String) parts.get(i)));
      }
    } else {
      //if (log.isDebugEnabled()) log.debug("saving scp = " + scp);
      if (scp.getBuildID() == BuildConfig.UNSAVED_ID) {
        scp.setBuildID(buildID);
      }
      session.saveOrUpdateCopy(scp);
    }
  }


  /**
   * @return full list of version control settings for this
   *         configuration. If the build is a reference build, it
   *         will return list of settings for the referenced
   *         build.
   */
  public List getEffectiveSourceControlSettings(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() {
        final List result = new ArrayList(11);
        // fist add our own
        final List ourOwnSettings = getSourceControlSettings(buildID);
        result.addAll(ourOwnSettings);
        // if necessary add effective
        final Map map = settingsListToMap(ourOwnSettings);
        final BuildConfig buildConfig = getBuildConfiguration(buildID);
        if (buildConfig.getSourceControl() == VersionControlSystem.SCM_REFERENCE) {
          final BuildConfig effectiveBuildConfig = getEffectiveBuildConfig(buildConfig);
          final List settings = getSourceControlSettings(effectiveBuildConfig.getBuildID());
          for (final Iterator i = settings.iterator(); i.hasNext(); ) {
            final SourceControlSetting scs = (SourceControlSetting) i.next();
            if (map.containsKey(scs.getPropertyName())) {
              continue; // we use our own
            }
            if (scs.getPropertyName().equals(VCSAttribute.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE)) {
              continue; // Paren't custom checkout dir cannot be effective
            }
            result.add(scs);
          }
        }
        return result;
      }
    });
  }


  /**
   * @return full list of version control settings for this
   *         configuration.
   */
  public List getSourceControlSettings(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from SourceControlSetting as vcs where vcs.buildID = ?");
        q.setInteger(0, buildID);
        q.setCacheable(true);

        // this list will contains partial p4.depot.path items, if any
        final List perforceDepotPathParts = new ArrayList(23);

        // execute query
        final List list = q.list();
        for (final Iterator i = list.iterator(); i.hasNext(); ) {
          final SourceControlSetting scs = (SourceControlSetting) i.next();
          if (scs.getPropertyName().startsWith(VCSAttribute.P4_DEPOT_PATH)) {
            perforceDepotPathParts.add(scs);
            session.evict(scs);
            i.remove();
          }
        }

        // process parts, if any
        if (!perforceDepotPathParts.isEmpty()) {
          //if (log.isDebugEnabled()) log.debug("perforceDepotPathParts.size():" + perforceDepotPathParts.size());
          perforceDepotPathParts.sort(SourceControlSetting.PROPERTY_NAME_COMPARATOR);
          final StringBuilder depotPathValue = new StringBuilder(512);
          for (final Iterator j = perforceDepotPathParts.iterator(); j.hasNext(); ) {
            final SourceControlSetting peforceDeportPartSetting = (SourceControlSetting) j.next();
            depotPathValue.append(peforceDeportPartSetting.getPropertyValue());
          }
          final SourceControlSetting peforceDeportHead = (SourceControlSetting) perforceDepotPathParts.get(0);
          //if (log.isDebugEnabled()) log.debug("peforceDeportHead: " + peforceDeportHead);
          peforceDeportHead.setPropertyValue(depotPathValue.toString());
          // return to list
          list.add(peforceDeportHead);
        }
        return list;
      }
    });
  }


  /**
   * @return map of version control settings for this
   *         configuration. Key of the map is setting name. If
   *         the build is a reference build, it will return list
   *         of settings for the referenced build.
   */
  public Map getEffectiveSourceControlSettingsAsMap(final int buildID) {
    return settingsListToMap(getEffectiveSourceControlSettings(buildID));
  }


  /**
   * @return version control setting for the given buildID and
   *         setting name
   */
  public SourceControlSetting getSourceControlSetting(final int buildID, final String settingName) {
    if (settingName.equals(VCSAttribute.P4_DEPOT_PATH)) {
      return (SourceControlSetting) runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Query q = session.createQuery("from SourceControlSetting as vcs where vcs.buildID = ? and vcs.propertyName  like '" + VCSAttribute.P4_DEPOT_PATH + "%'");
          q.setInteger(0, buildID);
          q.setCacheable(true);

          // this list will contains partial p4.depot.path items, if any
          final List parts = new ArrayList(23);

          // execute query
          final List list = q.list();
          for (final Iterator i = list.iterator(); i.hasNext(); ) {
            final VCSAttribute scs = (VCSAttribute) i.next();
            parts.add(scs);
            session.evict(scs);
            i.remove();
          }

          if (parts.isEmpty()) {
            return null;
          }

          // process parts, if any
          parts.sort(SourceControlSetting.PROPERTY_NAME_COMPARATOR);
          final StringBuilder depotPathValue = new StringBuilder(512);
          for (final Iterator j = parts.iterator(); j.hasNext(); ) {
            final SourceControlSetting peforceDeportPartSetting = (SourceControlSetting) j.next();
            depotPathValue.append(peforceDeportPartSetting.getPropertyValue());
          }
          final SourceControlSetting peforceDepotHead = (SourceControlSetting) parts.get(0);
          peforceDepotHead.setPropertyValue(depotPathValue.toString());
          //if (log.isDebugEnabled()) log.debug("peforceDepotHead: " + peforceDepotHead);
          return peforceDepotHead;
        }
      });
    } else {
      return (SourceControlSetting) runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Query q = session.createQuery("from SourceControlSetting as vcs where vcs.buildID = ? and vcs.propertyName = ?");
          q.setInteger(0, buildID);
          q.setString(1, settingName);
          q.setCacheable(true);
          return q.uniqueResult();
        }
      });
    }
  }


  /**
   * @param defaultValue String value to return if the
   *                     property does not exist or it is value is blank.
   * @return version control setting value for the given buildID and
   *         setting name
   */
  public String getSourceControlSettingValue(final int buildID, final String settingName, final String defaultValue) {
    return (String) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select vcs.propertyValue from SourceControlSetting as vcs where vcs.buildID = ? and vcs.propertyName = ?");
        q.setInteger(0, buildID);
        q.setString(1, settingName);
        q.setCacheable(true);
        final String propertyValue = (String) q.uniqueResult();
        if (StringUtils.isBlank(propertyValue)) {
          return defaultValue;
        }
        return propertyValue;
      }
    });
  }


  /**
   * @param defaultValue String value to return if the
   *                     property does not exist or it is value is blank or it is not an integer.
   * @return version control setting value for the given buildID and
   *         setting name
   */
  public int getSourceControlSettingValue(final int buildID, final String settingName, final int defaultValue) {
    final String stringValue = getSourceControlSettingValue(buildID, settingName, null);
    if (StringUtils.isValidInteger(stringValue)) {
      return Integer.parseInt(stringValue);
    }
    return defaultValue;
  }


  /**
   * Saves list of version control settings
   *
   * @param settings
   */
  public void saveScheduleSettings(final int buildID, final List settings) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (final Iterator iter = settings.iterator(); iter.hasNext(); ) {
          final ScheduleProperty scheduleProperty = (ScheduleProperty) iter.next();
          if (scheduleProperty.getBuildID() == BuildConfig.UNSAVED_ID) {
            scheduleProperty.setBuildID(buildID);
          }
//          if (log.isDebugEnabled()) log.debug("scheduleProperty: " + scheduleProperty);
          session.saveOrUpdate(scheduleProperty);
        }
        return null;
      }
    });
  }


  /**
   * Saves list of build label settings
   *
   * @param settings
   */
  public void saveLabelSettings(final int buildID, final List settings) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (final Iterator iter = settings.iterator(); iter.hasNext(); ) {
          final LabelProperty labelProperty = (LabelProperty) iter.next();
          if (labelProperty.getBuildID() == BuildConfig.UNSAVED_ID) {
            labelProperty.setBuildID(buildID);
          }
          session.saveOrUpdate(labelProperty);
        }
        return null;
      }
    });
  }


  /**
   * Saves list of build attributes
   *
   * @param settings
   */
  public void saveBuildAttributes(final int buildID, final List settings) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (final Iterator iter = settings.iterator(); iter.hasNext(); ) {
          final BuildConfigAttribute ba = (BuildConfigAttribute) iter.next();
          if (ba.getBuildID() == BuildConfig.UNSAVED_ID) {
            ba.setBuildID(buildID);
          }
          session.saveOrUpdate(ba);
        }
        return null;
      }
    });
  }


  /**
   * List of currently available build configurations
   *
   * @return List of ActiveBuildConfigs
   */
  public List getExistingBuildConfigs() {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // REVIEWME: consider removing ordering by name
        final Query q = session.createQuery("select abc from Project p, ProjectBuild pb, ActiveBuild ab, ActiveBuildConfig as abc" +
                " order by abc.buildName " +
                " where p.deleted = no " +
                "   and p.ID = pb.projectID " +
                "   and pb.activeBuildID = ab.ID " +
                "   and ab.id = abc.id " +
                "   and ab.deleted = no");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Lists all ActiveBuildConfigs including deleted ones and those belonging to deleted projects.
   *
   * @return List of ActiveBuildConfigs
   */
  public List getAllBuildConfigurations() {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // REVIEWME: consider removing ordering by name
        final Query q = session.createQuery("select abc from ActiveBuildConfig as abc" +
                " order by abc.buildName");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Get a currently available active build configuration.
   * <p/>
   * A current build configuration is not deleted and
   * belongs to a project that is not deleted as well.
   *
   * @return currently available build configuration
   */
  public ActiveBuildConfig getExistingBuildConfig(final int activeBuildID) {
    return (ActiveBuildConfig) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // REVIEWME: consider removing ordering by name
        final Query q = session.createQuery("select abc from Project p, ProjectBuild pb, ActiveBuild ab, ActiveBuildConfig as abc" +
                " order by abc.buildName " +
                " where p.deleted = no " +
                "   and p.ID = pb.projectID " +
                "   and pb.activeBuildID = ab.ID " +
                "   and ab.id = abc.id " +
                "   and ab.deleted = no " +
                "   and ab.ID = ?");
        q.setCacheable(true);
        q.setInteger(0, activeBuildID);
        return q.uniqueResult();
      }
    });
  }


  /**
   * List of currently available build configurations
   */
  public List getExistingBuildConfigsOrderedByID() {
    final List existingBuildConfigs = getExistingBuildConfigs();
    existingBuildConfigs.sort(BuildConfig.ID_COMPARATOR);
    return existingBuildConfigs;
  }


  /**
   * List of currently available build configurations IDs
   */
  public List getExistingBuildConfigurationsIDs() {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select ab.ID from Project p, ProjectBuild pb, ActiveBuild ab" +
                " where p.deleted = no and p.ID = pb.projectID and pb.activeBuildID = ab.ID and ab.deleted = no");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * List of currently available log configurations
   */
  public List getLogConfigs(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from LogConfig as lc  where lc.buildID = ? order by lc.description");
        q.setInteger(0, buildID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * List of currently available log configurations
   */
  public List getLogConfigs(final int activeBuildID, final byte logType) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from LogConfig as lc  where lc.buildID = ? and lc.type = ?");
        q.setInteger(0, activeBuildID);
        q.setByte(1, logType);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * @return build log config for a given log config ID.
   */
  public LogConfig getLogConfig(final int logConfigID) {
    return (LogConfig) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.get(LogConfig.class, new Integer(logConfigID));
      }
    });
  }


  /**
   * List of currently available log configuration attributes
   */
  public List getLogConfigProperties(final int logConfigID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from LogConfigProperty as lcp  where lcp.logConfigID = ?");
        q.setInteger(0, logConfigID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * List of currently available result configurations
   */
  public List getResultConfigs(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from ResultConfig as rc  where rc.buildID = ? order by rc.description");
        q.setInteger(0, buildID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * List of currently available result configuration attributes
   */
  public List getResultConfigProperties(final int resultConfigID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find("from ResultConfigProperty as rcp  where rcp.resultConfigID = ?", new Integer(resultConfigID), Hibernate.INTEGER);
      }
    });
  }


  /**
   * Get Log configuration property.
   *
   * @return LogConfigProperty if found or null if not found.
   */
  public LogConfigProperty getLogConfigProperty(final int logConfigID, final String propName) {
    return (LogConfigProperty) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List result = session.find("from LogConfigProperty as lcp  where lcp.logConfigID = ? and lcp.name = ?",
                new Object[]{new Integer(logConfigID), propName},
                new Type[]{Hibernate.INTEGER, Hibernate.STRING});
        if (result.size() == 1) {
          return result.get(0);
        }
        return null;
      }
    });
  }


  /**
   * Get Log configuration property.
   *
   * @return LogConfigProperty if found or null if not found.
   */
  public String getLogConfigPropertyValue(final int logConfigID, final String propName, final String defaultValue) {
    final LogConfigProperty logConfigProperty = getLogConfigProperty(logConfigID, propName);
    if (logConfigProperty == null || StringUtils.isBlank(logConfigProperty.getValue())) {
      return defaultValue;
    }
    return logConfigProperty.getValue();
  }


  /**
   * Get Result configuration property.
   *
   * @return ResultConfigProperty if found or null if not found.
   */
  public ResultConfigProperty getResultConfigProperty(final int resultConfigID, final String propName) {
    return (ResultConfigProperty) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List result = session.find("from ResultConfigProperty as rcp  where rcp.resultConfigID = ? and rcp.name = ?",
                new Object[]{new Integer(resultConfigID), propName},
                new Type[]{Hibernate.INTEGER, Hibernate.STRING});
        if (result.size() == 1) {
          return result.get(0);
        }
        return null;
      }
    });
  }


  /**
   * @return List of build attributes
   */
  public List getBuildAttributes(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find("from BuildConfigAttribute as ba where ba.buildID = ?",
                new Object[]{new Integer(buildID)},
                new Type[]{Hibernate.INTEGER});
      }
    });
  }


  /**
   * @return Map of currently existing builds with Integer key
   *         containing buildID
   */
  public Map getBuildConfigurationsMap() {
    final HashMap result = new HashMap(11);
    final List configs = getExistingBuildConfigs();
    for (final Iterator iter = configs.iterator(); iter.hasNext(); ) {
      final BuildConfig buildConfig = (BuildConfig) iter.next();
      result.put(new Integer(buildConfig.getBuildID()), buildConfig);
    }
    return result;
  }


  /**
   * Gets build config by ID
   */
  public BuildConfig getBuildConfiguration(final int ID) {
    return (BuildConfig) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select bc from BuildConfig as bc " +
                "where bc.buildID = ?");
        q.setCacheable(true);
        q.setInteger(0, ID);
        return q.uniqueResult();
      }
    });
  }


  /**
   * @return true if the changelist belongs to the build.
   */
  public boolean isChangeListBelongsToBuild(final int changeListID, final int activeBuildID) {
    return (Boolean) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("" +
                " select brp.participantID from BuildRunParticipant as brp, BuildRun as br " +
                " where brp.buildRunID = br.buildRunID" +
                "   and brp.changeListID = ?" +
                "   and br.activeBuildID = ? ");
        q.setInteger(0, changeListID);
        q.setInteger(1, activeBuildID);
        q.setCacheable(true);
        q.setMaxResults(1);
        return Boolean.valueOf(!q.list().isEmpty());
      }
    });
  }


  /**
   * Delete "hibernated" object in TX
   */
  public void deleteObject(final Object object) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete(object);
        return null;
      }
    });
  }


  /**
   * Delete build sequences
   */
  public void delete(final BuildSequence sequence) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete(sequence);
        return null;
      }
    });
  }


  /**
   * Get schedule items
   */
  public List getScheduleItems(final int buildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from ScheduleItem as sc " +
                "where sc.buildID = ?");
        q.setCacheable(true);
        q.setInteger(0, buildID);
        return q.list();
      }
    });
  }


  /**
   * Delete build schedule items
   */
  public void deleteScheduleItems(final List scheduleItems) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (final Iterator i = scheduleItems.iterator(); i.hasNext(); ) {
          session.delete(i.next());
        }
        return null;
      }
    });
  }


  /**
   * Save build schedule items
   */
  public void saveScheduleItems(final List scheduleItems) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (final Iterator i = scheduleItems.iterator(); i.hasNext(); ) {
          session.saveOrUpdate(i.next());
        }
        return null;
      }
    });
  }


  /**
   * Delete user to email map
   */
  public void delete(final VCSUserToEmailMap userToEmail) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete(userToEmail);
        return null;
      }
    });
  }


  /**
   * Finds build config by build name among active builds that
   * are not deleted.
   *
   * @param name of the build
   * @return found BuildConfig, null if not found
   */
  public BuildConfig findActiveBuildConfigByName(final String name) {
    return (BuildConfig) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select abc from Project p, ProjectBuild pb, ActiveBuild ab, ActiveBuildConfig as abc" +
                " order by abc.buildName " +
                " where p.deleted = no " +
                "   and p.ID = pb.projectID " +
                "   and pb.activeBuildID = ab.ID " +
                "   and ab.id = abc.id " +
                "   and ab.deleted = no" +
                "   and UPPER(abc.buildName) = ?");
        q.setString(0, name.trim().toUpperCase());
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * Finds change list header by ID
   */
  public ChangeList getChangeList(final int changeListID) {
    return (ChangeList) getObject(ChangeList.class, changeListID);
  }


  /**
   * Finds change list header by build ID and ID
   */
  public ChangeList getChangeList(final int activeBuildID, final int changeListID) {
    return (ChangeList) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("" +
                " select chl from ChangeList chl, BuildRunParticipant as brp, BuildRun as br " +
                " where chl.changeListID = brp.changeListID" +
                "   and brp.buildRunID = br.buildRunID" +
                "   and brp.changeListID = ?" +
                "   and br.activeBuildID = ? ");
        q.setInteger(0, changeListID);
        q.setInteger(1, activeBuildID);
        q.setCacheable(true);
        q.setMaxResults(1);
        final List list = q.list();
        if (list.isEmpty()) {
          return null;
        } else {
          return list.get(0);
        }
      }
    });
  }


  /**
   * Finds build-bound change list header by build ID and ID
   */
  public BuildChangeList getBuildChangeList(final int activeBuildID, final int changeListID) {
    return (BuildChangeList) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final List list = session.find("select bchl from BuildChangeList as bchl " +
                "where bchl.buildID = ? and bchl.changeListID = ?",
                new Object[]{new Integer(activeBuildID), new Integer(changeListID)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
        if (list.size() == 1) {
          return list.get(0);
        }
        if (list.size() <= 0) {
          return null;
        }
        throw new UnexpectedErrorException("Result count can not be greater than one.");
      }
    });
  }


  /**
   * Copies change lists from sourceBuildID to destBuildID
   * starting with startChangeListID till the last successful
   * source build run.
   */
  public int copyChangeListsToBuild(final int sourceActiveBuildID, final int destinationActiveBuildID, final int startChangeListID) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, destinationActiveBuildID);
        }
        if (validateActiveID) {
          validateIsActiveBuildID(session, sourceActiveBuildID);
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("destBuildID: " + destinationActiveBuildID);
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("sourceBuildID: " + sourceActiveBuildID);
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("startChangeListID: " + startChangeListID);
        }

        // first, confirm change list exists in the source change list
        int beginChangeListID = startChangeListID;
        if (!isChangeListBelongsToBuild(startChangeListID, sourceActiveBuildID)) {
          beginChangeListID = -1; // will grab all
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("beginChangeListID: " + beginChangeListID);
        }

        // now get last clean change list for source build
        final int endChangeListID = getLatestCleanChangeListID(sourceActiveBuildID);
        if (LOG.isDebugEnabled()) {
          LOG.debug("endChangeListID: " + endChangeListID);
        }
        if (endChangeListID == startChangeListID) {
          return new Integer(startChangeListID); // nothing
        }

        // Prepare query to check existence of build change list
        final Query buildChangeListExistsQuery = session.createQuery("select count(bchl) from  BuildChangeList as bchl " +
                "where bchl.buildID = ? " +
                "  and bchl.changeListID = ? ");
        buildChangeListExistsQuery.setCacheable(true);

        // now get source change lists
        // REVIEWME: simeshev@parabuilci.org -> this can fail if identity generation may generate next it lesser then previous
        final Iterator sourceIter = session.iterate("select distinct chl from BuildRun as br, BuildRunParticipant as brp, ChangeList as chl " +
                " where br.activeBuildID = ? " +
                "   and br.buildRunID = brp.buildRunID " +
                "   and brp.changeListID > ? " +
                "   and brp.changeListID <= ?" +
                "   and brp.changeListID = chl.changeListID",
                new Object[]{new Integer(sourceActiveBuildID), new Integer(beginChangeListID), new Integer(endChangeListID)},
                new Type[]{Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER});

        // copy
        int maxNewID = beginChangeListID;
        while (sourceIter.hasNext()) {
          final ChangeList source = (ChangeList) sourceIter.next();
          // NOTE: simeshev@parabuilci.org - 2009-04-17 - It is possible that the
          // reference build was switched, and the startChangeList is no longer
          // there. As a result, source may contain previously built change lists.
          // We need to validate that source change list does not exist.
          buildChangeListExistsQuery.setInteger(0, destinationActiveBuildID);
          buildChangeListExistsQuery.setInteger(1, source.getChangeListID());
          if ((Integer) buildChangeListExistsQuery.uniqueResult() > 0) {
            continue; // Record exists, go to next
          }

          // create and save new build change list
          final BuildChangeList dest = new BuildChangeList();
          dest.setBuildID(destinationActiveBuildID);
          dest.setChangeListID(source.getChangeListID());
          dest.setChangeListCreatedAt(source.getCreatedAt());
          session.save(dest);
          maxNewID = Math.max(maxNewID, source.getChangeListID());
        }
        return new Integer(maxNewID);
      }
    });
  }


  public void copyBuildRunParticipants(final int sourceBuildRunID, final int destBuildRunID) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List list = session.createQuery("from BuildRunParticipant as brp " +
                "where brp.buildRunID = ?")
                .setInteger(0, sourceBuildRunID)
                .setCacheable(true)
                .list();
        for (final Iterator i = list.iterator(); i.hasNext(); ) {
          final BuildRunParticipant source = (BuildRunParticipant) i.next();
          final BuildRunParticipant dest = new BuildRunParticipant();
          dest.setBuildRunID(destBuildRunID);
          dest.setFirstBuildRunID(source.getFirstBuildRunID());
          dest.setFirstBuildRunNumber(source.getFirstBuildRunNumber());
          dest.setChangeListID(source.getChangeListID());
          session.save(dest);
        }
        return null;
      }
    });
  }


  /**
   * Creates and stores build run participants. Build run
   * participant list consists of:
   * <p/>
   * 1. A set of change lists beginning from changeListID and
   * before that haven't participated in the build yet.
   * <p/>
   * 2. A set of participants from previous build if the prev
   * build broken or failed.
   *
   * @param currBuildRun
   * @param changeListID
   * @param copyPreviousIfBroken
   */
  public void createBuildRunParticipants(final BuildRun currBuildRun, final int changeListID, final boolean copyPreviousIfBroken) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        //if (log.isDebugEnabled()) log.debug("currBuildRunID: " + currBuildRun.getBuildRunID());
        //if (log.isDebugEnabled()) log.debug("changeListID: " + changeListID);

        // detect
        final ChangeList fromChangeList = getChangeList(changeListID);
        final Date cutOffDate = fromChangeList == null ? new Date(0L) : fromChangeList.getCreatedAt();
//        if (log.isDebugEnabled()) log.debug("cutOffDate: " + cutOffDate);

        // get new change lists
        final int activeBuildID = currBuildRun.getActiveBuildID();
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final List newChangeList = session.find("select n from BuildChangeList as n " +
                " where n.buildID = ? " +
                "   and n.new = 'Y' " +
                "   and n.changeListCreatedAt <= ? ",
                new Object[]{new Integer(activeBuildID), cutOffDate},
                new Type[]{Hibernate.INTEGER, Hibernate.TIMESTAMP});

        //
//        if (log.isDebugEnabled()) log.debug("new change list size: " + newChangeList.size());

        // copy previous participants if any

        if (copyPreviousIfBroken) {

          final BuildRun prevBuildRun = getPreviousBuildRun(currBuildRun);

          // get prev unsuccessful build run participants, if any
          List prevParticipants = new ArrayList(11); // empty
          if (prevBuildRun != null) {
            if (prevBuildRun.getResultID() != BuildRun.BUILD_RESULT_SUCCESS || newChangeList.isEmpty()) {
              // was not successful, or current list is blank, get its participants
              final Query prevParticipantsQuery = session.createQuery("from BuildRunParticipant as brp " +
                      "where brp.buildRunID = ?")
                      .setInteger(0, prevBuildRun.getBuildRunID())
                      .setCacheable(true);
              prevParticipants = prevParticipantsQuery.list();
              //if (log.isDebugEnabled()) log.debug("prevParticipants.size(): " + prevParticipants.size());
            }
          }

          if (LOG.isDebugEnabled()) {
            LOG.debug("prev change list size: " + prevParticipants.size());
          }

          // store previous participants
          for (final Iterator iter = prevParticipants.iterator(); iter.hasNext(); ) {
            final BuildRunParticipant prevParticipant = (BuildRunParticipant) iter.next();
            // make it "new"
            session.evict(prevParticipant);
            prevParticipant.setParticipantID(-1);
            prevParticipant.setBuildRunID(currBuildRun.getBuildRunID());
            save(prevParticipant);
            //if (log.isDebugEnabled()) log.debug("prevParticipant: " + prevParticipant);
          }
        }

        // store new
        for (final Iterator iter = newChangeList.iterator(); iter.hasNext(); ) {
          final BuildChangeList changeList = (BuildChangeList) iter.next();
          final BuildRunParticipant newParticipant = new BuildRunParticipant();
          newParticipant.setBuildRunID(currBuildRun.getBuildRunID());
          newParticipant.setChangeListID(changeList.getChangeListID());
          newParticipant.setFirstBuildRunID(currBuildRun.getBuildRunID());
          newParticipant.setFirstBuildRunNumber(currBuildRun.getBuildRunNumber());
          save(newParticipant);
          deleteObject(changeList);
          //if (log.isDebugEnabled()) log.debug("newParticipant.getChangeListID(): " + newParticipant.getChangeListID());
          //if (log.isDebugEnabled()) log.debug("newParticipant.getBuildRunID(): " + newParticipant.getBuildRunID());
        }

        // store number of new change lists
        saveObject(new BuildRunAttribute(currBuildRun.getBuildRunID(), BuildRunAttribute.NEW_CHANGE_LIST_IN_THIS_BUILD, newChangeList.size()));

        return null; // this method does not return results
      }
    });
  }


  /**
   * Returns previous build run or null if there was not a
   * previous build run.
   */
  public BuildRun getPreviousBuildRun(final BuildRun currentBuildRun) {
    return getPreviousBuildRun(currentBuildRun.getActiveBuildID(), currentBuildRun.getBuildRunID());
  }


  /**
   * Returns previous build run or null if there was not a
   * previous build run.
   */
  public BuildRun getPreviousBuildRun(final int activeBuildID, final int buildRunID) {
    return (BuildRun) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from BuildRun as br " +
                " where br.buildRunID = (select max(a.buildRunID) " +
                "                         from BuildRun a" +
                "                         where a.activeBuildID = ? " +
                "                           and a.buildRunID < ?" +
                "                           and a.type = ?)");
        q.setInteger(0, activeBuildID);
        q.setInteger(1, buildRunID);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * Returns previous build run or null if there was not a
   * previous build run.
   */
  public BuildRun getNextBuildRun(final BuildRun currentBuildRun) {
    return (BuildRun) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from BuildRun as br " +
                " where br.buildRunID = (select min(a.buildRunID) " +
                "                         from BuildRun a" +
                "                         where a.activeBuildID = ? " +
                "                           and a.buildRunID > ?" +
                "                           and a.type = ?)");
        q.setInteger(0, currentBuildRun.getActiveBuildID());
        q.setInteger(1, currentBuildRun.getBuildRunID());
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * Returns latest change list ID for the given build ID
   *
   * @param activeBuildID to return latest change list ID
   * @return latest change list ID, or <code>zero</code> if there
   *         are no change lists for the build
   */
  public int getLatestChangeListID(final int activeBuildID) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }

        // Find change list it from build runs
        final Query brpQuery = session.createQuery("select max(brp.changeListID) from BuildRun as br, BuildRunParticipant as brp " +
                " where br.activeBuildID = ? " +
                "   and br.buildRunID = brp.buildRunID");
        brpQuery.setInteger(0, activeBuildID);
        brpQuery.setCacheable(true);
        final Integer brpChangeListID = (Integer) brpQuery.uniqueResult();

        // Find change list from build change lists
        final Query bchlQuery = session.createQuery("select max(bchl.changeListID) from BuildChangeList as bchl " +
                "where bchl.buildID = ?");
        bchlQuery.setInteger(0, activeBuildID);
        bchlQuery.setCacheable(true);
        final Integer bchlChangeListID = (Integer) bchlQuery.uniqueResult();

        if (brpChangeListID != null && bchlChangeListID != null) {
          return new Integer(Math.max(brpChangeListID, bchlChangeListID));
        }

        if (brpChangeListID != null) {
          return brpChangeListID;
        }

        if (bchlChangeListID != null) {
          return bchlChangeListID;
        }

        return new Integer(ChangeList.UNSAVED_ID);
      }
    });
  }


  /**
   * Returns latest change list ID for the given build ID
   *
   * @param activeBuildID to return latest change list ID
   * @return latest change list ID, or <code>zero</code> if there
   *         are no change lists for the build
   */
  public int getNewBuildNumber(final int activeBuildID) {
    return getNewBuildNumber(activeBuildID, true); // increment and store the counter
  }


  /**
   */
  public int getNewBuildNumber(final int activeBuildID, final boolean doStoreIncrement) {
    return incrementActiveBuildAttribute(activeBuildID, ActiveBuildAttribute.BUILD_NUMBER_SEQUENCE, doStoreIncrement, 0);
  }


  /**
   */
  public int getNewVersionCounter(final int activeBuildID, final boolean doStoreIncrement) {
    return incrementActiveBuildAttribute(activeBuildID, ActiveBuildAttribute.VERSION_COUNTER_SEQUENCE, doStoreIncrement, -1);
  }


  /**
   */
  public int incrementActiveBuildAttribute(final int activeBuildID, final String attributeName, final boolean doStoreIncrement, final int initialValueIfNotSet) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        // get next number property
        ActiveBuildAttribute ba = getActiveBuildAttribute(activeBuildID, attributeName);
        // create new if needed
        if (ba == null) {
          // first call
          ba = new ActiveBuildAttribute();
          ba.setBuildID(activeBuildID);
          ba.setPropertyName(attributeName);
          ba.setPropertyValue(Integer.toString(initialValueIfNotSet));
        }

        if (LOG.isDebugEnabled()) {
          LOG.debug("activeBuildAttribute: " + ba);
        }

        // calculate next number
        final int newID = ba.getPropertyValueAsInteger() + 1;
        final Integer integerNewID = new Integer(newID);
        if (!doStoreIncrement) {
          return integerNewID;
        }
        ba.setPropertyValue(newID);
        session.saveOrUpdate(ba);
        return integerNewID;
      }
    });
  }


  /**
   * Returns latest clean change list id.
   *
   * @param activeBuildID to return latest clean change list id.
   * @return latest clean change list id, or <code>-1</code> if
   *         there are no change lists for the build.
   */
  public int getLatestCleanChangeListID(final int activeBuildID) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }

        // find latest clean build run
        final BuildRun lastCleanBuildRun = getLastCleanBuildRun(activeBuildID);
        if (lastCleanBuildRun == null) {
          return new Integer(ChangeList.UNSAVED_ID);
        }
//        if (log.isDebugEnabled()) log.debug("found build run: " + lastCleanBuildRun);

        // return result
        return new Integer(getLatestBuildRunParticipantID(lastCleanBuildRun.getBuildRunID()));
      }
    });
  }


  /**
   * Returns list of ChangeLists participating in the build run
   *
   * @param buildRun
   * @return List of ChangeList objects participating in the build
   *         run
   */
  public List getBuildRunParticipants(final BuildRun buildRun) {
    return getBuildRunParticipants(buildRun.getBuildRunID());
  }


  /**
   * Returns list of ChangeLists participating in the build run
   *
   * @param buildRunID
   * @return List of ChangeList objects participating in the build
   *         run
   */
  public List getBuildRunParticipants(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select chl from ChangeList as chl, BuildRunParticipant as brp " +
                "where brp.buildRunID = ? and brp.changeListID = chl.changeListID")
                .setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns list of ChangeLists participating in the build run
   *
   * @param buildRunID
   * @return List of ChangeList objects participating in the build
   *         run
   */
  public int getLatestBuildRunParticipantID(final int buildRunID) {
    final Integer id = (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select max(brp.changeListID) from BuildRunParticipant as brp " +
                "where brp.buildRunID = ?")
                .setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.uniqueResult();
      }
    });
    return id == null ? ChangeList.UNSAVED_ID : id;
  }


  /**
   * Returns number new of ChangeLists participating in the build
   * run.
   *
   * @param buildRun
   * @return number of ChangeLists participating in the build run.
   */
  public int getNewBuildRunParticipantsCount(final BuildRun buildRun) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final int buildRunID = buildRun.getBuildRunID();
        final Query query = session.createQuery("select count(brp) from BuildRunParticipant as brp " +
                " where brp.buildRunID = ? and brp.firstBuildRunID = ?")
                .setInteger(0, buildRunID)// same
                .setInteger(1, buildRunID)// same
                .setCacheable(true);
        return query.uniqueResult();
      }
    });
  }


  /**
   * Returns list of Issues that have made into the given build
   * run
   *
   * @param buildRunID
   * @return List of {@link Issue} objects.
   */
  public List getBuildRunIssues(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select ish from Issue as ish, ReleaseNote as rn " +
                " where rn.buildRunID = ? and rn.issueID = ish.ID")
                .setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns a map made of a an issueID as as a key and a List of
   * {@link ReleaseNoteChangeList} objects.
   *
   * @param buildRunID
   * @return List of {@link ReleaseNoteChangeList} objects.
   */
  public Map getIssueChangeListMap(final Session session, final int buildRunID) throws HibernateException {
    // exec query
    final Query q = session.createQuery("select " +
            "   icl.issueID, chl.changeListID, chl.number " +
            " from " +
            "   ReleaseNote rn, IssueChangeList as icl, ChangeList chl " +
            " where " +
            "   rn.buildRunID = ? " +
            "   and rn.issueID = icl.issueID " +
            "   and icl.changeListID = chl.changeListID ");
    q.setInteger(0, buildRunID);
    q.setCacheable(true);
    final List list = q.list();
    // fill map
    final Map map = new HashMap(11);
    for (final Iterator i = list.iterator(); i.hasNext(); ) {
      final Object[] objects = (Object[]) i.next();
      final Integer issueID = (Integer) objects[0];
      final Integer changeListID = (Integer) objects[1];
      final String number = (String) objects[2];
      List changeLists = (List) map.get(issueID);
      if (changeLists == null) {
        changeLists = new ArrayList(1);
        map.put(issueID, changeLists);
      }
      changeLists.add(new ReleaseNoteChangeList(changeListID, number));
    }
    return map;
  }


  /**
   * Returns list of {@link ReleaseNoteReport} that have made
   * into the given build run.
   *
   * @param buildRunID
   * @return List of {@link ReleaseNoteReport} objects.
   * @see ReleaseNoteReport
   */
  public List getReleaseNotesReportList(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List result = new ArrayList(11);
        final Map issueChangeListMap = getIssueChangeListMap(session, buildRunID);
        final Query query = session.createQuery("select rn.ID, ish.ID, ish.key, ish.description, ish.url " +
                " from Issue as ish, ReleaseNote as rn " +
                " where rn.buildRunID = ? and rn.issueID = ish.ID")
                .setInteger(0, buildRunID);
        query.setCacheable(true);
        for (final Iterator i = query.list().iterator(); i.hasNext(); ) {
          final Object[] objects = (Object[]) i.next();
          final Integer relnoteID = (Integer) objects[0];
          final Integer issueID = (Integer) objects[1];
          final ReleaseNoteReport report = new ReleaseNoteReport(relnoteID, issueID, (String) objects[2], (String) objects[3], (String) objects[4]);
          final List noteChangeLists = (List) issueChangeListMap.get(issueID);
          if (noteChangeLists != null) {
            report.addChageLists(noteChangeLists);
          }
          result.add(report);
        }
        return result;
      }
    });
  }


  public boolean buildRunIssuesExist(final int buildRunID) {
    return (Boolean) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select count(rn.ID) from ReleaseNote as rn " +
                " where rn.buildRunID = ?")
                .setInteger(0, buildRunID);
        query.setCacheable(true);
        return Boolean.valueOf((Integer) query.uniqueResult() > 0);
      }
    });
  }


  /**
   * Returns list of Issues that have made into the given build
   * run
   *
   * @param buildRun
   * @return List of Issue objects.
   */
  public List getBuildRunIssues(final BuildRun buildRun) {
    return getBuildRunIssues(buildRun.getBuildRunID());
  }


  /**
   * Returns list of ReleaseNotes that have made into the given
   * build run
   *
   * @param buildRunID
   * @return List of {@link ReleaseNote} objects.
   */
  public List getBuildRunReleaseNotes(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("from ReleaseNote as rn " +
                "where rn.buildRunID = ?")
                .setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns number of ReleaseNotes that have made into the given
   * build run.
   *
   * @param buildRunID
   * @return number of ReleaseNotes that have made into the given
   *         build run.
   */
  public int getBuildRunReleaseNotesCount(final int buildRunID) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select count(rn) from ReleaseNote as rn " +
                "where rn.buildRunID = ?")
                .setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.uniqueResult();
      }
    });
  }


  public Integer findIssueIDByKeyAndAttributes(final String key, final List attributes) {

    Integer result = null;

    // first, find *attributes* belonging to the issue with the given key
    final List foundAttrs = (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find("select ia from Issue as ish, IssueAttribute as ia " +
                " where ish.key = ? and ia.issueID = ish.ID " +
                " order by ia.issueID",
                key, Hibernate.STRING);
      }
    });

    // now analyze if issues match
    int currentIssueID = Issue.UNSAVED_ID;
    int matchCount = 0;
    for (final Iterator i = foundAttrs.iterator(); i.hasNext(); ) {
      final IssueAttribute foundAttr = (IssueAttribute) i.next();
      if (currentIssueID != foundAttr.getIssueID()) {
        // first attribute with other issue ID
        currentIssueID = foundAttr.getIssueID();
        matchCount = 0;
      }

      // match - traverse param attrs
      for (final Iterator j = attributes.iterator(); j.hasNext(); ) {
        final IssueAttribute attrToMatch = (IssueAttribute) j.next();
        if (attrToMatch.getName().equals(foundAttr.getName())
                && attrToMatch.getValue().equals(foundAttr.getValue())) {
          matchCount++;
        }
      }

      // check match count
      if (matchCount == attributes.size()) {
        result = new Integer(currentIssueID);
        break;
      }
    }

    return result;
  }


  public List findIssueIDByKey(final int activeBuildID, final String key) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select distinct ish.ID from Issue as ish, ReleaseNote as rn, BuildRun br " +
                " where ish.key = ? " +
                "   and ish.ID = rn.issueID " +
                "   and rn.buildRunID = br.buildRunID " +
                "   and br.activeBuildID = ?" +
                "   and br.type = ?");
        query.setString(0, key);
        query.setInteger(1, activeBuildID);
        query.setByte(2, BuildRun.TYPE_BUILD_RUN);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * find Issue ID By Product And Version.
   * <p/>
   * Note: vimeshev - 06/20/2004 - as of this writing, only BZ
   * relnotes handler uses this.
   *
   * @param trackerType - tracker type
   * @param key         - key
   * @param product     - product
   * @param version     - version
   * @return Integer issue ID or null if not found
   */
  public Integer findIssueIDByProductAndVersion(final byte trackerType, final String key, final String product, final String version) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List list = session.find("select ish.ID from Issue as ish " +
                " where ish.trackerType = ? and ish.key = ? and ish.product = ? and ish.version = ? ",
                new Object[]{new Byte(trackerType), key, product, version},
                new Type[]{Hibernate.BYTE, Hibernate.STRING, Hibernate.STRING, Hibernate.STRING});
        if (list.size() >= 1) {
          return list.get(0); // return first found
        }
        return null;
      }
    });
  }


  /**
   * find Issue ID By Project And Version.
   * <p/>
   * Note: vimeshev - 06/20/2004 - as of this writing, only Jira
   * relnotes handler uses this.
   *
   * @param trackerType - tracker type
   * @param key         - key
   * @param project     - project
   * @param version     - version
   * @return Integer issue ID or null if not found
   */
  public Integer findIssueIDByProjectAndVersion(final byte trackerType, final String key, final String project, final String version) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List list = session.find("select ish.ID from Issue as ish " +
                " where ish.trackerType = ? and ish.key = ? and ish.project = ? and ish.version = ? ",
                new Object[]{new Byte(trackerType), key, project, version},
                new Type[]{Hibernate.BYTE, Hibernate.STRING, Hibernate.STRING, Hibernate.STRING});
        if (list.size() >= 1) {
          return list.get(0); // return first found
        }
        return null;
      }
    });
  }


  /**
   * Returns last complete build run
   *
   * @param activeBuildID to look up
   * @return last complete build run or null if there is no one
   */
  public BuildRun getLastCompleteBuildRun(final int activeBuildID) {
    return (BuildRun) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        //if (validateActiveID) validateIsActiveBuildID(session, activeBuildID);
        final Query q = session.createQuery("select br from BuildRun as br " +
                "   where br.buildRunID = (select max(bra.buildRunID) " +
                "                           from BuildRun as bra" +
                "                           where bra.activeBuildID = ? " +
                "                             and bra.complete = ?" +
                "                             and bra.type = ?" +
                "                             and bra.reRun = no)");
        q.setInteger(0, activeBuildID);
        q.setInteger(1, BuildRun.RUN_COMPLETE);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * Returns last complete and clean build run
   *
   * @param activeBuildID to look up
   * @return last complete and clean build run or null if there
   *         is no one
   */
  public BuildRun getLastCleanBuildRun(final int activeBuildID) {
    return (BuildRun) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("select br from BuildRun as br " +
                "   where br.buildRunID = (select max(bra.buildRunID) " +
                "                           from BuildRun as bra " +
                "                           where bra.activeBuildID = ? " +
                "                             and bra.complete = ? " +
                "                             and bra.type = ? " +
                "                             and bra.resultID = ? " +
                "                             and bra.reRun = no) ");
        q.setInteger(0, activeBuildID);
        q.setByte(1, BuildRun.RUN_COMPLETE);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setByte(3, BuildRun.BUILD_RESULT_SUCCESS);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  public List getIssueChangeLists(final int issueID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select chl from IssueChangeList as icl, ChangeList chl " +
                " where icl.issueID = ? " +
                "   and icl.changeListID = chl.changeListID ");
        q.setInteger(0, issueID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  // REVIEWME: simeshev@parabuilci.org -> not clear meaning of buildID here


  public boolean referringIssueTrackersExist(final int buildID, final byte trackerType) {
    return (Boolean) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select count(isht) from IssueTracker as isht, SourceControlSetting as scs " +
                "where scs.propertyName = ? " +
                " and scs.propertyValue = ? " +
                " and scs.buildID = isht.buildID " +
                " and isht.type = ?");
        q.setString(0, VCSAttribute.REFERENCE_BUILD_ID);
        q.setString(1, Integer.toString(buildID));
        q.setByte(2, trackerType);
        q.setCacheable(true);
        return Boolean.valueOf((Integer) q.uniqueResult() > 0);
      }
    });
  }


  public boolean issueTrackersExist(final int buildID, final byte trackerType) {
    final List list = (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find("select isht from IssueTracker as isht " +
                " where isht.buildID = ? " +
                " and isht.type = ?",
                new Object[]{new Integer(buildID), new Byte(trackerType)},
                new Type[]{Hibernate.INTEGER, Hibernate.BYTE});
      }
    });
    return !list.isEmpty();
  }


  public List getChangeListIssues(final int changeListID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find("select ish from  Issue ish, IssueChangeList as icl " +
                "where  icl.changeListID = ? and icl.issueID = ish.ID ",
                new Integer(changeListID),
                Hibernate.INTEGER);
      }
    });
  }


  public boolean issueChangeListExists(final int changeListID, final int issueID) {
    return (Boolean) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select count(icl) from  IssueChangeList as icl " +
                "where icl.changeListID = ? " +
                "  and icl.issueID = ? ");
        query.setInteger(0, changeListID);
        query.setInteger(1, issueID);
        query.setCacheable(true);
        final Integer i = (Integer) query.uniqueResult();
        return Boolean.valueOf(i > 0);
      }
    });
  }


  /**
   * Returns relative work directory name
   */
  public static String getSystemWorkDirectoryName() {
    if (ConfigurationConstants.CATALINA_BASE != null) {
      return ConfigurationConstants.CATALINA_BASE + ConfigurationConstants.FS + PARABUILD_WORK_DIR;
    }
    return PARABUILD_WORK_DIR;
  }


  /**
   * Get name of the autobuild system directory containing new
   * errors
   *
   * @return String with directory name
   */
  public static File getSystemNewErrorsDirectory() {
    final File dir = new File(ConfigurationConstants.CATALINA_BASE + ConfigurationConstants.FS + "msgs" + ConfigurationConstants.FS + "active");
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return dir;
  }


  /**
   * Get name of the autobuild system directory containing
   * cleared errors
   *
   * @return String with directory name
   */
  public static File getSystemClearedErrorsDirectory() {
    final File result = new File(ConfigurationConstants.CATALINA_BASE + ConfigurationConstants.FS + "msgs" + ConfigurationConstants.FS + "cleared");
    if (!result.exists()) {
      result.mkdirs();
    }
    return result;
  }


  /**
   * Composes a URL to build log used to send build results
   * notifications.
   *
   * @param stepLog
   */
  public String makeBuildLogURL(final StepLog stepLog) {
    final String hostName = SystemConfigurationManagerFactory.getManager().getBuildManagerProtocolHostAndPort();
    final String url;
    if (stepLog.getPathType() == StepLog.PATH_TYPE_HTML_FILE
            || stepLog.getPathType() == StepLog.PATH_TYPE_HTML_DIR) {
      final String pathInfo = makeHTMLLogURLPathInfo(stepLog);
      url = hostName + pathInfo;
    } else {
      url = hostName + "/parabuild/build/log.htm?logid=" + stepLog.getID();
    }
    return url;
  }


  public String makeHTMLLogURLPathInfo(final StepLog stepLog) {
    return HTML_LOG_URL_PREFIX + getBuildRunConfig(stepLog).getActiveBuildID()
            + '/' + stepLog.getID() + '/' + stepLog.getPath();
  }


  /**
   * @param buildID
   * @return Prefix used to append to any outgoing notification
   *         e-mail. If prefix is not defined, return non-null,
   *         empty String.
   */
  public String getNotificationPrefix(final int buildID) {
    final StringBuilder result = new StringBuilder(20);

    // check JVM first
    final String jvmPrefix = System.getProperty(SystemProperty.NOTIFICATION_PREFIX);
    if (!StringUtils.isBlank(jvmPrefix)) {
      result.append(jvmPrefix);
    }

    if (buildID == BuildConfig.UNSAVED_ID) {
      // system msg prefix
      final String prefix = SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.NOTIFICATION_PREFIX, null);
      if (!StringUtils.isBlank(prefix)) {
        result.append(prefix);
      }
    } else {
      // build specific
      final BuildConfigAttribute ba = getBuildAttribute(buildID, BuildConfigAttribute.MESSAGE_PREFIX);
      if (ba != null && !StringUtils.isBlank(ba.getPropertyValue())) {
        result.append(ba.getPropertyValue());
      }
    }

    // add divider space
    if (result.length() != 0) {
      result.append(' ');
    }

    // return result
    return result.toString();
  }


  /**
   * Returns session factory
   *
   * @return session factory
   */
  public SessionFactory getSessionFactory() {
    if (sessionFactory == null) {
      sessionFactory = ServiceManager.getInstance().getConfigurationService().getSessionFactory();
    }
    return sessionFactory;
  }


  public Session openSession() throws HibernateException {
    return getSessionFactory().openSession();
  }


  /**
   * This methods sets provides hibernate transaction wrap to a
   * callback method supplied by transaction callback
   * implementation.
   *
   * @param callback
   */
  public static Object runInHibernate(final TransactionCallback callback) {

    try {
      final HibernateTransaction tx = HibernateTransaction.beginTransaction();
      final Session session = tx.getSession();
      callback.setSession(session);
      final Object result = callback.runInTransaction();
      HibernateTransaction.commitTransaction();
      return result;
    } catch (final RuntimeException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("exception while running in hibernate: " + e, e);
      }
      rollbackHard();
      throw e;
    } catch (final Exception e) {
      rollbackHard();
      throw new UnexpectedErrorException(e);
    }
  }


  /**
   *
   */
  private static void rollbackHard() {
    // REVIEWME: - thing over error processing
    try {
      HibernateTransaction.rollbackTransaction();
    } catch (final HibernateException e1) {
      throw new UnexpectedErrorException(e1);
    }
  }


  /**
   * Returns ConfigurationManager singleton instance
   */
  public static ConfigurationManager getInstance() {
    return configManager;
  }


  /**
   * This helper method creates a name to setting map out of the
   * list of settings.
   *
   * @param list of SourceControlSetting objects
   * @return Map containing setting names as keys and
   *         SourceControlSetting objects as values
   */
  public static Map settingsListToMap(final List list) {
    final Map result = new HashMap(list.size());
    for (final Iterator iter = list.iterator(); iter.hasNext(); ) {
      final SourceControlSetting setting = (SourceControlSetting) iter.next();
      result.put(setting.getPropertyName(), setting);
    }
    return result;
  }


  public void saveIssuesAndAddToPendingList(final int activeBuildID, final List newIssues) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        for (final Iterator iter = newIssues.iterator(); iter.hasNext(); ) {
          final Issue issue = (Issue) iter.next();
          if (issue.getID() == Issue.UNSAVED_ID) {
            session.saveOrUpdate(issue);
          }
          savePendingIssue(new PendingIssue(activeBuildID, issue.getID()));
        }
        return null;
      }
    });
  }


  /**
   * Returns list of active build configs directly referencing
   * this build.
   *
   * @param buildID
   * @return list of Integer IDs of builds referencing this
   *         build.
   */
  public List getReferencingBuildConfigs(final int buildID) {
    return findBuildConfigsByVersionControlSettingValue(VCSAttribute.REFERENCE_BUILD_ID, Integer.toString(buildID));
  }


  /**
   * Returns list of active build configs directly referencing
   * this build.
   *
   * @param versionControlSettingName
   * @param versionControlSettingValue
   * @return list of Integer IDs of builds referencing this
   *         build.
   */
  public List findBuildConfigsByVersionControlSettingValue(final String versionControlSettingName, final String versionControlSettingValue) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select abc from Project p, ProjectBuild pb, ActiveBuild ab, ActiveBuildConfig as abc, SourceControlSetting as scs " +
                " where p.deleted = no " +
                "   and p.ID = pb.projectID " +
                "   and pb.activeBuildID = ab.ID " +
                "   and ab.id = abc.id " +
                "   and ab.deleted = no" +
                "  and abc.buildID = scs.buildID " +
                "  and scs.propertyName = ? " +
                "  and scs.propertyValue = ? ");
        q.setString(0, versionControlSettingName);
        q.setString(1, versionControlSettingValue);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns list of active build configs directly depending
   * on this build.
   *
   * @param buildID
   * @return list of Integer IDs of builds referencing this
   *         build.
   */
  public List getDependingBuildConfigs(final int buildID) {
    return findBuildConfigsByBuildAttributeValue(BuildConfigAttribute.DEPENDENT_BUILD_ID, Integer.toString(buildID));
  }


  /**
   */
  public List findBuildConfigsByBuildAttributeValue(final String attName, final String attrValue) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select abc from Project p, ProjectBuild pb, ActiveBuild ab, ActiveBuildConfig as abc, BuildConfigAttribute as bca " +
                " where p.deleted = no " +
                "   and p.ID = pb.projectID " +
                "   and pb.activeBuildID = ab.ID " +
                "   and ab.id = abc.id " +
                "   and ab.deleted = no" +
                "  and abc.buildID = bca.buildID " +
                "  and bca.propertyName = ? " +
                "  and bca.propertyValue = ? ");
        q.setString(0, attName);
        q.setString(1, attrValue);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public void saveLogConfigProperties(final int logConfigID, final List updatedProperties) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (final Iterator iter = updatedProperties.iterator(); iter.hasNext(); ) {
          final LogConfigProperty logConfigProperty = (LogConfigProperty) iter.next();
          if (logConfigProperty.getLogConfigID() == LogConfig.UNSAVED_ID) {
            logConfigProperty.setLogConfigID(logConfigID);
          }
          session.saveOrUpdate(logConfigProperty);
        }
        return null;
      }
    });
  }


  public void saveResultConfigProperties(final int resultConfigID, final List updatedProperties) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (final Iterator iter = updatedProperties.iterator(); iter.hasNext(); ) {
          final ResultConfigProperty resultConfigProperty = (ResultConfigProperty) iter.next();
          if (resultConfigProperty.getResultConfigID() == ResultConfig.UNSAVED_ID) {
            resultConfigProperty.setResultConfigID(resultConfigID);
          }
          session.saveOrUpdate(resultConfigProperty);
        }
        return null;
      }
    });
  }


  /**
   * Save sequence's statistical value. The value is saved as in
   * sequence attribute table. First a check make if there is
   * already stat saved. If yes, new value is added to the
   * existing value and then saved.
   *
   * @param statName
   * @param statValue
   */
  public void addStepStatistics(final int stepRunID, final String statName, final int statValue) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select sra from StepRunAttribute as sra where sra.stepRunID = ? and sra.name = ?");
        query.setInteger(0, stepRunID);
        query.setString(1, statName);
        StepRunAttribute attr = (StepRunAttribute) query.uniqueResult();
        if (attr != null) {
          attr.setValue(attr.getValueAsInt() + statValue);
        } else {
          attr = new StepRunAttribute(stepRunID, statName, statValue);
        }
        session.saveOrUpdate(attr);
        return null;
      }
    });
  }


  /**
   * Save build run's statistical value. The value is saved as in
   * sequence attribute table. First a check make if there is
   * already stat saved. If yes, new value is added to the
   * existing value and then saved.
   *
   * @param statName
   * @param statValue
   */
  public void addRunStatistics(final int buildRunID, final String statName, final int statValue) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select bra from BuildRunAttribute as bra where bra.buildRunID = ? and bra.name = ?");
        query.setInteger(0, buildRunID);
        query.setString(1, statName);
        BuildRunAttribute attr = (BuildRunAttribute) query.uniqueResult();
        if (attr != null) {
          attr.setValue(attr.getValueAsInteger() + statValue);
        } else {
          attr = new BuildRunAttribute(buildRunID, statName, statValue);
        }
        session.saveOrUpdate(attr);
        return null;
      }
    });
  }


  /**
   * Returns all sequence statistics
   */
  public List getStepRunAttributes(final int stepRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.createQuery("select sra from StepRunAttribute as sra " +
                "  where sra.stepRunID = ?")
                .setCacheable(true)
                .setInteger(0, stepRunID)
                .list();
      }
    });
  }


  /**
   * Returns all sequence statistics
   */
  public Map getStepRunAttributesAsMap(final int stepRunID) {
    return (Map) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() {
        final Map result = new HashMap(11);
        final List list = getStepRunAttributes(stepRunID);
        for (final Iterator i = list.iterator(); i.hasNext(); ) {
          final StepRunAttribute attr = (StepRunAttribute) i.next();
          result.put(attr.getName(), attr);
        }
        return result;
      }
    });
  }


  /**
   * Returns a list of sequence runs that belongs to the same
   * build and precedes the given seq run.
   */
  public List getPreviousStepRuns(final StepRun stepRun) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.find("from StepRun as sr " +
                "where sr.buildRunID = ? and sr.startedAt < ? order by sr.ID",
                new Object[]{new Integer(stepRun.getBuildRunID()), stepRun.getStartedAt()},
                new Type[]{Hibernate.INTEGER, Hibernate.TIMESTAMP});
      }
    });
  }


  public List getAllStepResults(final StepRun stepRun) {
    return getAllStepResults(stepRun.getID());
  }


  public List getAllStepResults(final int stepRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("from StepResult sr " +
                "where sr.stepRunID = ? and sr.found = 'Y' " +
                "order by sr.ID");
        query.setInteger(0, stepRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  public List getBuildRunResults(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select sres from StepResult sres, StepRun srun" +
                " where srun.buildRunID = ? and sres.stepRunID = srun.ID " +
                "    and sres.found = 'Y' " +
                "order by sres.ID");
        query.setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  public List getBuildRunTests(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select brt from BuildRunTest brt " +
                " where tcn.buildRunID = ?  ");
        query.setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns a map of with String user e-mail as a key and a User
   * object as value.
   */
  public Map getIMUsersEmailMap(final int imType, final String messageSelection) {
    final List imUsers = getIMUsers(imType, messageSelection);
    final Map imUsersMap = new HashMap(imUsers.size());
    for (final Iterator i = imUsers.iterator(); i.hasNext(); ) {
      final User user = (User) i.next();
      if (StringUtils.isBlank(user.getImAddress())) {
        continue;
      }
      imUsersMap.put(user.getEmail().toLowerCase(), user.getImAddress());
    }
    return imUsersMap;
  }


  /**
   * Returns list of users having given IM type and IM message
   * selection. It's possible that those users still have an IM
   * address unset.
   *
   * @param imType
   * @param messageSelection
   */
  public List getIMUsers(final int imType, final String messageSelection) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select u from User u, UserProperty up" +
                "   where u.imType = ?" +
                "       and up.userID = u.userID " +
                "       and up.name = ? " +
                "       and up.value = ?");
        q.setInteger(0, imType);
        q.setString(1, messageSelection);
        q.setString(2, UserProperty.OPTION_CHECKED);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns step result for the given id or null if not found.
   */
  public StepResult getStepResult(final int id) {
    return (StepResult) getObject(StepResult.class, id);
  }


  /**
   * Retrieves change number from given change list ID stored in
   * DB
   */
  public String getChangeListNumberFromID(final int changeListID) {
    // REVIEWME: simeshev@parabuilci.org -> getting whole change list just for the number
    // doesn't make sense.
    final ChangeList chl = getChangeList(changeListID);
    final String changeListNumber = chl.getNumber();
    if (StringUtils.isBlank(changeListNumber)) {
      throw new IllegalStateException("Change list number is blank");
    }
    return changeListNumber;
  }


  public ActiveBuild getActiveBuild(final int id) {
    return (ActiveBuild) getObject(ActiveBuild.class, id);
  }


  public int getSequenceNumber(final int activeBuild) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select ab.sequenceNumber from ActiveBuild ab " +
                " where ab.ID = ? ");
        query.setInteger(0, activeBuild);
        query.setCacheable(true);
        return query.uniqueResult();
      }
    });
  }


  public int getActiveBuildStartupStatus(final int id) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select ab.startupStatus from ActiveBuild ab " +
                " where ab.ID = ? ");
        query.setInteger(0, id);
        query.setCacheable(true);
        return query.uniqueResult();
      }
    });
  }


  public void saveNew(final ActiveBuild activeBuild) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.save(activeBuild);
        return activeBuild;
      }
    });
  }


  public ActiveBuild update(final ActiveBuild activeBuild) {
    return (ActiveBuild) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.update(activeBuild);
        return activeBuild;
      }
    });
  }


  public BuildRunConfig getBuildRunConfig(final BuildRun buildRun) {
    return getBuildRunConfig(buildRun.getBuildID());
  }


  public BuildRunConfig getBuildRunConfig(final StepRun stepRun) {
    // REVIEWME: may be not very efficient - single SQL?
    return getBuildRunConfig(getBuildRun(stepRun.getBuildRunID()));
  }


  public BuildRunConfig getBuildRunConfig(final int buildRunConfigID) {
    return (BuildRunConfig) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select brc from BuildRunConfig as brc " +
                "where brc.buildID = ?");
        q.setCacheable(true);
        q.setInteger(0, buildRunConfigID);
        return q.uniqueResult();
      }
    });
  }


  public ActiveBuildConfig getActiveBuildConfig(final int activeBuildID) {
    return (ActiveBuildConfig) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select abc from ActiveBuildConfig as abc " +
                "where abc.buildID = ?");
        q.setCacheable(true);
        q.setInteger(0, activeBuildID);
        return q.uniqueResult();
      }
    });
  }


  /**
   * This method is used by callers who always work with active
   * build IDs like ArchiveManager. Returns build configuration
   * ID that is guaranteed to be active.
   *
   * @param buildID any buildID, can be build run config or
   *                active ID.
   * @return build configuration ID that is guaranteed to be
   *         active.
   */
  public int getActiveIDFromBuildID(final int buildID) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select bc.activeBuildID from BuildConfig bc " +
                " where bc.buildID = ?");
        q.setInteger(0, buildID);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  public int getBuildRunActiveConfigID(final int buildRunID) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select br.activeBuildID from BuildRun br " +
                " where br.buildRunID = ?");
        q.setInteger(0, buildRunID);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  private static void validateIsActiveBuildID(final Session session, final int id) throws HibernateException {
    final Object o = session.get(ActiveBuildConfig.class, new Integer(id));
    if (!(o instanceof ActiveBuildConfig)) {
      throw new IllegalArgumentException("Build ID '" + id + "' is not an active build");
    }
  }


  public void validateIsActiveBuildID(final int buildID) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        validateIsActiveBuildID(session, buildID);
        return null;
      }
    });
  }


  public void validateIsBuildRunBuildID(final int buildID) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        validateIsBuildRunBuildID(session, buildID);
        return null;
      }
    });
  }


  private static void validateIsBuildRunBuildID(final Session session, final int id) throws HibernateException {
    final Query q = session.createQuery("select brc from BuildRunConfig brc where brc.buildID = ?");
    q.setInteger(0, id);
    if (q.uniqueResult() == null) {
      throw new IllegalArgumentException("Build ID '" + id + "' is not an build run config.");
    }
  }


  /**
   * @param activeBuildID
   * @param buildRunNumber
   * @return list of {@link BuildRun} object sorted in oder build runs happened.
   */
  public List getBuildRunListByNumber(final int activeBuildID, final int buildRunNumber) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(" select br from BuildRun br " +
                " where br.activeBuildID = ? " +
                "   and br.buildRunNumber = ? " +
                "   and br.type = ? " +
                " order by br.buildRunID");
        q.setInteger(0, activeBuildID);
        q.setInteger(1, buildRunNumber);
        q.setByte(2, BuildRun.TYPE_BUILD_RUN);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns list of change lists for the given active build ID.
   *
   * @param activeBuildID
   */
  public List getPendingChangeLists(final int activeBuildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery("select chl from BuildChangeList as bchl, ChangeList as chl " +
                " where bchl.buildID = ?  " +
                "   and bchl.new='Y' " +
                "   and bchl.changeListID = chl.changeListID" +
                " order by chl.changeListID");
        q.setInteger(0, activeBuildID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns list of change lists for the given active build ID.
   *
   * @param activeBuildID
   */
  public Integer getLatestPendingChangeListID(final int activeBuildID) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (validateActiveID) {
          validateIsActiveBuildID(session, activeBuildID);
        }
        final Query q = session.createQuery(
                "select max(bchl.changeListID) from BuildChangeList as bchl " +
                        " where bchl.buildID = ?  " +
                        "   and bchl.new='Y' ");
        q.setInteger(0, activeBuildID);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * Returns build config corresponding the left of linked list
   * of build config referred starting from the given
   * originalBuildConfig.
   *
   * @param originalBuildConfig - referring build config
   * @return found leaf, or "effective" config.
   * @throws IllegalArgumentException if the given
   *                                  originalBuildConfig is not a referring build config.
   * @throws IllegalStateException    if Circular reference detected
   *                                  for build configuration.
   */
  public BuildConfig getEffectiveBuildConfig(final BuildConfig originalBuildConfig) {
    // validate
    if (originalBuildConfig.getSourceControl() != VersionControlSystem.SCM_REFERENCE) {
      throw new IllegalArgumentException("Unexpected version control code: " + originalBuildConfig.getSourceControl());
    }
    // process
    return (BuildConfig) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() {
        // referred SCM ID from reference build config
        final int originalBuildConfigID = originalBuildConfig.getBuildID();
        BuildConfig effectiveBuildConfig = originalBuildConfig;
        while (effectiveBuildConfig.getSourceControl() == VersionControlSystem.SCM_REFERENCE) {
          final SourceControlSetting setting = getSourceControlSetting(effectiveBuildConfig.getBuildID(), VCSAttribute.REFERENCE_BUILD_ID);
          effectiveBuildConfig = getBuildConfiguration(setting.getPropertyValueAsInt());
          if (effectiveBuildConfig == null) {
            // see build #674 - we provide more verbose information - normally it should not happen
            // because the fix for #674 should prevent deleting a build if it is referred.
            throw new IllegalStateException("Referred build configuration with ID not found: " + setting.getPropertyValue());
          }
          if (effectiveBuildConfig.getBuildID() == originalBuildConfigID) {
            throw new IllegalStateException("Circular reference detected for build configuration ID " + originalBuildConfigID);
          }
        }
        return effectiveBuildConfig;
      }
    });
  }


  /**
   * @param referringBuildConfig - referring build config
   * @param referredBuildConfig  - referred build config.
   * @return true if it is a circular reference.
   * @throws IllegalArgumentException if the given
   *                                  originalBuildConfig is not a referring build config.
   * @throws IllegalStateException    if Circular reference detected
   *                                  for build configuration.
   */
  public boolean isCircularReference(final BuildConfig referringBuildConfig, final BuildConfig referredBuildConfig) {
    // validate
    if (referringBuildConfig.getSourceControl() != VersionControlSystem.SCM_REFERENCE) {
      throw new IllegalArgumentException("Unexpected version control code: " + referringBuildConfig.getSourceControl());
    }
    // process
    return (Boolean) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() {
        final int originalBuildConfigID = referringBuildConfig.getBuildID();
        BuildConfig nextBuildConfig = referredBuildConfig;
        while (nextBuildConfig.getSourceControl() == VersionControlSystem.SCM_REFERENCE) {
          final SourceControlSetting setting = getSourceControlSetting(nextBuildConfig.getBuildID(), VCSAttribute.REFERENCE_BUILD_ID);
          nextBuildConfig = getBuildConfiguration(setting.getPropertyValueAsInt());
          if (nextBuildConfig.getBuildID() == originalBuildConfigID) {
            return Boolean.TRUE;
          }
        }
        return Boolean.FALSE;
      }
    });
  }


  /**
   * Returns build attribute value or 0 if not found.
   *
   * @param attrName
   * @return build attribute value or 0 if not found.
   */
  public Integer getActiveBuildAttributeValue(final int buildID, final String attrName) {
    return getActiveBuildAttributeValue(buildID, attrName, new Integer(0));
  }


  /**
   * Returns build attribute value or 0 if not found.
   *
   * @param attrName
   * @return build attribute value or 0 if not found.
   */
  public Integer getActiveBuildAttributeValue(final int buildID, final String attrName, final Integer defaultValue) {
    final ActiveBuildAttribute aba = getActiveBuildAttribute(buildID, attrName);
    if (aba == null) {
      return defaultValue;
    }
    return new Integer(aba.getPropertyValueAsInteger());
  }


  /**
   * Returns build attribute value or default if not found.
   *
   * @param attrName
   * @return build attribute value or default if not found.
   */
  public String getActiveBuildAttributeValue(final int buildID, final String attrName, final String defaultValue) {
    final ActiveBuildAttribute aba = getActiveBuildAttribute(buildID, attrName);
    if (aba == null || StringUtils.isBlank(aba.getPropertyValue())) {
      return defaultValue;
    }
    return aba.getPropertyValue();
  }


  /**
   * Finds build run results by build run ID, pathType and
   * configuration path. The path is trimmed before
   * search.
   *
   * @param buildRunID build run ID
   * @param pathType   pathType
   * @param path       path
   * @return List of StepResult objects.
   */
  public List findBuildRunResults(final int buildRunID, final byte pathType, final String path) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select sres from StepResult sres, StepRun srun" +
                " where srun.buildRunID = ? " +
                "    and sres.stepRunID = srun.ID " +
                "    and sres.found = 'Y' " +
                "    and sres.pathType = ? " +
                "    and sres.path = ? ");
        query.setInteger(0, buildRunID);
        query.setByte(1, pathType);
        query.setString(2, path.trim());
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Finds build run results by build run ID, pathType and
   * configuration path. The path is trimmed before
   * search.
   *
   * @param buildRunID  build run ID
   * @param description description
   * @param pathType    pathType
   * @param path        path
   * @return List of StepResult objects.
   */
  public List findBuildRunResults(final int buildRunID, final String description, final byte pathType, final String path) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select sres from StepResult sres, StepRun srun" +
                " where srun.buildRunID = ? " +
                "    and sres.stepRunID = srun.ID " +
                "    and sres.found = 'Y' " +
                "    and sres.description = ? " +
                "    and sres.pathType = ? " +
                "    and sres.path = ? ");
        query.setInteger(0, buildRunID);
        query.setString(1, description.trim());
        query.setByte(2, pathType);
        query.setString(3, path.trim());
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Finds build run logs by build run ID, pathType and
   * configuration result path. The path is trimmed before
   * search.
   *
   * @param buildRunID build run ID
   * @param pathType   pathType
   * @param path       resultPath
   * @return List of StepResult objects.
   */
  public List findBuildRunLogs(final int buildRunID, final byte pathType, final String path) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select slog from StepLog slog, StepRun srun" +
                " where srun.buildRunID = ? " +
                "    and slog.stepRunID = srun.ID " +
                "    and slog.found = ? " +
                "    and slog.pathType = ? " +
                "    and slog.path = ? ");
        query.setInteger(0, buildRunID);
        query.setByte(1, (byte) 1);
        query.setByte(2, pathType);
        query.setString(3, path.trim());
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Finds build run attributes belonging to a given build run with a given name and value.
   */
  public List findBuildRunAttributes(final int activeBuildID, final String name, final String value) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(" select bra from BuildRun br, BuildRunAttribute as bra,  "
                + "   where br.activeBuildID = ? "
                + "     and br.buildRunID = bra.buildRunID "
                + "     and bra.name = ? "
                + "     and bra.value = ?"
        );
        q.setInteger(0, activeBuildID);
        q.setString(1, name);
        q.setString(2, value);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Finds build run logs by build run ID, pathType and
   * configuration resultPath. The path is trimmed before
   * search.
   *
   * @param buildRunID build run ID
   * @param pathType   pathType
   * @return List of StepResult objects.
   */
  public List findStepLogs(final int buildRunID, final String description, final byte type, final byte pathType) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select slog from StepLog slog, StepRun srun" +
                " where srun.buildRunID = ? " +
                "    and slog.stepRunID = srun.ID " +
                "    and slog.found = ? " +
                "    and slog.pathType = ? " +
                "    and slog.type = ? " +
                "    and slog.description = ? ");
        query.setInteger(0, buildRunID);
        query.setByte(1, (byte) 1);
        query.setByte(2, pathType);
        query.setByte(3, type);
        query.setString(4, description);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns integer schedule property
   */
  public int getScheduleSettingValue(final int activeBuildID, final String propertyName, final int defaultValue) {
    try {
      final String stringValue = getScheduleSettingValue(activeBuildID, propertyName, null);
      if (StringUtils.isBlank(stringValue)) {
        return defaultValue;
      }
      return Integer.parseInt(stringValue);
    } catch (final NumberFormatException e) {
      return defaultValue;
    }
  }


  public void markActiveBuildDeleted(final int activeBuildID) {
    final ActiveBuild activeBuild = getActiveBuild(activeBuildID);
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        activeBuild.setDeleted(true);
        session.update(activeBuild);
        return null;
      }
    });
  }


  public VCSAttribute save(final SourceControlSetting scs) {
    return (VCSAttribute) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        saveSourceControlSetting(session, scs.getBuildID(), scs);
        return scs;
      }
    });
  }


  public void delete(final StartParameter startParameter) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete(startParameter);
        return null;
      }
    });
  }


  public void save(final StartParameter startParameter) {
    saveObject(startParameter);
  }


  public List getStartParameters(final StartParameterType type, final int ownerID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from StartParameter as mrp where mrp.buildID = ? and mrp.type = ? order by mrp.order");
        q.setInteger(0, ownerID);
        q.setByte(1, type.byteValue());
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public StartParameter findStartParameter(final byte variableType, final int variableOwner, final String name) {
    return (StartParameter) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("" +
                " from StartParameter as mrp " +
                " where mrp.buildID = ? and mrp.type = ? and mrp.name = ?");
        q.setInteger(0, variableOwner);
        q.setByte(1, variableType);
        q.setString(2, name);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  public int getRequiredStartParameterCount(final int buildConfigID, final StartParameterType type) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(
                "select count(mrp) from StartParameter as mrp " +
                        " where mrp.buildID = ? " +
                        " and mrp.type = ?" +
                        " and mrp.required = yes " +
                        " and mrp.enabled = yes ");
        q.setInteger(0, buildConfigID);
        q.setByte(1, type.byteValue());
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * Copies list of {@link ReleaseNote} from source build run to destination build run.
   *
   * @param sourceBuildRunID
   * @param destBuildRunID
   */
  public void copyReleaseNotes(final int sourceBuildRunID, final int destBuildRunID) {
    runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List buildRunReleaseNotes = getBuildRunReleaseNotes(sourceBuildRunID);
        for (final Iterator i = buildRunReleaseNotes.iterator(); i.hasNext(); ) {
          final ReleaseNote source = (ReleaseNote) i.next();
          final ReleaseNote dest = new ReleaseNote(destBuildRunID, source.getIssueID());
          session.save(dest);
        }
        return null;
      }
    });
  }


  public BuildRun getBuildRunFromStepLog(final int stepLogID) {
    return (BuildRun) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(" select br from StepRun sr, BuildRun br, StepLog sl " +
                " where sl.ID = ? " +
                " and sl.stepRunID = sr.ID " +
                " and sr.buildRunID = br.buildRunID ");
        q.setInteger(0, stepLogID);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  public List getChangeLists(final int activeBuildID, final int startBuildNumber, final int endBuildNumber) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select distinct chl from BuildRun br, BuildRunParticipant as brp, ChangeList chl " +
                "where br.activeBuildID = ? " +
                " and br.buildRunNumber >= ? " +
                " and br.buildRunNumber <= ? " +
                " and br.type = ? " +
                " and br.reRun = no " +
                " and br.buildRunID = brp.buildRunID " +
                " and brp.changeListID = chl.changeListID " +
                "order by chl.createdAt desc");
        query.setInteger(0, activeBuildID);
        query.setInteger(1, startBuildNumber);
        query.setInteger(2, endBuildNumber);
        query.setByte(3, BuildRun.TYPE_BUILD_RUN);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns a list of changes for a given file name.
   *
   * @param activeBuildID    the active build ID
   * @param startBuildNumber the build number to start from.
   * @param endBuildNumber   the build number to finish at.
   * @param filePath         the file name
   * @return a list of {@link ChangeList} objects.
   */
  public List getChangeLists(final int activeBuildID, final int startBuildNumber, final int endBuildNumber, final String filePath) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select distinct chl from BuildRun br, BuildRunParticipant as brp, ChangeList chl, Change ch " +
                "where br.activeBuildID = ? " +
                " and br.buildRunNumber >= ? " +
                " and br.buildRunNumber <= ? " +
                " and br.type = ? " +
                " and br.reRun = no " +
                " and br.buildRunID = brp.buildRunID " +
                " and chl.changeListID = brp.changeListID" +
                " and ch.changeListID = chl.changeListID" +
                " and ch.filePath = ?" +
                " order by chl.createdAt desc");
        query.setInteger(0, activeBuildID);
        query.setInteger(1, startBuildNumber);
        query.setInteger(2, endBuildNumber);
        query.setByte(3, BuildRun.TYPE_BUILD_RUN);
        query.setString(4, filePath);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * Returns a list of changed file paths.
   *
   * @param activeBuildID
   * @param startBuildNumber
   * @param endBuildNumber
   * @return
   */
  public List getChangedFiles(final int activeBuildID, final int startBuildNumber, final int endBuildNumber) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select distinct ch.filePath from BuildRun br, BuildRunParticipant as brp, ChangeList chl, Change ch " +
                "where br.activeBuildID = ? " +
                " and br.buildRunNumber >= ? " +
                " and br.buildRunNumber <= ? " +
                " and br.type = ? " +
                " and br.reRun = no " +
                " and br.buildRunID = brp.buildRunID " +
                " and brp.changeListID = chl.changeListID " +
                " and chl.changeListID = ch.changeListID " +
                "order by ch.filePath asc");
        query.setInteger(0, activeBuildID);
        query.setInteger(1, startBuildNumber);
        query.setInteger(2, endBuildNumber);
        query.setByte(3, BuildRun.TYPE_BUILD_RUN);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  /**
   * @param buildRunID
   * @return List of {@link BuildRunActionVO}
   */
  public List getBuildRunActionLogVOs(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        // execute query

        final Query query = session.createQuery(" select bra.action, bra.description, bra.date, usr.name " +
                " from BuildRunAction bra, User usr " +
                " where bra.buildRunID = ?  " +
                "   and bra.userID = usr.userID " +
                " order by bra.date desc");
        query.setInteger(0, buildRunID);
        query.setCacheable(true);

        // traverse query result

        final List result = new LinkedList();
        for (final Iterator i = query.iterate(); i.hasNext(); ) {
          final Object[] objects = (Object[]) i.next();
          final BuildRunActionVO vo = new BuildRunActionVO();
          vo.setAction((String) objects[0]);
          vo.setDescription((String) objects[1]);
          vo.setDate((Date) objects[2]);
          vo.setUser((String) objects[3]);
          result.add(vo);
        }

        return result;
      }
    });
  }


  /**
   * @param buildRunID
   * @return List of {@link BuildRunActionVO}
   */
  public List getBuildRunActions(final int buildRunID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery(" select bra " +
                " from BuildRunAction bra " +
                " where bra.buildRunID = ?  " +
                " order by bra.date desc");
        query.setInteger(0, buildRunID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  public boolean getBuildRunAttributeValue(final int buildRunID, final String attributeName, final boolean defaultValue) {
    return Boolean.valueOf(getBuildRunAttributeValue(buildRunID, attributeName));
  }


  /**
   * @param buildRun
   * @return List of {@link ParallelBuildRunVO} objects
   */
  public List getAllParallelBuildRunVOs(final BuildRun buildRun) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        // get leaders and dependents
        final List result = new LinkedList();
        final int leadingBuildRunID;
        final byte dependence = buildRun.getDependence();
        if (dependence == BuildRun.DEPENDENCE_LEADER
                || dependence == BuildRun.DEPENDENCE_STANDALONE) {
          leadingBuildRunID = buildRun.getBuildRunID();
          result.add(new ParallelBuildRunVO(leadingBuildRunID, buildRun.getBuildName(), dependence));
        } else if (dependence == BuildRun.DEPENDENCE_SUBORDINATE) {
          // add leader
          final Query query = session.createQuery(" select br.buildRunID, br.buildName, br.dependence " +
                  " from BuildRunDependence brd, BuildRun br " +
                  " where brd.dependentBuildRunID = ? " +
                  "   and brd.leadingBuildRunID = br.buildRunID");
          query.setInteger(0, buildRun.getBuildRunID());
          query.setCacheable(true);
          final Object[] objects = (Object[]) query.uniqueResult();
          leadingBuildRunID = (Integer) objects[0];
          result.add(new ParallelBuildRunVO(leadingBuildRunID, (String) objects[1], ((Byte) objects[2]).byteValue()));
        } else {
          throw new IllegalArgumentException("Unknown dependence type: " + dependence);
        }

        // traverse dependents list
        final List list = getDependentParallelBuildRuns(session, leadingBuildRunID);
        for (final Iterator i = list.iterator(); i.hasNext(); ) {
          final Object[] objects = (Object[]) i.next();
          result.add(new ParallelBuildRunVO((Integer) objects[0], (String) objects[1], (Byte) objects[2]));
        }

        return result;
      }
    });
  }


  /**
   * Helper method.
   *
   * @param session
   * @param buildRunID
   * @return
   * @throws HibernateException
   */
  private static List getDependentParallelBuildRuns(final Session session, final int buildRunID) throws HibernateException {
    final Query query = session.createQuery(" select br.buildRunID, br.buildName, br.dependence " +
            " from BuildRunDependence brd, BuildRun br " +
            " where brd.leadingBuildRunID = ? " +
            "   and brd.dependentBuildRunID = br.buildRunID");
    query.setInteger(0, buildRunID);
    query.setCacheable(true);
    return query.list();
  }


  /**
   * @param buildRun
   * @return List of {@link ParallelBuildRunVO} objects
   */
  public List getAllParallelBuildRuns(final BuildRun buildRun) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        // get leaders and dependents
        final List result = new ArrayList(11);
        result.add(buildRun);
        final int leaderBuildRunID;
        if (buildRun.getDependence() == BuildRun.DEPENDENCE_STANDALONE) {

          return result;

        } else if (buildRun.getDependence() == BuildRun.DEPENDENCE_LEADER) {

          leaderBuildRunID = buildRun.getBuildRunID();

        } else if (buildRun.getDependence() == BuildRun.DEPENDENCE_SUBORDINATE) {

          // find leader
          final Query leaderQuery = session.createQuery(" select br " +
                  " from BuildRunDependence brd, BuildRun br " +
                  " where brd.dependentBuildRunID = ? " +
                  "   and brd.leadingBuildRunID = br.buildRunID");
          leaderQuery.setInteger(0, buildRun.getBuildRunID());
          leaderQuery.setCacheable(true);
          final BuildRun leaderBuildRun = (BuildRun) leaderQuery.uniqueResult();
          if (leaderBuildRun == null) {
            // NOTE: vimeshev - 2007-11-02 - Should never be
            // null, but see #1286 "Unexpected user
            // interface error: NullPointerException". Try
            // to get it from the attribute.
            leaderBuildRunID = getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_LEAD_BUILD_RUN_ID, BuildRun.UNSAVED_ID);
            if (leaderBuildRunID == BuildRun.UNSAVED_ID) {
              // Report an error first
              ErrorManagerFactory.getErrorManager().reportSystemError(new Error(buildRun.getActiveBuildID(), "Could not find a leader build run for " + buildRun, Error.ERROR_LEVEL_ERROR));
              // Return result containing self
              return result;
            }
          } else {
            result.add(leaderBuildRun);
            leaderBuildRunID = leaderBuildRun.getBuildRunID();
          }
        } else {
          throw new IllegalArgumentException("Unknown dependence type: " + buildRun.getDependence());
        }

        final Query query = session.createQuery(" select br " +
                " from BuildRunDependence brd, BuildRun br " +
                " where brd.leadingBuildRunID = ? " +
                "   and brd.dependentBuildRunID = br.buildRunID");
        query.setInteger(0, leaderBuildRunID);
        query.setCacheable(true);
        final List c = query.list();
        for (final Iterator j = c.iterator(); j.hasNext(); ) {
          final BuildRun dependentBuildRun = (BuildRun) j.next();
          if (dependentBuildRun.getBuildRunID() != buildRun.getBuildRunID()) {
            result.add(dependentBuildRun); // current is already added
          }
        }

        // sort
        result.sort(BuildRun.BUILD_NAME_IGNORE_CASE);

        return result;
      }
    });
  }


  public List getDependentParallelBuildIDs(final int activeBuildID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select abc.buildID from Project p, ProjectBuild pb, ActiveBuild ab, ActiveBuildConfig as abc, SourceControlSetting scs" +
                " where p.deleted = no " +
                "   and p.ID = pb.projectID " +
                "   and pb.activeBuildID = ab.ID " +
                "   and ab.id = abc.id " +
                "   and ab.deleted = no" +
                "   and abc.scheduleType = ? " +
                "   and scs.buildID = abc.buildID " +
                "   and scs.propertyName = ? " +
                "   and scs.propertyValue = ? "
        ).setCacheable(true)
                .setByte(0, BuildConfig.SCHEDULE_TYPE_PARALLEL)
                .setString(1, VCSAttribute.REFERENCE_BUILD_ID)
                .setString(2, Integer.toString(activeBuildID));
        return q.list();
      }
    });
  }


  public long getBuilderTimeStamp(final StepRun stepRun) {
    // 1. Try to get step run attribute
    final StepRunAttribute stepRunAttribute = getStepRunAttribute(stepRun.getID(), StepRunAttribute.ATTR_BUILDER_TIMESTAMP);
    if (stepRunAttribute != null) {
      return stepRunAttribute.getValueAsLong();
    }

    // 2. Try step run start time
    if (stepRun.getStartedAt() != null) {
      return stepRun.getStartedAt().getTime();
    }

    // 3. Try to get from build run
    final BuildRun buildRun = getBuildRun(stepRun.getBuildRunID());
    final BuildRunAttribute builderTimeStampAttr = getBuildRunAttribute(stepRun.getBuildRunID(), BuildRunAttribute.ATTR_BUILDER_TIMESTAMP);
    return builderTimeStampAttr == null ? buildRun.getStartedAt().getTime() : builderTimeStampAttr.getValueAsLong();
  }


  public boolean stepFixedPreviousBreakage(final StepRun stepRun) {

    // REVIEWME: vimeshev - 2007-12-28 - consider finding
    // if a previous step was broken using plain Hibernate.
    // Or mark a step run as fixed.

    return (Boolean) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() {
        if (stepRun.isSuccessful()) {
          final BuildRun previousBuildRun = getPreviousBuildRun(getBuildRun(stepRun.getBuildRunID()));
          if (previousBuildRun != null) {
            // go through the list of previous steps and see
            // if there are any other broken
            final List previousStepRuns = getStepRuns(previousBuildRun.getBuildRunID());
            for (final Iterator i = previousStepRuns.iterator(); i.hasNext(); ) {
              final StepRun previousStepRun = (StepRun) i.next();
              if (previousStepRun.getName().equals(stepRun.getName())) {
                return Boolean.valueOf(!previousStepRun.isSuccessful());
              }
            }
          }
        }
        return Boolean.FALSE;
      }
    });
  }


  public ChangeList getChangeList(final int buildRunID, final String changeListNumber) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("buildRunID: " + buildRunID);
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("changeListNumber: " + changeListNumber);
    }
    return (ChangeList) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select distinct chl from ChangeList as chl, BuildRunParticipant as brp " +
                " where brp.buildRunID = ? " +
                "   and brp.changeListID = chl.changeListID " +
                "   and chl.number = ?");
        q.setInteger(0, buildRunID);
        q.setString(1, changeListNumber);
        q.setCacheable(true);
        // NOTE: vimeshev - 2007-05-05 - we use this instead
        // of q.uniqueResult to cover a strange case when
        // the statement above fails with
        // net.sf.hibernate.NonUniqueResultException.
        // Reported by Patrick Bennett.
        final List list = q.list();
        if (list.isEmpty()) {
          return null;
        }
        // report the problem
        if (list.size() > 1) {
          // form the duplicate list
          final StringBuilder warningOutput = new StringBuilder(500);
          for (int i = 0; i < list.size(); i++) {
            final ChangeList chList = (ChangeList) list.get(i);
            warningOutput.append(chList.toString());
            warningOutput.append(' ');
          }
          LOG.warn("Unexpected duplication of change lists has been encountered. Will use first change list: " + warningOutput);
        }
        return list.get(0);
      }
    });
  }


  public List findBuildConfigsByVCS(final int projectID, final byte scmType) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select abc from Project p, ProjectBuild pb, ActiveBuild ab, ActiveBuildConfig as abc" +
                " order by abc.buildName " +
                " where p.deleted = no " +
                "   and p.ID = ? " +
                "   and p.ID = pb.projectID " +
                "   and pb.activeBuildID = ab.ID " +
                "   and ab.id = abc.id " +
                "   and ab.deleted = no" +
                "   and abc.sourceControl = ?");
        q.setInteger(0, projectID);
        q.setByte(1, scmType);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public List findBuildConfigsByVCS(final byte scmType) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select abc from Project p, ProjectBuild pb, ActiveBuild ab, ActiveBuildConfig as abc" +
                " order by abc.buildName " +
                " where p.deleted = no " +
                "   and p.ID = pb.projectID " +
                "   and pb.activeBuildID = ab.ID " +
                "   and ab.id = abc.id " +
                "   and ab.deleted = no" +
                "   and abc.sourceControl = ?");
        q.setByte(0, scmType);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public BuildRun getBuildRunFromStepRun(final int stepRunID) {
    return (BuildRun) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select br from BuildRun br, StepRun sr where sr.ID = ? and sr.buildRunID = br.buildRunID"
        );
        q.setInteger(0, stepRunID);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  public int saveChangeList(final ChangeList changeList) {
    return (Integer) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return new Integer(saveChangeList(changeList, session));
      }
    });
  }


  public ChangeList getBuildRunChangeListFromBuildRunParicipants(final int buildRunID) {
    final List buildRunParticipants = (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select chl from BuildRunParticipant as brp, ChangeList chl " +
                "where brp.buildRunID = ? and brp.changeListID = chl.changeListID " +
                "order by chl.createdAt desc");
        query.setInteger(0, buildRunID);
        query.setCacheable(true);
        query.setMaxResults(1);
        return query.list();
      }
    });
    return (ChangeList) buildRunParticipants.get(0);
  }


  public BuildRun getBuildRun(final Integer validationBuildRunID) {
    return getBuildRun(validationBuildRunID.intValue());
  }


  public TestSuiteName findTestSuiteName(final String testSuiteName) {
    return (TestSuiteName) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select tsn from TestSuiteName tsn " +
                " where tsn.name = ? ");
        query.setString(0, testSuiteName);
        query.setCacheable(true);
        return query.uniqueResult();
      }
    });
  }


  public List getTestSuiteNames() {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select tsn from TestSuiteName tsn " +
                " order by tsn.name ");
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  public TestCaseName findTestCaseName(final int testSuiteNameID, final String testCaseName) {
    return (TestCaseName) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select tcn from TestCaseName tcn " +
                " where tcn.testSuiteNameID = ?  " +
                "   and tcn.name = ? ");
        query.setInteger(0, testSuiteNameID);
        query.setString(1, testCaseName);
        query.setCacheable(true);
        return query.uniqueResult();
      }
    });
  }


  public List getTestCaseNames(final int testSuiteNameID) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select tcn from TestCaseName tcn " +
                " where tcn.testSuiteNameID = ?  ");
        query.setInteger(0, testSuiteNameID);
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  public int findOrCreateTestSuiteName(final String testSuiteName) {
    TestSuiteName testSuiteNameObject = findTestSuiteName(testSuiteName);
    if (testSuiteNameObject == null) {
      testSuiteNameObject = new TestSuiteName();
      testSuiteNameObject.setName(testSuiteName);
      saveObject(testSuiteNameObject);
    }
    return testSuiteNameObject.getID();
  }


  public int findOrCreateTestCaseName(final int testSuiteNameID, final String testCaseName) {
    TestCaseName testCaseNameObject = findTestCaseName(testSuiteNameID, testCaseName);
    if (testCaseNameObject == null) {
      testCaseNameObject = new TestCaseName();
      testCaseNameObject.setName(testCaseName);
      testCaseNameObject.setTestSuiteNameID(testSuiteNameID);
      saveObject(testCaseNameObject);
    }
    return testCaseNameObject.getID();
  }


  public String toString() {
    return "ConfigurationManager{" +
            "sessionFactory=" + sessionFactory +
            '}';
  }


  public BuildSequence getBuildSequence(final StepLog stepLog, final String name) {
    return (BuildSequence) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select bs " +
                "from BuildSequence as bs, BuildRun br, StepRun sr  " +
                "where sr.ID = ? " +
                " and br.buildRunID = sr.buildRunID " +
                " and bs.buildID = br.buildID " +
                " and bs.stepName = ?");
        query.setInteger(0, stepLog.getStepRunID());
        query.setString(1, name);
        query.setCacheable(true);
        return query.uniqueResult();
      }
    });
  }


  public StartParameter getStartParameter(final int parameterID) {
    return (StartParameter) getObject(StartParameter.class, parameterID);
  }


  public Issue getIssue(final int issueID) {
    return (Issue) getObject(Issue.class, issueID);
  }


  public boolean isCleanCheckoutIfBroken(final int activeBuildID) {
    return (Boolean) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() {
        final BuildConfig config = getBuildConfiguration(activeBuildID);
        final int buildID;
        if (config.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
          buildID = getSourceControlSettingValue(activeBuildID, VCSAttribute.REFERENCE_BUILD_ID,
                  BuildConfig.UNSAVED_ID);
        } else {
          buildID = activeBuildID;
        }
        return Boolean.valueOf(getScheduleSettingValue(buildID, ScheduleProperty.AUTO_CLEAN_CHECKOUT_IF_BROKEN,
                ScheduleProperty.OPTION_UNCHECKED).equals(ScheduleProperty.OPTION_CHECKED));
      }
    });
  }


  public List findLastCleanBuildRuns(final int displayGroupID, final int changeListNumber) {
    return (List) runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select bra from BuildRun bra " +
                "where bra.buildRunID = (select max(brb.buildRunID) from BuildRun brb, DisplayGroupBuild dgb " +
                "                               where brb.activeBuildID = dgb.buildID " +
                "                                  and brb.changeListNumber = ? " +
                "                                  and dgb.displayGroupID = ? " +
                "                               group by brb.activeBuildID) ");
        query.setInteger(0, displayGroupID);
        query.setString(1, Integer.toString(changeListNumber));
        query.setCacheable(true);
        query.setMaxResults(1);
        return query.list();
      }
    });
  }
} // NOPMD ExcessiveImports
