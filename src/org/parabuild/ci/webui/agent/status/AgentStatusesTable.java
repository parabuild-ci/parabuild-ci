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
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.webui.BuildNameLinkFlow;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.BreakLabel;
import org.parabuild.ci.webui.common.CommonBoldLink;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Link;

import java.util.List;
import java.util.Properties;

/**
 * AgentStatusTable
 * <p/>
 *
 * @author Slava Imeshev
 * @since Apr 19, 2008 8:12:27 PM
 */
final class AgentStatusesTable extends AbstractFlatTable {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration,unused
   */
  private static final Log LOG = LogFactory.getLog(AgentStatusesTable.class); // NOPMD
  private static final long serialVersionUID = 7057344101623709681L;

  private static final int COLUMN_COUNT = 4;
  private static final int COL_AGENT = 0;
  private static final int COL_STATUS = 1;
  private static final int COL_BUILD_COUNT = 2;
  private static final int COL_RUNNING_BUILDS = 3;

  private final List statuses;


  /**
   * Constructor - creates an instance of flat table with given
   * number of columns.
   */
  AgentStatusesTable() {
    super(COLUMN_COUNT, false);
    setWidth("100%");
    this.statuses = ServiceManager.getInstance().getAgentStatusMonitor().getStatuses();
    populate();
  }


  /**
   */
  protected Component[] makeHeader() {
    return new Component[]{
            new TableHeaderLabel("Agent", "20%"),
            new TableHeaderLabel("Status", "10%", Layout.CENTER),
            new TableHeaderLabel("Build Configurations", "10%", Layout.CENTER),
            new TableHeaderLabel("Currently Building", "60%"),
    };
  }


  /**
   * Makes row, should be implemented by successor class
   */
  protected Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_AGENT] = new AgentLink();
    result[COL_STATUS] = new CommonLabel();
    result[COL_STATUS].setAlignX(Layout.CENTER);
    result[COL_BUILD_COUNT] = new CommonBoldLink("", "");
    result[COL_BUILD_COUNT].setAlignX(Layout.CENTER);
    result[COL_RUNNING_BUILDS] = new RunningBuilds();
    return result;
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
   * @see AbstractFlatTable#TBL_ROW_FETCHED
   * @see AbstractFlatTable#TBL_NO_MORE_ROWS
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= statuses.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final AgentStatus agentStatus = (AgentStatus) statuses.get(rowIndex);
    final Component[] row = getRow(rowIndex);

    ((AgentLink) row[COL_AGENT]).setAgentStatus(agentStatus);
    ((Label) row[COL_STATUS]).setText(agentStatus.getActivityTypeAsString());

    // Set up agent build count
    final Properties properties = new Properties();
    properties.setProperty(Pages.PARAM_AGENT_STATUS_VIEW, Pages.AGENT_STATUS_VIEW_LIST);
    properties.setProperty(Pages.PARAM_AGENT_ID, Integer.toString(agentStatus.getAgentID()));

    final BuilderConfigurationManager bcm = BuilderConfigurationManager.getInstance();
    final int buildConfigCountForAgent = bcm.getBuildConfigCountForAgent(agentStatus.getAgentID());
    final String stringBuildConfigCountForAgent = Integer.toString(buildConfigCountForAgent);

    ((Link) row[COL_BUILD_COUNT]).setText(stringBuildConfigCountForAgent);
    ((Link) row[COL_BUILD_COUNT]).setUrl(Pages.PAGE_AGENTS);
    ((Link) row[COL_BUILD_COUNT]).setParameters(properties);

    ((RunningBuilds) row[COL_RUNNING_BUILDS]).setAgentStatus(agentStatus);
    return TBL_ROW_FETCHED;
  }


  /**
   * Shows running builds.
   */
  private static final class RunningBuilds extends Flow {

    private static final long serialVersionUID = -4016867644537035217L;


    void setAgentStatus(final AgentStatus agentStatus) {
      final String hostName = agentStatus.getHostName();
      final BuildListService service = ServiceManager.getInstance().getBuildListService();
      final List buildsStatuses = service.getCurrentBuildStatuses();
      final int size = buildsStatuses.size();
      for (int i = 0; i < size; i++) {
        final BuildState buildState = (BuildState) buildsStatuses.get(i);
        final String runningOnHost = buildState.getCurrentlyRunningOnBuildHost();
        if (hostName.equalsIgnoreCase(runningOnHost)) {
          final BuildNameLinkFlow buildNameLinkFlow = new BuildNameLinkFlow();
          buildNameLinkFlow.setBuildState(buildState);
          add(buildNameLinkFlow);
          if (i < size - 1) {
            add(new BreakLabel());
          }
        }
      }
    }
  }

  /**
   * Displays an agent host name along with an optional environment link.
   */
  public static final class AgentLink extends Flow {

    private static final long serialVersionUID = -5459838287808885276L;


    void setAgentStatus(final AgentStatus agentStatus) {

      // If it is online, add a link to environment
      final byte activity = agentStatus.getActivityType();
      if (activity == AgentStatus.ACTIVITY_BUSY || activity == AgentStatus.ACTIVITY_IDLE) {
        add(new CommonBoldLink(agentStatus.getHostName(), Pages.PAGE_AGENT_ENVIRONMENT, Pages.PARAM_AGENT_ID, Integer.toString(agentStatus.getAgentID())));
      } else {
        add(new BoldCommonLabel(agentStatus.getHostName()));
      }
    }
  }


  public String toString() {
    return "AgentStatusTable{" +
            "statuses=" + statuses +
            "} " + super.toString();
  }
}
