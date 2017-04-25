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
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonBoldLink;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Border;
import viewtier.ui.Color;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;

import java.util.List;

/**
 * Shows dashboard view for build statuses.
 *
 * @noinspection OverridableMethodCallInConstructor
 */
public class DashboardStatusesPanel extends Panel {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(BuildStatusesTable.class); // NOPMD
  private DashboardOverviewPanel dashboardOverviewPanel = new DashboardOverviewPanel();
  private DashboardRecentEventsPanel dashboardRecentEventsPanel = new DashboardRecentEventsPanel();
  private DashboardImagesPanel dashboardImagesPanel = new DashboardImagesPanel();
  private static final long serialVersionUID = -2324061501016615774L;


  public DashboardStatusesPanel() {
    setWidth("100%");
    add(dashboardOverviewPanel, new Layout(1, 1, 1, 2));
    add(new Label(" "), new Layout(2, 1, 1, 2));
    add(dashboardRecentEventsPanel, new Layout(3, 1, 1, 1));
    add(dashboardImagesPanel, new Layout(3, 2, 1, 1));
    dashboardOverviewPanel.setWidth(150);
    dashboardRecentEventsPanel.setWidth("100%");
    dashboardRecentEventsPanel.setBorder(Border.BOTTOM, 4, Color.White); // use it as separator
    dashboardImagesPanel.setWidth("100%");
  }


  /**
   * Populates with current build statruses.
   *
   * @param currentBuildsStatuses
   */
  public void populate(final List currentBuildsStatuses) {
    final SecurityManager sm = SecurityManager.getInstance();
    final List userBuildStatuses = sm.getUserBuildStatuses(sm.getUserIDFromContext(getTierletContext()));
    dashboardOverviewPanel.populate(userBuildStatuses);
    dashboardRecentEventsPanel.populate(currentBuildsStatuses);
    dashboardImagesPanel.populate(currentBuildsStatuses);
  }


  private static final class DashboardOverviewPanel extends MessagePanel {

    private static final String CAPTION_BROKEN = "Broken: ";
    private static final String CAPTION_BUILDING = "Building: ";
    private static final String CAPTION_INACTIVE = "Inactive: ";
    private static final String CAPTION_OVERVIEW = "Overview";
    private static final String CAPTION_TOTAL = "Total: ";
    private static final long serialVersionUID = -5143981006112540515L;


    /**
     * Creates message panel without title.
     */
    DashboardOverviewPanel() {
      super(CAPTION_OVERVIEW);
    }


    public void populate(final List currentBuildsStatuses) {
      final Panel up = getUserPanel();

      // count
      int broken = 0;
      int total = 0;
      int building = 0;
      int inactive = 0;
      for (int i = 0; i < currentBuildsStatuses.size(); i++) {
        total++;
        final BuildState buildState = (BuildState) currentBuildsStatuses.get(i);
        final BuildRun lastCompleteBuildRun = buildState.getLastCompleteBuildRun();
        if (lastCompleteBuildRun != null) {
          if (!lastCompleteBuildRun.successful()) {
            broken++;
          }
        }
        if (buildState.isRunning()) {
          building++;
        }
        if (BuildStatus.INACTIVE.equals(buildState.getStatus())) {
          inactive++;
          total--;
        }
      }

      // add overview items

      final GridIterator gi = new GridIterator(up, 2);
      up.clear();

      // add broken
      if (broken > 0) {
        final CommonLabel lbBroken = new BoldCommonLabel(CAPTION_BROKEN);
        lbBroken.setForeground(Pages.COLOR_BUILD_FAILED);
        final CommonLink lnkBroken = new CommonBoldLink(Integer.toString(broken), Pages.PUBLIC_BUILDS, Pages.PARAM_DISPLAY_GROUP_ID, DisplayGroup.DISPLAY_GROUP_ID_BROKEN);
        lnkBroken.setForeground(Pages.COLOR_BUILD_FAILED);
        gi.add(lbBroken).add(lnkBroken);
      }

      // add building
      gi.add(new BoldCommonLabel(CAPTION_BUILDING)).add(new CommonBoldLink(Integer.toString(building), Pages.PUBLIC_BUILDS, Pages.PARAM_DISPLAY_GROUP_ID, DisplayGroup.DISPLAY_GROUP_ID_BUILDING));


      // add total
      gi.add(new BoldCommonLabel(CAPTION_TOTAL)).add(new CommonBoldLink(Integer.toString(total), Pages.PUBLIC_BUILDS, Pages.PARAM_DISPLAY_GROUP_ID, DisplayGroup.DISPLAY_GROUP_ID_ALL));

      // add inactive
      gi.add(new BoldCommonLabel(CAPTION_INACTIVE)).add(new CommonBoldLink(Integer.toString(inactive), Pages.PUBLIC_BUILDS, Pages.PARAM_DISPLAY_GROUP_ID, DisplayGroup.DISPLAY_GROUP_ID_INACTIVE));
    }


