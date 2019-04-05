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
import org.parabuild.ci.object.PersistentTestStats;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyPersistentTestStatsRetriever extends AbstractPersistentTestStatsRetriever {


  /**
   * @param testToolCode a code for a test tool.
   * @see PersistentTestStats#TYPE_CPPUNIT
   * @see PersistentTestStats#TYPE_JUNIT
   * @see PersistentTestStats#TYPE_NUNIT
   */
  public DailyPersistentTestStatsRetriever(final int activeBuildID, final byte testToolCode) {
    super(activeBuildID, testToolCode);
  }


  protected StatisticsRetrieverConfiguration getConfiguration() {
    final int rollerInitTruncateTo = Calendar.DAY_OF_MONTH;
    final int rollerStep = Calendar.DAY_OF_MONTH;
    final int cutOffBefore = Calendar.MONTH;
    return new StatisticsRetrieverConfiguration(rollerInitTruncateTo,
            DEFAULT_STATS_DAYS,
            rollerStep,
            cutOffBefore);
  }


  /**
   * Returns list of PersistentObject corresponding the type of
   * the statistics.
   */
  protected List getStatsFromDB(final Session session, final int buildID, final Date fromDate,
                                final Date toDate) throws HibernateException {

    final Query q = session.createQuery("select dts from DailyTestStats dts " +
            " where dts.activeBuildID = ? " +
            "   and dts.testCode = ? " +
            "   and dts.sampleTime >= ? " +
            "   and dts.sampleTime <= ?");
    q.setCacheable(true);
    q.setInteger(0, buildID);
    q.setByte(1, getTestCode());
    q.setDate(2, fromDate);
    q.setDate(3, toDate);
    return q.list();
  }
}
