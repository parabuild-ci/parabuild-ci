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

import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonMenu;
import org.parabuild.ci.webui.common.CommonPasswordField;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.LoginNameField;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.CheckBox;
import viewtier.ui.Field;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Menu;
import viewtier.ui.MenuSelectedEvent;
import viewtier.ui.MenuSelectedListener;
import viewtier.ui.Panel;
import viewtier.ui.Password;
import viewtier.ui.Tierlet;

/**
 * This panel holds login components used to compose login
 * screens
 */
public final class LoginPanel extends MessagePanel {

  private static final long serialVersionUID = 5897126272843281653L; // NOPMD

  public static final String STR_TITLE = "Enter your name and password to log in";

  private static final String CAPTION_LOGIN_NAME = "Login name: ";
  private static final String CAPTION_REMEMBER_ME = "Remember me: ";
  private static final String CAPTION_PASSWORD = "Password: ";
  private static final String CAPTION_LOGIN = " Login ";
  private static final String CAPTION_CANCEL = " Cancel ";

  public static final String FIELD_NAME_LOGIN_NAME = "login_name";
  public static final String FIELD_NAME_LOGIN_PASSWORD = "login_password";
  private static final String FIELD_NAME_REMEMBER_ME = "login_remember_me";

  private final CheckBox cbRememberMe = new CheckBox();  // NOPMD
  private final Field nameField = new LoginNameField(FIELD_NAME_LOGIN_NAME, 15);  // NOPMD
  private final Password passwordField = new CommonPasswordField();  // NOPMD
  private final Button loginButton = new CommonButton(CAPTION_LOGIN);  // NOPMD
  private final Button cancelButton = new CommonButton(CAPTION_CANCEL);  // NOPMD
  private final Flow buttonsFlow = new Flow().add(loginButton).add(new Label("   ")).add(cancelButton);  // NOPMD
  private final Menu forgotPassword = new CommonMenu("Forgot password?");  // NOPMD


  public LoginPanel() {

    cbRememberMe.setName(FIELD_NAME_REMEMBER_ME);

    final Panel contentPanel = getUserPanel();
    final Panel fieldsPanel = new Panel();
    contentPanel.add(fieldsPanel);
    setWidth(300);

    final GridIterator gridIterator = new GridIterator(fieldsPanel, 2);

    // create
    passwordField.setName(FIELD_NAME_LOGIN_PASSWORD);
    passwordField.setPadding(2);
    buttonsFlow.setAlignX(Layout.CENTER);

    // layout
    setTitle(STR_TITLE);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_LOGIN_NAME), nameField);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PASSWORD), passwordField);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_REMEMBER_ME), cbRememberMe);

    // buttons
    contentPanel.add(WebuiUtils.makeHorizontalDivider(15));
    contentPanel.add(buttonsFlow); // add below the user content panel
    contentPanel.add(WebuiUtils.makeHorizontalDivider(15));

    // forgot password
    forgotPassword.setAlignX(Layout.CENTER);
    forgotPassword.setAlignY(Layout.CENTER);
    forgotPassword.setForeground(Pages.COLOR_COMMON_LINK_FG);
    forgotPassword.setFont(Pages.FONT_COMMON_SMALL);
    contentPanel.add(forgotPassword);
    contentPanel.add(WebuiUtils.makeHorizontalDivider(15));

    // set handler for cancel button
    cancelButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 2265999592345221796L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
        return Tierlet.Result.Done(Pages.PUBLIC_BUILDS);
      }
    });

    // set handler for "forgot password" menu
    forgotPassword.addListener(new MenuSelectedListener() {
      private static final long serialVersionUID = -5690593220907534121L;


      public Tierlet.Result menuSelected(final MenuSelectedEvent event) {
        return Tierlet.Result.Done(Pages.ADMIN_RESET_PASSWORD);
      }
    });
  }


  public String getName() {
    return nameField.getValue();
  }


  public String getPassword() {
    return passwordField.getValue();
  }


  public void setName(final String name) {
    nameField.setValue(name);
  }


  public void setPassword(final String password) {
    passwordField.setValue(password);
  }


  public boolean isRememberMeSelected() {
    return cbRememberMe.isChecked();
  }
}
