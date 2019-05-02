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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Panel;

/**
 */
public final class GeneralBuildConfigPanel extends MessagePanel implements Loadable, Validatable, Saveable {

  private static final long serialVersionUID = 5685188730000206429L; // NOPMD
  private static final Log log = LogFactory.getLog(GeneralBuildConfigPanel.class);

  private BuildSequenceTable buildSequenceTable = null;
  private LabelSettingsPanel labelSettingsPanel = null;
  private ScheduleSettingsPanel scheduleSettingsPanel = null;
  private DependenceSettingsPanel dependenceSettingsPanel = null;

  private int buildID = BuildConfig.UNSAVED_ID;


  /**
   * Creates message panel without title.
   */
  public GeneralBuildConfigPanel(final BuildConfig buildConfig) {
    super(false);
    showHeaderDivider(true);

    // retrieve MessagePanel's user content panel
    final Panel contentPanel = getUserPanel();

    // create sequence table
    buildSequenceTable = new BuildSequenceTable(BuildStepType.BUILD);
    buildSequenceTable.populate();
    buildSequenceTable.setUpDefaults(buildConfig);

    // panels
    scheduleSettingsPanel = ScheduleSettingsPanelFactory.getPanel(buildConfig);
    labelSettingsPanel = LabelSettingsPanelFactory.getPanel(buildConfig);

    // layout
    scheduleSettingsPanel.setWidth(Pages.PAGE_WIDTH);
    labelSettingsPanel.setWidth(Pages.PAGE_WIDTH);

    contentPanel.add(buildSequenceTable);
    contentPanel.add(WebuiUtils.makePanelDivider());

    contentPanel.add(scheduleSettingsPanel);
    contentPanel.add(WebuiUtils.makePanelDivider());

    // add labeling panel only if labeling supported.
    if (supportsLabeling(buildConfig)) {
      contentPanel.add(labelSettingsPanel);
      contentPanel.add(WebuiUtils.makePanelDivider());
    }

    // Add dependence settings panel
    dependenceSettingsPanel = new DependenceSettingsPanel();
    if (SystemConfigurationManagerFactory.getManager().isAdvancedConfigurationMode()
            && !(buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL)) {
      contentPanel.add(dependenceSettingsPanel);
      contentPanel.add(WebuiUtils.makePanelDivider());
    }
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
    buildSequenceTable.setBuildID(buildID);
    scheduleSettingsPanel.setBuildID(buildID);
    labelSettingsPanel.setBuildID(buildID);
    dependenceSettingsPanel.setBuildID(buildID);
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    buildSequenceTable.load(buildConfig);
    scheduleSettingsPanel.load(buildConfig);
    labelSettingsPanel.load(buildConfig);
    dependenceSettingsPanel.load(buildConfig);
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should display a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    //
    // save
    //
    boolean saved = true;

    // save build header
    buildSequenceTable.setBuildID(buildID);
    scheduleSettingsPanel.setBuildID(buildID);
    labelSettingsPanel.setBuildID(buildID);
    dependenceSettingsPanel.setBuildID(buildID);

    saved &= buildSequenceTable.save();
    saved &= scheduleSettingsPanel.save();
    saved &= labelSettingsPanel.save();
    saved &= dependenceSettingsPanel.save();
    return saved;
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    boolean valid = true;
    valid = buildSequenceTable.validate() && valid;
    if (log.isDebugEnabled()) log.debug("buildSequenceTable valid: " + valid);
    valid = scheduleSettingsPanel.validate() && valid;
    if (log.isDebugEnabled()) log.debug("scheduleSettingsPanel valid: " + valid);
    valid = labelSettingsPanel.validate() && valid;
    if (log.isDebugEnabled()) log.debug("labelSettingsPanel valid: " + valid);
    valid = dependenceSettingsPanel.validate() && valid;
    if (log.isDebugEnabled()) log.debug("dependenceSettingsPanel valid: " + valid);
    return valid;
  }


  /**
   * Helper method. Returns true if the given build config
   * supports labeling. Subversion and Subversion-reference
   * builds don't support labeling.
   *
   * @param bc build config to probe.
   * @return true if the given build config supports labeling.
   */
  private static boolean supportsLabeling(final BuildConfig bc) {
    // SVN doesn't support labeling
    if (bc.getSourceControl() == BuildConfig.SCM_SVN) {
      return false;
    }

    // others support
    if (bc.getSourceControl() != BuildConfig.SCM_REFERENCE) {
      return true;
    }

    // for new builds we always display labeling because
    // we don't know the referred build type
    if (bc.getBuildID() == BuildConfig.UNSAVED_ID) {
      return true;
    }

    // check reference
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildConfig effectiveBuildConfig = cm.getEffectiveBuildConfig(bc);
    return effectiveBuildConfig.getSourceControl() != BuildConfig.SCM_SVN;
  }


  /**
   * Sets builder ID when validating.
   *
   */
  public void setBuilderID(final int builderID) {
  }
}
