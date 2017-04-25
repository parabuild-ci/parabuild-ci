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

import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;
import viewtier.ui.Parameters;

/**
 * This class shows a page with build run's configuration.
 */
abstract class AbstractBuildRunResultPage extends BasePage {

  private static final long serialVersionUID = 2670365403954030583L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(AbstractBuildRunResultPage.class); // NOPMD

  /**
   * headerPanel holds a panel wiht an overview of the build
   * run and a panel with an overview of build steps.
   */
  protected final BuildRunHeaderPanel headerPanel = new BuildRunHeaderPanel();


  /**
   * Constructor.
   */
  public AbstractBuildRunResultPage() {
    super(FLAG_SHOW_QUICK_SEARCH | FLAG_SHOW_PAGE_HEADER_LABEL | FLAG_FLOATING_WIDTH);
    baseContentPanel().getUserPanel().add(headerPanel);
  }


  /**
   * In this class this is a trategy method that adds a header
   * and than delegates detailed layout to concrete classes.
   *
   * @see #executeBuildRunResultPage(Parameters, BuildRun)
   */
  protected final Result executePage(final Parameters params) {
//    if (log.isDebugEnabled()) log.debug("params: " + params);
    final BuildRun buildRun = getBuildRunFromParameters(params);
    if (buildRun == null) {
      return WebuiUtils.showBuildNotFound(this);
    } else {
      // authorise
      if (!getUserRigths(buildRun.getActiveBuildID()).isAllowedToViewBuild()) {
        baseContentPanel().getUserPanel().clear();
        return WebuiUtils.showNotAuthorized(this);
      }

      //
      // set title
      setTitle(buildRunResultPageTitle(buildRun));

      //
      // set page header label
      //
      setPageHeader("Results for Build " + buildRun.getBuildName() + " #" + buildRun.getBuildRunNumberAsString());
      setPageHeaderForeground(WebuiUtils.getBuildResultColor(getTierletContext(), buildRun));

      //
      // set nav links
      setNavigationLinks(makePreviousNextNavigationLinks(buildRun, params));

      //
      // set build run to header peanl
      headerPanel.setBuildRunLinkFactory(makeBuildRunURLFactory());
      headerPanel.showBuildRun(buildRun);
      headerPanel.setDescription(description(params));
      headerPanel.setWidth("100%");

      //
      // delegate further execution to the implementing method
      return executeBuildRunResultPage(params, buildRun);
    }
  }


  /**
   * Should return a build run from the given Parameter list.
   *
   * Typically this is done by checking Pages.PARAM_BUILD_RUN_ID
   * paramter. If build run ID is not avialable an implementtor
   * may chose finding build run using indirect ways. Example:
   * traversing back from log ID: log ID -> step run ID -> build
   * run ID -> build run.
   */
  protected BuildRun getBuildRunFromParameters(final Parameters params) {
    return ParameterUtils.getBuildRunFromParameters(params);
  }


  /**
   * Helper method do be used by implementors of executeBuildRunResultPage.
   * Returns either plain Done or Done with cache set to a year if build
   * run is complete.
   *
   * @return either plain Done or Done with cache set to a year if build
   * run is complete.
   */
  protected final Result makeDoneResult(final BuildRun buildRun) {
    final Result done = Result.Done();
    if (buildRun.completed()) {
      done.setClientCacheSeconds(Result.CACHE_SECONDS_YEAR);
    }
    return done;
  }


  protected abstract String description(final Parameters params);


  /**
   * Implementing classes should provide main handing in this
   * method. It is called after a common build result panel was
   * created and added to the layout. The content panel is
   * provided by {@link #baseContentPanel().getUserPanel()}
   * method.
   *
   * @param params that #executePage method was called with.
   * @param buildRun BuildRun to process.
   */
  protected abstract Result executeBuildRunResultPage(final Parameters params, final BuildRun buildRun);


  /**
   * Creates a page title. Implementing classes should provide
   * page titles that correspond the given page result.
   *
   * @param buildRun to create a title for.
   *
   * @return created page title.
   */
  protected abstract String buildRunResultPageTitle(final BuildRun buildRun);


  /**
   * @param buildRun the thapge was executed with.
   *
   * @param parameters that the page was executed with.
   * @return Flow, typically {@link PreviousNextLinks} that
   *  contant Prev/Next nav links that will be inserted into the
   *  right side header divider.
   */
  protected Flow makePreviousNextNavigationLinks(final BuildRun buildRun, final Parameters parameters) {
    return new PreviousNextLinks(null, null, null);
  }


  /**
   * @return a page-specific build run link factory.
   *
   * @see BuildRunURLFactory
   */
  protected abstract BuildRunURLFactory makeBuildRunURLFactory();
}
