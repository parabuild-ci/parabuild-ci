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
package org.parabuild.ci.configuration;

import org.apache.log4j.Logger;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuilderAgent;
import org.parabuild.ci.object.BuilderConfiguration;

import java.util.List;

/**
 * BuilderConfigurationManagerTest
 * <p/>
 *
 * @author Slava Imeshev
 * @noinspection ProhibitedExceptionDeclared
 * @since Feb 22, 2009 7:04:12 PM
 */
public final class BuilderConfigurationManagerTest extends ServersideTestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(BuilderConfigurationManagerTest.class); // NOPMD
  private BuilderConfigurationManager manager;


  public BuilderConfigurationManagerTest(final String s) {
    super(s);
  }


  public void testGetInstance() {
    assertNotNull(manager);
  }


  public void testGetBuilder() {
    final BuilderConfiguration builderConfiguration = manager.getBuilder(1);
    assertNotNull(builderConfiguration);
    assertEquals(1, builderConfiguration.getID());
  }


  public void testDeleteBuilder() {
    manager.deleteBuilder(manager.getBuilder(1));
    assertTrue(manager.getBuilder(1).isDeleted());
  }


  public void testFindBuilderByName() {
    final String builderName = "Multi-Agent Build Farm";
    final BuilderConfiguration byName = manager.findBuilderByName(builderName);
    assertNotNull(byName);
    assertEquals(builderName, byName.getName());
  }


  public void testSaveBuilder() {
    final BuilderConfiguration builderConfiguration = manager.getBuilder(1);
    final String description = "New Description";
    builderConfiguration.setDescription(description);
    manager.saveBuilder(builderConfiguration);
    assertEquals(description, manager.getBuilder(1).getDescription());
  }


  public void testGetBuilders() {
    final List builders = manager.getBuilders();
    assertTrue(!builders.isEmpty());
    assertTrue(builders.get(0) instanceof BuilderConfiguration);
  }


  public void testGetAllBuilders() {
    final List builders = manager.getAllBuilders();
    assertTrue(!builders.isEmpty());
    assertTrue(builders.get(0) instanceof BuilderConfiguration);
  }


  public void testGetBuilderAgentVOs() {
    final List agentVOs = manager.getBuilderAgentVOs(1);
    assertTrue(!agentVOs.isEmpty());
    assertTrue(agentVOs.get(0) instanceof BuilderAgentVO);
  }


  public void testBuilderMemberWithHostNameExists() {
    // NOTE: simeshev@parabuildci.org -> 2009-02-22
  }


  public void testGetBuilderAgent() {
    final BuilderAgent builderAgent = manager.getBuilderAgent(1);
    assertNotNull(builderAgent);
  }


  public void testDetachBuilderAgent() {
    manager.detachBuilderAgent(manager.getBuilderAgent(1));
    assertNull(manager.getBuilderAgent(1));
  }


  public void testSaveBuilderAgent() {
    manager.saveBuilderAgent(manager.getBuilderAgent(1));
  }


  public void testGetAgent() {
    final AgentConfig agentConfig = manager.getAgentConfig(1);
    assertNotNull(agentConfig);
  }


  public void testSaveAgent() {
    final AgentConfig agentConfig = manager.getAgentConfig(1);
    final String host = "other.host";
    agentConfig.setHost(host);
    manager.saveAgent(agentConfig);
    assertEquals(host, manager.getAgentConfig(1).getHost());
  }


  public void testGetAgentList() {
    final List list = manager.getAgentList();
    assertTrue(!list.isEmpty());
    assertTrue(manager.getAgentList().get(0) instanceof AgentConfig);
  }


  public void testDeletedAgent() {
    manager.deletedAgent(manager.getAgentConfig(1));
    assertTrue(manager.getAgentConfig(1).isDeleted());
  }


  public void testFindBuilderAgentByAgentID() {
    assertNotNull(manager.findBuilderAgentByAgentID(1, 1));
  }


  public void testHostNameToBuilderName() {
    assertEquals("test_host_8080", BuilderConfigurationManager.hostNameToBuilderName("test.host:8080"));
  }


  public void testGetFirstAgentConfig() {
    assertNotNull(manager.getFirstAgentConfig(1));
  }


  protected void setUp() throws Exception {
    super.setUp();
    manager = BuilderConfigurationManager.getInstance();
  }


  public String toString() {
    return "BuilderConfigurationManagerTest{" +
            "manager=" + manager +
            "} " + super.toString();
  }
}
