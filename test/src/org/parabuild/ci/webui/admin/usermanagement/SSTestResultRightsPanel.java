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

import org.parabuild.ci.object.*;

/**
 * Tests ResultRightsPanel
 */
public class SSTestResultRightsPanel extends ServletTestCase {

  private static final int TEST_GROUP_ID = 0;

  private ResultRightsPanel pnl = null;


  public SSTestResultRightsPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_loadValidateSave() throws Exception {
    final Group group = org.parabuild.ci.security.SecurityManager.getInstance().getGroup(TEST_GROUP_ID);
    pnl.load(group);
    assertTrue(pnl.validate());
    pnl.save(TEST_GROUP_ID);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestResultRightsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    pnl = new ResultRightsPanel();
  }
}
