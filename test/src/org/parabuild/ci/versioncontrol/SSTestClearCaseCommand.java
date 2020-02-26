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
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;


/**
 * Tests
 */
public class SSTestClearCaseCommand extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestClearCaseCommand.class);

  public static final String clearToolExe = "D:\\Rational\\ClearCase\\bin\\cleartool.exe";
  private static final String TEST_TAG = "new_vew_tag";
  private static final String EMPTY_IGNORE_LINES = "";


  public SSTestClearCaseCommand(final String s) {
    super(s);
  }


  public void test_execute() throws IOException, CommandStoppedException, AgentFailureException {
    final Agent agent = AgentManager.getInstance().getNextLiveAgent(TestHelper.TEST_CLEARCASE_VALID_BUILD_ID);

    // delete tag, just in case
    final ClearCaseRmtagCommand rmTagCommand = new ClearCaseRmtagCommand(agent, clearToolExe, TEST_TAG, EMPTY_IGNORE_LINES);
    rmTagCommand.execute();
    log.debug("IoUtils.fileToString(stdoutFile): " + IoUtils.fileToString(rmTagCommand.getStdoutFile()));
    log.debug("IoUtils.fileToString(stderrFile): " + IoUtils.fileToString(rmTagCommand.getStderrFile()));

    final boolean deleted = agent.deleteCheckoutDir();
    if (log.isDebugEnabled()) log.debug("deleted: " + deleted);
    final ClearCaseMkviewCommand mkviewCommand = new ClearCaseMkviewCommand(
            agent,
            clearToolExe,
            SourceControlSetting.CLEARCASE_TEXT_MODE_UNIX,
            SourceControlSetting.CLEARCASE_STORAGE_CODE_AUTOMATIC,
            "",
            TEST_TAG, EMPTY_IGNORE_LINES);
    mkviewCommand.execute();
    log.debug("IoUtils.fileToString(stdoutFile): " + IoUtils.fileToString(mkviewCommand.getStdoutFile()));
    log.debug("IoUtils.fileToString(stderrFile): " + IoUtils.fileToString(mkviewCommand.getStderrFile()));
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestClearCaseCommand.class);
  }
}
