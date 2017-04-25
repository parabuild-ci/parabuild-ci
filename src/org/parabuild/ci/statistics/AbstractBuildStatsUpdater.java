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

import org.parabuild.ci.object.*;

/**
 */
abstract class AbstractBuildStatsUpdater extends AbstractStatsUpdater { // NOPMD AbstractClassWithoutAbstractMethod

  /**
   * This method should calculate this builf run's statistics
   * and add to the given statistics sample. Casting is required.
   */
  public final void addRunStatsToPersistantStats(final BuildRun buildRun, final StatisticsSample stample) {
    final BuildStatistics runStats = StatisticsUtils.calculateBuildStatistics(buildRun);
    final PersistentBuildStats pStat = (PersistentBuildStats)stample;
    addRunStatsToPersistantBuildStats(runStats, pStat);
  }


  static void addRunStatsToPersistantBuildStats(final BuildStatistics runStats, final PersistentBuildStats pStat) {// make BuildStatistics that is up to date first
    final BuildStatistics result = new BuildStatistics(runStats);
    result.addChangeLists(pStat.getChangeListCount());
    result.addFailedBuilds(pStat.getFailedBuildCount());
    result.addIssues(pStat.getIssueCount());
    result.addSuccessfulBuilds(pStat.getSuccessfulBuildCount());

    // make store
    pStat.setChangeListCount(result.getChangeLists());
    pStat.setFailedBuildCount(result.getFailedBuilds());
    pStat.setFailedBuildPercent(result.getFailedBuildsPercent());
    pStat.setIssueCount(result.getIssues());
    pStat.setSuccessfulBuildCount(result.getSuccessfulBuilds());
    pStat.setSuccessfulBuildPercent(result.getSuccessfulBuildsPercent());
    pStat.setTotalBuildCount(result.getTotalBuilds());
  }
}
