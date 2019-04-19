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

import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.MenuDividerLabel;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Layout;

import java.util.Properties;


/**
 * Used to show  "List view" / "Detailed View" links on the build
 * statuses page.
 *
 * @see BuildsStatusesPage
 */
public final class BuildStatusesViewSwitchLink extends Flow {

  private static final long serialVersionUID = -5086249110529635889L;

  private static final String CAPTION_LIST_VIEW = "List View";
  private static final String CAPTION_DETAILED_VIEW = "Detailed View";
  private static final String CAPTION_DASHBOARD_VIEW = "Dashboard View";
  private static final String CAPTION_RECENT_BUILDS = "Recent Builds";


  /**
   * Constructor
   */
  public BuildStatusesViewSwitchLink() {
    setAlignY(Layout.CENTER);
  }


  public void setDetailedViewSelected(final int displayGroupID, final int activeBuildID) {
    addComponents(displayGroupID, activeBuildID, false, false, true, false);
  }


  public void setTableViewSelected(final int displayGroupID) {
    addComponents(displayGroupID, BuildConfig.UNSAVED_ID, false, true, false, false);
  }


  public void setDashboardViewSelected(final int displayGroupID) {
    addComponents(displayGroupID, BuildConfig.UNSAVED_ID, true, false, false, false);
  }


  public void setRecentViewSelected(final int displayGroupID) {
    addComponents(displayGroupID, BuildConfig.UNSAVED_ID, false, false, false, true);
  }


  private void addComponents(final int displayGroupID, final int activeBuildID, final boolean dashboardSelected,
                             final boolean listSelected, final boolean detailedSelected, final boolean recentSelected) {
    clear();
    add(makeSelector(CAPTION_DASHBOARD_VIEW, Pages.STATUS_VIEW_DASHBOARD, displayGroupID, activeBuildID, dashboardSelected));
    add(new MenuDividerLabel()); // divider
    add(makeSelector(CAPTION_LIST_VIEW, Pages.STATUS_VIEW_LIST, displayGroupID, activeBuildID, listSelected));
    add(new MenuDividerLabel()); // divider
    add(makeSelector(CAPTION_DETAILED_VIEW, Pages.STATUS_VIEW_DETAILED, displayGroupID, activeBuildID, detailedSelected));
    add(new MenuDividerLabel()); // divider
    add(makeSelector(CAPTION_RECENT_BUILDS, Pages.STATUS_VIEW_RECENT, displayGroupID, activeBuildID, recentSelected));
  }


  private static Component makeSelector(final String caption, final String statusView, final int displayGroupID, final int activeBuildID, final boolean selected) {
    if (selected) {
      final CommonLabel disabledLink = new CommonLabel(caption);
      disabledLink.setFont(Pages.FONT_COMMON_MENU);
      return disabledLink;
    } else {
      final CommonCommandLink commonCommandLink = new CommonCommandLink(caption, Pages.PUBLIC_BUILDS);
      final Properties params = new Properties();
      params.setProperty(Pages.PARAM_STATUS_VIEW, statusView);
      params.setProperty(Pages.PARAM_DISPLAY_GROUP_ID, Integer.toString(displayGroupID));
      if (activeBuildID != BuildConfig.UNSAVED_ID) {
        params.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(activeBuildID));
      }
      commonCommandLink.setParameters(params);
      return commonCommandLink;
    }
  }
}
