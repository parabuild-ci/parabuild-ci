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
import org.parabuild.ci.common.PropertyToInputMap;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.util.BuildVersionGenerator;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonCheckBox;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonText;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.List;

/**
 * This panel holds build result configuration settings.
 *
 * @noinspection FieldCanBeLocal
 * @see BuildConfigTabs
 */
public final class ManualStartSettingsPanel extends MessagePanel implements Validatable, Saveable, Loadable {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(ManualStartSettingsPanel.class); // NOPMD
  private static final long serialVersionUID = -6009879325960322446L; // NOPMD

  /**
   * Build ID associated with this panel
   */
  private int buildID = BuildConfig.UNSAVED_ID;

  private final CommonCheckBox cbEnableBuildVersion = new CommonCheckBox(); // NOPMD SingularField
  private final CommonCheckBox cbIncrementIfBroken = new CommonCheckBox(); // NOPMD SingularField
  private final CommonCheckBox cbShowPerforceParameters = new CommonCheckBox(); // NOPMD SingularField
  private final CommonCheckBox cbShowBazaarParameters = new CommonCheckBox(); // NOPMD SingularField
  private final CommonCheckBox cbShowMercurialParameters = new CommonCheckBox(); // NOPMD SingularField
  private final CommonCheckBox cbUserFirstValueAsDefault = new CommonCheckBox(); // NOPMD SingularField
  private final CodeNameDropDown fldVersionCounterIncrementMode = new VersionCounterIncrementModeDropdown(); // NOPMD SingularField
  private final CommonField fldBuildInstructionsURL = new CommonField(300, 100); // NOPMD SingularField
  private final CommonField fldVersionTemplate = new CommonField(100, 60); // NOPMD SingularField
  private final ManualStartSettingsTable tblParameters = new ManualStartSettingsTable("Parameters", StartParameterType.BUILD); // NOPMD SingularField
  private final PropertyToInputMap inputMap = new PropertyToInputMap(false, new BuildAttributeHandler()); // strict map  // NOPMD SingularField
  private final CommonText fldBuildInstructions = new CommonText(100, 5); // NOPMD SingularField


  /**
   * Creates panel without title.
   *
   * @param sourceControl source control ID
   * @param scheduleType  schedule type.
   */
  public ManualStartSettingsPanel(final byte sourceControl, final byte scheduleType) {
    super(false);
    showHeaderDivider(true);

    // layout
    final GridIterator gridIterator = new GridIterator(super.getUserPanel(), 3);
    gridIterator.add(new CommonFieldLabel("Enable version counter: ")).add(cbEnableBuildVersion, 2);
    gridIterator.add(new CommonFieldLabel("Version template: ")).add(fldVersionTemplate, 2);
    gridIterator.add(new CommonFieldLabel("Version  counter  increment: ")).add(fldVersionCounterIncrementMode, 2);
    gridIterator.add(new CommonFieldLabel("Increment if broken: ")).add(cbIncrementIfBroken, 2);
    gridIterator.add(WebuiUtils.makePanelDivider(), 3);
    if (scheduleType == BuildConfig.SCHEDULE_TYPE_MANUAL) {
      if (sourceControl == VersionControlSystem.SCM_PERFORCE) {
        gridIterator.add(new CommonFieldLabel("Show Perforce parameters: ")).add(cbShowPerforceParameters, 2);
        gridIterator.add(WebuiUtils.makePanelDivider(), 3);
      } else if (sourceControl == VersionControlSystem.SCM_BAZAAR) {
        gridIterator.add(new CommonFieldLabel("Show Bazaar parameters: ")).add(cbShowBazaarParameters, 2);
        gridIterator.add(WebuiUtils.makePanelDivider(), 3);
      } else if (sourceControl == VersionControlSystem.SCM_MERCURIAL) {
        gridIterator.add(new CommonFieldLabel("Show Mercurial parameters: ")).add(cbShowMercurialParameters, 2);
        gridIterator.add(WebuiUtils.makePanelDivider(), 3);
      }
    }
    if (SystemConfigurationManagerFactory.getManager().isShowBuildInstructions()) {
      final CommonFieldLabel lbBuildInstructions = new CommonFieldLabel("Build instructions: ");
      lbBuildInstructions.setAlignY(Layout.TOP);
      gridIterator.add(lbBuildInstructions).add(fldBuildInstructions, 2);
      gridIterator.add(new CommonFieldLabel("Build instructions URL: ")).add(fldBuildInstructionsURL, 2);
    }
    gridIterator.add(new CommonFieldLabel("Use first value as default: ")).add(cbUserFirstValueAsDefault, 2);
    gridIterator.add(tblParameters, 3);

    // bind
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.BUILD_INSTRUCTIONS, fldBuildInstructions);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.BUILD_INSTRUCTIONS_URL, fldBuildInstructionsURL);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.ENABLE_VERSION, cbEnableBuildVersion);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.USE_FIRST_PARAMETER_VALUE_AS_DEFAULT, cbUserFirstValueAsDefault);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.VERSION_COUNTER_INCREMENT_IF_BROKEN, cbIncrementIfBroken);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE, fldVersionCounterIncrementMode);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.VERSION_TEMPLATE, fldVersionTemplate);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.SHOW_PERFORCE_PARAMETERS, cbShowPerforceParameters);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.SHOW_BAZAAR_PARAMETERS, cbShowBazaarParameters);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.SHOW_MERCURIAL_PARAMETERS, cbShowMercurialParameters);
  }


  /**
   * Returns build ID.
   *
   * @return build ID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets build ID.
   *
   * @param buildID build IDto set.
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
    tblParameters.setBuildID(buildID);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    final List errors = new ArrayList(1);
    boolean valid = true;

    // validate template
    if (!InputValidator.isBlank(fldVersionTemplate)) {
      try {
        final BuildVersionGenerator buildVersionGenerator = new BuildVersionGenerator();
        buildVersionGenerator.validateTemplate(fldVersionTemplate.getValue());
      } catch (final ValidationException e) {
        errors.add(StringUtils.toString(e));
        valid = false;
      }
    }

    // validate params
    valid = this.tblParameters.validate() && valid;

    if (errors.isEmpty()) {
      return valid;
    }
    showErrorMessage(errors);
    return false;
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should display a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfully
   */
  public boolean save() {
    if (buildID == BuildConfig.UNSAVED_ID) {
      throw new IllegalArgumentException("Build ID can not be uninitialized");
    }
    ConfigurationManager.getInstance().saveBuildAttributes(buildID, inputMap.getUpdatedProperties());
    return tblParameters.save();
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    tblParameters.load(buildConfig);
    inputMap.setProperties(ConfigurationManager.getInstance().getBuildAttributes(buildConfig.getActiveBuildID()));
  }


  public String toString() {
    return "ManualStartSettingsPanel{" +
            "buildID=" + buildID +
            ", cbEnableBuildVersion=" + cbEnableBuildVersion +
            ", cbIncrementIfBroken=" + cbIncrementIfBroken +
            ", cbShowPerforceParameters=" + cbShowPerforceParameters +
            ", cbUserFirstValueAsDefault=" + cbUserFirstValueAsDefault +
            ", fldBuildInstructions=" + fldBuildInstructions +
            ", fldBuildInstructionsURL=" + fldBuildInstructionsURL +
            ", fldVersionCounterIncrementMode=" + fldVersionCounterIncrementMode +
            ", fldVersionTemplate=" + fldVersionTemplate +
            ", inputMap=" + inputMap +
            ", tblParameters=" + tblParameters +
            "} " + super.toString();
  }
}
