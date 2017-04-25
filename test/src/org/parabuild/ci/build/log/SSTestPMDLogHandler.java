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
package org.parabuild.ci.build.log;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.apache.commons.logging.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.build.AbstractCustomLogTest;
import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;


/**
 * Tests PMDLogHandler
 *
 * @see org.parabuild.ci.build.log.PMDLogHandler
 * @see org.parabuild.ci.build.AbstractCustomLogTest
 */
public class SSTestPMDLogHandler extends AbstractCustomLogTest {

  private static final Log log = LogFactory.getLog(SSTestPMDLogHandler.class);

  private PMDLogHandler pmdLogHandler = null;


  /**
   * @return log type handler being tested
   */
  protected byte logTypeBeingTested() {
    return LogConfig.LOG_TYPE_PMD_XML_FILE;
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 4;
  }


  /**
   * @see AbstractCustomLogTest#processLogs
   */
  protected void processLogs() {
    this.pmdLogHandler.process();
  }


  /**
   * Return a string to be found in search after calling
   * processLogs.
   *
   * @return
   *
   * @see AbstractCustomLogTest - parent class that will call
   *      this method after calling processLogs().
   * @see #processLogs
   */
  protected String stringToBeFoundBySearch() {
    return ""; // REVIEWME: simeshev@parabuilci.org -> change to non-blank when PMD handler is capable of indexing.
  }


  public void test_savesStatistics() {
    // get attr number before
    final Map before = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countBefore = before.size();
    assertNull("Attrbute " + StepRunAttribute.ATTR_PMD_PROBLEMS + " should not exist", before.get(StepRunAttribute.ATTR_PMD_PROBLEMS));

    // handle
    this.pmdLogHandler.process();

    // check if PMD stat attr found an has appropriate value
    final Map after = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countAfter = after.size();
    final StepRunAttribute found = (StepRunAttribute)after.get(StepRunAttribute.ATTR_PMD_PROBLEMS);
    assertTrue("Number of attributes should increase", countAfter > countBefore);
    assertNotNull("Attribute " + StepRunAttribute.ATTR_PMD_PROBLEMS + " should be present", found);
    assertEquals("Saved PMD problems count", 13, found.getValueAsInt());
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();

    // create handler
    this.pmdLogHandler = new PMDLogHandler(super.agent, super.buildRunConfig,
      super.remoteCheckoutDir + '/' + super.relativeBuildDir,
      super.logConfig, TEST_STEP_RUN_ID);

    // create log file to simuate result
    final String testPMDLogFileName = super.remoteBuildDirName
      + super.agent.getSystemProperty("file.separator")
      + super.logConfig.getPath().trim();
    agent.createFile(testPMDLogFileName, IoUtils.fileToString(new File(TestHelper.getTestDataDir(), "test_pmd_report.xml")));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestPMDLogHandler.class,
      new String[]{
        "test_process"
      });
  }


  public SSTestPMDLogHandler(final String s) {
    super(s);
  }
}
