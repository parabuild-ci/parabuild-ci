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
import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.EmailField;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.LoginNameField;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Border;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.List;

/**
 * This panel holds components used to reset password screen.
 */
public final class ResetPasswordPanel extends MessagePanel implements Validatable {

  private static final long serialVersionUID = 3827210653915867123L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(ResetPasswordPanel.class); // NOPMD

  public static final String NAME_LOGIN_NAME = "Login name:";
  public static final String NAME_EMAIL = "E-mail:";

  private final CommonField nameField = new LoginNameField(15);  // NOPMD
  private final CommonField emailField = new EmailField();  // NOPMD
  private final Button submitButton = new CommonButton(" Submit ");  // NOPMD
  private final Button cancelButton = new CommonButton(" Cancel ");  // NOPMD
  private final Flow buttonsFlow = new Flow().add(submitButton).add(new Label("   ")).add(cancelButton);  // NOPMD
  private final Flow noteFlow = new Flow().add(new BoldCommonLabel("Note: ")).add(new CommonLabel("When you press \"Submit\" button, your password will be re-set to a random value and e-mailed to you. After logging in using the new password please change it to a password you can remember as soon as possible."));  // NOPMD


  public ResetPasswordPanel() {

    final Panel contentPanel = getUserPanel();
    final GridIterator gridIterator = new GridIterator(contentPanel, 2);

    // appearance
    buttonsFlow.setAlignX(Layout.CENTER);
    noteFlow.setBorder(Border.TOP | Border.BOTTOM, 1, Pages.COLOR_PANEL_BORDER);
    noteFlow.setAlignX(Layout.CENTER);

    // layout
    setTitle("Request To Reset Password");
    gridIterator.addPair(new CommonFieldLabel(NAME_LOGIN_NAME), nameField);
    gridIterator.addPair(new CommonFieldLabel(NAME_EMAIL), emailField);

    // buttons
    gridIterator.add(WebuiUtils.makeHorizontalDivider(15), 2);
    gridIterator.add(noteFlow, 2);
    gridIterator.add(WebuiUtils.makeHorizontalDivider(15), 2);
    gridIterator.add(buttonsFlow, 2); // add below the user content panel
    gridIterator.add(WebuiUtils.makeHorizontalDivider(15), 2);

    // set handler for cancel button
    cancelButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 6534937229867560680L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
        return Tierlet.Result.Done(Pages.PUBLIC_LOGIN);
      }
    });
  }


  public void setSubmitButtonListener(final ButtonPressedListener listener) {
    submitButton.addListener(listener);
  }


  /**
   * Returns name
   */
  public String getName() {
    return nameField.getValue();
  }


  public void setName(final String name) {
    nameField.setValue(name);
  }


  /**
   * Returns e-mail value
   */
  public String getEmail() {
    return emailField.getValue();
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    final List errors = new ArrayList(11);
    // preliminary checks
    InputValidator.validateFieldNotBlank(errors, NAME_LOGIN_NAME, nameField);
    InputValidator.validateFieldNotBlank(errors, NAME_EMAIL, emailField);
    if (errors.isEmpty()) {
      WebuiUtils.validateFieldValidEmail(errors, NAME_EMAIL, emailField);
    }
    // check if user exists
    if (errors.isEmpty()) {
      if (SecurityManager.getInstance().getUserByNameAndEmail(nameField.getValue(), emailField.getValue()) == null) {
        errors.add("Given combination of user name and password does not exist.");
      }
    }
    // return
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }
}
