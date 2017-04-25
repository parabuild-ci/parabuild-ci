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

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.build.AbstractCustomLogTest;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepRunAttribute;

import java.util.Map;


/**
 * Tests GoogleTestLogHandler.
 *
 * @see org.parabuild.ci.build.log.GoogleTestLogHandler
 * @see org.parabuild.ci.build.AbstractCustomLogTest
 */
public final class SSTestGoogleTestLogHandler extends AbstractCustomLogTest {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SSTestGoogleTestLogHandler.class); // NOPMD

  private GoogleTestLogHandler logHandler = null;


  /**
   * @see org.parabuild.ci.build.AbstractCustomLogTest#processLogs
   */
  protected void processLogs() {
    this.logHandler.process();
  }


  /**
   * @return log type handler being tested
   */
  protected byte logTypeBeingTested() {
    return LogConfig.LOG_TYPE_GOOGLETEST_XML_FILE;
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 33;
  }


  /**
   * Return a string to be found in search after calling
   * processLogs.
   *
   * @return
   * @see org.parabuild.ci.build.AbstractCustomLogTest - parent class that will call
   *      this method after calling processLogs().
   * @see #processLogs
   */
  protected String stringToBeFoundBySearch() {
    return ""; // REVIEWME: simeshev@parabuilci.org -> change to non-blank when GoogleTest handler is capable of indexing.
  }


  public void test_savesStatistics() {
    // get attr number for step run
    final Map stepRunAttributesBefore = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countBefore = stepRunAttributesBefore.size();
    assertAttrNotExist(stepRunAttributesBefore, StepRunAttribute.ATTR_JUNIT_ERRORS);
    assertAttrNotExist(stepRunAttributesBefore, StepRunAttribute.ATTR_JUNIT_FAILURES);
    assertAttrNotExist(stepRunAttributesBefore, StepRunAttribute.ATTR_JUNIT_SUCCESSES);
    assertAttrNotExist(stepRunAttributesBefore, StepRunAttribute.ATTR_JUNIT_TESTS);

    //  build run before
    final int buildRunID = cm.getBuildRunFromStepRun(TEST_STEP_RUN_ID).getBuildRunID();
    assertNull(cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.HAS_TESTS));
    assertNull(cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.NEW_BROKEN_TESTS));

    // handle
    this.logHandler.process();

    // step run after
    final Map after = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countAfter = after.size();
    assertTrue("Number of attributes should increase", countAfter > countBefore);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_ERRORS, 0);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_FAILURES, 1);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_SUCCESSES, 5);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_TESTS, 6);

    // build run after
    assertEquals(cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.HAS_TESTS).getValue(), BuildRunAttribute.VALUE_YES);
    assertEquals(cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.NEW_BROKEN_TESTS).getValue(), "1");
    assertEquals(cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.ATTR_JUNIT_ERRORS).getValue(), "0");
    assertEquals(cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.ATTR_JUNIT_FAILURES).getValue(), "1");
    assertEquals(cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.ATTR_JUNIT_SUCCESSES).getValue(), "5");
    assertEquals(cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.ATTR_JUNIT_TESTS).getValue(), "6");
  }


  public static void assertAttrEquals(final Map attrMap, final String attrName, final int value) {
    final StepRunAttribute found = (StepRunAttribute) attrMap.get(attrName);
    assertNotNull("Attribute " + attrName + " should be present", found);
    assertEquals("Attribute " + attrName, value, found.getValueAsInt());
  }


  public static void assertAttrNotExist(final Map attrMap, final String attrName) {
    assertNull("Attrbute " + attrName + " should not exist", attrMap.get(attrName));
  }


  protected void setUp() throws Exception {
    super.setUp();

    // Create handler
    this.logHandler = new GoogleTestLogHandler(super.agent, super.buildRunConfig,
            super.remoteCheckoutDir + '/' + super.relativeBuildDir,
            super.logConfig, TEST_STEP_RUN_ID);

    // Create test log file to simulate presence of the log
    final String remoteFileSeparator = super.agent.getSystemProperty("file.separator");
    final String testLogFileToCreate = super.remoteBuildDirName + remoteFileSeparator + logConfig.getPath().trim();
    agent.createFile(testLogFileToCreate, IoUtils.fileToString(TestHelper.getTestFile("googletest_report.xml")));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestGoogleTestLogHandler.class,
            new String[]{
                    "test_savesStatistics"
            });
  }


  public SSTestGoogleTestLogHandler(final String s) {
    super(s);
  }
}