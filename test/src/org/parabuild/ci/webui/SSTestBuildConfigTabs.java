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
package org.parabuild.ci.webui;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.webui.admin.BuildConfigTabs;

/**
 *
 */
public class SSTestBuildConfigTabs extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log LOG = LogFactory.getLog(SSTestBuildConfigTabs.class); // NOPMD
  private static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  private BuildConfigTabs buildConfigTabs = null;


  public SSTestBuildConfigTabs(final String s) {
    super(s);
  }


  /**
   */
  public void test_setBuildID() throws Exception {
    buildConfigTabs.setBuildID(TEST_BUILD_ID);
  }


  /**
   */
  public void test_load() throws Exception {
    final BuildConfig buildConfig = load();
    assertEquals(TEST_BUILD_ID, buildConfig.getBuildID());
  }


  /**
   */
  public void test_validate() throws Exception {
    load();
    assertTrue(buildConfigTabs.validate());
  }


  /**
   */
  public void test_save() throws Exception {
    load();
    assertTrue(buildConfigTabs.validate());
    assertTrue(buildConfigTabs.save());
  }


  /**
   */
  public void test_saveParallel() throws Exception {
    final BuildConfig bc = ConfigurationManager.getInstance().getBuildConfiguration(TestHelper.TEST_DEPENDENT_PARALLEL_BUILD_ID_1);
    final BuildConfigTabs parallelBCT = new BuildConfigTabs(bc);
    parallelBCT.load(bc);
    assertTrue("Parallel build config tabs should be valid", parallelBCT.validate());
    assertTrue(parallelBCT.save());
  }


  private BuildConfig load() {
    final BuildConfig buildConfig = ConfigurationManager.getInstance().getBuildConfiguration(TEST_BUILD_ID);
    buildConfigTabs.load(buildConfig);
    return buildConfig;
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestBuildConfigTabs.class, new String[]{
            "test_saveParallel",
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
    // get build config
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildConfig buildConfig = cm.getBuildConfiguration(TEST_BUILD_ID);
    // alter path to CVS according to OS
    final AgentEnvironment agentEnvironment = AgentManager.getInstance().getAgentEnvironment(new AgentHost(AgentConfig.BUILD_MANAGER, ""));
    final SourceControlSetting cvsPath = cm.getSourceControlSetting(TEST_BUILD_ID, SourceControlSetting.CVS_PATH_TO_CLIENT);
    if (agentEnvironment.isWindows() || agentEnvironment.systemType() == AgentEnvironment.SYSTEM_TYPE_CYGWIN) {
      final String cvsExePath = agentEnvironment.getSystemProperty("test.cvs.exe");
      cvsPath.setPropertyValue(cvsExePath);
    } else {
      cvsPath.setPropertyValue("/usr/bin/cvs");
    }
    cm.saveObject(cvsPath);
    // create tabs
    buildConfigTabs = new BuildConfigTabs(buildConfig);
  }
}
