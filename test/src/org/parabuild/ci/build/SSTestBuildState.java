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
package org.parabuild.ci.build;

import java.util.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;


public class SSTestBuildState extends ServersideTestCase {

  BuildState buildState = null;

  public static final String TEST_BUILD_NAME = "Test Build";
  public static final int TEST_BUILD_ID = 1000;


  public void test_setGetBuildName() {
    buildState.setBuildName(TEST_BUILD_NAME);
    assertEquals(TEST_BUILD_NAME, buildState.getBuildName());
  }


  public void test_setGetBuildID() {
    buildState.setActiveBuildID(TEST_BUILD_ID);
    assertEquals(TEST_BUILD_ID, buildState.getActiveBuildID());
  }


  public void test_getLastCompleteBuildRun() {
    assertNull(buildState.getLastCompleteBuildRun());
    buildState.setLastCompleteBuildRun(new BuildRun());
    assertNotNull(buildState.getLastCompleteBuildRun());
  }


  public void test_setLastBuildAt() {
    assertTrue(buildState.getFinishedAtAsString().equals(BuildState.STRING_NOT_RUN_YET));
    assertNotNull(buildState.getBuildResultAsString());
    assertTrue(StringUtils.isBlank(buildState.getBuildResultAsString()));
    final BuildRun br = new BuildRun();
    br.setFinishedAt(new Date());
    buildState.setLastCompleteBuildRun(br);
    assertNotNull(buildState.getFinishedAt());
    assertTrue(!buildState.getFinishedAtAsString().equals(BuildState.STRING_NOT_RUN_YET));
  }


  public void test_getBuildResultAsString() {
    assertNotNull(buildState.getBuildResultAsString());
    assertTrue(StringUtils.isBlank(buildState.getBuildResultAsString()));
    final BuildRun br = new BuildRun();
    br.setResultID(BuildRun.BUILD_RESULT_SUCCESS);
    buildState.setLastCompleteBuildRun(br);
    assertNotNull(buildState.getBuildResultAsString());
    assertTrue(!StringUtils.isBlank(buildState.getBuildResultAsString()));
  }


  public void test_getLastBuildRunID() {
    assertTrue(buildState.getLastBuildRunID() == BuildRun.UNSAVED_ID);
    final BuildRun br = new BuildRun();
    br.setBuildRunID(9999);
    buildState.setLastCompleteBuildRun(br);
    assertEquals(9999, buildState.getLastBuildRunID());
  }


  protected void setUp() throws Exception {
    buildState = new BuildState();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestBuildState.class);
  }


  public SSTestBuildState(final String s) {
    super(s);
  }
}
