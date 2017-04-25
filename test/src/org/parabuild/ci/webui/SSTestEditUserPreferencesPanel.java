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

/**
 * Tests UserPanel
 *
 * @see UserPanel.EDIT_MODE_PREFERENCES
 */
public class SSTestEditUserPreferencesPanel extends ServletTestCase {

  private UserPanel editUserPanel = null;


  public SSTestEditUserPreferencesPanel(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_load() throws Exception {
    editUserPanel.load(org.parabuild.ci.security.SecurityManager.getInstance().getUser(1));
    editUserPanel.load(SecurityManager.getInstance().getUser(2));
    editUserPanel.load(SecurityManager.getInstance().getUser(3));
  }


  /**
   *
   */
  public void test_save() throws Exception {
    loadThanSave(1);
    loadThanSave(2);
    loadThanSave(3);
  }


  private void loadThanSave(final int userID) {
    editUserPanel.load(SecurityManager.getInstance().getUser(userID));
    editUserPanel.save();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestEditUserPreferencesPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.editUserPanel = new UserPanel(UserPanel.MODE_EDIT_PREFERENCES);
  }
}
