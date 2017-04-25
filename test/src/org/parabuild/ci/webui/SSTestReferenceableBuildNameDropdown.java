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
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.admin.*;

/**
 * Tests ReferenceableBuildNameDropdown
 */
public class SSTestReferenceableBuildNameDropdown extends ServersideTestCase {

  private ReferenceableBuildNameDropdown dropDown = null;


  public SSTestReferenceableBuildNameDropdown(final String s) {
    super(s);
  }


  public void test_defaultSelection() {
    assertEquals(BuildConfig.UNSAVED_ID, dropDown.getSelectedBuildID());
  }


  /**
   * @see ReferenceableBuildNameDropdown.ITEM_PLEASE_SELECT
   */
  public void test_getsPopulated() {
    assertEquals(19, dropDown.getItemCount());
  }


  /**
   * @see ReferenceableBuildNameDropdown.ITEM_PLEASE_SELECT
   */
  public void test_setSelectedBuildID() {
    dropDown.setSelectedBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertEquals(TestHelper.TEST_CVS_VALID_BUILD_ID, dropDown.getSelectedBuildID());
  }


  public void text_excludeBuild() {
    final int itemCountAfterExclusion = 11; // number of active builds minus excluded plus "ReferenceableBuildNameDropdown.ITEM_PLEASE_SELECT"
    dropDown.excludeBuildID(4);
    assertEquals(itemCountAfterExclusion, dropDown.getItemCount());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestReferenceableBuildNameDropdown.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    dropDown = new ReferenceableBuildNameDropdown();
  }
}
