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

import org.parabuild.ci.object.*;

/**
 * Tests BuildRunParticipantVO
 */
public class SATestBuildRunParticipantVO extends TestCase {

  public SATestBuildRunParticipantVO(final String s) {
    super(s);
  }


  public void test_create() {
    final int TEST_RUN_ID = 9999;
    final int TEST_RUN_NUMBER = 1111;
    final ChangeList changeList = new ChangeList();
    final BuildRunParticipantVO buildRunParticipantVO = new BuildRunParticipantVO(changeList, TEST_RUN_ID, TEST_RUN_NUMBER, 1);
    assertEquals(changeList, buildRunParticipantVO.getChangeList());
    assertEquals(TEST_RUN_ID, buildRunParticipantVO.getFirstBuildRunID());
    assertEquals(TEST_RUN_NUMBER, buildRunParticipantVO.getFirstBuildRunNumber());
    assertEquals(TEST_RUN_NUMBER, Integer.parseInt(buildRunParticipantVO.getFirstBuildRunNumberAsString()));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestBuildRunParticipantVO.class);
  }
}
