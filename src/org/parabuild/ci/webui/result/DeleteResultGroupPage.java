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
package org.parabuild.ci.webui.result;

import org.parabuild.ci.configuration.ResultGroupManager;
import org.parabuild.ci.object.ResultGroup;
import org.parabuild.ci.security.ResultGroupRights;
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
 */
public final class DeleteResultGroupPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L;  // NOPMD

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeleteResultGroupPage() {
    setTitle(makeTitle("Delete result group"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.RESULT_GROUP_DELETE, params);
    }

    // check if resultGroup exists, show error message if not
    final ResultGroup resultGroup = ParameterUtils.getResultGroupFromParameters(params);
    final MessagePanel cp = super.baseContentPanel();
    if (resultGroup == null) {
      cp.showErrorMessage("Requested result group can not be found.");
      cp.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.RESULT_GROUPS));
      return Result.Done();
    }

    // verify that a user has a right to delete this group
    final ResultGroupRights userResultGroupRights = SecurityManager.getInstance().getUserResultGroupRights(getUser(), resultGroup.getID());
    if (!userResultGroupRights.isAllowedToDeleteResultGroup()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    setTitle(makeTitle("Delete result group >> " + resultGroup.getName()));

    if (isNew()) {
      // display form and hand control to resultGroup
      final MessagePanel deleteResultGroupPanel = makeContent(resultGroup.getName());
      cp.getUserPanel().add(deleteResultGroupPanel);
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return Result.Done(Pages.RESULT_GROUPS);
      } else if (action == ACTION_DELETE) {
        // delete resultGroup
        ResultGroupManager.getInstance().deleteResultGroup(resultGroup);
        // show success message
        cp.getUserPanel().clear();
        cp.getUserPanel().add(new Flow()
          .add(new BoldCommonLabel("Result group has been deleted. "))
          .add(WebuiUtils.clickHereToContinue(Pages.RESULT_GROUPS)));
        return Result.Done();
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeContent(final String resultGroupName) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to delete result group \"" + resultGroupName + "\". Press \"Delete\" button to confirm.");
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
      private static final long serialVersionUID = 3130242747185368044L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -2434247036778540815L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }
}
