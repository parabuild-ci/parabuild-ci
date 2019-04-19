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
package org.parabuild.ci.webui.merge;

import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.BuildsStatusesPage;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Layout;

/**
 * Used to show  "List view" / "Detailed View" links on the build
 * statuses page.
 *
 * @see BuildsStatusesPage
 */
final class MergeStatusesViewSwitchLink extends Flow {

  private static final long serialVersionUID = 2101110679439011400L;

  private static final String STR_LIST_VIEW = "List View";
  private static final String STR_DETAILED_VIEW = "Detailed View";
  private static final String STR_DASHBOARD_VIEW = "Dashboard View";


  /**
   * Constructor
   */
  public MergeStatusesViewSwitchLink() {
    setAlignY(Layout.CENTER);
    setAlignX(Layout.RIGHT);
  }


  public void setDetailedViewSelected() {
    clear();
// REVIEWME: simeshev@parabuilci.org -> enable when views other than table are avialable
//    add(makeDashboardViewSelector(false));
//    add(new MenuDividerLabel()); // divider
    add(makeListViewSelector(false));
//    add(new MenuDividerLabel()); // divider
//    add(makeDetailedViewSelector(true));
  }


  public void setTableViewSelected() {
    clear();
//    add(makeDashboardViewSelector(false));
//    add(new MenuDividerLabel()); // divider
    add(makeListViewSelector(true));
//    add(new MenuDividerLabel()); // divider
//    add(makeDetailedViewSelector(false));
  }


  public void setDashboardViewSelected() {
    clear();
//    add(makeDashboardViewSelector(true));
//    add(new MenuDividerLabel()); // divider
    add(makeListViewSelector(false));
//    add(new MenuDividerLabel()); // divider
//    add(makeDetailedViewSelector(false));
  }


  private Component makeListViewSelector(final boolean selected) {
    if (selected) {
      return makeDisabledLink(STR_LIST_VIEW);
    } else {
      return new CommonCommandLink(STR_LIST_VIEW, Pages.PAGE_MERGE_LIST,
        Pages.PARAM_STATUS_VIEW, Pages.STATUS_VIEW_LIST);
    }
  }


  private Component makeDashboardViewSelector(final boolean selected) {
    if (selected) {
      return makeDisabledLink(STR_DASHBOARD_VIEW);
    } else {
      return new CommonCommandLink(STR_DASHBOARD_VIEW, Pages.PAGE_MERGE_LIST,
        Pages.PARAM_STATUS_VIEW, Pages.STATUS_VIEW_DASHBOARD);
    }
  }


  private Component makeDetailedViewSelector(final boolean selected) {
    if (selected) {
      return makeDisabledLink(STR_DETAILED_VIEW);
    } else {
      return new CommonCommandLink(STR_DETAILED_VIEW, Pages.PAGE_MERGE_LIST,
        Pages.PARAM_STATUS_VIEW, Pages.STATUS_VIEW_DETAILED);
    }
  }


  private static CommonLabel makeDisabledLink(final String strListView) {
    final CommonLabel disabledLink = new CommonLabel(strListView);
    disabledLink.setFont(Pages.FONT_COMMON_MENU);
    return disabledLink;
  }
}
