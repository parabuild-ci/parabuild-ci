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
package org.parabuild.ci.webui.agent.status;

import org.parabuild.ci.webui.BuildsStatusesPage;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.MenuDividerLabel;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Layout;


/**
 * Used to show  "List view" / "Detailed View" links on the build
 * statuses page.
 *
 * @see BuildsStatusesPage
 */
final class AgentViewSwitchLink extends Flow {

  private static final long serialVersionUID = 502148288287306412L;
  private static final String CAPTION_LIST_VIEW = "Agent List View";
  private static final String CAPTION_LOAD_VIEW = "Agent Load View";


  /**
   * Constructor
   */
  AgentViewSwitchLink() {
    setAlignY(Layout.CENTER);
  }


  public void setLoadViewSelected() {
    clear();
    add(makeLoadViewSelector(true));
    add(new MenuDividerLabel()); // divider
    add(makeListViewSelector(false));
  }


  public void setListViewSelected() {
    clear();
    add(makeLoadViewSelector(false));
    add(new MenuDividerLabel()); // divider
    add(makeListViewSelector(true));
  }


  private Component makeListViewSelector(final boolean selected) {
    if (selected) {
      return makeDisabledLink(CAPTION_LIST_VIEW);
    } else {
      return new CommonCommandLink(CAPTION_LIST_VIEW, Pages.PAGE_AGENTS,
              Pages.PARAM_AGENT_STATUS_VIEW, Pages.AGENT_STATUS_VIEW_LIST);
    }
  }


  private Component makeLoadViewSelector(final boolean selected) {
    if (selected) {
      return makeDisabledLink(CAPTION_LOAD_VIEW);
    } else {
      return new CommonCommandLink(CAPTION_LOAD_VIEW, Pages.PAGE_AGENTS,
              Pages.PARAM_AGENT_STATUS_VIEW, Pages.AGENT_STATUS_VIEW_LOAD);
    }
  }


  private CommonLabel makeDisabledLink(final String strListView) {
    final CommonLabel disabledLink = new CommonLabel(strListView);
    disabledLink.setFont(Pages.FONT_COMMON_MENU);
    return disabledLink;
  }
}