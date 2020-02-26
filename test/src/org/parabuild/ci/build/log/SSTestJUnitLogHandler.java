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
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepRunAttribute;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;


/**
 * Tests JUnitLogHandler
 *
 * @see JUnitLogHandler
 * @see org.parabuild.ci.build.AbstractCustomLogTest
 */
public class SSTestJUnitLogHandler extends AbstractCustomLogTest {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestJUnitLogHandler.class);

  private JUnitLogHandler junitLogHandler = null;


  /**
   * @see AbstractCustomLogTest#processLogs
   */
  protected void processLogs() {
    this.junitLogHandler.process();
  }


  /**
   * @return log type handler being tested
   */
  protected byte logTypeBeingTested() {
    return LogConfig.LOG_TYPE_JNUIT_XML_DIR;
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 3;
  }


  /**
   * Return a string to be found in search after calling
   * processLogs.
   *
   * @return
   * @see AbstractCustomLogTest - parent class that will call
   *      this method after calling processLogs().
   * @see #processLogs
   */
  protected String stringToBeFoundBySearch() {
    return ""; // REVIEWME: simeshev@parabuilci.org -> change to non-blank when JUnit handler is capable of indexing.
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
    int buildRunID = cm.getBuildRunFromStepRun(TEST_STEP_RUN_ID).getBuildRunID();
    assertNull(cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.HAS_TESTS));
    assertNull(cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.NEW_BROKEN_TESTS));

    // handle
    this.junitLogHandler.process();

    // step run after
    final Map after = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countAfter = after.size();
    assertTrue("Number of attributes should increase", countAfter > countBefore);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_ERRORS, 0);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_FAILURES, 2);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_SUCCESSES, 13);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_TESTS, 15);

    // build run after
    cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.HAS_TESTS).getValue().equals(BuildRunAttribute.VALUE_YES);
    cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.NEW_BROKEN_TESTS).getValue().equals("2");
    cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.ATTR_JUNIT_ERRORS).getValue().equals("0");
    cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.ATTR_JUNIT_FAILURES).getValue().equals("2");
    cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.ATTR_JUNIT_SUCCESSES).getValue().equals("13");
    cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.ATTR_JUNIT_TESTS).getValue().equals("15");
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

    // create handler
    this.junitLogHandler = new JUnitLogHandler(super.agent, super.buildRunConfig,
            super.remoteCheckoutDir + '/' + super.relativeBuildDir,
            super.logConfig, TEST_STEP_RUN_ID);

    // create test log files to simulate presence of the log

    // create dir
    final String remoteFileSeparator = super.agent.getSystemProperty("file.separator");
    final String testBuildLogDirName = super.remoteBuildDirName
            + remoteFileSeparator + logConfig.getPath().trim();
//    if (log.isDebugEnabled()) log.debug("testBuildLogDirName = " + testBuildLogDirName);
    super.agent.mkdirs(testBuildLogDirName);

    // list test JUnit log files
    final File[] list = new File(TestHelper.getTestDataDir(), "junit_xml_logs").listFiles(new FileFilter() {
      public boolean accept(final File pathname) {
        return (!pathname.isDirectory());
      }
    });

    // create files in the dir
    for (int i = 0; i < list.length; i++) {
      final String testLogFileToCreate = testBuildLogDirName + remoteFileSeparator + list[i].getName();
//      if (log.isDebugEnabled()) log.debug("testLogFileToCreate = " + testLogFileToCreate);
      agent.createFile(testLogFileToCreate, IoUtils.fileToString(list[i]));
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestJUnitLogHandler.class,
            new String[]{
                    "test_savesStatistics"
            });
  }


  public SSTestJUnitLogHandler(final String s) {
    super(s);
  }
}
