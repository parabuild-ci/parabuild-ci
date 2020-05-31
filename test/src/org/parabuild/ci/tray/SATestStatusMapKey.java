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

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests StatusMapKey
 */
public class SATestStatusMapKey extends TestCase {


  private static final String TEST_HOST_PORT = "localhost";
  private static final Integer TEST_BUILD_ID = Integer.valueOf(1);

  private StatusMapKey statusMapKey = null;


  /**
   */
  public void test_getBuildID() throws Exception {
    assertEquals(TEST_BUILD_ID, statusMapKey.getBuildID());
  }


  /**
   */
  public void test_getHostPort() throws Exception {
    assertEquals(TEST_HOST_PORT, statusMapKey.getHostPort());
  }


  /**
   */
  public void test_equals() throws Exception {
    assertEquals(new StatusMapKey(TEST_HOST_PORT, TEST_BUILD_ID), statusMapKey);
  }


  /**
   */
  public void test_hashCode() throws Exception {
    assertEquals(new StatusMapKey(TEST_HOST_PORT, TEST_BUILD_ID).hashCode(), statusMapKey.hashCode());
  }


  protected void setUp() throws Exception {
    super.setUp();
    statusMapKey = new StatusMapKey(TEST_HOST_PORT, TEST_BUILD_ID);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestStatusMapKey.class, new String[]{
    });
  }


  public SATestStatusMapKey(final String s) {
    super(s);
  }
}
