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
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.services.BuildStartRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Tests BuildRunner
 *
 * @noinspection ProhibitedExceptionDeclared
 */
public class SSTestBuildRunner extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestBuildRunner.class);

  private static final int TEST_CVS_VALID_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;

  private BuildRunner buildRunner = null;
  private ConfigurationManager configManager = null;
  private static final int TEST_BUILD_ID = TEST_CVS_VALID_BUILD_ID;


  public SSTestBuildRunner(final String s) {
    super(s);
  }


  public void test_requestShutdown() throws Exception {
    buildRunner.requestShutdown();
    TestHelper.waitForThreadToDie(buildRunner, 5000L); // 5 secs
    assertTrue("Thread should be dead", !buildRunner.isAlive());
  }


  public void test_requestBuildStart() throws Exception {
    final RunnerStatus initialStatus = buildRunner.getStatus();
    buildRunner.requestBuildStart(new BuildStartRequest());
    // starting build is not instant, so we wait for some time
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals("Expected status RunnerStatus.BUILDING", RunnerStatus.BUILDING, buildRunner.getStatus());
    // finishing build is not instant, so we wait for some time
    TestHelper.waitForStatus(buildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);
    // make sure it's returned to the initial status
    assertEquals(initialStatus, buildRunner.getStatus());
  }


  public void test_producesBuildRunResults() throws Exception {

    // get initial state

    final int initialBuildRunID = getLastCompleteBuildRunID();
    final RunnerStatus initialStatus = buildRunner.getStatus();

    buildRunner.requestBuildStart(new BuildStartRequest());
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, buildRunner.getStatus());
    TestHelper.waitForStatus(buildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);

    // make sure it's returned to the initial status
    assertEquals(initialStatus, buildRunner.getStatus());

    // test number there are more build runs after the build
    final int finalBuildRunID = getLastCompleteBuildRunID();
    if (log.isDebugEnabled()) {
      log.debug("initialBuildRunID = " + initialBuildRunID);
    }
    if (log.isDebugEnabled()) {
      log.debug("finalBuildRunID = " + finalBuildRunID);
    }
    assertTrue(finalBuildRunID > initialBuildRunID);

    // make sure that version counter incremented
    final String version = configManager.getBuildRunAttributeValue(getLastCompleteBuildRunID(), BuildRunAttribute.VERSION);
    final String versionCounter = configManager.getBuildRunAttributeValue(getLastCompleteBuildRunID(), BuildRunAttribute.VERSION_COUNTER);
    final Integer versionCounterSequence = configManager.getActiveBuildAttributeValue(TEST_BUILD_ID, ActiveBuildAttribute.VERSION_COUNTER_SEQUENCE);
    assertEquals("2.0.0.4", version);
    assertEquals("0", versionCounter);
    assertEquals("0", versionCounterSequence.toString());
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
    final int finalBuildRunID = getLastCompleteBuildRunID();
    final List stepRuns = configManager.getStepRuns(finalBuildRunID);
    if (log.isDebugEnabled()) {
      log.debug("stepRuns.size() = " + stepRuns.size());
    }
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
    final int finalBuildRunID = getLastCompleteBuildRunID();
    final List stepsResults = configManager.getStepRuns(finalBuildRunID);
    assertTrue(!stepsResults.isEmpty());
    final StepRun stepRun = (StepRun) stepsResults.get(0);
    final List stepLogs = configManager.getAllStepLogs(stepRun.getID());
    assertTrue(!stepLogs.isEmpty());
  }


  public void test_labelsBuild() throws Exception {

    // alter build script to be successful
    TestHelper.setSequenceScript(1, "echo BUILD SUCCESSFUL");
    TestHelper.setSequenceScript(2, "echo BUILD SUCCESSFUL");

    // get initial state
    final int initialBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_BUILD_ID);
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
    final int finalBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_BUILD_ID);
    if (log.isDebugEnabled()) {
      log.debug("initialBuildRunID = " + initialBuildRunID);
    }
    if (log.isDebugEnabled()) {
      log.debug("finalBuildRunID = " + finalBuildRunID);
    }
    assertTrue(finalBuildRunID > initialBuildRunID);
    assertNotNull(configManager.getLastCompleteBuildRun(TEST_BUILD_ID).getLabel());
  }


  public void test_reRunBuild() {

    final int numberOfBuildRunsBefore = configManager.getBuildRuns(TEST_BUILD_ID, Integer.MAX_VALUE).size();

    // alter build script to be successful
    final BuildSequence bseq1 = (BuildSequence) configManager.getObject(BuildSequence.class, 1);
    bseq1.setScriptText("echo BUILD SUCCESSFUL");
    configManager.save(bseq1);
    final BuildSequence bseq2 = (BuildSequence) configManager.getObject(BuildSequence.class, 2);
    bseq2.setScriptText("echo BUILD SUCCESSFUL");
    configManager.save(bseq2);

    // get initial state
    final int initialBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_BUILD_ID);
    final RunnerStatus initialStatus = buildRunner.getStatus();
    buildRunner.setLabelingEnabled(true);
    buildRunner.setForceLabeling(true);
    buildRunner.requestBuildStart(new BuildStartRequest(BuildStartRequest.REQUEST_RERUN, -1, -1, 2, new ArrayList(1), "", "", false, null, -1, Collections.EMPTY_LIST));
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(RunnerStatus.BUILDING, buildRunner.getStatus());
    TestHelper.waitForStatus(buildRunner, initialStatus, TestHelper.BUILD_FINISHED_SECONDS);

    // make sure it's returned to the initial status
    assertEquals(initialStatus, buildRunner.getStatus());

    // test number there are more build runs after the build
    final int finalBuildRunID = TestHelper.getLastCompleteBuildRunID(TEST_BUILD_ID);
    if (log.isDebugEnabled()) {
      log.debug("initialBuildRunID = " + initialBuildRunID);
    }
    if (log.isDebugEnabled()) {
      log.debug("finalBuildRunID = " + finalBuildRunID);
    }
    assertEquals(finalBuildRunID, initialBuildRunID);
    assertNull("Label should be null", configManager.getLastCompleteBuildRun(TEST_BUILD_ID).getLabel());

    final int numberOfBuildRunsAfter = configManager.getBuildRuns(TEST_BUILD_ID, Integer.MAX_VALUE).size();
    assertTrue(numberOfBuildRunsAfter > numberOfBuildRunsBefore);
  }


  /**
   * Helper method to return las complete build run
   */
  private int getLastCompleteBuildRunID() {
    return configManager.getLastCompleteBuildRun(TEST_BUILD_ID).getBuildRunID();
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    configManager = ConfigurationManager.getInstance();
    final BuildConfig buildConfig = configManager.getBuildConfiguration(TEST_BUILD_ID);
    final List hosts = AgentManager.getInstance().getLiveAgentHosts(buildConfig.getBuilderID(), true);
    for (int i = 0; i < hosts.size(); i++) {
      final AgentHost host = (AgentHost) hosts.get(i);
      final Agent agent = AgentManager.getInstance().createAgent(TEST_BUILD_ID, host);
      assertTrue("Set up should be able to empty build script dir", agent.emptyScriptDir());
    }
    buildRunner = BuildRunnerFactory.getBuildRunner(buildConfig.getBuildID());
    buildRunner.enableNotification(false);
    buildRunner.setLabelingEnabled(false);
    buildRunner.start();
  }


  protected void tearDown() throws Exception {
    // request build runner thread to stop
    if (buildRunner.isAlive()) {
      buildRunner.requestShutdown();
    }
    TestHelper.waitForThreadToDie(buildRunner, 5000L); // 5 secs
    assertTrue(!buildRunner.isAlive());
    final BuildConfig buildConfig = configManager.getBuildConfiguration(TEST_BUILD_ID);
    final List hosts = AgentManager.getInstance().getLiveAgentHosts(buildConfig.getBuilderID(), true);
    for (int i = 0; i < hosts.size(); i++) {
      final AgentHost host = (AgentHost) hosts.get(i);
      final Agent agent = AgentManager.getInstance().createAgent(TEST_BUILD_ID, host);
      assertTrue("Build script directory should be empty", agent.scriptDirIsEmpty());
    }
    super.tearDown();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestBuildRunner.class, new String[]{
            "test_producesStepRunLogs",
            "test_reRunBuild",
            "test_requestBuildStart",
            "test_producesBuildRunResults",
            "test_producesStepRunResults",
    });
  }


  public String toString() {
    return "SSTestBuildRunner{" +
            "buildRunner=" + buildRunner +
            ", configManager=" + configManager +
            "} " + super.toString();
  }
}
