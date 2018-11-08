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
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.StartButton;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel to hold re-run configuration entered by a build
 * administrator.
 */
public final class ReRunBuildPanel extends MessagePanel {

  private static final long serialVersionUID = -294361566029085672L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(ReRunBuildPanel.class); // NOPMD
  private static final String CAPTION_BUILD_NUMBER = "Build run number: ";

  private final Field flBuildNumber = new CommonField(10, 10);
  private final Button btnStart = new StartButton();
  private final Button btnCancel = new CancelButton();
  private final Button btnLoadParameters = new CommonButton("Load parameters");

  private final int activeBuildID;
  private int lastLoadedBuildRunID = BuildRun.UNSAVED_ID;
  private ManualStartParametersPanel pnlParameters = null;
  private final String buildName;


  /**
   * Creates build re-run panel.
   */
  public ReRunBuildPanel(final ActiveBuildConfig activeBuildConfig) {
    super("Re-run build " + activeBuildConfig.getBuildName());
    super.showContentBorder(false);
    super.showHeaderDivider(true);

    // set build id
    activeBuildID = activeBuildConfig.getActiveBuildID();
    ArgumentValidator.validateBuildIDInitialized(activeBuildID);

    // set build name
    buildName = activeBuildConfig.getBuildName();

    // add listeners
    btnLoadParameters.addListener(new ButtonPressedListener() {
      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        if (validateBuildNumber()) {
          // get manual parameters from that build run we are going to run
          final BuildRun buildRun = getFirstBuildRunFromEnteredRunNumber();
          final ConfigurationManager cm = ConfigurationManager.getInstance();
          final BuildRunConfig buildRunConfig = cm.getBuildRunConfig(buildRun);

          // init the last loaded build run ID
          lastLoadedBuildRunID = buildRun.getBuildRunID();

          // create panel
          final StartParameterType parameterType = buildRun.getType() == BuildRun.TYPE_PUBLISHING_RUN ? StartParameterType.PUBLISH : StartParameterType.BUILD;
          final BuildConfig buildConfig = cm.getActiveBuildConfig(buildRun.getActiveBuildID());
          pnlParameters = new ManualStartParametersPanel(buildRunConfig.getBuildID(), buildConfig.isScheduled(), parameterType, WebUIConstants.MODE_EDIT);
          final Integer versionCounter = cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.VERSION_COUNTER, (Integer)null);
          if (versionCounter != null) {
            pnlParameters.setVersionCounter(versionCounter);
          }

          // layout
          layoutCleanly();
        }
        return Tierlet.Result.Continue();
      }
    });
    btnCancel.addListener(new CancelPressedListener());
    btnStart.addListener(new StartPressedListener());

    layoutCleanly();
  }


  private void layoutCleanly() {
    final Panel userPanel = getUserPanel();
    userPanel.clear();
    final GridIterator gi = new GridIterator(userPanel, 2);
    gi.add(new CommonFlow(new CommonFieldLabel(CAPTION_BUILD_NUMBER), flBuildNumber, new Label("   "), btnLoadParameters), 2);
    gi.add(WebuiUtils.makePanelDivider(), 2);
    if (pnlParameters != null) {
      gi.add(pnlParameters, 2);
    }
    final CommonFlow startCancelFlow = new CommonFlow(btnStart, new Label("   "), btnCancel);
    startCancelFlow.setAlignX(Layout.CENTER);
    gi.add(startCancelFlow, 2);
    gi.add(WebuiUtils.makePanelDivider(), 2);
  }


  /**
   * Requests build manager to start.
   */
  private void requestReRun() {
    // get build run ID
    final BuildRun buildRun = getFirstBuildRunFromEnteredRunNumber();
    final BuildManager buildManager = BuildManager.getInstance();
    final SecurityManager securityManager = SecurityManager.getInstance();
    final int userIDFromRequest = securityManager.getUserIDFromContext(getTierletContext());
    final BuildStartRequest startRequest = new BuildStartRequest(
      BuildStartRequest.REQUEST_RERUN, userIDFromRequest, -1,
      buildRun.getBuildRunID(),
      getStartParamterList(),
      getLabel(),
      pnlParameters.getNote(),
      pnlParameters.isPinResult(),
      pnlParameters.getVersionTemplate(),
      pnlParameters.getVersionCounter(),
      pnlParameters.getManualScheduleParameters());
//    if (log.isDebugEnabled()) log.debug("Will re-run buildRun: " + buildRun);
//    if (log.isDebugEnabled()) log.debug("       start request: " + startRequest);
    buildManager.reRunBuild(activeBuildID, startRequest);
  }


  private BuildRun getFirstBuildRunFromEnteredRunNumber() {
    try {
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final List buildRunListByNumber = cm.getBuildRunListByNumber(activeBuildID, Integer.parseInt(flBuildNumber.getValue()));
      if (buildRunListByNumber.isEmpty()) {
        return null;
      }
      return (BuildRun)buildRunListByNumber.get(0);
    } catch (final NumberFormatException e) {
      log.warn("This should not happen", e);
      return null;
    }
  }


  /**
   * @return true if panel inpt is valid
   */
  private boolean validateInput() {
    // validate build number
    boolean valid = validateBuildNumber();

    // validate parameters
    if (pnlParameters != null) {
      valid &= pnlParameters.validate();
    }

    // make sure if paramters were loaded
    if (valid) {
      final BuildRun buildRun = getFirstBuildRunFromEnteredRunNumber();
      if (buildRun.getBuildRunID() != lastLoadedBuildRunID) {
        showErrorMessage("Load parameters for this build number first.");
        valid = false;
      }
    }

    // validate there are no potential duplicates excluding current build run
    if (valid) {
      try {
        final BuildRun buildRun = getFirstBuildRunFromEnteredRunNumber();
        final BuildVersionDuplicateValidator duplicateValidator = new BuildVersionDuplicateValidator();
        duplicateValidator.validate(activeBuildID, pnlParameters.getVersionTemplate(), buildName, pnlParameters.getVersionCounter(), buildRun.getBuildRunID());
      } catch (final ValidationException e) {
        pnlParameters.showErrorMessage(StringUtils.toString(e));
        valid = false;
      }
    }
    return valid;
  }


  /**
   * @return entered label or null or empty String if not set.
   */
  private String getLabel() {
    if (pnlParameters == null) {
      return null;
    }
    return pnlParameters.getLabel();
  }


  /**
   * @return entered start parameters of empty list of nothing is set.
   */
  private List getStartParamterList() {
    if (pnlParameters == null) {
      return new ArrayList();
    }
    return pnlParameters.getStartParameterList();
  }


  private boolean validateBuildNumber() {
    final List errorList = new ArrayList(5);
    WebuiUtils.validateFieldValidPositiveInteger(errorList, CAPTION_BUILD_NUMBER, flBuildNumber);
    if (errorList.isEmpty()) {
      // check if the build run exists
      if (getFirstBuildRunFromEnteredRunNumber() == null) {
        errorList.add("Requested build run number not found. Please enter a correct build run number.");
      }
    }

    if (errorList.isEmpty()) {
      return true;
    }

    showErrorMessage(errorList);
    return false;
  }


  /**
   * @return helper method to create return params.
   */
  private Parameters makeDoneResultParams() {
    final Parameters parameters = new Parameters();
    parameters.addParameter(Pages.PARAM_BUILD_ID, Integer.toString(activeBuildID));
    return parameters;
  }


  private class StartPressedListener implements ButtonPressedListener {

    public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
      if (validateInput()) {
        requestReRun();
        return WebuiUtils.createBuildActionReturnResult(getTierletContext(), makeDoneResultParams());
      } else {
        return Tierlet.Result.Continue();
      }
    }
  }

  private class CancelPressedListener implements ButtonPressedListener {

    public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
      return Tierlet.Result.Done(Pages.ADMIN_BUILD_COMMANDS_LIST, makeDoneResultParams());
    }
  }


  public String toString() {
    return "ReRunBuildPanel{" +
      "flBuildNumber=" + flBuildNumber +
      ", btnStart=" + btnStart +
      ", btnCancel=" + btnCancel +
      ", btnLoadParameters=" + btnLoadParameters +
      ", activeBuildID=" + activeBuildID +
      ", lastLoadedBuildRunID=" + lastLoadedBuildRunID +
      ", pnlParameters=" + pnlParameters +
      ", buildName='" + buildName + '\'' +
      '}';
  }
}
