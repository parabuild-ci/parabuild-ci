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
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.PersistentDistribution;
import org.parabuild.ci.object.WeekDayDistribution;

import java.util.Calendar;

/**
 * Updates day-of-the-week build results distribution.
 */
final class WeekDayBuildDistributionUpdater extends AbstractDistributionUpdater {


  /**
   * Factory method for PersistentDistribution. Extending classes
   * should return new instance of corresponding implementation
   * of PersistentDistribution.
   *
   * @return implementation PersistentDistribution
   */
  protected PersistentDistribution makePersistentDistribution() {
    return new WeekDayDistribution();
  }


  /**
   * Finds existing persisted distribution.
   */
  protected PersistentDistribution findPersistedDistribution(final Session session, final int activeBuildID, final int target) throws HibernateException {
    final Query query = session.createQuery("select pd from WeekDayDistribution pd where pd.activeBuildID = ? and pd.target = ?");
    query.setInteger(0, activeBuildID);
    query.setInteger(1, target);
    query.setCacheable(true);
    return (PersistentDistribution)query.uniqueResult();
  }


  /**
   * Returns distribution target corresponding this build run.
   * An example of target is a day a week, a month.
   *
   * @return distribution target corresponding this build run.
   */
  protected int getDistributionTarget(final BuildRun buildRun) {
    final Calendar c = Calendar.getInstance();
    c.setTime(buildRun.getFinishedAt());
    return c.get(Calendar.DAY_OF_WEEK);
  }
}
