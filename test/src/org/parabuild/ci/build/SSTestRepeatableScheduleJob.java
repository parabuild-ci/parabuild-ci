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

import java.text.*;
import java.util.*;
import org.apache.commons.logging.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.quartz.*;
import org.quartz.impl.calendar.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;


/**
 * Tests RepeatableScheduleJob
 */
public class SSTestRepeatableScheduleJob extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestRepeatableScheduleJob.class);

  private static final int TEST_RECURRENT_BUILD_ID = TestHelper.TEST_RECURRENT_BUILD_ID;

  private BuildRunner buildRunner = null;
  private CleanCheckoutCounter cleanCheckoutCounter = null;
  private ConfigurationManager cm = null;
  private RepeatableScheduleJob scheduleJob = null;
  private JobExecutionContext jobExecutionContext = null;
  private ErrorManager errorManager;


  public SSTestRepeatableScheduleJob(final String s) {
    super(s);
  }


  public void test_execute() throws Exception {
    // first run consumes changes in the test dataset.xml
    int initialBuildRunID = -1;
    if (cm.getLastCompleteBuildRun(TEST_RECURRENT_BUILD_ID) != null) {
      initialBuildRunID = cm.getLastCompleteBuildRun(TEST_RECURRENT_BUILD_ID).getBuildRunID();
    }
    scheduleJob.execute(jobExecutionContext);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, 60000);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.WAITING, 60000);
    assertEquals(0, errorManager.errorCount());
    final int firstBuildRunID = cm.getLastCompleteBuildRun(TEST_RECURRENT_BUILD_ID).getBuildRunID();
    assertTrue(initialBuildRunID != firstBuildRunID);

    // second request should not run as we don't have "run-if-no-changes" set
    scheduleJob.execute(jobExecutionContext);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.WAITING, 60000);
    assertEquals(0, errorManager.errorCount());
    assertEquals(firstBuildRunID, cm.getLastCompleteBuildRun(TEST_RECURRENT_BUILD_ID).getBuildRunID());

    // set schedule setting to run-if-no-changes and request to start
    jobExecutionContext.getJobDetail().getJobDataMap().put(RepeatableScheduleJob.RUN_IF_NO_CHANGES, Boolean.TRUE);
    // request to start
    scheduleJob.execute(jobExecutionContext);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, 60000);
    TestHelper.waitForStatus(buildRunner, RunnerStatus.WAITING, 60000);
    assertEquals(0, errorManager.errorCount());
    assertTrue(firstBuildRunID != cm.getLastCompleteBuildRun(TEST_RECURRENT_BUILD_ID).getBuildRunID());
  }


  public void test_executePicksNewChages() throws Exception {
    //TODO: simeshev@parabuilci.org - implement
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    cm = ConfigurationManager.getInstance();
    cleanCheckoutCounter = new CleanCheckoutCounter(TEST_RECURRENT_BUILD_ID);
    final BuildConfig buildConfig = cm.getBuildConfiguration(TEST_RECURRENT_BUILD_ID);
    buildRunner = BuildRunnerFactory.getBuildRunner(buildConfig.getBuildID());
    buildRunner.enableNotification(false);
    buildRunner.start();

    final SchedulerFactory factory = new org.quartz.impl.StdSchedulerFactory();
    final Scheduler scheduler = factory.getScheduler();

    // create job detail
    final JobDetail jobDetail = RepeatableScheduler.makeJobDetail(TEST_RECURRENT_BUILD_ID, RepeatableScheduleJob.class);
    final JobDataMap dataMap = makeJobDataMap();
    jobDetail.setJobDataMap(dataMap);
    jobDetail.setName(RepeatableScheduler.makeJobName(TEST_RECURRENT_BUILD_ID, 1));

    // create schedule trigger
    final CronTrigger trigger = makeTrigger();
    jobDetail.setName(RepeatableScheduler.makeTriggerName(TEST_RECURRENT_BUILD_ID, 1));

    // create schedule job
    scheduleJob = new RepeatableScheduleJob();

    jobExecutionContext = new JobExecutionContext(scheduler, trigger, new BaseCalendar(), jobDetail, scheduleJob, true);
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
  }


  private CronTrigger makeTrigger() throws ParseException {
    final List scheduleItems = ConfigurationManager.getInstance().getScheduleItems(TEST_RECURRENT_BUILD_ID);
    final ScheduleItem item = (ScheduleItem)scheduleItems.get(0);
    return RepeatableScheduler.makeCronTriggers(TEST_RECURRENT_BUILD_ID, item)[0];
  }


  private JobDataMap makeJobDataMap() {
    final JobDataMap dataMap;
    dataMap = new JobDataMap();
    dataMap.put(RepeatableScheduleJob.BUILD_ID_KEY, TEST_RECURRENT_BUILD_ID);
    dataMap.put(RepeatableScheduleJob.BUILD_RUNNER_KEY, buildRunner);
    dataMap.put(RepeatableScheduleJob.CLEAN_CHECKOUT_COUNTER, cleanCheckoutCounter);
    dataMap.put(RepeatableScheduleJob.FORCE_CLEAN_CHECKOUT, Boolean.FALSE);
    dataMap.put(RepeatableScheduleJob.RUN_IF_NO_CHANGES, Boolean.FALSE);
    return dataMap;
  }


  protected void tearDown() throws Exception {
    // request build runner thread to stop
    if (buildRunner.isAlive()) buildRunner.requestShutdown();
    TestHelper.waitForThreadToDie(buildRunner, 5000L); // 5 secs
    assertTrue(!buildRunner.isAlive());
    super.tearDown();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestRepeatableScheduleJob.class, new String[]{
      "test_execute"
    });
  }
}
