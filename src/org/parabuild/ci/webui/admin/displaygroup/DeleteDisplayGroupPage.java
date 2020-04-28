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
package org.parabuild.ci.webui.admin.displaygroup;

import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.object.DisplayGroup;
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
public final class DeleteDisplayGroupPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L;  // NOPMD

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeleteDisplayGroupPage() {
    setTitle(makeTitle("Delete group"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.ADMIN_DELETE_DISPLAY_GROUP, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // check if group exists, show error message if not
    final DisplayGroup group = ParameterUtils.getDisplayGroupFromParameters(params);
    final MessagePanel cp = super.baseContentPanel();
    if (group == null) {
      cp.showErrorMessage("Requested display group can not be found.");
      cp.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_DISPLAY_GROUPS));
      return Result.Done();
    }

    setTitle(makeTitle("Delete display group >> " + group.getName()));

    if (isNew()) {
      // display form and hand control to group
      final MessagePanel deleteGroupPanel = makeContent(group.getName());
      cp.getUserPanel().add(deleteGroupPanel);
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return Result.Done(Pages.ADMIN_DISPLAY_GROUPS);
      } else if (action == ACTION_DELETE) {
        // delete group
        DisplayGroupManager.getInstance().deleteGroup(group);
        // show success message
        cp.getUserPanel().clear();
        cp.getUserPanel().add(new Flow()
          .add(new BoldCommonLabel("Display group has been deleted. "))
          .add(WebuiUtils.clickHereToContinue(Pages.ADMIN_DISPLAY_GROUPS)));
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
      private static final long serialVersionUID = -3097230154117945660L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -3970482592809124284L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }
}
