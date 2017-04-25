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

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.webui.admin.*;

/**
 * Tests home page
 */
public class SSTestResultSettingsPanel extends ServersideTestCase {

  private ResultSettingsPanel testPanel = null;
  private ConfigurationManager cm;
  public static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;


  public SSTestResultSettingsPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_load() throws Exception {
    testPanel.load(cm.getBuildConfiguration(TEST_BUILD_ID));
    testPanel.setBuildID(TEST_BUILD_ID);
    assertEquals(TEST_BUILD_ID, testPanel.getBuildID());
  }


  public void test_save() throws Exception {
    test_load();
    assertTrue(testPanel.save());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestResultSettingsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    testPanel = new ResultSettingsPanel();
  }
}
