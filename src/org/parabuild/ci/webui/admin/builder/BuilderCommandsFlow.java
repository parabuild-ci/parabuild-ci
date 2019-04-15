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

import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Link;

import java.util.Properties;


/**
 * List of commands available for a parcticular builder in a
 * builder table.
 */
final class BuilderCommandsFlow extends Flow {

  private static final long serialVersionUID = 4527607383487639830L;
  private final Link lnkEdit;
  private final Link lnkDelete;


  /**
   * Constructor.
   *
   * @param builderID ID to use to compose command links.
   */
  BuilderCommandsFlow(final int builderID) {
    this();
    setBuilderID(builderID);
  }


  /**
   * Constructor.
   */
  BuilderCommandsFlow() {
    lnkEdit = new CommonLink("Edit", Pages.ADMIN_EDIT_BUILDER);
    lnkDelete = new CommonLink("Delete", Pages.ADMIN_DELETE_BUILDER);
    this.add(lnkEdit);
    this.add(new Label(" | "));
    this.add(lnkDelete);
  }


  /**
   * Sets cluster ID
   *
   * @param builderID to set
   */
  public void setBuilderID(final int builderID) {
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_BUILDER_ID, Integer.toString(builderID));
    lnkDelete.setParameters(param);
    lnkEdit.setParameters(param);
  }


  public String toString() {
    return "ActionMenuList{" +
            "lnkEdit=" + lnkEdit +
            ", lnkDelete=" + lnkDelete +
            '}';
  }
}
