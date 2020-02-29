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

import org.apache.cactus.*;

import junit.framework.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * Tests VaultSettingsPanel
 */
public class SSTestVaultSettingsPanel extends ServletTestCase {

  /** @noinspection UNUSED_SYMBOL,FieldCanBeLocal*/
  private VaultSettingsPanel vaultSettingsPanel = null;
  private static final int TEST_VAULT_VALID_BUILD_ID = TestHelper.TEST_VAULT_VALID_BUILD_ID;


  public SSTestVaultSettingsPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_setMode() throws Exception {
    vaultSettingsPanel.setMode(WebUIConstants.MODE_VIEW);
    vaultSettingsPanel.setMode(WebUIConstants.MODE_EDIT);
  }


  /**
   */
  public void test_validate() throws Exception {
    assertTrue(!vaultSettingsPanel.doValidate());
  }


  /**
   */
  public void test_load() throws Exception {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildConfig buildConfig = cm.getActiveBuildConfig(TEST_VAULT_VALID_BUILD_ID);
    vaultSettingsPanel.load(buildConfig);
    assertEquals(TEST_VAULT_VALID_BUILD_ID, vaultSettingsPanel.getBuildID());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestVaultSettingsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    vaultSettingsPanel = new VaultSettingsPanel();
  }
}
