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
public final class TestStatistics {

  private int activeBuildID;
  private Calendar sampleTime;
  private byte testCode;
  private int successfulTestCount;
  private int failedTestCount;
  private int totalTestCount;
  private int errorTestCount;
  private int failedTestPercent;
  private int successfulTestPercent;
  private int errorTestPercent;
  private int buildCount;


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


  public byte getTestCode() {
    return testCode;
  }


  public void setTestCode(final byte testCode) {
    this.testCode = testCode;
  }


  public int getSuccessfulTestCount() {
    return successfulTestCount;
  }


  public void setSuccessfulTestCount(final int successfulTestCount) {
    this.successfulTestCount = successfulTestCount;
  }


  public int getFailedTestCount() {
    return failedTestCount;
  }


  public void setFailedTestCount(final int failedTestCount) {
    this.failedTestCount = failedTestCount;
  }


  public int getTotalTestCount() {
    return totalTestCount;
  }


  public void setTotalTestCount(final int totalTestCount) {
    this.totalTestCount = totalTestCount;
  }


  public int getErrorTestCount() {
    return errorTestCount;
  }


  public void setErrorTestCount(final int errorTestCount) {
    this.errorTestCount = errorTestCount;
  }


  public int getFailedTestPercent() {
    return failedTestPercent;
  }


  public void setFailedTestPercent(final int failedTestPercent) {
    this.failedTestPercent = failedTestPercent;
  }


  public int getSuccessfulTestPercent() {
    return successfulTestPercent;
  }


  public void setSuccessfulTestPercent(final int successfulTestPercent) {
    this.successfulTestPercent = successfulTestPercent;
  }


  public int getErrorTestPercent() {
    return errorTestPercent;
  }


  public void setErrorTestPercent(final int errorTestPercent) {
    this.errorTestPercent = errorTestPercent;
  }


  public int getBuildCount() {
    return buildCount;
  }


  public void setBuildCount(final int buildCount) {
    this.buildCount = buildCount;
  }
}