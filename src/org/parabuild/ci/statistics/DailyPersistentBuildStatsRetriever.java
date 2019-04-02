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
 * returns per-day statistics for a given build.
 */
public final class DailyPersistentBuildStatsRetriever extends AbstractPersistentBuildStatsRetriever {


  /**
   * @param activeBuildID
   */
  public DailyPersistentBuildStatsRetriever(final int activeBuildID) {
    super(activeBuildID);
  }


  protected StatisticsRetrieverConfiguration getConfiguration() {
    final int rollerInitTrauncateTo = Calendar.DAY_OF_MONTH;
    final int rollerStep = Calendar.DAY_OF_MONTH;
    final int cutOffBefore = Calendar.MONTH;
    return new StatisticsRetrieverConfiguration(rollerInitTrauncateTo,
            DEFAULT_STATS_DAYS,
            rollerStep,
            cutOffBefore);
  }


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
  protected List getStatsFromDB(final Session session, final int buildID, final Date fromDate,
                                final Date toDate) throws HibernateException {

    final Query q = session.createQuery("select ds from DailyStats ds " +
            " where ds.activeBuildID = ? " +
            "   and ds.sampleTime >= ? " +
            "   and ds.sampleTime <= ?");
    q.setInteger(0, buildID);
    q.setCacheable(true);
    q.setDate(1, fromDate);
    q.setDate(2, toDate);
    return q.list();
  }
}
