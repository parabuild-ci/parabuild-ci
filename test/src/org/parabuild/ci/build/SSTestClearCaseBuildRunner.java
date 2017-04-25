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

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.services.BuildStartRequest;

import java.util.List;


/**
 * Tests ClearCaseSourceControl
 */
public class SSTestClearCaseBuildRunner extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestClearCaseBuildRunner.class);

  private static final int TEST_CC_VALID_BUILD_ID = TestHelper.TEST_CLEARCASE_VALID_BUILD_ID;

  private BuildRunner buildRunner = null;
  private ConfigurationManager cm = null;


  public SSTestClearCaseBuildRunner(final String s) {
    super(s);
  }


  public void test_requestShutdown() throws Exception {
    buildRunner.requestShutdown();
    TestHelper.waitForThreadToDie(buildRunner, 5000L); // 5 secs
    assertTrue(!buildRunner.isAlive());
  }


  public void test_requestBuildStart() throws Exception {
    final RunnerStatus initialStatus = buildRunner.getStatus();
    if (log.isDebugEnabled()) log.debug("initialStatus: " + initialStatus);
    buildRunner.requestBuildStart(new BuildStartRequest());
    // starting build is not instant, so we wait for some time
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, buildRunner.getStatus());
    // finishing build is not instant, so we wait for some time
    if (log.isDebugEnabled()) log.debug("waiiting for initial status: " + initialStatus);
    TestHelper.waitForStatus(buildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);
    // make sure it's returned to the initial status
    assertEquals(initialStatus, buildRunner.getStatus());
  }


  public void test_producesBuildRunResults() throws Exception {

    // get initial state

    final int initialBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_CC_VALID_BUILD_ID);
    final RunnerStatus initialStatus = buildRunner.getStatus();

    buildRunner.requestBuildStart(new BuildStartRequest());
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, buildRunner.getStatus());
    TestHelper.waitForStatus(buildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);

    // make sure it's returned to the initial status
    assertEquals(initialStatus, buildRunner.getStatus());

    // test number there are more build runs after the build
    final int finalBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_CC_VALID_BUILD_ID);
    if (log.isDebugEnabled()) log.debug("initialBuildRunID = " + initialBuildRunID);
    if (log.isDebugEnabled()) log.debug("finalBuildRunID = " + finalBuildRunID);
    assertTrue(finalBuildRunID > initialBuildRunID);
  }


  public void test_labelsBuild() throws Exception {

    // get initial state

    final int initialBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_CC_VALID_BUILD_ID);
    final RunnerStatus initialStatus = buildRunner.getStatus();

    buildRunner.setLabelingEnabled(true);
    buildRunner.setForceLabeling(true);
    buildRunner.requestBuildStart(new BuildStartRequest());
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, buildRunner.getStatus());
    TestHelper.waitForStatus(buildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);

    // make sure it's returned to the initial status
    assertEquals(initialStatus, buildRunner.getStatus());

    // test number there are more build runs after the build
    final int finalBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_CC_VALID_BUILD_ID);
    if (log.isDebugEnabled()) log.debug("initialBuildRunID = " + initialBuildRunID);
    if (log.isDebugEnabled()) log.debug("finalBuildRunID = " + finalBuildRunID);
    assertTrue(finalBuildRunID > initialBuildRunID);
    //noinspection UNUSED_SYMBOL
    final BuildRun lastCompleteBuildRun = cm.getLastCompleteBuildRun(TEST_CC_VALID_BUILD_ID);
    // NOTE: simeshev@parabuildci.org ->
    // assertNotNull(lastCompleteBuildRun.getLabel());
  }


  public void test_producesStepRunResults() throws Exception {

    final RunnerStatus initialStatus = buildRunner.getStatus();

    buildRunner.requestBuildStart(new BuildStartRequest());
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, buildRunner.getStatus());
    TestHelper.waitForStatus(buildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);
    // make sure it's returned to the initial status
    assertEquals(initialStatus, buildRunner.getStatus());

    // test number there are more build runs after the build
    final int finalBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_CC_VALID_BUILD_ID);
    final List stepRuns = cm.getStepRuns(finalBuildRunID);
    if (log.isDebugEnabled()) log.debug("stepRuns.size() = " + stepRuns.size());
    assertTrue(!stepRuns.isEmpty());
  }


  public void test_producesStepRunLogs() throws Exception {

    final RunnerStatus initialStatus = buildRunner.getStatus();
    buildRunner.requestBuildStart(new BuildStartRequest());
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, buildRunner.getStatus());
    TestHelper.waitForStatus(buildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);
    // make sure it's returned to the initial status
    assertEquals(initialStatus, buildRunner.getStatus());

    // test number there are more build runs after the build
    final int finalBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_CC_VALID_BUILD_ID);
    final List stepsResults = cm.getStepRuns(finalBuildRunID);
    assertTrue(!stepsResults.isEmpty());
    final StepRun stepRun = (StepRun) stepsResults.get(0);
    final List stepLogs = cm.getAllStepLogs(stepRun.getID());
    assertTrue(!stepLogs.isEmpty());
  }


  protected void setUp() throws Exception {
    super.setUp();
    System.setProperty("parabuild.print.stacktrace", "true");
    cm = ConfigurationManager.getInstance();
    final BuildConfig buildConfig = cm.getBuildConfiguration(TEST_CC_VALID_BUILD_ID);
    buildRunner = BuildRunnerFactory.getBuildRunner(buildConfig.getBuildID());
    buildRunner.enableNotification(false);
    buildRunner.setLabelingEnabled(false);
    buildRunner.start();
  }


  protected void tearDown() throws Exception {
    if (buildRunner.isAlive()) buildRunner.requestShutdown();
    TestHelper.waitForThreadToDie(buildRunner, 5000L); // 5 secs
    assertTrue(!buildRunner.isAlive());
    super.tearDown();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestClearCaseBuildRunner.class, new String[]{
            "test_requestBuildStart",
            "test_producesBuildRunResults",
            "test_producesStepRunResults",
            "test_producesStepRunLogs"
    });
  }
}
