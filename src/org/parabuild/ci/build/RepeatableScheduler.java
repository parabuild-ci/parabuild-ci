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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ScheduleItem;
import org.parabuild.ci.services.BuildServiceImpl;
import org.parabuild.ci.services.BuildStartRequest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This class is respondible for running scheduled-style builds.
 *
 * @see BuildScheduler
 * @see BuildServiceImpl
 * @see BuildRunner
 */
public final class RepeatableScheduler implements BuildScheduler {

  private static final String NAME_PREFIX_BUILD = "BUILD";
  private static final String NAME_PREFIX_JOB = "JOB";
  private static final String NAME_PREFIX_GROUP = "GROUP";
  private static final String NAME_PREFIX_TRIGGER = "TRIGGER";

  private static final Log log = LogFactory.getLog(RepeatableScheduler.class);

  private boolean startedUp = false;
  private int activeBuildID = BuildConfig.UNSAVED_ID;
  private BuildRunner buildRunner = null;
  private Scheduler scheduler = null;
  private final List triggerKeyList = new ArrayList(11);
  private SchedulerStatus schedulerStatus = SchedulerStatus.IDLE;
  private final CleanCheckoutCounter cleanCheckoutCounter;


  public RepeatableScheduler(final int activeBuildID, final BuildRunner buildRunner) {
    // repeatable scheduler always runs from current build config.
    ConfigurationManager.getInstance().validateIsActiveBuildID(activeBuildID);
    this.activeBuildID = activeBuildID;
    this.buildRunner = buildRunner;
    this.cleanCheckoutCounter = new CleanCheckoutCounter(activeBuildID);
    try {
      if (log.isDebugEnabled()) log.debug("Creating scheduler for buildID: " + activeBuildID);
      scheduler = new StdSchedulerFactory().getScheduler();
    } catch (final Exception e) {
      reportInitializationError(e);
    } catch (final java.lang.Error e) {
      reportInitializationError(e);
      throw e;
    }
  }


  private void scheduleJobs() throws ParseException, SchedulerException {
    if (log.isDebugEnabled()) log.debug("Creating jobs for buildID: " + activeBuildID);
    if (buildRunner == null) throw new SchedulerException("Build runner for scheduler is undefined");
    if (!triggerKeyList.isEmpty()) throw new SchedulerException("Jobs should be unscheduled before scheduling jobs.");

    // create schedule triggers for this build
    if (log.isDebugEnabled()) log.debug("Creating schedule triggers for buildID: " + activeBuildID);
    int nameIndex = 0;

    final List itemList = ConfigurationManager.getInstance().getScheduleItems(activeBuildID);
    for (final Iterator iter = itemList.iterator(); iter.hasNext();) {

      // create trigger
      final ScheduleItem scheduleItem = (ScheduleItem) iter.next();
      final CronTrigger[] triggers = makeCronTriggers(activeBuildID, scheduleItem);

      // go through the list of triggers
      for (int i = 0; i < triggers.length; i++) {

        final CronTrigger trigger = triggers[i];
        trigger.setName(makeTriggerName(nameIndex));
        final TriggerKey key = new TriggerKey(trigger.getName(), trigger.getGroup());
        triggerKeyList.add(key);

        // make job detail
        final JobDetail jobDetail = makeJobDetail(activeBuildID, RepeatableScheduleJob.class);
        jobDetail.setName(makeJobName(nameIndex));

        // set build parameters
        final JobDataMap dataMap = new JobDataMap();
        dataMap.put(RepeatableScheduleJob.BUILD_ID_KEY, activeBuildID);
        dataMap.put(RepeatableScheduleJob.BUILD_RUNNER_KEY, buildRunner);
        dataMap.put(RepeatableScheduleJob.CLEAN_CHECKOUT_COUNTER, cleanCheckoutCounter);
        dataMap.put(RepeatableScheduleJob.FORCE_CLEAN_CHECKOUT, Boolean.valueOf(scheduleItem.isCleanCheckout()));
        dataMap.put(RepeatableScheduleJob.RUN_IF_NO_CHANGES, Boolean.valueOf(scheduleItem.isRunIfNoChanges()));
        jobDetail.setJobDataMap(dataMap);

        // schedule
        if (log.isDebugEnabled()) log.debug("Scheduling job for buildID: " + activeBuildID);
        scheduler.scheduleJob(jobDetail, trigger);
        nameIndex++;
      }
    }
  }


