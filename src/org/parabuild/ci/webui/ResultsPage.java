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

import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.security.BuildRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;

import java.util.Properties;

/**
 * This class shows list of available build results.
 *
 * @see Pages#PARAM_BUILD_RUN_ID
 */
public final class ResultsPage extends AbstractBuildRunResultPage implements ConversationalTierlet {

  private static final long serialVersionUID = -3767571546131357778L; // NOPMD

  private static final String TITLE = "Build Results";

  public ResultsPage() {
    setTitle(makeTitle(TITLE));
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
    return makeTitle(TITLE) + " >> " + buildRun.getBuildName() + " >> " + buildRun.getBuildRunNumberAsString();
  }


  protected String description(final Parameters params) {
    return TITLE;
  }


  /**
   * It is called after a common build result panel was
   * created and added to the layout. The content panel is
   * provided by {@link #baseContentPanel().getUserPanel()}
   * method.
   *
   * @param params that #executePage method was called with.
   * @param buildRun BuildRun to process.
   */
  protected Result executeBuildRunResultPage(final Parameters params, final BuildRun buildRun) {
    final Panel cp = baseContentPanel().getUserPanel();
    if (isNew()) {

      // calculate edit mode

      final SecurityManager sm = SecurityManager.getInstance();
      final BuildRights userBuildRights = sm.getUserBuildRights(getUser(), buildRun.getActiveBuildID());
      final boolean editMode = ParameterUtils.getEditFromParameters(params);

      // add edit link if neccesary/possible
      if (!editMode && userBuildRights.isAllowedToListResultCommands()) {
        final Properties manageLinkParameters = ParameterUtils.makeBuildRunResultsParameters(buildRun.getBuildRunID(), Boolean.TRUE);
        cp.add(new CommonCommandLink("Manage Results", Pages.BUILD_RESULTS, manageLinkParameters));
      }

      // add panel
      cp.add(new ResultsPanel(buildRun, editMode, userBuildRights));

      if (editMode) {
        // the rest will be handled by panel controls
        return Result.Continue();
      } else {
        // normal stateless view mode
        return makeDoneResult(buildRun);
      }
    } else {
      return Result.Continue();
    }
  }


  /**
   * @param buildRun the thapge was executed with.
   *
   * @param parameters that the page was executed with.
   * @return Flow, typically {@link PreviousNextLinks} that
   *  contant Prev/Next nav links that will be inserted into the
   *  right side header divider.
   */
  protected Flow makePreviousNextNavigationLinks(final BuildRun buildRun, final Parameters parameters) {
    return new StandardPreviousNextLinksMaker(Pages.BUILD_RESULTS).makeLinks(parameters, buildRun, getTierletContext());
  }


  protected BuildRunURLFactory makeBuildRunURLFactory() {
    return new StandardBuildRunURLFactory(Pages.BUILD_RESULTS);
  }
}
