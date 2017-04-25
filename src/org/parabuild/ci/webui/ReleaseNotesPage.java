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

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This class is a build run release notes list page front page.
 */
public final class ReleaseNotesPage extends AbstractBuildRunResultPage implements ConversationalTierlet {

  private static final long serialVersionUID = -1934992147911972765L; // NOPMD
  private static final String RELEASE_NOTES = "Release notes";


  /**
   * Constructor
   */
  public ReleaseNotesPage() {
    setTitle(makeTitle(RELEASE_NOTES));
  }


  protected String description(final Parameters params) {
    return RELEASE_NOTES;
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
    // show table with issues
    final Panel pnlContent = baseContentPanel().getUserPanel();
    final ReleaseNotesTable releaseNotesTable = new ReleaseNotesTable();
    releaseNotesTable.populateFromBuildRun(buildRun.getBuildRunID());
    pnlContent.add(releaseNotesTable);
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
    return "Release notes for build " + buildRun.getBuildName() + " run #" + buildRun.getBuildRunNumber();
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
