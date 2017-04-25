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
package org.parabuild.ci.remote;

import junit.framework.TestCase;
import org.parabuild.ci.configuration.AgentHost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tester for AgentUse.
 */
public final class SATestAgentUse extends TestCase {

  private AgentUse agentUse;


  public void testGetCheckoutCounter() throws Exception {

    assertEquals(0, agentUse.getCheckoutCounter());

    agentUse.incrementCheckoutCounter();
    assertEquals(1, agentUse.getCheckoutCounter());

    agentUse.decrementCheckoutCounter();
    assertEquals(0, agentUse.getCheckoutCounter());

    agentUse.decrementCheckoutCounter();
    assertEquals(0, agentUse.getCheckoutCounter());
  }


  public void testGetTotalCheckouts() throws Exception {

    assertEquals(0, agentUse.getTotalCheckouts());

    agentUse.incrementCheckoutCounter();
    assertEquals(1, agentUse.getTotalCheckouts());

    agentUse.incrementCheckoutCounter();
    assertEquals(2, agentUse.getTotalCheckouts());
  }


  public void testGetAgentHost() throws Exception {

    assertEquals(new AgentHost("localhost:8888"), agentUse.getAgentHost());
  }


  public void testOrdering() {

    final AgentUse use0 = new AgentUse(new AgentHost("localhost:0"));
    use0.incrementCheckoutCounter();
    use0.incrementCheckoutCounter();
    use0.incrementCheckoutCounter();
    use0.incrementCheckoutCounter();

    final AgentUse use1 = new AgentUse(new AgentHost("localhost:1"));
    use1.incrementCheckoutCounter();
    use1.incrementCheckoutCounter();
    use1.incrementCheckoutCounter();

    final AgentUse use2 = new AgentUse(new AgentHost("localhost:2"));
    use2.incrementCheckoutCounter();
    use2.incrementCheckoutCounter();

    final AgentUse use3 = new AgentUse(new AgentHost("localhost:3"));
    use3.incrementCheckoutCounter();

    final List agentUseList = new ArrayList(5);
    agentUseList.add(use0);
    agentUseList.add(use1);
    agentUseList.add(use2);
    agentUseList.add(use3);

    // Sort
    Collections.sort(agentUseList, AgentUse.REVERSE_ORDER_USE_COMPARATOR);

    // Assert
    assertEquals(new AgentHost("localhost:3"), ((AgentUse)agentUseList.get(0)).getAgentHost());
    assertEquals(new AgentHost("localhost:2"), ((AgentUse)agentUseList.get(1)).getAgentHost());
    assertEquals(new AgentHost("localhost:1"), ((AgentUse)agentUseList.get(2)).getAgentHost());
    assertEquals(new AgentHost("localhost:0"), ((AgentUse)agentUseList.get(3)).getAgentHost());
  }


  public void testToString() throws Exception {

    assertNotNull(agentUse.toString());
  }


  public void setUp() throws Exception {

    super.setUp();
    agentUse = new AgentUse(new AgentHost("localhost:8888"));
  }


  public SATestAgentUse(final String name) {
    super(name);
  }
}
