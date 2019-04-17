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
package org.parabuild.ci.webui.admin.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.AgentConfigVO;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows list of agents in the system.
 *
 */
final class AgentsTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AgentsTable.class); // NOPMD

  private static final int COLUMN_COUNT = 8;

  private static final int COL_HOST = 0;
  private static final int COL_DESCRIPTION = 1;
  private static final int COL_ENABLED = 2;
  private static final int COL_CAPACITY = 3;
  private static final int COL_MAX_CONCURRENT_BUILDS = 4;
  private static final int COL_SERIALIZE = 5;
  private static final int COL_BUILD_CONFIG_COUNT = 6;
  private static final int COL_ACTION = 7;

  public static final String CAPTION_HOST = "Host and Port";
  public static final String CAPTION_BUILD_CONFIG_COUNT = "Build Configurations";
  public static final String CAPTION_DESCRIPTION = "Description";
  public static final String CAPTION_ACTION = "Action";
  public static final String CAPTION_ENABLED = "Enabled";
  private static final String CAPTION_SERIALIZE = "Serialize";
  private static final String CAPTION_CAPACITY = "Capacity";
  private static final String CAPTION_MAX_CONCURRENT_BUILDS = "Maximum concurrent builds";

  private List agents = null;


  AgentsTable(final boolean showControls) {
    super(showControls ? COLUMN_COUNT : COLUMN_COUNT - 1, false);
    setWidth("100%");
    setGridColor(Pages.COLOR_PANEL_BORDER);
    populate(BuilderConfigurationManager.getInstance().getAgentVOList());
  }


  /**
   * Returns array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   */
  protected Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_HOST] = new TableHeaderLabel(CAPTION_HOST, "15%");
    headers[COL_BUILD_CONFIG_COUNT] = new TableHeaderLabel(CAPTION_BUILD_CONFIG_COUNT, "10%", Layout.CENTER);
    headers[COL_DESCRIPTION] = new TableHeaderLabel(CAPTION_DESCRIPTION, "25%");
    headers[COL_CAPACITY] = new TableHeaderLabel(CAPTION_CAPACITY, "10%", Layout.CENTER);
    headers[COL_MAX_CONCURRENT_BUILDS] = new TableHeaderLabel(CAPTION_MAX_CONCURRENT_BUILDS, "10%", Layout.CENTER);
    headers[COL_SERIALIZE] = new TableHeaderLabel(CAPTION_SERIALIZE, "10%", Layout.CENTER);
    headers[COL_ENABLED] = new TableHeaderLabel(CAPTION_ENABLED, "10%");
    if (columnCount() == COLUMN_COUNT) {
      headers[COL_ACTION] = new TableHeaderLabel(CAPTION_ACTION, "20%");
    }
    return headers;
  }


  /**
   * Returns array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= agents.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final AgentConfigVO agentConfigVO = (AgentConfigVO) agents.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((Label) row[COL_HOST]).setText(agentConfigVO.getHost());
    ((Label) row[COL_BUILD_CONFIG_COUNT]).setText(agentConfigVO.getBuildConfigCountAsString());
    ((Label) row[COL_SERIALIZE]).setText(agentConfigVO.getSerializeAsString());
    ((Label) row[COL_ENABLED]).setText(agentConfigVO.isEnabledAsString());
    ((Label) row[COL_DESCRIPTION]).setText(agentConfigVO.getDescription());
    ((Label) row[COL_CAPACITY]).setText(agentConfigVO.getCapacityAsString());
    ((Label) row[COL_MAX_CONCURRENT_BUILDS]).setText(agentConfigVO.getMaxConcurrentBuilds() == 0 ? "No limit" : agentConfigVO.getMaxConcurrentBuildsAsString());
    if (columnCount() == COLUMN_COUNT) {
      ((AgentCommandsFlow) row[COL_ACTION]).setAgentID(agentConfigVO.getID());
    }
    return TBL_ROW_FETCHED;
  }


  /**
   * Makes row
   */
  protected Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_HOST] = new BoldCommonLabel();
    result[COL_BUILD_CONFIG_COUNT] = new CommonLabel();
    result[COL_BUILD_CONFIG_COUNT].setAlignX(Layout.CENTER);
    result[COL_SERIALIZE] = new CommonLabel(Layout.CENTER);
    result[COL_ENABLED] = new CommonLabel();
    result[COL_DESCRIPTION] = new CommonLabel();
    result[COL_CAPACITY] = new CommonLabel(Layout.CENTER);
    result[COL_MAX_CONCURRENT_BUILDS] = new CommonLabel(Layout.CENTER);
    if (columnCount() == COLUMN_COUNT) {
      result[COL_ACTION] = new AgentCommandsFlow();
    }
    return result;
  }


  /**
   * Populates table with agents. This list is reused in fetchRow
   * method.
   *
   * @noinspection ParameterHidesMemberVariable
   */
  public void populate(final List agents) {
    this.agents = new ArrayList(agents);
    super.populate();
  }


  public String toString() {
    return "AgentListTable{" +
            "agents=" + agents +
            '}';
  }
}
