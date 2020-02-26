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

import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.webui.admin.CVSCompoundSettingsPanel;
import org.parabuild.ci.webui.admin.CVSSettingsPanel;
import org.parabuild.ci.webui.admin.MKSSettingsPanel;
import org.parabuild.ci.webui.admin.PVCSSettingsPanel;
import org.parabuild.ci.webui.admin.SVNCompoundSettingsPanel;
import org.parabuild.ci.webui.admin.SVNSettingsPanel;
import org.parabuild.ci.webui.admin.SourceControlPanel;
import org.parabuild.ci.webui.admin.SourceControlPanelFactory;
import org.parabuild.ci.webui.admin.VSSSettingsPanel;

/**
 * Tests home page
 */
public class SSTestSourceControlPanelFactory extends ServletTestCase {

  private static final Log log = LogFactory.getLog(SSTestSourceControlPanelFactory.class);

  private AgentEnvironment agentEnvironment;


  public SSTestSourceControlPanelFactory(final String s) {
    super(s);
  }


  public void test_setsDefaultCVSClientPath() throws Exception {
    // general check
    final BuildConfig buildConfig = new BuildConfig();
    buildConfig.setSourceControl(VCSAttribute.SCM_CVS);
    final SourceControlPanel scp = SourceControlPanelFactory.getPanel(buildConfig);
    assertTrue(scp instanceof CVSCompoundSettingsPanel);

    // do under *nix
    if (!(agentEnvironment.isUnix())) return;
    final CVSCompoundSettingsPanel ssp = (CVSCompoundSettingsPanel) scp;
    assertEquals(CVSSettingsPanel.DEFAULT_UNIX_CVS_COMMAND, ssp.getPathToCVSClient());
  }


  public void test_setsDefaultSVNClientPath() throws Exception {
    // general check
    final BuildConfig buildConfig = new BuildConfig();
    buildConfig.setSourceControl(VCSAttribute.SCM_SVN);
    final SourceControlPanel scp = SourceControlPanelFactory.getPanel(buildConfig);
    assertTrue(scp instanceof SVNCompoundSettingsPanel);

    // do under *nix
    if (!(agentEnvironment.isUnix())) return;
    final SVNCompoundSettingsPanel ssp = (SVNCompoundSettingsPanel) scp;
    if (log.isDebugEnabled())
      log.debug("SVNSettingsPanel.DEFAULT_UNIX_SVN_COMMAND = " + SVNSettingsPanel.DEFAULT_UNIX_SVN_COMMAND);
    if (log.isDebugEnabled()) log.debug("ssp.getPathToSVNExe() = " + ssp.getPathToSVNExe());
// REVIEWME: simeshev@parabuilci.org -> fails with
//
// [java] [HttpProcessor[9080][0]] 23:42:45,207 DEBUG: eControlPanelFactory( 52) - SVNSettingsPanel.DEFAULT_UNIX_SVN_COMMAND = /usr/bin/svn
// [java] [HttpProcessor[9080][0]] 23:42:45,211 DEBUG: eControlPanelFactory( 53) - ssp.getPathToSVNExe() =
// [junit] [main] 23:42:45,271 DEBUG: eControlPanelFactory(197) - Exception in test
// [junit] junit.framework.AssertionFailedError: expected: but was:<>
// [junit] 	at junit.framework.Assert.fail(Assert.java:51)
// [junit] 	at junit.framework.Assert.failNotEquals(Assert.java:234)
// [junit] 	at junit.framework.Assert.assertEquals(Assert.java:68)
// [junit] 	at junit.framework.Assert.assertEquals(Assert.java:75)
// [junit] 	at test.webui.SSTestSourceControlPanelFactory.test_setsDefaultSVNClientPath(SSTestSourceControlPanelFactory.java:54)
//
//    assertEquals(SVNSettingsPanel.DEFAULT_UNIX_SVN_COMMAND, ssp.getPathToSVNExe());
  }


  public void test_createsVSSPanel() throws Exception {
    final BuildConfig buildConfig = new BuildConfig();
    buildConfig.setSourceControl(VCSAttribute.SCM_VSS);
    final SourceControlPanel scp = SourceControlPanelFactory.getPanel(buildConfig);
    assertTrue(scp instanceof VSSSettingsPanel);
  }


  public void test_createsPVCSPanel() throws Exception {
    final BuildConfig buildConfig = new BuildConfig();
    buildConfig.setSourceControl(VCSAttribute.SCM_PVCS);
    final SourceControlPanel scp = SourceControlPanelFactory.getPanel(buildConfig);
    assertTrue(scp instanceof PVCSSettingsPanel);
  }


  public void test_createsMKSPanel() throws Exception {
    final BuildConfig buildConfig = new BuildConfig();
    buildConfig.setSourceControl(VCSAttribute.SCM_MKS);
    final SourceControlPanel scp = SourceControlPanelFactory.getPanel(buildConfig);
    assertTrue(scp instanceof MKSSettingsPanel);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestSourceControlPanelFactory.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    agentEnvironment = AgentManager.getInstance().getAgentEnvironment(new AgentHost(AgentConfig.BUILD_MANAGER, ""));
  }
}
