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
package org.parabuild.ci.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.AutomaticScheduler;
import org.parabuild.ci.build.BuildRunner;
import org.parabuild.ci.build.BuildRunnerFactory;
import org.parabuild.ci.build.BuildRunnerState;
import org.parabuild.ci.build.BuildScheduler;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.build.RepeatableScheduler;
import org.parabuild.ci.build.RunnerStatus;
import org.parabuild.ci.build.SchedulerStatus;
import org.parabuild.ci.build.StopRequestImpl;
import org.parabuild.ci.util.ExceptionUtils;
import org.parabuild.ci.util.ThreadUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.process.RemoteCommandManager;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.statistics.StatisticsMonitor;
import org.parabuild.ci.statistics.TimeToFixMonitor;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.VersionControlFactory;

import java.io.IOException;

/**
 * BuildService is a composite holding build runner, build
 * scheduler and build health monitor. BuildService is a child
 * service of the BuildService.
 *
 */
public final class BuildServiceImpl implements BuildService {

  private static final Log LOG = LogFactory.getLog(BuildServiceImpl.class);
  private final RemoteCommandManager commandManager = RemoteCommandManager.getInstance();

  private byte serviceStatus = SERVICE_STATUS_NOT_STARTED;
  private final int activeBuildID;

  private final BuildRunner buildRunner;
  private final BuildScheduler buildScheduler;
  private final ConfigurationManager configManager = ConfigurationManager.getInstance();
  private final BuildScriptMonitor buildScriptMonitor = new BuildScriptMonitor();


  public BuildServiceImpl(final BuildConfig buildConfig) {
    this.activeBuildID = buildConfig.getBuildID();
    if (ConfigurationManager.validateActiveID) {
      configManager.validateIsActiveBuildID(activeBuildID);
    }
    this.buildRunner = BuildRunnerFactory.getBuildRunner(activeBuildID);
    this.buildRunner.addSubscriber(new PublishedResultDateAdjuster());
    this.buildRunner.addSubscriber(new TimeToFixMonitor(activeBuildID));
    this.buildRunner.addSubscriber(new StatisticsMonitor(activeBuildID));
    this.buildRunner.addSubscriber(new BuildRunChangesIndexer());
    this.buildRunner.addSubscriber(buildScriptMonitor);
    this.buildScheduler = makeScheduler();
  }


  private BuildScheduler makeScheduler() {
    final BuildConfig buildConfig = configManager.getActiveBuildConfig(activeBuildID);
    final SourceControl sourceControl = VersionControlFactory.makeVersionControl(buildConfig);
    final BuildScheduler result;
    switch (buildConfig.getScheduleType()) {
      case BuildConfig.SCHEDULE_TYPE_AUTOMATIC:
        result = new AutomaticScheduler(activeBuildID, sourceControl, buildRunner, true);
        break;
      case BuildConfig.SCHEDULE_TYPE_RECURRENT:
        result = new RepeatableScheduler(activeBuildID, buildRunner);
        break;
      case BuildConfig.SCHEDULE_TYPE_MANUAL:
        result = new AutomaticScheduler(activeBuildID, sourceControl, buildRunner, false);
        break;
      case BuildConfig.SCHEDULE_TYPE_PARALLEL:
        result = new AutomaticScheduler(activeBuildID, sourceControl, buildRunner, false);
        break;
      default:
        throw new IllegalStateException("Unknown schedule type");
    }
    return result;
  }


  /**
   * Deactivates build.
   *
   * @param userID user that deactivated the build
   */
  public void deactivateBuild(final int userID) {
    setStartupStatus(BuildStatus.INACTIVE_VALUE);
    stopBuild(userID);
  }


