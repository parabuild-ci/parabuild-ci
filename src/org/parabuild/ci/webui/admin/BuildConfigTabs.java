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
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Panel;
import viewtier.ui.Tabs;

/**
 *
 */
public final class BuildConfigTabs extends Tabs implements Saveable, Validatable {

  private static final long serialVersionUID = -4738503954980585676L; // NOPMD
  private static final Log log = LogFactory.getLog(BuildConfigTabs.class);

  private static final String CAPTION_BUILD_CONFIGURATION = "Build Configuration";
  private static final String CAPTION_display_GROUPS = "Display Groups";
  private static final String CAPTION_LOGS = "Logs";
  private static final String CAPTION_MANUAL_START_PARAMETERS = "Parameters";
  private static final String CAPTION_NOTIFICATION = "Notification";
  private static final String CAPTION_PROMOTION = "Promotion";
  private static final String CAPTION_RELEASE_NOTES = "Release Notes";
  private static final String CAPTION_RESULTS = "Results";
  private static final String CAPTION_VERSION_CONTROL = "Version Control";

  private int tabIndexVersionControl = 0;
  private int tabIndexGeneral = 0;
  private int tabIndexNotification = 0;
  private int tabIndexLogs = 0;
  private int tabIndexResults = 0;
  private int tabIndexManualRunParameters = 0;
  private int tabIndexPromotionSettings = 0;
  private int tabIndexIssueTrackers = 0;
  private int tabIndexDisplayGroups = 0;

  /**
   * Notidication settings panel.
   */
  private final NotificationSettingsPanel pnlNotificationConfig;

  /**
   * Version control settings
   */
  private final SourceControlPanel pnlVersionControl;

  /**
   * General settings panel.
   */
  private final GeneralBuildConfigPanel pnlGeneralConfig;

  /**
   * Logging settings panel.
   */
  private final LogSettingsPanel pnlLogSettings;

  /**
   * Result settings panel.
   */
  private final ResultSettingsPanel pnlResultSettings;

  /**
   * Manual start parameters settings panel.
   */
  private ManualStartSettingsPanel pnlManualStartSettings = null;

  /**
   * Prootional settings panel.
   */
  private PromotionSettingsPanel pnlPromotionSettings = null;

//  /**
//   * General settings panel.
//   */
//  private SecuritySettingsPanel pnlSecurity = null;

  /**
   * Issue tracker settings table.
   */
  private final IssueTrackerTable tblIssueTrackers;

  /**
   * Build display groups.
   */
  private final DisplayGroupsTable tblDisplayGroups;

  /**
   *
   */
  private int buildID = BuildConfig.UNSAVED_ID;


