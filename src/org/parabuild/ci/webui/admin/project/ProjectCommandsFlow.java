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
package org.parabuild.ci.webui.admin.project;

import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Link;

import java.util.Properties;

/**
 * List of commands available for a parcticular project in a projects
 * table.
 */
public final class ProjectCommandsFlow extends Flow {

  private static final long serialVersionUID = -5296419812863188923L;
  private Link lnkEdit = null;
  private Link lnkDelete = null;
  private final Link lnkVariables;


  /**
   * Constructor.
   *
   * @param projectID ID to use to compose command links.
   */
  public ProjectCommandsFlow(final int projectID) {
    this();
    setProjectID(projectID);
  }


  /**
   * Constructor.
   */
  public ProjectCommandsFlow() {
    lnkEdit = new CommonLink("Edit", Pages.PAGE_EDIT_PROJECT);
    lnkDelete = new CommonLink("Delete", Pages.PAGE_DELETE_PROJECT);
    lnkVariables = new CommonLink("Variables", Pages.PAGE_VARIABLE_LIST);
    this.add(lnkEdit);
    this.add(new Label(" | "));
    this.add(lnkDelete);
    this.add(new Label(" | "));
    this.add(lnkVariables);
  }


  /**
   * Sets project ID
   *
   * @param projectID to set
   */
  public void setProjectID(final int projectID) {
    final String stringProjectID = Integer.toString(projectID);
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_PROJECT_ID, stringProjectID);
    lnkDelete.setParameters(param);
    lnkEdit.setParameters(param);

    // Variable
    final Properties variablesParam = new Properties();
    variablesParam.setProperty(Pages.PARAM_VARIABLE_TYPE, Byte.toString(StartParameter.TYPE_PROJECT));
    variablesParam.setProperty(Pages.PARAM_VARIABLE_OWNER, stringProjectID);
    lnkVariables.setParameters(variablesParam);
  }
}