  /**
   * Stops the build. Stopping can happen when a user selects
   * "Stop" command for the given build. As build itself consists
   * of a scheduler and a build runner, both need to be stopped.
   * <p/>
   * Semantics of the stop for a scheduler depends on a
   * scheduler:
   * <p/>
   * o  Automatic schedulers runs in a loop constantly checking
   * for changes spanning OS processes (via SourceControl).
   * Automatic scheduler should move into "paused" state after
   * stop.
   * <p/>
   * o Scheduled scheduler should just stop current processing if
   * any and start at next scheduled time as normally.
   *
   * @param userID the user's ID.
   */
  public void stopBuild(final int userID) {
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Stop build");
      }
      if (getStartupStatus() == BuildStatus.ACTIVE_VALUE) {
        setStartupStatus(BuildStatus.PAUSED_VALUE);
      }
      buildScheduler.requestPause();
      buildRunner.requestBuildStop(new StopRequestImpl(userID));

      // let it cool down - wait till everything stopped
      // or given time passed
      final long coolDownMillis = 500L;
      final long stepMillis = 100L;
      for (long waitedMillis = 0L; waitedMillis < coolDownMillis; waitedMillis += stepMillis) {
        if (buildScheduler.getStatus().equals(SchedulerStatus.PAUSED)
                && buildRunner.getStatus().equals(RunnerStatus.WAITING)) {
          // stop waiting
          break;
        }
        ThreadUtils.sleep(stepMillis);
      }

