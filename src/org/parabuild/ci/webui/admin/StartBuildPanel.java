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
package org.parabuild.ci.webui.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildVersionDuplicateValidator;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.StartButton;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Tierlet;

/**
 * Panel to hold build start paramters and contol buttons (Start/Cancel);
 */
public final class StartBuildPanel extends MessagePanel {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(StartBuildPanel.class); // NOPMD
  private static final long serialVersionUID = -194369566029085673L; // NOPMD

  private ManualStartParametersPanel pnlStartParameters = null;
  private final int activeBuildID;
  private final String buildName;
  private final boolean publishingRun;


  public StartBuildPanel(final BuildConfig buildConfig, final boolean publishingRun) {
    super.showContentBorder(false);
    super.showHeaderDivider(true);

    this.publishingRun = publishingRun;
    this.activeBuildID = buildConfig.getActiveBuildID();
    this.buildName = buildConfig.getBuildName();
    this.pnlStartParameters = new ManualStartParametersPanel(activeBuildID, buildConfig.isScheduled(),
            publishingRun ? StartParameterType.PUBLISH : StartParameterType.BUILD, WebUIConstants.MODE_EDIT);

    // create run button
    final Button btStart = new StartButton();
    btStart.addListener(new StartPressedListener());

    // create cancel button
    final Button btCancel = new CancelButton();
    btCancel.addListener(new CancelPressedListener());

    // align
    final GridIterator gi = new GridIterator(getUserPanel(), 1);
    final CommonFlow startCancelFlow = new CommonFlow(btStart, new Label("  "), btCancel);
    startCancelFlow.setAlignX(Layout.CENTER);
    startCancelFlow.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    gi.add(pnlStartParameters, 1).add(WebuiUtils.makePanelDivider(), 1);
    gi.add(startCancelFlow, 1);
  }


  /**
   * This listener is called when "Start" burron is pressed.
   */
  private final class StartPressedListener implements ButtonPressedListener {

    private static final long serialVersionUID = -4735396899994177361L;


    public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
      // validate
      if (!validateInput()) return Tierlet.Result.Continue();
      try {
        // compose a manual build start request
        final int userIDFromRequest = org.parabuild.ci.security.SecurityManager.getInstance().getUserIDFromContext(getTierletContext());
        final BuildStartRequest startRequest = new BuildStartRequest(
                BuildStartRequest.REQUEST_NORMAL, userIDFromRequest,
                ChangeList.UNSAVED_ID,
                BuildRun.UNSAVED_ID,
                pnlStartParameters.getStartParameterList(),
                pnlStartParameters.getLabel(),
                pnlStartParameters.getNote(),
                pnlStartParameters.isPinResult(),
                pnlStartParameters.getVersionTemplate(),
                pnlStartParameters.getVersionCounter(),
                pnlStartParameters.getManualScheduleParameters());
        startRequest.setSkipNextScheduledBuild(pnlStartParameters.isSkipNextScheduledBuild());
        startRequest.setIgnoreSerialization(pnlStartParameters.isStartIfBuilding());
        startRequest.setCleanCheckout(pnlStartParameters.isClearWorkspace());
        startRequest.setPublishingRun(publishingRun);
        startRequest.setAgentHost(pnlStartParameters.getDesiredAgentHost());
        startRequest.setAgentHostRequired(pnlStartParameters.getDesiredAgentHost() != null);

        // Check if we have to show the the build was queued.
        final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
        final BuildManager bm = BuildManager.getInstance();
        final boolean showQueuedNotice = scm.isSerializedBuilds() && bm.getFreeAgentHosts(activeBuildID).isEmpty()
                && scm.isQueueManualStartRequests();

        // Start the build
        bm.startBuild(activeBuildID, startRequest);

        if (showQueuedNotice) {
          // Show "Queued" notice.
          getUserPanel().clear();
          getUserPanel().add(new CommonFlow(new CommonLabel("Your build request has been queued. The build will start it as soon as resources are available. "), WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILDS)));
          return Tierlet.Result.Done();
        } else {
          // Return to build statuses
          return WebuiUtils.createBuildActionReturnResult(getTierletContext());
        }
      } catch (final Exception e) {
        // Show error
        showErrorMessage("Unxpected error while starting build \"" + buildName + "\": " + StringUtils.toString(e));
        return Tierlet.Result.Continue();
      }
    }


    /**
     * Validates input.
     *
     * @return true if input is valid, false if is invalid.
     */
    private boolean validateInput() {
      // for scheduled build, validate that there is a
      // successful backing build
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      if (cm.getActiveBuildConfig(activeBuildID).getScheduleType() == BuildConfig.SCHEDULE_TYPE_RECURRENT) {
        final int backingBuildID = cm.getSourceControlSettingValue(activeBuildID, VersionControlSystem.REFERENCE_BUILD_ID, BuildConfig.UNSAVED_ID);
        if (cm.getLastCleanBuildRun(backingBuildID) == null) {
          pnlStartParameters.showErrorMessage("Please run the backing build cleanly before starting this build");
          return false;
        }
      }
      return pnlStartParameters.validate() && validateCounter();
    }


    private boolean validateCounter() {
      try {
        final BuildVersionDuplicateValidator duplicateValidator = new BuildVersionDuplicateValidator();
        duplicateValidator.validate(activeBuildID, pnlStartParameters.getVersionTemplate(), buildName, pnlStartParameters.getVersionCounter());
      } catch (final ValidationException e) {
        pnlStartParameters.showErrorMessage(StringUtils.toString(e));
        return false;
      }
      return true;
    }
  }


  /**
   * This listener is called when "Cancel" burron is pressed.
   */
  private class CancelPressedListener implements ButtonPressedListener {

    private static final long serialVersionUID = -2069975221805082365L;


    public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
      return WebuiUtils.createBuildActionReturnResult(getTierletContext());
    }
  }
}
