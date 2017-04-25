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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.RuntimeUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;


/**
 * Tests SVNLogCommand
 */
public class SSTestSVNLogCommand extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestSVNLogCommand.class); // NOPMD

  public static final String TEST_EXE_PATH = "svn";
  public static final String TEST_URL = "svn://localhost:11111/test";
  public static final String TEST_DEPOT_PATH = "sourceline/alwaysvalid";
  public static final String TEST_CHANGE_LIST_NUMBER = "1";
  public static final String TEST_USER = "test_user";
  public static final String TEST_PASSWORD = "test_password";


  public SSTestSVNLogCommand(final String s) {
    super(s);
  }


  /**
   * Tests that log fails on deleted paths.
   *
   * @throws java.io.IOException
   * @throws org.parabuild.ci.common.CommandStoppedException
   *
   * @throws org.parabuild.ci.common.BuildException
   *
   */
  public void test_bug734() throws IOException, CommandStoppedException, BuildException, AgentFailureException {

    // windows only
    if (!RuntimeUtils.isWindows()) return;

    final Agent agent = AgentManager.getInstance().getNextLiveAgent(1);
    final SVNLogCommand logCommand = new SVNLogCommand(agent, TEST_EXE_PATH, TEST_URL, new RepositoryPath(TEST_DEPOT_PATH), "1", "HEAD", false, Integer.MAX_VALUE);
    logCommand.setUser(TEST_USER);
    logCommand.setPassword(TEST_PASSWORD);
    logCommand.execute();
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestSVNLogCommand.class);
  }
}
