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

import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.security.BuildRights;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.webui.secured.MaintenanceCommandsPanel;
import org.parabuild.ci.webui.secured.SecuredComponentFactory;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

import java.util.Properties;

/**
 *
 */
public final class BuildCommandListPage extends BasePage implements StatelessTierlet {

  // private static final Log log = LogFactory.getLog(ProcessListPage.class);
  private static final long serialVersionUID = -4758150699291034504L; // NOPMD

  private static final String TITLE_BUILD_COMMANDS_LIST = "Build Commands List";
  private static final String CAPTION_RUNTIME_COMMANDS = "Runtime Commands";
  private static final String CAPTION_ADVANCED_COMMANDS = "Advanced Commands";
  private static final String CAPTION_CONFIGURATION_COMMANDS = "Configuration Commands";
  private static final String CAPTION_MISCELLANEOUS_COMMANDS = "Miscellaneous Commands";


  public BuildCommandListPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_PAGE_HEADER_LABEL | FLAG_SHOW_HEADER_SEPARATOR);
    setTitle(makeTitle(TITLE_BUILD_COMMANDS_LIST));
  }


  /**
   * @param params Parameters include mandatory <code>buildid</code>
   *               and optional <code>buildnumstart</code> and <code>buildnumend<code>.
   *               <p/>
   *               If any of <code>buildnumstart</code> and <code>buildnumend</code>
   *               is not present then the page shows query form. If both are present
   *               it shows the query form and the results of the query.
   */
  public Result executePage(final Parameters params) {
    // authenticate
    if (!isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_BUILD_COMMANDS_LIST, params);
    }

    // check if build exists, show error message if not
    final BuildConfig buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (buildConfig == null) {
      return WebuiUtils.showBuildNotFound(this);
    }
    // authorise
    final BuildRights userRights = getUserRigths(buildConfig.getActiveBuildID());
    if (!userRights.isAllowedToListCommands()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // set actual title
    setTitle(makeTitle(TITLE_BUILD_COMMANDS_LIST + " for " + buildConfig.getBuildName()));

    // create parameters to use by linkd
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(buildConfig.getBuildID()));

    final SecuredComponentFactory scf = SecuredComponentFactory.getInstance();
    final MaintenanceCommandsPanel pnlMaintenanceCommands = scf.makeMaintenanceCommandsPanel();

    final AnnotatedCommandLink flwActivate = new AnnotatedCommandLink("Activate", Pages.ADMIN_ACTIVATE_BUILD, "activates inactive build.", true);
    final AnnotatedCommandLink flwDeactivate = new AnnotatedCommandLink("Deactivate", Pages.ADMIN_DEACTIVATE_BUILD, "deactivates active build and places it to INACTIVE display group.", true);
    final AnnotatedCommandLink flwCopy = new AnnotatedCommandLink("Copy", Pages.ADMIN_CLONE_BUILD, "creates new build configuration by copying this build configuration.", true);
    final AnnotatedCommandLink flwDelete = new AnnotatedCommandLink("Delete", Pages.ADMIN_DELTE_BUILD, "deletes build configuration.", true);
    final AnnotatedCommandLink flwEdit = new AnnotatedCommandLink("Edit", Pages.ADMIN_EDIT_BUILD, "edits build configuration.", true);
    final AnnotatedCommandLink flwChangeBuildType = new AnnotatedCommandLink("Change schedule type", Pages.ADMIN_CHANGE_BUILD_TYPE, "changes type of the build schedule.", true);
    final AnnotatedCommandLink flwResume = new AnnotatedCommandLink("Resume", Pages.ADMIN_RESUME_BUILD, "resumes previously stopped build.", true);
    final AnnotatedCommandLink flwResumeGroup = new AnnotatedCommandLink("Resume group", Pages.ADMIN_RESUME_GROUP, "resumes a group of previously stopped builds.", true);
    final AnnotatedCommandLink flwStart = new AnnotatedCommandLink("Start", Pages.ADMIN_START_BUILD, "requests build to start.", true);
    final AnnotatedCommandLink flwStop = new AnnotatedCommandLink("Stop", Pages.ADMIN_STOP_BUILD, "stops and pauses build.", true);
    final AnnotatedCommandLink flwStopGroup = new AnnotatedCommandLink("Stop group", Pages.ADMIN_STOP_GROUP, "stops and pauses a group of builds at once.", true);
    final AnnotatedCommandLink flwRerun = new AnnotatedCommandLink("Re-run", Pages.ADMIN_RERUN_BUILD, "re-runs build.", true);
    final AnnotatedCommandLink flwRequestNextCleanCheckout = new AnnotatedCommandLink("Clean checkout", Pages.ADMIN_REQUEST_CLEAN_CHECKOUT, "requests clean cleckout for the next build run.", true);
    final AnnotatedCommandLink flwResetStatsCaches = new AnnotatedCommandLink("Reset statistics caches", Pages.ADMIN_RESET_BUILD_STATS_CACHES, "resets statistics. Next call to statistics will do full recalculate.", true);
    final AnnotatedCommandLink flwDiff = new AnnotatedCommandLink("Diff", Pages.BUILD_DIFF_TWO, "Show changes between two builds", true);

    // set parameters
    flwActivate.setParameters(param);
    pnlMaintenanceCommands.setParameters(param);
    flwCopy.setParameters(param);
    flwDeactivate.setParameters(param);
    flwDelete.setParameters(param);
    flwEdit.setParameters(param);
    flwChangeBuildType.setParameters(param);
    flwResume.setParameters(param);
    flwResumeGroup.setParameters(createResumeGroupParameters(buildConfig.getBuildID()));
    flwStart.setParameters(param);
    flwStop.setParameters(param);
    flwStopGroup.setParameters(createStopGroupParameters(buildConfig.getBuildID()));
    flwResetStatsCaches.setParameters(param);
    flwRerun.setParameters(param);
    flwRequestNextCleanCheckout.setParameters(param);
    flwDiff.setParameters(param);

    // add links to content
    final Panel pnlContent = baseContentPanel().getUserPanel();
    setPageHeader("List Of Commands for Build: " + buildConfig.getBuildName());

    // commands in configuration group
    if (userRights.isAllowedToUpdateBuild() || userRights.isAllowedToDeleteBuild() || isValidAdminUser()) {
      pnlContent.add(new BoldCommonLabel(CAPTION_CONFIGURATION_COMMANDS));
      if (isValidAdminUser() || userRights.isAllowedToUpdateBuild()) {
        pnlContent.add(flwCopy);
      }
      if (userRights.isAllowedToUpdateBuild()) {
        pnlContent.add(flwEdit);
      }
      if (userRights.isAllowedToDeleteBuild()) {
        pnlContent.add(flwDelete);
      }
      if (userRights.isAllowedToUpdateBuild() && isCanChangeBuildType(buildConfig)) {
        pnlContent.add(flwChangeBuildType);
      }
      pnlContent.add(WebuiUtils.makePanelDivider());
    }

    // commands in runtime group
    if (userRights.isAllowedToStartBuild() || userRights.isAllowedToStopBuild() || userRights.isAllowedToResumeBuild()
            || userRights.isAllowedToActivateBuild() || userRights.isAllowedToDeactivateBuild()) {
      pnlContent.add(new BoldCommonLabel(CAPTION_RUNTIME_COMMANDS));
      if (userRights.isAllowedToStopBuild()) {
        pnlContent.add(flwStop);
      }
      if (userRights.isAllowedToStopBuild()) {
        pnlContent.add(flwStopGroup);
      }
      if (userRights.isAllowedToResumeBuild()) {
        pnlContent.add(flwResume);
      }
      if (userRights.isAllowedToResumeBuild()) {
        pnlContent.add(flwResumeGroup);
      }
      if (userRights.isAllowedToStartBuild() && !isParallel(buildConfig)) {
        pnlContent.add(flwStart);
      }
      if (userRights.isAllowedToDeactivateBuild()) {
        pnlContent.add(flwDeactivate);
      }
      if (userRights.isAllowedToActivateBuild()) {
        pnlContent.add(flwActivate);
      }
      if (userRights.isAllowedToStartBuild()) {
        pnlContent.add(flwRequestNextCleanCheckout);
      }
      if (userRights.isAllowedToStartBuild() && !isParallel(buildConfig)) {
        pnlContent.add(flwRerun);
      }
      pnlContent.add(WebuiUtils.makePanelDivider());
    }

    // commands in Miscellaneous group
    if (userRights.isAllowedToViewBuild()) {
      pnlContent.add(new BoldCommonLabel(CAPTION_MISCELLANEOUS_COMMANDS));
      pnlContent.add(flwDiff);
      pnlContent.add(WebuiUtils.makePanelDivider());
    }

    // commands in maintenance group
    if (isValidAdminUser() && pnlMaintenanceCommands.commandsAvailable() > 0) {
      pnlContent.add(pnlMaintenanceCommands);
      pnlContent.add(WebuiUtils.makePanelDivider());
    }

    // commands in currently hidden "Advanced" group
    // REVIEWME: consider if some or all of the items should be
    // made public.
    if (params.isParameterPresent(Pages.PARAM_SHOW_ADVANCED) && isValidAdminUser()) {
      pnlContent.add(WebuiUtils.makePanelDivider());
      pnlContent.add(new BoldCommonLabel(CAPTION_ADVANCED_COMMANDS));
      pnlContent.add(flwResetStatsCaches);
    }
    return Result.Done();
  }


  /**
   * Returns true if build schedule can be changed.
   *
   * @param buildConfig true if build schedule can be changed.
   * @return true if build schedule can be changed.
   */
  private boolean isCanChangeBuildType(final BuildConfig buildConfig) {
    return buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_AUTOMATIC
            || buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_MANUAL;
  }


  private Properties createStopGroupParameters(final int buildID) {
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(buildID));
    param.setProperty(Pages.PARAM_STOP_GROUP_SOURCE, Pages.STOP_GROUP_SOURCE_BUILD_COMMANDS);
    return param;
  }


  private Properties createResumeGroupParameters(final int buildID) {
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(buildID));
    param.setProperty(Pages.PARAM_RESUME_GROUP_SOURCE, Pages.RESUME_GROUP_SOURCE_BUILD_COMMANDS);
    return param;
  }


  private boolean isParallel(final BuildConfig buildConfig) {
    return buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL;
  }
}
