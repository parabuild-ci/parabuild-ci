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

import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This page is responsible for creating/editting user
 */
public final class EditUserPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(EditUserPage.class); // NOPMD

  public static final String PAGE_TITLE_DEFAULT = "Manage User";
  public static final String PAGE_TITLE_ADD_USER = "Add User";
  public static final String ERROR_USER_NOT_FOUND = "Requested user not found";

  private final UserPanel pnlUser = new UserPanel(UserPanel.MODE_EDIT_USER);  // NOPMD
  private final SaveButton btnSave = new SaveButton();  // NOPMD
  private final CancelButton btnCancel = new CancelButton();  // NOPMD
  private final Flow flwSaveCancel = new Flow().add(btnSave).add(new ButtonSeparator()).add(btnCancel);  // NOPMD


  /**
   * Constructor.
   */
  public EditUserPage() {
    // layout
    setTitle(makeTitle(PAGE_TITLE_DEFAULT)); // default title
    flwSaveCancel.setAlignX(Layout.CENTER);
    flwSaveCancel.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    baseContentPanel().getUserPanel().add(pnlUser);
    baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
    baseContentPanel().getUserPanel().add(flwSaveCancel);

    // add cancel button listener
    btnCancel.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        return Result.Done(Pages.ADMIN_USERS);
      }
    });

    // add save button listener
    btnSave.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        if (save()) {
          refreshUserInSession(getUserID() != User.UNSAVED_ID && getUserID() == pnlUser.getUserID());
          return Result.Done(Pages.ADMIN_USERS);
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
   * @param params
   */
  public Result executePage(final Parameters params) {
    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.ADMIN_EDIT_USER, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    if (params.isParameterPresent(Pages.PARAM_USER_ID)) {
      // user ID is provided
      final User userFromParameters = ParameterUtils.getUserFromParameters(params);
      if (userFromParameters == null) {
        // show error and exit
        baseContentPanel().getUserPanel().clear();
        baseContentPanel().showErrorMessage(ERROR_USER_NOT_FOUND);
        return Result.Done();
      } else {
        // user found, load data
        setTitle(makeTitle("Edit user \"" + userFromParameters.getName() + '\"'));
        pnlUser.setTitle("Edit User");
        pnlUser.load(userFromParameters);
        return Result.Continue();
      }
    } else {

      // new user
      setFocusOnFirstInput(true);
      setTitle(makeTitle(PAGE_TITLE_ADD_USER));
      pnlUser.setTitle("New User");
      return Result.Continue();
    }
  }
}


