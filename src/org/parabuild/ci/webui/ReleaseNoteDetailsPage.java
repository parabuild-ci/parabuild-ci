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

import org.apache.commons.logging.*;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This class shows a page with build run release note details.
 */
public final class ReleaseNoteDetailsPage extends AbstractBuildRunResultPage implements ConversationalTierlet {

  private static final Log log = LogFactory.getLog(ReleaseNoteDetailsPage.class);
  private static final long serialVersionUID = -1934992147911972765L; // NOPMD


  /**
   * Constructor
   */
  public ReleaseNoteDetailsPage() {
    setTitle(makeTitle("Release note details"));
  }


  protected String description(final Parameters params) {
    final Issue issue = getIssueFromParameters(params);
    if (issue == null) {
      return "Release note details";
    } else {
      return "Release note details for " + issue.getKey() + " - " + issue.getDescription();
    }
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
    final Issue issue = getIssueFromParameters(params);
    if (issue == null) {
      baseContentPanel().showErrorMessage("Requested release note cannot be found.");
      pnlContent.add(WebuiUtils.clickHereToContinue(Pages.PUBLIC_BUILDS));
    } else {
      pnlContent.add(new ReleaseNoteDetailsPanel(issue));
    }
    return makeDoneResult(buildRun);
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
    return "Details for release note for build " + buildRun.getBuildName() + " run #" + buildRun.getBuildRunNumber();
  }


  private Issue getIssueFromParameters(final Parameters params) {
    final Integer releaseNoteID = ParameterUtils.getIntegerParameter(params, Pages.PARAM_RELEASE_NOTE_ID, null);
    if (log.isDebugEnabled()) log.debug("releaseNoteID: " + releaseNoteID);
    if (releaseNoteID == null) return null;
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ReleaseNote releaseNote = (ReleaseNote)cm.getObject(ReleaseNote.class, releaseNoteID);
    if (log.isDebugEnabled()) log.debug("releaseNote: " + releaseNote);
    if (releaseNote == null) return null;
    final Issue issue = (Issue)cm.getObject(Issue.class, releaseNote.getIssueID());
    if (log.isDebugEnabled()) log.debug("issue: " + issue);
    return issue;
  }


  /**
   * @return a page-specific build run link factory.
   *
   * @see BuildRunURLFactory
   */
  protected BuildRunURLFactory makeBuildRunURLFactory() {
    return new StandardBuildRunURLFactory(Pages.BUILD_COFNIG_REPORT);
  }
}
