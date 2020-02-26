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

import org.parabuild.ci.ServersideTestCase;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;

/**
 * Created by simeshev on Sep 16, 2004 at 11:27:18 AM
 */
public abstract class AbstractSourceControlTest extends ServersideTestCase {

  protected Agent agent;
  protected ConfigurationManager cm = null;
  protected ErrorManager errorManager = null;
  protected AgentHost agentHost = null;


  public AbstractSourceControlTest(final String s) {
    super(s);
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public abstract void test_getChangesSince() throws Exception;


  public abstract void test_checkoutLatest() throws Exception;


  public abstract void test_checkOutLatestBranch() throws Exception;


  public abstract void test_syncToChangeListBranch() throws Exception;


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public abstract void test_getChangesSinceInBranch() throws Exception;


  public abstract void test_checkOutLatestCatchesConfigUpdates() throws Exception;


  public abstract void test_checkOutMultilineRepositoryPath() throws Exception;


  /**
   * Tests that checkoutLatest cannot process unxesising source line
   *
   * @throws Exception
   */
  public abstract void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception;


  /**
   * Tests that checkoutLatest cannot process unxesising source line
   *
   * @throws Exception
   */
  public abstract void test_checkOutLatestCantProcessInavalidUser() throws Exception;


  /**
   * @throws Exception
   */
  public abstract void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception;


  /**
   * @throws Exception
   */
  public abstract void test_syncToChangeList() throws Exception;


  /**
   * @throws Exception
   */
  public abstract void test_syncToChangeListPicksUpConfigChanges() throws Exception;


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public abstract void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception;


  /**
   * Tests can not access passworded source line with wrong password
   */
  public abstract void test_canNotAccessWithWrongPassword() throws Exception;


  public abstract void test_getUsersMap() throws Exception;


  public abstract int getTestBuildID();


  protected void afterSuperSetUp() {
    // may be overwirtten in children
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.cm = ConfigurationManager.getInstance();
    afterSuperSetUp();
    this.errorManager = ErrorManagerFactory.getErrorManager();
    this.errorManager.clearAllActiveErrors();
    this.agentHost = AgentManager.getInstance().getNextLiveAgentHost(getTestBuildID());
    this.agent = AgentManager.getInstance().createAgent(getTestBuildID(), agentHost);

    // set the value used by all version control system to maximum
    SystemConfigurationManagerFactory.getManager().saveSystemProperty(new SystemProperty(SystemProperty.INITIAL_NUMBER_OF_CHANGELISTS, SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS));
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public abstract void test_getChangesSinceHandlesFirstRun() throws Exception;


  /**
   * Tests label method.
   */
  public abstract void test_label() throws BuildException, CommandStoppedException, AgentFailureException;
}
