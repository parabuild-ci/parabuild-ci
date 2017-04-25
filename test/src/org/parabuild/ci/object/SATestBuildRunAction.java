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

import java.util.*;

import junit.framework.*;

/**
 *
 */
public class SATestBuildRunAction extends TestCase {

  private BuildRunAction bra;
  private static final String TEST_ACTION = "test_action";
  private static final int TEST_BUILD_RUN_ID = 999;
  private static final byte TEST_CODE = 33;
  private static final Date TEST_DATE = new Date();
  private static final String TEST_DESCIPTION = "test_desciption";
  private static final int TEST_ID = 444;
  private static final int TEST_USER_ID = 888;


  public void test_setAction() {
    bra.setAction(TEST_ACTION);
    assertEquals(TEST_ACTION, bra.getAction());
  }


  public void test_setBuildRunID() {
    bra.setBuildRunID(TEST_BUILD_RUN_ID);
    assertEquals(TEST_BUILD_RUN_ID, bra.getBuildRunID());
  }


  public void test_setCode() {
    bra.setCode(TEST_CODE);
    assertEquals(TEST_CODE, bra.getCode());
  }


  public void test_setDate() {
    bra.setDate(TEST_DATE);
    assertEquals(TEST_DATE, bra.getDate());
  }


  public void test_setDescription() {
    bra.setDescription(TEST_DESCIPTION);
    assertEquals(TEST_DESCIPTION, bra.getDescription());
  }


  public void test_setID() {
    bra.setID(TEST_ID);
    assertEquals(TEST_ID, bra.getID());
  }


  public void test_getUserID() {
    bra.setUserID(TEST_USER_ID);
    assertEquals(TEST_USER_ID, bra.getUserID());
  }


  protected void setUp() throws Exception {
    super.setUp();
    bra = new BuildRunAction();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestBuildRunAction.class);
  }


  public SATestBuildRunAction(final String s) {
    super(s);
  }
}
