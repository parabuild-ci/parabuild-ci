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
package org.parabuild.ci.statistics;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.type.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.PersistentTestStats;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This class is responsible for delivering build statistics
 * information.
 */
final class StatisticsManagerImpl implements StatisticsManager {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(StatisticsManagerImpl.class); // NOPMD

  private int activeBuildID = BuildConfig.UNSAVED_ID;
  private final ConfigurationManager cm = ConfigurationManager.getInstance();
  private static final int MAX_LAST_BUILDS = 30;


  /**
   * Constuctor.
   */
  public StatisticsManagerImpl(final int activeBuildID) {
    ArgumentValidator.validateBuildIDInitialized(activeBuildID);
    if (ConfigurationManager.validateActiveID) cm.validateIsActiveBuildID(activeBuildID);
    this.activeBuildID = activeBuildID;
  }


  /**
   * Returns current up-to-date statistics.
   */
  public BuildStatistics getUpToDateBuildStatistics() {
    return (BuildStatistics) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() {
        final Integer successfulCount = cm.getActiveBuildAttributeValue(activeBuildID, ActiveBuildAttribute.STAT_SUCC_BUILDS_TO_DATE);
        final Integer brokenCount = cm.getActiveBuildAttributeValue(activeBuildID, ActiveBuildAttribute.STAT_FAILED_BUILDS_TO_DATE);
        final Integer changeListCount = cm.getActiveBuildAttributeValue(activeBuildID, ActiveBuildAttribute.STAT_CHANGE_LISTS_TO_DATE);
        final Integer issueCount = cm.getActiveBuildAttributeValue(activeBuildID, ActiveBuildAttribute.STAT_ISSUES_TO_DATE);
        return new BuildStatistics(successfulCount, brokenCount, changeListCount, issueCount);
      }
    });
  }


  /**
   * Returns current month-to-date distribution by day.
   *
   * @return SortedMap where key is a month Date and value is
   *         BuildStatistics
   */
  public SortedMap getMonthToDateBuildStatistics() {
    return new DailyPersistentBuildStatsRetriever(activeBuildID).getStatistics();
  }


  /**
   * Returns current month-to-date test distribution by day.
   *
   * @return SortedMap where key is a month Date and value is
   *         {@link TestStatistics}
   */
  public SortedMap getMonthToDateTestStatistics(final byte testToolCode) {
    return new DailyPersistentTestStatsRetriever(activeBuildID, testToolCode).getStatistics();
  }


  /**
   * Returns current year-to-date distribution by month.
   *
   * @return SortedMap where key is a month Date and value is
   *         BuildStatistics
   */
  public SortedMap getYearToDateBuildStatistics() {
    return new MonthlyPersistentBuildStatsRetriever(activeBuildID).getStatistics();
  }


  /**
   * Returns current Year-to-date test distribution by day.
   *
   * @return SortedMap where key is a Year Date and value is
   *         {@link TestStatistics}
   */
  public SortedMap getYearToDateTestStatistics(final byte testToolCode) {
    return new MonthlyPersistentTestStatsRetriever(activeBuildID, testToolCode).getStatistics();
  }


  /**
   * Returns current statists for last {@link #MAX_LAST_BUILDS} builds.
   *
   * @return SortedMap where key is a an integer build number and value is
   *         {@link TestStatistics}
   */
  public SortedMap getRecentTestStatistics(final byte testToolCode) {
    // REVIEW: subject for caching
    // get build run ID
    return (SortedMap) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final SortedMap result = new TreeMap();
        // prepare a query for atrributes
        final Query buildRunAttrQuery = session.createQuery("select bra.name, bra.value from BuildRunAttribute bra " +
                " where bra.buildRunID = ? ");
        buildRunAttrQuery.setCacheable(true);
        // prepare a query for build runs
        final Query q3 = session.createQuery("select br.buildRunID, br.buildRunNumber " +
                " from BuildRun br " +
                " where br.activeBuildID = ? " +
                "   and br.complete = 1 " +
                "   and br.type = ? " +
                " order by br.buildRunNumber desc");
        q3.setMaxResults(MAX_LAST_BUILDS);
        q3.setInteger(0, activeBuildID);
        q3.setByte(1, BuildRun.TYPE_BUILD_RUN);
        q3.setCacheable(true);
        // itereate build runs
        for (final Iterator buildRunIter = q3.iterate(); buildRunIter.hasNext();) {
          final TestStatistics testStatistics = new TestStatistics();
          testStatistics.addBuildCount(1);
          final Object[] buildRuns = (Object[]) buildRunIter.next();
          final Integer buildRinID = (Integer) buildRuns[0];
          final Integer buildRunNumber = (Integer) buildRuns[1];
          // execute and iterate an attr quesry for the given build run
          buildRunAttrQuery.setInteger(0, buildRinID);
          for (final Iterator buildRunAttrIter = buildRunAttrQuery.iterate(); buildRunAttrIter.hasNext();) {
            final Object[] attrs = (Object[]) buildRunAttrIter.next();
            final String attrName = (String) attrs[0];
            final String attrValue = (String) attrs[1];
            if (testToolCode == PersistentTestStats.TYPE_JUNIT) {
              if (attrName.equals(BuildRunAttribute.ATTR_JUNIT_FAILURES)) {
                testStatistics.addFailedTests(Integer.parseInt(attrValue));
              } else if (attrName.equals(BuildRunAttribute.ATTR_JUNIT_SUCCESSES)) {
                testStatistics.addSuccessfulTests(Integer.parseInt(attrValue));
              } else if (attrName.equals(BuildRunAttribute.ATTR_JUNIT_ERRORS)) {
                testStatistics.addErrorTests(Integer.parseInt(attrValue));
              }
            } else {
              throw new IllegalStateException("Unknown test tool code: " + testToolCode);
            }
          }
          // add only builds with tests
          if (testStatistics.getTotalTests() != 0) {
            result.put(buildRunNumber, testStatistics);
          }
        }
        return result;
      }
    });
  }


  /**
   * Returns current build time statists for last builds.
   *
   * @return SortedMap where key is a an integer build number and
   *         value is Integer number of seconds.
   */
  public SortedMap getRecentBuildTimesStatistics() {
    // REVIEW: subject for caching
    return (SortedMap) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final SortedMap result = new TreeMap();
        // prepare a query for build runs
        final Query query = session.createQuery("select br.buildRunNumber, br.startedAt, br.finishedAt " +
                " from BuildRun br " +
                " where br.activeBuildID = ? " +
                "   and br.complete = 1 " +
                "   and br.type = ? " +
                "   and br.resultID = ? " +
                " order by br.buildRunNumber desc");
        query.setMaxResults(MAX_LAST_BUILDS);
        query.setInteger(0, activeBuildID);
        query.setByte(1, BuildRun.TYPE_BUILD_RUN);
        query.setByte(2, BuildRun.BUILD_RESULT_SUCCESS);
        query.setCacheable(true);
        // itereate build runs
        for (final Iterator buildRunIter = query.iterate(); buildRunIter.hasNext();) {
          final TestStatistics testStatistics = new TestStatistics();
          testStatistics.addBuildCount(1);
          final Object[] buildRunInfo = (Object[]) buildRunIter.next();
          final Integer buildRunNumber = (Integer) buildRunInfo[0];
          final Date startedAt = (Date) buildRunInfo[1];
          final Date finishedAt = (Date) buildRunInfo[2];
          result.put(buildRunNumber, new Integer((int) ((finishedAt.getTime() - startedAt.getTime()) / 1000)));
        }
        return result;
      }
    });
  }


  public SortedMap getRecentPMDViolations() {
    return getRecentBuildRunStatistics(BuildRunAttribute.ATTR_PMD_PROBLEMS, MAX_LAST_BUILDS);
  }


  public SortedMap getRecentCheckstyleViolations() {
    return getRecentBuildRunStatistics(BuildRunAttribute.ATTR_CHECKSTYLE_ERRORS, MAX_LAST_BUILDS);
  }


  public SortedMap getRecentFindbugsViolations() {
    return getRecentBuildRunStatistics(BuildRunAttribute.ATTR_FINDBUGS_PROBLEMS, MAX_LAST_BUILDS);
  }


  public SortedMap getRecentTimeToFix() {
    return getRecentBuildRunStatistics(BuildRunAttribute.ATTR_TIME_TO_FIX, MAX_LAST_BUILDS);
  }


  /**
   * @param maxLastBuilds
   * @return time to fix MA
   */
  public SortedMap getRecentTimeToFixMovingAverage(final int maxLastBuilds) {
    return getRecentBuildRunStatistics(BuildRunAttribute.ATTR_TIME_TO_FIX_MOVING_AVERAGE, maxLastBuilds);
  }


  /**
   * @return time to fix MA
   */
  public SortedMap getRecentTimeToFixMovingAverage() {
    return getRecentTimeToFixMovingAverage(MAX_LAST_BUILDS);
  }


  public SortedMap getRecentBuildRunStatistics(final String statisticsAttrName, final int maxLastBuilds) {
    return (SortedMap) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final SortedMap result = new TreeMap();
        // prepare a query
        final Query q = session.createQuery("select br.buildRunNumber, bra.value from BuildRun br, BuildRunAttribute bra" +
                " where br.activeBuildID = ? " +
                "   and br.type = ? " +
                "   and bra.buildRunID = br.buildRunID " +
                "   and bra.name = ? " +
                " order by br.buildRunNumber desc");
        q.setInteger(0, activeBuildID);
        q.setByte(1, BuildRun.TYPE_BUILD_RUN);
        q.setString(2, statisticsAttrName);
        q.setCacheable(true);
        if (maxLastBuilds != Integer.MAX_VALUE) {
          q.setMaxResults(maxLastBuilds);
        }
        // itereate build runs
        for (final Iterator buildRunIter = q.iterate(); buildRunIter.hasNext();) {
          final Object[] buildRun = (Object[]) buildRunIter.next();
          final Integer buildNumber = (Integer) buildRun[0];
          final String value = (String) buildRun[1];
          result.put(buildNumber, new Integer(Integer.parseInt(value)));
        }
        return result;
      }
    });
  }


  /**
   * Returns current up-to-date hourly distribution.
   *
   * @return SortedMap where key is an Integer hour and value is
   *         BuildStatistics
   */
  public SortedMap getHourlyDistribution() {
    return new HourlyBuildDistributionRetrievier(activeBuildID).getStatistics();
  }


  /**
   * Returns current up-to-date day of week distribution.
   *
   * @return SortedMap where key is an Integer hour and value is
   *         BuildStatistics
   */
  public SortedMap getDayOfWeekDistribution() {
    return new WeekDayBuildDistributionRetriever(activeBuildID).getStatistics();
  }


  /**
   * Updates build statistics based on a just complete build
   * run.
   *
   * @param newBuildRun just complete build run.
   */
  public void updateStatistics(final BuildRun newBuildRun) {
    try {
      validateBuildRunToUpdate(newBuildRun);
      PersistentStatsUpdaterFactory.getUpdater().updateStatistics(newBuildRun);
    } catch (final Exception e) {
      // we are not allowing to propagate the exception,
      // instead, we report it to build admin.
      reportUpdateStatisticsError(e);
    }
  }


  /**
   * Resets all statistics caches.
   */
  public synchronized void clearStatistics() {

    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // delete up-to-date stats
        delete(ActiveBuildAttribute.STAT_CHANGE_LISTS_TO_DATE);
        delete(ActiveBuildAttribute.STAT_FAILED_BUILDS_TO_DATE);
        delete(ActiveBuildAttribute.STAT_SUCC_BUILDS_TO_DATE);
        delete(ActiveBuildAttribute.STAT_ISSUES_TO_DATE);

        // delete historical stats
        final Integer id = new Integer(activeBuildID);
        session.delete("from HourlyStats s where s.activeBuildID = ?", id, Hibernate.INTEGER);
        session.delete("from DailyStats s where s.activeBuildID = ?", id, Hibernate.INTEGER);
        session.delete("from MonthlyStats s where s.activeBuildID = ?", id, Hibernate.INTEGER);
        session.delete("from YearlyStats s where s.activeBuildID = ?", id, Hibernate.INTEGER);
        session.delete("from HourlyDistribution s where s.activeBuildID = ?", id, Hibernate.INTEGER);
        session.delete("from WeekDayDistribution s where s.activeBuildID = ?", id, Hibernate.INTEGER);
        return null;
      }


      /**
       * Deletes ActiveBuildAttr
       *
       * @param attrName
       *
       * @throws HibernateException
       */
      private void delete(final String attrName) throws HibernateException {
        session.delete("from ActiveBuildAttribute aba where aba.buildID = ? and aba.propertyName = ?", new Object[]{new Integer(activeBuildID), attrName}, new Type[]{Hibernate.INTEGER, Hibernate.STRING});
      }
    });
  }


  private void validateBuildRunToUpdate(final BuildRun newBuildRun) {
    final BuildRunConfig buildRunConfig = cm.getBuildRunConfig(newBuildRun);
    ArgumentValidator.validateArgumentNotNull(newBuildRun, "build run");
    if (buildRunConfig.getActiveBuildID() != activeBuildID) {
      throw new IllegalArgumentException("Run build ID " + newBuildRun.getBuildID() +
              "does not match manager build ID " + activeBuildID);
    }
  }


  /**
   * Clears and re-populates statistics tables.
   */
  public synchronized void initStatistics() {
    // clear
    clearStatistics();

    // get build run ID
    final List buildRunIDList = (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(
                "select br.buildRunID from BuildRun as br " +
                        " where br.activeBuildID = ?" +
                        "   and br.type = ?");
        q.setInteger(0, activeBuildID);
        q.setByte(1, BuildRun.TYPE_BUILD_RUN);
        q.setCacheable(true);
        return q.list();
      }
    });

    // recalculate
    for (final Iterator i = buildRunIDList.iterator(); i.hasNext();) {
      final Integer id = (Integer) i.next();
      updateStatistics(cm.getBuildRun(id.intValue()));
    }
  }


  /**
   * Helper method to report error
   */
  private static void reportUpdateStatisticsError(final Exception e) {
    final Error error = new Error("Error while updating statistics: " + StringUtils.toString(e));
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_STATISTICS);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setDetails(e);
    error.setSendEmail(true);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }
}
