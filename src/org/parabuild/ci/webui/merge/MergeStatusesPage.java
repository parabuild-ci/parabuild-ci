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
package org.parabuild.ci.webui.merge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.merge.MergeState;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.MergeRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.DashboardStatusesPanel;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.PageHeaderPanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.RSSImage;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.DropDownSelectedEvent;
import viewtier.ui.DropDownSelectedListener;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.TierletContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * This class shows a list of merge statuses.
 */
public class MergeStatusesPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 7588143097642409882L;  // NOPMD

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(MergeStatusesPage.class); //NOPMD

  private static final String CAPTION_ADD_NEW_MERGE = "Add New Merge";
  private static final String CAPTION_MERGE_LIST = "Automerge List >>";
  private static final String CAPTION_MERGE_LIST_DASHBOARD_VIEW = CAPTION_MERGE_LIST + " Dashboard View";
  private static final String CAPTION_MERGE_LIST_DETAILED_VIEW = CAPTION_MERGE_LIST + " Detailed view";
  private static final String CAPTION_MERGE_LIST_TABLE_VIEW = CAPTION_MERGE_LIST + " Table View";

  public static final String ATTR_SELECTED_MERGE_DISPLAY_GROUP_ID = "selected.merge.display.group.id";
  public static final String ATTR_DETAILED_MERGE_ID = "session.detailed.merge.id";
  public static final String ATTR_MERGE_STATUS_VIEW = "session.merge.status.view";

  private static final String COOKIE_MERGE_STATUS_VIEW = "parabuild.merge.status.view";

  private final MergeDisplayGroupDropDown ddDisplayGroup = new MergeDisplayGroupDropDown();
  private final MergeStatusesViewSwitchLink lnkStatusesViewSwitch = new MergeStatusesViewSwitchLink();


  public MergeStatusesPage() {
    super(FLAG_NO_HEADER_SEPARATOR | FLAG_FLOATING_WIDTH);
    super.setTitle(makeTitle("Automerge List"));
    super.addStylePath("/parabuild/styles/dashboard.css");
    super.markTopMenuItemSelected(MENU_SELECTION_MERGES);

    // add selection change listener
    ddDisplayGroup.setName("dropdown_select_merge_display_group");
    ddDisplayGroup.addListener(new DropDownSelectedListener() {
      public Result dropDownSelected(final DropDownSelectedEvent dropDownSelectedEvent) {
        final MergeDisplayGroupDropDown dropDown = (MergeDisplayGroupDropDown) dropDownSelectedEvent.getDropDown();
        final int groupID = dropDown.getCode();
//        if (log.isDebugEnabled()) log.debug("dropdown selected. group ID: " + groupID);
        return selectDisplayGroup(groupID);
      }
    });

    // appearances
    lnkStatusesViewSwitch.setHeight(HEADER_DIVIDER_HEIGHT);

    // layout
    final Flow pnlGroupAndViewTypeHolder = new Flow();
    pnlGroupAndViewTypeHolder.add(ddDisplayGroup);
    pnlGroupAndViewTypeHolder.add(WebuiUtils.makeBlueBulletSquareImage16x16());
    pnlGroupAndViewTypeHolder.add(lnkStatusesViewSwitch);
    pnlGroupAndViewTypeHolder.setWidth("100%");
    baseContentPanel().getUserPanel().setWidth("100%");
    baseContentPanel().getUserPanel().add(pnlGroupAndViewTypeHolder);
  }


  public Result executePage(final Parameters params) {
//    if (log.isDebugEnabled()) log.debug("params: " + params);
    if (!super.isValidUser() && !org.parabuild.ci.security.SecurityManager.getInstance().isAnonymousAccessEnabled()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.PAGE_MERGE_LIST, params);
    }

    final HttpServletRequest request = getTierletContext().getHttpServletRequest();
    final HttpSession session = request.getSession();

    // handle refresh enablement
    if (params.isParameterPresent(Pages.PARAM_ENABLE_REFRESH)) {
      final String parameterValue = params.getParameterValue(Pages.PARAM_ENABLE_REFRESH);
      session.setAttribute(PageHeaderPanel.SESSION_ATTRIBUTE_REFRESH_ENABLED, Boolean.valueOf(parameterValue));
    }
    super.setRefreshSwitchVisible(true);

    final int displayGroupID = getDisplayGroupID(params, request);
    ddDisplayGroup.setCode(displayGroupID);

    // create statuses switch link

    // get view from parameters
    final String statusView = getStatusView(params, request);

