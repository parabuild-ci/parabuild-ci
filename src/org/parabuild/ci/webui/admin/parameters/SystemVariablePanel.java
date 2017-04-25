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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.SaveErrorProcessor;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Panel;

import java.util.ArrayList;
import java.util.List;

final class SystemVariablePanel extends MessagePanel implements Validatable, Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private static final Log LOG = LogFactory.getLog(SystemVariablePanel.class); // NOPMD

  /**
   * @noinspection NumericCastThatLosesPrecision
   */
  public static final byte EDIT_MODE_ADMIN = (byte) 0;

  private static final String CAPTION_SHELL_VARIABLE_NAME = "Shell variable name: ";
  private static final String CAPTION_DESCRIPTION = "Description: ";
  private static final String CAPTION_VALUE = "Value: ";


  private final Label lbShellVariableName = new CommonFieldLabel(CAPTION_SHELL_VARIABLE_NAME); // NOPMD
  private final Label lbDescription = new CommonFieldLabel(CAPTION_DESCRIPTION); // NOPMD
  private final Label lbValue = new CommonFieldLabel(CAPTION_VALUE); // NOPMD

  private final Field flName = new CommonField("shell-var-name", 80, 75); // NOPMD
  private final Field flDescription = new CommonField("shell-var-description", 100, 80); // NOPMD
  private final Field flValue = new CommonField("shell-var-value", 120, 90); // NOPMD


  private int variableID = StartParameter.UNSAVED_ID;
  private final byte variableType;
  private final int variableOnwer;
  private static final String PARABUILD_PREFIX = "PARABUILD_";


  /**
   * Creates message panel without title.
   *
   * @param variableType
   * @param variableOnwer
   * @noinspection UnusedDeclaration
   */
  SystemVariablePanel(final byte variableType, final int variableOnwer) {
    super(true);
    this.variableType = variableType;
    this.variableOnwer = variableOnwer;
    showHeaderDivider(true);
    final Panel cp = getUserPanel();
    cp.setWidth(Pages.PAGE_WIDTH);
    final GridIterator gi = new GridIterator(cp, 2);
    gi.addPair(lbShellVariableName, new RequiredFieldMarker(flName));
    gi.addPair(lbValue, flValue);
    gi.addPair(lbDescription, new RequiredFieldMarker(flDescription));
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Validating configuration");
    }
    // general validation
    final List errors = new ArrayList(1);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_SHELL_VARIABLE_NAME, flName);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_DESCRIPTION, flDescription);


    // Validate variable name
    WebuiUtils.validateVariableName(errors, flName);


    flName.setValue(flName.getValue().trim().toUpperCase());

    // Check prefix
    if (errors.isEmpty() && flValue.getValue().startsWith(PARABUILD_PREFIX)) {
      errors.add("Shell variable name cannot start with " + PARABUILD_PREFIX);
    }

    // Check for duplicates
    if (errors.isEmpty() && variableID == StartParameter.UNSAVED_ID) {
      if (ConfigurationManager.getInstance().findStartParameter(variableType, variableOnwer, flName.getValue()) != null) {
        errors.add("Shell variable already exists: " + flName.getValue());
      }
    }

    // validation failed, show errors
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
    }
    return errors.isEmpty();
  }


  /**
   * Loads given StartParameter.
   *
   * @param startParameter
   */
  public void load(final StartParameter startParameter) {
    variableID = startParameter.getID();
    flName.setValue(startParameter.getName());
    flName.setEditable(false);
    flDescription.setValue(startParameter.getDescription());
    flValue.setValue(startParameter.getValue());
  }


  /**
   * Saves variable data.
   *
   * @return if saved successfuly.
   * @noinspection ReuseOfLocalVariable
   */
  public boolean save() {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Saving variable configuration");
    }
    try {
      // Validate
      if (!validate()) {
        return false;
      }

      // Get startParameter object
      if (LOG.isDebugEnabled()) {
        LOG.debug("variableID: " + variableID);
      }
      final StartParameter startParameter;
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      if (variableID == StartParameter.UNSAVED_ID) {
        // Create startParameter object
        startParameter = new StartParameter();
        startParameter.setBuildID(variableOnwer); // System
        startParameter.setType(variableType);
        startParameter.setPresentation(StartParameter.PRESENTATION_SINGLE_VALUE);
        startParameter.setEnabled(true);
      } else {
        startParameter = cm.getStartParameter(variableID);
      }

      // Cover-ass check - if the startParameter is there
      if (startParameter == null) {
        showErrorMessage("Start variable being edited not found. Please cancel editing and try again.");
        return false;
      }

      // Set startParameter data
      final String value = flValue.getValue().trim();
      startParameter.setDescription(flDescription.getValue());
      startParameter.setModifiable(false);
      startParameter.setName(flName.getValue().trim().toUpperCase());
      startParameter.setValue(value);
      startParameter.setRuntimeValue(value);
      startParameter.setRequired(true);

      // Save startParameter object
      cm.save(startParameter);

      return true;
    } catch (Exception e) {
      final SaveErrorProcessor exceptionProcessor = new SaveErrorProcessor();
      return exceptionProcessor.process(this, e);
    }
  }


  /**
   * @return loaded variable ID
   */
  public int getParameterID() {
    return variableID;
  }


  public String toString() {
    return "SystemVariablePanel{" +
            "lbShellVariableName=" + lbShellVariableName +
            ", lbDescription=" + lbDescription +
            ", lbValue=" + lbValue +
            ", flName=" + flName +
            ", flDescription=" + flDescription +
            ", flValue=" + flValue +
            ", variableID=" + variableID +
            ", variableType=" + variableType +
            ", variableOnwer=" + variableOnwer +
            '}';
  }
}