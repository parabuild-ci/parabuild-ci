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

/**
 * Tests ManualStartSettingsPanel
 */
public final class SSTestManualStartSettingsPanel extends ServletTestCase {

  private static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;

  /**
   * @noinspection UNUSED_SYMBOL,FieldCanBeLocal
   */
  private ManualStartSettingsPanel testPanel = null;


  public SSTestManualStartSettingsPanel(final String s) {
    super(s);
  }


  /**
   * @noinspection JUnitTestMethodWithNoAssertions
   */
  public void testCreate() throws Exception {
    // do nothing, create is called in setUp method.
  }


  /**
   */
  public void testLoadSave() throws Exception {
    testPanel.load(ConfigurationManager.getInstance().getBuildConfiguration(TEST_BUILD_ID));
    testPanel.setBuildID(TEST_BUILD_ID);
    testPanel.save();
    assertTrue(testPanel.validate());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestManualStartSettingsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    testPanel = new ManualStartSettingsPanel(VersionControlSystem.SCM_PERFORCE, BuildConfig.SCHEDULE_TYPE_MANUAL);
  }


  public String toString() {
    return "SSTestManualStartSettingsPanel{" +
            "testPanel=" + testPanel +
            "} " + super.toString();
  }
}
