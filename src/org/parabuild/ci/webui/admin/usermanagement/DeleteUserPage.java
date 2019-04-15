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
package org.parabuild.ci.webui.admin.usermanagement;

import org.parabuild.ci.object.User;
import org.parabuild.ci.security.SecurityManager;
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
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is repsonsible for editing Parabuild system
 * properties.
 */
public final class DeleteUserPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L; // NOPMD

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeleteUserPage() {
    setTitle(makeTitle("Delete user"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.ADMIN_DELETE_USER, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // check if user exists, show error message if not
    final User user = ParameterUtils.getUserFromParameters(params);
    if (user == null) {
      super.baseContentPanel().showErrorMessage("Requested user can not be found.");
      super.baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_USERS));
      return Result.Done();
    }

    // check if it's deletable user
    final String[] protectedUsers = {"admin", "jira"};
    for (int i = 0; i < protectedUsers.length; i++) {
      final String protectedUser = protectedUsers[i];
      if (user.getName().equalsIgnoreCase(protectedUser)) {
        super.baseContentPanel().showErrorMessage("System user \"" + user.getName() + "\" can not be deleted.");
        super.baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_USERS));
        return Result.Done();
      }
    }

    setTitle(makeTitle("Delete user >> " + user.getName()));

    if (isNew()) {
      // display form and hand control to user
      final MessagePanel deleteUserPanel = makeConfirmDeletePanel(user.getName());
      super.baseContentPanel().getUserPanel().add(deleteUserPanel);
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return Result.Done(Pages.ADMIN_USERS);
      } else if (action == ACTION_DELETE) {
        // delete user
        SecurityManager.getInstance().deleteUser(user);
        // show success message
        super.baseContentPanel().getUserPanel().clear();
        super.baseContentPanel().getUserPanel().add(new Flow()
          .add(new BoldCommonLabel("User has been deleted. "))
          .add(WebuiUtils.clickHereToContinue(Pages.ADMIN_USERS)));
        return Result.Done();
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeConfirmDeletePanel(final String userName) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to delete user \"" + userName + "\". Press \"Delete\" button to confirm.");
    confirmationRequestLabel.setHeight(30);
    confirmationRequestLabel.setAlignY(Layout.CENTER);

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
      private static final long serialVersionUID = -224157615888519042L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 3367839542500747914L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }
}
