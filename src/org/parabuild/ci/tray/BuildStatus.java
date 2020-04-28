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
package org.parabuild.ci.tray;

/**
 * BuildStatus is a value objects that is returned by Hessian
 * tray status webservice.
 */
public final class BuildStatus {

  private static final int UNDEFINED_VALUE = org.parabuild.ci.build.BuildStatus.UNDEFINED_VALUE;
  private static final int INACTIVE_VALUE = org.parabuild.ci.build.BuildStatus.INACTIVE_VALUE;

  private static final int UNSET_ID = -1;

  private byte buildStatusID = UNDEFINED_VALUE;
  private byte lastBuildRunResultID = UNSET_ID;
  private int activeBuildID = UNSET_ID;
  private int currentlyRunnigBuildRunID = UNSET_ID;
  private int currentlyRunningBuildConfigID = UNSET_ID;
  private int currentlyRunningStepID = UNSET_ID;
  private int lastBuildRunID = UNSET_ID;
  private int lastCleanBuildRunID = UNSET_ID;
  private int lastCompleteBuildRunID = UNSET_ID;
  private byte schedule;
  private byte sourceControl;
  private String buildName;
  private String currentlyRunningStepName;
  private int lastBuildRunNumber;


  public BuildStatus() { // NOPMD
  }


  public final byte getBuildStatusID() {
    return buildStatusID;
  }


  public final void setBuildStatusID(final byte buildStatusID) {
    this.buildStatusID = buildStatusID;
  }


  public final byte getLastBuildRunResultID() {
    return lastBuildRunResultID;
  }


  public final void setLastBuildRunResultID(final byte lastBuildRunResultID) {
    this.lastBuildRunResultID = lastBuildRunResultID;
  }


  public final int getActiveBuildID() {
    return activeBuildID;
  }


  public final void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  public final int getCurrentlyRunnigBuildRunID() {
    return currentlyRunnigBuildRunID;
  }


  public final void setCurrentlyRunnigBuildRunID(final int currentlyRunnigBuildRunID) {
    this.currentlyRunnigBuildRunID = currentlyRunnigBuildRunID;
  }


  public final int getCurrentlyRunningBuildConfigID() {
    return currentlyRunningBuildConfigID;
  }


  public final void setCurrentlyRunningBuildConfigID(final int currentlyRunningBuildConfigID) {
    this.currentlyRunningBuildConfigID = currentlyRunningBuildConfigID;
  }


  public final int getCurrentlyRunningStepID() {
    return currentlyRunningStepID;
  }


  public final void setCurrentlyRunningStepID(final int currentlyRunningStepID) {
    this.currentlyRunningStepID = currentlyRunningStepID;
  }


  public final int getLastBuildRunID() {
    return lastBuildRunID;
  }


  public final void setLastBuildRunID(final int lastBuildRunID) {
    this.lastBuildRunID = lastBuildRunID;
  }


  public final int getLastCleanBuildRunID() {
    return lastCleanBuildRunID;
  }


  public final void setLastCleanBuildRunID(final int lastCleanBuildRunID) {
    this.lastCleanBuildRunID = lastCleanBuildRunID;
  }


  public final int getLastCompleteBuildRunID() {
    return lastCompleteBuildRunID;
  }


  public final void setLastCompleteBuildRunID(final int lastCompleteBuildRunID) {
    this.lastCompleteBuildRunID = lastCompleteBuildRunID;
  }


  public final int getSchedule() {
    return schedule;
  }


  public final void setSchedule(final byte schedule) {
    this.schedule = schedule;
  }


  public final int getSourceControl() {
    return sourceControl;
  }


  public final void setSourceControl(final byte sourceControl) {
    this.sourceControl = sourceControl;
  }


  public final String getBuildName() {
    return buildName;
  }


  public final void setBuildName(final String buildName) {
    this.buildName = buildName;
  }


  public final String getCurrentlyRunningStepName() {
    return currentlyRunningStepName;
  }


  public final void setCurrentlyRunningStepName(final String currentlyRunningStepName) {
    this.currentlyRunningStepName = currentlyRunningStepName;
  }


  public final int getLastBuildRunNumber() {
    return lastBuildRunNumber;
  }


  public final void setLastBuildRunNumber(final int lastBuildRunNumber) {
    this.lastBuildRunNumber = lastBuildRunNumber;
  }


  public final boolean isInactive() {
    return buildStatusID == INACTIVE_VALUE;
  }


  public String toString() {
    return "BuildStatus{" +
      "buildStatusID=" + buildStatusID +
      ", lastBuildRunResultID=" + lastBuildRunResultID +
      ", activeBuildID=" + activeBuildID +
      ", currentlyRunnigBuildRunID=" + currentlyRunnigBuildRunID +
      ", currentlyRunningBuildConfigID=" + currentlyRunningBuildConfigID +
      ", currentlyRunningStepID=" + currentlyRunningStepID +
      ", lastBuildRunID=" + lastBuildRunID +
      ", lastCleanBuildRunID=" + lastCleanBuildRunID +
      ", lastCompleteBuildRunID=" + lastCompleteBuildRunID +
      ", schedule=" + schedule +
      ", sourceControl=" + sourceControl +
      ", buildName='" + buildName + '\'' +
      ", currentlyRunningStepName='" + currentlyRunningStepName + '\'' +
      ", lastBuildRunNumber=" + lastBuildRunNumber +
      '}';
  }
}
