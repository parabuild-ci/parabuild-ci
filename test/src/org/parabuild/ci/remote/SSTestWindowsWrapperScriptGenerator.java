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
package org.parabuild.ci.remote;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;

/**
 * Tests WindowsWarapperScriptGenerator
 */
public class SSTestWindowsWrapperScriptGenerator extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestWindowsWrapperScriptGenerator.class);

  private WrapperScriptGenerator scriptGenerator = null;
  private Agent agent = null;

  public static final String TEST_COMMAND = "some test command string";


  public void test_generateScript() throws Exception {
    if (!agent.isWindows()) return; // dont run if not winwdows
    final String scriptFileName = generateTestScript();
    assertTrue(new File(scriptFileName).length() > 0);
    assertTrue(TEST_COMMAND, TestHelper.findStringInTextFile(scriptFileName, TEST_COMMAND));
  }


  public void test_addVariables() throws BuildException, IOException, AgentFailureException {
    // test names and values
    final String testName1 = "PARABUILD_ADDITIONAL_VAR_NAME1";
    final String testValue1 = "PARABUILD_ADDITIONAL_VAR_VALUE1";
    final String testName2 = "PARABUILD_ADDITIONAL_VAR_NAME2";
    final String testValue2 = "PARABUILD_ADDITIONAL_VAR_VALUE2";

    // add
    final Map testVars = new HashMap(3);
    testVars.put(testName1, testValue1);
    testVars.put(testName2, testValue2);
    scriptGenerator.addVariables(testVars);

    // generate
    final String scriptFileName = generateTestScript();

    // check if name and value is there
    assertNameValuePairIsSet(scriptFileName, testName1, testValue1);
    assertNameValuePairIsSet(scriptFileName, testName2, testValue2);
  }


  private void assertNameValuePairIsSet(final String scriptFileName, final String name, final String value) throws IOException {
    assertTrue(TestHelper.findStringInTextFile(scriptFileName, "set " + name + '=' + value));
  }


  /**
   * Helper.
   *
   * @return script file name.
   * @throws java.io.IOException
   */
  private String generateTestScript() throws IOException, AgentFailureException {
    return scriptGenerator.generateScript(TEST_COMMAND);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestWindowsWrapperScriptGenerator.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    agent = AgentManager.getInstance().getNextLiveAgent(TestHelper.TEST_CVS_VALID_BUILD_ID);
    scriptGenerator = new WindowsWrapperScriptGenerator(agent);
  }


  public SSTestWindowsWrapperScriptGenerator(final String s) {
    super(s);
  }
}
