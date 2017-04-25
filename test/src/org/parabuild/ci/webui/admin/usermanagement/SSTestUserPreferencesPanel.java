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
package org.parabuild.ci.webui.admin.usermanagement;

import java.util.*;
import org.apache.cactus.*;

import junit.framework.*;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;

/**
 * Tests UserPanel
 */
public class SSTestUserPreferencesPanel extends ServletTestCase {

  private static final int TEST_USER_ID_2 = 2;

  private UserPanel pnlUser = null;


  public SSTestUserPreferencesPanel(final String s) {
    super(s);
  }


  /**
   * Check that "save" operation does not delete a user from a group.
   */
  public void test_save_bug1071() throws Exception {

    // load

    final org.parabuild.ci.security.SecurityManager sm = SecurityManager.getInstance();
    final User user = sm.getUser(TEST_USER_ID_2);
    pnlUser.load(user);

    // save

    pnlUser.save();

    // assert

    final List displayUserGroups = sm.getDisplayUserGroups(TEST_USER_ID_2);
    boolean found = false;
    for (int i = 0; i < displayUserGroups.size(); i++) {
      final DisplayUserGroupVO displayUserGroupVO = (DisplayUserGroupVO)displayUserGroups.get(i);
      if (displayUserGroupVO.getGroupID() == 1) {
        assertTrue(displayUserGroupVO.isGroupMember());
        found = true;
      }
    }
    assertTrue(found);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestUserPreferencesPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    pnlUser = new UserPanel(UserPanel.MODE_EDIT_PREFERENCES);
  }
}
