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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.build.AbstractCustomLogTest;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepRunAttribute;

/**
 * Tests CheckstyleLogHandler
 *
 * @see org.parabuild.ci.build.log.CheckstyleLogHandler
 * @see org.parabuild.ci.build.AbstractCustomLogTest
 */
public class SSTestCheckstyleLogHandler extends AbstractCustomLogTest {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestCheckstyleLogHandler.class);

  private CheckstyleLogHandler checkstyleLogHandler = null;


  /**
   * @return log type handler being tested
   */
  protected byte logTypeBeingTested() {
    return LogConfig.LOG_TYPE_CHECKSTYLE_XML_FILE;
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 28;
  }


  /**
   * @see org.parabuild.ci.build.AbstractCustomLogTest#processLogs
   */
  protected void processLogs() {
    this.checkstyleLogHandler.process();
  }


  /**
   * Return a string to be found in search after calling
   * processLogs.
   *
   * @return
   *
   * @see org.parabuild.ci.build.AbstractCustomLogTest - parent class that will call
   *      this method after calling processLogs().
   * @see #processLogs
   */
  protected String stringToBeFoundBySearch() {
    return ""; // REVIEWME: simeshev@parabuilci.org -> change to non-blank when Checkstyle handler is capable of indexing.
  }


  public void test_savesStatistics() {
    // get attr number before
    final Map before = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countBefore = before.size();
    final String attrName = StepRunAttribute.ATTR_CHECKSTYLE_ERRORS;
    assertNull("Attrbute " + attrName + " should not exist", before.get(attrName));

    // handle
    this.checkstyleLogHandler.process();

    // check if Checkstyle stat attr found an has appropriate value
    final Map after = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countAfter = after.size();
    final StepRunAttribute errors = (StepRunAttribute)after.get(attrName);
    assertTrue("Number of attributes should increase", countAfter > countBefore);
    assertPresent(attrName, errors);
    assertEquals(34187, errors);

    assertPresent(StepRunAttribute.ATTR_CHECKSTYLE_FILES, (StepRunAttribute)after.get(StepRunAttribute.ATTR_CHECKSTYLE_FILES));
    assertEquals(2338, (StepRunAttribute)after.get(StepRunAttribute.ATTR_CHECKSTYLE_FILES));
  }


  private void assertEquals(final int i, final StepRunAttribute files) {assertEquals("Saved Checkstyle problems count", i, files.getValueAsInt());}


  private static void assertPresent(final String attrName, final StepRunAttribute attr) {
    assertNotNull("Attribute " + attrName + " should be present", attr);
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();

    // create handler
    this.checkstyleLogHandler = new CheckstyleLogHandler(super.agent, super.buildRunConfig,
      super.remoteCheckoutDir + '/' + super.relativeBuildDir,
      super.logConfig, TEST_STEP_RUN_ID);

    // create log file to simuate result
    final String testCheckstyleLogFileName = super.remoteBuildDirName
      + super.agent.getSystemProperty("file.separator")
      + super.logConfig.getPath().trim();
    agent.createFile(testCheckstyleLogFileName, IoUtils.fileToString(new File(TestHelper.getTestDataDir(), "test_checkstyle_errors.xml")));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestCheckstyleLogHandler.class,
      new String[]{
        "test_process"
      });
  }


  public SSTestCheckstyleLogHandler(final String s) {
    super(s);
  }
}
