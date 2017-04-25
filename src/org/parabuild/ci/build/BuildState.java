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
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;


/**
 * Build state is a value object that represents status of the
 * particular build.
 */
public final class BuildState implements Serializable {

  private static final long serialVersionUID = 2601650602009236539L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BuildState.class); // NOPMD


  /**
   * This comparator copares BuildStates by their build names.
   */
  public static final Comparator BUILD_NAME_COMPARATOR = new Comparator() {
    /**
     * Compares its two BuildStates accordingly to their build
     * names.
     */
    public int compare(final Object o1, final Object o2) {
      if (o1 == null || o2 == null) {
        return 0;
      }
      return ((BuildState) o1).buildName.compareToIgnoreCase(((BuildState) o2).buildName);
    }
  };

  public static final String STRING_NOT_RUN_YET = "Not Run Yet";
  public static final String STRING_NOT_AVAILABLE = "Not Available";

  private volatile BuildRun lastCleanBuildRun = null;
  private volatile BuildRun lastCompleteBuildRun = null;
  private volatile BuildSequence currentlyRunningStep = null;
  private volatile BuildStatus status = null;
  private volatile int activeBuildID = BuildConfig.UNSAVED_ID;
  private volatile int currentlyRunningBuildConfigID = BuildConfig.UNSAVED_ID;
  private volatile int currentlyRunnigBuildRunID = BuildRun.UNSAVED_ID;
  private volatile int currentlyRunningBuildNumber = 0;
  private volatile byte access = BuildConfig.ACCESS_PRIVATE;
  private volatile byte schedule = 0;
  private volatile byte sourceControl = 0;
  private volatile String buildName = null;
  private volatile Date nextBuildTime = null;
  private volatile String currentlyRunningOnBuildHost = null;
  private volatile String currentlyRunningChangeListNumber = null;


  /**
   * @return build name
   */
  public String getBuildName() {
    return buildName;
  }


  /**
   * Sets build name
   *
   * @param buildName build name, can not be null or blank
   */
  public void setBuildName(final String buildName) {
    ArgumentValidator.validateArgumentNotBlank(buildName, "build name");
    this.buildName = buildName;
  }


  /**
   * @return build ID. Each build has as unique ID associated
   *         with it
   */
  public int getActiveBuildID() {
    return activeBuildID;
  }


  /**
   * @return build ID as String.
   */
  public String getBuildIDAsString() {
    return Integer.toString(activeBuildID);
  }


  /**
   * Sets version control code
   */
  public void setSourceControl(final byte sourceControl) {
    this.sourceControl = sourceControl;
  }


  /**
   * Sets build ID. Each build has as unique ID associated with
   * it
   */
  public void setActiveBuildID(final int activeBuildID) {
    ArgumentValidator.validateBuildIDInitialized(activeBuildID);
    this.activeBuildID = activeBuildID;
  }


  public BuildStatus getStatus() {
    return status;
  }


  public void setStatus(final BuildStatus status) {
    this.status = status;
  }


  public int getSchedule() {
    return schedule;
  }


  public void setSchedule(final byte schedule) {
    this.schedule = schedule;
  }


  public Date getFinishedAt() {
    if (lastCompleteBuildRun != null) {
      return lastCompleteBuildRun.getFinishedAt();
    }
    return null;
  }


  public String getFinishedAtAsString() {
    if (lastCompleteBuildRun != null
            && lastCompleteBuildRun.getFinishedAt() != null) {
      return SystemConfigurationManagerFactory.getManager()
              .formatDateTime(lastCompleteBuildRun.getFinishedAt());
    }
    return STRING_NOT_RUN_YET;
  }


  public String getBuildResultAsString() {
    if (lastCompleteBuildRun != null) {
      return lastCompleteBuildRun.buildResultToString();
    }
    return "";
  }


  /**
   * Returns status in the human - readable format
   */
  public String getStatusAsString() {
    if (status.equals(BuildStatus.STARTING)) {
      return "Starting";
    }
    if (status.equals(BuildStatus.INITIALIZING)) {
      return "Initializing";
    }
    if (status.equals(BuildStatus.BUILDING)) {
      return "Building";
    }
    if (status.equals(BuildStatus.INACTIVE)) {
      return "Inactive";
    }
    if (status.equals(BuildStatus.GETTING_CHANGES)) {
      return "Getting changes";
    }
    if (status.equals(BuildStatus.PAUSED)) {
      return "Paused";
    }
    if (status.equals(BuildStatus.PENDING_BUILD)) {
      return "Pending build";
    }
    if (status.equals(BuildStatus.IDLE)) {
      if (schedule == BuildConfig.SCHEDULE_TYPE_RECURRENT) {
        return "Scheduled";
      }
      return "Idle";
    }
    if (status.equals(BuildStatus.STOPPING)) {
      return "Stopping";
    }
    if (status.equals(BuildStatus.CHECKING_OUT)) {
      if (sourceControl == BuildConfig.SCM_CVS) {
        return "Checking out";
      }
      if (sourceControl == BuildConfig.SCM_SVN) {
        return "Checking out";
      }
      if (sourceControl == BuildConfig.SCM_CLEARCASE) {
        return "Updating view";
      }
      if (sourceControl == BuildConfig.SCM_PERFORCE) {
        return "Syncing";
      }
      return "Checking out";
    }

    return "System";
  }


  /**
   * Returns true if build is busy
   */
  public boolean isBusy() {
    return status.equals(BuildStatus.BUILDING) || status.equals(BuildStatus.STARTING) || status.equals(BuildStatus.STOPPING) || status.equals(BuildStatus.CHECKING_OUT);
  }


  /**
   * Returns ID of the last build run
   */
  public int getLastBuildRunID() {
    if (lastCompleteBuildRun != null) {
      return lastCompleteBuildRun.getBuildRunID();
    }
    return BuildRun.UNSAVED_ID;
  }


  public BuildRun getLastCompleteBuildRun() {
    return lastCompleteBuildRun;
  }


  public void setLastCompleteBuildRun(final BuildRun lastCompleteBuildRun) {
    this.lastCompleteBuildRun = lastCompleteBuildRun;
  }


  /**
   * @return last clean build run
   */
  public BuildRun getLastCleanBuildRun() {
    return lastCleanBuildRun;
  }


  /**
   * Sets last clean build run.
   */
  public void setLastCleanBuildRun(final BuildRun lastCleanBuildRun) {
    this.lastCleanBuildRun = lastCleanBuildRun;
  }


  /**
   * Null if not running anything.
   */
  public String getCurrentlyRunningStepName() {
    if (currentlyRunningStep == null) {
      return null;
    }
    return currentlyRunningStep.getStepName();
  }


  public void setCurrentlyRunningBuildRunID(final int currentlyRunnigBuildRunID) {
    this.currentlyRunnigBuildRunID = currentlyRunnigBuildRunID;
  }


  public int getCurrentlyRunningBuildRunID() {
    return currentlyRunnigBuildRunID;
  }


  public int getCurrentlyRunnigSequenceID() {
    if (currentlyRunningStep == null) {
      return BuildSequence.UNSAVED_ID;
    }
    return currentlyRunningStep.getSequenceID();
  }


  public BuildSequence getCurrentlyRunningStep() {
    return currentlyRunningStep;
  }


  public void setCurrentlyRunningStep(final BuildSequence currentlyRunningStep) {
    this.currentlyRunningStep = currentlyRunningStep;
  }


  /**
   * @return An ID of the currently running build configuration.
   */
  public int getCurrentlyRunningBuildConfigID() {
    return currentlyRunningBuildConfigID;
  }


  public void setCurrentlyRunningBuildConfigID(final int currentlyRunningBuildConfigID) {
    this.currentlyRunningBuildConfigID = currentlyRunningBuildConfigID;
  }


  /**
   * @return time when next build will run or null if there is no
   *         information.
   */
  public Date getNextBuildTime() {
    return nextBuildTime;
  }


  /**
   * Stes time when next build will run or null if there is no
   * information.
   */
  public void setNextBuildTime(final Date nextBuildTime) {
    if (!(nextBuildTime == null)) {
      this.nextBuildTime = (Date) nextBuildTime.clone();
    } else {
      this.nextBuildTime = null;
    }
  }


  /**
   * @return true if build is running (checking out/stopping/building/etc).
   */
  public boolean isRunning() {
    return currentlyRunnigBuildRunID != BuildRun.UNSAVED_ID;
  }


  public int getSourceControl() {
    return sourceControl;
  }


  public byte getAccess() {
    return access;
  }


  public void setAccess(final byte access) {
    this.access = access;
  }


  /**
   * Sets build host the build is currently running on or null
   * if not running or host is not set.
   */
  public void setCurrentlyRunningOnBuildHost(final String currentlyRunningOnBuildHost) {
    this.currentlyRunningOnBuildHost = currentlyRunningOnBuildHost;
  }


  /**
   * Returns build host the build is currently running on or null
   * if not running or host is not set.
   */
  public String getCurrentlyRunningOnBuildHost() {
    return currentlyRunningOnBuildHost;
  }


  public boolean isParallel() {
    return schedule == BuildConfig.SCHEDULE_TYPE_PARALLEL;
  }


  public int getCurrentlyRunningBuildNumber() {
    return currentlyRunningBuildNumber;
  }


  public void setCurrentlyRunningBuildNumber(final int currentlyRunningBuildNumber) {
    this.currentlyRunningBuildNumber = currentlyRunningBuildNumber;
  }


  public String getCurrentlyRunningChangeListNumber() {
    return currentlyRunningChangeListNumber;
  }


  public void setCurrentlyRunningChangeListNumber(final String currentlyRunningChangeListNumber) {
    this.currentlyRunningChangeListNumber = currentlyRunningChangeListNumber;
  }


  /**
   * Returns true if the build can be stopped.
   *
   * @return true if the build can be stopped.
   */
  public boolean isStopable() {
    return !(status.equals(BuildStatus.INACTIVE) || status.equals(BuildStatus.PAUSED));
  }


  /**
   * Returns true if the build can be resumed.
   *
   * @return true if the build can be resumed.
   */
  public boolean isResumable() {
    return status.equals(BuildStatus.PAUSED);
  }


  public int getCurrentlyRunningStepID() {
    return currentlyRunningStep == null ? -1 : currentlyRunningStep.getSequenceID();
  }


  public int getLastCleanBuildRunID() {
    return lastCleanBuildRun == null ? -1 : lastCleanBuildRun.getBuildRunID();
  }


  public int getLastCompleteBuildRunID() {
    return lastCompleteBuildRun == null ? -1 : lastCompleteBuildRun.getBuildRunID();
  }


  public boolean isNextBuildTimeSet() {
    return nextBuildTime != null;
  }


  public String toString() {
    return "BuildState{" +
            "lastCleanBuildRun=" + lastCleanBuildRun +
            ", lastCompleteBuildRun=" + lastCompleteBuildRun +
            ", currentlyRunningStep=" + currentlyRunningStep +
            ", status=" + status +
            ", activeBuildID=" + activeBuildID +
            ", currentlyRunningBuildConfigID=" + currentlyRunningBuildConfigID +
            ", currentlyRunnigBuildRunID=" + currentlyRunnigBuildRunID +
            ", currentlyRunningBuildNumber=" + currentlyRunningBuildNumber +
            ", access=" + access +
            ", schedule=" + schedule +
            ", sourceControl=" + sourceControl +
            ", buildName='" + buildName + '\'' +
            ", nextBuildTime=" + nextBuildTime +
            ", currentlyRunningOnBuildHost='" + currentlyRunningOnBuildHost + '\'' +
            ", currentlyRunningChangeListNumber='" + currentlyRunningChangeListNumber + '\'' +
            '}';
  }
}
