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
package org.parabuild.ci.webui.common;

import org.apache.commons.logging.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.webui.SearchPage;
import viewtier.ui.*;

/**
 * Shows a low-profile panel that contains optional Previous/Next
 * navigation and qucik search imput and button.
 *
 * @see SearchPage
 */
public final class HeaderDividerPanel extends Panel {

  private static final Log log = LogFactory.getLog(HeaderDividerPanel.class);
  private static final long serialVersionUID = -8389654870926798326L; // NOPMD

  public static final String FIELD_NAME_QUERY = "query";
  public static final String FIELD_NAME_SEARCH = "search";
  public static final String CAPTION_SEARCH = "Search";

  private CommonField flQuery = null;
  private Flow currentNavigationLinks = null; // NOPMD


  public HeaderDividerPanel(final int pageFlags) {
//    super.showContentBorder(false);

    // search
    if (BasePage.flagSet(pageFlags, BasePage.FLAG_SHOW_QUICK_SEARCH)) {
      // create search components
      flQuery = new CommonField(50, 10);
      flQuery.setName(FIELD_NAME_QUERY);
      final CommonButton btSearch = new CommonButton(CAPTION_SEARCH);
      btSearch.setFont(Pages.FONT_COMMON_SMALL);
      btSearch.setName(FIELD_NAME_SEARCH);
      btSearch.setAlignX(Layout.CENTER);
      // set up listener
      btSearch.addListener(new SearchButtonPressedListener());
      // layout
      final CommonFlow commonFlow = new CommonFlow(flQuery, new Label(" "), btSearch);
      commonFlow.setAlignX(Layout.RIGHT);
      add(commonFlow, new Layout(1, 0, 1, 1));
    }
  }


  public static Tierlet.Result makeSearchForwardResult(final String query) {
    final Parameters params = new Parameters();
    params.addParameter(Pages.PARAM_QUERY, query);
    return Tierlet.Result.Done(Pages.PUBLIC_SEARCH, params);
  }


  /**
   * @return query
   */
  public String getQuery() {
    return flQuery == null ? "" : flQuery.getValue();
  }


  /**
   * Sets navigation navigationLinksToSet to be displayed on the left side.
   *
   * @param navigationLinksToSet
   */
  public void setNavigationLinks(final Flow navigationLinksToSet) {
    if (currentNavigationLinks == null) {
      add(navigationLinksToSet, new Layout(0, 0, 1, 1));
    } else {
      replace(currentNavigationLinks, navigationLinksToSet);
    }
    currentNavigationLinks = navigationLinksToSet;
  }


  private class SearchButtonPressedListener implements ButtonPressedListener {

    private static final long serialVersionUID = -4948672157490873173L;


    public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
      if (log.isDebugEnabled()) log.debug("search button pressed on quick search");
      // get query
      final String query = getQuery();
      if (StringUtils.isBlank(query)) return null; // do nothing
      // forward to advanced search page
      return makeSearchForwardResult(query);
    }
  }
}
