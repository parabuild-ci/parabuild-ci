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

import java.io.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.ResultGroupManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.ResultGroup;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.result.BuildRunResultVO;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;

/**
 * This table contains build run results.
 *
 * @see ResultsPanel
 */
final class ResultsTable extends AbstractFlatTable {

  private static final long serialVersionUID = -5702964383649296134L;
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log LOG = LogFactory.getLog(ResultsTable.class); // NOPMD

  private static final int COLUMN_COUNT = 3;

  private static final String CAPTION_FILE_NAME = "File Name";
  private static final String CAPTION_BUILD_NAME = "Build Name";
  private static final String CAPTION_PINNED = "";

  private List resultVOs = new ArrayList(0);
  private final List deleted = new ArrayList(0);
  private final int buildID;

  /**
   * Build run ID used to populate this table or BuildRun.UNSAVED_ID if not populated.
   */
  private int buildRunID = BuildRun.UNSAVED_ID;


  /**
   * Constructor - creates an instance of flat table with given
   * number of columns
   *
   * @param buildID ID of the build configuration used to run this build.
   *
   * @param editable
   */
  ResultsTable(final int buildID, final boolean editable) {
    super(COLUMN_COUNT, editable);
    if (LOG.isDebugEnabled()) LOG.debug("Creating ResultsTable with buildID: " + buildID);
    this.buildID = buildID;
    setAddCommandVisible(false);
    populate();
  }


  /**
   * This notification method is called when a row is deleted.
   */
  public void notifyRowDeleted(final int index) {
    deleted.add(resultVOs.remove(index));
  }


  /**
   */
  protected Component[] makeHeader() {
    return new Component[]{
      new TableHeaderLabel(CAPTION_FILE_NAME, "60%"),
      new TableHeaderLabel(CAPTION_PINNED, "10%", Layout.CENTER),
      new TableHeaderLabel(CAPTION_BUILD_NAME, "30%"),
    };
  }