  /**
   * Enables build scheduler. This method is made synchronized
   * because request to start scheduler can be sent only once.
   */
  public synchronized void requestActivate() {
    try {
      if (startedUp) return;
      if (log.isDebugEnabled()) log.debug("====== REQUESTING SCHEDULER TO START === ");
      schedulerStatus = SchedulerStatus.IDLE;
      scheduleJobs();
      startedUp = true;
    } catch (final Exception e) {
      reportError(e, "starting");
    } catch (final java.lang.Error e) {
      reportError(e, "starting");
      throw e;
    }
  }


  /**
   * Requests to run a build once.
   * <p/>
   * As all the jobs are the same for the given schdeuler, this
   * method just picks up the first job in the scheduler an runs
   * it.
   */
  public void requestRunOnce(final BuildStartRequest startRequest) {
    try {
      // NOTE: vimeshev - We can do it this simple way because there
      // is no a version control updating the source line in
      // background.

      // start the build
      if (log.isDebugEnabled()) log.debug("build runner status: " + buildRunner.getStatus());
      if (!buildRunner.getStatus().equals(RunnerStatus.WAITING)) return;
      if (log.isDebugEnabled()) log.debug("requesting scheduled build to start");
      startRequest.setCleanCheckout(cleanCheckoutCounter.increment());
      buildRunner.requestBuildStart(startRequest);
    } catch (final Exception e) {
      reportError(e, "starting a scheduled build by admin request");
    } catch (final java.lang.Error e) {
      reportError(e, "starting a scheduled build by admin request");
      throw e;
    }
  }


  /**
   * @return Current scheduler status
   */
  public SchedulerStatus getStatus() {
    return schedulerStatus;
  }


  /**
   * Request build to pause until further notification request
   * to activate.
   */
  public synchronized void requestPause() {
    try {
      pauseJobs();
      schedulerStatus = SchedulerStatus.PAUSED;
    } catch (final Exception e) {
      reportError(e, "pausing a scheduled build by admin request");
    } catch (final java.lang.Error e) {
      reportError(e, "pausing a scheduled build by admin request");
      throw e;
    }
  }


  /**
   * Request scheduler to resume.
   *
   * @see #requestPause
   */
  public synchronized void requestResume() {
    try {
      if (schedulerStatus.equals(SchedulerStatus.PAUSED)) {
        resumeJobs();
        schedulerStatus = SchedulerStatus.IDLE;
      }
    } catch (final Exception e) {
      reportError(e, "resuming a scheduled build by admin request");
    } catch (final java.lang.Error e) {
      reportError(e, "resuming a scheduled build by admin request");
      throw e;
    }
  }


  /**
   * Requests that next checkout runs cleanly.
   */
  public void requestCleanCheckout() {
    cleanCheckoutCounter.forceNextCheckoutClean();
  }


  /**
   * @return time when next build will run or null if there is no
   *         information.
   */
  public Date nextBuildTime() {
    try {

      if (schedulerStatus.equals(SchedulerStatus.PAUSED)) {
        return null;
      }

      // go through the list of this build's triggers and find the
      // closest fire time
      Date nextBuildTime = null;
      for (final Iterator i = triggerKeyList.iterator(); i.hasNext();) {

        final TriggerKey key = (TriggerKey) i.next();
        final Date nextFireTime = scheduler.getTrigger(key.getName(), key.getGroup()).getNextFireTime();
        if (nextBuildTime == null) {

          nextBuildTime = (Date) nextFireTime.clone();
        } else if (nextFireTime.compareTo(nextBuildTime) < 0) {

          nextBuildTime = (Date) nextFireTime.clone();
        }
      }

      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final boolean skipNextBuild = cm.getActiveBuildAttributeValue(activeBuildID, ActiveBuildAttribute.SKIP_NEXT_SCHEDULED_BUILD, ActiveBuildAttribute.OPTION_UNCHECKED).equalsIgnoreCase(ActiveBuildAttribute.OPTION_CHECKED);
      if (skipNextBuild) {

        Date nextNextBuildTime = null;
        for (final Iterator i = triggerKeyList.iterator(); i.hasNext();) {

          final TriggerKey key = (TriggerKey) i.next();
          final Date nextFireTime = scheduler.getTrigger(key.getName(), key.getGroup()).getFireTimeAfter(nextBuildTime);

          if (nextFireTime == null) {
            continue;
          }

          if (nextNextBuildTime == null) {

            nextNextBuildTime = (Date) nextFireTime.clone();
          } else if (nextFireTime.compareTo(nextNextBuildTime) < 0) {

            nextNextBuildTime = (Date) nextFireTime.clone();
          }
        }

        return nextNextBuildTime;
      }

      return nextBuildTime;
    } catch (final Exception e) {
      reportError(e, "computing next build time");
    } catch (final java.lang.Error e) {
      reportError(e, "computing next build time");
      throw e;
    }
    return null;
  }


