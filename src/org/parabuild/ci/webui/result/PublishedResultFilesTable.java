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
package org.parabuild.ci.webui.result;

import java.util.*;

import org.parabuild.ci.archive.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This table contains published build run results. Published
 * build run results are assocoated with a given result group.
 */
final class PublishedResultFilesTable extends AbstractFlatTable {

  private static final int COLUMN_COUNT = 5;

  private static final String WIDTH_FILES = "30%";
  private static final String WIDTH_DESCRIPTION = "25%";
  private static final String WIDTH_BUILD_LINK = "25%";
  private static final String WIDTH_PINNED = "5%";
  private static final String WIDTH_COMMANDS = "15%";

  private static final int COL_FILES = 0;
  private static final int COL_DESCRIPTION = 1;
  private static final int COL_BUILD_LINK = 2;
  private static final int COL_PINNED = 3;
  private static final int COL_COMMANDS = 4;

  private List results = new ArrayList();


  /**
   * Populates the table with content of the given result group.
   *
   * @param publishedStepResults a list of {@link PublishedStepResult} objects
   */
  public PublishedResultFilesTable(final List publishedStepResults) {
    super(COLUMN_COUNT, false);
    this.setHeaderVisible(false);
    this.setAddCommandVisible(false);
    this.results = publishedStepResults;
    this.populate();
  }


  /**
   */
  protected Component[] makeHeader() {
    return new Component[]{
      new TableHeaderLabel("File(s)", WIDTH_FILES),
      new TableHeaderLabel("Description", WIDTH_DESCRIPTION),
      new TableHeaderLabel("Build", WIDTH_BUILD_LINK),
      new TableHeaderLabel("", WIDTH_PINNED),
      new TableHeaderLabel("", WIDTH_COMMANDS), // commands don't title
    };
  }


  /**
   * Makes row, should be implemented by successor class
   *
   */
  protected Component[] makeRow(final int rowIndex) {
    return new Component[]{
      new StepResultPanel(),
      new CommonLabel(),
      new PublishedResultLinkFlow(),
      new CommonLabel(),
      new RowCommandsFlow(),
    };
  }


  /**
   * This implementation of this abstract method is called when
   * the table wants to fetch a row with a given rowIndex.
   * Implementing method should fill the data corresponding the
   * given rowIndex.
   *
   * @return this method should return either TBL_ROW_FETCHED or
   *         TBL_NO_MORE_ROWS if the requested row is out of
   *         range.
   *
   * @see AbstractFlatTable#TBL_ROW_FETCHED
   * @see AbstractFlatTable#TBL_NO_MORE_ROWS
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {

    if (rowIndex >= results.size()) return TBL_NO_MORE_ROWS;
    final PublishedStepResult psr = (PublishedStepResult)results.get(rowIndex);
    final Component[] row = getRow(rowIndex);

    // files

    final StepResultPanel pnlStepResult = (StepResultPanel)row[COL_FILES];
    // REVIEWME: vimeshev - 2006-09-28 - performance can be poor
    // because a) we create ArchiveManager; b) we go to the DB for
    // step result for every row. Consider using a composite VO
    // for PublishedStepResult and for StepResult.
    final int activeBuildID = psr.getActiveBuildID();
    final ArchiveManager archiveManager = ArchiveManagerFactory.getArchiveManager(psr.getActiveBuildID());
    final StepResult stepResult = ConfigurationManager.getInstance().getStepResult(psr.getStepResultID());
    pnlStepResult.setStepResult(activeBuildID, archiveManager, stepResult, false);

    // description

    final Label lbDescription = (Label)row[COL_DESCRIPTION];
    lbDescription.setText(stepResult.getDescription());

    // build

    final String caption = psr.getBuildName() + " #" + Integer.toString(psr.getBuildRunNumber());
    final PublishedResultLinkFlow resultLinkFlow = (PublishedResultLinkFlow)row[COL_BUILD_LINK];
    resultLinkFlow.setBuildRun(caption, psr.getBuildRunID(), currentUserCanSeeBuild(psr.getActiveBuildID()));

    // pinned

    final Label lbPinned = (Label)row[COL_PINNED];
    lbPinned.setText(stepResult.isPinned() ? "Pinned" : "");
    lbPinned.setAlignX(Layout.CENTER);

    // commands

    final RowCommandsFlow flwCommands = (RowCommandsFlow)row[COL_COMMANDS];
    flwCommands.setResult(psr.getID(), psr.getResultGroupID(), currentUserCanUnpublishStepResult(psr.getActiveBuildID()));

    // done

    return TBL_ROW_FETCHED;
  }


  /**
   * Helper method.
   *
   * @param activeBuildID
   *
   * @return true if current user allowed to unpublish results
   *  for the given active build ID.'
   */
  private boolean currentUserCanUnpublishStepResult(final int activeBuildID) {
    final SecurityManager sm = SecurityManager.getInstance();
    return sm.getUserBuildRights(sm.getUserFromContext(getTierletContext()), activeBuildID).isAllowedToPublishResults();
  }


  /**
   * Helper method.
   *
   * @param activeBuildID
   *
   * @return true if current user allowed to view build results
   *  with the given active build ID.
   */
  private boolean currentUserCanSeeBuild(final int activeBuildID) {
    return SecurityManager.getInstance().userCanViewBuild(SecurityManager.getInstance().getUserIDFromContext(getTierletContext()), activeBuildID);
  }


  private static final class RowCommandsFlow extends Flow {

    public void setResult(final int publishedResultID, final int resultGroupID, final boolean showUnpublishCommand) {
      reset();
      if (showUnpublishCommand) {
        final Properties props = new Properties();
        props.setProperty(Pages.PARAM_PUBLISHED_RESULT_ID, Integer.toString(publishedResultID));
        props.setProperty(Pages.PARAM_RESULT_GROUP_ID, Integer.toString(resultGroupID));
        add(new CommonCommandLink("Unpublish", Pages.RESULT_GROUP_UNPUBLISH_RESULT, props));
      }
    }
  }
}
