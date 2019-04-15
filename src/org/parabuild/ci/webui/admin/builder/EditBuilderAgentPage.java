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
import org.parabuild.ci.object.BuilderAgent;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.ButtonSeparator;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.SaveButton;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is responsible for creating/editting builder
 */
public final class EditBuilderAgentPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(EditBuilderAgentPage.class); // NOPMD

  public static final String PAGE_TITLE_DEFAULT = "Adding Build Farm Agent";
  public static final String ERROR_BUILDER_NOT_FOUND = "Requested build farm not found";

  private final EditBuilderAgentPanel pnlBuilderAgent = new EditBuilderAgentPanel(WebUIConstants.MODE_EDIT); // NOPMD
  private final SaveButton btnSave = new SaveButton(); // NOPMD
  private final CancelButton btnCancel = new CancelButton(); // NOPMD
  private final Flow flwSaveCancel = new Flow().add(btnSave).add(new ButtonSeparator()).add(btnCancel); // NOPMD


  /**
   * Constructor.
   */
  public EditBuilderAgentPage() {
    setTitle(makeTitle(PAGE_TITLE_DEFAULT));
    flwSaveCancel.setAlignX(Layout.CENTER);
    flwSaveCancel.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    baseContentPanel().getUserPanel().add(pnlBuilderAgent);
    baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
    baseContentPanel().getUserPanel().add(flwSaveCancel);
  }


  /**
   * Strategy method derived from BasePage.
   *
   * @param parameters
   */
  public Result executePage(final Parameters parameters) {
    if (log.isDebugEnabled()) {
      log.debug("Executing edit build farm agent page");
    }
    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_BUILDERS, parameters);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    if (isNew()) {
      if (parameters.isParameterPresent(Pages.PARAM_BUILDER_ID)) {

        // Builder ID is provided
        final BuilderConfiguration builderConfiguration = ParameterUtils.getBuilderFromParameters(parameters);
        if (builderConfiguration == null) {
          return createBuilderNotFoundResult();
        } else {
          // Builder found, load data
          pnlBuilderAgent.load(builderConfiguration);

          // Check if builder agent is set
          final BuilderAgent builderAgent = ParameterUtils.getBuilderAgentFromParameters(parameters);
          if (builderAgent == null) {
            // New agent
            final String title = "Adding Agent to Build Farm - " + builderConfiguration.getName();
            setTitle(makeTitle(title));
            pnlBuilderAgent.setTitle(title);
          } else {
            final String title = "Editing Agent for Build Farm - " + builderConfiguration.getName();
            setTitle(makeTitle(title));
            pnlBuilderAgent.setTitle(title);
            pnlBuilderAgent.load(builderAgent);
          }

          // Add cancel button listener
          btnCancel.addListener(new ButtonPressedListener() {
            private static final long serialVersionUID = -492831846932995216L;


            public Result buttonPressed(final ButtonPressedEvent event) {
              return createDoneResult(builderConfiguration);
            }
          });

          // Add save button listener
          btnSave.addListener(new ButtonPressedListener() {
            private static final long serialVersionUID = -5044933409329292882L;


            public Result buttonPressed(final ButtonPressedEvent event) {
              if (pnlBuilderAgent.save()) {
                return createDoneResult(builderConfiguration);
              } else {
                return Result.Continue();
              }
            }
          });
          return Result.Continue();
        }
      } else {
        return createBuilderNotFoundResult();
      }
    } else {
      return Result.Continue();
    }
  }


  private Result createDoneResult(final BuilderConfiguration builderConfiguration) {
    return Result.Done(Pages.PAGE_BUILDER_DETAILS, BuilderUtils.createBuilderResultParameters(builderConfiguration.getID()));
  }


  private Result createBuilderNotFoundResult() {
    baseContentPanel().getUserPanel().clear();
    baseContentPanel().showErrorMessage(ERROR_BUILDER_NOT_FOUND);
    return Result.Done();
  }


  public String toString() {
    return "EditBuilderPage{" +
            "pnlbuilder=" + pnlBuilderAgent +
            ", btnSave=" + btnSave +
            ", btnCancel=" + btnCancel +
            ", flwSaveCancel=" + flwSaveCancel +
            '}';
  }
}
