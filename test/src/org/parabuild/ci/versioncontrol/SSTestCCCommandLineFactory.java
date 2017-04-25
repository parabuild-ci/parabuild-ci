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
package org.parabuild.ci.versioncontrol;

import java.io.IOException;

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;


/**
 * Tests ClearCaseCommandLineFactory
 */
public class SSTestCCCommandLineFactory extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestCCCommandLineFactory.class);

  private Agent agent;
  private ClearCaseCommandLineFactory commandLineFactory;


  public SSTestCCCommandLineFactory(final String s) {
    super(s);
  }


  public void test_() throws IOException, CommandStoppedException, AgentFailureException {
    final String cleartoolExePathToUse = "cleartool";
    final String exeArguments = "some test exe arguments";
    final StringBuffer commandLine = commandLineFactory.makeCommandLine(cleartoolExePathToUse, exeArguments);
    if (agent.isWindows()) {
      assertEquals('\"' + cleartoolExePathToUse + '\"' + ' ' + exeArguments, commandLine.toString());
    } else {
      assertEquals("sh -c  \"" + cleartoolExePathToUse + ' ' + exeArguments + '\"', commandLine.toString());
    }
  }


  public void test_canHandleQuotedExe() throws IOException, CommandStoppedException, AgentFailureException {
    if (agent.isWindows()) return;
    final String cleartoolExePathToUse = "cleartool";
    final String quotedCleartoolExePathToUse = '\"' + cleartoolExePathToUse + '\"';
    final String exeArguments = "some test exe arguments";
    final StringBuffer commandLine = commandLineFactory.makeCommandLine(quotedCleartoolExePathToUse, exeArguments);
    assertEquals("sh -c  \"" + cleartoolExePathToUse + ' ' + exeArguments + '\"', commandLine.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.agent = AgentManager.getInstance().getNextLiveAgent(TestHelper.TEST_CLEARCASE_VALID_BUILD_ID);
    this.commandLineFactory = new ClearCaseCommandLineFactory(agent);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestCCCommandLineFactory.class);
  }
}