  /**
   * Accociates build runner with this scheduler
   *
   * @param buildRunner
   */
  public void setBuildRunner(final BuildRunner buildRunner) {
    this.buildRunner = buildRunner;
  }


  /**
   * Method to start thread implementing this interface
   */
  public void start() {
    // do nothing. this scheduler uses internal thread
    // which is nadled in requestStart
  }


  /**
   * Requests scheduler to die
   */
  public void requestShutdown() {
    try {
      if (log.isDebugEnabled()) log.debug("Shutting down repeatable scheduler");
      unscheduleJobs();
    } catch (final Exception e) {
      reportError(e, "shutting down");
    } catch (final java.lang.Error e) {
      reportError(e, "shutting down");
      throw e;
    }
  }


  /**
   * Requests this schedule to relod it's configuration. This
   * method is used to notify a scheduler about changes in
   * persistant configuration.
   */
  public synchronized void reloadSchedule() {
    try {
      // check if we are up - reload makes sense only if scheduler
      // already started/activated.
      if (!startedUp) return;

      try {
        // prevent scheduler from doing *anything*
        scheduler.pause();

        // process request
        pauseJobs();
        unscheduleJobs();
        scheduleJobs();

        // handle state
        if (log.isDebugEnabled()) log.debug("schedulerStatus: " + schedulerStatus);
        if (log.isDebugEnabled()) log.debug("isInactiveAtStartup(): " + isInactiveAtStartup());
        if (SchedulerStatus.PAUSED.equals(schedulerStatus) || isInactiveAtStartup()) {
          if (log.isDebugEnabled()) log.debug("pausing jobs");
          schedulerStatus = SchedulerStatus.PAUSED;
          pauseJobs();
        }

      } finally {
        // re-active scheduler
        scheduler.start();
      }
    } catch (final Exception e) {
      reportError(e, "reloading");
    } catch (final java.lang.Error e) {
      reportError(e, "reloading");
      throw e;
    }
  }


  private boolean isInactiveAtStartup() {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ActiveBuild activeBuild = cm.getActiveBuild(activeBuildID);
    return activeBuild.getStartupStatus() == BuildStatus.INACTIVE_VALUE;
  }


  private void unscheduleJobs() throws SchedulerException {
    for (final Iterator i = triggerKeyList.iterator(); i.hasNext();) {
      final TriggerKey key = (TriggerKey) i.next();
      //if (log.isDebugEnabled()) log.debug("unscheduling: " + key.toString());
      scheduler.unscheduleJob(key.getName(), key.getGroup());
      i.remove();
    }
  }


  private void pauseJobs() throws SchedulerException {
    for (final Iterator i = triggerKeyList.iterator(); i.hasNext();) {
      final TriggerKey key = (TriggerKey) i.next();
      //if (log.isDebugEnabled()) log.debug("pausing: " + key.toString());
      scheduler.pauseTrigger(key.getName(), key.getGroup());
    }
  }


  private void resumeJobs() throws SchedulerException {
    for (final Iterator i = triggerKeyList.iterator(); i.hasNext();) {
      final TriggerKey key = (TriggerKey) i.next();
      //if (log.isDebugEnabled()) log.debug("resuming: " + key.toString());
      scheduler.resumeTrigger(key.getName(), key.getGroup());
    }
  }


