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

import java.io.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;

/**
 * Tests PMDViolationsChartGenerator
 */
public class SSTestPMDViolationsChartGenerator extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestPMDViolationsChartGenerator.class);

  private PMDViolationsChartGenerator chartGenerator = null;
  private StatisticsManagerImpl statMan = null;
  private ErrorManager errorManager;


  public SSTestPMDViolationsChartGenerator(final String s) {
    super(s);
  }


  public void test_createChart() throws IOException {
    OutputStream os = null;
    try {
      final SortedMap monthToDateDistribution = statMan.getRecentPMDViolations();
      final File f = IoUtils.createTempFile("ddd", ".png", TestHelper.getTestTempDir());
      os = new FileOutputStream(f);
      chartGenerator.createChart(monthToDateDistribution, os);
      IoUtils.closeHard(os);
      assertTrue(f.length() > 0);
    } finally {
      IoUtils.closeHard(os);
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestPMDViolationsChartGenerator.class, new String[]{
    });
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    assertEquals(0, errorManager.errorCount());
  }


  protected void setUp() throws Exception {
    super.setUp();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    statMan = new StatisticsManagerImpl(TestHelper.TEST_CVS_VALID_BUILD_ID);
    chartGenerator = new PMDViolationsChartGenerator();
  }
}