  /**
   * Makes row, should be implemented by successor class
   *
   */
  protected Component[] makeRow(final int rowIndex) {
    return new Component[]{
      new StepResultPanel(),
      new BoldCommonLabel(),
      new CommonLabel()
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
    if (rowIndex >= resultVOs.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final BuildRunResultVO resultVO = (BuildRunResultVO)resultVOs.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    //
    final StepResultPanel pnlStepResult = (StepResultPanel)row[0];
    final ArchiveManager am = ArchiveManagerFactory.getArchiveManager(resultVO.getActiveBuildID());
    pnlStepResult.setStepResult(resultVO.getActiveBuildID(), am, resultVO.getStepResult(), true);
    //
    final Label lbPinned = (Label)row[1];
    lbPinned.setText(resultVO.getStepResult().isPinned() ? "Pinned" : "");
    lbPinned.setAlignX(Layout.CENTER);
    //
    final Label lbBuildName = (Label)row[2];
    lbBuildName.setText(resultVO.getBuildName());
    return TBL_ROW_FETCHED;
  }


  /**
   */
  public void populate(final BuildRun buildRun) {
    buildRunID = buildRun.getBuildRunID();
    // add resuts as is
    addResultsToShow(buildRun.getBuildRunID(), buildRun);
    // add dependent build results that requested to present
    // them on the leader pages.
    if (buildRun.getDependence() == BuildRun.DEPENDENCE_LEADER) {
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      for (final Iterator i = cm.getAllParallelBuildRuns(buildRun).iterator(); i.hasNext();) {
        final BuildRun current = (BuildRun)i.next();
        if (current.getBuildRunID() == buildRun.getBuildRunID()) {
          continue; // skip seld
        }
        // check if it asked to be presented of the leader's page
        if (cm.getBuildAttributeValue(current.getBuildID(), BuildConfigAttribute.SHOW_RESULTS_ON_LEADER_PAGE, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED))
        {
          addResultsToShow(buildRun.getBuildRunID(), current);
        }
      }
    }
    populate();
  }


  /**
   * Adds results from the given run to the list of results
   * to show.
   */
  private void addResultsToShow(final int publisherBuildRunID, final BuildRun buildRun) {
    final List buildRunResults = ConfigurationManager.getInstance().getBuildRunResults(buildRun.getBuildRunID());
    for (int i = 0; i < buildRunResults.size(); i++) {
      final StepResult stepResult = (StepResult)buildRunResults.get(i);
//      if (LOG.isDebugEnabled()) LOG.debug("stepResult: " + stepResult);
      final BuildRunResultVO resultVO = new BuildRunResultVO();
      resultVO.setActiveBuildID(buildRun.getActiveBuildID());
      resultVO.setBuildDate(buildRun.getStartedAt());
      resultVO.setBuildName(buildRun.getBuildName());
      resultVO.setStepResult(stepResult);
      resultVO.setBuildRunNumber(buildRun.getBuildRunNumber());
      resultVO.setBuildRunID(buildRun.getBuildRunID());
      resultVO.setPublisherBuildRunID(publisherBuildRunID);
      resultVOs.add(resultVO);
    }
  }


  /**
   * Saves changes made to the table.
   */
  public void save() {
    if (LOG.isDebugEnabled()) LOG.debug("Saving...");
    // delete deleted from archive and the database.
    if (!deleted.isEmpty()) {
      // schedule deleting of the given results.
      for (final Iterator i = deleted.iterator(); i.hasNext();) {
        final BuildRunResultVO resultVO = (BuildRunResultVO)i.next();
        try {
          final ArchiveManager am = ArchiveManagerFactory.getArchiveManager(resultVO.getActiveBuildID());
          am.deleteResult(resultVO.getStepResult());
        } catch (final IOException e) {
          final Error error = new Error(e.toString());
          error.setSendEmail(false);
          ErrorManagerFactory.getErrorManager().reportSystemError(error);
        }
        i.remove();
      }
    }

    final ResultsTableCommandPanel pnlResultsTableCommand = (ResultsTableCommandPanel)getTableCommands();

    // pin step result if requested

    if (pnlResultsTableCommand.getPinResult()) {
      for (int i = 0, n = getRowCount(); i < n; i++) {
        if (isRowSelected(i)) {
          final BuildRunResultVO resultVO = (BuildRunResultVO)resultVOs.get(i);
          final StepResult stepResult = resultVO.getStepResult();
          if (!stepResult.isPinned()) {
            stepResult.setPinned(true);
            ConfigurationManager.getInstance().saveObject(stepResult);
          }
        }
      }
    }

    // publish result if requested

    final List actuallyPublishedResults = new ArrayList(5); // list of BuildRunResultVO to pass to publishing commands
    final int selectedResultGroupID = pnlResultsTableCommand.getSelectedResultGroupID();
    if (selectedResultGroupID != ResultGroup.UNSAVED_ID) {
      final ResultGroupManager rgm = ResultGroupManager.getInstance();
      final String comment = pnlResultsTableCommand.getComment();
      final Date publishedOnDate = new Date();
      final String selectedResultGroupName = pnlResultsTableCommand.getSelectedResultGroupName();
      final int userID = SecurityManager.getInstance().getUserIDFromContext(getTierletContext());
      for (int i = 0, n = getRowCount(); i < n; i++) {
        if (isRowSelected(i)) {
          final BuildRunResultVO resultVO = (BuildRunResultVO)resultVOs.get(i);
          final StepResult stepResult = resultVO.getStepResult();

          // find if already publlished
          if (rgm.getPublishedStepResult(stepResult.getID(), selectedResultGroupID) != null) {
            continue;
          }

          // publish
          rgm.publish(resultVO, selectedResultGroupID, stepResult, comment, publishedOnDate, selectedResultGroupName, userID);

          // add to list of published to pass to publishig commands
          actuallyPublishedResults.add(resultVO);
        }
      }
    }

    // Start publishing build run if requested

    final boolean runPublishCommands = pnlResultsTableCommand.getRunPublishCommands() == RunPublishingCommandsDropDown.CODE_RUN_COMMANDS;
    if (LOG.isDebugEnabled()) LOG.debug("runPublishCommands: " + runPublishCommands);
    if (LOG.isDebugEnabled()) LOG.debug("buildRunID: " + buildRunID);
    if (runPublishCommands && buildRunID != BuildRun.UNSAVED_ID) {
      final int userIDFromRequest = SecurityManager.getInstance().getUserIDFromContext(getTierletContext());
      if (LOG.isDebugEnabled()) LOG.debug("userIDFromRequest: " + userIDFromRequest);
      final BuildStartRequest startRequest = new BuildStartRequest(
        BuildStartRequest.REQUEST_RERUN, userIDFromRequest, -1,
        buildRunID,
        pnlResultsTableCommand.getUpdatedParameters(),
        "", // No label
        "Publishing run",
        false,
        "",
        0,
        Collections.EMPTY_LIST);
      // Important: to make sure it will pick up the
      // publishing commands.
      startRequest.setPublishingRun(true);
      startRequest.addPublishedResults(actuallyPublishedResults);
      if (LOG.isDebugEnabled()) LOG.debug("About to execute start request: " + startRequest);
      BuildManager.getInstance().reRunBuild(ConfigurationManager.getInstance().getActiveIDFromBuildID(buildID), startRequest);
    }
  }


  /**
   * Adds footer with editable commands. This method provides
   * default implementation. It can be overriden by successors to
   * provide specialized commands
   */
  protected TableCommands makeEditableCommands() {
    return new ResultsTableCommandPanel(this);
  }


  /**
   * Makes publishing controls visible.
   */
  public void setPublisingControlsVisible(final boolean visible) {
    final TableCommands commands = getTableCommands();
    if (commands != null && commands instanceof ResultsTableCommandPanel) { // NOPMD SimplifyConditional
      final ResultsTableCommandPanel commandPanel = (ResultsTableCommandPanel)getTableCommands();
      commandPanel.setPublisingControlsVisible(visible);
    }
  }


  public boolean validate() {
    if (LOG.isDebugEnabled()) LOG.debug("Validating results table...");
    final ResultsTableCommandPanel pnlCommands = (ResultsTableCommandPanel)getTableCommands();
    if (pnlCommands.getSelectedResultGroupID() != ResultGroup.UNSAVED_ID) {
      // check if there are rows selected to publish
      final int n = getRowCount();
      boolean selected = false;
      for (int i = 0; i < n; i++) {
        if (isRowSelected(i)) {
          selected = true;
          break;
        }
      }
      if (!selected) {
        showErrorMessage("Select at least one row to publish");
        return false;
      }
    }

    // Check if there are commands to run
    if (pnlCommands.getRunPublishCommands() == RunPublishingCommandsDropDown.CODE_RUN_COMMANDS) {
      final List publishingSteps = ConfigurationManager.getInstance().getEnabledBuildSequences(buildID, BuildStepType.PUBLISH);
      if (publishingSteps.isEmpty()) {
        showErrorMessage("There are no publishing commands defined for this build run.");
        return false;
      }
    }
    return true;
  }


  /**
   * @return build configuration ID for this table.
   */
  public int getBuildID() {
    return buildID;
  }


  public String toString() {
    return "ResultsTable{" +
      "resultVOs=" + resultVOs +
      ", deleted=" + deleted +
      '}';
  }
}
