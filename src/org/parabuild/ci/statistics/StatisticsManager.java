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

import org.parabuild.ci.object.BuildRun;

import java.util.SortedMap;

/**
 * Created by simeshev on Jan 18, 2005 at 11:16:53 AM
 */
public interface StatisticsManager {

  /**
   * Returns current up-to-date statistics.
   */
  BuildStatistics getUpToDateBuildStatistics();


  /**
   * Returns current month-to-date statistics
   *
   * @return SortedMap where key is a day Date and value is
   *         BuildStatistics
   *
   * @see BuildStatistics
   */
  SortedMap getMonthToDateBuildStatistics();


  /**
   * Returns current year-to-date statistics.
   *
   * @return SortedMap where key is a month Date and value is
   *         BuildStatistics
   */
  SortedMap getYearToDateBuildStatistics();


  /**
   * Returns current up-to-date hourly distribution.
   *
   * @return SortedMap where key is an Integer hour and value is
   *         BuildStatistics
   */
  SortedMap getHourlyDistribution();


  /**
   * Returns current up-to-date day of week distribution.
   *
   * @return SortedMap where key is an Integer hour and value is
   *         BuildStatistics
   */
  SortedMap getDayOfWeekDistribution();


  /**
   * Updates build statistics based on a just complete build
   * run.
   *
   * @param newBuildRun just complete build run.
   */
  void updateStatistics(BuildRun newBuildRun);


  /**
   * Resets all statistics caches.
   */
  void clearStatistics();


  /**
   * Clears and re-populates statistics tables.
   */
  void initStatistics();


  /**
   * Returns current month-to-date test distribution by day.
   *
   * @return SortedMap where key is a month Date and value is
   *         {@link TestStatistics}
   */
  SortedMap getMonthToDateTestStatistics(byte testToolCode);


  /**
   * Returns current Year-to-date test distribution by day.
   *
   * @return SortedMap where key is a Year Date and value is
   *         {@link TestStatistics}
   */
  SortedMap getYearToDateTestStatistics(byte testToolCode);


  /**
   * Returns current test statistics for last builds.
   *
   * @return SortedMap where key is a an integer build number and value is
   *         {@link TestStatistics}
   */
  SortedMap getRecentTestStatistics(byte testToolCode);


  /**
   * Returns current build time statistics for last builds.
   *
   * @return SortedMap where key is a an integer build number and
   *  value is Integer number of seconds.
   */
  SortedMap getRecentBuildTimesStatistics();


  /**
   * Returns build number and number of violations.
   */
  SortedMap getRecentPMDViolations();


  SortedMap getRecentFindbugsViolations();


  /**
   * @return recent time to fix.
   */
  SortedMap getRecentTimeToFix();


  /**
   * @return time to fix MA
   */
  SortedMap getRecentTimeToFixMovingAverage(int maxLastBuilds);


  /**
   * @return time to fix MA
   */
  SortedMap getRecentTimeToFixMovingAverage();


  /**
   * Returns build number and number of violations.
   */
  SortedMap getRecentCheckstyleViolations();
}
