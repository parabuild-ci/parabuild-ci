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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.security.BuildRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.admin.AdminBuildStatusesTable;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.PageHeaderPanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.RSSImage;
import org.parabuild.ci.webui.common.ReturnPage;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.DropDownSelectedEvent;
import viewtier.ui.DropDownSelectedListener;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * This class shows a list of builds statuses.
 */
public class BuildsStatusesPage extends BasePage implements ConversationalTierlet {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(BuildsStatusesPage.class); //NOPMD
  private static final long serialVersionUID = 7588143097642409882L;  // NOPMD

  private static final String CAPTION_ADD_NEW_BUILD = "Add New Build";
  private static final String CAPTION_BUILD_LIST = "Build List >>";
  private static final String CAPTION_BUILD_LIST_DASHBOARD_VIEW = CAPTION_BUILD_LIST + " Dashboard View";
  private static final String CAPTION_BUILD_LIST_DETAILED_VIEW = CAPTION_BUILD_LIST + " Detailed view";
  private static final String CAPTION_BUILD_LIST_TABLE_VIEW = CAPTION_BUILD_LIST + " Table View";
  private static final String CAPTION_RECENT_BUILDS_VIEW = "Recent Builds View";

  private final DisplayGroupDropDown ddDisplayGroup = new DisplayGroupDropDown();
  private final BuildStatusesViewSwitchLink lnkStatusesViewSwitch = new BuildStatusesViewSwitchLink();


  public BuildsStatusesPage() {
    super(FLAG_NO_HEADER_SEPARATOR | FLAG_FLOATING_WIDTH);
    super.setTitle(makeTitle("Build List"));
    super.addStylePath("/parabuild/styles/dashboard.css");
    super.markTopMenuItemSelected(MENU_SELECTION_BUILDS);

    // add selection change listener
    ddDisplayGroup.setName("dropdown_select_display_group");
    ddDisplayGroup.addListener(new DropDownSelectedListener() {
      private static final long serialVersionUID = -6811304843246063808L;


      public Result dropDownSelected(final DropDownSelectedEvent dropDownSelectedEvent) {
        final DisplayGroupDropDown dropDown = (DisplayGroupDropDown) dropDownSelectedEvent.getDropDown();
        final int groupID = dropDown.getCode();
//        if (log.isDebugEnabled()) log.debug("dropdown selected. group ID: " + groupID);
        return selectDisplayGroup(groupID);
      }
    });

    // appearances
    lnkStatusesViewSwitch.setHeight(HEADER_DIVIDER_HEIGHT);

    // layout
    final Flow flwGroupAndViewTypeHolder = new CommonFlow(ddDisplayGroup, new Label(" "), WebuiUtils.makeBlueBulletSquareImage16x16(), new Label(" "), lnkStatusesViewSwitch);
    flwGroupAndViewTypeHolder.setAlignY(Layout.CENTER);
    final Panel pnlGroupAndViewTypeHolder = new Panel();
    pnlGroupAndViewTypeHolder.setWidth("100%");
    pnlGroupAndViewTypeHolder.add(flwGroupAndViewTypeHolder, new Layout(0, 0, 1, 1));
    if (isValidAdminUser()) {
      final CommonCommandLinkWithImage lnkAddNewBuild = new CommonCommandLinkWithImage(CAPTION_ADD_NEW_BUILD, Pages.ADMIN_NEW_BUILD);
      lnkAddNewBuild.setAlignX(Layout.RIGHT);
      lnkAddNewBuild.setAlignY(Layout.CENTER);
      pnlGroupAndViewTypeHolder.add(lnkAddNewBuild, new Layout(1, 0, 1, 1));
    }
    baseContentPanel().getUserPanel().setWidth("100%");
    baseContentPanel().getUserPanel().add(pnlGroupAndViewTypeHolder);
  }


  public Result executePage(final Parameters params) {
//    if (log.isDebugEnabled()) log.debug("params: " + params);

    if (!super.isValidUser() && !SecurityManager.getInstance().isAnonymousAccessEnabled()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.PUBLIC_BUILDS, params);
    }

    final HttpServletRequest request = getTierletContext().getHttpServletRequest();
    final HttpSession session = request.getSession();
    getTierletContext().getHttpServletRequest().getSession().setAttribute(ReturnPage.PARABUILD_RETURN_PAGE, null);

