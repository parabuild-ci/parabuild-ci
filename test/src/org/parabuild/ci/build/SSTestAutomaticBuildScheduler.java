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

import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import junitx.util.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.versioncontrol.*;
import org.parabuild.ci.services.*;


/**
 * Tests AutomaticBuildScheduler
 */
public class SSTestAutomaticBuildScheduler extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestAutomaticBuildScheduler.class);

  private static final int TEST_VALID_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  private static final String FIELD_RUN_BUILD_ONCE_REQUEST = "runOnceRequest";
  private static final String FIELD_RUN_CONTINUOSLY = "runContinuosly";
  private static final String FIELD_FIRST_TIME = "firstTimeCycle";
  private static final String FIELD_SHUTDOWN = "shutdown";

  private BuildRunner buildRunner = null;
  private ConfigurationManager configManager = null;
  private AutomaticScheduler scheduler = null;


  public SSTestAutomaticBuildScheduler(final String s) {
    super(s);
  }


  public void test_requestStart() throws NoSuchFieldException {
    log.debug("start thread and assert initial state command");
    scheduler.start();
    assertEquals(PrivateAccessor.getField(scheduler, FIELD_RUN_CONTINUOSLY), Boolean.FALSE);
    assertNull(PrivateAccessor.getField(scheduler, FIELD_RUN_BUILD_ONCE_REQUEST));
    assertEquals(PrivateAccessor.getField(scheduler, FIELD_FIRST_TIME), Boolean.TRUE);

    log.debug("request activate");
    scheduler.requestActivate();
    assertEquals(PrivateAccessor.getField(scheduler, FIELD_RUN_CONTINUOSLY), Boolean.TRUE);

    log.debug("shutdown");
    scheduler.requestShutdown();
  }


  public void test_requestShutdown() throws NoSuchFieldException {
    scheduler.start();
    scheduler.requestActivate();

    // shutdown and assers resulting state command
    scheduler.requestShutdown();
    assertEquals(PrivateAccessor.getField(scheduler, FIELD_RUN_CONTINUOSLY), Boolean.FALSE);
    assertEquals(PrivateAccessor.getField(scheduler, FIELD_SHUTDOWN), Boolean.TRUE);
  }


  public void test_requestRunOnceOKWithIdleScheduler() throws NoSuchFieldException {
    scheduler.start();
    scheduler.requestRunOnce(new BuildStartRequest());

    log.debug("check initial status");
    assertEquals(PrivateAccessor.getField(scheduler, FIELD_RUN_CONTINUOSLY), Boolean.FALSE);
    assertNotNull(PrivateAccessor.getField(scheduler, FIELD_RUN_BUILD_ONCE_REQUEST));

    log.debug("check final status");
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.WAITING, TestHelper.BUILD_FINISHED_SECONDS);
    assertEquals(PrivateAccessor.getField(scheduler, FIELD_RUN_CONTINUOSLY), Boolean.FALSE);
    assertNull(PrivateAccessor.getField(scheduler, FIELD_RUN_BUILD_ONCE_REQUEST));

    log.debug("shutdown and assers resulting state command");
    scheduler.requestShutdown();
    assertNull(PrivateAccessor.getField(scheduler, FIELD_RUN_BUILD_ONCE_REQUEST));
  }


  public void test_requestRunOnceOKWithSerializedBuilds() throws NoSuchFieldException {
    SystemConfigurationManagerFactory.getManager().saveSystemProperty(new SystemProperty(SystemProperty.SERIALIZE_BUILDS, SystemProperty.OPTION_CHECKED));
    scheduler.start();
    while(!scheduler.isAlive()) {}
    if (log.isDebugEnabled()) log.debug("scheduler: " + scheduler);
    scheduler.requestRunOnce(new BuildStartRequest());

    log.debug("check initial status");
    assertEquals(PrivateAccessor.getField(scheduler, FIELD_RUN_CONTINUOSLY), Boolean.FALSE);
    assertNotNull(PrivateAccessor.getField(scheduler, FIELD_RUN_BUILD_ONCE_REQUEST));

    log.debug("check final status");
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.WAITING, TestHelper.BUILD_FINISHED_SECONDS);
    assertEquals(PrivateAccessor.getField(scheduler, FIELD_RUN_CONTINUOSLY), Boolean.FALSE);
    assertNull(PrivateAccessor.getField(scheduler, FIELD_RUN_BUILD_ONCE_REQUEST));

    log.debug("shutdown and assers resulting state command");
    scheduler.requestShutdown();
    assertNull(PrivateAccessor.getField(scheduler, FIELD_RUN_BUILD_ONCE_REQUEST));
  }


  /**
   * Tests that build run does clean check out if last build was
   * broken.
   */
  public void test_doesCleanCheckoutIfLasBuildWasBroken() throws NoSuchFieldException {
    // make sure we are set up OK - las build was not successful
    final BuildRunnerState buildRunnerState = buildRunner.getRunnerState();
    BuildRun lastCompleteBuildRun = buildRunnerState.getLastCompleteBuildRun();
    assertTrue(lastCompleteBuildRun.completed());
    assertTrue(lastCompleteBuildRun.getResultID() != BuildRun.BUILD_RESULT_SUCCESS);

    // modify settings
    alterScheduleSetting(ScheduleProperty.AUTO_CLEAN_CHECKOUT_IF_BROKEN, ScheduleProperty.OPTION_CHECKED);

    // start
    scheduler.start();
    scheduler.requestRunOnce(new BuildStartRequest());
    if (log.isDebugEnabled()) log.debug("scheduler: " + scheduler);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.WAITING, TestHelper.BUILD_FINISHED_SECONDS);
    scheduler.requestShutdown();

    // check if there is build run attr telling that we were running clean
    lastCompleteBuildRun = buildRunnerState.getLastCompleteBuildRun();
    final BuildRunAttribute buildRunAttribute = configManager.getBuildRunAttribute(lastCompleteBuildRun.getBuildRunID(),
      BuildRunAttribute.ATTR_CLEAN_CHECKOUT);
    assertNotNull(buildRunAttribute);
    assertEquals(buildRunAttribute.getValue(), Boolean.TRUE.toString());
  }


  public void test_requestRunOnceOKWithActiveScheduler() throws NoSuchFieldException {
    scheduler.start();

    log.debug("start and wait for build");
    scheduler.requestActivate();
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.WAITING, TestHelper.BUILD_FINISHED_SECONDS);
    assertNull(PrivateAccessor.getField(scheduler, FIELD_RUN_BUILD_ONCE_REQUEST));
    assertEquals(PrivateAccessor.getField(scheduler, FIELD_RUN_CONTINUOSLY), Boolean.TRUE);

    log.debug("request run once");
    scheduler.requestRunOnce(new BuildStartRequest());

    log.debug("check final status");
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.WAITING, TestHelper.BUILD_FINISHED_SECONDS);
    assertNull(PrivateAccessor.getField(scheduler, FIELD_RUN_BUILD_ONCE_REQUEST));

    log.debug("shutdown and assers resulting state command");
    scheduler.requestShutdown();
    assertNull(PrivateAccessor.getField(scheduler, FIELD_RUN_BUILD_ONCE_REQUEST));
  }


  protected void setUp() throws Exception {
    if (log.isDebugEnabled()) log.debug("begin SSTestAutomaticBuildScheduler set up");
    super.setUp();
    super.enableErrorManagerStackTraces();

    configManager = ConfigurationManager.getInstance();

    //
    final BuildConfig buildConfig = configManager.getBuildConfiguration(TEST_VALID_BUILD_ID);
    if (log.isDebugEnabled()) log.debug("creating build runner for buildConfig: " + buildConfig);
    buildRunner = BuildRunnerFactory.getBuildRunner(buildConfig.getBuildID());
    buildRunner.enableNotification(false);
    buildRunner.start();

    // create scheduler
    if (log.isDebugEnabled()) log.debug("creating source control");
    final SourceControl sourceControl = VersionControlFactory.makeVersionControl(configManager.getActiveBuildConfig(TEST_VALID_BUILD_ID));
    scheduler = new AutomaticScheduler(TEST_VALID_BUILD_ID, sourceControl, buildRunner, true);
    if (log.isDebugEnabled()) log.debug("end SSTestAutomaticBuildScheduler set up");
  }


  private void alterScheduleSetting(final String name, final String value) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // NOTE: simeshev@parabuilci.org - sometimes it can not find the
        // setting inserted as a part of test dataset, no idea why. May
        // be it is a timing issue? So, we use the wait cycle.
        ScheduleProperty property = null;
        int attemptCount = 0;
        while (property == null && attemptCount < 1000) {
          property = configManager.getScheduleSetting(TestHelper.TEST_CVS_VALID_BUILD_ID, name);
          Thread.sleep(100);
          attemptCount++;
        }
        // create new if needed
        if (property == null) {
          if (log.isDebugEnabled()) log.debug("property " + name + " was null");
          property = new ScheduleProperty();
          property.setBuildID(TEST_VALID_BUILD_ID);
          property.setPropertyName(name);
        }
        property.setPropertyValue(value);
        configManager.saveObject(property);
        return null;
      }
    });
  }


  protected void tearDown() throws Exception {
    log.debug("begin SSTestAutomaticBuildScheduler tearDown");

    log.debug("waiting for scheduler to die");
    TestHelper.waitForThreadToDie(scheduler, 30000L); // 30 secs
    assertTrue(!scheduler.isAlive());

    log.debug("waiting for build runner to die");
    buildRunner.requestShutdown(); // started at startUp
    TestHelper.waitForThreadToDie(buildRunner, 30000L); // 5 secs
    assertTrue(!buildRunner.isAlive());

    super.tearDown();
    log.debug("end SSTestAutomaticBuildScheduler tearDown");
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestAutomaticBuildScheduler.class, new String[]{
      "test_doesCleanCheckoutIfLasBuildWasBroken",
      "test_requestRunOnceOKWithActiveScheduler",
      "test_requestStart",
      "test_requestShutdown",
    });
  }
}
