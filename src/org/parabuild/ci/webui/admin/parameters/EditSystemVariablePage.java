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
package org.parabuild.ci.webui.admin.parameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.ButtonSeparator;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.SaveButton;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is responsible for creating/editting variable.
 */
public final class EditSystemVariablePage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(EditSystemVariablePage.class); // NOPMD

  public static final String PAGE_TITLE_DEFAULT = "Manage Variable";
  public static final String PAGE_TITLE_ADD = "Add New Variable";
  public static final String ERROR_NOT_FOUND = "Requested variable not found";


  /**
   * Constructor.
   */
  public EditSystemVariablePage() {
    // layout
    setTitle(makeTitle(PAGE_TITLE_DEFAULT)); // default title

  }


  /**
   * Strategy method derived from BasePage.
   *
   * @param parameters
   */
  public Result executePage(final Parameters parameters) {
    try {
      // Authenticate
      if (!isValidUser()) {
        return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
                Pages.PUBLIC_LOGIN, Pages.PAGE_VARIABLE_EDIT, parameters);
      }

      if (!isValidAdminUser()) {
        return WebuiUtils.showNotAuthorized(this);
      }

      // Get Parameters
      final byte variableType = SystemVariableUtils.getValidType(parameters);
      final int variableOwner = SystemVariableUtils.getValidOwner(variableType, parameters);

      if (isNew()) {
        final SystemVariablePanel pnlVariable = new SystemVariablePanel(variableType, variableOwner);  // NOPMD
        final SaveButton btnSave = new SaveButton();  // NOPMD
        final CancelButton btnCancel = new CancelButton();  // NOPMD
        final Flow flwSaveCancel = new Flow().add(btnSave).add(new ButtonSeparator()).add(btnCancel);  // NOPMD
        flwSaveCancel.setAlignX(Layout.CENTER);
        flwSaveCancel.setBackground(Pages.COLOR_PANEL_HEADER_BG);

        baseContentPanel().getUserPanel().add(pnlVariable);
        baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
        baseContentPanel().getUserPanel().add(flwSaveCancel);

        // Add listeners
        btnCancel.addListener(new CancelButtonPressedListener(variableType, variableOwner));
        btnSave.addListener(new SaveButtonPressedListener(variableType, variableOwner, pnlVariable));

        if (parameters.isParameterPresent(Pages.PARAM_VARIABLE_ID)) {

          // startParameter cofiguration ID is provided
          final StartParameter startParameter = ParameterUtils.getStartParameterFromParameters(parameters);
          if (startParameter == null) {
            // show error and exit
            baseContentPanel().getUserPanel().clear();
            baseContentPanel().showErrorMessage(ERROR_NOT_FOUND);
            return Result.Done();
          } else {
            // startParameter configuration found, load data
            setTitle(makeTitle("Edit Variable \"" + startParameter.getName() + '\"'));
            pnlVariable.setTitle("Edit Variable");
            pnlVariable.load(startParameter);
          }

        } else {

          // New variable
          setFocusOnFirstInput(true);
          setTitle(makeTitle(PAGE_TITLE_ADD));
          pnlVariable.setTitle("New Variable");
        }
      }
      return Result.Continue();
    } catch (final ValidationException ve) {
      return showPageErrorAndExit(StringUtils.toString(ve));
    }
  }


  private static class CancelButtonPressedListener implements ButtonPressedListener {

    private final byte variableType;
    private final int variableOwner;


    public CancelButtonPressedListener(final byte variableType, final int variableOwner) {
      //To change body of created methods use File | Settings | File Templates.
      this.variableType = variableType;
      this.variableOwner = variableOwner;
    }


    public Result buttonPressed(final ButtonPressedEvent event) {
      return SystemVariableUtils.createReturnToVariableList(variableType, variableOwner);
    }


    public String toString() {
      return "CancelButtonPressedListener{" +
              "variableType=" + variableType +
              ", variableOwner=" + variableOwner +
              '}';
    }
  }

  private static class SaveButtonPressedListener implements ButtonPressedListener {

    private final SystemVariablePanel pnlVariable;
    private byte variableType;
    private int variableOwner;


    public SaveButtonPressedListener(final byte variableType, final int variableOwner, final SystemVariablePanel pnlVariable) {
      this.pnlVariable = pnlVariable;
      this.variableType = variableType;
      this.variableOwner = variableOwner;
    }


    public Result buttonPressed(final ButtonPressedEvent event) {
      if (pnlVariable.save()) {
        // Launch async notification about changes
        SystemVariableUtils.notifyConfigurationChanged(variableType, variableOwner);
        // Return result
        return SystemVariableUtils.createReturnToVariableList(variableType, variableOwner);
      } else {
        return Result.Continue();
      }
    }


    public String toString() {
      return "SaveButtonPressedListener{" +
              ", variableType=" + variableType +
              ", variableOwner=" + variableOwner +
              '}';
    }
  }
}