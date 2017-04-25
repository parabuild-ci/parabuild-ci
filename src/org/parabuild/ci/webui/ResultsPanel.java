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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * Holds build results table, controls and results history table.
 *
 * @see ResultsPage
 */
final class ResultsPanel extends MessagePanel {

  private static final long serialVersionUID = -9042765883093625531L;

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log LOG = LogFactory.getLog(ResultsPanel.class); // NOPMD

  private final ResultsTable tblResults;
  private final int buildRunID;


  public ResultsPanel(final BuildRun buildRun, final boolean editable, final BuildRights userBuildRights) {
    showContentBorder(false);

    final Panel cp = getUserPanel();
    buildRunID = buildRun.getBuildRunID();

    // get results

    final ConfigurationManager cm = ConfigurationManager.getInstance();

    // set up results table

    final boolean resultsEditable = editable && userBuildRights.isAllowedToDeleteResults();
    final int buildID = buildRun.getBuildID();
    if (LOG.isDebugEnabled()) LOG.debug("Creating ResultsTable for buildID: " + buildID);
    tblResults = new ResultsTable(buildID, resultsEditable);
    tblResults.setWidth(editable ? Pages.PAGE_WIDTH - 15 : Pages.PAGE_WIDTH);
    tblResults.populate(buildRun);
    cp.add(tblResults);

    // set up controls for publishing results

    if (userBuildRights.isAllowedToPublishResults()) {
      tblResults.setPublisingControlsVisible(true);
    }

    // set up buttons

    if (editable) {
      final SaveButton btnSave = makeSaveButton();
      final CancelButton btnCancel = makeCancelButton();
      final CommonFlow saveCancelFlow = new CommonFlow(btnSave, new Label(" "), btnCancel);
      saveCancelFlow.setAlignX(Layout.CENTER);
      saveCancelFlow.setBackground(Pages.COLOR_PANEL_HEADER_BG);
      cp.add(WebuiUtils.makePanelDivider());
      cp.add(saveCancelFlow);
    } else {
      // get build run action LOG
      final List buildRunActionLogVOs = cm.getBuildRunActionLogVOs(buildRunID);
      if (!buildRunActionLogVOs.isEmpty()) {
        cp.add(WebuiUtils.makePanelDivider());
        cp.add(new ResultsActionsTable(buildRunActionLogVOs));
      }
    }
  }


  /**
   * Makes "Save" button and attaches click handler to it.
   */
  private SaveButton makeSaveButton() {
    final SaveButton saveButton = new SaveButton();
    saveButton.addListener(new ButtonPressedListener() {

      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        if (tblResults.validate()) {
          if (LOG.isDebugEnabled()) LOG.debug("Results table is valid");
          tblResults.save();
          return createDoneGotoResultsPage();
        } else {
          if (LOG.isDebugEnabled()) LOG.debug("Results table was invalid");
          return Tierlet.Result.Continue();
        }
      }
    });
    return saveButton;
  }


  private Tierlet.Result createDoneGotoResultsPage() {
    return Tierlet.Result.Done(Pages.BUILD_RESULTS, ParameterUtils.propertiesToParameters(
      ParameterUtils.makeBuildRunResultsParameters(buildRunID, Boolean.FALSE)));
  }


  /**
   * Makes "Cancel" button and attaches click handler to it.
   */
  private CancelButton makeCancelButton() {
    final CancelButton cancelButton = new CancelButton();
    cancelButton.addListener(new ButtonPressedListener() {

      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        return createDoneGotoResultsPage();
      }
    });
    return cancelButton;
  }
}
