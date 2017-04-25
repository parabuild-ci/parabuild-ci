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

import org.apache.cactus.*;

import junit.framework.*;

import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.admin.usermanagement.*;
import org.parabuild.ci.webui.common.*;

/**
 * Tests UserPanel
 */
public class SSTestEditUserPanel extends ServletTestCase {

  private static final int USER_ID_0 = 0;
  private static final int USER_ID_1 = 1;
  private static final int USER_ID_2 = 2;

  private UserPanel editUserPanel = null;


  public SSTestEditUserPanel(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_load() throws Exception {
    editUserPanel.load(SecurityManager.getInstance().getUser(USER_ID_0));
    editUserPanel.load(SecurityManager.getInstance().getUser(USER_ID_1));
    editUserPanel.load(SecurityManager.getInstance().getUser(USER_ID_2));
    assertEquals(USER_ID_2, editUserPanel.getUserID());
  }


  /**
   *
   */
  public void test_loadSetsCurrectUserID() throws Exception {
    editUserPanel.load(SecurityManager.getInstance().getUser(USER_ID_2));
    assertEquals(USER_ID_2, editUserPanel.getUserID());
  }


  /**
   *
   */
  public void test_save() throws Exception {
    loadThanSave(USER_ID_0);
    loadThanSave(USER_ID_1);
    loadThanSave(USER_ID_2);
  }


  public void test_successfulBuildColorHex() {
    assertEquals("006400", Pages.COLOR_BUILD_SUCCESSFUL.toHexString());
  }


  public void test_failedBuildColorHex() {
    assertEquals("8b0000", Pages.COLOR_BUILD_FAILED.toHexString());
  }


  private void loadThanSave(final int userID) {
    editUserPanel.load(org.parabuild.ci.security.SecurityManager.getInstance().getUser(userID));
    editUserPanel.save();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestEditUserPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.editUserPanel = new UserPanel(UserPanel.MODE_EDIT_USER);
  }
}
