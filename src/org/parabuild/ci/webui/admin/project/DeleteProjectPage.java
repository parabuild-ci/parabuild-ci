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

import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.Project;
import org.parabuild.ci.project.ProjectManager;
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
public final class DeleteProjectPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L; // NOPMD

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeleteProjectPage() {
    setTitle(makeTitle("Delete project"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.PAGE_DELETE_PROJECT, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // check if project exists, show error message if not
    final Project project = ParameterUtils.getProjectFromParameters(params);
    if (project == null || project.getID() == 0) {
      super.baseContentPanel().showErrorMessage("Requested project can not be found.");
      super.baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.PAGE_PROJECTS));
      return Result.Done();
    }

    // check if it's deletable project
    final String[] protectedProjects = {"admin", "jira"};
    for (int i = 0; i < protectedProjects.length; i++) {
      final String protectedProject = protectedProjects[i];
      if (project.getName().equalsIgnoreCase(protectedProject)) {
        super.baseContentPanel().showErrorMessage("System project \"" + project.getName() + "\" can not be deleted.");
        super.baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.PAGE_PROJECTS));
        return Result.Done();
      }
    }


    // Check if there are build associated with this project.
    final ProjectManager pm = ProjectManager.getInstance();
    final int buildCount = pm.getBuildCount(project.getID());
    if (buildCount > 0) {
      super.baseContentPanel().showErrorMessage("Project \"" + project.getName() + "\" can not be deleted because it contains builds (" + buildCount + ").");
      super.baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.PAGE_PROJECTS));
      return Result.Done();
    }

    setTitle(makeTitle("Deleting project >> " + project.getName()));

    if (isNew()) {
      if (!SystemConfigurationManagerFactory.getManager().isProjectDeletionEnabled()) {
        super.baseContentPanel().getUserPanel().clear();
        super.baseContentPanel().getUserPanel().add(new Flow()
                .add(new BoldCommonLabel("Deleting projects is disabled. Please adjust system settings to enable deleting projects."))
                .add(WebuiUtils.clickHereToContinue(Pages.PAGE_PROJECTS)));
        return Result.Done();
      }
      // display form and hand control to project
      final MessagePanel deleteProjectPanel = makeConfirmDeletePanel(project.getName());
      super.baseContentPanel().getUserPanel().add(deleteProjectPanel);
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return Result.Done(Pages.PAGE_PROJECTS);
      } else if (action == ACTION_DELETE) {
        // delete project
        pm.deleteProject(project);
        // show success message
        super.baseContentPanel().getUserPanel().clear();
        super.baseContentPanel().getUserPanel().add(new Flow()
                .add(new BoldCommonLabel("Project has been deleted. "))
                .add(WebuiUtils.clickHereToContinue(Pages.PAGE_PROJECTS)));
        return Result.Done();
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeConfirmDeletePanel(final String projectName) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to delete project \"" + projectName + "\". All build configurations belonging to this project will be deleted as well. Press \"Delete\" button to confirm.");
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
      public Result buttonPressed(final ButtonPressedEvent event) {
        action = DeleteProjectPage.ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        action = DeleteProjectPage.ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }
}
