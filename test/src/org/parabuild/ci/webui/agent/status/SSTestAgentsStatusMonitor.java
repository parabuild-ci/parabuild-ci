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

import org.apache.log4j.Logger;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.services.ServiceManager;

/**
 * AgentsStatusMonitorTest
 * <p/>
 *
 * @author Slava Imeshev
 * @since Sep 26, 2009 10:07:51 PM
 */
public final class SSTestAgentsStatusMonitor extends ServersideTestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(SSTestAgentsStatusMonitor.class); // NOPMD


  public void testGetStatuses() {
    assertTrue(ServiceManager.getInstance().getAgentStatusMonitor().getStatuses().size() > 0);
  }


  public SSTestAgentsStatusMonitor(final String s) {
    super(s);
  }
}
