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

import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.process.LinuxProcessParser;
import org.parabuild.ci.process.ProcessParser;
import org.parabuild.ci.process.SolarisProcessParser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A tester for {@link AgentManager}.
 */
public final class SSTestAgentManager extends ServersideTestCase {

  private AgentEnvironment agentEnvironment;
  private ProcessParser solaris;
  private ProcessParser linux;


  public SSTestAgentManager(String name) {
    super(name);
  }


  /**
   * Tests Solaris process parser
   */
  public void test_solarisParser() throws Exception {

    final List ret = new ArrayList(11);
    final Map tree = new HashMap(11);
    final Map m = new HashMap(11);

    InputStream processes = new FileInputStream(TestHelper.getTestDataDir() + "/test_ps_output_solaris.txt");
    try {
      solaris.parse(processes, ret, m, tree);
    } finally {
      IoUtils.closeHard(processes);
    }

    assertTrue("Processes not found", !ret.isEmpty());
    assertEquals("Wrong number of processes reterieved", 38, ret.size());

    m.clear();

    processes = new FileInputStream(TestHelper.getTestDataDir() + "/test_ps_env_output_solaris.txt");
    try {
      solaris.parseEnvironment(processes, m);
    } finally {
      IoUtils.closeHard(processes);
    }

    assertEquals("Wrong number of environment reterieved", 2, m.keySet().size());
  }


  /**
   * Tests Linux process parser
   */
  public void test_linuxParser() throws Exception {

    final List ret = new ArrayList(11);
    final Map tree = new HashMap(11);
    final Map m = new HashMap(11);

    InputStream processes = new FileInputStream(TestHelper.getTestDataDir() + "/test_ps_output_linux.txt");
    try {
      linux.parse(processes, ret, m, tree);
    } finally {
      IoUtils.closeHard(processes);
    }

    assertTrue("Processes not found", !ret.isEmpty());
    assertEquals("Wrong number of processes reterieved", 39, ret.size());

    m.clear();

    processes = new FileInputStream(TestHelper.getTestDataDir() + "/test_ps_env_output_linux.txt");
    try {
      linux.parseEnvironment(processes, m);
    } finally {
      IoUtils.closeHard(processes);
    }

    assertEquals("Wrong number of environment reterieved", 39, m.keySet().size());
  }


  protected void setUp() throws Exception {
    super.setUp();
    agentEnvironment = AgentManager.getInstance().getAgentEnvironment(new AgentHost(AgentConfig.BUILD_MANAGER, ""));
    linux = new LinuxProcessParser(agentEnvironment);
    solaris = new SolarisProcessParser(agentEnvironment);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestAgentManager.class);
  }
}