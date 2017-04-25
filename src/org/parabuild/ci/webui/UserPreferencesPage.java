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
package org.parabuild.ci.webui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.object.User;
import org.parabuild.ci.webui.admin.usermanagement.UserPanel;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.ButtonSeparator;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.SaveButton;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is responsible for creating/editting user
 *
 * @noinspection ObjectToString
 */
public final class UserPreferencesPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(UserPreferencesPage.class); // NOPMD

  public static final String PAGE_TITLE = "User preferences";
  public static final String ERROR_USER_NOT_FOUND = "Requested user not found";

  private final UserPanel pnlUser = new UserPanel(UserPanel.MODE_EDIT_PREFERENCES);  // NOPMD
  private final SaveButton btnSave = new SaveButton();  // NOPMD
  private final CancelButton btnCancel = new CancelButton();  // NOPMD
  private final Flow flwSaveCancel = new Flow().add(btnSave).add(new ButtonSeparator()).add(btnCancel);  // NOPMD


  /**
   * Constructor.
   */
  public UserPreferencesPage() {
    // layout
    setTitle(makeTitle(PAGE_TITLE)); // default title
    flwSaveCancel.setAlignX(Layout.CENTER);
    flwSaveCancel.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    baseContentPanel().getUserPanel().add(pnlUser);
    baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
    baseContentPanel().getUserPanel().add(flwSaveCancel);

    // add cancel button listener
    btnCancel.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 0L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        return Result.Done(Pages.PUBLIC_BUILDS);
      }
    });

    // add save button listener
    btnSave.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 0L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        if (save()) {
          refreshUserInSession(getUserID() != User.UNSAVED_ID && getUserID() == pnlUser.getUserID());
          return Result.Done(Pages.PUBLIC_BUILDS);
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
    return pnlUser.save();
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
              Pages.PUBLIC_LOGIN, Pages.PUBLIC_PREFERENCES, parameters);
    }

    // user ID is provided
    final User userLoggedIn = getUser();
    if (userLoggedIn == null) {
      // show error and exit
      baseContentPanel().getUserPanel().clear();
      baseContentPanel().showErrorMessage(ERROR_USER_NOT_FOUND);
      return Result.Done();
    } else {
      // user found, load data
      setTitle(makeTitle(PAGE_TITLE + " for \"" + userLoggedIn.getName() + '\"'));
      pnlUser.setTitle("Preferences");
      pnlUser.load(userLoggedIn);
      return Result.Continue();
    }
  }


  public String toString() {
    return "UserPreferencesPage{" +
            "btnCancel=" + btnCancel +
            ", btnSave=" + btnSave +
            ", flwSaveCancel=" + flwSaveCancel +
            ", pnlUser=" + pnlUser +
            "} " + super.toString();
  }
}
