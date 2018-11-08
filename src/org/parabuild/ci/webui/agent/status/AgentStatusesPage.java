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
package org.parabuild.ci.webui.agent.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.webui.BuildStatusesTable;
import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import org.parabuild.ci.webui.admin.AdminBuildStatusesTable;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MenuDividerLabel;
import org.parabuild.ci.webui.common.PageHeaderLabel;
import org.parabuild.ci.webui.common.PageHeaderPanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ReturnPage;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Agents status page.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Apr 17, 2008 9:19:37 PM
 */
public final class AgentStatusesPage extends BasePage implements ConversationalTierlet {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AgentStatusesPage.class); // NOPMD
  private static final long serialVersionUID = -6605153907643158757L;

  private static final String SESSION_AGENT_STATUS_VIEW = "session.agent.status.view";
  private static final String COOKIE_AGENT_STATUS_VIEW = "cookie.agent.status.view";

  private AgentViewSwitchLink viewSwitchLink = new AgentViewSwitchLink();


  public AgentStatusesPage() {
    super(FLAG_NO_HEADER_SEPARATOR | FLAG_FLOATING_WIDTH);
    setTitle(makeTitle("Build Agent Statuses and Load"));
    super.markTopMenuItemSelected(MENU_SELECTION_AGENTS);
  }


  /**
   * Strategy method to be implemented by classes inheriting
   * BasePage.
   *
   * @param params
   * @return result of page execution
   */
  protected Result executePage(final Parameters params) {

    if (!super.isValidUser() && !org.parabuild.ci.security.SecurityManager.getInstance().isAnonymousAccessEnabled()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.PAGE_AGENTS, params);
    }

    final HttpServletRequest request = getTierletContext().getHttpServletRequest();
    final HttpSession session = request.getSession();

    // Handle refresh enablement
    if (params.isParameterPresent(Pages.PARAM_ENABLE_REFRESH)) {
      final String parameterValue = params.getParameterValue(Pages.PARAM_ENABLE_REFRESH);
      session.setAttribute(PageHeaderPanel.SESSION_ATTRIBUTE_REFRESH_ENABLED, Boolean.valueOf(parameterValue));
    }
    super.setRefreshSwitchVisible(true);

    // Set view mode
    final String viewMode = getClientParameter(params, request, Pages.PARAM_AGENT_STATUS_VIEW,
            SESSION_AGENT_STATUS_VIEW, COOKIE_AGENT_STATUS_VIEW);
    if (Pages.AGENT_STATUS_VIEW_LIST.equalsIgnoreCase(viewMode)) {
      final String agentID = params.getParameterValue(Pages.PARAM_AGENT_ID);
      if (StringUtils.isValidInteger(agentID)) {
        final BuilderConfigurationManager bcm = BuilderConfigurationManager.getInstance();
        final AgentConfig agentConfig = bcm.getAgentConfig(Integer.parseInt(agentID));
        if (agentConfig == null) {
          return showPageErrorAndExit("Agent not found");
        }
        showAgentBuilds(agentConfig);
      } else {
        showAgentStatuses(session);
      }
    } else //noinspection IfStatementWithIdenticalBranches
      if (Pages.AGENT_STATUS_VIEW_LOAD.equalsIgnoreCase(viewMode)) {
        showAgentLoads(session);
      } else {
        showAgentLoads(session);
      }

    // get system whide refresh period and return
    final int refreshRate = WebuiUtils.getRefreshRate(session, getUserID());
    return refreshRate > 0 ? Result.Done(refreshRate) : Result.Done();
  }


  private void showAgentBuilds(final AgentConfig agentConfig) {

    viewSwitchLink.setListViewSelected();
    final Panel up = baseContentPanel().getUserPanel();
    up.add(new CommonFlow(WebuiUtils.makeBlueBulletSquareImage16x16(), viewSwitchLink));
    up.add(WebuiUtils.makePanelDivider());
    up.add(new PageHeaderLabel("Builds assigned to agent: " + agentConfig.getHost()));

    // Get user builds
    final List userBuildStatuses = WebuiUtils.getUserBuildStatuses(DisplayGroup.DISPLAY_GROUP_ID_ALL, getUserID(), true);

    // Get agent build ids
    final List agentBuildIDs = BuilderConfigurationManager.getInstance().getBuildConfigIDsForAgent(agentConfig.getID());

    // Filter user builds
    final List result = new ArrayList(agentBuildIDs.size());
    for (final Iterator iterator = agentBuildIDs.iterator(); iterator.hasNext();) {
      final Integer activeBuildID = (Integer) iterator.next();
      for (int j = 0; j < userBuildStatuses.size(); j++) {
        final BuildState buildState = (BuildState) userBuildStatuses.get(j);
        if (buildState.getActiveBuildID() == activeBuildID) {
          result.add(buildState);
          iterator.remove();
        }
      }
    }

    // Show agent builds
    int flags = 0;
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    flags |= scm.isShowIPAddressOnBuildStatusList() ? BuildStatusesTable.SHOW_IP_ADDRESS : 0;
    flags |= scm.isShowNextBuildTimeOnBuildStatusList() ? BuildStatusesTable.SHOW_NEXT_RUN : 0;
    final BuildStatusesTable buildStatusesTable = getUser() != null ? new AdminBuildStatusesTable(flags) : new BuildStatusesTable(flags);
    buildStatusesTable.populate(result);
    // Add stop link
    addStopAll(agentConfig, up);
    up.add(WebuiUtils.makePanelDivider());
    up.add(buildStatusesTable);

    // Add stop link
    addStopAll(agentConfig, up);

    // Remember return in case any one decides to edit a build
    final ReturnPage returnPage = new ReturnPage();
    returnPage.setPage(Pages.PAGE_AGENTS);
    returnPage.setParameter(Pages.PARAM_AGENT_STATUS_VIEW, Pages.AGENT_STATUS_VIEW_LIST);
    returnPage.setParameter(Pages.PARAM_AGENT_ID, Integer.toString(agentConfig.getID()));
    getTierletContext().getHttpServletRequest().getSession().setAttribute(ReturnPage.PARABUILD_RETURN_PAGE, returnPage);
  }


  private void showAgentLoads(final HttpSession session) {
    viewSwitchLink.setLoadViewSelected();
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    final int statusColumns = scm.getAgentStatusColums();
    final int imageWidthPixels = scm.getAgentStatusImageWidthPixels();

    final Panel bcp = baseContentPanel().getUserPanel();
    bcp.setAlignX(Layout.CENTER);
    bcp.setWidth(imageWidthPixels * statusColumns);

    final GridIterator gi = new GridIterator(bcp, statusColumns);
    gi.add(new CommonFlow(WebuiUtils.makeBlueBulletSquareImage16x16(), viewSwitchLink), statusColumns);

    // Show statuses
    final AgentsStatusMonitor statusMonitor = ServiceManager.getInstance().getAgentStatusMonitor();
    final List statuses = statusMonitor.getStatuses();
    for (int i = 0; i < statuses.size(); i++) {
      final AgentStatus agentStatus = (AgentStatus) statuses.get(i);
      final AgentStatusPanel panel = new AgentStatusPanel(agentStatus);
      gi.add(panel);
    }

    // Remember status is session and cookies
    session.setAttribute(SESSION_AGENT_STATUS_VIEW, Pages.AGENT_STATUS_VIEW_LOAD);
    setCookie(COOKIE_AGENT_STATUS_VIEW, Pages.AGENT_STATUS_VIEW_LOAD);
  }


  private void showAgentStatuses(final HttpSession session) {
    viewSwitchLink.setListViewSelected();
    final Panel up = baseContentPanel().getUserPanel();
    up.add(new CommonFlow(WebuiUtils.makeBlueBulletSquareImage16x16(), viewSwitchLink));
    up.add(new AgentStatusesTable());

    // Remember status is session and cookies
    session.setAttribute(SESSION_AGENT_STATUS_VIEW, Pages.AGENT_STATUS_VIEW_LIST);
    setCookie(COOKIE_AGENT_STATUS_VIEW, Pages.AGENT_STATUS_VIEW_LIST);
  }


  private void addStopAll(final AgentConfig agentConfig, final Panel up) {
    if (isValidAdminUser()) {
      final Properties stopParams = new Properties();
      stopParams.setProperty(Pages.PARAM_STOP_GROUP_SOURCE, Pages.STOP_GROUP_SOURCE_AGENT_STATUS);
      stopParams.setProperty(Pages.PARAM_AGENT_ID, Integer.toString(agentConfig.getID()));
      final Properties resumeParams = new Properties();
      stopParams.setProperty(Pages.PARAM_RESUME_GROUP_SOURCE, Pages.RESUME_GROUP_SOURCE_AGENT_STATUS);
      stopParams.setProperty(Pages.PARAM_AGENT_ID, Integer.toString(agentConfig.getID()));
      up.add(WebuiUtils.makePanelDivider());
      up.add(new CommonFlow(new CommonCommandLinkWithImage("Stop All", Pages.ADMIN_STOP_GROUP, stopParams),
              new MenuDividerLabel(), new CommonCommandLink("Resume All", Pages.ADMIN_RESUME_GROUP, resumeParams)));
    }
  }


  public String toString() {
    return "AgentStatusesPage{" +
            "viewSwitchLink=" + viewSwitchLink +
            "} " + super.toString();
  }
}
