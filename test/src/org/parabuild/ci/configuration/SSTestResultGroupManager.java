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

import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.object.*;

/**
 */
public final class SSTestResultGroupManager extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestResultGroupManager.class);

  private static final int TEST_STEP_RESULT_ID = 0;
  private static final int TEST_RESULT_GROUP_ID = TEST_STEP_RESULT_ID;
  private static final String TEST_RESULT_GROUP_NAME = "Test result group 1";

  private ResultGroupManager rgm = null;


  public void test_getResultGroup() {
    final ResultGroup resultGroup = rgm.getResultGroup(TEST_RESULT_GROUP_ID);
    assertNotNull(resultGroup);
  }


  public void test_deleteResultGroup() {
    rgm.deleteResultGroup(rgm.getResultGroup(TEST_RESULT_GROUP_ID));
    assertNull(rgm.getResultGroup(TEST_RESULT_GROUP_ID));
  }


  public void test_getPublishedStepResult() {
    final PublishedStepResult publishedStepResult = rgm.getPublishedStepResult(TEST_STEP_RESULT_ID, TEST_RESULT_GROUP_ID);
    assertNotNull(publishedStepResult);
  }


  public void test_getPublishedStepResults() {
    final List publishedStepResults = rgm.getPublishedStepResults(TEST_RESULT_GROUP_ID, 100);
    assertTrue(!publishedStepResults.isEmpty());
  }


  public void test_getResultGroupByName() {
    assertNotNull(rgm.getResultGroupByName(TEST_RESULT_GROUP_NAME));
  }


  public void test_getResultGroups() {
    final List resultGroups = rgm.getResultGroups();
    assertTrue(!resultGroups.isEmpty());
  }


  public void test_logBuildRunAction() {
    final BuildRunAction bra = new BuildRunAction();
    bra.setAction("Test action");
    bra.setBuildRunID(1);
    bra.setCode((byte)0);
    bra.setDescription("Test description");
    bra.setDate(new Date());
    bra.setUserID(1);
    rgm.logBuildRunAction(bra);
    assertTrue(bra.getID() >= 0);
  }


  public void test_savePublishedStepResult() {
    final PublishedStepResult psr = new PublishedStepResult();
    psr.setActiveBuildID(1);
    psr.setBuildName("test_cvs_build");
    psr.setBuildRunDate(new Date());
    psr.setBuildRunID(1);
    psr.setPublisherBuildRunID(1);
    psr.setBuildRunNumber(1);
    psr.setPublishDate(new Date());
    psr.setResultGroupID(0);
    psr.setStepResultID(1);
    psr.setDescription("Test description");
    rgm.save(psr);
    assertTrue(psr.getID() >= 0);
  }


  public void test_publish() {
    rgm.publish(TEST_RESULT_GROUP_ID, ConfigurationManager.getInstance().getStepResult(1));
  }


  protected void setUp() throws Exception {
    super.setUp();
    rgm = ResultGroupManager.getInstance();
  }


  public SSTestResultGroupManager(final String s) {
    super(s);
  }
}
