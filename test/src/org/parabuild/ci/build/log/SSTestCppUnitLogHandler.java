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
 * Tests CppUnitLogHandler
 *
 * @see org.parabuild.ci.build.log.CppUnitLogHandler
 * @see org.parabuild.ci.build.AbstractCustomLogTest
 */
public class SSTestCppUnitLogHandler extends AbstractCustomLogTest {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestCppUnitLogHandler.class);

  private CppUnitLogHandler cppunitLogHandler = null;


  /**
   * @see org.parabuild.ci.build.AbstractCustomLogTest#processLogs
   */
  protected void processLogs() {
    this.cppunitLogHandler.process();
  }


  /**
   * @return log type handler being tested
   */
  protected byte logTypeBeingTested() {
    return LogConfig.LOG_TYPE_CPPUNIT_XML_DIR;
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 27;
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
    return ""; // REVIEWME: simeshev@parabuilci.org -> change to non-blank when CppUnit handler is capable of indexing.
  }


  public void test_savesStatistics() {
    // get attr number before
    final Map before = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countBefore = before.size();
    assertAttrNotExist(before, StepRunAttribute.ATTR_CPPUNIT_FAILURES);
    assertAttrNotExist(before, StepRunAttribute.ATTR_CPPUNIT_SUCCESSES);
    assertAttrNotExist(before, StepRunAttribute.ATTR_CPPUNIT_TESTS);

    // handle
    this.cppunitLogHandler.process();

    // check if PMD stat attr found an has appropriate value
    final Map after = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countAfter = after.size();
    assertTrue("Number of attributes should increase", countAfter > countBefore);
    assertAttrEquals(after, StepRunAttribute.ATTR_CPPUNIT_FAILURES, 3);
    assertAttrEquals(after, StepRunAttribute.ATTR_CPPUNIT_SUCCESSES, 49);
    assertAttrEquals(after, StepRunAttribute.ATTR_CPPUNIT_TESTS, 52);
  }


  public static void assertAttrEquals(final Map attrMap, final String attrName, final int value) {
    final StepRunAttribute found = (StepRunAttribute)attrMap.get(attrName);
    assertNotNull("Attribute " + attrName + " should be present", found);
    assertEquals(attrName, value, found.getValueAsInt());
  }


  public static void assertAttrNotExist(final Map attrMap, final String attrName) {
    assertNull("Attrbute " + attrName + " should not exist", attrMap.get(attrName));
  }


  protected void setUp() throws Exception {
    super.setUp();

    // create handler
    this.cppunitLogHandler = new CppUnitLogHandler(super.agent, super.buildRunConfig,
      super.remoteCheckoutDir + "/" + super.relativeBuildDir,
      super.logConfig, TEST_STEP_RUN_ID);

    // create test log files to simulate presence of the log

    // create dir
    final String remoteFileSeparator = super.agent.getSystemProperty("file.separator");
    final String testBuildLogDirName = super.remoteBuildDirName
      + remoteFileSeparator + logConfig.getPath().trim();
//    if (log.isDebugEnabled()) log.debug("testBuildLogDirName = " + testBuildLogDirName);
    super.agent.mkdirs(testBuildLogDirName);

    // list test CppUnit log files
    final File[] list = new File(TestHelper.getTestDataDir(), "cppunit_xml_logs").listFiles(new FileFilter() {
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
   * Required by CppUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestCppUnitLogHandler.class,
      new String[]{
        "test_process"
      });
  }


  public SSTestCppUnitLogHandler(final String s) {
    super(s);
  }
}
