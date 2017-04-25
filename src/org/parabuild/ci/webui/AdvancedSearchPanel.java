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

import org.parabuild.ci.common.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * Shows advanced search controls.
 */
public final class AdvancedSearchPanel extends MessagePanel {

  private static final long serialVersionUID = 3185882169076162293L; // NOPMD

  public static final String FIELD_NAME_QUERY = "query";
  public static final String FIELD_NAME_DAYS = "days";
  public static final String FIELD_NAME_SEARCH = "search";
  public static final String FIELD_NAME_BUILD = "build";

  public static final String CAPTION_DATE = "Date:";
  public static final String CAPTION_FIND_RESULTS = "Find results:";
  public static final String CAPTION_SEARCH = "         Search         ";
  public static final String CAPTION_BUILD = "Build:";

  private CommonField flQuery = null;
  private CommonButton btSearch = null;
  /** @noinspection FieldCanBeLocal*/
  private DaysAgoDropDown ddDays = null; // NOPMD
  private BuildNameDropDown ddBuild = null;
  private boolean searchRequested = false;


  public AdvancedSearchPanel() {
    super(false); // no border

    // create components
    flQuery = new CommonField(50, 50);
    flQuery.setName(FIELD_NAME_QUERY);

    ddDays = new DaysAgoDropDown();
    ddDays.setName(FIELD_NAME_DAYS);

    ddBuild = new BuildNameDropDown();
    ddBuild.setName(FIELD_NAME_BUILD);

    btSearch = new CommonButton(CAPTION_SEARCH);
    btSearch.setName(FIELD_NAME_SEARCH);
    btSearch.setAlignX(Layout.CENTER);

    // layout components
    final GridIterator gridIterator = new GridIterator(getUserPanel(), 2);
    gridIterator.addPair(new BoldCommonLabel(CAPTION_FIND_RESULTS), flQuery);
    gridIterator.addPair(new BoldCommonLabel(CAPTION_DATE), ddDays);
    gridIterator.addPair(new BoldCommonLabel(CAPTION_BUILD), ddBuild);
    gridIterator.add(WebuiUtils.makeHorizontalDivider(5), 2);
    gridIterator.add(btSearch, 2);

    // set up handler
    btSearch.addListener(new ButtonPressedListener() {
      public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
        searchRequested = StringUtils.isBlank(getQuery());
//        // redirect to advanced search page
//        Parameters parameters = new Parameters();
//        parameters.addParameter(Pages.PARAM_QUERY, getQuery());
//        parameters.addParameter(Pages.PARAM_BUILD_ID, Integer.toString(getBuildID()));
//        return Tierlet.Result.Done(Pages.PUBLIC_SEARCH, parameters);
        return null;

      }
    });
  }


  /**
   * Sets query string.
   *
   * @param query
   */
  public void setQuery(final String query) {
    flQuery.setValue(query);
  }


  /**
   * @return the query string.
   */
  public String getQuery() {
    return flQuery.getValue();
  }


  public boolean isSeachRequested() {
    return btSearch.isButtonPressed();
  }


  /**
   * @return selected buildID or BuildConfig.UNSAVED_ID if "Any"
   *         selected.
   */
  public int getBuildID() {
    return ddBuild.getCode();
  }


  /**
   * Sets buildID
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    ddBuild.setCode(buildID);
  }


  /**
   * @return true if search button was explicetely pressed.
   */
  public boolean isSearchRequested() {
    return searchRequested;
  }
}
