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

import org.parabuild.ci.configuration.BuilderAgentVO;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 */
class BuilderAgentsTable extends AbstractFlatTable {

  private static final int COLUMN_COUNT = 2;
  private static final int COL_AGENT_HOST = 0;
  private static final int COL_COMMANDS = 1;

  private static final String CAPTION_COMMANDS = "Commands";
  private static final String CAPTION_AGENT_HOST = "Host and Port";
  private static final long serialVersionUID = 7021820734893711835L;

  private List builderAgents = new ArrayList(5);


  BuilderAgentsTable() {
    super(COLUMN_COUNT, false);
    setWidth("100%");
    setTitle("Agents in Build Farm");
  }


  public void load(final BuilderConfiguration builderConfiguration) {
    this.builderAgents = BuilderConfigurationManager.getInstance().getBuilderAgentVOs(builderConfiguration.getID());
    populate();
  }


  /**
   */
  protected Component[] makeHeader() {
    final Component[] header = new Component[COLUMN_COUNT];
    header[COL_AGENT_HOST] = new TableHeaderLabel(CAPTION_AGENT_HOST, 180);
    header[COL_COMMANDS] = new TableHeaderLabel(CAPTION_COMMANDS, 60);
    return header;
  }


  /**
   * Makes row, should be implemented by successor class
   */
  protected Component[] makeRow(final int rowIndex) {
    final Component[] row = new Component[COLUMN_COUNT];
    row[COL_AGENT_HOST] = new CommonLink("", "");
    row[COL_COMMANDS] = new BuilderAgentCommandsFlow();
    return row;
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
    if (rowIndex >= builderAgents.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final Component[] row = getRow(rowIndex);
    final BuilderAgentVO builderAgentVO = (BuilderAgentVO) builderAgents.get(rowIndex);
    ((Link) row[COL_AGENT_HOST]).setText(builderAgentVO.getHost());
    ((Link) row[COL_AGENT_HOST]).setUrl(Pages.PAGE_EDIT_AGENT);
    ((Link) row[COL_AGENT_HOST]).setParameters(createParameters(builderAgentVO.getAgentID()));
    ((BuilderAgentCommandsFlow) row[COL_COMMANDS]).setBuilderAgentID(builderAgentVO.getBuilderAgentID());
    return TBL_ROW_FETCHED;
  }


  private static Properties createParameters(final int agentID) {
    final Properties properties = new Properties();
    properties.setProperty(Pages.PARAM_AGENT_ID, Integer.toString(agentID));
    return properties;
  }


  public String toString() {
    return "BuilderMemberTable{" +
            "builderAgents=" + builderAgents +
            '}';
  }
}
