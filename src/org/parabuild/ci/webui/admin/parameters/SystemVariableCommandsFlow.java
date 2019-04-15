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
package org.parabuild.ci.webui.admin.parameters;

import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Link;

import java.util.Properties;

/**
 * List of commands available for a parcticular builderConfiguration in a builderConfigurations
 * table.
 */
final class SystemVariableCommandsFlow extends Flow {

  private static final long serialVersionUID = 6445311608524358286L;
  private Link lnkEdit = null;
  private Link lnkDelete = null;


  /**
   * Constructor.
   */
  SystemVariableCommandsFlow() {
    lnkEdit = new CommonLink("Edit", Pages.PAGE_VARIABLE_EDIT);
    lnkDelete = new CommonLink("Delete", Pages.PAGE_VARIABLE_DELETE);
    add(lnkEdit);
    add(new Label(" | "));
    add(lnkDelete);
  }


  /**
   * Sets variable ID
   *
   * @param variableType
   * @param variableOwner
   */
  public void setParameter(final int variableID, final byte variableType, final int variableOwner) {
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_VARIABLE_ID, Integer.toString(variableID));
    param.setProperty(Pages.PARAM_VARIABLE_TYPE, Byte.toString(variableType));
    param.setProperty(Pages.PARAM_VARIABLE_OWNER, Integer.toString(variableOwner));
    lnkDelete.setParameters(param);
    lnkEdit.setParameters(param);
  }


  public String toString() {
    return "SystemVariableCommandsFlow{" +
            "lnkEdit=" + lnkEdit +
            ", lnkDelete=" + lnkDelete +
            '}';
  }
}