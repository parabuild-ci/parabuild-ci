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
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ThreadUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.versioncontrol.SourceControl;

import java.util.Date;
import java.util.List;

/**
 * Scheduler for automatic builds
 *
 * @noinspection ClassHasNoToStringMethod, OverlyComplexBooleanExpression, ClassExplicitlyExtendsThread @see BuildScheduler
 */
public final class AutomaticScheduler extends Thread implements BuildScheduler {

  private static final Log LOG = LogFactory.getLog(AutomaticScheduler.class);  // NOPMD

  private static final int DEFAULT_POLL_INTERVAL = 60;
  private static final int DEFAULT_ERROR_PAUSE = 5;

  private final ConfigurationManager cm = ConfigurationManager.getInstance();

  private final int activeBuildID;
  private final BuildRunner buildRunner;
  private final SourceControl sourceControl;
  private final Object lock = new Object();
  private final CleanCheckoutCounter cleanCheckoutCounter;

  private volatile boolean automaticSchedule;
  private volatile boolean firstTimeCycle = true; // NOPMD SingularField
  private volatile boolean paused = false;
  private volatile boolean runContinuously = false;
  private volatile boolean shutdown = false;

  private volatile SchedulerStatus currentStatus = SchedulerStatus.IDLE;
  private volatile BuildStartRequest runOnceRequest = null;


  private int lastFoundChangeListID = ChangeList.UNSAVED_ID;


  /**
   * Constructor
   *
   * @param activeBuildID     for which the scheduler is created
   * @param sourceControl     a source control for this build.
   * @param buildRunner       a build runner.
   * @param automaticSchedule true if this is a automatic schedule. false if this is a manual schedule.
   */
  public AutomaticScheduler(final int activeBuildID, final SourceControl sourceControl, final BuildRunner buildRunner, final boolean automaticSchedule) {
    super((automaticSchedule ? "AutomaticScheduler:" : "ManualScheduler:") + activeBuildID);
    super.setDaemon(true);
    this.activeBuildID = activeBuildID;
    this.sourceControl = sourceControl;
    this.buildRunner = buildRunner;
    this.cleanCheckoutCounter = new CleanCheckoutCounter(activeBuildID);
    this.automaticSchedule = automaticSchedule;
    if (ConfigurationManager.validateActiveID) {
      cm.validateIsActiveBuildID(activeBuildID);
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Created automatic scheduler");
    }
  }


  private static List parseParameters(final PriorityMarkerParser priorityMarkerParser, final ChangeList changeList) {
    final String changeListDescription = changeList.getDescription();
    return priorityMarkerParser.parseChangeListDescription(changeListDescription);
  }


