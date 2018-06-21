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
import org.parabuild.ci.common.CommonConstants;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.notification.NotificationManager;
import org.parabuild.ci.notification.NotificationManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.process.TimeoutCallback;
import org.parabuild.ci.process.TimeoutWatchdog;

/**
 * This handler is responsible for handling timed out builds.
 *
 * @see TimeoutCallback
 * @see TimeoutWatchdog#setTimeoutCallback
 */
public final class BuildTimeoutCallback implements TimeoutCallback, CommonConstants {

  private static final Log log = LogFactory.getLog(BuildTimeoutCallback.class);

  private boolean notificationEnabled = true;
  private BuildRun buildRun = null;
  private BuildScriptRunner scriptRunner = null;
  private BuildSequence sequence = null;
  private final NotificationManager notificationManager;

  /**
   * Default constructor.
   */
  public BuildTimeoutCallback() {
    this(NotificationManagerFactory.makeNotificationManager());
  }

  /**
   * Constructor.
   *
   * @param notificationManager notification manager
   */
  public BuildTimeoutCallback(final NotificationManager notificationManager) {
    this.notificationManager = notificationManager;
  }

  /**
   * Sets script runner for which timeouts and hangs could
   * occur.
   *
   * @param scriptRunner
   */
  public void setScriptRunner(final BuildScriptRunner scriptRunner) {
    this.scriptRunner = scriptRunner;
  }


  /**
   * Sets build run for which timeouts and hangs could occur.
   */
  public void setBuildRun(final BuildRun buildRun) {
    this.buildRun = buildRun;
  }


  /**
   * Sets build sequence for which timeouts and hangs could
   * occur.
   */
  public void setSequence(final BuildSequence sequence) {
    this.sequence = sequence;
  }


  /**
   * This callback method is called when watched command is timed
   * out but before watchdog tries to kill command.
   */
  public void commandTimedOut() {
    if (scriptRunner == null) throw new IllegalStateException("Script runner was not set");
    scriptRunner.markTimedOut();
  }


  /**
   * This callback method is called when watched command is
   * identified as hung.
   */
  public void commandHung() {
    if (notificationEnabled) {
      if (buildRun == null) throw new IllegalStateException("Build run was not set");
      if (sequence == null) throw new IllegalStateException("Build sequence was not set");
      reportBuildHungHard();
      notifyBuildHungHard();
    }
  }


  /**
   * Sends notification ignoring any exceptions
   */
  private void notifyBuildHungHard() {
    try {
      notificationManager.notifyBuildStepHung(buildRun, sequence);
    } catch (final Exception e) {
      if (log.isWarnEnabled()) log.warn("Ignored exception while notifying about hung build", e);
    }
  }


  /**
   * Stores error in the file system ignoring any exceptions
   */
  private void reportBuildHungHard() {
    final Error error = new Error();
    error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
    // set preliminary description
    error.setDescription("Build " + buildRun.getBuildName() + "hung at step " + sequence.getStepName());

    try {
      error.setBuildName(buildRun.getBuildName());
      error.setDescription("Build hung");
      error.setDetails("Build hung at step \"" + sequence.getStepName() + "\" after " + sequence.getTimeoutMins() + " minutes timeout. System attempted and failed to stop the build. Build won't start until the problem is fixed. System requires immediate attention of a build administrator.");
      error.setPossibleCause("Build script spawns long-running process(es) that cannot be identified as belonging to the build sequence.");
    } catch (final Exception e) {
      // ignore, nothing to go to from here
      log.warn(STR_IGNORED_EXCEPTION, e);
    }
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  /**
   * Disables notification if false
   *
   * @param notificationEnabled disables notification if false
   */
  public void setNotificationEnabled(final boolean notificationEnabled) {
    this.notificationEnabled = notificationEnabled;
  }


  public String toString() {
    return "BuildTimeoutCallback{" +
            "notificationEnabled=" + notificationEnabled +
            ", buildRun=" + buildRun +
            ", scriptRunner=" + scriptRunner +
            ", sequence=" + sequence +
            '}';
  }
}
