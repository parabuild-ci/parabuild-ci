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

import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.webui.common.CodeNameDropDown;

import java.util.List;

/**
 * AgentDropDown
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 16, 2009 8:58:36 PM
 */
final class AgentDropDown extends CodeNameDropDown {

  AgentDropDown() {
    super(ALLOW_NONEXISTING_CODES);
    final List agentList = BuilderConfigurationManager.getInstance().getAgentList();
    for (int i = 0; i < agentList.size(); i++) {
      final AgentConfig agentConfig = (AgentConfig) agentList.get(i);
      addCodeNamePair(agentConfig.getID(), agentConfig.getHost());
    }
  }
}
