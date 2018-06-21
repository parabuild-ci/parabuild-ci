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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.services.BuildStartRequest;

import java.io.IOException;

/**
 * Defines a factory used by {@link BuildRunner} to create a
 * agent to work on the workspace.
 * <p/>
 * This fault implementation just delegates creating a
 * agent to AgentFactory.
 */
final class DefaultBuildRunnerAgentFactory implements BuildRunnerAgentFactory {

  private static final Log LOG = LogFactory.getLog(AutomaticScheduler.class); // NOPMD


  /**
   * {@inheritDoc}
   */
  public Agent checkoutAgent(final int activeBuildID, final BuildStartRequest buildStartRequest) {

    try {

      final AgentHost agentHost = buildStartRequest.getAgentHost();
      final boolean agentHostRequired = buildStartRequest.isAgentHostRequired();
      final boolean uniqueAgentCheckout = buildStartRequest.isUniqueAgentCheckout();
      LOG.debug("Desired agent host: " + agentHost + ", agentHostRequired: " + agentHostRequired);

      final AgentManager agentManager = AgentManager.getInstance();
      final AgentHost result = agentManager.checkoutAgentHost(activeBuildID, agentHost, uniqueAgentCheckout, agentHostRequired);

      LOG.debug("Final agent host: " + result);

      if (result == null) {
        return null;
      }

      return agentManager.createAgent(activeBuildID, result);

    } catch (final IOException e) {

      return new FailedAgent(e);
    }
  }


  public boolean supportsNextAgent() {

    return true;
  }


  /**
   * {@inheritDoc}
   */
  public void checkinAgent(final Agent agent) {

    final AgentHost agentHost = agent.getHost();
    AgentManager.getInstance().checkinAgentHost(agentHost);
  }
}
