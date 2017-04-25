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
package org.parabuild.ci.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;


public class SSTestUnixBuildScriptRunner extends ServersideTestCase {

  private UnixBuildScriptRunner scriptRunner = null;

  private File merged = null;
  private File script = null;
  private BuildTimeoutCallback timeoutCallback;
  private Agent agent = null;


  public void test_executeBuildScript() throws Exception {

    if (isUnix()) {
      final FileWriter fw = new FileWriter(script);
      fw.write("# Test script file \n");
      fw.write("echo test successful\n");
      IoUtils.closeHard(fw);
      scriptRunner.executeBuildScript(script.getAbsolutePath());
      assertTrue("Test line was not found in script runner output", findTestStringInTheResult("test successful"));
    }
  }


  public void test_executeBuildScriptTimesOut() throws Exception {

    if (isUnix()) {
      final FileWriter fw = new FileWriter(script);
      fw.write("# Test script file \n");
      fw.write("sleep 100 \n");
      fw.write("echo test successful\n");
      IoUtils.closeHard(fw);
      scriptRunner.setTimeoutSecs(5);
      scriptRunner.executeBuildScript(script.getAbsolutePath());
      assertTrue(scriptRunner.isTimedOut());
    }
  }


  public void test_executeBuildRetunsZeroResult() throws Exception {
    if (isUnix()) {
      final FileWriter fw = new FileWriter(script);
      fw.write("# Test script file \n");
      fw.write("echo test successful\n");
      IoUtils.closeHard(fw);
      assertEquals(0, scriptRunner.executeBuildScript(script.getAbsolutePath()));
    }
  }


  public void test_executeBuildRetunsNonZeroResult() throws Exception {
    if (isUnix()) {
      final FileWriter fw = new FileWriter(script);
      fw.write("# Test script file \n");
      fw.write("exit 100\n");
      IoUtils.closeHard(fw);
      assertEquals(100, scriptRunner.executeBuildScript(script.getAbsolutePath()));
    }
  }


  public void test_executeMakeBuildRetunsNonZeroResult() throws Exception {
    if (isUnix()) {
      final FileWriter fw = new FileWriter(script);
      fw.write("# Test script file \n");
      fw.write("make\n");
      IoUtils.closeHard(fw);
      assertEquals(2, scriptRunner.executeBuildScript(script.getAbsolutePath()));
    }
  }


  public void test_executeMakeBuildRetunsZeroResult() throws Exception {
    if (isUnix()) {
      final FileWriter fw = new FileWriter(script);
      fw.write("# Test script file \n");
      fw.write("make --help\n");
      IoUtils.closeHard(fw);
      assertEquals(0, scriptRunner.executeBuildScript(script.getAbsolutePath()));
    }
  }


  /**
   * Helper
   */
  private boolean isUnix() throws IOException, AgentFailureException {
    return agent.isUnix();
  }


  /**
   * Helper
   */
  private boolean findTestStringInTheResult(final String toFind) throws IOException {
    boolean found = false;
    final BufferedReader br = new BufferedReader(new FileReader(merged));
    String line = br.readLine();
    while (line != null) {
      if (line.equals(toFind)) {
        found = true; // success
        break;
      }
      line = br.readLine();
    }
    IoUtils.closeHard(br);
    return found;
  }


  protected void setUp() throws Exception {
    //
    agent = AgentManager.getInstance().getNextLiveAgent(TestHelper.TEST_CVS_VALID_BUILD_ID);

    // files
    merged = makeTempTestFile("-merged");
    script = makeTempTestFile("-script.sh");

    // timeout matches
    final List timeoutMatches = new ArrayList();
    timeoutMatches.add(script.getAbsolutePath());

    // runner
    scriptRunner = new UnixBuildScriptRunner(agent);
    scriptRunner.setMergedFile(merged);
    scriptRunner.addTimeoutMatches(timeoutMatches);

    // callback
    timeoutCallback = new BuildTimeoutCallback();
    timeoutCallback.setScriptRunner(scriptRunner);
    timeoutCallback.setNotificationEnabled(false);
    scriptRunner.setTimeoutCallback(timeoutCallback);
  }


  private File makeTempTestFile(final String suffix) {
    final File result = new File(TestHelper.getTestTempDir(), this.getClass().getName() + suffix);
    if (result.exists()) result.delete();
    return result;
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestUnixBuildScriptRunner.class);
  }


  public SSTestUnixBuildScriptRunner(final String s) {
    super(s);
  }
}
