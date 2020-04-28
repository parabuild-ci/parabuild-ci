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
public final class BuildStatistics {

  private int ID;
  private int activeBuildID;
  private Calendar sampleTime;
  private int successfulBuildCount;
  private int failedBuildCount;
  private int totalBuildCount;
  private int changeListCount;
  private int issueCount;
  private int failedBuildPercent;
  private int successfulBuildPercent;


  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  public int getActiveBuildID() {
    return activeBuildID;
  }


  public void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  public Calendar getSampleTime() {
    return sampleTime;
  }


  public void setSampleTime(final Calendar sampleTime) {
    this.sampleTime = sampleTime;
  }


  public int getSuccessfulBuildCount() {
    return successfulBuildCount;
  }


  public void setSuccessfulBuildCount(final int successfulBuildCount) {
    this.successfulBuildCount = successfulBuildCount;
  }


  public int getFailedBuildCount() {
    return failedBuildCount;
  }


  public void setFailedBuildCount(final int failedBuildCount) {
    this.failedBuildCount = failedBuildCount;
  }


  public int getTotalBuildCount() {
    return totalBuildCount;
  }


  public void setTotalBuildCount(final int totalBuildCount) {
    this.totalBuildCount = totalBuildCount;
  }


  public int getChangeListCount() {
    return changeListCount;
  }


  public void setChangeListCount(final int changeListCount) {
    this.changeListCount = changeListCount;
  }


  public int getIssueCount() {
    return issueCount;
  }


  public void setIssueCount(final int issueCount) {
    this.issueCount = issueCount;
  }


  public int getFailedBuildPercent() {
    return failedBuildPercent;
  }


  public void setFailedBuildPercent(final int failedBuildPercent) {
    this.failedBuildPercent = failedBuildPercent;
  }


  public int getSuccessfulBuildPercent() {
    return successfulBuildPercent;
  }


  public void setSuccessfulBuildPercent(final int successfulBuildPercent) {
    this.successfulBuildPercent = successfulBuildPercent;
  }
}