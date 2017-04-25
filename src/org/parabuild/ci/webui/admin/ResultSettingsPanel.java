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


import java.util.*;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This panel holds build result configuration settings.
 *
 * @see BuildConfigTabs
 */
public final class ResultSettingsPanel extends MessagePanel implements Validatable, Saveable, Loadable {

  private static final long serialVersionUID = -6009879325960322446L; // NOPMD

  private static final String CAPTION_RESULTS_ARCHIVE = "Results Archive";
  private static final String CAPTION_PERMANENTLY_DELETE_BUILD_RESULTS_OLDER_THAN = "Delete build results: ";
  private static final String CAPTION_DAYS = " days";
  private static final String CAPTION_RETENTION = " older than ";
  private static final String CAPTION_SHOW_RESULTS_ON_LEADER_S_PAGE = "Show results on leader's page: ";

  private int buildID = BuildConfig.UNSAVED_ID;

  private final CheckBox flEnabledeleteBuildResults = new CheckBox(); // NOPMD SingularField
  private final Field flRetentionDays = new Field(4, 4); // NOPMD SingularField
  private final Label lbEnableDeletes = new CommonFieldLabel(CAPTION_PERMANENTLY_DELETE_BUILD_RESULTS_OLDER_THAN); // NOPMD SingularField
  private final Label lbDays = new CommonFieldLabel(CAPTION_DAYS); // NOPMD SingularField
  private final Label lbShowResultsOnLeaderPage = new CommonFieldLabel(CAPTION_SHOW_RESULTS_ON_LEADER_S_PAGE); // NOPMD SingularField
  private final CheckBox flShowResultsOnLeaderPage = new CheckBox(); // NOPMD SingularField
  private final CommonFlow flwShowResultsOnLeaderPage = new CommonFlow(lbShowResultsOnLeaderPage, flShowResultsOnLeaderPage);
  private final Component dividerForShowResultsOnLeaderPage = WebuiUtils.makePanelDivider();
  private final ResultConfigsTable tblResultConfigs = new ResultConfigsTable(); // NOPMD SingularField

  private final PropertyToInputMap propertyToInputMap = new PropertyToInputMap(false, new BuildConfigAttributePropertyHandler()); // strict map


  /**
   * Creates panel without title.
   */
  public ResultSettingsPanel() {
    super(false);
    showHeaderDivider(true);

    // layout
    final Panel contentPanel = super.getUserPanel();

    // add archive settings
    final MessagePanel pnlResultsBorder = new MessagePanel(CAPTION_RESULTS_ARCHIVE);
    pnlResultsBorder.setWidth(Pages.PAGE_WIDTH);
    final Panel userPanel = pnlResultsBorder.getUserPanel();
    final GridIterator gi = new GridIterator(userPanel, 2);
    gi.addPair(lbEnableDeletes, new CommonFlow(flEnabledeleteBuildResults, new CommonFieldLabel(CAPTION_RETENTION), flRetentionDays, lbDays));
    contentPanel.add(pnlResultsBorder);

    // add results config
    contentPanel.add(WebuiUtils.makePanelDivider());
    contentPanel.add(tblResultConfigs);

    // add check box to show results on the leader's page
    contentPanel.add(dividerForShowResultsOnLeaderPage);
    contentPanel.add(flwShowResultsOnLeaderPage);

    // bind settings
    propertyToInputMap.bindPropertyNameToInput(BuildConfigAttribute.RESULT_RETENTION_DAYS, flRetentionDays);
    propertyToInputMap.bindPropertyNameToInput(BuildConfigAttribute.ENABLE_AUTOMATIC_DELETING_OLD_BUILD_RESULTS, flEnabledeleteBuildResults);
    propertyToInputMap.bindPropertyNameToInput(BuildConfigAttribute.SHOW_RESULTS_ON_LEADER_PAGE, flShowResultsOnLeaderPage);
  }


  /**
   * Returns build ID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets build ID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
    tblResultConfigs.setBuildID(buildID);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    final List errors = new ArrayList(3);

    // validate retention
    if (flEnabledeleteBuildResults.isChecked()) {
      // validate retention
      WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_RETENTION, flRetentionDays);
      if (errors.isEmpty()) {
        final int minimumValue = SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.MINIMUM_RESULTS_RETENTION, SystemProperty.DEFAULT_MINIMUM_RESULTS_RETENTION);
        if (Integer.parseInt(flRetentionDays.getValue()) < minimumValue) {
          errors.add("Field \"" + CAPTION_RETENTION
            + "\" should be bigger or equal " + SystemProperty.DEFAULT_MINIMUM_RESULTS_RETENTION
            + ". This minimum can be adjusted using system stability settings.");
        }
      }
    }

    //
    if (!errors.isEmpty()) showErrorMessage(errors);

    // validate table
    boolean valid = errors.isEmpty();
    valid = this.tblResultConfigs.validate() && valid;
    return valid;
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should dispaly a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    ConfigurationManager.getInstance().saveBuildAttributes(buildID, propertyToInputMap.getUpdatedProperties());
    boolean saved = true;
    if (buildID == BuildConfig.UNSAVED_ID) throw new IllegalArgumentException("Build ID can not be uninitialized");
    saved &= tblResultConfigs.save();
    return saved;
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    propertyToInputMap.setProperties(ConfigurationManager.getInstance().getBuildAttributes(buildConfig.getBuildID()));
    tblResultConfigs.load(buildConfig);
  }


  /**
   * Shows or hides attributes characteristic to Parallel
   * builds such as a check box asking to show parallel's
   * results on leader's resut page.
   */
  public void showParallelAttributes(final boolean show) {
    flwShowResultsOnLeaderPage.setVisible(show);
    dividerForShowResultsOnLeaderPage.setVisible(show);
  }
}
