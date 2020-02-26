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

import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.TailUpdate;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.BreakLabel;
import org.parabuild.ci.webui.common.CommonBoldLink;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RSSImage;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.webui.secured.BuildCommandsLinks;
import org.parabuild.ci.webui.secured.SecuredComponentFactory;
import org.parabuild.ci.webui.tail.TailService;
import viewtier.ui.Border;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Image;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.TierletContext;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This panel shows detailed build status.
 *
 * @see DetailedBuildStatusesPanel
 * @see BuildsStatusesPage
 * @see BuildRunParticipantsTable
 */
public final class DetailedBuildStatusPanel extends Panel {

  public static final String BUILD_RUN_INFO_WIDTH = "49%";
  private static final String PARTICIPANTS_WIDTH = "98%";
  private static final long serialVersionUID = -6828964126697955637L;


  public DetailedBuildStatusPanel(final BuildState currentBuildState, final boolean showBuildControls, final TailWindowActivator tailWindowActivator) {

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    final String dateTimeFormat = systemCM.getDateTimeFormat();
    //noinspection ThisEscapedInObjectConstruction
    final GridIterator gi = new GridIterator(this, 2);

    // get last complete build run
    final BuildRun lastBuildRun = currentBuildState.getLastCompleteBuildRun();
    final String buildNameAndStatusCaption = makeBuildNameAndStatusCaption(currentBuildState);

    final Flow flBuildNameAndStatus = new Flow();

    // throbber, if necessary
    final Image throbber = WebuiUtils.makeThrobber(currentBuildState, buildNameAndStatusCaption);
    if (throbber != null) {
      throbber.setAlignY(Layout.BOTTOM);
      flBuildNameAndStatus.add(throbber).add(new Label("&nbsp;"));
    }

    // build name and status overview
    final CommonLabel lbBuildNameAndStatus = new CommonLabel(buildNameAndStatusCaption);
    lbBuildNameAndStatus.setFont(Pages.FONT_HEADER_LABEL);
    lbBuildNameAndStatus.setAlignY(Layout.BOTTOM);
    lbBuildNameAndStatus.setHeight(35);
    final TierletContext tierletContext = getTierletContext();
    if (WebuiUtils.isBuildRunNotNullAndComplete(lastBuildRun)) {
      lbBuildNameAndStatus.setForeground(WebuiUtils.getBuildResultColor(tierletContext, lastBuildRun));
    }
    flBuildNameAndStatus.add(lbBuildNameAndStatus);

    // build name and next build time panel
    final Panel pnlBuildName = new Panel();
    pnlBuildName.setBorder(Border.BOTTOM, 2, Pages.COLOR_PANEL_BORDER);
    pnlBuildName.setWidth("100%");


    // Next build time
    final Flow flwFarmAndTime = new Flow();
    flwFarmAndTime.setAlignX(Layout.RIGHT);
    flwFarmAndTime.setAlignY(Layout.BOTTOM);
    if (currentBuildState.isNextBuildTimeSet()) {
      flwFarmAndTime.add(new CommonLabel("Next build: "));
      flwFarmAndTime.add(new BoldCommonLabel(StringUtils.formatDate(currentBuildState.getNextBuildTime(), dateTimeFormat)));
    }

    // Build farm name
    flwFarmAndTime.add(new CommonLabel((currentBuildState.isNextBuildTimeSet() ? " | " : "") + "Build farm: "));
    final BuilderConfigurationManager bcm = BuilderConfigurationManager.getInstance();
    final BuilderConfiguration bc = bcm.getBuilderByBuildID(currentBuildState.getActiveBuildID());
    final SecurityManager sm = SecurityManager.getInstance();
    if (sm.isAdmin(getTierletContext())) {
      flwFarmAndTime.add(new CommonLink(bc.getName(), Pages.PAGE_BUILDER_DETAILS, Pages.PARAM_BUILDER_ID, bc.getID()));
    } else {
      flwFarmAndTime.add(new BoldCommonLabel(bc.getName()));
    }

    pnlBuildName.add(flBuildNameAndStatus, new Layout(0, 0, 1, 1));
    pnlBuildName.add(flwFarmAndTime, new Layout(1, 0, 1, 1));

    gi.add(pnlBuildName, 2);

    // command links / RSS link-image
    final boolean showRSS = systemCM.isRSSDisplayEnabled();
    if (showBuildControls) {
      final BuildCommandsLinks buildCommandsLinks = makeCommandLinks(currentBuildState);
      if (showRSS) {
        gi.addPair(buildCommandsLinks, makeRSSImage(currentBuildState));
      } else {
        gi.add(buildCommandsLinks, 2);
      }
    } else {
      if (showRSS) {
        gi.addPair(new Label(), makeRSSImage(currentBuildState));
      }
    }

    // add divider
    gi.add(WebuiUtils.makeHorizontalDivider(10), 2);

    // preExecute current status if running
    BuildRun currentBuildRun = null;
    Flow flCurrentlyBuildingSteps = null;
    final int currentlyRunnigBuildRunID = currentBuildState.getCurrentlyRunningBuildRunID();
    if (currentlyRunnigBuildRunID != BuildRun.UNSAVED_ID) {
      currentBuildRun = cm.getBuildRun(currentlyRunnigBuildRunID);
      flCurrentlyBuildingSteps = new CurrentlyBuildingStepsFlow(currentBuildState);
      flCurrentlyBuildingSteps.add(new BreakLabel());
      flCurrentlyBuildingSteps.add(new ElapsedTimeFlow(currentBuildRun));
      flCurrentlyBuildingSteps.add(new BreakLabel());
      flCurrentlyBuildingSteps.add(makeResultsLinks(currentBuildRun));
      flCurrentlyBuildingSteps.setWidth(BUILD_RUN_INFO_WIDTH);
      flCurrentlyBuildingSteps.setAlignY(Layout.TOP);
    }

    // preExecute last build result links if any
    Flow flLastBuildRun = null;
    if (WebuiUtils.isBuildRunNotNullAndComplete(lastBuildRun)) {
      flLastBuildRun = new LastBuildRunFlow(lastBuildRun, dateTimeFormat);
      flLastBuildRun.add(new BreakLabel());
      flLastBuildRun.add(makeResultsLinks(lastBuildRun));
      flLastBuildRun.setWidth(BUILD_RUN_INFO_WIDTH);
      flLastBuildRun.setAlignY(Layout.TOP);
    }

    // preExecute last clean build result links if needed
    Flow flLastCleanBuildRun = null;
    final BuildRun lastCleanBuildRun = currentBuildState.getLastCleanBuildRun();
    if (WebuiUtils.isBuildRunNotNullAndComplete(lastBuildRun)
            && lastBuildRun.getResultID() != BuildRun.BUILD_RESULT_SUCCESS
            && lastCleanBuildRun != null) {
      flLastCleanBuildRun = new LastCleanBuildRunFlow(lastCleanBuildRun, dateTimeFormat);
      flLastCleanBuildRun.setWidth(BUILD_RUN_INFO_WIDTH);
      flLastCleanBuildRun.setAlignY(Layout.TOP);
    }

    // layout statuses
    if (flCurrentlyBuildingSteps != null) {
      gi.add(flCurrentlyBuildingSteps);
      if (flLastBuildRun != null) {
        flLastBuildRun.setAlignX(Layout.RIGHT);
        gi.add(flLastBuildRun);
        if (flLastCleanBuildRun != null) {
          flLastCleanBuildRun.setAlignX(Layout.RIGHT);
          gi.add(new Label());
          gi.add(flLastCleanBuildRun);
        }
      }
    } else {
      if (flLastBuildRun != null) {
        gi.add(flLastBuildRun);
        if (flLastCleanBuildRun != null) {
          flLastCleanBuildRun.setAlignX(Layout.RIGHT);
          gi.add(flLastCleanBuildRun);
        }
      }
    }

    // NOTE: simeshev@parabuilci.org - 11/18/2004 - move to next GI
    // line - laying out statuses can leave gi position in the
    // middle of a row, moving to next GI line ensures that further
    // layout start at right position (begining of the row).
    gi.moveToNextLine();

    // get number of new change lists and compose a suffix
    // to add to the title for the chage list table
    final Integer newInThisBuild = cm.getBuildRunAttributeValue(currentlyRunnigBuildRunID, BuildRunAttribute.NEW_CHANGE_LIST_IN_THIS_BUILD, (Integer) null);
    final String newInThisBuildSuffix;
    if (newInThisBuild == null) {
      newInThisBuildSuffix = "";
    } else {
      if (newInThisBuild == 0) {
        newInThisBuildSuffix = ", No New Changes";
      } else {
        newInThisBuildSuffix = ", " + newInThisBuild.toString() + " New Change List(s)";
      }
    }

    // show current changes and pending changes
    if (currentBuildRun != null) {
      // show log tail on/off Switch
      final boolean showingLogTail = currentBuildState.isRunning() && WebuiUtils.currentlyShowingLogTail(tierletContext, currentBuildRun.getActiveBuildID());
      final Component logTailOnOffSwitchLink = makeLogTailOnOffSwitchLink(currentBuildRun.getActiveBuildID(), showingLogTail);
      gi.add(WebuiUtils.makeHorizontalDivider(3), 2);
      gi.add(logTailOnOffSwitchLink, 2);

      if (showingLogTail) {
        // create and pre-popualate the log tail
        final int tailWindowSize = tailWindowSize();
        final TailComponent tailComponent = new TailComponent(tailWindowSize);
        final TailUpdate tailUpdate = new TailService().getUpdate(currentBuildRun.getActiveBuildID(), 0);
        tailComponent.show(tailUpdate);
        gi.add(tailComponent, 2);
        // let window know that is should be tailing
        tailWindowActivator.activate(currentBuildRun.getActiveBuildID(), tailWindowSize, tailUpdate.getTimeStamp()); // tail comonent will append blank lines if needed
      } else {
        // show current changes
        final boolean showDescription = sm.userCanSeeChangeListDescriptions(tierletContext);
        final BuildRunParticipantsTable tblCurrentBuildRunParticipants = new BuildRunParticipantsTable(currentBuildRun, showDescription);
        tblCurrentBuildRunParticipants.setTitle("Currently Building Changes" + newInThisBuildSuffix);
        tblCurrentBuildRunParticipants.setPadding(4);
        tblCurrentBuildRunParticipants.setWidth(PARTICIPANTS_WIDTH);
        gi.add(WebuiUtils.makeHorizontalDivider(3), 2);
        gi.add(tblCurrentBuildRunParticipants, 2);
        // show pending changes
        // TODO: move getPendingChangeLists to build manager.
        final List pendingChangeLists = cm.getPendingChangeLists(currentBuildRun.getActiveBuildID());
        if (!pendingChangeLists.isEmpty()) {
          final PendingChangeListsTable pendingChangeListsTable = new PendingChangeListsTable(pendingChangeLists);
          pendingChangeListsTable.setTitle("Pending Changes");
          pendingChangeListsTable.setPadding(4);
          pendingChangeListsTable.setWidth(PARTICIPANTS_WIDTH);
          gi.add(WebuiUtils.makeHorizontalDivider(3), 2);
          gi.add(pendingChangeListsTable, 2);
        }
      }
    } else if (lastBuildRun != null) {
      // show last build change lists w/header colored according to build result
      String lastBuildRunTitle = "";
      if (lastBuildRun.getResultID() == BuildRun.BUILD_RESULT_SUCCESS) {
        lastBuildRunTitle = "Changes in Last Build" + newInThisBuildSuffix;
      } else {
        lastBuildRunTitle = "Suspected Changes" + newInThisBuildSuffix;
      }
      final BuildRunParticipantsTable tblLastBuildRunParticipants = new BuildRunParticipantsTable(lastBuildRun, true);
      tblLastBuildRunParticipants.setTitle(lastBuildRunTitle);
      tblLastBuildRunParticipants.setTitleForeground(WebuiUtils.getBuildResultColor(tierletContext, lastBuildRun));
      tblLastBuildRunParticipants.setPadding(4);
      tblLastBuildRunParticipants.setWidth(PARTICIPANTS_WIDTH);
      gi.add(WebuiUtils.makeHorizontalDivider(3), 2);
      gi.add(tblLastBuildRunParticipants, 2);
    }

    // Add permalink to the last clean build
    gi.add(WebuiUtils.makeHorizontalDivider(3), 2);
    gi.add(new PermanentLatestSuccessfulBuildFlow(currentBuildState.getActiveBuildID()), 2);
  }


