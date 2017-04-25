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
package org.parabuild.ci.versioncontrol;

import java.util.Map;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildChangeList;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;


/**
 * Tests ReferenceSourceControl backed up by P4SourceControl
 */
public class SSTestReferenceSourceControl extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestReferenceSourceControl.class);
  private static final int TEST_CHANGE_LIST_ID_7 = 7;

  private ReferenceSourceControl refSCM = null;
  private ConfigurationManager cm = null;
  private Agent agent = null;


  public void test_empty() {

  }


  public void test_getUsersMap() throws Exception {
    final Map result = refSCM.getUsersMap();
    assertTrue(!result.isEmpty());
  }


  public void test_getRelativeBuildDir() throws Exception {
    refSCM.getRelativeBuildDir();
  }


  public void test_syncToChangeList() throws Exception {

    refSCM.syncToChangeList(TEST_CHANGE_LIST_ID_7);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   *
   */
  public void test_getChangesSince() throws BuildException {
    final int newChangeListID = refSCM.getChangesSince(TEST_CHANGE_LIST_ID_7);
    if (log.isDebugEnabled()) log.debug("newChangeListID = " + newChangeListID);
    assertTrue(newChangeListID != TEST_CHANGE_LIST_ID_7);
    assertNotNull(cm.getChangeList(newChangeListID));

    // test that build change list is marked as new
    final BuildChangeList buildChangeList = cm.getBuildChangeList(TestHelper.TEST_RECURRENT_BUILD_ID, newChangeListID);
    assertEquals("Y", buildChangeList.getNew());
  }


  public void test_syncToLatest() throws Exception {
    // clean up log dir
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());

    // sync
    refSCM.checkoutLatest();

    // check
    TestHelper.assertCheckoutDirNotEmpty(agent);
    assertTrue("Build logs home should be empty", agent.logDirIsEmpty());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestReferenceSourceControl.class, new String[]{
            "test_getChangesSince",
            "test_getUsersMap",
            "test_getRelativeBuildDir",
            "test_syncToChangeList"
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
    final AgentHost agentHost = AgentManager.getInstance().getNextLiveAgentHost(TestHelper.TEST_RECURRENT_BUILD_ID);
    agent = AgentManager.getInstance().createAgent(TestHelper.TEST_RECURRENT_BUILD_ID, agentHost);
    cm = ConfigurationManager.getInstance();
    final BuildConfig buildConfig = cm.getBuildConfiguration(TestHelper.TEST_RECURRENT_BUILD_ID);
    refSCM = (ReferenceSourceControl) VersionControlFactory.makeVersionControl(buildConfig);
    refSCM.setAgentHost(agentHost);
  }


  public SSTestReferenceSourceControl(final String s) {
    super(s);
  }
}
