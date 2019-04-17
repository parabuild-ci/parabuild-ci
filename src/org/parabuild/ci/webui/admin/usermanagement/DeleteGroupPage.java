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

import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.*;
import org.parabuild.ci.configuration.*;
import viewtier.ui.*;

/**
 * This page is repsonsible for editing Parabuild system
 * properties.
 */
public final class DeleteGroupPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L; // NOPMD

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  /**
   * List of group names that can not be deleted.
   */
  private static final String[] NONDELETEABLE_GROUPS = {
    Group.SYSTEM_ADMIN_GROUP, Group.SYSTEM_ANONYMOUS_GROUP};

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeleteGroupPage() {
    setTitle(makeTitle("Delete group"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.ADMIN_DELETE_GROUP, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // check if group exists, show error message if not
    final Group group = ParameterUtils.getGroupFromParameters(params);
    final MessagePanel cp = super.baseContentPanel();
    if (group == null) {
      cp.showErrorMessage("Requested group can not be found.");
      cp.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_GROUPS));
      return Result.Done();
    }

    // check if it's deletable group

    // first check built-in non-deletable groups
    for (int i = 0; i < NONDELETEABLE_GROUPS.length; i++) {
      final String protectedGroup = NONDELETEABLE_GROUPS[i];
      if (group.getName().equalsIgnoreCase(protectedGroup)) {
        cp.showErrorMessage("Cannot delete system group \"" + group.getName() + '\"');
        cp.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_GROUPS));
        return Result.Done();
      }
    }
    // check if this is not an initial LDAP group
    final int initialLDAPGroupID = SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.LDAP_ADD_FIRST_TIME_USER_TO_GROUP, Group.UNSAVED_ID);
    if (initialLDAPGroupID == group.getID()) {
      cp.showErrorMessage("Cannot delete group \"" + group.getName() + '\"' + ". This group is an initial LDAP group.");
      cp.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_GROUPS));
    }

    setTitle(makeTitle("Delete group >> " + group.getName()));

    if (isNew()) {
      // display form and hand control to group
      final MessagePanel deleteGroupPanel = makeContent(group.getName());
      cp.getUserPanel().add(deleteGroupPanel);
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return Result.Done(Pages.ADMIN_GROUPS);
      } else if (action == ACTION_DELETE) {
        // delete group
        SecurityManager.getInstance().deleteGroup(group);
        // show success message
        cp.getUserPanel().clear();
        cp.getUserPanel().add(new Flow()
          .add(new BoldCommonLabel("Group has been deleted. "))
          .add(WebuiUtils.clickHereToContinue(Pages.ADMIN_GROUPS)));
        return Result.Done();
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeContent(final String groupName) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to delete group \"" + groupName + "\". Press \"Delete\" button to confirm.");
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
      private static final long serialVersionUID = 3602872524109857064L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 1987468520948528291L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }
}
