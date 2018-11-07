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
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.VersionControlFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This job is responsible for running a scheduled build.
 */
public final class RepeatableScheduleJob implements Job {

  private static final Log log = LogFactory.getLog(RepeatableScheduleJob.class);

  public static final String BUILD_ID_KEY = "BUILD_ID";
  public static final String BUILD_RUNNER_KEY = "BUILD_RUNNER";
  public static final String CLEAN_CHECKOUT_COUNTER = "CLEAN_CHECKOUT_COUNTER";

  /**
   * Holds Boolean object. If Boolean.TRUE than the check out
   * should be forced.
   */
  public static final String FORCE_CLEAN_CHECKOUT = "CLEAN_CHECKOUT";

  /**
   * Holds Boolean object. If Boolean.TRUE than the build will run even
   * if there are no new changes
   */
  public static final String RUN_IF_NO_CHANGES = "RUN_IF_NO_CHANGES";

  // registry for active builds
  private static final Set activeJobs = new HashSet(5);

  private int activeBuildID = BuildConfig.UNSAVED_ID;
  private BuildRunner buildRunner = null;


  /**
   * Callback method for trigger
   *
   * @param ctx job context data
   */
  public void execute(final JobExecutionContext ctx) {

    try {
      if (log.isDebugEnabled()) {
        log.debug("======== EXECUTING SCHEDULED BUILD ====== ");
      }

      // check if in the golbal schedule gap
      final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
      final Date currentTime = new Date();
      if (systemCM.isTimeInScheduleGap(currentTime)) {
        if (log.isDebugEnabled()) {
          log.debug("Current time is in schedule gap = " + currentTime);
        }
        return;
      }

      // get build ID
      final JobDataMap dataMap = ctx.getJobDetail().getJobDataMap();
      if (!isValidDataMap(dataMap)) {
        return;
      }
      activeBuildID = dataMap.getInt(BUILD_ID_KEY);
      final Integer integerActiveBuildID = new Integer(this.activeBuildID);

      // get build runner
      buildRunner = (BuildRunner) dataMap.get(BUILD_RUNNER_KEY);
      if (buildRunner == null) {
        reportErrorBuldRunnerNotFound();
        return;
      }

      synchronized (activeJobs) {
        if (activeJobs.contains(integerActiveBuildID)) {
          return; // there is a build running
        }
        activeJobs.add(integerActiveBuildID);
      }

      // Skip if was requested by a manual start
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final ActiveBuildAttribute skipBuildAttribute = cm.getActiveBuildAttribute(activeBuildID, ActiveBuildAttribute.SKIP_NEXT_SCHEDULED_BUILD);
      if (skipBuildAttribute != null) {

        if (ActiveBuildAttribute.OPTION_CHECKED.equalsIgnoreCase(skipBuildAttribute.getPropertyValue())) {

          // Delete flag
          cm.deleteObject(skipBuildAttribute);

          // Skip
          return;
        } else {

          cm.deleteObject(skipBuildAttribute);
        }
      }

      // run the build
      try {
        final CleanCheckoutCounter cleanCheckoutCounter = (CleanCheckoutCounter) dataMap.get(CLEAN_CHECKOUT_COUNTER);
        final boolean forceCleanCheckout = (Boolean) dataMap.get(FORCE_CLEAN_CHECKOUT);
        final boolean cleanCheckOut = cleanCheckoutCounter.increment() || forceCleanCheckout;
        runBuild((Boolean) dataMap.get(RUN_IF_NO_CHANGES), cleanCheckOut);
      } finally {
        // remove build run presense flag
        synchronized (activeJobs) {
          activeJobs.remove(integerActiveBuildID);
        }
      }
    } catch (final Exception e) {
      reportErrorRunningBuild(e);
    } catch (final java.lang.Error e) {
      reportErrorRunningBuild(e);
      throw e;
    }
  }


