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
package org.parabuild.ci.object;

import junit.framework.TestCase;

/**
 * GlobalVCSUserMap Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>12/28/2008</pre>
 */
public final class SATestGlobalVCSUserMap extends TestCase {

  private GlobalVCSUserMap globalVCSUserMap = null;
  private static final int ID = 999;
  private static final String TEST_USER_NAME = "test_user_name";
  private static final String TEST_EMAIL = "test@email";
  private static final String TEST_DESCRIPTION = "test_description";


  public SATestGlobalVCSUserMap(String s) {
    super(s);
  }


  public void testSetGetID() throws Exception {
    globalVCSUserMap.setID(ID);
    assertEquals(ID, globalVCSUserMap.getID());
  }


  public void testSetGetVcsUserName() throws Exception {
    globalVCSUserMap.setVcsUserName(TEST_USER_NAME);
    assertEquals(TEST_USER_NAME, globalVCSUserMap.getVcsUserName());
  }


  public void testSetGetEmail() throws Exception {
    globalVCSUserMap.setEmail(TEST_EMAIL);
    assertEquals(TEST_EMAIL, globalVCSUserMap.getEmail());
  }


  public void testSetGetDescription() throws Exception {
    globalVCSUserMap.setDescription(TEST_DESCRIPTION);
    assertEquals(TEST_DESCRIPTION, globalVCSUserMap.getDescription());
  }


  public void testToString() {
    assertNotNull(globalVCSUserMap.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    globalVCSUserMap = new GlobalVCSUserMap();
  }
}
