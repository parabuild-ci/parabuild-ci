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
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.versioncontrol.VersionControlSystem;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.WebUIConstants;
import viewtier.ui.DropDownSelectedEvent;
import viewtier.ui.DropDownSelectedListener;
import viewtier.ui.Panel;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class InheritedSourceControlPanel extends SourceControlPanel {

  private static final long serialVersionUID = 8441444070730397800L; // NOPMD
  private static final Log log = LogFactory.getLog(InheritedSourceControlPanel.class);

  private final ReferenceableBuildNameDropdown ddParentBuildName = new ReferenceableBuildNameDropdown();
  private final Panel pnlWrapper = new Panel();
  private final PropertyToInputMap propertyToInputMap = new PropertyToInputMap(false, makePropertyHandler()); // strict map
  private SourceControlPanel pnlParentSourceControl = null;
  private int buildID = BuildConfig.UNSAVED_ID;


  public InheritedSourceControlPanel(final String parentBuildNameCaption) {
    super("Version Control Settings");
    // layout
    final Panel cp = super.getUserPanel();
    cp.add(new CommonFlow(new BoldCommonLabel(parentBuildNameCaption), ddParentBuildName));
    cp.add(pnlWrapper);
    pnlWrapper.setVisible(false);

    // bind
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.REFERENCE_BUILD_ID, ddParentBuildName);

    // set listener to show the configurations online
    ddParentBuildName.addListener(new DropDownSelectedListener() {
      private static final long serialVersionUID = 207624589146709030L;


      public Tierlet.Result dropDownSelected(final DropDownSelectedEvent dropDownSelectedEvent) {
        final ReferenceableBuildNameDropdown dropDown = (ReferenceableBuildNameDropdown) dropDownSelectedEvent.getDropDown();
        final int selectedBuildID = dropDown.getSelectedBuildID();
        if (log.isDebugEnabled()) {
          log.debug("selectedBuildID: " + selectedBuildID);
        }
        final BuildConfig selectedBuildConfig = ConfigurationManager.getInstance().getBuildConfiguration(selectedBuildID);
        final BuildConfig self = ConfigurationManager.getInstance().getBuildConfiguration(buildID); // will be null for a new build
        loadParentConfigPanel(self, selectedBuildConfig);
        return Tierlet.Result.Continue();
      }
    });
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
    if (pnlParentSourceControl != null) {
      pnlParentSourceControl.setBuildID(buildID);
    }
  }


  public void setMode(final int mode) {
    // set leader
    if (pnlParentSourceControl != null) {
      if (log.isDebugEnabled()) {
        log.debug("mode for leader: " + mode);
      }
      pnlParentSourceControl.setMode(mode);
    }

    if (mode == WebUIConstants.MODE_VIEW) {
      ddParentBuildName.setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      ddParentBuildName.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  public int getBuildID() {
    return buildID;
  }


  /**
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    buildID = buildConfig.getBuildID();
    propertyToInputMap.setProperties(ConfigurationManager.getInstance().getEffectiveSourceControlSettings(buildID));

    // exclude self
    ddParentBuildName.excludeBuildID(buildConfig.getBuildID());

    // create "effective" SCM config panel
    if (log.isDebugEnabled()) {
      log.debug("buildConfig: " + buildConfig);
    }
    final BuildConfig effectiveBuildConfig = ConfigurationManager.getInstance().getEffectiveBuildConfig(buildConfig);
    loadParentConfigPanel(buildConfig, effectiveBuildConfig);
  }


  public boolean save() {
    if (log.isDebugEnabled()) {
      log.debug("saving");
    }
    ConfigurationManager.getInstance().saveSourceControlSettings(buildID, getUpdatedSettings());
    if (log.isDebugEnabled()) {
      log.debug("saved");
    }
    return true;
  }


  public void setUpDefaults(final BuildConfig buildConfig) {
    // Do nothing
  }


  protected void loadParentConfigPanel(final BuildConfig self, final BuildConfig parent) {
    if (log.isDebugEnabled()) {
      log.debug("Loading parent config panel");
    }
    if (log.isDebugEnabled()) {
      log.debug("                      self: " + self);
    }
    if (log.isDebugEnabled()) {
      log.debug("                   parent: " + parent);
    }

    if (parent == null) {
      pnlWrapper.clear();
      pnlParentSourceControl = null;
      return;
    }


    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildConfig parentToLoad = parent.getSourceControl() == VersionControlSystem.SCM_REFERENCE ? cm.getEffectiveBuildConfig(parent) : parent;
    pnlParentSourceControl = SourceControlPanelFactory.getPanel(parentToLoad);
    pnlParentSourceControl.load(parentToLoad);
    // NOTE: vimeshev - 2006-12-29 - override with our settings,
    // if any. This will also chage the owning build if config.
    if (self != null) {
      pnlParentSourceControl.load(self);
    }
    pnlParentSourceControl.setMode(WebUIConstants.MODE_INHERITED);
    pnlWrapper.clear();
    pnlWrapper.add(pnlParentSourceControl);
    pnlWrapper.setVisible(true);
  }


  /**
   * Return modified properties
   */
  public List getUpdatedSettings() {
    // get our result, (ref build ID)
    final List result = propertyToInputMap.getUpdatedProperties();

    // get result from the leader that are marked as
    // editable. this specific way of handling for parallel
    // builds is set in AbstractSourceControlPanel#setMode
    final List updatedSettings = pnlParentSourceControl.getUpdatedSettings();
    for (int i = 0; i < updatedSettings.size(); i++) {
      final SourceControlSetting setting = (SourceControlSetting) updatedSettings.get(i);
      // make sure that they have "our" build config
      if (setting.getBuildID() != buildID) {
        // this is "their setting
        setting.setPropertyID(SourceControlSetting.UNSAVED_ID);
        setting.setBuildID(buildID);
      }
      result.add(setting);
    }
    return result;
  }


  public void setBuilderID(final int builderID) {
    if (pnlParentSourceControl != null) {
      pnlParentSourceControl.setBuilderID(builderID);
    }
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not
   * valid, a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();
    final List errors = new ArrayList(1);

    // validate fields are not blank
    if (ddParentBuildName.getSelection() == 0) {
      errors.add("Please select build name");
    }

    // validate it is not a circulare reference
    if (log.isDebugEnabled()) {
      log.debug("validate circularity");
    }
    if (buildID != BuildConfig.UNSAVED_ID) {
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final BuildConfig selectedBuildConfig = cm.getBuildConfiguration(ddParentBuildName.getSelectedBuildID());
      if (selectedBuildConfig == null) {
        errors.add("Please select build name");
      } else {
        if (log.isDebugEnabled()) {
          log.debug("selectedBuildConfig: " + selectedBuildConfig);
        }
        if (selectedBuildConfig.getSourceControl() == VersionControlSystem.SCM_REFERENCE) {
          final BuildConfig thisBuildConfig = cm.getBuildConfiguration(buildID);
          if (log.isDebugEnabled()) {
            log.debug("thisBuildConfig: " + thisBuildConfig);
          }
          if (cm.isCircularReference(thisBuildConfig, selectedBuildConfig)) {
            errors.add("This build refers to a build in the build chain that refers to this build. Circular references are not allowed.");
          }
        }
      }
    }

    // show error if there are any
    if (log.isDebugEnabled()) {
      log.debug("errors.size: " + errors.size());
    }
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
    }


    return errors.isEmpty() && pnlParentSourceControl.validate();
  }


  public String toString() {
    return "InheritedSourceControlPanel{" +
            "ddParentBuildName=" + ddParentBuildName +
            ", pnlWrapper=" + pnlWrapper +
            ", propertyToInputMap=" + propertyToInputMap +
            ", pnlParentSourceControl=" + pnlParentSourceControl +
            ", buildID=" + buildID +
            '}';
  }
}
