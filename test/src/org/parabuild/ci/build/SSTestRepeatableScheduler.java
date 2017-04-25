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
import org.apache.commons.logging.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.quartz.*;

import com.gargoylesoftware.base.testing.*;
import junitx.util.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.services.*;


/**
 * Tests RepeatableScheduleJob
 */
public class SSTestRepeatableScheduler extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestRepeatableScheduler.class);

  private static final int TEST_RECURRENT_BUILD_ID = TestHelper.TEST_RECURRENT_BUILD_ID;
  private ConfigurationManager cm = null;
  private BuildRunner buildRunner = null;
  private RepeatableScheduler scheduler = null;
  private ErrorManager errorManager = null;


  public SSTestRepeatableScheduler(final String s) {
    super(s);
  }


  /**
   * Tests that reloadSchedule does not load quarts jobs if
   * scheduler has not been started (scheduler.requestStart() has
   * not been called) yet.
   *
   * @throws Exception
   */
  public void test_reloadSchedule_bug509() throws Exception {

    // check nothing is scheduled
    assertEquals(0, getNextFireHash());

    // call reload
    scheduler.reloadSchedule();

    // check nothing has been scheduled after call to reloadSchedule();
    assertEquals(0, getNextFireHash());
  }


  public void test_reloadSchedule() throws Exception {

    // get original config hash
    scheduler.requestActivate();
    final int initialHash = getNextFireHash();

    // update schedule item
    final List items = cm.getScheduleItems(TEST_RECURRENT_BUILD_ID);
    for (Iterator iter = items.iterator(); iter.hasNext();) {
      final ScheduleItem scheduleItem = (ScheduleItem)iter.next();
      scheduleItem.setHour("5");
    }
    cm.saveScheduleItems(items);


    // get updated config hash
    scheduler.reloadSchedule();
    final int newHash = getNextFireHash();
    assertTrue(initialHash != newHash);

    scheduler.requestShutdown();
  }


  public void test_requestRunOnceOKWithIdleScheduler() throws NoSuchFieldException {
    scheduler.start();
    scheduler.requestRunOnce(new BuildStartRequest());

    TestHelper.waitForStatus(buildRunner, RunnerStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    // REVIEWME: simeshev@parabuilci.org -> breaks (P4 is not up?)
    // assertEquals(buildRunner.getStatus(), RunnerStatus.BUILDING);

    TestHelper.waitForStatus(buildRunner, RunnerStatus.WAITING, TestHelper.BUILD_FINISHED_SECONDS);
    assertEquals(buildRunner.getStatus(), RunnerStatus.WAITING);

    // shutdown and assers resulting state command
    scheduler.requestShutdown();
    assertEquals("Number of errors should be zero", 0, errorManager.errorCount());
  }


  private int getNextFireHash() throws NoSuchFieldException, SchedulerException {
    int initialHash = 0;
    final Scheduler quartzScheduler = (Scheduler)PrivateAccessor.getField(scheduler, "scheduler");
    final String[] groupNames = quartzScheduler.getJobGroupNames();
    for (int i = 0; i < groupNames.length; i++) {
      final String groupName = groupNames[i];
      final String[] jobNames = quartzScheduler.getJobNames(groupName);
      for (int j = 0; j < jobNames.length; j++) {
        final String jobName = jobNames[j];
        final Trigger[] triggers = quartzScheduler.getTriggersOfJob(jobName, groupName);
        for (int k = 0; k < triggers.length; k++) {
          final Trigger trigger = triggers[k];
          final Date d = trigger.getNextFireTime();
          initialHash ^= d.hashCode();
          if (log.isDebugEnabled()) log.debug("d = " + d);
        }
      }
    }
    return initialHash;
  }


  /**
   * Tests that repeatable scheduler can resume after if it was
   * stopped.
   * <p/>
   * Bug #583 - "Once a scheduled build stopped, clickin on
   * "Resume" does not resume the build - confirmed by our own
   * instance."
   */
  public void test_requestResume_bug583() {
    scheduler.requestActivate();
    assertEquals(SchedulerStatus.IDLE, scheduler.getStatus());

    scheduler.requestPause();
    assertEquals(SchedulerStatus.PAUSED, scheduler.getStatus());

    scheduler.requestResume();
    assertEquals(SchedulerStatus.IDLE, scheduler.getStatus());
    scheduler.requestShutdown();
  }


  /**
   * Demonstrates that scheduler factory returns the same
   * scheduler for each call.
   *
   * @throws SchedulerException
   */
  public void test_schedulerInstance() throws SchedulerException {
    final SchedulerFactory factory1 = new org.quartz.impl.StdSchedulerFactory();
    final SchedulerFactory factory2 = new org.quartz.impl.StdSchedulerFactory();
    assertEquals(factory1.getScheduler(), factory2.getScheduler());
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();

    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    cm = ConfigurationManager.getInstance();
    final BuildConfig buildConfig = cm.getBuildConfiguration(TEST_RECURRENT_BUILD_ID);
    buildRunner = BuildRunnerFactory.getBuildRunner(buildConfig.getBuildID());
    buildRunner.enableNotification(false);
    buildRunner.start();
    scheduler = new RepeatableScheduler(TEST_RECURRENT_BUILD_ID, buildRunner);
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
    return new OrderedTestSuite(SSTestRepeatableScheduler.class, new String[]{
      "test_schedulerInstance",
      "test_requestResume_bug583",
      "test_reloadSchedule_bug509",
      "test_reloadSchedule"
    });
  }
}