  /**
   * Runs single scheduler cycle
   */
  private void runSchedulerCycle() {
    //if (log.isDebugEnabled()) log.debug("Run scheduler cycle");
    //if (log.isDebugEnabled()) log.debug("Run build once: " + runOnce);
    //if (log.isDebugEnabled()) log.debug("Runner status: " + buildRunner.getStatus());

    // Create local reference copy.
    final BuildStartRequest localRunOnceRequest = runOnceRequest;

    try {

      // if build runner is ready - try to run it
      if (buildRunner.getStatus().equals(RunnerStatus.WAITING)) {

        //
        final BuildManager buildManager = BuildManager.getInstance();
        final List freeAgentHosts = buildManager.getFreeAgentHosts(activeBuildID);
        final AgentHost preferredAgentHost = getPreferredAgentHost();

        // VCS agent host is an agent host that is used to find changes.
        final AgentHost vcsAgentHost = AgentManager.getInstance().getNextLiveAgentHost(activeBuildID, freeAgentHosts, preferredAgentHost);

        // Remember last agent host
        saveLastAgentHost(vcsAgentHost);

        // Reload SCM configuration at the beginning of the cycle
        sourceControl.setAgentHost(vcsAgentHost);
        sourceControl.reloadConfiguration();

        // get changes
        if (LOG.isDebugEnabled()) {
          LOG.debug("getting changes since: " + lastFoundChangeListID);
        }
        if (firstTimeCycle) {
          currentStatus = SchedulerStatus.CHECKING_OUT;
          firstTimeCycle = false;
        } else {
          currentStatus = SchedulerStatus.GETTING_CHANGES;
        }
        ThreadUtils.checkIfInterrupted();

        // REVIEWME: vimeshev - 12/11/2004 - we should consider making
        // SourceControl "stop aware" the same way as BuildRunner.
        // Otherwise even if we stop the SCM commands, we can issue a
        // request to stop a command hard between calls to commands
        // thus not stopping processing.
        //noinspection UnusedAssignment
        int currentChangeListID = lastFoundChangeListID;
        if (automaticSchedule) {
          // this is an automatic scheduler, check since latest found.
          currentChangeListID = sourceControl.getChangesSince(lastFoundChangeListID);
        } else {
          // this is a manual scheduler
          if (localRunOnceRequest == null) {
            currentChangeListID = sourceControl.getChangesSince(ChangeList.UNSAVED_ID);
          } else {
            if (localRunOnceRequest.isParallel()) {
              // parallel requests receive change lists via the request
              currentChangeListID = localRunOnceRequest.changeListID();
              if (LOG.isDebugEnabled()) {
                LOG.debug("parallel currentChangeListID: " + currentChangeListID);
              }
            } else {
              if (localRunOnceRequest.hasNativeChangeListNumber()) {
                currentChangeListID = sourceControl.getNativeChangeList(localRunOnceRequest.getNativeChangeListNumber());
              } else {
                if (localRunOnceRequest.isChangeListRedetectionRequired()) {
                  currentChangeListID = ChangeList.UNSAVED_ID;
                } else {
                  currentChangeListID = sourceControl.getChangesSince(ChangeList.UNSAVED_ID);
                }
              }
            }
          }
        }

        // run build if necessary
        final boolean buildShouldBeSerialized = isShouldBeSerialized();
        boolean thereAreNewChanges = currentChangeListID != lastFoundChangeListID;
        if (!thereAreNewChanges && localRunOnceRequest == null && !isRunIfNoChanges()) {
          // first check if this is serialized build
          if (!buildShouldBeSerialized) {
            return; // no new changes
          }

          // it is serialized. it is possible that we
          // previously skipped build run due to a lock.
          // check if there are changes pending
          final Integer latestPendingChangeListID = cm.getLatestPendingChangeListID(activeBuildID);
          if (latestPendingChangeListID == null) {
            return; // nothing is pending
          }

          // there are some pending. mark as there are new
          // changes, currentChangeListID is (should be)
          // pointing to the latestFound (and pending)
          thereAreNewChanges = true;
        }

        if (localRunOnceRequest == null && thereAreNewChanges) { // double check just in case something changes upstream
          // Double check just in case something changes upstream
          final CooldownWaiter cooldownWaiter = new CooldownWaiter(lock, activeBuildID, sourceControl, currentChangeListID);
          currentChangeListID = cooldownWaiter.waitUntilCoolsDown();
        }


        lastFoundChangeListID = currentChangeListID;

        //
        // Calculate the change list to run the build against
        //
        final PriorityMarkerParser priorityMarkerParser = new PriorityMarkerParser();
        final List pendingChangeLists = cm.getPendingChangeLists(activeBuildID);
        int startChangeListID = currentChangeListID;
        List parameters = null;

        // REVIEWME: simeshev@parabuildci.org - 2017-11-20 - is it possible at all that the pending change list is empty?
        if (isBuildChangesOneByOne() && !pendingChangeLists.isEmpty()) {

          // There is a change list to pick from the pending change lists
          final ChangeList changeList = (ChangeList) pendingChangeLists.get(0);
          parameters = parseParameters(priorityMarkerParser, changeList);
          startChangeListID = changeList.getChangeListID();

        } else {

          // Find a priority change list if any
          for (int i = 0; i < pendingChangeLists.size(); i++) {

            final ChangeList changeList = (ChangeList) pendingChangeLists.get(i);
            parameters = parseParameters(priorityMarkerParser, changeList);
            startChangeListID = changeList.getChangeListID();

            if (parameters != null) {

              // Found a priority change list
              break;
            }
          }
        }


        // set status to getting changes so that it does not
        // appear that it is checking out forever if the
        // serialization lock was invoked.
        currentStatus = SchedulerStatus.IDLE;

        // check if we have to wait for other builds

        // NOTE: vimeshev - 2007-04-14 - we try to serialize
        // only builds started automatically. Such builds do
        // not have runOnceRequest. See bug # 1127 for details.
        final boolean serializationShouldBeIgnored = localRunOnceRequest != null && localRunOnceRequest.isIgnoreSerialization();
        final boolean uniqueCheckout = buildShouldBeSerialized && !serializationShouldBeIgnored
                && (localRunOnceRequest == null || localRunOnceRequest.isParallel());

        //noinspection ControlFlowStatementWithoutBraces
        if (LOG.isDebugEnabled()) LOG.debug("uniqueCheckout: " + uniqueCheckout); // NOPMD

        ThreadUtils.checkIfInterrupted();

        // Compose build start request to pass to the build runner
        final BuildStartRequestBuilder builder = new BuildStartRequestBuilder();
        final BuildStartRequest startRequest = builder.makeStartRequest(activeBuildID, startChangeListID, runOnceRequest);
        startRequest.setAgentHost(preferredAgentHost != null && preferredAgentHost.equals(vcsAgentHost) ? preferredAgentHost : null);
        startRequest.setUniqueAgentCheckout(uniqueCheckout);
        startRequest.addParameters(parameters);


        // NOTE: vimeshev - we do not overwrite clean checkout
        // setting for parallel builds. See bug 1093 for details.
        if (!startRequest.isParallel() && !startRequest.isCleanCheckout()) {

          startRequest.setCleanCheckout(cleanCheckoutCounter.increment());
        }

        buildRunner.requestBuildStart(startRequest);
      }
    } catch (final CommandStoppedException e) {

      currentStatus = SchedulerStatus.IDLE;
      IoUtils.ignoreExpectedException(e);
    } catch (final Exception e) {

      // Set status
      currentStatus = SchedulerStatus.IDLE;

      // Reset build request so that it does not show "Pending" in case
      // if there was a manual start and the cycle experienced exceptions.
      // See PARABUILD-1427 for more information.
      runOnceRequest = null;

      // Report error
      final Error error = new Error(StringUtils.toString(e));
      error.setSubsystemName(Error.ERROR_SUBSYSTEM_SCHEDULING);
      error.setErrorLevel(Error.ERROR_LEVEL_ERROR);
      error.setDetails(e);
      error.setBuildID(activeBuildID);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);

      // Wait
      waitSeconds(DEFAULT_ERROR_PAUSE);
    } finally {

      currentStatus = SchedulerStatus.IDLE;

      // Clear processed request
      //noinspection ObjectEquality
      if (runOnceRequest == localRunOnceRequest) {
        runOnceRequest = null;
      }
    }
  }


  /**
   * Returns true if build should be serialized.
   *
   * @return true if build should be serialized.
   * @see ScheduleProperty#SERIALIZE
   * @see SystemConfigurationManager#isSerializedBuilds()
   */
  private boolean isShouldBeSerialized() {

    // Get system/build wide setting
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    if (scm.isSerializedBuilds() || cm.getScheduleSettingValue(activeBuildID, ScheduleProperty.SERIALIZE,
            ScheduleProperty.OPTION_UNCHECKED).equals(ScheduleProperty.OPTION_CHECKED)) {
      return true;
    }

    // Check if this is a parallel build
    final BuildConfig config = cm.getBuildConfiguration(activeBuildID);
    if (config.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
      // Get leader build ID
      final int leaderBuildID = cm.getSourceControlSettingValue(activeBuildID,
              SourceControlSetting.REFERENCE_BUILD_ID, BuildConfig.UNSAVED_ID);
      // Return leader's setting
      return cm.getScheduleSettingValue(leaderBuildID, ScheduleProperty.SERIALIZE,
              ScheduleProperty.OPTION_UNCHECKED).equals(ScheduleProperty.OPTION_CHECKED);
    } else {
      return false;
    }
  }


  /**
   * Enables build scheduler
   */
  public void requestActivate() {

    if (shutdown) {
      return;
    }
    synchronized (lock) {
      runOnceRequest = null;
      if (automaticSchedule) {
        // only if enabled.
        runContinuously = true;
        paused = false;
      }
      lock.notifyAll();
    }
  }


  public void requestRunOnce(final BuildStartRequest startRequest) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Run once");
    }
