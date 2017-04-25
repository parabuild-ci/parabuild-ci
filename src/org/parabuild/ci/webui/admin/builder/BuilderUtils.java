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
package org.parabuild.ci.webui.admin.builder;

import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuilderAgent;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * BuilderUtils
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 16, 2009 7:31:14 PM
 */
final class BuilderUtils {

  private BuilderUtils() {
  }


  public static Properties createBuilderParameters(final int builderID) {
    final Properties properties = new Properties();
    properties.setProperty(Pages.PARAM_BUILDER_ID, Integer.toString(builderID));
    return properties;
  }


  public static Parameters createBuilderResultParameters(final int id) {
    final Parameters parameters = new Parameters();
    parameters.addParameter(Pages.PARAM_BUILDER_ID, id);
    return parameters;
  }


  public static boolean validateNotLastAgent(final MessagePanel panel, final int agentID) {
    final List usedInBuilders = new ArrayList(1);
    final List agentBuilders = BuilderConfigurationManager.getInstance().getAgentBuilders(agentID);
    for (int i = 0; i < agentBuilders.size(); i++) {
      final BuilderAgent builderAgent = (BuilderAgent) agentBuilders.get(i);
      final int builderID = builderAgent.getBuilderID();
      final int builderAgentCount = BuilderConfigurationManager.getInstance().getBuilderAgentCount(builderID);
      if (builderAgentCount <= 1) {
        usedInBuilders.add(BuilderConfigurationManager.getInstance().getBuilder(builderID));
      }
    }
    if (usedInBuilders.isEmpty()) {
      return true;
    }

    final StringBuffer errorMessage = new StringBuffer(200);
    errorMessage.append("This agent cannot be deleted because it is the last agent attached to the following build farm(s): ");
    for (int i = 0; i < usedInBuilders.size(); i++) {
      final BuilderConfiguration builderConfig = (BuilderConfiguration) usedInBuilders.get(i);
      errorMessage.append(builderConfig.getName());
      if (i < usedInBuilders.size() - 1) {
        errorMessage.append(", ");
      }
    }
    errorMessage.append(". Change the build farm configuration(s) and then try to delete this agent again.");
    panel.showErrorMessage(errorMessage);
    panel.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.PAGE_AGENT_LIST));
    return false;
  }


  public static boolean validateNotLastAgent(final MessagePanel panel, final int builderID, final int agentID) {
    final BuilderConfigurationManager bcm = BuilderConfigurationManager.getInstance();
    final int builderAgentCount = bcm.getBuilderAgentCount(builderID);
    if (builderAgentCount > 1) {
      return true;
    }

    final AgentConfig agentConfig = bcm.getAgentConfig(agentID);
    final BuilderConfiguration builder = bcm.getBuilder(builderID);
    panel.showErrorMessage("Agent \"" + agentConfig.getHost() + "\" cannot be detached because it is the last agent attached to build farm \"" + builder.getName() + '\"');
    panel.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.PAGE_BUILDER_DETAILS, BuilderUtils.createBuilderParameters(builderID)));
    return false;
  }
}
