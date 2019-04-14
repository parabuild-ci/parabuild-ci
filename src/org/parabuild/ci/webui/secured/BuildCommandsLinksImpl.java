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
package org.parabuild.ci.webui.secured;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.BuildRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.CommandLinkWithSeparator;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.TierletContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 */
final class BuildCommandsLinksImpl extends BuildCommandsLinks {


  private static final long serialVersionUID = 8694125381974772557L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(BuildCommandsLinksImpl.class); // NOPMD

  private final CommandLinkWithSeparator flwActivate = new CommandLinkWithSeparator(new CommonCommandLink("Activate", Pages.ADMIN_ACTIVATE_BUILD));
//  private final CommandLinkWithSeparator flwWatchLog = new CommandLinkWithSeparator(new CommonCommandLink("Watch", Pages.PUBLIC_WATCH_LOG, true));
  private final CommandLinkWithSeparator flwStart = new CommandLinkWithSeparator(new CommonCommandLink("Start", Pages.ADMIN_START_BUILD));
  private final CommandLinkWithSeparator flwStop = new CommandLinkWithSeparator(new CommonCommandLink("Stop", Pages.ADMIN_STOP_BUILD));
  private final CommandLinkWithSeparator flwResume = new CommandLinkWithSeparator(new CommonCommandLink("Resume", Pages.ADMIN_RESUME_BUILD));
  private final CommandLinkWithSeparator flwEdit = new CommandLinkWithSeparator(new CommonCommandLink("Edit", Pages.ADMIN_EDIT_BUILD));
  private final CommandLinkWithSeparator flwCommandList = new CommandLinkWithSeparator(new CommonCommandLink("Commands", Pages.ADMIN_BUILD_COMMANDS_LIST));

  // to keep track of command sequence in the flow
  private final List commands = new ArrayList(7);


  protected BuildCommandsLinksImpl() {
    // hide
    flwActivate.setVisible(false);
//    flwWatchLog.setVisible(false);
    flwStart.setVisible(false);
    flwStop.setVisible(false);
    flwResume.setVisible(false);
    flwEdit.setVisible(false);
    flwCommandList.setVisible(false);

    // add
    addCommand(flwActivate);
    addCommand(flwStart);
    addCommand(flwStop);
    addCommand(flwStop);
    addCommand(flwResume);
//    addCommand(flwWatchLog);
    addCommand(flwEdit);
    addCommand(flwCommandList);
  }


  /**
   * Adds a command to the flow and register it int the commands
   * list.
   *
   * @param command CommandLinkWithSeparator to add
   *
   * @see CommandLinkWithSeparator
   */
  private void addCommand(final CommandLinkWithSeparator command) {
    add(command);
    commands.add(command);
  }


  /**
   * Sets build status
   *
   * @param buildState
   */
  public void setBuildStatus(final BuildState buildState) {
    // get build config
    final int buildID = buildState.getActiveBuildID();
    final ActiveBuild activeBuild = ConfigurationManager.getInstance().getActiveBuild(buildID);

    // preExecute params
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(buildID));

    // set params to always available commands
    flwCommandList.setParameters(param);
    flwEdit.setParameters(param);

    // get user's rights
    BuildRights rights = null;
    final TierletContext tierletContext = getTierletContext();
//    if (log.isDebugEnabled()) log.debug("tierletContext: " + tierletContext);
    if (tierletContext != null) {
      final SecurityManager sm = SecurityManager.getInstance();
      final User user = sm.getUserFromRequest(tierletContext.getHttpServletRequest());
      rights = sm.getUserBuildRights(user, buildID);
//      if (log.isDebugEnabled()) log.debug("========================================");
//      if (log.isDebugEnabled()) log.debug("activeBuild ID: " + activeBuild.getID());
//      if (log.isDebugEnabled()) log.debug("tierletContext: " + tierletContext);
//      if (log.isDebugEnabled()) log.debug("user: " + user);
//      if (log.isDebugEnabled()) log.debug("rights: " + rights);
    } else {
      rights = BuildRights.NO_RIGHTS;
    }

    // show "Activate/Deactivate"
    if (activeBuild.getStartupStatus() == BuildStatus.INACTIVE_VALUE) {
      flwActivate.setVisible(rights.isAllowedToActivateBuild());
      flwActivate.setParameters(param);
    }

    // show "Start/Stop"
    switch (buildState.getStatus().intValue()) {
      case BuildStatus.IDLE_VALUE:
      case BuildStatus.PAUSED_VALUE:
      case BuildStatus.INACTIVE_VALUE:
        // show "Start".
        // NOTE: simeshev - 2006-12-05 - Parallel builds cannot be started
        // separately so for them "Start" command is not available.
        if (buildState.getSchedule() != BuildConfig.SCHEDULE_TYPE_PARALLEL) {
          flwStart.setVisible(rights.isAllowedToStartBuild());
          flwStart.setParameters(param);
        }
        break;
      case BuildStatus.BUILDING_VALUE:
      case BuildStatus.CHECKING_OUT_VALUE:
      case BuildStatus.GETTING_CHANGES_VALUE:
        // show "Stop"
        flwStop.setVisible(rights.isAllowedToStopBuild());
        flwStop.setParameters(param);
        // show "Watch"
//        flwWatchLog.setVisible(rights.isAllowedToStopBuild());
//        flwWatchLog.setParameters(param);
        break;
      default:
        break;
    }

    // show "Resume"
    switch (buildState.getStatus().intValue()) {
      case BuildStatus.PAUSED_VALUE:
        // show "Resume"
        flwResume.setVisible(rights.isAllowedToResumeBuild());
        flwResume.setParameters(param);
        break;
      default:
        break;
    }

    // show commands
    flwCommandList.setVisible(rights.isAllowedToListCommands());

    // show edit
    flwEdit.setVisible(rights.isAllowedToUpdateBuild());

    // hide last separator and set soft separators.
    boolean hidLastSeparator = false;
    final int commandsSize = commands.size();
    for (int i = commandsSize - 1; i >= 0; i--) {
      final CommandLinkWithSeparator flwCmd = (CommandLinkWithSeparator)commands.get(i);
      if (flwCmd.isVisible()) {
        // hide last divider
        if (!hidLastSeparator) {
          flwCmd.hideSeparator();
          hidLastSeparator = true;
        }
      }
    }
  }

}
