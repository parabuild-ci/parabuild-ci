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
public class SATestMenuStatus extends TestCase {

  private static final String TEST_IMAGE = "test_image";
  private static final String TEST_CAPTION = "test_caption";


  /**
   */
  public void test_create() throws Exception {
    final MenuStatus menuStatus = new MenuStatus(TEST_IMAGE, TEST_CAPTION);
    assertTrue(menuStatus.getImage().endsWith(TEST_IMAGE));
    assertEquals(TEST_CAPTION, menuStatus.getCaption());
  }


  /**
   */
  public void test_equal() throws Exception {
    assertEquals(new MenuStatus(TEST_IMAGE, TEST_CAPTION), new MenuStatus(TEST_IMAGE, TEST_CAPTION));
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestMenuStatus.class, new String[]{
    });
  }


  public SATestMenuStatus(final String s) {
    super(s);
  }
}
