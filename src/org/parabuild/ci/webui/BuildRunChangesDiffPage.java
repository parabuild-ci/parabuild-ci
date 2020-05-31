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
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Parameters;

/**
 * This class shows a page with a list of changes in the given
 * build run.
 */
public final class BuildRunChangesDiffPage extends AbstractBuildRunResultPage implements ConversationalTierlet {

  private static final long serialVersionUID = 2670365403954030583L;  // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BuildRunChangesDiffPage.class); // NOPMD
  public static final String TITLE_BUILD_RESULT = "Build result";

  private final DiffPanel pnlDiff = new DiffPanel(null, Pages.BUILD_DIFF, Pages.BUILD_DIFF, false, false);


  /**
   * Constructor
   */
  public BuildRunChangesDiffPage() {
    setTitle(makeTitle(TITLE_BUILD_RESULT));
    baseContentPanel().getUserPanel().add(pnlDiff);
    pnlDiff.setWidth("100%");
  }


  protected String description(final Parameters params) {
    return "Changes Between This and Another Build";
  }


  /**
   * Implementing classes should provide main handing in this
   * method. It is called after a common build result panel was
   * created and added to the layout. The content panel is
   * provided by {@link #baseContentPanel().getUserPanel()}
   * method.
   *
   * @param params   that #executePage method was called with.
   * @param buildRun BuildRun to process.
   */
  protected Result executeBuildRunResultPage(final Parameters params, final BuildRun buildRun) {
//    if (log.isDebugEnabled()) log.debug("params: " + params);

    // get params
    final Integer startBuildNumber = Integer.valueOf(buildRun.getBuildRunNumber());
    final Integer endBuildNumber = ParameterUtils.getIntegerParameter(params, Pages.PARAM_BUILD_END_NUMBER, null);

    // set title
    super.setTitle(makeTitle("Changes between this build #" + buildRun.getBuildRunNumberAsString() + ' ' + makeOtherBuildString(endBuildNumber)) + " for " + buildRun.getBuildName());

//    if (log.isDebugEnabled()) log.debug("endBuildNumber: " + endBuildNumber);
//    if (log.isDebugEnabled()) log.debug("startBuildNumber: " + startBuildNumber);

    // create query panel
    pnlDiff.setBuildStartNumber(startBuildNumber);
    pnlDiff.setBuildEndNumber(endBuildNumber);

    // run search if params are OK
    if (startBuildNumber != null && endBuildNumber != null
            && startBuildNumber > 0 && endBuildNumber > 0) {
      pnlDiff.display(startBuildNumber, endBuildNumber);
    }

    return makeDoneResult(buildRun);
  }


  private static String makeOtherBuildString(final Integer endBuildNumber) {
    if (endBuildNumber == null) {
      return " and other build";
    }
    return " and build #" + endBuildNumber.toString();
  }


  /**
   * @param buildRun   the page was executed with.
   * @param parameters that the page was executed with.
   * @return Flow that contains Prev/Next nav links that will be
   *         inserted into the right side header divider.
   */
  protected Flow makePreviousNextNavigationLinks(final BuildRun buildRun, final Parameters parameters) {
    final StandardPreviousNextLinksMaker linksMaker = new StandardPreviousNextLinksMaker(Pages.BUILD_CHANGES);
    return linksMaker.makeLinks(parameters, buildRun, getTierletContext());
  }


  /**
   * @return a page-specific build run link factory.
   * @see BuildRunURLFactory
   */
  protected BuildRunURLFactory makeBuildRunURLFactory() {
    return new StandardBuildRunURLFactory(Pages.BUILD_CHANGES);
  }


  /**
   * Creates a page title. Implementing classes should provide
   * page titles that correspond the given page result.
   *
   * @param buildRun to create a title for.
   * @return created page title.
   */
  protected String buildRunResultPageTitle(final BuildRun buildRun) {
    return "Changes in build " + buildRun.getBuildName() + " run #" + buildRun.getBuildRunNumber();
  }
}