    public String toString() {
      return "DashboardOverviewPanel{}";
    }
  }

  private static final class DashboardRecentEventsPanel extends Panel {

    private static final String OLDEST_BROKEN = "Oldest Broken";
    private static final String LAST_BROKEN = "Last Broken";
    private static final String LASTEST_CLEAN = "Latest Successful";

    private static final Color BG_COLOR_BROKEN = new Color(0xFFAAAA);
    private static final Color BORDER_COLOR_BROKEN = new Color(0xFFEEEE);

    private static final Color BG_COLOR_CLEAN = new Color(0xAAFFAA);
    private static final Color BORDER_COLOR_CLEAN = new Color(0xEEFFEE);
    private static final long serialVersionUID = 29561441565263082L;


    public void populate(final List currentBuildsStatuses) {
      clear();

      // find latest broken
      BuildState latestBroken = null;
      BuildState oldestBroken = null;
      BuildState lastClean = null;
      for (int i = 0; i < currentBuildsStatuses.size(); i++) {
        final BuildState buildState = (BuildState) currentBuildsStatuses.get(i);
        final BuildRun lastCompleteBuildRun = buildState.getLastCompleteBuildRun();
        if (lastCompleteBuildRun != null) {
          if (lastCompleteBuildRun.successful()) {
            if (lastClean == null) {
              lastClean = buildState;
            } else {
              if (lastCompleteBuildRun.getFinishedAt().getTime() > lastClean.getLastCompleteBuildRun().getFinishedAt().getTime()) {
                lastClean = buildState;
              }
            }
          } else {
            // process latest broken
            if (latestBroken == null) {
              latestBroken = buildState;
            } else {
              if (lastCompleteBuildRun.getFinishedAt().getTime() > latestBroken.getLastCompleteBuildRun().getFinishedAt().getTime()) {
                latestBroken = buildState;
              }
            }
            // process oldest broken
            if (oldestBroken == null) {
              oldestBroken = buildState;
            } else {
              if (lastCompleteBuildRun.getFinishedAt().getTime() < oldestBroken.getLastCompleteBuildRun().getFinishedAt().getTime()) {
                oldestBroken = buildState;
              }
            }
          }
        }
      }

      final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
      final String dateTimeFormat = systemCM.getDateTimeFormat();

      // arrange panel accordingly
      final GridIterator gi = new GridIterator(this, 8);
      if (oldestBroken != null) {
        addCaptionAndBuild(oldestBroken, gi, dateTimeFormat, OLDEST_BROKEN, BG_COLOR_BROKEN, BORDER_COLOR_BROKEN);
      }
      if (latestBroken != null && latestBroken != oldestBroken) {
        addCaptionAndBuild(latestBroken, gi, dateTimeFormat, LAST_BROKEN, BG_COLOR_BROKEN, BORDER_COLOR_BROKEN);
      }
      if (lastClean != null && (latestBroken == null || oldestBroken == null)) {
        addCaptionAndBuild(lastClean, gi, dateTimeFormat, LASTEST_CLEAN, BG_COLOR_CLEAN, BORDER_COLOR_CLEAN);
      }
    }


    private void addCaptionAndBuild(final BuildState state, final GridIterator gi,
                                    final String dateTimeFormat, final String caption, final Color bgColor, final Color borderColor) {
      final BoldCommonLabel label = new BoldCommonLabel(caption);
      label.setWidth(35);
      label.setHeight(35);
      label.setAlignX(Layout.CENTER);
      label.setAlignY(Layout.CENTER);
      label.setBackground(bgColor);
      label.setBorder(Border.ALL, 1, borderColor);
      label.setPadding(5);
      gi.add(label);
      gi.add(new Label(" "));
      final LastBuildRunFlow flwBuildRun = new LastBuildRunFlow(state.getLastCompleteBuildRun(), true, dateTimeFormat);
      flwBuildRun.setAlignX(Layout.LEFT);
      flwBuildRun.setAlignY(Layout.CENTER);
      flwBuildRun.setBackground(bgColor);
      flwBuildRun.setBorder(Border.ALL, 1, borderColor);
      flwBuildRun.setPadding(5);
      gi.add(flwBuildRun);
      gi.add(new Label(" "));
    }


    public String toString() {
      return "DashboardRecentEventsPanel{}";
    }
  }


  public String toString() {
    return "DashboardStatusesPanel{" +
            "dashboardOverviewPanel=" + dashboardOverviewPanel +
            ", dashboardRecentEventsPanel=" + dashboardRecentEventsPanel +
            ", dashboardImagesPanel=" + dashboardImagesPanel +
            '}';
  }
}
