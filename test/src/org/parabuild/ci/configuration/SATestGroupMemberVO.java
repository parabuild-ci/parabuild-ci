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
package org.parabuild.ci.configuration;

import junit.framework.*;

/**
 * Tests BuildRunParticipantVO
 */
public class SATestGroupMemberVO extends TestCase {

  private static final boolean TEST_TRUE_GROUP_MEMBER = true;
  private static final boolean TEST_FALSE_GROUP_MEMBER = false;
  private static final int TEST_ID = 999;
  private static final String TEST_NAME = "test_build_name";

  private GroupMemberVO vo;


  public void test_create() {
    assertEquals(TEST_NAME, vo.getName());
    assertEquals(TEST_ID, vo.getID());
    assertEquals(TEST_TRUE_GROUP_MEMBER, vo.isGroupMember());
  }


  public void test_setGroupMember() {
    vo.setGroupMember(TEST_FALSE_GROUP_MEMBER);
    assertEquals(TEST_FALSE_GROUP_MEMBER, vo.isGroupMember());
  }


  protected void setUp() throws Exception {
    super.setUp();
    vo = new GroupMemberVO(TEST_TRUE_GROUP_MEMBER, TEST_ID, TEST_NAME);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestGroupMemberVO.class);
  }


  public SATestGroupMemberVO(final String s) {
    super(s);
  }
}
