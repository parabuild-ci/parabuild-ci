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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.StatisticsSample;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Gof Strategy for PersistentStatisticsRetriever
 *
 * @see #getConfiguration
 * @see #getStatsFromDB
 */
abstract class AbstractPersistentStatsRetriever {

  private static final Log log = LogFactory.getLog(StatisticsManagerImpl.class);

  public static final int DEFAULT_STATS_MONTHS = 12;
  public static final int DEFAULT_STATS_DAYS = 30;
  public static final int DEFAULT_STATS_HOURS = 24;


  private int activeBuildID;


  /**
   * Strategey constructor.
   *
   * @param activeBuildID
   */
  AbstractPersistentStatsRetriever(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  /**
   * @return configuration for the PersistentStatisticsRetriever
   * @see StatisticsRetrieverConfiguration
   */
  protected abstract StatisticsRetrieverConfiguration getConfiguration();


  /**
   * Returns list of PersistentObject corresponding the type of
   * the statistics.
   *
   * @param session
   * @param buildID
   * @param fromDate
   * @return
   * @throws HibernateException
   */
  protected abstract List getStatsFromDB(Session session, int buildID, Date fromDate,
                                         Date toDate) throws HibernateException;


  /**
   * Returns list of {@link org.parabuild.ci.object.PersistentBuildStats} corresponding the type of
   * the statistics.
   *
   * @param fromDate
   * @return
   * @throws HibernateException
   */
  public final List getStatistics(final Date fromDate, final Date toDate) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return getStatsFromDB(session, activeBuildID, fromDate, toDate);
      }
    });
  }


  /**
   * Returns SortedMap contaning BuildStatistics objects as
   * values and dates as key sorted by dates. Size of the
   * returned map depends on the configuration.
   *
   * @see #getConfiguration
   * @see StatisticsRetrieverConfiguration
   */
  public final SortedMap getStatistics() {

    final StatisticsRetrieverConfiguration configuration = getConfiguration();
    final int rollerInitTrauncateTo = configuration.getRollerInitTrauncateTo();
    final int statisticsSize = configuration.getStatisticsSize();
    final int rollerStep = configuration.getRollerStep();
    final int cutOffBefore = configuration.getCutOffBefore();

    // preExecute roller
    final Calendar roller = Calendar.getInstance();
    roller.setTime(StatisticsUtils.truncateDate(new Date(), rollerInitTrauncateTo));
//    if (log.isDebugEnabled()) log.debug("initialized roller: " + roller.getTime());

    // create inital empty distribution rolling back for 30 days
    final SortedMap result = new TreeMap(StatisticsUtils.NATURAL_DATE_COMPARATOR);
    for (int i = 0; i < statisticsSize; i++) {
      final Date date = roller.getTime();
//      if (log.isDebugEnabled()) log.debug("rolled date at " + i + ": " + date);
      result.put(date, null);
      roller.add(rollerStep, -1);
    }

    // calculate cutoff date
    final Calendar cal = Calendar.getInstance();
    cal.add(cutOffBefore, -1);
    final Date cutOffDate = cal.getTime();

    // populate distribution
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final long start = System.currentTimeMillis();

        // query DB
        //if (log.isDebugEnabled()) log.debug("cut off date: " + cutOffDate);
        final List list = getStatsFromDB(session, activeBuildID, cutOffDate, new Date());

        // travers DailyStats
        for (final Iterator i1 = list.iterator(); i1.hasNext();) {
          final StatisticsSample sample = (StatisticsSample) i1.next();
          if (result.get(sample.getSampleTime()) != null) {
            log.warn("statistics was unexpectedly not null for date: " + sample.getSampleTime());
            continue;
          }
          result.put(sample.getSampleTime(), createStatisticsFromSample(sample));
        }

        if (log.isDebugEnabled()) {
          log.debug("distribution  populating took: " + (System.currentTimeMillis() - start) + " ms");
        }
        return null;
      }
    });

    // fill gaps
    for (final Iterator i = result.entrySet().iterator(); i.hasNext();) {
      final Map.Entry entry = (Map.Entry) i.next();
      if (entry.getValue() == null) {
        entry.setValue(createZeroStatistics());
      }
    }

    // done
    return result;
  }


  /**
   * @return a statistics object that contains all zeroes
   */
  protected abstract Object createZeroStatistics();


  /**
   * @return creates a statistics obect based on (copy) of the given sample
   */
  protected abstract Object createStatisticsFromSample(final StatisticsSample sample);


  public String toString() {
    return "AbstractPersistentStatsRetriever{" +
            "activeBuildID=" + activeBuildID +
            '}';
  }
}