  /**
   * Constructor
   */
  public BuildConfigTabs(final BuildConfig buildConfig) {

    final Panel pnlVCHolder = new Panel();
    pnlVersionControl = SourceControlPanelFactory.getPanel(buildConfig);
    pnlVersionControl.setWidth(Pages.PAGE_WIDTH);
    pnlVCHolder.add(WebuiUtils.makePanelDivider());
    pnlVCHolder.add(pnlVersionControl);
    tabIndexVersionControl = addConfigTab(CAPTION_VERSION_CONTROL, pnlVCHolder);

    // create build config tab
    pnlGeneralConfig = new GeneralBuildConfigPanel(buildConfig);
    tabIndexGeneral = addConfigTab(CAPTION_BUILD_CONFIGURATION, pnlGeneralConfig);

    // create notif tab tab
    pnlNotificationConfig = new NotificationSettingsPanel(WebUIConstants.MODE_EDIT);
    tabIndexNotification = addConfigTab(CAPTION_NOTIFICATION, pnlNotificationConfig);

    // create log configs tab
    pnlLogSettings = new LogSettingsPanel();
    tabIndexLogs = addConfigTab(CAPTION_LOGS, pnlLogSettings);

    // create result configs tab
    pnlResultSettings = new ResultSettingsPanel();
    pnlResultSettings.showParallelAttributes(buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL);
    tabIndexResults = addConfigTab(CAPTION_RESULTS, pnlResultSettings);

    // create release notes
    tblIssueTrackers = new IssueTrackerTable();
    tblIssueTrackers.setWidth(Pages.PAGE_WIDTH);
    // add tab only if it's a scheduled build
    final byte sourceControl = buildConfig.getSourceControl();
    final byte scheduleType = buildConfig.getScheduleType();
    if (scheduleType == BuildConfig.SCHEDULE_TYPE_RECURRENT || sourceControl == VCSAttribute.SCM_PERFORCE) {
      tabIndexIssueTrackers = addConfigTab(CAPTION_RELEASE_NOTES, tblIssueTrackers);
    }

    // create manual run parameters
    if (scheduleType != BuildConfig.SCHEDULE_TYPE_PARALLEL) {
      // manual run parameters are supported only for "independent" builds.
      pnlManualStartSettings = new ManualStartSettingsPanel(sourceControl, scheduleType);
      tabIndexManualRunParameters = addConfigTab(CAPTION_MANUAL_START_PARAMETERS, pnlManualStartSettings);
    }

    // create promotion tab
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    if (scm.isBuildPromotionEnabled() || scm.isPublishingCommandsEnabled()) {
      pnlPromotionSettings = new PromotionSettingsPanel();
      tabIndexPromotionSettings = addConfigTab(CAPTION_PROMOTION, pnlPromotionSettings);
    }

    // Create display groups tab
    tblDisplayGroups = new DisplayGroupsTable();
    tblIssueTrackers.setWidth(Pages.PAGE_WIDTH);
    tabIndexDisplayGroups = addConfigTab(CAPTION_display_GROUPS, tblDisplayGroups);

//    pnlSecurity = new SecuritySettingsPanel();
//    tabIndexSecurity = addConfigTab(CAPTION_SECURITY, pnlSecurity);

    selectTab(tabIndexVersionControl);
    setWidth(Pages.PAGE_WIDTH);
  }


  /**
   * Helper method
   */
  private int addConfigTab(final String caption, final Panel panel) {
    final int index = super.addTab(caption);
    setTabContent(index, panel);
    return index;
  }


  /**
   * Loads build configuration
   *
   * @param buildConfig
   */
  public void load(final BuildConfig buildConfig) {
    pnlVersionControl.load(buildConfig);
    pnlGeneralConfig.load(buildConfig); // load general config
    pnlNotificationConfig.load(buildConfig); // load notification config
    pnlLogSettings.load(buildConfig); // load log table
    pnlResultSettings.load(buildConfig); // load log table
    tblIssueTrackers.load(buildConfig); // load issue trackers
    tblDisplayGroups.load(buildConfig);
//    pnlSecurity.load(buildConfig); // load issue trackers
    if (pnlManualStartSettings != null) {
      pnlManualStartSettings.load(buildConfig); // load run params table
    }
    if (pnlPromotionSettings != null) {
      pnlPromotionSettings.load(buildConfig); // load run params table
    }
    setBuildID(buildConfig.getBuildID());

    // select tab to last saved or ro tabIndexGeneral
    final Integer lastSavedTab = ConfigurationManager.getInstance().getBuildAttributeValue(buildID, BuildConfigAttribute.LAST_SAVED_TAB, new Integer(tabIndexGeneral));
    if (lastSavedTab < getTabCount()) {
      selectTab(lastSavedTab);
    }
  }


