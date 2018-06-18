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

import java.util.*;

import net.sf.hibernate.*;
import org.parabuild.ci.object.*;

/**
 * Updates per-hour statistics.
 */
final class HourlyBuildStatsUpdater extends AbstractBuildStatsUpdater {


  /**
   * @return truncation lavel to be used by the updater.
   *
   * @see Calendar#DAY_OF_MONTH
   * @see Calendar.MONTH
   * @see Calendar#HOUR_OF_DAY
   * @see Calendar#YEAR
   */
  public int truncateLevel() {
    return Calendar.HOUR_OF_DAY;
  }


  /**
   * @param session Hibenrate session
   * @param activeBuildID to get stats for
   * @param sampleDate Date already truncated
   *
   * @return persistant stats corrsponding the given buidl run or
   *         null if doesn't exist.
   *
   * @throws HibernateException
   */
  protected StatisticsSample findPersistedStats(final Session session, final int activeBuildID, final Date sampleDate) throws HibernateException {
    final Query query = session.createQuery("select hs from HourlyStats hs where hs.activeBuildID = ? and hs.sampleTime = ?");
    query.setInteger(0, activeBuildID);
    query.setTimestamp(1, sampleDate);
    query.setCacheable(true);
    return (HourlyStats)query.uniqueResult();
  }


  /**
   * Factory method to make an objct that implements
   * PersistentStats.
   *
   * @return new instance of PersistentStats.
   */
  protected StatisticsSample makePersistentStats() {
    return new HourlyStats();
  }
}


