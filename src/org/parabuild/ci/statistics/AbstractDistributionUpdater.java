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

import org.apache.commons.logging.*;

import net.sf.hibernate.*;
import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * Created by IntelliJ IDEA. User: vimeshev Date: May 14, 2005
 * Time: 9:19:53 PM To change this template use File | Settings |
 * File Templates.
 */
abstract class AbstractDistributionUpdater implements PersistentStatsUpdater {

  private static final Log log = LogFactory.getLog(HourlyBuildDistributionUpdater.class);


  /**
   * Updates statistics corresponding this build run.  This
   * method should not throw any exceptions. Instead, it should
   * report any errors using ErrorManager.
   *
   * @param buildRun
   */
  public final void updateStatistics(final BuildRun buildRun) {
    final BuildStatistics runStats = StatisticsUtils.calculateBuildStatistics(buildRun);
    // check
    if (!buildRun.completed()) return;

    // update
    try {
      ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          // find stats objects that will be used to store stats fot this build run
          final int activeBuildID = buildRun.getActiveBuildID();
          final int distrTarget = getDistributionTarget(buildRun);
          PersistentDistribution pd = findPersistedDistribution(session, activeBuildID, distrTarget);
          if (pd == null) {
            pd = makePersistentDistribution();
            pd.setActiveBuildID(activeBuildID);
            pd.setTarget(distrTarget);
            StatisticsUtils.addStatsToDistribution(runStats, pd);
            try {
              session.save(pd);
            } catch (Exception e) {
              if (log.isDebugEnabled()) log.debug("e: " + e);
              if (log.isDebugEnabled()) log.debug("pd: " + pd);
            }
          } else {
            StatisticsUtils.addStatsToDistribution(runStats, pd);
            session.update(pd);
          }
          return null;
        }
      });
    } catch (Exception e) {
      // report error
      final Error error = new Error(StringUtils.toString(e));
      error.setBuildName(buildRun.getBuildName());
      error.setDetails(e);
      error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      error.setSendEmail(false);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    }
  }


  /**
   * Factory method for PersistentDistribution. Extending classes
   * should return new instance of corresponding implementation
   * of PersistentDistribution.
   *
   * @return implementation PersistentDistribution
   */
  protected abstract PersistentDistribution makePersistentDistribution();


  /**
   * Finds existing persisted distribution.
   *
   * @param session
   * @param activeBuildID
   * @param target
   *
   */
  protected abstract PersistentDistribution findPersistedDistribution(Session session, int activeBuildID, int target) throws HibernateException;


  /**
   * Returns distribuition target corresponging this build run.
   * An example of target is a day a week, a month.
   *
   * @param buildRun
   *
   * @return distribuition target corresponging this build run.
   */
  protected abstract int getDistributionTarget(BuildRun buildRun);
}
