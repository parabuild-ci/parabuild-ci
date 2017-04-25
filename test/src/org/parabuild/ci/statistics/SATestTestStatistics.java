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

/**
 * Tests TestStatistics
 */
public class SATestTestStatistics extends TestCase {

  private static final int TEST_SUCC_COUNT = 40;
  private static final int TEST_FAILED_COUNT = 35;
  private static final int TEST_ERROR_COUNT = 25;
  private static final int TEST_ERROR_PERCENT = 25;
  private static final int TEST_SUCC_PERCENT = 40;
  private static final int TEST_FALIED_PERCENT = 35;
  private static final int ONE_HUNDRED_PERCENT = 100;
  private static final int TEST_TOTAL_TESTS = 100;
  private static final int ZERO = 0;

  private TestStatistics testStatistics;


  public SATestTestStatistics(final String s) {
    super(s);
  }


  public void test_addErrorTests() {
    testStatistics.addErrorTests(TEST_ERROR_COUNT);
    assertEquals(TEST_ERROR_COUNT, testStatistics.getErrorTests());
    assertEquals(ZERO, testStatistics.getFailedTests());
    assertEquals(ZERO, testStatistics.getSuccessfulTests());
    assertEquals(ZERO, testStatistics.getFailedTestsPercent());
    assertEquals(ZERO, testStatistics.getSuccessfulTestsPercent());
    assertEquals(ONE_HUNDRED_PERCENT, testStatistics.getErrorTestsPercent());
  }


  public void test_addFailedTests() {
    testStatistics.addFailedTests(TEST_FAILED_COUNT);
    assertEquals(TEST_FAILED_COUNT, testStatistics.getFailedTests());
    assertEquals(ZERO, testStatistics.getErrorTests());
    assertEquals(ZERO, testStatistics.getSuccessfulTests());
    assertEquals(ZERO, testStatistics.getErrorTestsPercent());
    assertEquals(ZERO, testStatistics.getSuccessfulTestsPercent());
    assertEquals(ONE_HUNDRED_PERCENT, testStatistics.getFailedTestsPercent());
  }


  public void test_addSuccessfulTests() {
    testStatistics.addSuccessfulTests(TEST_SUCC_COUNT);
    assertEquals(TEST_SUCC_COUNT, testStatistics.getSuccessfulTests());
    assertEquals(ZERO, testStatistics.getErrorTests());
    assertEquals(ZERO, testStatistics.getFailedTestsPercent());
    assertEquals(ZERO, testStatistics.getErrorTestsPercent());
    assertEquals(ZERO, testStatistics.getFailedTestsPercent());
    assertEquals(ONE_HUNDRED_PERCENT, testStatistics.getSuccessfulTestsPercent());
  }


  public void test_addSuccessfulErrorFailedTests() {
    testStatistics.addSuccessfulTests(TEST_SUCC_COUNT);
    testStatistics.addFailedTests(TEST_FAILED_COUNT);
    testStatistics.addErrorTests(TEST_ERROR_COUNT);
    assertEquals(TEST_SUCC_COUNT, testStatistics.getSuccessfulTests());
    assertEquals(TEST_FAILED_COUNT, testStatistics.getFailedTests());
    assertEquals(TEST_ERROR_COUNT, testStatistics.getErrorTests());
    assertEquals(TEST_SUCC_PERCENT, testStatistics.getSuccessfulTestsPercent());
    assertEquals(TEST_FALIED_PERCENT, testStatistics.getFailedTestsPercent());
    assertEquals(TEST_ERROR_PERCENT, testStatistics.getErrorTestsPercent());
    assertEquals(TEST_TOTAL_TESTS, testStatistics.getTotalTests());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestTestStatistics.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    testStatistics = new TestStatistics();
    testStatistics.addBuildCount(1);
  }
}
