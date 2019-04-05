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
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.object.DailyTestStats;
import org.parabuild.ci.object.StatisticsSample;

import java.util.Calendar;
import java.util.Date;

/**
 * Updates per-day statistics.
 */
final class DailyTestStatsUpdater extends AbstractTestStatsUpdater {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(DailyTestStatsUpdater.class); // NOPMD

  public DailyTestStatsUpdater(final byte testCode) {
    super(testCode);
  }


  /**
   * @return truncation level to be used by the updater.
   *
   * @see Calendar#DAY_OF_MONTH
   * @see Calendar#HOUR_OF_DAY
   * @see Calendar#YEAR
   */
  public int truncateLevel() {
    return Calendar.DAY_OF_MONTH;
  }


  /**
   * @param session Hibernate session
   * @param activeBuildID to get stats for
   * @param sampleDate Date already truncated
   *
   * @return persistent stats corresponding the given build run or
   *         null if doesn't exist.
   *
   * @throws HibernateException
   */
  protected StatisticsSample findPersistedStats(final Session session, final int activeBuildID, final Date sampleDate) throws HibernateException {
    final Query query = session.createQuery("select dst from DailyTestStats dst where dst.activeBuildID = ? and dst.testCode = ? and dst.sampleTime = ?");
    query.setInteger(0, activeBuildID);
    query.setByte(1, getTestCode());
    query.setTimestamp(2, sampleDate);
    query.setCacheable(true);
    return (StatisticsSample) query.uniqueResult();
  }


  /**
   * Factory method to make an object that implements
   * PersistentStats.
   *
   * @return new instance of PersistentStats.
   */
  protected StatisticsSample makePersistentStats() {
    final DailyTestStats dailyTestStats = new DailyTestStats();
    dailyTestStats.setTestCode(getTestCode());
    return dailyTestStats;
  }
}