  /**
   * Runs build
   *
   * @param runIfNoChanges
   * @param cleanCheckOut
   */
  private void runBuild(final boolean runIfNoChanges, final boolean cleanCheckOut) {
    try {
      if (log.isDebugEnabled()) {
        log.debug("build runner status: " + buildRunner.getStatus());
      }
      if (!buildRunner.getStatus().equals(RunnerStatus.WAITING)) {
        return;
      }
      //
      // Check if there were changes
      //
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final SourceControl sourceControl = VersionControlFactory.makeVersionControl(cm.getActiveBuildConfig(activeBuildID));

      // If sticky, read prefered agent host from the presistent storage.
      final AgentHost preferedAgentHost = getPreferedAgentHost();

      // Get actual agent
      final AgentHost nextLiveAgentHost = AgentManager.getInstance().getNextLiveAgentHost(activeBuildID,
              BuildManager.getInstance().getFreeAgentHosts(activeBuildID), preferedAgentHost);

      // Store last agent host for possible future use by stiky agents
      saveLastAgentHost(nextLiveAgentHost);

      // Set agent
      sourceControl.setAgentHost(nextLiveAgentHost);
      final int lastBuildChangeListID = cm.getLatestChangeListID(activeBuildID);
      if (log.isDebugEnabled()) {
        log.debug("lastBuildChangeListID: " + lastBuildChangeListID);
      }
      // NOTE: vimeshev - we expect that getChangesSince for the repeatable
      // schedule source control will return only change lists upto the last
      // *successful* automatic build.
      final int changeListID = sourceControl.getChangesSince(lastBuildChangeListID);
      if (log.isDebugEnabled()) {
        log.debug("changeListID: " + changeListID);
      }
      if (changeListID == lastBuildChangeListID) {
        // no changes - check if we have to run at no changes
        if (runIfNoChanges) {
          // yes, we have to run
          if (log.isDebugEnabled()) {
            log.debug("have to run even if there are no changes");
          }
          requestBuildStart(changeListID, cleanCheckOut, nextLiveAgentHost);
        }
      } else {
        requestBuildStart(changeListID, cleanCheckOut, nextLiveAgentHost);
      }
    } catch (final Exception e) {
      reportErrorRunningBuild(e);
    }
  }


  private void requestBuildStart(final int targetChangeListID, final boolean cleanCheckout, final AgentHost agentHost) {
    if (log.isDebugEnabled()) {
      log.debug("requesting scheduled build to start at " + targetChangeListID);
    }
    // compose build start request to be passed to the build runner.
    final BuildStartRequest startRequest = new BuildStartRequestBuilder().makeStartRequest(activeBuildID, targetChangeListID, null);
    startRequest.setCleanCheckout(cleanCheckout);
    startRequest.setAgentHost(agentHost);
    buildRunner.requestBuildStart(startRequest);
  }


  /**
   */
  private boolean isValidDataMap(final JobDataMap dataMap) {
    if (dataMap.containsKey(BUILD_ID_KEY)) {
      return true;
    }
    reportErrorBuildNotFound();
    return false;
  }


  private void reportErrorBuldRunnerNotFound() {
    final Error error = new Error("Repeatable scheduler could not find build runner");
    error.setDetails("Please report this error to support");
    error.setBuildID(activeBuildID);
    error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
    error.setSubsystemName(Error.ERROR_SUSBSYSTEM_SCHEDULING);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private void reportErrorBuildNotFound() {
    final Error error = new Error("Repeatable scheduler could not find build to run");
    error.setDetails("Please report this error to support");
    error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
    error.setSubsystemName(Error.ERROR_SUSBSYSTEM_SCHEDULING);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private void reportErrorRunningBuild(final Throwable e) {
    final Error error = new Error("Error while running scheduled build: " + StringUtils.toString(e));
    error.setDetails(e);
    error.setBuildID(activeBuildID);
    error.setErrorLevel(Error.ERROR_LEVEL_ERROR);
    error.setSubsystemName(Error.ERROR_SUSBSYSTEM_SCHEDULING);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private void saveLastAgentHost(final AgentHost lastAgentHost) {
    if (lastAgentHost == null) {
      return;
    }
    ConfigurationManager.getInstance().createOrUpdateActiveBuildAttribute(activeBuildID,
            ActiveBuildAttribute.LAST_AGENT_HOST, lastAgentHost.getHost());
  }


  private boolean isStickyAgent() {
    return ConfigurationManager.getInstance().getScheduleSettingValue(activeBuildID, ScheduleProperty.STICKY_AGENT,
            ScheduleProperty.OPTION_UNCHECKED).equals(ScheduleProperty.OPTION_CHECKED);
  }


  private AgentHost getPreferedAgentHost() {
    // Non-sticky don't have a preference
    if (!isStickyAgent()) {
      return null;
    }

    // Non-sticky try to get from the persistent storage.
    final String lastHostName = ConfigurationManager.getInstance().getActiveBuildAttributeValue(activeBuildID,
            ActiveBuildAttribute.LAST_AGENT_HOST, (String) null);
    if (StringUtils.isBlank(lastHostName)) {
      return null;
    } else {
      return new AgentHost(lastHostName);
    }
  }


  public String toString() {
    return "RepeatableScheduleJob{" +
            "activeBuildID=" + activeBuildID +
            ", buildRunner=" + buildRunner +
            '}';
  }
}
