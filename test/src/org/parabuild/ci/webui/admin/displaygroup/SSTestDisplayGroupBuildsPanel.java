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

/**
 * Tests DisplayGroupBuildsPanel
 */
public class SSTestDisplayGroupBuildsPanel extends ServletTestCase {

  private DisplayGroupBuildsPanel displayGroupBuildsPanel = null;
  private static final int TEST_DISPLAY_GROUP_ID = 0;


  public SSTestDisplayGroupBuildsPanel(final String s) {
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
    displayGroupBuildsPanel.load(TEST_DISPLAY_GROUP_ID);
  }


  /**
   */
  public void test_loadSave() throws Exception {
    displayGroupBuildsPanel.load(TEST_DISPLAY_GROUP_ID);
    displayGroupBuildsPanel.save();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestDisplayGroupBuildsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    displayGroupBuildsPanel = new DisplayGroupBuildsPanel();
  }
}
