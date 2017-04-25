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
package org.parabuild.ci.webui.admin.displaygroup;

import org.apache.cactus.*;

import junit.framework.*;

import org.parabuild.ci.configuration.*;

/**
 * Tests EditDisplayGroupPanel
 */
public class SSTestEditDisplayGroupPanel extends ServletTestCase {

  private EditDisplayGroupPanel editDisplayGroupPanel = null;
  private static final int TEST_DISPLAY_GROUP_ID = 0;


  public SSTestEditDisplayGroupPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_create() throws Exception {
    // do nothing - created in setUp
  }


  /**
   */
  public void test_load() throws Exception {
    editDisplayGroupPanel.load(DisplayGroupManager.getInstance().getDisplayGroup(TEST_DISPLAY_GROUP_ID));
  }


  /**
   */
  public void test_loadSave() throws Exception {
    editDisplayGroupPanel.load(DisplayGroupManager.getInstance().getDisplayGroup(TEST_DISPLAY_GROUP_ID));
    editDisplayGroupPanel.save();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestEditDisplayGroupPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    editDisplayGroupPanel = new EditDisplayGroupPanel();
  }
}
