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

import java.util.*;
import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import org.parabuild.ci.build.AbstractCustomLogTest;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepRunAttribute;
import org.parabuild.ci.common.IoUtils;
import com.gargoylesoftware.base.testing.OrderedTestSuite;

/**
 * Tests NUnitLogHandler
 *
 * @see org.parabuild.ci.build.log.NUnitLogHandler
 * @see org.parabuild.ci.build.AbstractCustomLogTest
 */
public class SSTestPHPUnitLogHandler extends AbstractCustomLogTest {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestPHPUnitLogHandler.class);

  private PHPUnitLogHandler logHandler = null;


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
    return LogConfig.LOG_TYPE_PHPUNIT_XML_DIR;
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 29;
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
    return ""; // REVIEWME: simeshev@parabuilci.org -> change to non-blank when PHPUnit handler is capable of indexing.
  }


  public void test_savesStatistics() {
    // get attr number before
    final Map before = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countBefore = before.size();
    assertAttrNotExist(before, StepRunAttribute.ATTR_PHPUNIT_FAILURES);
    assertAttrNotExist(before, StepRunAttribute.ATTR_PHPUNIT_SUCCESSES);
    assertAttrNotExist(before, StepRunAttribute.ATTR_PHPUNIT_TESTS);

    // handle
    this.logHandler.process();

    // check if PMD stat attr found an has appropriate value
    final Map after = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countAfter = after.size();
    assertTrue("Number of attributes should increase", countAfter > countBefore);
    assertAttrEquals(after, StepRunAttribute.ATTR_PHPUNIT_FAILURES, 1);
    assertAttrEquals(after, StepRunAttribute.ATTR_PHPUNIT_ERRORS, 1);
    assertAttrEquals(after, StepRunAttribute.ATTR_PHPUNIT_SUCCESSES, 2);
    assertAttrEquals(after, StepRunAttribute.ATTR_PHPUNIT_TESTS, 4);
  }


  public static void assertAttrEquals(final Map attrMap, final String attrName, final int value) {
    final StepRunAttribute found = (StepRunAttribute)attrMap.get(attrName);
    assertNotNull("Attribute " + attrName + " should be present", found);
    assertEquals("\" + attrName + \"", value, found.getValueAsInt());
  }


  public static void assertAttrNotExist(final Map attrMap, final String attrName) {
    assertNull("Attrbute " + attrName + " should not exist", attrMap.get(attrName));
  }


  protected void setUp() throws Exception {
    super.setUp();

    // create handler
    this.logHandler = new PHPUnitLogHandler(super.agent, super.buildRunConfig,
      super.remoteCheckoutDir + '/' + super.relativeBuildDir,
      super.logConfig, TEST_STEP_RUN_ID);

    // create test log files to simulate presence of the log

    // create dir
    final String remoteFileSeparator = super.agent.getSystemProperty("file.separator");
    final String testBuildLogDirName = super.remoteBuildDirName
      + remoteFileSeparator + logConfig.getPath().trim();
    super.agent.mkdirs(testBuildLogDirName);

    // list test PHPUnit log files
    final File[] list = new File(TestHelper.getTestDataDir(), "phpunit_xml_logs").listFiles(new FileFilter() {
      public boolean accept(final File pathname) {
        return (!pathname.isDirectory());
      }
    });


    // create files in the dir
    for (int i = 0; i < list.length; i++) {
      final String testLogFileToCreate = testBuildLogDirName + remoteFileSeparator + list[i].getName();
      agent.createFile(testLogFileToCreate, IoUtils.fileToString(list[i]));
    }
  }


  /**
   * Required by PHPUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestPHPUnitLogHandler.class,
      new String[]{
        "test_process"
      });
  }


  public SSTestPHPUnitLogHandler(final String s) {
    super(s);
  }
}
