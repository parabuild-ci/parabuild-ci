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
import org.parabuild.ci.webui.common.WebUIConstants;

/**
 * Tests FishEyeSettingsPanel
 */
public class SSTestFishEyeSettingsPanel extends ServletTestCase {

  /** @noinspection UNUSED_SYMBOL,FieldCanBeLocal*/
  private FishEyeSettingsPanel panel = null;


  public SSTestFishEyeSettingsPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_setMode() throws Exception {
    panel.setMode(WebUIConstants.MODE_VIEW);
    panel.setMode(WebUIConstants.MODE_EDIT);
  }


  /**
   */
  public void test_validate() throws Exception {
    assertTrue(panel.doValidate());
  }


  /**
   */
  public void test_load() throws Exception {
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, VersionControlSystem.REPOSITORY_BROWSER_TYPE, Integer.toString(VersionControlSystem.CODE_FISHEYE));
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, VersionControlSystem.FISHEYE_ROOT, "fish_eye_root");
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, VersionControlSystem.FISHEYE_URL, "http://fish_eye_url");
    final BuildConfig buildConfig = ConfigurationManager.getInstance().getActiveBuildConfig(TestHelper.TEST_CVS_VALID_BUILD_ID);
    panel.load(buildConfig);
    assertEquals(TestHelper.TEST_CVS_VALID_BUILD_ID, panel.getBuildID());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestFishEyeSettingsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    panel = new FishEyeSettingsPanel(true);
  }
}