      // use hard stop
      if (LOG.isDebugEnabled()) {
        LOG.debug("using hard stop");
      }
      commandManager.killBuildCommands(activeBuildID);
    } catch (final Exception e) {
      reportStopException(e);
    }
  }


  /**
   * Sets build's startup status.
   *
   * @param status build's startup status.
   */
  private void setStartupStatus(final byte status) {
    final ActiveBuild activeBuild = configManager.getActiveBuild(activeBuildID);
    activeBuild.setStartupStatus(status);
    configManager.update(activeBuild);
  }


  /**
   * Returns build status ID
   *
   * @return integer BuildService status ID
   */
  private BuildStatus calculateConsolidatedStatus() {

    // check scheduler first
    // if (log.isDebugEnabled()) log.debug("buildScheduler status : " + buildScheduler.getStatus());
    if (buildScheduler.getStatus().equals(SchedulerStatus.INITIALIZING)) {
      return BuildStatus.INITIALIZING;
    }
    if (buildScheduler.getStatus().equals(SchedulerStatus.STARTING_BUILD)) {
      return BuildStatus.STARTING;
    }
    if (buildScheduler.getStatus().equals(SchedulerStatus.CHECKING_OUT)) {
      return BuildStatus.CHECKING_OUT;
    }
    if (buildScheduler.getStatus().equals(SchedulerStatus.GETTING_CHANGES)) {
      return BuildStatus.GETTING_CHANGES;
    }

    // runner
    if (buildRunner.getStatus().equals(RunnerStatus.BUILDING)) {
      return BuildStatus.BUILDING;
    }
    if (buildRunner.getStatus().equals(RunnerStatus.NOTIFYING)) {
      return BuildStatus.BUILDING;
    }
    if (buildRunner.getStatus().equals(RunnerStatus.STOPPING)) {
      return BuildStatus.STOPPING;
    }
    if (buildRunner.getStatus().equals(RunnerStatus.CHECKING_OUT)) {
      return BuildStatus.CHECKING_OUT;
    }

    // adjust calculated with initial status
    if (buildRunner.getStatus().equals(RunnerStatus.WAITING)) {
      final int startupStatus = configManager.getActiveBuildStartupStatus(activeBuildID);
      if (startupStatus == BuildStatus.INACTIVE_VALUE) {
        return BuildStatus.INACTIVE;
      }

      if (buildScheduler.getStatus().equals(SchedulerStatus.PAUSED)) {
        return BuildStatus.PAUSED;
      }

      if (buildScheduler.getStatus().equals(SchedulerStatus.PENDING_BUILD)) {
        return BuildStatus.PENDING_BUILD;
      }

      return BuildStatus.IDLE;
    }

    return BuildStatus.BUILDING; // covers unmentioned cases
  }


  /**
   * Returns composite information about build status and last
   * complete build.
   */
  public BuildState getBuildState() {
    final BuildConfig buildConfig = configManager.getBuildConfiguration(activeBuildID);
    if (buildConfig == null) {
      throw new IllegalStateException("Build configuration for build ID " + activeBuildID + " not found");
    }
    final BuildRunnerState buildRunnerState = buildRunner.getRunnerState();
    final BuildState state = new BuildState();
    state.setActiveBuildID(activeBuildID);
    state.setBuildName(buildConfig.getBuildName());
    state.setCurrentlyRunningBuildConfigID(buildRunnerState.getCurrentlyRunningBuildConfigID());
    state.setCurrentlyRunningBuildRunID(buildRunnerState.getCurrentlyRunningBuildRunID());
    state.setCurrentlyRunningStep(buildRunnerState.getCurrentlyRunningStep());
    state.setLastCleanBuildRun(buildRunnerState.getLastCleanBuildRun());
    state.setLastCompleteBuildRun(buildRunnerState.getLastCompleteBuildRun());
    state.setSchedule(buildConfig.getScheduleType());
    state.setSourceControl(buildConfig.getSourceControl());
    state.setStatus(calculateConsolidatedStatus());
    state.setNextBuildTime(buildScheduler.nextBuildTime());
    state.setAccess(buildConfig.getAccess());
    state.setCurrentlyRunningOnBuildHost(buildRunnerState.getCurrentlyRunningOnHost());
    state.setCurrentlyRunningBuildNumber(buildRunnerState.getCurrentlyRunningBuildNumber());
    state.setCurrentlyRunningChangeListNumber(buildRunnerState.getCurrentlyRunningChangeListNumber());
    return state;
  }


  /**
   * @return this build's build ID
   */
  public int getActiveBuildID() {
    return activeBuildID;
  }


  /**
   * Returns status
   */
  public byte getServiceStatus() {
    return serviceStatus;
  }


  /**
   * @return ServiceName.BUILD_SERVICE
   */
  public ServiceName serviceName() {
    return ServiceName.BUILD_SERVICE;
  }


  /**
   * Service shutdown
   */
  public void shutdownService() {
    try {
      validateBuildServiceStartedUp();
      buildScheduler.requestShutdown();
      buildRunner.requestShutdown();
      commandManager.killBuildCommands(activeBuildID);
    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      reportShutdownException(e);
    }
    serviceStatus = SERVICE_STATUS_NOT_STARTED;
  }


  /**
   * Starts this particular build service, including starting
   * BuildRunner and HealthMonitor threads.
   */
  public void startupService() {

    final ActiveBuildConfig buildConfig = configManager.getActiveBuildConfig(activeBuildID);
    final String buildName = buildConfig.getBuildName();

      LOG.debug("Starting runner: " + buildName);

    buildRunner.start();

    if (LOG.isDebugEnabled()) {
      LOG.debug("Starting scheduler: " + buildName);
    }

    final int startupStatus = getStartupStatus();

    LOG.debug("Scheduler startup status: " + startupStatus + ", build: " + buildConfig.getBuildName());

    if (startupStatus == BuildStatus.ACTIVE_VALUE) {

      LOG.debug("Requesting scheduler to activate: " + buildName);

      buildScheduler.requestActivate();

    } else if (startupStatus == BuildStatus.PAUSED_VALUE) {

      final byte scheduleType = ConfigurationManager.getInstance().getActiveBuildConfig(activeBuildID).getScheduleType();
      if (scheduleType == BuildConfig.SCHEDULE_TYPE_RECURRENT) {

        LOG.debug("Requesting scheduler to activate: " + buildName);

        buildScheduler.requestActivate();
      }

      LOG.debug("Requesting scheduler to pause: " + buildName);

      buildScheduler.requestPause();
    } else if (startupStatus == BuildStatus.INACTIVE_VALUE) {

      LOG.debug("Requesting scheduler to pause: " + buildName);

      buildScheduler.requestPause();
    }

    buildScheduler.start();
    serviceStatus = SERVICE_STATUS_STARTED;
  }


  private int getStartupStatus() {
    return configManager.getActiveBuild(activeBuildID).getStartupStatus();
  }


  protected void finalize() throws Throwable {
    try {
      if (buildRunner != null) {
        try {
          buildRunner.requestShutdown();
        } catch (final Exception e) {
          if (LOG.isWarnEnabled()) {
            LOG.warn("Ignored exception while shutting down", e);
          }
        }
        if (buildRunner.isAlive()) {
          LOG.error("BuildRunner was alive at Build death");
        }
      }
    } finally {
      super.finalize(); // NOPMD - it is required to call super's finalize.
    }
  }


  /**
   * Activates build
   */
  public void activate() {
    // update configuration
    final ActiveBuild activeBuild = configManager.getActiveBuild(activeBuildID);
    // REVIEWME: not very clear if startup status is the same for all builds
    if (activeBuild.getStartupStatus() != BuildStatus.ACTIVE_VALUE) {
      activeBuild.setStartupStatus(BuildStatus.ACTIVE_VALUE);
      configManager.update(activeBuild);
    }

    // activate
    buildScheduler.requestActivate();
  }


  /**
   * Notifies the build that config has changed externally.
   */
  public void notifyConfigurationChanged() {
    buildScheduler.reloadSchedule();
  }


  /**
   * Resumes previously stopped/paused build.
   */
  public void resumeBuild() {
    if (getStartupStatus() != BuildStatus.INACTIVE_VALUE) {
      buildScheduler.requestResume();
      setStartupStatus(BuildStatus.ACTIVE_VALUE);
    }
  }


  /**
   * Helper method to check if we are up.
   */
  private void validateBuildServiceStartedUp() {
    if (serviceStatus != SERVICE_STATUS_STARTED) {
      throw new IllegalStateException("Build service has not been started up");
    }
  }


  private void reportShutdownException(final Exception e) {
    final Error error = new Error("Error while shutting down a build");
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    error.setSendEmail(false);
    error.setBuildID(activeBuildID);
    error.setDetails(e);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private void reportStopException(final Exception e) {
    final Error error = new Error("Error while stopping a build");
    error.setDetails(e);
    error.setBuildID(activeBuildID);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    error.setSendEmail(false);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  /**
   * Re-runs build
   *
   * @param startRequest the request to start the build.
   */
  public void rerunBuild(final BuildStartRequest startRequest) {
    // REVIEWME: if any other request comes in, this one will get
    // overridden. Consider queuing re-run requests.
    //
    // REVIEWME: this is a plain duplicate of the startBuild method.
    validateBuildServiceStartedUp();
    buildScheduler.requestRunOnce(startRequest);
  }


  /**
   * Requests that next checkout runs cleanly.
   */
  public void requestCleanCheckout() {
    buildScheduler.requestCleanCheckout();
  }


  /**
   * Requests build to start, according to the given {@link BuildStartRequest}.
   *
   * @param startRequest the request to start the build.
   */
  public void startBuild(final BuildStartRequest startRequest) {
    validateBuildServiceStartedUp();
    // TODO: this method should be removed when
    // build request is implemented.
    buildScheduler.requestRunOnce(startRequest);
  }


  /**
   * @param sinceServerTimeMs the server time since that to return the update.
   * @return Log's {@link TailUpdate} since the given time in millis.
   */
  public TailUpdate getTailUpdate(final long sinceServerTimeMs) throws IOException {
    // get command
    final int currentlyRunningHandle = buildScriptMonitor.getCurrentlyRunningHandle();
//    if (log.isDebugEnabled()) log.debug("currentlyRunningHandle: " + currentlyRunningHandle);
    if (currentlyRunningHandle == 0) {
      return TailUpdateImpl.EMPTY_UPDATE;
    }

    // request log tail update
    final Agent agent = buildRunner.getCurrentAgent();
    if (agent == null) {
      return TailUpdateImpl.EMPTY_UPDATE;
    }
    try {
      return agent.getTailUpdate(currentlyRunningHandle, sinceServerTimeMs);
    } catch (final AgentFailureException e) {
      throw ExceptionUtils.createIOException(e);
    }
  }


  public String toString() {
    return "BuildServiceImpl{" +
            "serviceStatus=" + serviceStatus +
            ", activeBuildID=" + activeBuildID +
            ", buildRunner=" + buildRunner +
            ", buildScheduler=" + buildScheduler +
            ", configManager=" + configManager +
            ", buildScriptMonitor=" + buildScriptMonitor +
            '}';
  }
}
