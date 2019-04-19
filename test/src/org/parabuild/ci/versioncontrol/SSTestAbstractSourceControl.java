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
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestSuite;
import junitx.util.PrivateAccessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.process.RemoteCommand;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;

/**
 *
 */
public class SSTestAbstractSourceControl extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestAbstractSourceControl.class);

  private MockAbstractSourceControl sourceControl = null;
  private MockRemoteCommand mockRemoteCommand = null;


  public SSTestAbstractSourceControl(final String s) {
    super(s);
  }


  public void test_deleteCommandWorkFiles() throws Exception {
    // preExecute
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    SystemProperty sp = systemCM.getSystemProperty(SystemProperty.KEEP_SCM_LOGS);
    if (sp == null) {
      sp = new SystemProperty();
      sp.setPropertyName(SystemProperty.KEEP_SCM_LOGS);
      sp.setPropertyValue("true");
    } else if (!sp.getPropertyValue().equals("true")) {
      sp.setPropertyValue("true");
    }
    systemCM.saveSystemProperty(sp);

    // invoke
    PrivateAccessor.invoke(sourceControl, "cleanup",
            new Class[]{RemoteCommand.class},
            new Object[]{mockRemoteCommand});
    assertTrue(!mockRemoteCommand.isCleanupCalled());
  }


  public void test_deleteCommandWorkFiles_Bug257() throws Exception {
    // invoke
    PrivateAccessor.invoke(sourceControl, "cleanup",
            new Class[]{RemoteCommand.class},
            new Object[]{mockRemoteCommand});
    assertEquals(true, mockRemoteCommand.isCleanupCalled());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestAbstractSourceControl.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    final AgentHost agentHost = AgentManager.getInstance().getNextLiveAgentHost(TestHelper.TEST_CVS_VALID_BUILD_ID);
    final Agent agent = AgentManager.getInstance().createAgent(TestHelper.TEST_CVS_VALID_BUILD_ID, agentHost);
    sourceControl = new MockAbstractSourceControl();
    sourceControl.setAgentHost(agentHost);
    mockRemoteCommand = new MockRemoteCommand(agent);
  }
}

class MockAbstractSourceControl extends AbstractSourceControl {

  protected MockAbstractSourceControl() {
    super(ConfigurationManager.getInstance().getBuildConfiguration(TestHelper.TEST_CVS_VALID_BUILD_ID));
  }


  public int getChangesSince(final int changeListID) throws BuildException {
    return 0;
  }


  public String getRelativeBuildDir() throws BuildException {
    return null;
  }


  public Map getUsersMap() {
    return null;
  }


  /**
   * This method requests SourceControl to reload its
   * configuration from the database.
   * <p/>
   * If configuration has changed in such a way that requires
   * cleaning up source line, next operation involving
   * manipulation on source line file should should be performed
   * on a clean checkout directory.
   * <p/>
   * For instance, if source line path has changed, the content
   * of the old checkout directory should be cleaned up
   * (deleted).
   */
  public void reloadConfiguration() {
  }


  public boolean isBuildDirInitialized() throws IOException, BuildException {
    return true;
  }


  public String getSyncCommandNote(final int changeListID) {
    return null;
  }


  /**
   * @return Map with a shell variable name as a key and variable
   *         value as value. The shell variables will be made
   *         avaiable to the build commands.
   *         <p/>
   *         This is a default implementation that returns an
   *         empty map.
   * @see org.parabuild.ci.build.BuildScriptGenerator#addVariables(java.util.Map)
   */
  public Map getShellVariables() {
    return new HashMap(11);
  }


  public void label(final String label) throws BuildException {
  }


  public void syncToChangeList(final int changeListID) throws BuildException {
  }


  public void checkoutLatest() throws BuildException {
  }
}

class MockRemoteCommand extends RemoteCommand {

  private boolean cleanupCalled = false;


  public MockRemoteCommand(final Agent agent) {
    super(agent, true);
  }


  public void cleanup() throws AgentFailureException {
    cleanupCalled = true;
    super.cleanup();
  }


  public boolean isCleanupCalled() {
    return cleanupCalled;
  }
}