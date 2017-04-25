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

import org.apache.cactus.*;

import junit.framework.*;

/**
 * Tests UserGroupsPanel
 */
public class SSTestUserGroupsPanel extends ServletTestCase {

  private static final int TEST_USER_ID_2 = 2;

  private UserGroupsPanel pnlUserGroups = null;


  public SSTestUserGroupsPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_load() throws Exception {
    pnlUserGroups.load(TEST_USER_ID_2);
    assertEquals(3, pnlUserGroups.getGroupCount());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestUserGroupsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    pnlUserGroups = new UserGroupsPanel();
  }
}
