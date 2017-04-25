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
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This page is repsonsible for editing Parabuild system
 * properties.
 */
public final class DetachBuilderAgentPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L;  // NOPMD

  private static final String DELETE_BUILDER = "Detach Build Farm Agent";
  private static final String REQUESTED_BUILDER_CAN_NOT_BE_FOUND = "Requested build farm agent can not be found.";

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DetachBuilderAgentPage() {
    setTitle(makeTitle(DELETE_BUILDER));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters parameters) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.PAGE_DETACH_BUILDER_AGENT, parameters);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // check if exists, show error message if not
    final BuilderAgent builderAgent = ParameterUtils.getBuilderAgentFromParameters(parameters);
    final MessagePanel cp = super.baseContentPanel();
    if (builderAgent == null) {
      cp.showErrorMessage(REQUESTED_BUILDER_CAN_NOT_BE_FOUND);
      cp.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILDERS));
      return Result.Done();
    }

    setTitle(makeTitle("Detach Agent >> " + builderAgent.getID()));

    if (isNew()) {

      // Check if this is not the last agent in any of the builders
      final AgentConfig agentConfig = BuilderConfigurationManager.getInstance().getAgentConfig(builderAgent.getAgentID());
      if (agentConfig.isEnabled() && !BuilderUtils.validateNotLastAgent(cp, builderAgent.getBuilderID(), agentConfig.getID())) {
        return Result.Done();
      }

      // Display form and hand control
      final MessagePanel deleteBuilderPanel = makeContent(builderAgent.getID());
      cp.getUserPanel().add(deleteBuilderPanel);
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return Result.Done(Pages.ADMIN_BUILDERS);
      } else if (action == ACTION_DELETE) {
        // delete builderedBuilderConfiguration
        BuilderConfigurationManager.getInstance().detachBuilderAgent(builderAgent);
        // show success message
        cp.getUserPanel().clear();
        cp.getUserPanel().add(new Flow()
                .add(new BoldCommonLabel("Agent has been detached"))
                .add(WebuiUtils.clickHereToContinue(Pages.PAGE_BUILDER_DETAILS, BuilderUtils.createBuilderParameters(builderAgent.getBuilderID()))));
        return Result.Done();
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeContent(final int builderAgentID) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to detach agent \"" + builderAgentID + "\". Press \"Detach\" button to confirm.");
    confirmationRequestLabel.setHeight(30);
    confirmationRequestLabel.setAlignY(Layout.CENTER);

    // buttons
    final Flow buttons = new Flow();
    final CommonButton confimDetachButton = new CommonButton(" Detach ");
    final CommonButton cancelDetachButton = new CommonButton(" Cancel ");
    buttons.add(confimDetachButton).add(new BoldCommonLabel("    ")).add(cancelDetachButton);

    // panel and layout
    final MessagePanel panel = new MessagePanel(false);
    panel.getUserPanel().add(confirmationRequestLabel);
    panel.getUserPanel().add(buttons);

    // messaging
    confimDetachButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 1659250364205840172L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    cancelDetachButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 1158513934474773215L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }


  public String toString() {
    return "DetachBuilderPage{" +
            "action=" + action +
            '}';
  }
}
