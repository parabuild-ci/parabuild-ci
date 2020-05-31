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
package org.parabuild.ci.process;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.util.IoUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests ProcessManager functionality
 */
public class SSTestProcessUtils extends TestCase {

  private OSProcess p1;
  private OSProcess p2;
  private OSProcess p3;

  private Map processes;
  private Map tree;
  private AgentEnvironment agentEnvironment;


  public SSTestProcessUtils(final String s) {
    super(s);
  }


  /**
   * Test pattern matching
   */
  public void test_matching() throws Exception {
    assertTrue(ProcessUtils.matches("abc", new String[]{"a"}));
    assertTrue(!ProcessUtils.matches("abc", new String[]{"A"}));
    assertTrue(ProcessUtils.matches("abc", null));
    assertTrue(ProcessUtils.matches("abc", new String[0]));
  }


  /**
   * Tests conversion String->PID
   */
  public void test_conversion() throws Exception {
      assertEquals(ProcessUtils.getPID("abc"), -1);
      assertEquals(1234, ProcessUtils.getPID("1234"));
  }


  /**
   * Test command executions
   */
  public void test_execution() throws Exception {
    InputStream is = null;
    try {
      is = ProcessUtils.execute(agentEnvironment, "sleep 1");
        assertNotNull(is);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  /**
   * Tests children collections
   */
  public void test_collection() throws Exception {
    Set found = new HashSet(11);
    ProcessUtils.collectChildren(found, p1, tree, processes, false);
    assertTrue(found.contains(p1));
    assertTrue(!found.contains(p2));
    assertTrue(!found.contains(p3));
    found = new HashSet(11);
    ProcessUtils.collectChildren(found, p1, tree, processes, true);
    assertTrue(found.contains(p1));
    assertTrue(found.contains(p2));
    assertTrue(found.contains(p3));
    found = new HashSet(11);
    ProcessUtils.collectChildren(found, p2, tree, processes, true);
    assertTrue(found.contains(p2));
    assertTrue(!found.contains(p1));
    assertTrue(!found.contains(p3));
    found = new HashSet(11);
    ProcessUtils.collectChildren(found, p2, tree, processes, false);
    assertTrue(found.contains(p2));
    assertTrue(!found.contains(p1));
    assertTrue(!found.contains(p3));
  }


  protected void setUp() throws Exception {
    super.setUp();

    // create processes structure
    processes = new HashMap(11);
    p1 = new OSProcess(1, 2, "A", "B", "C", "D");
    p2 = new OSProcess(3, 1, "A", "B", "C", "D");
    p3 = new OSProcess(4, 1, "A", "B", "C", "D");
    processes.put(Integer.valueOf(p1.getPID()), p1);
    processes.put(Integer.valueOf(p2.getPID()), p2);
    processes.put(Integer.valueOf(p3.getPID()), p3);

    tree = new HashMap(11);
    final List l = new ArrayList(11);
    l.add(Integer.valueOf(p2.getPID()));
    l.add(Integer.valueOf(p3.getPID()));
    tree.put(Integer.valueOf(p1.getPID()), l);

    agentEnvironment = AgentManager.getInstance().getAgentEnvironment(new AgentHost(AgentConfig.BUILD_MANAGER, ""));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestProcessUtils.class);
  }
}
