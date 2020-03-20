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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.BuildStartRequestParameter;
import org.parabuild.ci.util.BuildVersionGenerator;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Border;
import viewtier.ui.CheckBox;
import viewtier.ui.Color;
import viewtier.ui.Label;
import viewtier.ui.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * This panel holds a table that allows to enter build run
 * parameters and manual label.
 */
public final class ManualStartParametersPanel extends MessagePanel implements Validatable {

  private static final long serialVersionUID = -194369566029085673L; // NOPMD

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ManualStartParametersPanel.class); //NOPMD

  private static final String CAPTION_BUILD_COUNTER = "Version counter: ";
  private static final String CAPTION_CLEAR_WORKSPACE = "Clear workspace: ";
  private static final String CAPTION_LABEL = "Label: ";
  private static final String CAPTION_NOTE = "Note: ";
  private static final String CAPTION_PIN_BUILD_RESULTS = "Pin build results: ";
  private static final String CAPTION_START_IF_BUILDING = "Start if building: ";
  private static final String CAPTION_VERSION_TEMPLATE = "Version template: ";
  private static final String CAPTION_SKIP_NEXT_SCHEDULED_BUILD = "Skip next scheduled build: ";
  private static final String CAPTION_PREFERRED_BUILD_SERVER = "Preferred build server: ";

  private final Label lbBuildCounter = new CommonFieldLabel(CAPTION_BUILD_COUNTER); // NOPMD SingularField
  private final Label lbClearWorkspace = new CommonFieldLabel(CAPTION_CLEAR_WORKSPACE); // NOPMD SingularField
  private final Label lbLabel = new CommonFieldLabel(CAPTION_LABEL); // NOPMD SingularField
  private final Label lbNextCounterValue = new CommonLabel(""); // NOPMD SingularField
  private final Label lbNote = new CommonFieldLabel(CAPTION_NOTE); // NOPMD SingularField
  private final Label lbPinResults = new CommonFieldLabel(CAPTION_PIN_BUILD_RESULTS); // NOPMD SingularField
  private final Label lbStartIfBuilding = new CommonFieldLabel(CAPTION_START_IF_BUILDING);
  private final Label lbVersionTemplate = new CommonFieldLabel(CAPTION_VERSION_TEMPLATE); // NOPMD SingularField
  private final Label lbSkipNextScheduledBuild = new CommonFieldLabel(CAPTION_SKIP_NEXT_SCHEDULED_BUILD); // NOPMD SingularField
  private final Label lbPreferredBuildServer = new CommonFieldLabel(CAPTION_PREFERRED_BUILD_SERVER); // NOPMD SingularField

  private final EditManualStartParametersTable parametersTable;  // NOPMD SingularField
  private final CommonField flLabel = new CommonField(100, 50); // NOPMD SingularField
  private final CommonField flNote = new CommonField(100, 70); // NOPMD SingularField
  private final CommonField flVersionCounter = new CommonField(6, 6); // NOPMD SingularField
  private final CommonField flVersionTemplate = new CommonField(100, 60); // NOPMD SingularField
  private final CheckBox cbClearWorkspace = new CheckBox(); // NOPMD SingularField
  private final CheckBox cbPinResult = new CheckBox(); // NOPMD SingularField
  private final CheckBox cbStartIfBuilding = new CheckBox(); // NOPMD SingularField
  private final CheckBox cbSkipNextScheduledBuild = new CheckBox(); // NOPMD SingularField
  private final CommonFlow flwBuildCounter = new CommonFlow(flVersionCounter, lbNextCounterValue); // NOPMD SingularField
  private final ManualScheduleStartParametersPanel pnlManualScheduleStartParameters; // NOPMD SingularField
  private final BuildFarmAgentDropDown ddPreferredAgent;

  private final int buildID;


  /**
   */
  public ManualStartParametersPanel(final int buildID, final boolean scheduledBuild, final StartParameterType type, final byte editMode) {
    super.showContentBorder(false);

    this.buildID = buildID;

    final ConfigurationManager cm = ConfigurationManager.getInstance();

    //
    final List parameterDefinitions = getEditableParameterDefinitions(buildID, type, cm);
    final boolean userFirstParameterValueAsDefault = editMode == WebUIConstants.MODE_EDIT && cm.getBuildAttributeValue(
            buildID, BuildConfigAttribute.USE_FIRST_PARAMETER_VALUE_AS_DEFAULT, BuildConfigAttribute.OPTION_UNCHECKED)
            .equals(BuildConfigAttribute.OPTION_UNCHECKED);
    parametersTable = new EditManualStartParametersTable(parameterDefinitions, userFirstParameterValueAsDefault, editMode);

    // NOTE: vimeshev - 2006-08-17 - Make version template R/O so
    // that we don't have to care about saving it. Later we may
    // decide to make it changeable at the startup and make it
    // writable.
    flVersionTemplate.setEditable(false);

    // align
    final GridIterator gi = new GridIterator(getUserPanel(), 2);

    // Add instructions
    if (SystemConfigurationManagerFactory.getManager().isShowBuildInstructions()) {
      final String instructions = cm.getBuildAttributeValue(buildID, BuildConfigAttribute.BUILD_INSTRUCTIONS, (String) null);
      if (!StringUtils.isBlank(instructions)) {
        final Text lbInstructionsValue = new Text(120, 20);
        lbInstructionsValue.setValue(instructions);
        lbInstructionsValue.setEditable(false);
        lbInstructionsValue.setBorder(Border.ALL, 1, Color.Brown);
        gi.add(lbInstructionsValue, 2);
      }
      final String instructionsURL = cm.getBuildAttributeValue(buildID, BuildConfigAttribute.BUILD_INSTRUCTIONS_URL, (String) null);
      if (!StringUtils.isBlank(instructionsURL)) {
        final CommonLink lnkInstructionsURL = new CommonLink(instructionsURL, instructionsURL);
        lnkInstructionsURL.setTarget("_blank");
        gi.add(new CommonFlow(new CommonLabel("More build instructions: "), lnkInstructionsURL), 2);
      }
      gi.add(WebuiUtils.makePanelDivider(), 2);
    }

    // add param table
    if (!parameterDefinitions.isEmpty()) {
      gi.add(parametersTable, 2);
      gi.add(WebuiUtils.makePanelDivider(), 2);
    }

    // add a panel for manual schedule. if the schedule is not
    // manual the panel is not visible - factory takes care about
    // it.
    pnlManualScheduleStartParameters = ManualScheduleStartParametersPanelFactory.makePanel(buildID);
    pnlManualScheduleStartParameters.setEditMode(editMode);
    gi.add(pnlManualScheduleStartParameters, 2);
    if (pnlManualScheduleStartParameters.isVisible()) {
      gi.add(WebuiUtils.makePanelDivider(), 2);
    }

    // add note, label, pin result along with a border
    final MessagePanel pnlBorder = new MessagePanel(true);
    pnlBorder.showHeaderDivider(true);
    pnlBorder.setWidth(Pages.PAGE_WIDTH);
    gi.add(pnlBorder, 2);

    final GridIterator giBorder = new GridIterator(pnlBorder.getUserPanel(), 2);

    // add version template
    giBorder.addPair(lbVersionTemplate, flVersionTemplate);
    setVersionTemplateVisible(false);

    // add build counter
    giBorder.addPair(lbBuildCounter, flwBuildCounter);
    setVersionCounterVisible(false);

    // add build request note
    giBorder.addPair(lbNote, flNote);

    // add label field
    giBorder.addPair(lbLabel, flLabel);

    // add start if building
    giBorder.addPair(lbStartIfBuilding, cbStartIfBuilding);
    if (SystemConfigurationManagerFactory.getManager().isSerializedBuilds()) {
      // disallow to start by default
      cbStartIfBuilding.setChecked(false);
    } else {
      WebuiUtils.hideCaptionAndFieldIfBlank(lbStartIfBuilding, cbStartIfBuilding);
      // allow to start
      cbStartIfBuilding.setChecked(true);
    }

    // add pin request
    giBorder.addPair(lbClearWorkspace, cbClearWorkspace);
    giBorder.addPair(lbPinResults, cbPinResult);

    // Add controls for skipping a next scheduled build
    if (scheduledBuild) {

      giBorder.addPair(lbSkipNextScheduledBuild, cbSkipNextScheduledBuild);
    }

    //
    final int activeBuildID = cm.getActiveIDFromBuildID(buildID);
    final ActiveBuildConfig activeBuildConfig = cm.getActiveBuildConfig(activeBuildID);
    ddPreferredAgent = new BuildFarmAgentDropDown(activeBuildConfig.getBuilderID());
    giBorder.addPair(lbPreferredBuildServer, ddPreferredAgent);

    // adjust editability
    if (editMode == WebUIConstants.MODE_VIEW) {
      WebuiUtils.hideCaptionAndFieldIfBlank(lbStartIfBuilding, cbStartIfBuilding);
      flLabel.setEditable(false);
      flNote.setEditable(false);
      cbClearWorkspace.setEditable(false);
      cbPinResult.setEditable(false);
      flVersionTemplate.setEditable(false);
      flVersionCounter.setEditable(false);
      cbSkipNextScheduledBuild.setEditable(false);
      ddPreferredAgent.setEditable(false);
      ddPreferredAgent.setVisible(false);
      lbPreferredBuildServer.setVisible(false);
      if (InputValidator.isBlank(flLabel)) {
        lbLabel.setVisible(false);
        flLabel.setVisible(false);
      }
      if (InputValidator.isBlank(flNote)) {
        lbNote.setVisible(false);
        flNote.setVisible(false);
      }
    }

    //
    // load
    //
    if (cm.getBuildAttributeValue(buildID, BuildConfigAttribute.ENABLE_VERSION, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED)) {
      final Integer currentVersionCounter = cm.getActiveBuildAttributeValue(buildID, ActiveBuildAttribute.VERSION_COUNTER_SEQUENCE);
      final byte counterIncrementMode = cm.getBuildAttributeValue(buildID, BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE, new Integer(BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE_MANUAL)).byteValue();
      setVersionTemplate(cm.getBuildAttributeValue(buildID, BuildConfigAttribute.VERSION_TEMPLATE, ""));
      setVersionCounterNote(currentVersionCounter, counterIncrementMode);
    }
    pnlManualScheduleStartParameters.load(buildID); // knows build ID
  }


  /**
   * Sets value of version template field. If value is blank
   * hides the field and the value.
   *
   * @param versionTemplate value to set.
   */
  public void setVersionTemplate(final String versionTemplate) {
    flVersionTemplate.setValue(versionTemplate);
    setVersionTemplateVisible(!StringUtils.isBlank(versionTemplate));
    setVersionCounterVisible(!StringUtils.isBlank(versionTemplate));
  }


  /**
   * Sets value of build counter field. If value is blank
   * hides the field and the value.
   *
   * @param buildCounter value to set.
   */
  public void setVersionCounter(final int buildCounter) {
    flVersionCounter.setValue(Integer.toString(buildCounter));
    setVersionCounterVisible(true);
  }


  /**
   * Helper method to set visibility of the build counter label
   * and field.
   */
  private void setVersionCounterVisible(final boolean b) {
    lbBuildCounter.setVisible(b);
    flwBuildCounter.setVisible(b);
  }


  /**
   * Helper method to set visibility of the version template label
   * and field.
   */
  private void setVersionTemplateVisible(final boolean b) {
    lbVersionTemplate.setVisible(b);
    flVersionTemplate.setVisible(b);
  }


  /**
   * @return value entered in the label field.
   */
  public String getLabel() {
    return flLabel.getValue();
  }


  /**
   * @return value entered in the note field.
   */
  public String getNote() {
    return flNote.getValue();
  }


  /**
   * Returns an ID of a preferred agent.
   *
   * @return an ID of a preferred agent.
   */
  public AgentHost getDesiredAgentHost() {

    final int agentID = ddPreferredAgent.getCode();
    if (agentID == AgentConfig.UNSAVED_ID) {
      return null;
    }

    return new AgentHost(ddPreferredAgent.getValue());
  }


  /**
   * @return true if pinning of build results was requested.
   */
  public boolean isPinResult() {
    return cbPinResult.isChecked();
  }


  /**
   * @return true if clean builds was requested.
   */
  public boolean isClearWorkspace() {
    return cbClearWorkspace.isChecked();
  }


  /**
   * Returns true if this request requires starting a build even if the serialization is enabled.
   *
   * @return true if this request requires starting a build even if the serialization is enabled.
   */
  public boolean isStartIfBuilding() {
    return cbStartIfBuilding.isChecked();
  }


  /**
   * Returns <code>true</code> if next scheduled build should be skipped.
   *
   * @return <code>true</code> if next scheduled build should be skipped.
   */
  public boolean isSkipNextScheduledBuild() {

    return cbSkipNextScheduledBuild.isChecked();
  }


  /**
   * @return edited List of {@link BuildStartRequestParameter}
   */
  public List getStartParameterList() {
    return parametersTable.getUpdatedParameterList();
  }


  /**
   * @return true if input is valid
   */
  public boolean validate() {
    // TODO: add validating that a label does not exist
    final List errors = new ArrayList(5);

    // validate if the build allowed to start manually
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    if (scm.isSerializedBuilds() && !cbStartIfBuilding.isChecked() && !scm.isQueueManualStartRequests()) {
      final int activeBuildID = ConfigurationManager.getInstance().getActiveIDFromBuildID(buildID);
      if (BuildManager.getInstance().getFreeAgentHosts(activeBuildID).isEmpty()) {
        errors.add("Cannot start. Build serialization is enabled and another build is running. " +
                "Check box \"" + CAPTION_START_IF_BUILDING + "\" to force build start.");
      }
    }

    // validate counter integer format
    if (!InputValidator.isBlank(flVersionCounter)) {
      InputValidator.validateFieldValidNonNegativeInteger(errors, CAPTION_BUILD_COUNTER, flVersionCounter);
    }

    // validate template
    if (!InputValidator.isBlank(flVersionTemplate)) {
      try {
        final BuildVersionGenerator buildVersionGenerator = new BuildVersionGenerator();
        buildVersionGenerator.validateTemplate(flVersionTemplate.getValue());
      } catch (final ValidationException e) {
        errors.add(StringUtils.toString(e));
      }
    }

    if (!errors.isEmpty()) {
      showErrorMessage(errors);
    }
    return errors.isEmpty() && parametersTable.validate();
  }


  /**
   * Returns version template value.
   */
  public String getVersionTemplate() {
    return flVersionTemplate.getValue();
  }


  /**
   * @return value of the build counter field. If not set returns -1
   */
  public int getVersionCounter() {
    if (InputValidator.isBlank(flVersionCounter)) {
      return -1;
    }
    return Integer.parseInt(flVersionCounter.getValue());
  }


  public void setVersionCounterNote(final int value, final byte counterIncrementMode) {
    if (counterIncrementMode == BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE_AUTOMATIC) {
      lbNextCounterValue.setText(" If not set, the following value will be assigned automatically (as checked last time): " + (value + 1));
    } else if (counterIncrementMode == BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE_MANUAL) {
      lbNextCounterValue.setText(" Set counter manually if needed. Last time it was set to: " + value);
    } else {
      ErrorManagerFactory.getErrorManager().reportSystemError(new Error("Error while setting version counter note: unknown counter increment mode " + counterIncrementMode));
    }
    lbNextCounterValue.setVisible(true);
  }


  public List getManualScheduleParameters() {
    return pnlManualScheduleStartParameters.getUpdatedSettings();
  }


  private static List getEditableParameterDefinitions(final int buildID, final StartParameterType type, final ConfigurationManager cm) {
    final List startParameters = cm.getStartParameters(type, buildID);
    final List result = new ArrayList(startParameters.size());
    for (int i = 0; i < startParameters.size(); i++) {
      final StartParameter startParameter = (StartParameter) startParameters.get(i);
      if (startParameter.isModifiable()) {
        result.add(startParameter);
      }
    }
    return result;
  }


  public String toString() {
    return "ManualStartParametersPanel{" +
            "lbBuildCounter=" + lbBuildCounter +
            ", lbLabel=" + lbLabel +
            ", lbNextCounterValue=" + lbNextCounterValue +
            ", lbNote=" + lbNote +
            ", lbPinResults=" + lbPinResults +
            ", lbStartIfBuilding=" + lbStartIfBuilding +
            ", lbVersionTemplate=" + lbVersionTemplate +
            ", parametersTable=" + parametersTable +
            ", flLabel=" + flLabel +
            ", flNote=" + flNote +
            ", flVersionCounter=" + flVersionCounter +
            ", flVersionTemplate=" + flVersionTemplate +
            ", cbPinResult=" + cbPinResult +
            ", cbStartIfBuilding=" + cbStartIfBuilding +
            ", flwBuildCounter=" + flwBuildCounter +
            ", pnlManualScheduleStartParameters=" + pnlManualScheduleStartParameters +
            '}';
  }
}

