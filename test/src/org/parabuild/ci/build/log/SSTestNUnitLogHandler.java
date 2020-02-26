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
import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.build.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.util.*;
import com.gargoylesoftware.base.testing.*;

/**
 * Tests NUnitLogHandler
 *
 * @see org.parabuild.ci.build.log.NUnitLogHandler
 * @see org.parabuild.ci.build.AbstractCustomLogTest
 */
public class SSTestNUnitLogHandler extends AbstractCustomLogTest {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestNUnitLogHandler.class);

  private NUnitLogHandler nunitLogHandler = null;


  /**
   * @see org.parabuild.ci.build.AbstractCustomLogTest#processLogs
   */
  protected void processLogs() {
    this.nunitLogHandler.process();
  }


  /**
   * @return log type handler being tested
   */
  protected byte logTypeBeingTested() {
    return LogConfig.LOG_TYPE_NUNIT_XML_DIR;
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 25;
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
    return ""; // REVIEWME: simeshev@parabuilci.org -> change to non-blank when NUnit handler is capable of indexing.
  }


  public void test_savesStatistics() {
    // get attr number before
    final Map before = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countBefore = before.size();
    assertAttrNotExist(before, StepRunAttribute.ATTR_NUNIT_NOTRUN);
    assertAttrNotExist(before, StepRunAttribute.ATTR_NUNIT_FAILURES);
    assertAttrNotExist(before, StepRunAttribute.ATTR_NUNIT_SUCCESSES);
    assertAttrNotExist(before, StepRunAttribute.ATTR_NUNIT_TESTS);

    // handle
    this.nunitLogHandler.process();

    // check if PMD stat attr found an has appropriate value
    final Map after = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countAfter = after.size();
    assertTrue("Number of attributes should increase", countAfter > countBefore);
    assertAttrEquals(after, StepRunAttribute.ATTR_NUNIT_NOTRUN, 3);
    assertAttrEquals(after, StepRunAttribute.ATTR_NUNIT_FAILURES, 1);
    assertAttrEquals(after, StepRunAttribute.ATTR_NUNIT_SUCCESSES, 224);
    assertAttrEquals(after, StepRunAttribute.ATTR_NUNIT_TESTS, 228);
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
    this.nunitLogHandler = new NUnitLogHandler(super.agent, super.buildRunConfig,
      super.remoteCheckoutDir + '/' + super.relativeBuildDir,
      super.logConfig, TEST_STEP_RUN_ID);

    // create test log files to simulate presence of the log

    // create dir
    final String remoteFileSeparator = super.agent.getSystemProperty("file.separator");
    final String testBuildLogDirName = super.remoteBuildDirName
      + remoteFileSeparator + logConfig.getPath().trim();
//    if (log.isDebugEnabled()) log.debug("testBuildLogDirName = " + testBuildLogDirName);
    super.agent.mkdirs(testBuildLogDirName);

    // list test NUnit log files
    final File[] list = new File(TestHelper.getTestDataDir(), "nunit_xml_logs").listFiles(new FileFilter() {
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
   * Required by NUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestNUnitLogHandler.class,
      new String[]{
        "test_process"
      });
  }


  public SSTestNUnitLogHandler(final String s) {
    super(s);
  }
}
