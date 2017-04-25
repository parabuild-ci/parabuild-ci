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
package org.parabuild.ci.tray;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;

/**
 * Tests MenuStatus
 */
public class WindowsOnlySATestSettingsDialog extends TestCase {

  private SettingsDialog settingsDialog;
  private static final int TEST_POLL_INTERVAL = 999;
  private static final String TEST_SERVER_LIST = "server1,server2";


  /**
   */
  public void test_setPollInterval() throws Exception {
    settingsDialog.setPollInterval(TEST_POLL_INTERVAL);
    assertEquals(TEST_POLL_INTERVAL, settingsDialog.getPollInterval());
  }


  /**
   */
  public void test_setServerList() throws Exception {
    settingsDialog.setServerList(TEST_SERVER_LIST);
    assertEquals(TEST_SERVER_LIST, settingsDialog.getServerList());
  }


  protected void setUp() throws Exception {
    super.setUp();
    settingsDialog = new SettingsDialog();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(WindowsOnlySATestSettingsDialog.class, new String[]{
    });
  }


  public WindowsOnlySATestSettingsDialog(final String s) {
    super(s);
  }
}
