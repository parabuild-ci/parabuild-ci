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

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.RuntimeUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;

import java.io.File;
import java.io.IOException;


/**
 * Tests SVNUpdateCommand
 */
public class SSTestSVNUpdateCommand extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestSVNUpdateCommand.class);

  public static final String TEST_EXE_PATH = "svn";
  public static final String TEST_URL = "svn://localhost:11111/test";
  public static final String TEST_DEPOT_PATH = "sourceline/alwaysvalid";
  public static final String TEST_CHANGE_LIST_NUMBER = "1";
  public static final String TEST_USER = "test_user";
  public static final String TEST_PASSWORD = "test_password";


  public SSTestSVNUpdateCommand(final String s) {
    super(s);
  }


  /**
   * Tests that update can be issued if there are spaces in the
   * checkout path.
   *
   * @throws IOException
   * @throws org.parabuild.ci.util.CommandStoppedException
   * @throws BuildException
   */
  public void test_bug699() throws IOException, CommandStoppedException, BuildException, AgentFailureException {

    // windows only
    if (!RuntimeUtils.isWindows()) return;

    // run test
    final File testCheckoutDirWithSpaces = new File(TestHelper.getTestTempDir(), "dir name with spaces");
    IoUtils.deleteFileHard(testCheckoutDirWithSpaces);
    testCheckoutDirWithSpaces.mkdirs();
    final Agent agent = new MockAgent(AgentManager.getInstance().getNextLiveAgent(1), testCheckoutDirWithSpaces.getCanonicalPath());

    // checkout - tested by SSTestSVNCheckoutCommand
    final SVNCheckoutCommand checkoutCommand = new SVNCheckoutCommand(agent, TEST_EXE_PATH, TEST_URL, new RepositoryPath(TEST_DEPOT_PATH), false);
    checkoutCommand.setUser(TEST_USER);
    checkoutCommand.setPassword(TEST_PASSWORD);
    checkoutCommand.execute();

    // update
    final SVNUpdateCommand updateCommand = new SVNUpdateCommand(agent, TEST_EXE_PATH, TEST_URL, new RepositoryPath(TEST_DEPOT_PATH), TEST_CHANGE_LIST_NUMBER, false);
    updateCommand.setUser(TEST_USER);
    updateCommand.setPassword(TEST_PASSWORD);

    // no exceptions should be thrown
    updateCommand.execute();
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestSVNUpdateCommand.class);
  }
}
