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


public class SATestBuildStatistics extends TestCase {

  public static final int TEST_SUCC_BUILD_COUNT = 10;
  public static final int TEST_FAILED_BUILD_COUNT = 20;
  public static final int TEST_CHECKINS_COUNT = 30;
  public static final int TEST_ISSUES_COUNT = 40;
  public static final int TEST_SUCCESSFUL_BUILD_PRECENT = 33;
  public static final int TEST_FALIED_BUILD_PRECENT = 67;
  public static final int TEST_TOTAL_BUILDS = 30;


  public void test_Constuctor() {
    final BuildStatistics buildStatistics = new BuildStatistics(TEST_SUCC_BUILD_COUNT, TEST_FAILED_BUILD_COUNT, TEST_CHECKINS_COUNT, TEST_ISSUES_COUNT);
    assertEquals(TEST_SUCC_BUILD_COUNT, buildStatistics.getSuccessfulBuilds());
    assertEquals(TEST_FAILED_BUILD_COUNT, buildStatistics.getFailedBuilds());
    assertEquals(TEST_CHECKINS_COUNT, buildStatistics.getChangeLists());
    assertEquals(TEST_ISSUES_COUNT, buildStatistics.getIssues());
    assertEquals(TEST_SUCCESSFUL_BUILD_PRECENT, buildStatistics.getSuccessfulBuildsPercent());
    assertEquals(TEST_FALIED_BUILD_PRECENT, buildStatistics.getFailedBuildsPercent());
    assertEquals(TEST_TOTAL_BUILDS, buildStatistics.getTotalBuilds());
  }


  public void test_copyConstuctor() {
    final BuildStatistics source = new BuildStatistics(TEST_SUCC_BUILD_COUNT, TEST_FAILED_BUILD_COUNT, TEST_CHECKINS_COUNT, TEST_ISSUES_COUNT);
    final BuildStatistics buildStatistics = new BuildStatistics(source);
    assertEquals(TEST_SUCC_BUILD_COUNT, buildStatistics.getSuccessfulBuilds());
    assertEquals(TEST_FAILED_BUILD_COUNT, buildStatistics.getFailedBuilds());
    assertEquals(TEST_CHECKINS_COUNT, buildStatistics.getChangeLists());
    assertEquals(TEST_ISSUES_COUNT, buildStatistics.getIssues());
    assertEquals(TEST_SUCCESSFUL_BUILD_PRECENT, buildStatistics.getSuccessfulBuildsPercent());
    assertEquals(TEST_FALIED_BUILD_PRECENT, buildStatistics.getFailedBuildsPercent());
    assertEquals(TEST_TOTAL_BUILDS, buildStatistics.getTotalBuilds());
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestBuildStatistics.class);
  }


  public SATestBuildStatistics(final String s) {
    super(s);
  }
}
