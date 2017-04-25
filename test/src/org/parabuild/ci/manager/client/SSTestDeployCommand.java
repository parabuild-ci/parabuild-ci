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
package org.parabuild.ci.manager.client;

import org.apache.log4j.Logger;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.realm.RealmConstants;
import org.parabuild.ci.remote.internal.LocalAgentEnvironment;
import org.parabuild.ci.remote.internal.RemoteAgentEnvironmentProxy;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * SSTestDeployCommand
 * <p/>
 *
 * @author Slava Imeshev
 * @since May 24, 2009 12:23:43 PM
 */
public final class SSTestDeployCommand extends ServersideTestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL
   */
  private static final Logger LOG = Logger.getLogger(SSTestDeployCommand.class); // NOPMD
  private static final String PATH = "/";


  private DeployCommand deployCommand;


  public SSTestDeployCommand(final String s) {
    super(s);
  }


  public void testGetPath() {
    assertEquals(PATH, deployCommand.getPath());
  }


  public void testGetWar() throws MalformedURLException {
    assertEquals(getWar(), deployCommand.getWar());
  }


  public void testExecute() throws IOException, AgentFailureException {
    // Undeploy first
    final UndeployCommand undeployCommand = new UndeployCommand();
    undeployCommand.setPassword(RealmConstants.DEFAULT_AGENT_MANAGER_PASSWORD);
    undeployCommand.setUsername(RealmConstants.DEFAULT_AGENT_MANAGER_USER);
    final String agentUrl = getAgentUrl();
    undeployCommand.setUrl(agentUrl);
    undeployCommand.setPath(PATH);
    undeployCommand.execute();

    // Deploy
    deployCommand.execute();

    // Check remote verison matches our - this alse validate connectivity
    final AgentHost agentHost = new AgentHost(TestHelper.remoteTestBuilderHostName());
    final RemoteAgentEnvironmentProxy agentEnvironmentProxy = new RemoteAgentEnvironmentProxy(agentHost);
    final String remoteAgentVersion = agentEnvironmentProxy.builderVersionAsString();
    final LocalAgentEnvironment localEnv = new LocalAgentEnvironment();
    final String buildManagerVersion = localEnv.builderVersionAsString();
    assertEquals(buildManagerVersion, remoteAgentVersion);
  }


  protected void setUp() throws Exception {
    super.setUp();
    deployCommand = new DeployCommand();
    deployCommand.setPassword(RealmConstants.DEFAULT_AGENT_MANAGER_PASSWORD);
    deployCommand.setUsername(RealmConstants.DEFAULT_AGENT_MANAGER_USER);
    deployCommand.setPath(PATH);
    deployCommand.setUrl(getAgentUrl());
    deployCommand.setWar(getWar());
  }


  private static String getAgentUrl() {
    return "http://" + TestHelper.remoteTestBuilderHostName();
  }


  /**
   * Returns path of the war that should be redeployed on agent.
   *
   * @return path of the war that should be redeployed on agent.
   */
  private static String getWar() throws MalformedURLException {
    final String catalinaBase = System.getProperty("catalina.base");
    final File file = new File(catalinaBase, "app/parabuild.war");
    final String war = file.toURL().toExternalForm();
    return war;
  }
}
