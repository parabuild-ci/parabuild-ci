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
public final class EditBuilderPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(EditBuilderPage.class); // NOPMD

  public static final String PAGE_TITLE_DEFAULT = "Manage Build Farm";
  public static final String PAGE_TITLE_ADD_BUILDER = "Add Build Farm";
  public static final String ERROR_BUILDER_NOT_FOUND = "Requested Build Farm not found";

  private final EditBuilderPanel pnlBuilder = new EditBuilderPanel(WebUIConstants.MODE_EDIT); // NOPMD
  private final SaveButton btnSave = new SaveButton(); // NOPMD
  private final CancelButton btnCancel = new CancelButton(); // NOPMD
  private final Flow flwSaveCancel = new Flow().add(btnSave).add(new ButtonSeparator()).add(btnCancel); // NOPMD


  /**
   * Constructor.
   */
  public EditBuilderPage() {
    // layout
    setTitle(makeTitle(PAGE_TITLE_DEFAULT)); // default title
    flwSaveCancel.setAlignX(Layout.CENTER);
    flwSaveCancel.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    baseContentPanel().getUserPanel().add(pnlBuilder);
    baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
    baseContentPanel().getUserPanel().add(flwSaveCancel);

    // add cancel button listener
    btnCancel.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        if (log.isDebugEnabled()) {
          log.debug("Canceled - returning to build farm list");
        }
        return Result.Done(Pages.ADMIN_BUILDERS);
      }
    });

    // add save button listener
    btnSave.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        if (pnlBuilder.save()) {
          if (log.isDebugEnabled()) {
            log.debug("Saved - returning to build farm list");
          }
          return Result.Done(Pages.ADMIN_BUILDERS);
        } else {
          if (log.isDebugEnabled()) {
            log.debug("Couldn't save - contniuing edit");
          }
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
    if (log.isDebugEnabled()) {
      log.debug("Executing edit build farm page");
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
        final BuilderConfiguration builderConfigurationFromParameters = ParameterUtils.getBuilderFromParameters(parameters);
        if (builderConfigurationFromParameters == null) {
          // show error and exit
          baseContentPanel().getUserPanel().clear();
          baseContentPanel().showErrorMessage(ERROR_BUILDER_NOT_FOUND);
          return Result.Done();
        } else {
          // Builder found, load data
          final String title = "Edit Build Farm - " + builderConfigurationFromParameters.getName();
          setTitle(makeTitle(title));
          pnlBuilder.setTitle(title);
          pnlBuilder.load(builderConfigurationFromParameters);
          return Result.Continue();
        }
      } else {
        // REVIEWME: simeshev@parabuilci.org -> implement
        // new group
        setFocusOnFirstInput(true);
        setTitle(makeTitle(PAGE_TITLE_ADD_BUILDER));
        pnlBuilder.setTitle("New Build Farm");
        return Result.Continue();
      }
    } else {
      return Result.Continue();
    }
  }


  public String toString() {
    return "EditBuilderPage{" +
            "pnlbuilder=" + pnlBuilder +
            ", btnSave=" + btnSave +
            ", btnCancel=" + btnCancel +
            ", flwSaveCancel=" + flwSaveCancel +
            '}';
  }
}