//    if (log.isDebugEnabled()) log.debug("this.toString(): " + this.toString());
    if (shutdown) {
      return;
    }
    synchronized (lock) {
//      // NOTE: simeshev -- setting status is a workaround to give a UI
//      // user feeling that build started immediately after the
//      // clicking start. Normally status set by runSchedulerCycle.
//      currentStatus = SchedulerStatus.STARTING_BUILD;
      runOnceRequest = startRequest;
      lock.notifyAll();
    }
  }


  public void requestShutdown() {
    if (LOG.isDebugEnabled()) {
      LOG.debug("shutting down scheduler");
    }
    synchronized (lock) {
      runContinuously = false;
      runOnceRequest = null;
      shutdown = true;
//      lock.notifyAll();
      interrupt();
    }
  }


  /**
   * Request scheduler to pause until further notification
   * request to resume.
   *
   * @see #requestResume
   */
  public void requestPause() {
    if (paused || shutdown) {
      return;
    }
    synchronized (lock) {
      paused = true;
      runContinuously = false;
      runOnceRequest = null;
      // Update startup state
//      lock.notifyAll();
//      lock.notifyAll();
    }
    if (isAlive()) {
      interrupt();
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("scheduler.toString(): " + this.toString());
    }
  }


  /**
   * Request scheduler to resume.
   *
   * @see #requestPause
   */
  public void requestResume() {
    if (!paused || shutdown) {
      return;
    }
    synchronized (lock) {
      paused = false;
      runOnceRequest = null;
      if (automaticSchedule) {
        // only if enabled.
        runContinuously = true;
      }
      lock.notifyAll();
    }
  }


  /**
   * Requests that next checkout runs cleanly.
   */
  public void requestCleanCheckout() {
    this.cleanCheckoutCounter.forceNextCheckoutClean();
  }


  /**
   * @return null because AutomaticScheduler launches a build
   * according to appearance of new changes in a version control
   * system, not according to a schedule.
   */
  public Date nextBuildTime() {
    return null;
  }


  /**
   * Main thread execution method
   */
  public void run() {
    if (LOG.isDebugEnabled()) {
      LOG.debug("scheduler starting up");
    }
    try {
      currentStatus = SchedulerStatus.INITIALIZING;
      lastFoundChangeListID = cm.getLatestChangeListID(activeBuildID);
      // thread run loop
      while (!shutdown) {
        // wait for poll interval
        currentStatus = SchedulerStatus.IDLE;
        final int pollIntervalSecs;
        if (runContinuously) {
          pollIntervalSecs = cm.getScheduleSettingValue(activeBuildID, ScheduleProperty.AUTO_POLL_INTERVAL, DEFAULT_POLL_INTERVAL);
        } else {
          // NOTE: vimeshev - 2007-07-09 - in this case we
          // do not go to the database because we are not
          // going to hit continuous schedule cycle below.
          pollIntervalSecs = 5;
        }
        waitSeconds(pollIntervalSecs);
        // check if we have to exit
        if (shutdown) {
          return;
        }
        // check if we have to run cycle
        if (runOnceRequest != null) {
          runSchedulerCycle();
        } else if (runContinuously && !SystemConfigurationManagerFactory.getManager().isTimeInScheduleGap(new Date())) {
          runSchedulerCycle();
        }
      }
    } catch (final RuntimeException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Exception at run cycle", e);
      }
      ErrorManagerFactory.getErrorManager().reportSystemError(new Error(activeBuildID, "", Error.ERROR_SUBSYSTEM_SCHEDULING, e));
    } catch (final java.lang.Error e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Error at run cycle", e);
      }
      ErrorManagerFactory.getErrorManager().reportSystemError(new Error(activeBuildID, "", Error.ERROR_SUBSYSTEM_SCHEDULING, e));
      //noinspection ProhibitedExceptionThrown
      throw e;
    } finally {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Scheduler was shut down");
      }
    }
  }


  /**
   * Requests this schedule to reload it's configuration. This
   * method is used to notify a scheduler about changes in
   * persistent configuration.
   */
  public final void reloadSchedule() {
    final byte scheduleType = cm.getActiveBuildConfig(activeBuildID).getScheduleType();
    if (scheduleType == ActiveBuildConfig.SCHEDULE_TYPE_AUTOMATIC) {
      automaticSchedule = true;
      if (!paused) {
        runContinuously = true;
      }
    } else if (scheduleType == ActiveBuildConfig.SCHEDULE_TYPE_MANUAL) {
      automaticSchedule = false;
      runContinuously = false;
    }
  }


  /**
   * @return Current scheduler status
   */
  public SchedulerStatus getStatus() {
    // adjust IDLE status to "PAUSED"
    if (currentStatus.equals(SchedulerStatus.IDLE)) {
      if (paused) {
        return SchedulerStatus.PAUSED;
      }
      if (runOnceRequest != null) {
        return SchedulerStatus.PENDING_BUILD;
      }
    }
    return currentStatus;
  }


  /**
   * Waits for waitSeconds
   *
   * @param waitIntervalSeconds milliseconds to pause
   */
  private void waitSeconds(final int waitIntervalSeconds) {
    synchronized (lock) {
      try {
        //noinspection WaitNotInLoop
        lock.wait((long) waitIntervalSeconds * 1000L);
      } catch (final InterruptedException e) {
        if (!shutdown) {
          IoUtils.ignoreExpectedException(e);
        }
      }
    }
  }


  private void saveLastAgentHost(final AgentHost lastAgentHost) {
    if (lastAgentHost == null) {
      return;
    }
    cm.createOrUpdateActiveBuildAttribute(activeBuildID, ActiveBuildAttribute.LAST_AGENT_HOST, lastAgentHost.getHost());
  }


  /**
   * Returns preferred agent or null if there is no preference.
   *
   * @return preferred agent or null if there is no preference.
   */
  private AgentHost getPreferredAgentHost() {

    // Non-sticky don't have a preference
    if (!isStickyAgent()) {
      return null;
    }

    // Return VCS's if set
    if (sourceControl.getAgentHost() != null) {
      return sourceControl.getAgentHost();
    }

    // Get from the persistent storage
    final String lastHostName = cm.getActiveBuildAttributeValue(activeBuildID,
            ActiveBuildAttribute.LAST_AGENT_HOST, (String) null);
    if (!StringUtils.isBlank(lastHostName)) {
      return new AgentHost(lastHostName);
    }

    // Didn't find anything
    return null;
  }


  /**
   * @return true if the build should try to stick to a particular agent.
   * @noinspection BooleanMethodIsAlwaysInverted
   */
  private boolean isStickyAgent() {
    return cm.getScheduleSettingValue(activeBuildID, ScheduleProperty.STICKY_AGENT,
            ScheduleProperty.OPTION_UNCHECKED).equals(ScheduleProperty.OPTION_CHECKED);
  }


  /**
   * @return true if the build should run even if there are no changes.
   * @noinspection BooleanMethodIsAlwaysInverted
   */
  private boolean isRunIfNoChanges() {
    return cm.getScheduleSettingValue(activeBuildID, ScheduleProperty.RUN_IF_NO_CHANGES,
            ScheduleProperty.OPTION_UNCHECKED).equals(ScheduleProperty.OPTION_CHECKED);
  }


  /**
   * @return true if the build should run detected changes one-by-one.
   * @noinspection BooleanMethodIsAlwaysInverted
   */
  private boolean isBuildChangesOneByOne() {
    return cm.getScheduleSettingValue(activeBuildID, ScheduleProperty.AUTO_BUILD_ONE_BY_ONE,
            ScheduleProperty.OPTION_UNCHECKED).equals(ScheduleProperty.OPTION_CHECKED);
  }
}
