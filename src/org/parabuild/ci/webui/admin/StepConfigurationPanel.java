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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;
import viewtier.ui.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * StepConfigurationPanel
 * <p/>
 *
 * @author Slava Imeshev
 * @noinspection FieldCanBeLocal
 * @since Jul 24, 2008 12:52:08 PM
 */
public final class StepConfigurationPanel extends MessagePanel {

  /**
   * Maximum length of the Shell commands field.
   */
  private static final int MAX_COMMANDS_LENGTH = 1024;

  private static final String NAME_FAILURE_PATTERNS = "Failure Patterns";
  private static final String NAME_BUILD_COMMANDS = "Shell Commands";
  private static final String NAME_SUCCESS_PATTERNS = "Success Patterns";

  private final Text scriptText = new Text(110, 5);
  private final Text failurePatternText = new Text(20, 3);
  private final Text successPatternText = new Text(20, 3);
  private final StabilitySettingsPanel pnlStabilitySettings = new StabilitySettingsPanel();
  private final FieldWithButtonPanel pnlBuildCommands = new FieldWithButtonPanel(NAME_BUILD_COMMANDS, scriptText);
  private final FieldWithButtonPanel pnlFailurePattern = new FieldWithButtonPanel(NAME_FAILURE_PATTERNS, failurePatternText);
  private final FieldWithButtonPanel pnlSuccessPatterns = new FieldWithButtonPanel(NAME_SUCCESS_PATTERNS, successPatternText);


  /**
   * Creates a new panel using the default layout manager.
   * The default layout manager for all panels is the
   * <code>FlowLayout</code> class.
   *
   * @param mode
   */
  public StepConfigurationPanel(final byte mode) {
    final boolean editable = mode == WebUIConstants.MODE_EDIT;

    // Create
    scriptText.setAlignY(Layout.TOP);
    scriptText.setEditable(editable);

    failurePatternText.setEditable(editable);
    failurePatternText.setAlignY(Layout.TOP);

    successPatternText.setEditable(editable);
    successPatternText.setAlignY(Layout.TOP);

    pnlStabilitySettings.setAlignY(Layout.TOP);
    pnlStabilitySettings.setEditable(editable);

    pnlBuildCommands.setEditable(editable);
    pnlFailurePattern.setEditable(editable);
    pnlSuccessPatterns.setEditable(editable);

    final Layout layout = new Layout(0, 0, 1, 1);
    layout.spanX = 3;
    add(pnlBuildCommands, layout);

    layout.spanX = 1;

    layout.positionY++;
    add(new TableHeaderLabel(NAME_FAILURE_PATTERNS), layout);
    layout.positionY++;
    add(pnlFailurePattern, layout);

    layout.positionX++;
    layout.positionY--;
    add(new TableHeaderLabel(NAME_SUCCESS_PATTERNS), layout);
    layout.positionY++;
    add(pnlSuccessPatterns, layout);

    layout.positionY--;
    layout.positionX++;
    layout.spanY = 2;
    add(pnlStabilitySettings, layout);
  }


  public void setSuccessPatterns(final String value) {
    successPatternText.setValue(value);
  }


  public void setFailurePatterns(final String value) {
    failurePatternText.setValue(value);
  }


  public String getFailurePatternValue() {
    return failurePatternText.getValue();
  }


  public String getBuildCommandsValue() {
    return scriptText.getValue();
  }


  public String getSuccessPatternValue() {
    return successPatternText.getValue();
  }


  public boolean validate() {
    final List errors = new ArrayList(3);
    final boolean respectErrorsCode = pnlStabilitySettings.isRespectErrorCode();
    if (!respectErrorsCode) {
      // don't allow blanks if error code should not be respected
      WebuiUtils.validateFieldNotBlank(errors, NAME_FAILURE_PATTERNS, failurePatternText);
      WebuiUtils.validateFieldNotBlank(errors, NAME_SUCCESS_PATTERNS, successPatternText);
    }
    WebuiUtils.validateFieldNotBlank(errors, NAME_BUILD_COMMANDS, scriptText);

    // commans length
    if (scriptText.getValue().length() >= MAX_COMMANDS_LENGTH) {
      errors.add("Length of " + NAME_BUILD_COMMANDS + " cannot exeed " + MAX_COMMANDS_LENGTH
              + " characters. Current length is " + scriptText.getValue().length()
              + ". Consider storing build commands as a script file in your version control system.");
    }

    // Validate stability settings
    pnlStabilitySettings.validate(errors);

    if (errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }


  public void setScriptText(final String scriptText) {
    this.scriptText.setValue(scriptText);
  }


  public void setFinalizerVisible(final boolean visible) {
    this.pnlStabilitySettings.setFinalizerVisible(visible);
  }


  public void setInitializerVisible(final boolean visible) {
    this.pnlStabilitySettings.setInitializerVisible(visible);
  }


  public boolean isDisabled() {
    return pnlStabilitySettings.isDisabled();
  }


  public int getTimeout() {
    return pnlStabilitySettings.getTimeout();
  }


  public boolean isRespectErrorCode() {
    return pnlStabilitySettings.isRespectErrorCode();
  }


  public boolean isContinueOnFailure() {
    return pnlStabilitySettings.isContinueOnFailure();
  }


  public boolean isFinalizer() {
    return pnlStabilitySettings.isFinalizer();
  }


  public boolean isInitializer() {
    return pnlStabilitySettings.isInitializer();
  }


  public void setRespectErrorCode(final boolean respectErrorCode) {
    pnlStabilitySettings.setRespectErrorCode(respectErrorCode);
  }


  public void setTimeout(final int timeout) {
    pnlStabilitySettings.setTimeout(timeout);
  }


  public void setFinalizer(final boolean finalizer) {
    pnlStabilitySettings.setFinalizer(finalizer);
  }


  public void setInitializer(final boolean initializer) {
    pnlStabilitySettings.setInitializer(initializer);
  }


  public void setDisabled(final boolean disabled) {
    pnlStabilitySettings.setDisabled(disabled);
  }


  public void setContinueOnFailure(final boolean continueOnFailure) {
    pnlStabilitySettings.setContinueOnFailure(continueOnFailure);
  }


  public String toString() {
    return "StepConfigurationPanel{" +
            "scriptText=" + scriptText +
            ", failurePatternText=" + failurePatternText +
            ", successPatternText=" + successPatternText +
            ", pnlStabilitySettings=" + pnlStabilitySettings +
            '}';
  }
}
