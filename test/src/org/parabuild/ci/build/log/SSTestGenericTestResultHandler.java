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
import org.parabuild.ci.build.AbstractCustomLogTest;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepRunAttribute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Tests GenericTestResultHandler
 *
 * @see GenericTestResultHandler
 * @see AbstractCustomLogTest
 */
public class SSTestGenericTestResultHandler extends AbstractCustomLogTest {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SSTestGenericTestResultHandler.class); // NOPMD

  private GenericTestResultHandler handler = null;


  /**
   * @see AbstractCustomLogTest#processLogs
   */
  protected void processLogs() {
    this.handler.process();
  }


  /**
   * @return log type handler being tested
   */
  protected byte logTypeBeingTested() {
    return LogConfig.LOG_TYPE_GENERIC_TEST_RESULT;
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 30;
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
    return "";
  }


  public void testSavesStatistics() {
    // get attr number before
    final Map before = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countBefore = before.size();
    assertAttrNotExist(before, StepRunAttribute.ATTR_JUNIT_FAILURES);
    assertAttrNotExist(before, StepRunAttribute.ATTR_JUNIT_SUCCESSES);
    assertAttrNotExist(before, StepRunAttribute.ATTR_JUNIT_TESTS);
    assertAttrNotExist(before, StepRunAttribute.ATTR_JUNIT_SKIPS);

    // handle
    this.handler.process();

    // check if PMD stat attr found an has appropriate value
    final Map after = cm.getStepRunAttributesAsMap(TEST_STEP_RUN_ID);
    final int countAfter = after.size();
    assertTrue("Number of attributes should increase", countAfter > countBefore);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_FAILURES, 2);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_SUCCESSES, 49);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_TESTS, 52);
    assertAttrEquals(after, StepRunAttribute.ATTR_JUNIT_SKIPS, 52);
  }


  protected void setUp() throws Exception {
    super.setUp();

    // create handler
    this.handler = new GenericTestResultHandler(super.agent, super.buildRunConfig, super.remoteCheckoutDir + '/' + super.relativeBuildDir, super.logConfig, TEST_STEP_RUN_ID);

    // create test log file to simulate presence of the log
    final String remoteFileSeparator = super.agent.getSystemProperty("file.separator");
    final String testBuildFile = super.remoteBuildDirName + remoteFileSeparator + logConfig.getPath().trim();
    super.agent.mkdirs(new File(testBuildFile).getParent());
    final Properties properties = new Properties();
    properties.setProperty(GenericTestResultHandler.PROPERTY_TEST_ERRORS, "1");
    properties.setProperty(GenericTestResultHandler.PROPERTY_TEST_FAILURES, "2");
    properties.setProperty(GenericTestResultHandler.PROPERTY_TEST_SKIPS, "3");
    properties.setProperty(GenericTestResultHandler.PROPERTY_TEST_SUCCESSES, "4");
    OutputStream os = null;
    try {
      os = new FileOutputStream(testBuildFile);
      properties.store(os, "Tests");
    } finally {
      IoUtils.closeHard(os);
    }
  }


  /**
   * Required by CppUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestGenericTestResultHandler.class,
            new String[]{
                    "test_process"
            });
  }


  public SSTestGenericTestResultHandler(final String s) {
    super(s);
  }


  public static void assertAttrEquals(final Map attrMap, final String attrName, final int value) {
    final StepRunAttribute found = (StepRunAttribute) attrMap.get(attrName);
    assertNotNull("Attribute " + attrName + " should be present", found);
    assertEquals(attrName, value, found.getValueAsInt());
  }


  public static void assertAttrNotExist(final Map attrMap, final String attrName) {
    assertNull("Attrbute " + attrName + " should not exist", attrMap.get(attrName));
  }


  public String toString() {
    return "SSTestGenericTestResultHandler{" +
            "handler=" + handler +
            "} " + super.toString();
  }
}
