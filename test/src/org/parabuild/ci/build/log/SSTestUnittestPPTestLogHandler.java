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

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.build.AbstractCustomLogTest;

import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepRunAttribute;

/**
 * Tests NUnitLogHandler
 *
 * @see NUnitLogHandler
 * @see org.parabuild.ci.build.AbstractCustomLogTest
 */
public class SSTestUnittestPPTestLogHandler extends AbstractCustomLogTest {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestUnittestPPTestLogHandler.class);

  private UnitTestPpLogHandler logHandler = null;


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
    return LogConfig.LOG_TYPE_UNITTESTPP_XML_DIR;
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 32;
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
    return ""; // REVIEWME: simeshev@parabuilci.org -> change to non-blank when Boost Test handler is capable of indexing.
  }


  public void test_savesStatistics() {
    // get attr number before
    final Map before = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countBefore = before.size();
    assertAttrNotExist(before, StepRunAttribute.ATTR_UNITTESTPP_ERRORS);
    assertAttrNotExist(before, StepRunAttribute.ATTR_UNITTESTPP_SUCCESSES);
    assertAttrNotExist(before, StepRunAttribute.ATTR_UNITTESTPP_TESTS);
    assertAttrNotExist(before, StepRunAttribute.ATTR_UNITTESTPP_FAILURES);

    // handle
    this.logHandler.process();

    // check if PMD stat attr found an has appropriate value
    final Map after = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countAfter = after.size();
    assertTrue("Number of attributes should increase", countAfter > countBefore);
    assertAttrEquals(after, StepRunAttribute.ATTR_UNITTESTPP_ERRORS, 0);
    assertAttrEquals(after, StepRunAttribute.ATTR_UNITTESTPP_TESTS, 6);
    assertAttrEquals(after, StepRunAttribute.ATTR_UNITTESTPP_SUCCESSES, 4);
    assertAttrEquals(after, StepRunAttribute.ATTR_UNITTESTPP_FAILURES, 2);
  }


  public static void assertAttrEquals(final Map attrMap, final String attrName, final int value) {
    final StepRunAttribute found = (StepRunAttribute) attrMap.get(attrName);
    assertNotNull("Attribute " + attrName + " should be present", found);
    assertEquals("\"" + attrName + "\"", value, found.getValueAsInt());
  }


  public static void assertAttrNotExist(final Map attrMap, final String attrName) {
    assertNull("Attrbute " + attrName + " should not exist", attrMap.get(attrName));
  }


  protected void setUp() throws Exception {
    super.setUp();

    // create handler
    this.logHandler = new UnitTestPpLogHandler(super.agent, super.buildRunConfig,
            super.remoteCheckoutDir + '/' + super.relativeBuildDir,
            super.logConfig, TEST_STEP_RUN_ID);

    // create test log files to simulate presence of the log

    // create dir
    final String remoteFileSeparator = super.agent.getSystemProperty("file.separator");
    final String testBuildLogDirName = super.remoteBuildDirName
            + remoteFileSeparator + logConfig.getPath().trim();
    super.agent.mkdirs(testBuildLogDirName);

    // list test Boost test log files
    final File[] list = new File(TestHelper.getTestDataDir(), "unittestpp_xml_logs").listFiles(new FileFilter() {
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
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestUnittestPPTestLogHandler.class,
            new String[]{
                    "test_process"
            });
  }


  public SSTestUnittestPPTestLogHandler(final String s) {
    super(s);
  }
}