  /**
   * Helper method to create job detail
   */
  public static JobDetail makeJobDetail(final int buildID, final Class jobClass) {
    final JobDetail jobDetail = new JobDetail();
    jobDetail.setJobClass(jobClass);
    jobDetail.setGroup(makeGroupName(buildID));
    jobDetail.setRequestsRecovery(false);
    jobDetail.setDurability(false);
    jobDetail.setVolatility(true);
    return jobDetail;
  }


  /**
   * Helper method to create cron trigger
   */
  public static CronTrigger[] makeCronTriggers(final int buildID, final ScheduleItem item) throws ParseException {
    final String[] cronExpressions = ScheduleItem.toString(item);
    final CronTrigger[] result = new CronTrigger[cronExpressions.length];
    for (int i = 0; i < cronExpressions.length; i++) {
      final CronTrigger trigger = new CronTrigger();
      trigger.setCronExpression(cronExpressions[i]);
      trigger.setGroup(makeGroupName(buildID));
      trigger.setStartTime(new Date());
      result[i] = trigger;
    }
    return result;
  }


  /**
   * Helper method to create job name.
   * <p/>
   * Job name is composed from build id and index
   */
  public static String makeJobName(final int buildID, final int index) {
    return NAME_PREFIX_BUILD + '_' + buildID + '_' + NAME_PREFIX_JOB + '_' + index;
  }


  /**
   * Helper method to create job name.
   * <p/>
   * Job name is composed from build id and index
   */
  private String makeJobName(final int index) {
    return makeJobName(activeBuildID, index);
  }


  /**
   * Helper method to create trigger name.
   * <p/>
   * Trigger name is composed from build id and index
   */
  public static String makeTriggerName(final int buildID, final int index) {
    return NAME_PREFIX_BUILD + '_' + buildID + '_' + NAME_PREFIX_TRIGGER + '_' + index;
  }


  /**
   * Helper method to create trigger name.
   * <p/>
   * Trigger name is composed from build id and index
   */
  private String makeTriggerName(final int index) {
    return makeTriggerName(activeBuildID, index);
  }


  /**
   * Helper method to create group name.
   * <p/>
   * Group name is composed from build id
   */
  public static String makeGroupName(final int buildID) {
    return NAME_PREFIX_BUILD + '_' + buildID + '_' + NAME_PREFIX_GROUP;
  }


  private void reportInitializationError(final Throwable e) {
    reportError(e, "initializing");
  }


  private void reportError(final Throwable e, final String whileString) {
    final Error error = new Error("Unexpected scheduler error while " + whileString + " repeatable scheduler: " + StringUtils.toString(e));
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_SCHEDULING);
    error.setErrorLevel(Error.ERROR_LEVEL_ERROR);
    error.setDetails(e);
    error.setBuildID(activeBuildID);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  /**
   * A composite value object to hold scheduler's trugger data.
   */
  private static final class TriggerKey {


    private final String group;
    private final String name;


    /**
     * Constructor
     *
     * @param name  String trigger name
     * @param group String trigger group
     */
    TriggerKey(final String name, final String group) {
      ArgumentValidator.validateArgumentNotBlank(name, "name");
      ArgumentValidator.validateArgumentNotBlank(group, "group");
      this.group = group;
      this.name = name;
    }


    /**
     * @return trigger group
     */
    public String getGroup() {
      return group;
    }


    /**
     * @return trigger name
     */
    public String getName() {
      return name;
    }


    public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof TriggerKey)) return false;

      final TriggerKey triggerKey = (TriggerKey) o;

      if (!group.equals(triggerKey.group)) return false;
      return name.equals(triggerKey.name);
    }


    public int hashCode() {
      int result = group.hashCode();
      result = 29 * result + name.hashCode();
      return result;
    }


    public String toString() {
      return "TriggerKey{" +
              "group='" + group + '\'' +
              ", name='" + name + '\'' +
              '}';
    }
  }


  public String toString() {
    return "RepeatableScheduler{" +
            "startedUp=" + startedUp +
            ", activeBuildID=" + activeBuildID +
            ", buildRunner=" + buildRunner +
            ", scheduler=" + scheduler +
            ", triggerKeyList=" + triggerKeyList +
            ", schedulerStatus=" + schedulerStatus +
            ", cleanCheckoutCounter=" + cleanCheckoutCounter +
            '}';
  }
}
