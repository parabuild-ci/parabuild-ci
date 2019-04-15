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
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.DeleteButton;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Color;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is repsonsible for editing Parabuild system
 * properties.
 */
public final class DeleteAgentPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L; // NOPMD

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeleteAgentPage() {
    setTitle(makeTitle("Delete Agent Configuration"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters parameters) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.PAGE_DELETE_AGENT, parameters);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // check if agentConfig exists, show error message if not
    final AgentConfig agentConfig = ParameterUtils.getAgentFromParameters(parameters);
    if (agentConfig == null) {
      super.baseContentPanel().showErrorMessage("Requested agent not found.");
      super.baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.PAGE_AGENT_LIST));
      return Result.Done();
    }

    // check if it's deletable agentConfig
    if (agentConfig.isLocal()) {
      super.baseContentPanel().showErrorMessage("System agent configuration \"" + agentConfig.getHost() + "\" can not be deleted.");
      super.baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.PAGE_AGENT_LIST));
      return Result.Done();
    }

    setTitle(makeTitle("Delete Agent >> " + agentConfig.getHost()));

    if (isNew()) {
      final MessagePanel panel = super.baseContentPanel();

      // Check if this is not the last agent in any of the builders
      if (agentConfig.isEnabled() && !BuilderUtils.validateNotLastAgent(panel, agentConfig.getID())) {
        return Result.Done();
      }

      // Display form and hand control to agentConfig
      final MessagePanel deleteAgentPanel = makeConfirmDeletePanel(agentConfig.getHost());
      super.baseContentPanel().getUserPanel().add(deleteAgentPanel);
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return Result.Done(Pages.PAGE_AGENT_LIST);
      } else if (action == ACTION_DELETE) {
        // delete agentConfig
        BuilderConfigurationManager.getInstance().deletedAgent(agentConfig);
        // show success message
        super.baseContentPanel().getUserPanel().clear();
        super.baseContentPanel().getUserPanel().add(new Flow()
                .add(new BoldCommonLabel("Agent has been deleted. "))
                .add(WebuiUtils.clickHereToContinue(Pages.PAGE_AGENT_LIST)));
        // Notify agent status monitor
        ServiceManager.getInstance().getAgentStatusMonitor().notifyAgentDeleted(agentConfig.getID());
        return Result.Done();
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeConfirmDeletePanel(final String agentHostAndPort) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to delete agent \"" + agentHostAndPort + "\". Press \"Delete\" button to confirm.");
    confirmationRequestLabel.setHeight(30);
    confirmationRequestLabel.setAlignY(Layout.CENTER);
    confirmationRequestLabel.setForeground(Color.DarkRed);

    // buttons
    final Flow buttons = new Flow();
    final CommonButton cancelDeleteButton = new CancelButton();
    final CommonButton confimDeleteButton = new DeleteButton();
    buttons.add(cancelDeleteButton).add(new BoldCommonLabel("    ")).add(confimDeleteButton);

    // panel and layout
    final MessagePanel panel = new MessagePanel(false);
    panel.getUserPanel().add(confirmationRequestLabel);
    panel.getUserPanel().add(buttons);

    // messaging
    confimDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -4198648988809994773L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 1498603785427244389L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }


  public String toString() {
    return "DeleteAgentPage{" +
            "action=" + action +
            '}';
  }
}
