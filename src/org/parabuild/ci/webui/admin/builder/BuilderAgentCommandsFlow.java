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
import viewtier.ui.Link;

import java.util.Properties;

/**
 * List of commands available for a parcticular builderConfiguration in a builderConfigurations
 * table.
 */
final class BuilderAgentCommandsFlow extends Flow {

  private Link lnkDelete = null;


  /**
   * Constructor.
   */
  BuilderAgentCommandsFlow() {
    lnkDelete = new CommonLink("Detach", Pages.PAGE_DETACH_BUILDER_AGENT);
    this.add(lnkDelete);
  }


  /**
   * Sets agent ID
   *
   * @param agent ID to set
   */
  public void setBuilderAgentID(final int builderAgent) {
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_BUILDER_AGENT_ID, Integer.toString(builderAgent));
    lnkDelete.setParameters(param);
  }


  public String toString() {
    return "BuilderAgentCommandsFlow{" +
            ", lnkDelete=" + lnkDelete +
            '}';
  }
}
