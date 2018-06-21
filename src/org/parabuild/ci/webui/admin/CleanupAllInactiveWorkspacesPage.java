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
package org.parabuild.ci.webui.admin;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Color;
import viewtier.ui.Component;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is repsonsible for cleaning up directories for all inactive workspaces.
 */
public final class CleanupAllInactiveWorkspacesPage extends BasePage implements ConversationalTierlet {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(CleanupAllInactiveWorkspacesPage.class); // NOPMD
  private static final long serialVersionUID = -6608031121284992231L; // NOPMD

  private static final String CAPTION_CLEANUP = " Cleanup ";

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public CleanupAllInactiveWorkspacesPage() {
    setTitle(makeTitle("Cleanup All Inactive Workspaces"));
  }


  /**
   * Strategy method to be implemented by classes inheriting
   * BasePage.
   *
   * @param parameters
   * @return result of page execution
   */
  protected Result executePage(final Parameters parameters) {

    // authenticate
    if (!isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    if (isNew()) {
      // display form and hand control to user
      baseContentPanel().getUserPanel().add(makeConfirmCleanupPanel());
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        // return to the comand list
        return Result.Done(Pages.PAGE_UNDOCUMENTED_COMMANDS);
      } else if (action == ACTION_DELETE) {
        return cleanupAllInactiveWorkspaces();
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeConfirmCleanupPanel() {
    // request
    final Label lbConfirmationRequest = new BoldCommonLabel("You are about to clean up working directories for ALL inactive build configurations. Please be patient, this may take some time.  Press \"Clean up\" button to confirm.");
    lbConfirmationRequest.setHeight(30);
    lbConfirmationRequest.setAlignY(Layout.CENTER);

    // buttons
    final CommonButton btnCancel = new CancelButton();
    final CommonButton btnConfirm = new CommonButton(CAPTION_CLEANUP);
    final Flow buttons = new Flow();
    buttons.add(btnCancel).add(new BoldCommonLabel("    ")).add(btnConfirm);

    // panel and layout
    final MessagePanel panel = new MessagePanel(false);
    panel.getUserPanel().add(lbConfirmationRequest);
    panel.getUserPanel().add(buttons);

    // messaging
    btnConfirm.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    btnCancel.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }


  /**
   * Processes delete request.
   *
   * @return Result.Done() if deleted or Result.Continue() if
   *         preconditions didn't hold and delete was not
   *         performed.
   */
  private Result cleanupAllInactiveWorkspaces() {
    // clear panel
    baseContentPanel().getUserPanel().clear();

    final List statuses = BuildManager.getInstance().getCurrentBuildsStatuses();
    for (int i = 0; i < statuses.size(); i++) {
      final BuildState buildState = (BuildState) statuses.get(i);
      if (buildState.getStatus().equals(BuildStatus.INACTIVE)) {
        final String buildName = buildState.getBuildName();
        try {
          final List hosts = AgentManager.getInstance().getLiveAgentHosts(buildState.getActiveBuildID(), true);
          for (int j = 0; j < hosts.size(); j++) {
            final AgentHost agentHost = (AgentHost) hosts.get(j);
            final Agent agent = AgentManager.getInstance().createAgent(buildState.getActiveBuildID(), agentHost);
            if (!agent.checkoutDirIsEmpty()) {
              if (agent.deleteCheckoutDir()) {
                baseContentPanel().getUserPanel().add(makeSuccessLabel(buildName, agent.getCheckoutDirName()));
              } else {
                baseContentPanel().getUserPanel().add(makeFailureLabel(buildName, agent.getCheckoutDirName()));
              }
            }
          }
        } catch (final Exception e) {
          baseContentPanel().getUserPanel().add(makeExceptionLabel(buildName, e));
        }
      }
    }

    // show success message
    baseContentPanel().getUserPanel().add(new Flow()
            .add(new BoldCommonLabel("Inactive directories have been deleted"))
            .add(WebuiUtils.clickHereToContinue(Pages.PAGE_UNDOCUMENTED_COMMANDS)));
    return Result.Done();
  }


  private Component makeSuccessLabel(final String buildName, final String checkoutDirName) {
    final BoldCommonLabel label = new BoldCommonLabel("Build " + buildName + ": Successfuly deleted directory " + checkoutDirName);
    label.setForeground(Color.Green);
    return label;
  }


  private Component makeFailureLabel(final String buildName, final String checkoutDirName) {
    final BoldCommonLabel label = new BoldCommonLabel("Build " + buildName + ": Could not delete directory " + checkoutDirName);
    label.setForeground(Color.Red);
    return label;
  }


  private BoldCommonLabel makeExceptionLabel(final String buildName, final Exception e) {
    final BoldCommonLabel lbError = new BoldCommonLabel("Build " + buildName + ": Error while deleting: " + StringUtils.toString(e));
    lbError.setForeground(Color.Red);
    return lbError;
  }
}
