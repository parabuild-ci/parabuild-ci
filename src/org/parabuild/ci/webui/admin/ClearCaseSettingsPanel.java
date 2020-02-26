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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.remote.NoLiveAgentsException;
import org.parabuild.ci.versioncontrol.ClearCaseStorageNameGenerator;
import org.parabuild.ci.versioncontrol.ClearCaseViewNameGenerator;
import org.parabuild.ci.versioncontrol.clearcase.ClearCaseStartDate;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.DropDown;
import viewtier.ui.Field;
import viewtier.ui.Layout;
import viewtier.ui.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class ClearCaseSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  public static final int MAX_CLIENT_VIEW_LENGTH = 32552;
  public static final String MAX_CLIENT_VIEW_LENGTH_STRING = Integer.toString(MAX_CLIENT_VIEW_LENGTH);

  private static final String NAME_CLIENT_NAME_TEMPLATE = "View name template:";
  private static final String NAME_PATH_TO_CLEARTOOL = "Path to cleartool executable:";
  private static final String NAME_REL_BUILD_DIR = "Relative build dir: ";
  private static final String NAME_VIEW_CONFIG_SPEC = "Snapshot view config spec:";
  private static final String NAME_CHANGE_WINDOW = "Change window, secs:";
  private static final String NAME_BRANCH = "Branch:";
  private static final String NAME_TEXT_MODE = "Text mode (-tmode):";
  private static final String NAME_STORAGE_LOCATION = "View storage:";
  private static final String NAME_IGNORE_LINES = "Ignore error lines:";
  private static final String NAME_START_DATE = "Detect changes from:";

  private final Field flViewNameTemplate = new Field(60, 60); // NOPMD
  private final Field flPathToCleartool = new Field(200, 60); // NOPMD
  private final Field flViewStorageLocation = new Field(200, 60); // NOPMD
  private final Field flRelativeBuildDir = new Field(60, 60); // NOPMD
  private final Field flBranch = new Field(200, 60); // NOPMD
  private final Text flViewConfigSpec = new Text(100, 5); // NOPMD
  private final Text flIgnoreErrorLines = new Text(60, 3); // NOPMD
  private final Field flChangeWindow = new Field(2, 3); // NOPMD
  private final Field flStartDate = new Field(10, 10); // NOPMD
  private final DropDown ddTextMode = new ClearCaseTextModeDropDown(); // NOPMD
  private final DropDown ddStorage = new ClearCaseStorageLocationDropDown(); // NOPMD


  public ClearCaseSettingsPanel() {
    super("ClearCase Settings");

    final CommonFieldLabel lbViewConfigSpec = new CommonFieldLabel(NAME_VIEW_CONFIG_SPEC);
    final CommonFieldLabel lbIgnoreErrorLines = new CommonFieldLabel(NAME_IGNORE_LINES);

    // layout
    gridIterator.addPair(new CommonFieldLabel(NAME_PATH_TO_CLEARTOOL), new RequiredFieldMarker(flPathToCleartool));
    gridIterator.addPair(lbViewConfigSpec, new RequiredFieldMarker(flViewConfigSpec));
    gridIterator.addPair(new CommonFieldLabel(NAME_BRANCH), flBranch);
    gridIterator.addPair(new CommonFieldLabel(NAME_TEXT_MODE), ddTextMode);
    gridIterator.addPair(new CommonFieldLabel(NAME_REL_BUILD_DIR), new RequiredFieldMarker(flRelativeBuildDir));
    gridIterator.addPair(new CommonFieldLabel(NAME_CHANGE_WINDOW), new RequiredFieldMarker(flChangeWindow));
    gridIterator.addPair(new CommonFieldLabel(NAME_START_DATE), flStartDate);

    // layout optional
    final boolean advancedSelected = SystemConfigurationManagerFactory.getManager().isAdvancedConfigurationMode();
    if (advancedSelected) {
      gridIterator.addPair(new CommonFieldLabel(NAME_CLIENT_NAME_TEMPLATE), flViewNameTemplate);
      gridIterator.addPair(new CommonFieldLabel(NAME_STORAGE_LOCATION), new CommonFlow(ddStorage, flViewStorageLocation));
      gridIterator.addPair(lbIgnoreErrorLines, flIgnoreErrorLines);
    }

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_VIEW_CONFIG_SPEC, flViewConfigSpec);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_PATH_TO_EXE, flPathToCleartool);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_RELATIVE_BUILD_DIR, flRelativeBuildDir);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_VIEW_NAME_TEMPLATE, flViewNameTemplate);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_CHANGE_WINDOW, flChangeWindow);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_START_DATE, flStartDate);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_TEXT_MODE, ddTextMode);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_BRANCH, flBranch);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION_CODE, ddStorage);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION, flViewStorageLocation);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.CLEARCASE_IGNORE_LINES, flIgnoreErrorLines);

    // add footer
    addCommonAttributes();

    lbViewConfigSpec.setAlignY(Layout.TOP);
    lbIgnoreErrorLines.setAlignY(Layout.TOP);
    flViewConfigSpec.setAlignY(Layout.TOP);
    flChangeWindow.setValue("0");
    flStartDate.setValue(new ClearCaseStartDate().getValue());
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  protected void doSetMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      setEditable(true);
    } else if (mode == WebUIConstants.MODE_INHERITED) {
      setEditable(false); // disable all
      flPathToCleartool.setEditable(true); // but enable this
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flViewConfigSpec.setEditable(editable);
    flPathToCleartool.setEditable(editable);
    flRelativeBuildDir.setEditable(editable);
    flViewNameTemplate.setEditable(editable);
    flChangeWindow.setEditable(editable);
    flBranch.setEditable(editable);
    ddTextMode.setEditable(editable);
    ddStorage.setEditable(editable);
    flViewStorageLocation.setEditable(editable);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  protected boolean doValidate() {
    clearMessage();
    final List errors = new ArrayList(1);
    WebuiUtils.validateFieldNotBlank(errors, NAME_PATH_TO_CLEARTOOL, flPathToCleartool);
    WebuiUtils.validateFieldNotBlank(errors, NAME_VIEW_CONFIG_SPEC, flViewConfigSpec);
    WebuiUtils.validateFieldNotBlank(errors, NAME_REL_BUILD_DIR, flRelativeBuildDir);
    WebuiUtils.validateFieldValidNonNegativeInteger(errors, NAME_CHANGE_WINDOW, flChangeWindow);

    // realive build dir is a valid relative dir name
    try {
      if (errors.isEmpty() && getAgentEnv() != null && getAgentEnv().isAbsoluteFile(flRelativeBuildDir.getValue())) {
        errors.add("Content of \"" + NAME_REL_BUILD_DIR + "\" is an absolute path but it should be a relative path.");
      }
    } catch (final IOException e) {
      IoUtils.ignoreExpectedException(e);
    } catch (final AgentFailureException e) {
      errors.add("Validation error: " + StringUtils.toString(e));
    }

    // depot path length
    final String depotPath = flViewConfigSpec.getValue();
    if (errors.isEmpty() && depotPath.length() > MAX_CLIENT_VIEW_LENGTH) {
      errors.add('\"' + NAME_VIEW_CONFIG_SPEC + "\" is too long. The maximum allowed length is " + MAX_CLIENT_VIEW_LENGTH_STRING + '.');
    }

    // validate ClearCase client exists if there were no other errors
    try {
      WebuiUtils.validateCommandExists(super.getAgentEnv(), flPathToCleartool.getValue(), errors,
              "Path to cleartool executable is invalid, or cleartool executable is not accessible");
    } catch (final NoLiveAgentsException ignore) {
      IoUtils.ignoreExpectedException(ignore);
    } catch (final IOException e) {
      errors.add("Error while checking path for ClearCase client: " + StringUtils.toString(e));
    }

    // validate client name template
    if (!WebuiUtils.isBlank(flViewNameTemplate)) {
      final ClearCaseViewNameGenerator viewNameGenerator = new ClearCaseViewNameGenerator("test_name", 1, flViewNameTemplate.getValue());
      if (!viewNameGenerator.isTemplateValid()) {
        errors.add("Client name template is not valid. Client name template should contain ${build.id} and may contain ${cc.user}.");
      }
    }

    // validate storage path
    if (!WebuiUtils.isBlank(flViewStorageLocation)) {
      final ClearCaseStorageNameGenerator storageNameGenerator = new ClearCaseStorageNameGenerator(1, flViewStorageLocation.getValue());
      if (!storageNameGenerator.isTemplateValid()) {
        errors.add("View storage is not valid. View storage should contain a UNC path and an optional template parameter ${build.id}.");
      }
    }

    // Validate start date
    if (!WebuiUtils.isBlank(flStartDate)) {
      try {
        ClearCaseStartDate.parse(flStartDate.getValue());
      } catch (final Exception e) {
        errors.add("Field \"" + NAME_START_DATE + "\" is in an invalid format. The valid format is yyyy-MM-dd where yyyy " +
                "is a year, MM is a month and dd is a day. Example: 2008-12-31");
      }
    }

    // show errors
    if (errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }


  public String toString() {
    return "ClearCaseSettingsPanel{" +
            "flViewNameTemplate=" + flViewNameTemplate +
            ", flPathToCleartool=" + flPathToCleartool +
            ", flViewStorageLocation=" + flViewStorageLocation +
            ", flRelativeBuildDir=" + flRelativeBuildDir +
            ", flBranch=" + flBranch +
            ", flViewConfigSpec=" + flViewConfigSpec +
            ", flIgnoreErrorLines=" + flIgnoreErrorLines +
            ", flChangeWindow=" + flChangeWindow +
            ", flStartDate=" + flStartDate +
            ", ddTextMode=" + ddTextMode +
            ", ddStorage=" + ddStorage +
            '}';
  }
}
