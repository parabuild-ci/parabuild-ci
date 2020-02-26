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
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;


/**
 * Tests ClearCaseRmtagCommand
 */
public class SSTestClearCaseRmtagCommand extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestClearCaseRmtagCommand.class);

  /**
   * @noinspection FieldCanBeLocal
   */
  private ConfigurationManager cm = null;
  private String exePath = null;
  private static final String EMPTY_IGNORE_LINES = "";


  public void test_executeDoesntFailIfTagDoesntExist() throws IOException, CommandStoppedException, AgentFailureException {
    final Agent agent = AgentManager.getInstance().getNextLiveAgent(TestHelper.TEST_CLEARCASE_VALID_BUILD_ID);
    final String testTag = "new_vew_tag" + System.currentTimeMillis();
    ClearCaseRmtagCommand rmTagCommand = new ClearCaseRmtagCommand(agent, exePath, testTag, EMPTY_IGNORE_LINES);
    rmTagCommand.execute();
//    log.debug("IoUtils.fileToString(stdoutFile): " + IoUtils.fileToString(rmTagCommand.getStdoutFile()));
//    log.debug("IoUtils.fileToString(stderrFile): " + IoUtils.fileToString(rmTagCommand.getStderrFile()));
    rmTagCommand.cleanup();

    rmTagCommand = new ClearCaseRmtagCommand(agent, exePath, testTag, EMPTY_IGNORE_LINES);
    rmTagCommand.execute();
    rmTagCommand.cleanup();
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    exePath = cm.getSourceControlSetting(TestHelper.TEST_CLEARCASE_VALID_BUILD_ID, VCSAttribute.CLEARCASE_PATH_TO_EXE).getPropertyValue();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestClearCaseRmtagCommand.class);
  }


  public SSTestClearCaseRmtagCommand(final String s) {
    super(s);
  }
}
