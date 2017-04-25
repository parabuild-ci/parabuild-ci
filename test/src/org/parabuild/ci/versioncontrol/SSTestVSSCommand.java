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

import java.io.File;
import java.io.IOException;

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.remote.internal.LocalAgentEnvironment;

/**
 * Tests directory helper
 */
public class SSTestVSSCommand extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestVSSCommand.class);
  private static final int TEST_BUILD_ID = 1;

  private VSSCommand command = null;


  public SSTestVSSCommand(final String s) {
    super(s);
  }


  public void test_execute() throws IOException, CommandStoppedException, AgentFailureException {
    final int code = command.execute();
    if (log.isDebugEnabled()) log.debug("code = " + code);
    if (log.isDebugEnabled())
      log.debug("IoUtils.fileToString(command.getStderrFile()) = " + IoUtils.fileToString(command.getStderrFile()));
    if (log.isDebugEnabled())
      log.debug("IoUtils.fileToString(command.getStdoutFile()) = " + IoUtils.fileToString(command.getStdoutFile()));

    // NOTE: vimeshev - stderr output
    //    1. No VSS database (srcsafe.ini) found. == db path is misconfigured
    //
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    final TestSuite testSuite = new TestSuite();
    final LocalAgentEnvironment environment = new LocalAgentEnvironment();
    if (environment.isWindows()) testSuite.addTestSuite(SSTestVSSCommand.class);
    return testSuite;
  }


  protected void setUp() throws Exception {
    super.setUp();

    // preExecute test VSS database // REVIEWME: consider test stup once.
    final File source = new File(TestHelper.getTestDataDir(), "vss");
    final File testDabase = new File(TestHelper.getTestTempDir(), SSTestVSSCommand.class.getName() + '/' + "vss");
    testDabase.mkdirs();
    IoUtils.emptyDir(testDabase);
    IoUtils.copyDirectory(source, testDabase);

    // general setup
    final Agent agent = AgentManager.getInstance().getNextLiveAgent(TEST_BUILD_ID);
    command = new VSSCommand(agent, "SS.EXE", testDabase.getCanonicalPath(), "test", "test_password", "Status");
  }
}
