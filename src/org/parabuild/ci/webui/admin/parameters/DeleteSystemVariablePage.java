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

import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.util.StringUtils;
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
import viewtier.ui.Color;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is repsonsible for editing Parabuild system
 * properties.
 */
public final class DeleteSystemVariablePage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L; // NOPMD

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeleteSystemVariablePage() {
    setTitle(makeTitle("Delete Variable"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters parameters) {
    try {

      // authenticate
      if (!isValidUser()) {
        return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
                Pages.PUBLIC_LOGIN, Pages.PAGE_VARIABLE_DELETE, parameters);
      }

      if (!isValidAdminUser()) {
        return WebuiUtils.showNotAuthorized(this);
      }

      // check if variable exists, show error message if not
      final StartParameter startParameter = ParameterUtils.getStartParameterFromParameters(parameters);
      if (startParameter == null) {
        baseContentPanel().showErrorMessage("Requested variable not found.");
        baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.PAGE_VARIABLE_LIST));
        return Result.Done();
      }

      // Get Parameters
      final byte variableType = SystemVariableUtils.getValidType(parameters);
      final int variableOwner = SystemVariableUtils.getValidOwner(variableType, parameters);


      setTitle(makeTitle("Delete Variable >> " + startParameter.getName()));

      if (isNew()) {
        // Display form and hand control
        final MessagePanel deleteParameterPanel = makeConfirmDeletePanel(startParameter.getName());
        baseContentPanel().getUserPanel().add(deleteParameterPanel);
        return Result.Continue();
      } else {
        if (action == ACTION_CANCEL) {
          return Result.Done(Pages.PAGE_VARIABLE_LIST);
        } else if (action == ACTION_DELETE) {
          // delete startParameter
          ConfigurationManager.getInstance().deleteObject(startParameter);
          SystemVariableUtils.notifyConfigurationChanged(variableType, variableOwner);
          return SystemVariableUtils.createReturnToVariableList(variableType, variableOwner);
        }
        return Result.Continue();
      }
    } catch (final ValidationException ve) {
      return showPageErrorAndExit(StringUtils.toString(ve));
    }
  }


  private MessagePanel makeConfirmDeletePanel(final String parameterName) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to delete variable \"" + parameterName + "\". Press \"Delete\" button to confirm.");
    confirmationRequestLabel.setHeight(30);
    confirmationRequestLabel.setAlignY(Layout.CENTER);
    confirmationRequestLabel.setForeground(Color.DarkRed);

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
      private static final long serialVersionUID = -2621840056808212572L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -6922123329335297969L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }


  public String toString() {
    return "DeleteSystemVariablePage{" +
            "action=" + action +
            '}';
  }
}