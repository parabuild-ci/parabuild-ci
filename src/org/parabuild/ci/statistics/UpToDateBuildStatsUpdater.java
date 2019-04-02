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

import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * Updater that updates current up-to-date build statistics.
 */
final class UpToDateBuildStatsUpdater implements PersistentStatsUpdater {


  /**
   * Updates statistics corresponding this build run.
   *
   * @param buildRun
   */
  public void updateStatistics(final BuildRun buildRun) {
    final BuildStatistics runStats = StatisticsUtils.calculateBuildStatistics(buildRun);
    try {
      ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() {
          final int buildID = buildRun.getActiveBuildID();
          addToActtiveBuildAtribute(buildID, ActiveBuildAttribute.STAT_SUCC_BUILDS_TO_DATE, runStats.getSuccessfulBuilds());
          addToActtiveBuildAtribute(buildID, ActiveBuildAttribute.STAT_FAILED_BUILDS_TO_DATE, runStats.getFailedBuilds());
          addToActtiveBuildAtribute(buildID, ActiveBuildAttribute.STAT_CHANGE_LISTS_TO_DATE, runStats.getChangeLists());
          addToActtiveBuildAtribute(buildID, ActiveBuildAttribute.STAT_ISSUES_TO_DATE, runStats.getIssues());
          return null;
        }
      });
    } catch (final Exception e) {
      // report error
      final Error error = new Error(StringUtils.toString(e));
      error.setBuildName(buildRun.getBuildName());
      error.setDetails(e);
      error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      error.setSendEmail(false);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    }
  }


  private static void addToActtiveBuildAtribute(final int buildID, final String attrName, final int toAdd) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    ActiveBuildAttribute succAttr = cm.getActiveBuildAttribute(buildID, attrName);
    if (succAttr == null) succAttr = new ActiveBuildAttribute(buildID, attrName, "0");
    succAttr.setPropertyValue(succAttr.getPropertyValueAsInteger() + toAdd);
    cm.saveObject(succAttr);
  }
}
