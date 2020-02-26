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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.StatisticsSample;

import java.util.Calendar;
import java.util.Date;

/**
 * Strategy for PersistentStatisticsUpdater. Implementers should
 * take care about the abstract methods.
 *
 * @see #findPersistedStats
 * @see #truncateLevel()
 * @see #makePersistentStats()
 */
abstract class AbstractStatsUpdater implements PersistentStatsUpdater {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(AbstractStatsUpdater.class); // NOPMD


  /**
   * Updates statistics corresponding this build run.
   */
  public final void updateStatistics(final BuildRun buildRun) {

    // check
    if (!buildRun.completed()) return;

    // update
    try {
      ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          // find stats objects that will be used to store stats fot this build run
          final int activeBuildID = buildRun.getActiveBuildID();
          final Date sampleDate = truncateBuildRunDate(buildRun);
//          if (log.isDebugEnabled()) log.debug("activeBuildID: " + activeBuildID);
//          if (log.isDebugEnabled()) log.debug("sampleDate: " + sampleDate);
          StatisticsSample pStat = findPersistedStats(session, activeBuildID, sampleDate);
          if (pStat == null) {
            pStat = makePersistentStats();
            pStat.setActiveBuildID(activeBuildID);
            pStat.setSampleTime(sampleDate);
            addRunStatsToPersistentStats(buildRun, pStat);
            try {
//              if (log.isDebugEnabled()) log.debug("pStat before save: " + pStat);
              session.save(pStat);
            } catch (final Exception e) {
              // REVIEWME: simeshev@parabuilci.org ->
              // we ignore it because there are weird errors at initial data seed
              // where the truncateBuildRunDate above couldn't find already
              // existing stats, the hour in particular - looking like
              // the previous inserts didn't make it to the database.
              // should investigate further.
              IoUtils.ignoreExpectedException(e);
              // NOTE: simeshev@parabuildci.org ->
              // reportUpdateError(e, buildRun);
            }
          } else {
//            if (log.isDebugEnabled()) log.debug("pStat before update: " + pStat);
            addRunStatsToPersistentStats(buildRun, pStat);
            session.update(pStat);
          }
          return null;
        }
      });
    } catch (final Exception e) {
      reportUpdateError(e, buildRun);
    }
  }


  /**
   * This method should calculate this build run's statistics
   * and add to the given statistics sample. Casting is required.
   */
  protected abstract void addRunStatsToPersistentStats(BuildRun buildRun, StatisticsSample sampleToUpdate);


  /**
   * @return truncation level to be used by the updater.
   *
   * @see Calendar#DAY_OF_MONTH
   * @see Calendar#HOUR_OF_DAY
   * @see Calendar#YEAR
   */
  public abstract int truncateLevel();


  /**
   * Factory method to make an object that implements
   * PersistentStats.
   *
   * @return new instance of PersistentStats.
   */
  protected abstract StatisticsSample makePersistentStats();


  /**
   * @param session Hibernate session
   * @param activeBuildID to get stats for
   * @param sampleDate Date already truncated
   *
   * @return persistent stats corresponding the given build run or
   *         null if doesn't exist.
   */
  protected abstract StatisticsSample findPersistedStats(Session session, int activeBuildID, Date sampleDate) throws HibernateException;


  /**
   * Helper to truncate build run date according to implementor's
   * truncate level.
   */
  private Date truncateBuildRunDate(final BuildRun buildRun) {
    return StatisticsUtils.truncateDate(buildRun.getFinishedAt(), truncateLevel());
  }

  private static void reportUpdateError(final Exception e, final BuildRun buildRun) {
    final Error error = new Error(StringUtils.toString(e));
    error.setBuildName(buildRun.getBuildName());
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSendEmail(false);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }
}
