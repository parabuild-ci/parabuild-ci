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

import junit.framework.*;
import org.apache.cactus.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;

/**
 * Tests SurroundSettingsPanel
 */
public class SSTestSurroundSettingsPanel extends ServletTestCase {

  private SurroundSettingsPanel cvsSettingsPanel = null;


  public SSTestSurroundSettingsPanel(final String s) {
    super(s);
  }


  /**
   * Makes sure that it doesn't throw the exception
   */
  public void test_setSettings_Bug143() throws Exception {
    cvsSettingsPanel.load(ConfigurationManager.getInstance().getActiveBuildConfig(TestHelper.TEST_SURROUND_VALID_BUILD_ID));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestSurroundSettingsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cvsSettingsPanel = new SurroundSettingsPanel();
  }
}
