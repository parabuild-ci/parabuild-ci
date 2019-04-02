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
package org.parabuild.ci.webui.agent.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.remote.internal.LocalAgentEnvironment;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.webui.EnvironmentPanel;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * AgentEnvironmentPage outputs agent's page.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Mar 29, 2010 12:47:52 PM
 */
public final class AgentEnvironmentPage extends BasePage implements StatelessTierlet {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration, unused
   */
  private static final Log LOG = LogFactory.getLog(AgentEnvironmentPage.class); // NOPMD
  private static final long serialVersionUID = 6530850640252632895L;
  private static final String ERROR_AGENT_NOT_FOUND = "Requested agent not found";
  private static final String AGENT_ENVIRONMENT = "Agent Environment";


  public AgentEnvironmentPage() {
    super(FLAG_SHOW_PAGE_HEADER_LABEL|FLAG_SHOW_HEADER_SEPARATOR);
    setTitle(makeTitle(AGENT_ENVIRONMENT));
  }


  protected Result executePage(final Parameters parameters) {

    //
    if (!isValidUser() && !org.parabuild.ci.security.SecurityManager.getInstance().isAnonymousAccessEnabled()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.PAGE_AGENT_ENVIRONMENT, parameters);
    }

    // Agent configuration ID is provided
    final AgentConfig agentConfig = ParameterUtils.getAgentFromParameters(parameters);
    if (agentConfig == null) {
      // Show error and exit
      baseContentPanel().getUserPanel().clear();
      baseContentPanel().showErrorMessage(ERROR_AGENT_NOT_FOUND);
      return Result.Done();
    }

    // Set title
    setPageHeaderAndTitle("Agent Environment: " + agentConfig.getHost());


    // Check status
    final AgentStatus agentStatus = ServiceManager.getInstance().getAgentStatusMonitor().getStatus(agentConfig.getID());
    final byte activity = agentStatus.getActivityType();
    if (activity != AgentStatus.ACTIVITY_BUSY && activity != AgentStatus.ACTIVITY_IDLE) {
      baseContentPanel().getUserPanel().clear();
      baseContentPanel().showErrorMessage("Cannot obtain environment for agent status: " + agentStatus.getActivityTypeAsString());
      return Result.Done();
    }


    // Show panels
    try {
      final String hostName = agentStatus.getHostName();
      final AgentHost host = new AgentHost(hostName);
      final AgentEnvironment agentEnvironment;
      if (host.isLocal()) {
        agentEnvironment = new LocalAgentEnvironment();
      } else {
        agentEnvironment = AgentManager.getInstance().getAgentEnvironment(host);
      }
      baseContentPanel().getUserPanel().add(new EnvironmentPanel(agentEnvironment.getSystemProperties(), "JVM Environment"));
      baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
      baseContentPanel().getUserPanel().add(new EnvironmentPanel(agentEnvironment.getEnv(), "Shell Environment"));

    } catch (final Exception e) {
      baseContentPanel().getUserPanel().clear();
      baseContentPanel().showErrorMessage("Error obtaining agent environment: " + e.toString());
    }
    return Result.Done();
  }
}
