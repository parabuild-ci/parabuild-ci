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
package org.parabuild.ci.webui.admin.project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.object.Project;
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
 * This page is responsible for creating/editting project
 */
public final class EditProjectPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(EditProjectPage.class); // NOPMD

  public static final String PAGE_TITLE_DEFAULT = "Manage Project";
  public static final String PAGE_TITLE_ADD_PROJECT = "Add New Project";
  public static final String ERROR_PROJECT_NOT_FOUND = "Requested Project Not Found";

  private final ProjectPanel pnlProject = new ProjectPanel();  // NOPMD
  private final SaveButton btnSave = new SaveButton();  // NOPMD
  private final CancelButton btnCancel = new CancelButton();  // NOPMD
  private final Flow flwSaveCancel = new Flow().add(btnSave).add(new ButtonSeparator()).add(btnCancel);  // NOPMD


  /**
   * Constructor.
   */
  public EditProjectPage() {
    // layout
    setTitle(makeTitle(PAGE_TITLE_DEFAULT)); // default title
    flwSaveCancel.setAlignX(Layout.CENTER);
    flwSaveCancel.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    baseContentPanel().getUserPanel().add(pnlProject);
    baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
    baseContentPanel().getUserPanel().add(flwSaveCancel);

    // add cancel button listener
    btnCancel.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 1696172704055910799L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        return Result.Done(Pages.PAGE_PROJECTS);
      }
    });

    // add save button listener
    btnSave.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 3177176257449233188L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        if (save()) {
          return Result.Done(Pages.PAGE_PROJECTS);
        } else {
          return Result.Continue();
        }
      }
    });
  }


  /**
   * Saves edits.
   *
   * @return true if saved successfully.
   */
  private boolean save() {
    return pnlProject.save();
  }


  /**
   * Strategy method derived from BasePage.
   *
   * @param params
   */
  public Result executePage(final Parameters params) {
    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.PAGE_EDIT_PROJECT, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    if (params.isParameterPresent(Pages.PARAM_PROJECT_ID)) {
      // project ID is provided
      final Project projectFromParameters = ParameterUtils.getProjectFromParameters(params);
      if (projectFromParameters == null) {
        // show error and exit
        baseContentPanel().getUserPanel().clear();
        baseContentPanel().showErrorMessage(ERROR_PROJECT_NOT_FOUND);
        return Result.Done();
      } else {
        // project found, load data
        setTitle(makeTitle("Edit Project \"" + projectFromParameters.getName() + '\"'));
        pnlProject.setTitle("Edit Project");
        pnlProject.load(projectFromParameters);
        return Result.Continue();
      }
    } else {

      // new project
      setFocusOnFirstInput(true);
      setTitle(makeTitle(PAGE_TITLE_ADD_PROJECT));
      pnlProject.setTitle("New Project");
      return Result.Continue();
    }
  }
}