  public void setBuildID(final int buildID) {
    if (log.isDebugEnabled()) {
      log.debug("setBuildID buildID: " + buildID);
    }
    pnlVersionControl.setBuildID(buildID);
    pnlGeneralConfig.setBuildID(buildID);
    pnlNotificationConfig.setBuildID(buildID);
    pnlLogSettings.setBuildID(buildID);
    pnlResultSettings.setBuildID(buildID);
    tblIssueTrackers.setBuildID(buildID);
    tblDisplayGroups.setBuildID(buildID);
//    pnlSecurity.setBuildID(buildID);
    if (pnlManualStartSettings != null) {
      pnlManualStartSettings.setBuildID(buildID);
    }
    if (pnlPromotionSettings != null) {
      pnlPromotionSettings.setBuildID(buildID);
    }
    this.buildID = buildID;
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
    // save tabs
    boolean saved = pnlVersionControl.save();
    saved &= pnlGeneralConfig.save();
    saved &= pnlNotificationConfig.save();
    saved &= pnlLogSettings.save();
    saved &= pnlResultSettings.save();
    saved &= tblIssueTrackers.save();
    saved &= tblDisplayGroups.save();
//    saved &= pnlSecurity.save();
    if (pnlManualStartSettings != null) {
      saved &= pnlManualStartSettings.save();
    }
    if (pnlPromotionSettings != null) {
      saved &= pnlPromotionSettings.save();
    }

    // save current tab selection
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    BuildConfigAttribute bca = cm.getBuildAttribute(buildID, BuildConfigAttribute.LAST_SAVED_TAB);
    if (bca == null) {
      bca = new BuildConfigAttribute();
      bca.setBuildID(buildID);
      bca.setPropertyName(BuildConfigAttribute.LAST_SAVED_TAB);
    }
    bca.setPropertyValue(getSelectedTab());
    cm.saveObject(bca);

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
    if (log.isDebugEnabled()) {
      log.debug("validating version control");
    }
    boolean valid = validate(tabIndexVersionControl, pnlVersionControl);
    if (log.isDebugEnabled()) {
      log.debug("version control valid: " + valid);
    }
    valid &= validate(tabIndexGeneral, pnlGeneralConfig);
    valid &= validate(tabIndexNotification, pnlNotificationConfig);
    valid &= validate(tabIndexLogs, pnlLogSettings);
    valid &= validate(tabIndexResults, pnlResultSettings);
    valid &= validate(tabIndexManualRunParameters, pnlManualStartSettings);
    valid &= validate(tabIndexIssueTrackers, tblIssueTrackers);
    valid &= validate(tabIndexDisplayGroups, tblDisplayGroups);
    valid &= validate(tabIndexPromotionSettings, pnlPromotionSettings);
//    valid &= validate(tabIndexSecurity, pnlSecurity);
    return valid;
  }


  /**
   * Helper method to validate panels attached to given tabs
   *
   * @param tabIndex
   * @param validatable
   */
  private boolean validate(final int tabIndex, final Validatable validatable) {
    if (validatable == null) {
      return true;
    }
    final boolean valid = validatable.validate();
    if (!valid) {
      super.selectTab(tabIndex);
    }
    return valid;
  }


  /**
   * Sets builder ID to use when validating.
   *
   * @param builderID to set.
   */
  public void setBuilderID(final int builderID) {
    pnlVersionControl.setBuilderID(builderID);
    pnlGeneralConfig.setBuilderID(builderID);
  }


  public String toString() {
    return "BuildConfigTabs{" +
            "tabIndexVersionControl=" + tabIndexVersionControl +
            ", tabIndexGeneral=" + tabIndexGeneral +
            ", tabIndexNotification=" + tabIndexNotification +
            ", tabIndexLogs=" + tabIndexLogs +
            ", tabIndexResults=" + tabIndexResults +
            ", tabIndexManualRunParameters=" + tabIndexManualRunParameters +
            ", tabPromotionSettings=" + tabIndexPromotionSettings +
            ", pnlNotificationConfig=" + pnlNotificationConfig +
            ", pnlVersionControl=" + pnlVersionControl +
            ", pnlGeneralConfig=" + pnlGeneralConfig +
            ", pnlLogSettings=" + pnlLogSettings +
            ", pnlResultSettings=" + pnlResultSettings +
            ", pnlManualStartSettings=" + pnlManualStartSettings +
            ", pnlPromotionSettings=" + pnlPromotionSettings +
            ", tblIssueTrackers=" + tblIssueTrackers +
            ", buildID=" + buildID +
            '}';
  }
}
