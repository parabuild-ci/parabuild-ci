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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This implementation of the PersistentStatisticsRetriever
 * returns per-month statistics for a given build.
 *
 * @see AbstractPersistentStatsRetriever#getStatistics()
 */
public final class MonthlyPersistentTestStatsRetriever extends AbstractPersistentTestStatsRetriever {


  /**
   */
  public MonthlyPersistentTestStatsRetriever(final int activeBuildID, final byte testToolCode) {
    super(activeBuildID, testToolCode);
  }


  protected StatisticsRetrieverConfiguration getConfiguration() {
    final int rollerInitTruncateTo = Calendar.MONTH;
    final int rollerStep = Calendar.MONTH;
    final int cutOffBefore = Calendar.YEAR;
    return new StatisticsRetrieverConfiguration(rollerInitTruncateTo,
            DEFAULT_STATS_MONTHS,
            rollerStep,
            cutOffBefore);
  }


  /**
   * Returns list of PersistentObject corresponding the type of
   * the statistics.
   */
  protected List getStatsFromDB(final Session session, final int buildID, final Date fromDate,
                                final Date toDate) throws HibernateException {

    final Query q = session.createQuery("select mts from MonthlyTestStats mts " +
            " where mts.activeBuildID = ? " +
            "   and mts.testCode = ? " +
            "   and mts.sampleTime >= ? " +
            "   and mts.sampleTime <= ?");
    q.setCacheable(true);
    q.setInteger(0, buildID);
    q.setByte(1, getTestCode());
    q.setDate(2, fromDate);
    q.setDate(3, toDate);
    return q.list();
  }
}
