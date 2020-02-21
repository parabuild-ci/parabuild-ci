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
package org.parabuild.ci.webui.admin.system;

import org.parabuild.ci.configuration.GlobalVCSUserMapManager;
import org.parabuild.ci.object.GlobalVCSUserMap;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.DeleteButton;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
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
public final class GlobalVCSUserMapDeletePage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L;  // NOPMD

  private static final String CAPTION_DELETE_MAPPING = "Delete Mapping";
  private static final String CAPTION_REQUESTED_MAPPING_CAN_NOT_BE_FOUND = "Requested mapping can not be found.";
  private static final String CAPTION_MAPPING_HAS_BEEN_DELETED = "Mapping has been deleted. ";

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public GlobalVCSUserMapDeletePage() {
    setTitle(makeTitle(CAPTION_DELETE_MAPPING));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // Authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // Check if  exists, show error message if not
    final GlobalVCSUserMap map = GlobalVCSUserMapUtil.getMappingFromParameters(params);
    final MessagePanel cp = super.baseContentPanel();
    if (map == null) {
      cp.showErrorMessage(CAPTION_REQUESTED_MAPPING_CAN_NOT_BE_FOUND);
      cp.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP));
      return Result.Done();
    }

    setTitle(makeTitle("Delete Mapping >> " + map.getVcsUserName()));

    if (isNew()) {
      // Display form and hand control
      final MessagePanel deleteMappingPanel = makeContent(map.getVcsUserName());
      cp.getUserPanel().add(deleteMappingPanel);
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return Result.Done(Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP);
      } else if (action == ACTION_DELETE) {
        // Delete
        GlobalVCSUserMapManager.getInstance().deleteMapping(map);
        // Show success message
        cp.getUserPanel().clear();
        cp.getUserPanel().add(new Flow()
                .add(new BoldCommonLabel(CAPTION_MAPPING_HAS_BEEN_DELETED))
                .add(WebuiUtils.clickHereToContinue(Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP)));
        return Result.Done();
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeContent(final String mappingName) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to delete mapping \"" + mappingName + "\". Press \"Delete\" button to confirm.");
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
      private static final long serialVersionUID = 5827537735773267135L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 7717338970389923130L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }


  public String toString() {
    return "GlobalVersionControlUserMapDeletePage{" +
            "action=" + action +
            '}';
  }
}
