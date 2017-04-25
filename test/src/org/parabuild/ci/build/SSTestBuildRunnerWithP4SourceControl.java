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
 * Tests P4SourceControl
 */
public class SSTestBuildRunnerWithP4SourceControl extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestBuildRunnerWithP4SourceControl.class);

  private static final int TEST_P4_VALID_BUILD_ID = TestHelper.TEST_P4_VALID_BUILD_ID;

  private BuildRunner p4BuildRunner = null;
  private ConfigurationManager cm = null;


  public SSTestBuildRunnerWithP4SourceControl(final String s) {
    super(s);
  }


  public void test_requestShutdown() throws Exception {
    p4BuildRunner.requestShutdown();
    TestHelper.waitForThreadToDie(p4BuildRunner, 5000L); // 5 secs
    assertTrue(!p4BuildRunner.isAlive());
  }


  public void test_requestBuildStart() throws Exception {
    final RunnerStatus initialStatus = p4BuildRunner.getStatus();
    p4BuildRunner.requestBuildStart(new BuildStartRequest());
    // starting build is not instant, so we wait for some time
    TestHelper.waitForStatus(p4BuildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, p4BuildRunner.getStatus());
    // finishing build is not instant, so we wait for some time
    TestHelper.waitForStatus(p4BuildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);
    // make sure it's returned to the initial status
    assertEquals(initialStatus, p4BuildRunner.getStatus());
  }


  public void test_producesBuildRunResults() throws Exception {

    // get initial state

    final int initialBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_P4_VALID_BUILD_ID);
    final RunnerStatus initialStatus = p4BuildRunner.getStatus();

    p4BuildRunner.requestBuildStart(new BuildStartRequest());
    TestHelper.waitForStatus(p4BuildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, p4BuildRunner.getStatus());
    TestHelper.waitForStatus(p4BuildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);

    // make sure it's returned to the initial status
    assertEquals(initialStatus, p4BuildRunner.getStatus());

    // test number there are more build runs after the build
    final int finalBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_P4_VALID_BUILD_ID);
    if (log.isDebugEnabled()) log.debug("initialBuildRunID = " + initialBuildRunID);
    if (log.isDebugEnabled()) log.debug("finalBuildRunID = " + finalBuildRunID);
    assertTrue(finalBuildRunID > initialBuildRunID);
  }


  public void test_labelsBuild() throws Exception {

    // alter build script to be successful
    TestHelper.setSequenceScript(3, "echo BUILD SUCCESSFUL");
    TestHelper.setSequenceScript(4, "echo BUILD SUCCESSFUL");

    // get initial state

    final int initialBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_P4_VALID_BUILD_ID);
    final RunnerStatus initialStatus = p4BuildRunner.getStatus();

    p4BuildRunner.setLabelingEnabled(true);
    p4BuildRunner.setForceLabeling(true);
    p4BuildRunner.requestBuildStart(new BuildStartRequest());
    TestHelper.waitForStatus(p4BuildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, p4BuildRunner.getStatus());
    TestHelper.waitForStatus(p4BuildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);

    // make sure it's returned to the initial status
    assertEquals(initialStatus, p4BuildRunner.getStatus());

    // test number there are more build runs after the build
    final int finalBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_P4_VALID_BUILD_ID);
    if (log.isDebugEnabled()) log.debug("initialBuildRunID = " + initialBuildRunID);
    if (log.isDebugEnabled()) log.debug("finalBuildRunID = " + finalBuildRunID);
    assertTrue(finalBuildRunID > initialBuildRunID);
    final BuildRun lastCompleteBuildRun = cm.getLastCompleteBuildRun(TEST_P4_VALID_BUILD_ID);
    assertNotNull(lastCompleteBuildRun.getLabel());

    // REVIEWME: simeshev@parabuilci.org -> ideally we should be able to check that the label
    // was really created in P4 depot.
  }


  public void test_producesStepRunResults() throws Exception {

    final RunnerStatus initialStatus = p4BuildRunner.getStatus();

    p4BuildRunner.requestBuildStart(new BuildStartRequest());
    TestHelper.waitForStatus(p4BuildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, p4BuildRunner.getStatus());
    TestHelper.waitForStatus(p4BuildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);
    // make sure it's returned to the initial status
    assertEquals(initialStatus, p4BuildRunner.getStatus());

    // test number there are more build runs after the build
    final int finalBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_P4_VALID_BUILD_ID);
    final List stepRuns = cm.getStepRuns(finalBuildRunID);
    if (log.isDebugEnabled()) log.debug("stepRuns.size() = " + stepRuns.size());
    assertTrue(!stepRuns.isEmpty());
  }


  public void test_producesStepRunLogs() throws Exception {

    final RunnerStatus initialStatus = p4BuildRunner.getStatus();
    p4BuildRunner.requestBuildStart(new BuildStartRequest());
    TestHelper.waitForStatus(p4BuildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, p4BuildRunner.getStatus());
    TestHelper.waitForStatus(p4BuildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);
    // make sure it's returned to the initial status
    assertEquals(initialStatus, p4BuildRunner.getStatus());

    // test number there are more build runs after the build
    final int finalBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_P4_VALID_BUILD_ID);
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
    final BuildConfig buildConfig = cm.getBuildConfiguration(TEST_P4_VALID_BUILD_ID);
    p4BuildRunner = BuildRunnerFactory.getBuildRunner(buildConfig.getBuildID());
    p4BuildRunner.enableNotification(false);
    p4BuildRunner.setLabelingEnabled(false);
    p4BuildRunner.start();
  }


  protected void tearDown() throws Exception {
    if (p4BuildRunner.isAlive()) p4BuildRunner.requestShutdown();
    TestHelper.waitForThreadToDie(p4BuildRunner, 5000L); // 5 secs
    assertTrue(!p4BuildRunner.isAlive());
    super.tearDown();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestBuildRunnerWithP4SourceControl.class, new String[]{
            "test_requestBuildStart",
            "test_producesBuildRunResults",
            "test_producesStepRunResults",
            "test_producesStepRunLogs"
    });
  }
}