    // handle refresh enablement
    if (params.isParameterPresent(Pages.PARAM_ENABLE_REFRESH)) {
      final String parameterValue = params.getParameterValue(Pages.PARAM_ENABLE_REFRESH);
      session.setAttribute(PageHeaderPanel.SESSION_ATTRIBUTE_REFRESH_ENABLED, Boolean.valueOf(parameterValue));
    }
    super.setRefreshSwitchVisible(true);

    // try to get from explicit params
    final int displayGroupID;
    final Integer displayGroupIDFromParameters = ParameterUtils.getDisplayGroupIDFromParameters(params);
    if (displayGroupIDFromParameters != null) {
      displayGroupID = getValidDisplayGroupID(displayGroupIDFromParameters);
      rememberDisplayGroupID(new Integer(displayGroupID));
    } else {
      displayGroupID = getRememberedDisplayGroupID(request);
    }

    // By this time the actual display group could
    // have been deleted (see PARABUILD-1339). If
    // the dropdown doesn't have it, set to all.
    ddDisplayGroup.setCode(ddDisplayGroup.codeExists(displayGroupID) ? displayGroupID : DisplayGroup.DISPLAY_GROUP_ID_ALL);

    // create statuses switch link

    // get view from parameters
    String statusView = getClientParameter(params, request, Pages.PARAM_STATUS_VIEW, WebUIConstants.SESSION_ATTR_STATUS_VIEW, WebUIConstants.COOKIE_STATUS_VIEW);
    if (StringUtils.isBlank(statusView)) {
      statusView = Pages.STATUS_VIEW_LIST;
    }

    super.setDisplayGroupID(displayGroupID);

//    if (log.isDebugEnabled()) log.debug("statusView 2: " + statusView);
    // show view
    if (StringUtils.isBlank(statusView)) {
      showTableView(session, displayGroupID);
    } else if (statusView.equalsIgnoreCase(Pages.STATUS_VIEW_LIST)) {
      showTableView(session, displayGroupID);
    } else if (statusView.equalsIgnoreCase(Pages.STATUS_VIEW_DETAILED)) {
      showDetailedView(params, session, displayGroupID);
    } else if (statusView.equalsIgnoreCase(Pages.STATUS_VIEW_DASHBOARD)) {
      showDashboardView(session, displayGroupID);
    } else if (statusView.equalsIgnoreCase(Pages.STATUS_VIEW_RECENT)) {
      showRecentView(session, displayGroupID);
    } else {
      showTableView(session, displayGroupID);
    }

    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();

    // make and add "New Build"/rss link
    final Panel pnlFooterHolder = new Panel();
    pnlFooterHolder.setWidth("100%");
    if (systemCM.isRSSDisplayEnabled()) {
      pnlFooterHolder.add(new RSSImage(), new Layout(0, 0, 1, 1));
    }
    if (isValidAdminUser()) {
      final CommonCommandLinkWithImage lnkAddNewBuild = new CommonCommandLinkWithImage(CAPTION_ADD_NEW_BUILD, Pages.ADMIN_NEW_BUILD);
      lnkAddNewBuild.setAlignX(Layout.RIGHT);
      lnkAddNewBuild.setAlignY(Layout.TOP);
      pnlFooterHolder.add(lnkAddNewBuild, new Layout(1, 0, 1, 1));
    }
    baseContentPanel().getUserPanel().add(WebuiUtils.makeHorizontalDivider(10));
    baseContentPanel().getUserPanel().add(pnlFooterHolder);

