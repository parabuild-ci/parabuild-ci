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
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.RuntimeUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;

import java.io.File;
import java.io.IOException;


/**
 * Tests SVNCheckoutCommand
 */
public class SSTestSVNCheckoutCommand extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestSVNCheckoutCommand.class);


  public SSTestSVNCheckoutCommand(final String s) {
    super(s);
  }


  /**
   * Tests that checkout can be issued if there are spaces in the
   * checkout path.
   *
   * @throws java.io.IOException
   * @throws org.parabuild.ci.common.CommandStoppedException
   *
   * @throws org.parabuild.ci.common.BuildException
   *
   */
  public void test_bug699() throws IOException, CommandStoppedException, BuildException, AgentFailureException {

    // windows only
    if (!RuntimeUtils.isWindows()) return;

    // run test
    final File testCheckoutDirWithSpaces = new File(TestHelper.getTestTempDir(), "dir name with spaces");
    IoUtils.deleteFileHard(testCheckoutDirWithSpaces);
    testCheckoutDirWithSpaces.mkdirs();
    final Agent agent = new MockAgent(AgentManager.getInstance().getNextLiveAgent(1), testCheckoutDirWithSpaces.getCanonicalPath());
    final SVNCheckoutCommand checkoutCommand = new SVNCheckoutCommand(agent, "svn", "svn://localhost:11111/test", new RepositoryPath("sourceline/alwaysvalid"), false);
    checkoutCommand.setUser("test_user");
    checkoutCommand.setPassword("test_password");

    // no exceptions should be thrown
    checkoutCommand.execute();
  }


  /**
   */
  public void test_bug1340Checkout() throws IOException, CommandStoppedException, BuildException, AgentFailureException {
    // run test
    final File testCheckoutDirWithSpaces = new File(TestHelper.getTestTempDir(), "test_checkout_dir");
    IoUtils.deleteFileHard(testCheckoutDirWithSpaces);
    testCheckoutDirWithSpaces.mkdirs();
    final Agent agent = new MockAgent(AgentManager.getInstance().getNextLiveAgent(1), testCheckoutDirWithSpaces.getCanonicalPath());
    final SVNCheckoutCommand checkoutCommand = new SVNCheckoutCommand(agent, "svn", "svn://localhost:11111/test", new RepositoryPath("never/existed/path"), false);
    checkoutCommand.setUser("test_user");
    checkoutCommand.setPassword("test_password");

    // no exceptions should be thrown
    try {
      checkoutCommand.execute();
      fail("Exception should have been thrown but it was not.");
    } catch (IOException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_canCheckoutTwiceThanUpdate() throws IOException, CommandStoppedException, BuildException, AgentFailureException {
    // run test
    final File testCheckoutDir = new File(TestHelper.getTestTempDir(), "test_checkout_dir");
    assertTrue(IoUtils.deleteFileHard(testCheckoutDir));
    testCheckoutDir.mkdirs();

    final File addDir = new File(testCheckoutDir, "test/sourceline/alwaysvalid");
    addDir.mkdirs();

    final Agent agent = new MockAgent(AgentManager.getInstance().getNextLiveAgent(1), testCheckoutDir.getCanonicalPath());
    final SVNCheckoutCommand checkoutCommand = new SVNCheckoutCommand(agent, "svn", "svn://localhost:11111", new RepositoryPath("/test/sourceline/alwaysvalid"), false);
    checkoutCommand.setUser("test_user");
    checkoutCommand.setPassword("test_password");

    // no exceptions should be thrown
    checkoutCommand.execute(); // run once
    checkoutCommand.execute(); // run twice

    final SVNUpdateCommand updateCommand = new SVNUpdateCommand(agent, "svn", "svn://localhost:11111", new RepositoryPath("test/sourceline/alwaysvalid"), "1", false);
    updateCommand.setUser("test_user");
    updateCommand.setPassword("test_password");
    updateCommand.execute();
    updateCommand.execute();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestSVNCheckoutCommand.class);
  }
}
