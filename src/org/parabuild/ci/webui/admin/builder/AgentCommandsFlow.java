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

import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Link;

import java.util.Properties;

/**
 * List of commands available for a particular builderConfiguration in a builderConfigurations
 * table.
 */
final class AgentCommandsFlow extends Flow {

  private static final long serialVersionUID = 8689537282594821739L;
  private final Link lnkEdit;
  private final Link lnkDelete;
  private final CommonLink lnkVariables;


  /**
   * Constructor.
   *
   * @param agentID agent configuration ID ID to use to compose command links.
   */
  AgentCommandsFlow(final int agentID) {
    this();
    setAgentID(agentID);
  }


  /**
   * Constructor.
   */
  AgentCommandsFlow() {
    lnkEdit = new CommonLink("Edit", Pages.PAGE_EDIT_AGENT);
    lnkDelete = new CommonLink("Delete", Pages.PAGE_DELETE_AGENT);
    lnkVariables = new CommonLink("Variables", Pages.PAGE_VARIABLE_LIST);
    this.add(lnkEdit);
    this.add(new Label(" | "));
    this.add(lnkDelete);
    this.add(new Label(" | "));
    this.add(lnkVariables);
  }


  /**
   * Sets agent ID
   *
   * @param agentID agent ID to set
   */
  public void setAgentID(final int agentID) {
    // Common
    final String stringAgentID = Integer.toString(agentID);
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_AGENT_ID, stringAgentID);
    lnkDelete.setParameters(param);
    lnkEdit.setParameters(param);

    // Variable
    final Properties variablesParam = new Properties();
    variablesParam.setProperty(Pages.PARAM_VARIABLE_TYPE, Byte.toString(StartParameter.TYPE_AGENT));
    variablesParam.setProperty(Pages.PARAM_VARIABLE_OWNER, stringAgentID);
    lnkVariables.setParameters(variablesParam);
  }


  public String toString() {
    return "AgentCommandsFlow{" +
            "lnkEdit=" + lnkEdit +
            ", lnkDelete=" + lnkDelete +
            '}';
  }
}
