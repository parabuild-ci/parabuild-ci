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
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.search.SearchManager;
import org.parabuild.ci.search.SearchRequest;
import org.parabuild.ci.search.SearchRequestParameter;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Border;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

import java.io.IOException;

/**
 * This page is repsonsible for performing search of build
 * results.
 * <p/>
 * It works in to "modes" - if incoming request contains
 * parameter "query", it performs search acordingly to the
 * parameters and displays results. If there is no parameter, it
 * just shows an advanced search form.
 */
public final class SearchPage extends BasePage implements ConversationalTierlet {

  private static final Log log = LogFactory.getLog(SearchPage.class);

  private static final long serialVersionUID = 5113054037570114790L; // NOPMD
  private static final String TITLE = "Search";

  private final AdvancedSearchPanel pnlSearch;


  /**
   * Constructor
   */
  public SearchPage() {
    markTopMenuItemSelected(BasePage.MENU_SELECTION_SEARCH);
    setTitle(makeTitle(TITLE));
    setFocusOnFirstInput(true);
    pnlSearch = new AdvancedSearchPanel();
    pnlSearch.setAlignX(Layout.CENTER);
    pnlSearch.setWidth(Pages.PAGE_WIDTH);
    pnlSearch.clearMessage();
    baseContentPanel().add(pnlSearch);
  }


  public Result executePage(final Parameters params) {

    // REVIEWME: simeshev@parabuilci.org -> add test

    setTitle(makeTitle(TITLE));

    if (!super.isValidUser() && !org.parabuild.ci.security.SecurityManager.getInstance().isAnonymousAccessEnabled()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.PUBLIC_SEARCH, params);
    }

    // is request for search?
    final String query = params.getParameterValue(Pages.PARAM_QUERY);
    if (StringUtils.isBlank(query)) {
      // TODO: replace with normal button handling.
      // the query is blank, but is possible the search
      // button did not get pressed, so we check if the
      // field is not blank.
      if (!StringUtils.isBlank(pnlSearch.getQuery())) {
        search();
      }
    } else {
      // do search
      pnlSearch.setQuery(query);
      final String buildID = params.getParameterValue(Pages.PARAM_BUILD_ID);
      if (StringUtils.isValidInteger(buildID)) {
        pnlSearch.setBuildID(Integer.parseInt(buildID));
      }
      search();
    }
    return Result.Done();
  }


  /**
   * Performs search according to search panel values.
   */
  private void search() {
    final String query = pnlSearch.getQuery();
//    if (log.isDebugEnabled()) log.debug("query: " + query);
    if (StringUtils.isBlank(query)) return; // do nothing

    // add separator
    final Label separator = new Label();
    separator.setBorder(Border.TOP, 1, Pages.COLOR_PANEL_BORDER);
    separator.setWidth(Pages.PAGE_WIDTH);
    super.baseContentPanel().add(WebuiUtils.makePanelDivider());
    super.baseContentPanel().add(separator);

    // compose search request
    final SearchRequest sr = new SearchRequest(query);
//    if (log.isDebugEnabled()) log.debug("sr: " + sr.toString());
    sr.addParameter(SearchRequestParameter.BUILD_ID, Integer.toString(pnlSearch.getBuildID()));

    // do search
    try {
      final Hits hits = SearchManager.getInstance().search(sr);
      final int hitsCount = hits.length();
      if (log.isDebugEnabled()) log.debug("hits: " + hitsCount);
      if (hitsCount > 0) {
        // show search result
        super.baseContentPanel().add(new BoldCommonLabel("Search result(s):"));
        super.baseContentPanel().add(new SearchResultComponent(hits));
      } else {
        // show not found
        final Flow flEmptyResult = new Flow()
                .add(new CommonLabel("You search - "))
                .add(new BoldCommonLabel(query))
                .add(new CommonLabel(" - did not match any build results. "));
        super.baseContentPanel().add(flEmptyResult);
      }
    } catch (final IOException e) {
      pnlSearch.showErrorMessage("There was an error while perforning requested search. The error has been reported. Please contact your build administrator.");
      reportErrorToAdministrator(e);
    } catch (final ParseException e) {
      pnlSearch.showErrorMessage("Query is not valid. Please review it and repeat search.");
    }
  }


  /**
   * Helper method.
   */
  private static void reportErrorToAdministrator(final Exception e) {
    final Error error = new Error(StringUtils.toString(e));
    error.setDetails(e);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_SEARCH);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSendEmail(false);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }
}
