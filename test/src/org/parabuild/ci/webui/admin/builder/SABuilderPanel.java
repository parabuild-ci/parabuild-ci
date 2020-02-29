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

import junit.framework.TestCase;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.common.WebUIConstants;

/**
 * BuilderPanel Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>09/28/2008</pre>
 */
public final class SABuilderPanel extends TestCase {

  private AgentPanel agentPanel = null;


  public SABuilderPanel(final String s) {
    super(s);
  }


  public void testGetBuilderConfigurationID() throws Exception {
    assertEquals(agentPanel.getAgentID(), AgentConfig.UNSAVED_ID);
  }


  public void testToString() {
    assertNotNull(agentPanel.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    agentPanel = new AgentPanel(WebUIConstants.MODE_EDIT);
  }


  public String toString() {
    return "SABuilderPanel{" +
            "builderPanel=" + agentPanel +
            '}';
  }
}