  private static Component makeLogTailOnOffSwitchLink(final int activeBuildID, final boolean currentlyShowingValue) {

    // make props
    final Properties props = new Properties();
    props.setProperty(Pages.PARAM_STATUS_VIEW, Pages.STATUS_VIEW_DETAILED);
    props.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(activeBuildID));
    props.setProperty(Pages.PARAM_SHOW_LOG_TAIL, Boolean.toString(!currentlyShowingValue));

    // make caption
    final String caption = "Turn log tail " + (currentlyShowingValue ? "OFF" : "ON");

    // show
    return new CommonFlow(WebuiUtils.makeBlueBulletSquareImage16x16(), new Label(" "), new CommonBoldLink(caption, Pages.PUBLIC_BUILDS, props));
  }


  /**
   * Helper method to compose build name and status caption.
   *
   * @param currentBuildState current build state.
   * @return build name and status caption.
   */
  private static String makeBuildNameAndStatusCaption(final BuildState currentBuildState) {
    final String currentlyRunningOnBuildHost = currentBuildState.getCurrentlyRunningOnBuildHost();
    final String currentStatus = currentBuildState.getStatusAsString().toLowerCase() + (StringUtils.isBlank(currentlyRunningOnBuildHost) ? "" : " on " + currentlyRunningOnBuildHost);
    return WebuiUtils.getBuildName(currentBuildState) + " (" + currentStatus + ')';
  }


  private static RSSImage makeRSSImage(final BuildState currentBuildState) {
    final RSSImage rssImage = new RSSImage(currentBuildState.getActiveBuildID());
    rssImage.setAlignX(Layout.RIGHT);
    return rssImage;
  }


  /**
   * Makes a list of command links according to the state.
   *
   * @param currentBuildState
   * @return BuildCommandsLinks
   * @see BuildCommandsLinks
   */
  private static BuildCommandsLinks makeCommandLinks(final BuildState currentBuildState) {
    final SecuredComponentFactory scf = SecuredComponentFactory.getInstance();
    final BuildCommandsLinks result = scf.makeBuildCommandsLinks();
    result.setBuildStatus(currentBuildState);
    result.setAlignX(Layout.LEFT);
    result.setAlignY(Layout.TOP);
    result.setPadding(4);
    return result;
  }


  private static BuildRunResultsLinksFlow makeResultsLinks(final BuildRun buildRun) {
    final BuildRunResultsLinksFlow result = new BuildRunResultsLinksFlow();
    result.setBuildRun(buildRun, false);
    result.setPadding(4);
    return result;
  }


  private int tailWindowSize() {

    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();

    final TierletContext tierletContext = getTierletContext();
    if (tierletContext == null) return scm.getTailWindowSize();

    // get user
    final SecurityManager sm = SecurityManager.getInstance();
    final User userFromRequest = sm.getUserFromRequest(tierletContext.getHttpServletRequest());
    if (userFromRequest == null) return scm.getTailWindowSize();

    // get user preference
    final String rowSize = sm.getUserPropertyValue(userFromRequest.getUserID(), UserProperty.TAIL_WINDOW_SIZE);
    if (!StringUtils.isValidInteger(rowSize)) return scm.getTailWindowSize();

    return Math.min(ConfigurationConstants.TAIL_BUFFER_SIZE, Integer.parseInt(rowSize));
  }


  /**
   * Composite to show elapsed time.
   */
  private static final class ElapsedTimeFlow extends Flow {

    private static final long serialVersionUID = 1707110416253923843L;


    /**
     * Constructor.
     *
     * @param buildRun BuildRun that has already started.
     */
    public ElapsedTimeFlow(final BuildRun buildRun) {
      // cover-ass check
      final Date startedAt = buildRun.getStartedAt();
      if (startedAt == null) {
        // TODO: add log.warn
        return;
      }
      final long elapsedTime = (System.currentTimeMillis() - startedAt.getTime()) / 1000L;
      add(new CommonLabel("Elapsed build time: ")).add(new CommonLabel(StringUtils.durationToString(elapsedTime, true)));
    }
  }
}
