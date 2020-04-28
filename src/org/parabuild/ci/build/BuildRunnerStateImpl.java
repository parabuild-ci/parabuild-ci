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

import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;

/**
 * This class represents reportable build runner state.
 */
final class BuildRunnerStateImpl implements BuildRunnerState {

  private volatile int currentlyRunningBuildRunID = BuildRun.UNSAVED_ID;
  private volatile int currentlyRunningBuildConfigID = BuildConfig.UNSAVED_ID;
  private volatile BuildRun lastCleanBuildRun;
  private volatile BuildRun lastCompleteBuildRun;
  private volatile BuildSequence currentlyRunningStep;
  private volatile String currentlyRunningOnBuildHost;
  private volatile String currentlyRunningChangeListNumber;
  private volatile int currentlyRunningBuildNumber;


  /**
   * Constructor. Copies lastCompleteBuildRun and
   * lastCleanBuildRun into class internal variables.
   *
   * @param lastCompleteBuildRun
   * @param lastCleanBuildRun
   */
  BuildRunnerStateImpl(final BuildRun lastCompleteBuildRun, final BuildRun lastCleanBuildRun) {
    setLastCompleteBuildRun(lastCompleteBuildRun);
    setLastCleanBuildRun(lastCleanBuildRun);
  }


  public int getCurrentlyRunningBuildRunID() {
    return currentlyRunningBuildRunID;
  }


  public void setCurrentlyRunningBuildRunID(final int currentlyRunningBuildRunID) {
    this.currentlyRunningBuildRunID = currentlyRunningBuildRunID;
  }


  public int getCurrentlyRunningBuildConfigID() {
    return currentlyRunningBuildConfigID;
  }


  public void setCurrentlyRunningBuildConfigID(final int currentlyRunningBuildConfigID) {
    this.currentlyRunningBuildConfigID = currentlyRunningBuildConfigID;
  }


  public BuildRun getLastCleanBuildRun() {
    return lastCleanBuildRun;
  }


  /**
   * Sets last clean build run by copying it.
   *
   * @param lastCleanBuildRun
   */
  public void setLastCleanBuildRun(final BuildRun lastCleanBuildRun) {
    this.lastCleanBuildRun = BuildRun.copy(lastCleanBuildRun);
  }


  public BuildRun getLastCompleteBuildRun() {
    return lastCompleteBuildRun;
  }


  /**
   * Sets last complete build run by copying it.
   *
   * @param lastCompleteBuildRun
   */
  public void setLastCompleteBuildRun(final BuildRun lastCompleteBuildRun) {
    this.lastCompleteBuildRun = BuildRun.copy(lastCompleteBuildRun);
  }


  public BuildSequence getCurrentlyRunningStep() {
    return currentlyRunningStep;
  }


  /**
   * Sets currently running step build run by copying it.
   *
   * @param currentlyRunningStep
   */
  public void setCurrentlyRunningStep(final BuildSequence currentlyRunningStep) {
    if (currentlyRunningStep != null) {
      this.currentlyRunningStep = new BuildSequence(currentlyRunningStep);
    }
  }


  /**
   * Sets build host the build is currently running on.
   *
   * @param buildHost to set
   */
  public void setCurrentlyRunningOnBuildHost(final String buildHost) {
    currentlyRunningOnBuildHost = buildHost;
  }


  /**
   * Returns build host the build is currently running on or null
   * if not running or host is not set.
   */
  public String getCurrentlyRunningOnHost() {
    return currentlyRunningOnBuildHost;
  }


  /**
   * @return currently running build number.
   */
  public int getCurrentlyRunningBuildNumber() {
    return currentlyRunningBuildNumber;
  }


  public void setCurrentlyRunningBuildNumber(final int currentlyRunningBuildNumber) {
    this.currentlyRunningBuildNumber = currentlyRunningBuildNumber;
  }


  /**
   * @return currently running build number.
   */
  public String getCurrentlyRunningChangeListNumber() {
    return currentlyRunningChangeListNumber;
  }


  public void setCurrentlyRunningChangeListNumber(final String currentlyRunningChangeListNumber) {
    this.currentlyRunningChangeListNumber = currentlyRunningChangeListNumber;
  }


  public String toString() {
    return "BuildRunnerStateImpl{" +
      "currentlyRunningBuildRunID=" + currentlyRunningBuildRunID +
      ", currentlyRunningBuildConfigID=" + currentlyRunningBuildConfigID +
      ", lastCleanBuildRun=" + lastCleanBuildRun +
      ", lastCompleteBuildRun=" + lastCompleteBuildRun +
      ", currentlyRunningStep=" + currentlyRunningStep +
      ", currentlyRunningOnBuildHost='" + currentlyRunningOnBuildHost + '\'' +
      ", currentlyRunningChangeListNumber='" + currentlyRunningChangeListNumber + '\'' +
      ", currentlyRunningBuildNumber=" + currentlyRunningBuildNumber +
      '}';
  }
}
