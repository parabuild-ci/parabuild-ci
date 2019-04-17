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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.ButtonSeparator;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.SaveButton;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is responsible for creating/editting agent configuration
 */
public final class EditAgentPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(EditAgentPage.class); // NOPMD

  public static final String PAGE_TITLE_DEFAULT = "Manage Agent Configuration";
  public static final String PAGE_TITLE_ADD_AGENT = "Add New Agent";
  public static final String ERROR_AGENT_NOT_FOUND = "Requested agent not found";

  private final AgentPanel pnlAgent = new AgentPanel(AgentPanel.EDIT_MODE_ADMIN);  // NOPMD
  private final SaveButton btnSave = new SaveButton();  // NOPMD
  private final CancelButton btnCancel = new CancelButton();  // NOPMD
  private final Flow flwSaveCancel = new Flow().add(btnSave).add(new ButtonSeparator()).add(btnCancel);  // NOPMD


  /**
   * Constructor.
   */
  public EditAgentPage() {
    // layout
    setTitle(makeTitle(PAGE_TITLE_DEFAULT)); // default title
    flwSaveCancel.setAlignX(Layout.CENTER);
    flwSaveCancel.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    baseContentPanel().getUserPanel().add(pnlAgent);
    baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
    baseContentPanel().getUserPanel().add(flwSaveCancel);

    // add cancel button listener
    btnCancel.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -1780813357326862971L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        return Result.Done(Pages.PAGE_AGENT_LIST);
      }
    });

    // add save button listener
    btnSave.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -3612529047981728480L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        if (pnlAgent.save()) {
          return Result.Done(Pages.PAGE_AGENT_LIST);
        } else {
          return Result.Continue();
        }
      }
    });
  }


  /**
   * Strategy method derived from BasePage.
   *
   * @param parameters
   */
  public Result executePage(final Parameters parameters) {
    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.PAGE_EDIT_AGENT, parameters);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    if (parameters.isParameterPresent(Pages.PARAM_AGENT_ID)) {
      // agentConfig cofiguration ID is provided
      final AgentConfig agentConfig = ParameterUtils.getAgentFromParameters(parameters);
      if (agentConfig == null) {
        // show error and exit
        baseContentPanel().getUserPanel().clear();
        baseContentPanel().showErrorMessage(ERROR_AGENT_NOT_FOUND);
        return Result.Done();
      } else {
        // agentConfig configuration found, load data
        setTitle(makeTitle("Edit Agent \"" + agentConfig.getHost() + '\"'));
        pnlAgent.setTitle("Edit Agent");
        pnlAgent.load(agentConfig);
        return Result.Continue();
      }
    } else {

      // new agent configuration
      setFocusOnFirstInput(true);
      setTitle(makeTitle(PAGE_TITLE_ADD_AGENT));
      pnlAgent.setTitle("New Agent");
      return Result.Continue();
    }
  }


  public String toString() {
    return "EditAgentPage{" +
            "pnlBuilder=" + pnlAgent +
            ", btnSave=" + btnSave +
            ", btnCancel=" + btnCancel +
            ", flwSaveCancel=" + flwSaveCancel +
            '}';
  }
}
