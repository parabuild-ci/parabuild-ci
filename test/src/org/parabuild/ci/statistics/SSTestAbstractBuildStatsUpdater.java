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

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * Tests AbstractStatsUpdater
 */
public class SSTestAbstractBuildStatsUpdater extends ServersideTestCase {

  public static final int TEST_SUCC_BUILD_COUNT = 10;
  public static final int TEST_FAILED_BUILD_COUNT = 20;
  public static final int TEST_CHECKINS_COUNT = 30;
  public static final int TEST_ISSUES_COUNT = 40;
  public static final int TEST_SUCCESSFUL_BUILD_PRECENT = 33;
  public static final int TEST_FALIED_BUILD_PRECENT = 67;
  public static final int TEST_TOTAL_BUILDS = 30;


  public SSTestAbstractBuildStatsUpdater(final String s) {
    super(s);
  }


  public void test_addBuildStatsToPersistantStats() {

    // create BuildStatistics
    final BuildStatistics buildStatistics = new BuildStatistics(TEST_SUCC_BUILD_COUNT, TEST_FAILED_BUILD_COUNT, TEST_CHECKINS_COUNT, TEST_ISSUES_COUNT);

    // create zeroed PersistentStats
    final PersistentBuildStats persistentStats = new DailyStats();

    // add
    AbstractBuildStatsUpdater.addRunStatsToPersistantBuildStats(buildStatistics, persistentStats);

    // assert
    assertEquals(TEST_SUCC_BUILD_COUNT, persistentStats.getSuccessfulBuildCount());
    assertEquals(TEST_FAILED_BUILD_COUNT, persistentStats.getFailedBuildCount());
    assertEquals(TEST_CHECKINS_COUNT, persistentStats.getChangeListCount());
    assertEquals(TEST_ISSUES_COUNT, persistentStats.getIssueCount());
    assertEquals(TEST_TOTAL_BUILDS, persistentStats.getTotalBuildCount());
    assertEquals(TEST_SUCCESSFUL_BUILD_PRECENT, persistentStats.getSuccessfulBuildPercent());
    assertEquals(TEST_FALIED_BUILD_PRECENT, persistentStats.getFailedBuildPercent());

    // add again
    AbstractBuildStatsUpdater.addRunStatsToPersistantBuildStats(buildStatistics, persistentStats);

    // assert
    assertEquals(2 * TEST_SUCC_BUILD_COUNT, persistentStats.getSuccessfulBuildCount());
    assertEquals(2 * TEST_FAILED_BUILD_COUNT, persistentStats.getFailedBuildCount());
    assertEquals(2 * TEST_CHECKINS_COUNT, persistentStats.getChangeListCount());
    assertEquals(2 * TEST_ISSUES_COUNT, persistentStats.getIssueCount());
    assertEquals(2 * TEST_TOTAL_BUILDS, persistentStats.getTotalBuildCount());
    assertEquals(TEST_SUCCESSFUL_BUILD_PRECENT, persistentStats.getSuccessfulBuildPercent());
    assertEquals(TEST_FALIED_BUILD_PRECENT, persistentStats.getFailedBuildPercent());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestAbstractBuildStatsUpdater.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    ErrorManagerFactory.getErrorManager().clearAllActiveErrors();
  }
}
