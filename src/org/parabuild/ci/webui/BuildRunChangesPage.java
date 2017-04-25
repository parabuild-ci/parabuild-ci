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

import java.util.*;
import javax.servlet.http.*;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This class shows a page with a list of changes in the given
 * build run.
 */
public final class BuildRunChangesPage extends AbstractBuildRunResultPage implements ConversationalTierlet {

  private static final long serialVersionUID = 2670365403954030583L;  // NOPMD

  private static final BuildRunURLFactory BUILD_RUN_URL_FACTORY = new StandardBuildRunURLFactory(Pages.BUILD_CHANGES);

  public static final String COOKIE_SHOW_FILES = "parabuild_show_chanagelist_files";
  public static final String TITLE_BUILD_RESULT = "Build result";


  /**
   * Constructor
   */
  public BuildRunChangesPage() {
    setTitle(makeTitle(TITLE_BUILD_RESULT));
  }


  protected String description(final Parameters params) {
    return "Changes";
  }


  /**
   * Implementing classes should provide main handing in this
   * method. It is called after a common build result panel was
   * created and added to the layout. The content panel is
   * provided by {@link #baseContentPanel().getUserPanel()}
   * method.
   *
   * @param params that #executePage method was called with.
   *@param buildRun BuildRun to process.
   */
  protected Result executeBuildRunResultPage(final Parameters params, final BuildRun buildRun) {
    final Panel pnlContent = baseContentPanel().getUserPanel();

    final boolean showFiles = WebuiUtils.getShowFilesFromParamOrCookie(Pages.PARAM_SHOW_FILES, COOKIE_SHOW_FILES, getTierletContext().getHttpServletRequest());
    saveShowFilesInCookie(showFiles);

    // show/hide files link
    final Properties showFilesParameters = new Properties();
    showFilesParameters.setProperty(Pages.PARAM_BUILD_RUN_ID, String.valueOf(buildRun.getBuildRunID()));
    final CommonLink lnkShowHideFiles = new ShowHideFilesCommandLink(showFiles, Pages.BUILD_CHANGES, showFilesParameters);
    lnkShowHideFiles.setAlignX(Layout.LEFT);
    lnkShowHideFiles.setHeight(HEADER_DIVIDER_HEIGHT);
    pnlContent.add(lnkShowHideFiles);

    // summary/header
    final ChangeListsPanel pnlChanges = new ChangeListsPanel();
    pnlChanges.setShowFiles(showFiles);
    pnlChanges.showChangeLists(buildRun.getActiveBuildID(), ConfigurationManager.getInstance().getChangeListsOrderedByDate(buildRun.getBuildRunID()));
    pnlContent.add(pnlChanges);

    return makeDoneResult(buildRun);
  }


  /**
   * @return Flow that contant Prev/Next nav links that will be
   *  inserted into the right side header divider.
   *
   * @param buildRun the thapge was executed with.
   * @param parameters that the page was executed with.
   *
   */
  protected Flow makePreviousNextNavigationLinks(final BuildRun buildRun, final Parameters parameters) {
    final StandardPreviousNextLinksMaker linksMaker = new StandardPreviousNextLinksMaker(Pages.BUILD_CHANGES);
    return linksMaker.makeLinks(parameters, buildRun, getTierletContext());
  }


  /**
   * @return a page-specific build run link factory.
   *
   * @see BuildRunURLFactory
   */
  protected BuildRunURLFactory makeBuildRunURLFactory() {
    return BUILD_RUN_URL_FACTORY;
  }


  /**
   * Creates a page title. Implementing classes should provide
   * page titles that correspond the given page result.
   *
   * @param buildRun to create a title for.
   *
   * @return created page title.
   */
  protected String buildRunResultPageTitle(final BuildRun buildRun) {
    return "Changes in build " + buildRun.getBuildName() + " run #" + buildRun.getBuildRunNumber();
  }


  /**
   * Saves show files in a cookie for future use.
   */
  private void saveShowFilesInCookie(final boolean showFiles) {
    getTierletContext().addCookie(new Cookie(COOKIE_SHOW_FILES, Boolean.toString(showFiles)));
  }
}
