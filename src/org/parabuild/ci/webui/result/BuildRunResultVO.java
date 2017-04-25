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
package org.parabuild.ci.webui.result;

import java.io.*;
import java.util.*;

import org.parabuild.ci.object.*;

/**
 * This value object holds build run's result in a format
 * suitable in displaying and publishing to result groups.
 */
public final class BuildRunResultVO implements Serializable {

  private static final long serialVersionUID = 7805262279482097813L;

  private int activeBuildID = BuildConfig.UNSAVED_ID;
  private int buildRunID = BuildRun.UNSAVED_ID;
  private int publisherBuildRunID = BuildRun.UNSAVED_ID;
  private int buildRunNumber = 0;
  private Date buildDate = null;
  private String buildName = null;
  private StepResult stepResult = null;


  public int getActiveBuildID() {
    return activeBuildID;
  }


  public void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  public Date getBuildDate() {
    return buildDate;
  }


  public void setBuildDate(final Date buildDate) {
    this.buildDate = buildDate;
  }


  public String getBuildName() {
    return buildName;
  }


  public void setBuildName(final String buildName) {
    this.buildName = buildName;
  }


  public StepResult getStepResult() {
    return stepResult;
  }


  public void setStepResult(final StepResult stepResult) {
    this.stepResult = stepResult;
  }


  public int getBuildRunNumber() {
    return buildRunNumber;
  }


  public void setBuildRunNumber(final int buildRunNumber) {
    this.buildRunNumber = buildRunNumber;
  }


  public int getPublisherBuildRunID() {
    return publisherBuildRunID;
  }


  public void setPublisherBuildRunID(final int publisherBuildRunID) {
    this.publisherBuildRunID = publisherBuildRunID;
  }


  public String toString() {
    return "BuildRunResultVO{" +
      "activeBuildID=" + activeBuildID +
      ", buildRunID=" + buildRunID +
      ", publisherBuildRunID=" + publisherBuildRunID +
      ", buildRunNumber=" + buildRunNumber +
      ", buildDate=" + buildDate +
      ", buildName='" + buildName + '\'' +
      ", stepResult=" + stepResult +
      '}';
  }
}
