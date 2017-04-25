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
import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;

/**
 * GoF Composite implementation of the PersistentStatisticsUpdater
 * functor to update persitents statistcs according to the
 * results of the build run.
 *
 * @see DailyBuildStatsUpdater
 * @see HourlyBuildStatsUpdater
 * @see MonthlyBuildStatsUpdater
 * @see YearlyBuildStatsUpdater
 */
final class CompositePersistentStatsUpdater implements PersistentStatsUpdater {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(CompositePersistentStatsUpdater.class); // NOPMD

  private final List updaterList = new ArrayList(11);


  /**
   * Constructor.
   */
  public CompositePersistentStatsUpdater() {
    updaterList.add(new YearlyBuildStatsUpdater());
    updaterList.add(new MonthlyBuildStatsUpdater());
    updaterList.add(new DailyBuildStatsUpdater());
    updaterList.add(new HourlyBuildStatsUpdater());
    updaterList.add(new UpToDateBuildStatsUpdater());
    updaterList.add(new HourlyBuildDistributionUpdater());
    updaterList.add(new WeekDayBuildDistributionUpdater());
    // test updaters
    updaterList.add(new YearlyTestStatsUpdater(PersistentTestStats.TYPE_JUNIT));
    updaterList.add(new MonthlyTestStatsUpdater(PersistentTestStats.TYPE_JUNIT));
    updaterList.add(new DailyTestStatsUpdater(PersistentTestStats.TYPE_JUNIT));
    updaterList.add(new HourlyTestStatsUpdater(PersistentTestStats.TYPE_JUNIT));
  }


  /**
   * Updates statistics corresponding this build run.
   *
   * @param buildRun
   */
  public void updateStatistics(final BuildRun buildRun) {
    if (!buildRun.completed()) return;
    for (final Iterator i = updaterList.iterator(); i.hasNext();) {
      final PersistentStatsUpdater updater = (PersistentStatsUpdater)i.next();
      updater.updateStatistics(buildRun);
    }
  }
}