//    if (log.isDebugEnabled()) log.debug("statusView 2: " + statusView);
    // show view
    if (StringUtils.isBlank(statusView)) {
      showTableView(session, displayGroupID);
    } else if (statusView.equalsIgnoreCase(Pages.STATUS_VIEW_LIST)) {
      showTableView(session, displayGroupID);
    } else if (statusView.equalsIgnoreCase(Pages.STATUS_VIEW_DETAILED)) {
      showDetailedView(params, session, displayGroupID);
    } else {
      showTableView(session, displayGroupID);
    }

    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();

    // make and add "New Merge"/rss link
    final Panel pnlFooterHolder = new Panel();
    pnlFooterHolder.setWidth("100%");
    if (systemCM.isRSSDisplayEnabled()) {
      pnlFooterHolder.add(new RSSImage(), new Layout(0, 0, 1, 1));
    }
    if (isValidAdminUser()) {
      final CommonLink lnkAddNewMerge = new CommonCommandLink(CAPTION_ADD_NEW_MERGE, Pages.PAGE_MERGE_EDIT);
      lnkAddNewMerge.setAlignX(Layout.RIGHT);
      lnkAddNewMerge.setAlignY(Layout.TOP);
      pnlFooterHolder.add(lnkAddNewMerge, new Layout(1, 0, 1, 1));
    }
    baseContentPanel().getUserPanel().add(WebuiUtils.makeHorizontalDivider(10));
    baseContentPanel().getUserPanel().add(pnlFooterHolder);

    // get system whide refresh period and return
    final int refreshRate = getRefreshRate(session, getUserID());
    return refreshRate > 0 ? Result.Done(refreshRate) : Result.Done();
  }


  private void showDetailedView(final Parameters params, final HttpSession session, final int displayGroupID) {
    setTitle(makeTitle(CAPTION_MERGE_LIST_DETAILED_VIEW));
    // get merge ID
    Integer integerMergeID = ParameterUtils.getActiveMergeIDFromParameters(params);
    if (integerMergeID == null) integerMergeID = (Integer) session.getAttribute(ATTR_DETAILED_MERGE_ID);
    final int activeMergeID = integerMergeID != null ? integerMergeID : -1;

    // show panel
    final SecurityManager securityManager = SecurityManager.getInstance();
    final MergeRights userRights = securityManager.getUserMergeRights(getUser(), activeMergeID);
    final DetailedMergeStatusesPanel detailedMergeStatusesPanel = new DetailedMergeStatusesPanel(activeMergeID, userRights.isAllowedToListCommands(), getUserMergeStates(displayGroupID));
    detailedMergeStatusesPanel.setWidth("100%");
    baseContentPanel().getUserPanel().add(detailedMergeStatusesPanel);
    markSessionDetailedView(session, activeMergeID);
    lnkStatusesViewSwitch.setDetailedViewSelected();
  }


  /**
   * Displays list view merge status.
   *
   * @param session
   * @param displayGroupID
   */
  private void showTableView(final HttpSession session, final int displayGroupID) {
    setTitle(makeTitle(CAPTION_MERGE_LIST_TABLE_VIEW));

    // detect we have to show
    final SecurityManager sm = SecurityManager.getInstance();
    final User user = getUser();
    final List states = getUserMergeStates(displayGroupID);
    boolean showCommandsColumn = false;
    for (int i = 0, n = states.size(); i < n; i++) {
      final MergeState state = (MergeState) states.get(i);
      final MergeRights rights = sm.getUserMergeRights(user, state.getActiveMergeConfigurationID());
      if (rights.isAllowedToListCommands()) {
        showCommandsColumn = true;
        break;
      }
    }

    // add status table
    final MergeStatusesTable mergeStatusesTable = new MergeStatusesTable(showCommandsColumn);
    mergeStatusesTable.populate(states);
    baseContentPanel().getUserPanel().add(mergeStatusesTable);

    // remember selection
    markSessionAsListView(session);
    lnkStatusesViewSwitch.setTableViewSelected();
    setStatusViewCookie(Pages.STATUS_VIEW_LIST);
  }


  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private void showDashboardView(final HttpSession session, final int displayGroupID) {
    setTitle(makeTitle(CAPTION_MERGE_LIST_DASHBOARD_VIEW));
    final List currentMergesStatuses = getUserMergeStates(displayGroupID);
    final DashboardStatusesPanel dashboardStatusesPanel = new DashboardStatusesPanel();
    baseContentPanel().getUserPanel().add(dashboardStatusesPanel);
    dashboardStatusesPanel.populate(currentMergesStatuses);
    markSessionAsDashboard(session);
    lnkStatusesViewSwitch.setDashboardViewSelected();

    // set cookie to rememeber selection
    setStatusViewCookie(Pages.STATUS_VIEW_DASHBOARD);
  }


  private Result selectDisplayGroup(final int groupID) {
    // validate group exists
    final Integer integerGroupID = new Integer(getValidDisplayGroupID(groupID));

    // set group in cookie
    final Cookie cookie = new Cookie(WebUIConstants.COOKIE_MERGE_DISPLAY_GROUP_ID, integerGroupID.toString());
    cookie.setPath("/");
    getTierletContext().addCookie(cookie, WebUIConstants.YEAR_IN_SECONDS);

    // set group in session
    final HttpSession session = getTierletContext().getHttpServletRequest().getSession();
    session.setAttribute(ATTR_SELECTED_MERGE_DISPLAY_GROUP_ID, integerGroupID);

    // return result
    final Parameters parameters = new Parameters();
    parameters.addParameter(Pages.PARAM_DISPLAY_GROUP_ID, integerGroupID.toString());
    final int refreshRate = getRefreshRate(session, getUserID());
    return refreshRate > 0 ? Result.Done(Pages.PAGE_MERGE_LIST, parameters, refreshRate) : Result.Done(Pages.PAGE_MERGE_LIST, parameters);
  }


  /**
   * REVIEWME: simeshev@parabuilci.org -> replace with
   */
  private int getValidDisplayGroupID(final int groupID) {
    // System?
    if (groupID == DisplayGroup.DISPLAY_GROUP_ID_ALL
            || groupID == DisplayGroup.DISPLAY_GROUP_ID_BROKEN
            || groupID == DisplayGroup.DISPLAY_GROUP_ID_BUILDING
            || groupID == DisplayGroup.DISPLAY_GROUP_ID_SCHEDULED
            ) {
      return groupID;
    }
    // validate
    final DisplayGroup displayGroupByName = DisplayGroupManager.getInstance().getDisplayGroup(groupID);
    if (displayGroupByName == null) return DisplayGroup.DISPLAY_GROUP_ID_ALL;
    return groupID;
  }


  /**
   * Helper method to return refresh rate.
   */
  public static int getRefreshRate(final HttpSession session, final int userID) {
    // first check if refresh is enabled.
    if (Boolean.FALSE.equals(session.getAttribute(PageHeaderPanel.SESSION_ATTRIBUTE_REFRESH_ENABLED))) return 0;
    return SecurityManager.getInstance().getMergeStatusRefreshSecs(userID); // Use merge status refrsh
  }


  /**
   */
  private int getDisplayGroupID(final Parameters params, final HttpServletRequest request) {
    // try to get from explicit params
    Integer displayGroupID = ParameterUtils.getDisplayGroupIDFromParameters(params);
    if (displayGroupID != null) return getValidDisplayGroupID(displayGroupID);

    // try to get the display group from session
    displayGroupID = (Integer) request.getSession().getAttribute(ATTR_SELECTED_MERGE_DISPLAY_GROUP_ID);
    if (displayGroupID != null) return displayGroupID;

    // try to get the display group from the cookie
    final Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        final Cookie c = cookies[i];
//        if (log.isDebugEnabled()) log.debug("c.getName(): " + c.getName());
//        if (log.isDebugEnabled()) log.debug("c.getValue(): " + c.getValue());
        if (c.getName().equals(WebUIConstants.COOKIE_MERGE_DISPLAY_GROUP_ID)) {
          if (StringUtils.isValidInteger(c.getValue())) {
            return getValidDisplayGroupID(Integer.parseInt(c.getValue()));
          } else {
            return DisplayGroup.DISPLAY_GROUP_ID_ALL;
          }
        }
      }
    }

    if (displayGroupID != null) return getValidDisplayGroupID(displayGroupID);
    return DisplayGroup.DISPLAY_GROUP_ID_ALL;
  }


  /**
   * @param displayGroupID
   * @return List of MergeState objects filtered accordingly to
   *         user rights.
   */
  private List getUserMergeStates(final int displayGroupID) {
    final List states = SecurityManager.getInstance().getUserMergeStates(getUserID());
    return DisplayGroupManager.getInstance().filterMergeStates(states, displayGroupID);
  }


  /**
   * Marks session that user views merge statuses as a list.
   */
  private void markSessionAsListView(final HttpSession sessn) {
    sessn.setAttribute(ATTR_MERGE_STATUS_VIEW, Pages.STATUS_VIEW_LIST);
  }


  /**
   * Marks session that user views merge statuses as a single
   * merge.
   */
  private void markSessionDetailedView(final HttpSession sessn, final int mergeID) {
    sessn.setAttribute(ATTR_DETAILED_MERGE_ID, new Integer(mergeID));
    sessn.setAttribute(ATTR_MERGE_STATUS_VIEW, Pages.STATUS_VIEW_DETAILED);
  }


  private void markSessionAsDashboard(final HttpSession session) {
    session.setAttribute(ATTR_MERGE_STATUS_VIEW, Pages.STATUS_VIEW_DASHBOARD);
  }


  /**
   * Sets cookie to rememmer selection.
   *
   * @param statusView
   */
  private void setStatusViewCookie(final String statusView) {
    boolean found = false;
    final TierletContext tierletContext = getTierletContext();
    final HttpServletRequest httpServletRequest = tierletContext.getHttpServletRequest();
    final Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        final Cookie cookie = cookies[i];
        final String cookieName = cookie.getName();
        final String cookieValue = cookie.getValue();
        if (!StringUtils.isBlank(cookieName)
                && cookieName.equals(COOKIE_MERGE_STATUS_VIEW)
                && !StringUtils.isBlank(cookieValue)
                && statusView.equals(cookieValue)) {
          found = true;
          break;
        }
      }
    }

    if (!found) {
      final Cookie cookie = new Cookie(COOKIE_MERGE_STATUS_VIEW, statusView);
      cookie.setPath("/");
      cookie.setMaxAge(Integer.MAX_VALUE);
      getTierletContext().addCookie(cookie);
    }
  }


  /**
   * Returns status view selection, if any, based on
   * parameters, the session or the cookie.
   */
  private String getStatusView(final Parameters params, final HttpServletRequest request) {
    final HttpSession session = request.getSession();
    String statusView = params.getParameterValue(Pages.PARAM_STATUS_VIEW);

    if (StringUtils.isBlank(statusView)) {
      statusView = (String) session.getAttribute(ATTR_MERGE_STATUS_VIEW);
    }

    if (StringUtils.isBlank(statusView)) {
      String result = null;
      final Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (int i = 0; i < cookies.length; i++) {
          final Cookie cookie = cookies[i];
          final String cookieName = cookie.getName();
          final String cookieValue = cookie.getValue();
          if (!StringUtils.isBlank(cookieName)
                  && cookieName.equals(COOKIE_MERGE_STATUS_VIEW)
                  && !StringUtils.isBlank(cookieValue)) {
            result = cookieValue;
            break;
          }
        }
      }
      statusView = result;
    }
    return statusView;
  }
}
