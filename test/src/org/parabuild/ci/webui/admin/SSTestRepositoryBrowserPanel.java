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
package org.parabuild.ci.webui.admin;

import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.common.WebUIConstants;

/**
 * Tests RepositoryBrowserPanel
 */
public class SSTestRepositoryBrowserPanel extends ServletTestCase {

  public static final int TEST_CVS_VALID_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;

  /**
   * @noinspection UNUSED_SYMBOL, FieldCanBeLocal
   */
  private RepositoryBrowserPanel panel = null;


  public SSTestRepositoryBrowserPanel(final String s) {
    super(s);
  }


  /**
   * @throws Exception if an error occured while executing the test.
   */
  public void test_setMode() throws Exception {
    panel.setMode(WebUIConstants.MODE_VIEW);
    panel.setMode(WebUIConstants.MODE_EDIT);
  }


  /**
   * @throws Exception if an error occured while executing the test.
   */
  public void test_validate() throws Exception {
    assertTrue(panel.validate());
  }


  /**
   * @throws Exception if an error occured while executing the test.
   */
  public void test_loadFishEye() throws Exception {
    TestHelper.setSourceControlProperty(TEST_CVS_VALID_BUILD_ID, VersionControlSystem.REPOSITORY_BROWSER_TYPE, Integer.toString(VersionControlSystem.CODE_FISHEYE));
    TestHelper.setSourceControlProperty(TEST_CVS_VALID_BUILD_ID, VersionControlSystem.FISHEYE_ROOT, "fish_eye_root");
    TestHelper.setSourceControlProperty(TEST_CVS_VALID_BUILD_ID, VersionControlSystem.FISHEYE_URL, "http://fish_eye_url");
    final BuildConfig buildConfig = ConfigurationManager.getInstance().getActiveBuildConfig(TEST_CVS_VALID_BUILD_ID);
    panel.load(buildConfig);
    assertEquals(TEST_CVS_VALID_BUILD_ID, panel.getBuildID());
  }


  /**
   * @throws Exception if an error occured while executing the test.
   */
  public void test_loadWebSVN() throws Exception {
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, VersionControlSystem.REPOSITORY_BROWSER_TYPE, Integer.toString(VersionControlSystem.CODE_WEB_SVN));
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, VersionControlSystem.WEB_SVN_URL, "https://host.net/websvn/mycompany/");
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, VersionControlSystem.WEB_SVN_REPNAME, "mycompany");
    final BuildConfig buildConfig = ConfigurationManager.getInstance().getActiveBuildConfig(TestHelper.TEST_CVS_VALID_BUILD_ID);
    panel.load(buildConfig);
    assertEquals(TestHelper.TEST_CVS_VALID_BUILD_ID, panel.getBuildID());
  }


  /**
   * @throws Exception if an error occured while executing the test.
   */
  public void test_loadViewVC() throws Exception {
    TestHelper.setSourceControlProperty(TEST_CVS_VALID_BUILD_ID, VersionControlSystem.REPOSITORY_BROWSER_TYPE, Integer.toString(VersionControlSystem.CODE_VIEWVC));
    TestHelper.setSourceControlProperty(TEST_CVS_VALID_BUILD_ID, VersionControlSystem.VIEWCVS_ROOT, "viewvc_root");
    TestHelper.setSourceControlProperty(TEST_CVS_VALID_BUILD_ID, VersionControlSystem.VIEWCVS_URL, "http://viewvc_url");
    final BuildConfig buildConfig = ConfigurationManager.getInstance().getActiveBuildConfig(TEST_CVS_VALID_BUILD_ID);
    panel.load(buildConfig);
    assertEquals(TEST_CVS_VALID_BUILD_ID, panel.getBuildID());
  }


  /**
   * @throws Exception if an error occured while executing the test.
   */
  public void test_loadNotSelected() throws Exception {
    TestHelper.setSourceControlProperty(TEST_CVS_VALID_BUILD_ID, VersionControlSystem.REPOSITORY_BROWSER_TYPE, Integer.toString(VersionControlSystem.CODE_NOT_SELECTED));
    final BuildConfig buildConfig = ConfigurationManager.getInstance().getActiveBuildConfig(TEST_CVS_VALID_BUILD_ID);
    panel.load(buildConfig);
    assertEquals(TEST_CVS_VALID_BUILD_ID, panel.getBuildID());
  }


  /**
   * Required by JUnit
   *
   * @return a new test suite.
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestRepositoryBrowserPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    panel = new RepositoryBrowserPanel(true);
  }
}