    // Create all parameters
    if (params.isParameterPresent(Pages.PARAM_DISPLAY_GROUP_ID)
            && params.isParameterPresent(Pages.PARAM_STATUS_VIEW)
            && params.isParameterPresent(Pages.PARAM_BUILD_ID)) {
      // Get system whide refresh period and return
      final int refreshRate = WebuiUtils.getRefreshRate(session, getUserID());
      return refreshRate > 0 ? Result.Done(refreshRate) : Result.Done();
    } else {
      // Forward to page with all parameters set explictely
      final Parameters parameters = new Parameters();
      parameters.addParameter(Pages.PARAM_DISPLAY_GROUP_ID, displayGroupID);
      parameters.addParameter(Pages.PARAM_STATUS_VIEW, statusView);
      parameters.addParameter(Pages.PARAM_BUILD_ID, getBuildID(params, session));
      return Result.Done(Pages.PUBLIC_BUILDS, parameters);
    }
  }


  private void showDetailedView(final Parameters params, final HttpSession session, final int displayGroupID) {
    setTitle(makeTitle(CAPTION_BUILD_LIST_DETAILED_VIEW));
    // get build ID
    final int activeBuildID = getBuildID(params, session);

    // handle log tail view
    if (params.isParameterPresent(Pages.PARAM_SHOW_LOG_TAIL) && activeBuildID != -1) {
      final String parameterValue = params.getParameterValue(Pages.PARAM_SHOW_LOG_TAIL);
      session.setAttribute(WebuiUtils.makeCurrentlyShowingLogTailAttribute(activeBuildID), Boolean.valueOf(parameterValue));
    }

    // show panel
    final SecurityManager securityManager = SecurityManager.getInstance();
    final BuildRights userRights = securityManager.getUserBuildRights(getUser(), activeBuildID);
    final DetailedBuildStatusesPanel detailedBuildStatusesPanel = new DetailedBuildStatusesPanel(activeBuildID,
            displayGroupID, userRights.isAllowedToListCommands(), getUserBuildStatuses(displayGroupID),
            new TailWindowActivatorImpl(this));
    detailedBuildStatusesPanel.setWidth("100%");
    baseContentPanel().getUserPanel().add(detailedBuildStatusesPanel);
    markSessionDetailedView(session, activeBuildID);
    lnkStatusesViewSwitch.setDetailedViewSelected(displayGroupID, activeBuildID);
  }


  private static int getBuildID(final Parameters params, final HttpSession session) {
    Integer integerBuildID = ParameterUtils.getActiveBuildIDFromParameters(params);
    if (integerBuildID == null) {
      integerBuildID = (Integer) session.getAttribute(WebUIConstants.SESSION_ATTR_DETAILED_BUILD_ID);
    }
    return integerBuildID != null ? integerBuildID : -1;
  }


  /**
   * Displays list view build status.
   *
   * @param session
   * @param displayGroupID
   */
  private void showTableView(final HttpSession session, final int displayGroupID) {
    setTitle(makeTitle(CAPTION_BUILD_LIST_TABLE_VIEW));

    // get build statuses
    final List currentBuildsStatuses = getUserBuildStatuses(displayGroupID);
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    final List filteredBuildStatuses;
    if (scm.showParallelInListView()) {
      filteredBuildStatuses = currentBuildsStatuses; // no filtering
    } else {
      // REVIEWME: consider moving to where we get the build statuses in case we need this cached
      filteredBuildStatuses = new ArrayList(currentBuildsStatuses.size());
      for (int i = 0; i < currentBuildsStatuses.size(); i++) {
        final BuildState buildState = (BuildState) currentBuildsStatuses.get(i);
        if (buildState.isParallel()) {
          continue;
        }
        filteredBuildStatuses.add(buildState);
      }
    }

    // Show
    int flags = 0;
    flags |= scm.isShowIPAddressOnBuildStatusList() ? BuildStatusesTable.SHOW_IP_ADDRESS : 0;
    flags |= scm.isShowNextBuildTimeOnBuildStatusList() ? BuildStatusesTable.SHOW_NEXT_RUN : 0;
    final BuildStatusesTable buildStatusesTable = getUser() != null ? new AdminBuildStatusesTable(flags) : new BuildStatusesTable(flags);
    buildStatusesTable.populate(filteredBuildStatuses);
    baseContentPanel().getUserPanel().add(buildStatusesTable);

    // remember selection
    markSessionAsListView(session);
    lnkStatusesViewSwitch.setTableViewSelected(displayGroupID);
    setCookie(WebUIConstants.COOKIE_STATUS_VIEW, Pages.STATUS_VIEW_LIST);
  }


  private void showDashboardView(final HttpSession session, final int displayGroupID) {
    setTitle(makeTitle(CAPTION_BUILD_LIST_DASHBOARD_VIEW));
    final List currentBuildsStatuses = getUserBuildStatuses(displayGroupID);
    final DashboardStatusesPanel dashboardStatusesPanel = new DashboardStatusesPanel();
    baseContentPanel().getUserPanel().add(dashboardStatusesPanel);
    dashboardStatusesPanel.populate(currentBuildsStatuses);
    markSessionAsDashboard(session);
    lnkStatusesViewSwitch.setDashboardViewSelected(displayGroupID);

    // set cookie to rememeber selection
    setCookie(WebUIConstants.COOKIE_STATUS_VIEW, Pages.STATUS_VIEW_DASHBOARD);
  }


  private void showRecentView(final HttpSession session, final int displayGroupID) {
    setTitle(makeTitle(CAPTION_RECENT_BUILDS_VIEW));
    final BuildHistoryTable buildHistory = new BuildHistoryTable("Global Recent Builds");
    baseContentPanel().getUserPanel().add(buildHistory);
    buildHistory.populate(getRecentUserBuilds());
    markSessionAsRecent(session);
    lnkStatusesViewSwitch.setRecentViewSelected(displayGroupID);
    ddDisplayGroup.setDisabled(true);

    // set cookie to remember selection
    setCookie(WebUIConstants.COOKIE_STATUS_VIEW, Pages.STATUS_VIEW_RECENT);
  }


  /**
   * Returns a list of recent user build runs in revers date order.
   *
   * @return the list of recent user build runs in revers date order.
   */
  private List getRecentUserBuilds() {

    // Get allowed build IDs
    final SecurityManager sm = SecurityManager.getInstance();
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final List configurationsIDs = cm.getExistingBuildConfigurationsIDs();
    final List allowedIDs = new ArrayList(configurationsIDs.size());
    final int userID = getUserID();
    for (int i = 0; i < configurationsIDs.size(); i++) {
      final Integer activeBuildConfigID = (Integer) configurationsIDs.get(i);
      if (sm.userCanViewBuild(userID, activeBuildConfigID)) {
        allowedIDs.add(activeBuildConfigID);
      }
    }

    // Get max count of recent builds
    final int maxSize;
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    if (userID == User.UNSAVED_ID) {
      maxSize = scm.getDefaultMaxRecentBuilds();
    } else {
      final String stringMaxRecentBuilds = sm.getUserPropertyValue(userID, UserProperty.MAX_RECENT_BUILDS);
      if (StringUtils.isValidInteger(stringMaxRecentBuilds)) {
        maxSize = Integer.parseInt(stringMaxRecentBuilds);
      } else {
        maxSize = scm.getDefaultMaxRecentBuilds();
      }
    }

    // Return recent builds
    return cm.getCompletedBuildRuns(allowedIDs, maxSize);
  }


  private Result selectDisplayGroup(final int groupID) {
    // validate group exists
    final Integer integerGroupID = new Integer(getValidDisplayGroupID(groupID));

    // remember
    rememberDisplayGroupID(integerGroupID);

    // Get view
    final HttpServletRequest request = getTierletContext().getHttpServletRequest();
    final String statusView = getClientParameter(new Parameters(), request, Pages.PARAM_STATUS_VIEW, WebUIConstants.SESSION_ATTR_STATUS_VIEW, WebUIConstants.COOKIE_STATUS_VIEW);

    // Create parameters
    final Parameters parameters = new Parameters();
    parameters.addParameter(Pages.PARAM_DISPLAY_GROUP_ID, integerGroupID.toString());
    parameters.addParameter(Pages.PARAM_STATUS_VIEW, statusView);
    final String stringBuildID = request.getParameter(Pages.PARAM_BUILD_ID);
    if (StringUtils.isValidInteger(stringBuildID)) {
      parameters.addParameter(Pages.PARAM_BUILD_ID, stringBuildID);
    }

    // return result
    final int refreshRate = WebuiUtils.getRefreshRate(getTierletContext().getHttpServletRequest().getSession(), getUserID());
    return refreshRate > 0 ? Result.Done(Pages.PUBLIC_BUILDS, parameters, refreshRate) : Result.Done(Pages.PUBLIC_BUILDS, parameters);
  }


  private void rememberDisplayGroupID(final Integer integerGroupID) {// set group in cookie
    final Cookie cookie = new Cookie(WebUIConstants.COOKIE_DISPLAY_GROUP_ID, integerGroupID.toString());
    cookie.setPath("/");
    getTierletContext().addCookie(cookie, WebUIConstants.YEAR_IN_SECONDS);

    // set group in session
    getTierletContext().getHttpServletRequest().getSession().setAttribute(WebUIConstants.SESSION_ATTR_SELECTED_DISPLAY_GROUP_ID, integerGroupID);
  }


  /**
   * REVIEWME: simeshev@parabuilci.org -> replace with
   */
  private static int getValidDisplayGroupID(final int groupID) {
    // System?
    if (groupID == DisplayGroup.DISPLAY_GROUP_ID_ALL
            || groupID == DisplayGroup.DISPLAY_GROUP_ID_BROKEN
            || groupID == DisplayGroup.DISPLAY_GROUP_ID_BUILDING
            || groupID == DisplayGroup.DISPLAY_GROUP_ID_INACTIVE
            || groupID == DisplayGroup.DISPLAY_GROUP_ID_SCHEDULED
            ) {
      return groupID;
    }
    // validate
    final DisplayGroup displayGroupByName = DisplayGroupManager.getInstance().getDisplayGroup(groupID);
    if (displayGroupByName == null) {
      return DisplayGroup.DISPLAY_GROUP_ID_ALL;
    }
    return groupID;
  }


  /**
   */
  private int getRememberedDisplayGroupID(final HttpServletRequest request) {
    // try to get the display group from session
    final Integer displayGroupID = (Integer) request.getSession().getAttribute(WebUIConstants.SESSION_ATTR_SELECTED_DISPLAY_GROUP_ID);
    if (displayGroupID != null) {
      return displayGroupID;
    }

    // try to get the display group from the cookie
    final Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        final Cookie c = cookies[i];
//        if (log.isDebugEnabled()) log.debug("c.getName(): " + c.getName());
//        if (log.isDebugEnabled()) log.debug("c.getValue(): " + c.getValue());
        if (c.getName().equals(WebUIConstants.COOKIE_DISPLAY_GROUP_ID)) {
          if (StringUtils.isValidInteger(c.getValue())) {
            return getValidDisplayGroupID(Integer.parseInt(c.getValue()));
          } else {
            return DisplayGroup.DISPLAY_GROUP_ID_ALL;
          }
        }
      }
    }

    if (displayGroupID != null) {
      return getValidDisplayGroupID(displayGroupID);
    }
    return DisplayGroup.DISPLAY_GROUP_ID_ALL;
  }


  /**
   * @param displayGroupID
   * @return List of BuildState objects filtered accordingly to
   *         user rights.
   */
  private List getUserBuildStatuses(final int displayGroupID) {
    return WebuiUtils.getUserBuildStatuses(displayGroupID, getUserID());
  }


  /**
   * Marks session that user views build statuses as a list.
   */
  private static void markSessionAsListView(final HttpSession sessn) {
    sessn.setAttribute(WebUIConstants.SESSION_ATTR_STATUS_VIEW, Pages.STATUS_VIEW_LIST);
  }


  /**
   * Marks session that user views build statuses as a single
   * build.
   */
  private static void markSessionDetailedView(final HttpSession sessn, final int buildID) {
    sessn.setAttribute(WebUIConstants.SESSION_ATTR_DETAILED_BUILD_ID, new Integer(buildID));
    sessn.setAttribute(WebUIConstants.SESSION_ATTR_STATUS_VIEW, Pages.STATUS_VIEW_DETAILED);
  }


  private static void markSessionAsDashboard(final HttpSession session) {
    session.setAttribute(WebUIConstants.SESSION_ATTR_STATUS_VIEW, Pages.STATUS_VIEW_DASHBOARD);
  }


  private static void markSessionAsRecent(final HttpSession session) {
    session.setAttribute(WebUIConstants.SESSION_ATTR_STATUS_VIEW, Pages.STATUS_VIEW_RECENT);
  }


  public final String toString() {
    return "BuildsStatusesPage{" +
            "ddDisplayGroup=" + ddDisplayGroup +
            ", lnkStatusesViewSwitch=" + lnkStatusesViewSwitch +
            '}';
  }
}
