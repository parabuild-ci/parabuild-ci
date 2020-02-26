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
package org.parabuild.ci.webui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonBoldLink;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Border;
import viewtier.ui.Color;
import viewtier.ui.Component;
import viewtier.ui.Image;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public final class DetailedBuildStatusesPanel extends Panel {

  private static final long serialVersionUID = 330643685374154464L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BuildStatusesTable.class); // NOPMD

  public static final Color COLOR_LEFTNAV_DEFAULT_BG = Pages.COLOR_PANEL_BORDER;
  public static final Color COLOR_LEFTNAV_SELECTED_BG = Pages.TABLE_COLOR_HEADER_BG;

  public static final int LEFT_NAV_ITEM_HEIGHT = 35;
  public static final String LEFT_NAV_WIDTH = "15%";

  public static final int DIVIDER_WIDTH = 2;
  public static final String DETAILS_WIDTH = "85%";


  public DetailedBuildStatusesPanel(final int selectedBuildID, final int displayGroupID, final boolean showBuildCommands,
                                    final List currentBuildsStatuses, final TailWindowActivator tailWindowActivator) {

    // preExecute
    final int statusesSize = currentBuildsStatuses.size();
    if (statusesSize == 0) {
      add(new BoldCommonLabel("Currently there are no builds to show."));
      return;
    }

    // get selected index
    // make up the left nav from build statuses
    final Panel pnlLeftNav = makeLeftNavigationBar(currentBuildsStatuses, selectedBuildID, displayGroupID);
    pnlLeftNav.setWidth(LEFT_NAV_WIDTH);
    add(pnlLeftNav, new Layout(0, 0, 1, 1));

    // add build details
    if (selectedBuildID != BuildConfig.UNSAVED_ID) {
      BuildState selectedState = null;
      for (int i = 0; i < statusesSize; i++) {
        final BuildState buildState = (BuildState) currentBuildsStatuses.get(i);
        if (buildState.getActiveBuildID() == selectedBuildID) {
          selectedState = buildState;
          break;
        }
      }
      // set to first if the selected ID is not found
      if (selectedState == null) {
        selectedState = (BuildState) currentBuildsStatuses.get(0);
      }
      final DetailedBuildStatusPanel pnlStatus = new DetailedBuildStatusPanel(selectedState, showBuildCommands, tailWindowActivator);
      pnlStatus.setWidth(DETAILS_WIDTH);
      pnlStatus.setBorder(Border.LEFT, DIVIDER_WIDTH, Pages.COLOR_PANEL_BORDER);
      add(pnlStatus, new Layout(1, 0, 1, 1));
    }
  }


  /**
   * Creates a panel presenting a list of color-market current
   * builds.
   *
   * @param statuses        List of currentBuildStatuses.
   * @param selectedBuildID
   * @param displayGroupID
   * @return Panel - a vertically aligned panel.
   */
  private Panel makeLeftNavigationBar(final List statuses, final int selectedBuildID, final int displayGroupID) {

    final ConfigurationManager cm = ConfigurationManager.getInstance();

    // Lasy set of integer leader build IDs
    Set leaderBuildIDs = null;

    // layout
    final Panel pnlLeftNav = new Panel();
    final GridIterator giLeftNav = new GridIterator(pnlLeftNav, 3);
    for (int i = 0, n = statuses.size(); i < n; i++) {
      final BuildState buildState = (BuildState) statuses.get(i);
      final CommonLink lnkBuildName = makeBuildNameLink(buildState, selectedBuildID, displayGroupID);
      final Component marker = makeBuildStateMarker(buildState, selectedBuildID);
      if (buildState.isParallel()) {
        // Lasy init leader IDs
        if (leaderBuildIDs == null) {
          leaderBuildIDs = WebuiUtils.getLeaderBuildIDs(statuses);
        }

        // Check if leader is present
        final Integer leaderBuildID = new Integer(cm.getSourceControlSettingValue(buildState.getActiveBuildID(), VCSAttribute.REFERENCE_BUILD_ID, BuildConfig.UNSAVED_ID));
        if (leaderBuildIDs.contains(leaderBuildID)) {
          giLeftNav.add(marker).add(makeDependentBuildOffsetter(buildState, selectedBuildID)).add(lnkBuildName);
        } else {
          giLeftNav.add(marker).add(lnkBuildName, 2);
        }
      } else {
        giLeftNav.add(marker).add(lnkBuildName, 2);
      }
    }
    return pnlLeftNav;
  }


  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private Label makeDependentBuildOffsetter(final BuildState dependentState, final int selectedBuildID) {
    final Label dependentOffsetter = new Label("&nbsp;&nbsp;");
    dependentOffsetter.setWidth(30);
    dependentOffsetter.setBackground(makeBuildNameBackground(dependentState, selectedBuildID));
    dependentOffsetter.setBorder(Border.LEFT | Border.TOP | Border.BOTTOM, 1, Color.White);
    return dependentOffsetter;
  }


  private Component makeBuildStateMarker(final BuildState buildState, final int selectedBuildID) {
    final Image result = WebuiUtils.makeThrobber(buildState, buildState.getStatusAsString(), 20);
    result.setBorder(Border.ALL, 1, Color.White);
    result.setBackground(makeBuildNameBackground(buildState, selectedBuildID));
    return result;
  }


  private CommonLink makeBuildNameLink(final BuildState buildState, final int selectedBuildID, final int displayGroupID) {
    final Properties params = new Properties();
    params.setProperty(Pages.PARAM_BUILD_ID, buildState.getBuildIDAsString());
    params.setProperty(Pages.PARAM_STATUS_VIEW, Pages.STATUS_VIEW_DETAILED);
    params.setProperty(Pages.PARAM_DISPLAY_GROUP_ID, Integer.toString(displayGroupID));
    final CommonLink lnkBuildName = new CommonBoldLink(WebuiUtils.getBuildName(buildState), Pages.PUBLIC_BUILDS, params);
    // link appearance
    lnkBuildName.setHeight(LEFT_NAV_ITEM_HEIGHT);
    lnkBuildName.setWidth("100%");
    lnkBuildName.setBorder(buildState.isParallel() ? Border.TOP | Border.BOTTOM | Border.RIGHT : Border.ALL, 1, Color.White);
    lnkBuildName.setBackground(makeBuildNameBackground(buildState, selectedBuildID));
    return lnkBuildName;
  }


  private static Color makeBuildNameBackground(final BuildState buildState, final int selectedBuildID) {
    return buildState.getActiveBuildID() == selectedBuildID ? COLOR_LEFTNAV_SELECTED_BG : COLOR_LEFTNAV_DEFAULT_BG;
  }
}
