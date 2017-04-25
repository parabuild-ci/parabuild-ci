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
package org.parabuild.ci.webservice.templ;

import java.util.Calendar;


/**
 */
public final class BuildStatus {

  private int lastCleanBuildRunID = -1;
  private int lastCompleteBuildRun = -1;
  private int currentlyRunningStepID = -1;
  private int status = -1;
  private int activeBuildID = -1;
  private int currentlyRunningBuildConfigID = -1;
  private int currentlyRunnigBuildRunID = -1;
  private int currentlyRunningBuildNumber = 0;
  private byte access = 2;
  private byte schedule = 0;
  private byte sourceControl = 0;
  private String buildName = null;
  private Calendar nextBuildTime = null;
  private String currentlyRunningOnBuildHost = null;
  private String currentlyRunningChangeListNumber = null;


  public int getLastCleanBuildRunID() {
    return lastCleanBuildRunID;
  }


  public void setLastCleanBuildRunID(final int lastCleanBuildRunID) {
    this.lastCleanBuildRunID = lastCleanBuildRunID;
  }


  public int getLastCompleteBuildRun() {
    return lastCompleteBuildRun;
  }


  public void setLastCompleteBuildRun(final int lastCompleteBuildRun) {
    this.lastCompleteBuildRun = lastCompleteBuildRun;
  }


  public int getCurrentlyRunningStepID() {
    return currentlyRunningStepID;
  }


  public void setCurrentlyRunningStepID(final int currentlyRunningStepID) {
    this.currentlyRunningStepID = currentlyRunningStepID;
  }


  public int getStatus() {
    return status;
  }


  public void setStatus(final int status) {
    this.status = status;
  }


  public int getActiveBuildID() {
    return activeBuildID;
  }


  public void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  public int getCurrentlyRunningBuildConfigID() {
    return currentlyRunningBuildConfigID;
  }


  public void setCurrentlyRunningBuildConfigID(final int currentlyRunningBuildConfigID) {
    this.currentlyRunningBuildConfigID = currentlyRunningBuildConfigID;
  }


  public int getCurrentlyRunnigBuildRunID() {
    return currentlyRunnigBuildRunID;
  }


  public void setCurrentlyRunnigBuildRunID(final int currentlyRunnigBuildRunID) {
    this.currentlyRunnigBuildRunID = currentlyRunnigBuildRunID;
  }


  public int getCurrentlyRunningBuildNumber() {
    return currentlyRunningBuildNumber;
  }


  public void setCurrentlyRunningBuildNumber(final int currentlyRunningBuildNumber) {
    this.currentlyRunningBuildNumber = currentlyRunningBuildNumber;
  }


  public byte getAccess() {
    return access;
  }


  public void setAccess(final byte access) {
    this.access = access;
  }


  public byte getSchedule() {
    return schedule;
  }


  public void setSchedule(final byte schedule) {
    this.schedule = schedule;
  }


  public byte getSourceControl() {
    return sourceControl;
  }


  public void setSourceControl(final byte sourceControl) {
    this.sourceControl = sourceControl;
  }


  public String getBuildName() {
    return buildName;
  }


  public void setBuildName(final String buildName) {
    this.buildName = buildName;
  }


  public Calendar getNextBuildTime() {
    return nextBuildTime;
  }


  public void setNextBuildTime(final Calendar nextBuildTime) {
    this.nextBuildTime = nextBuildTime;
  }


  public String getCurrentlyRunningOnBuildHost() {
    return currentlyRunningOnBuildHost;
  }


  public void setCurrentlyRunningOnBuildHost(final String currentlyRunningOnBuildHost) {
    this.currentlyRunningOnBuildHost = currentlyRunningOnBuildHost;
  }


  public String getCurrentlyRunningChangeListNumber() {
    return currentlyRunningChangeListNumber;
  }


  public void setCurrentlyRunningChangeListNumber(final String currentlyRunningChangeListNumber) {
    this.currentlyRunningChangeListNumber = currentlyRunningChangeListNumber;
  }
}