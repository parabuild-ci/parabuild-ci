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
package org.parabuild.ci.object;

import java.io.Serializable;
import java.util.Date;

/**
 * Daily statistics.
 *
 * @hibernate.class table="HOURLY_TEST_STATS" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class HourlyTestStats implements Serializable, ObjectConstants, PersistentTestStats {

  private static final long serialVersionUID = -5907298521657059063L; // NOPMD

  private int ID = UNSAVED_ID;
  private int activeBuildID = UNSAVED_ID;
  private Date sampleTime;
  private byte testCode;
  private int successfulTestCount;
  private int failedTestCount;
  private int totalTestCount;
  private int errorTestCount;
  private int failedTestPercent;
  private int successfulTestPercent;
  private int errorTestPercent;
  private int buildCount;


  /**
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * @hibernate.property column="ACTIVE_BUILD_ID" unique="false"
   * null="false"
   */
  public int getActiveBuildID() {
    return activeBuildID;
  }


  public void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  /**
   * @hibernate.property column="SAMPLE_TIME" unique="false"
   * null="false"
   */
  public Date getSampleTime() {
    return sampleTime;
  }


  public void setSampleTime(final Date sampleTime) {
    this.sampleTime = sampleTime;
  }


  /**
   * @hibernate.property column="SUCCESSFUL_TEST_COUNT"
   * unique="false" null="false"
   */
  public int getSuccessfulTestCount() {
    return successfulTestCount;
  }


  public void setSuccessfulTestCount(final int successfulTestCount) {
    this.successfulTestCount = successfulTestCount;
  }


  /**
   * @hibernate.property column="SUCCESSFUL_TEST_PERCENT"
   * unique="false" null="false"
   */
  public int getSuccessfulTestPercent() {
    return successfulTestPercent;
  }


  public void setSuccessfulTestPercent(final int successfulTestPercent) {
    this.successfulTestPercent = successfulTestPercent;
  }


  /**
   * @hibernate.property column="FAILED_TEST_COUNT"
   * unique="false" null="false"
   */
  public int getFailedTestCount() {
    return failedTestCount;
  }


  public void setFailedTestCount(final int failedTestCount) {
    this.failedTestCount = failedTestCount;
  }


  /**
   * @hibernate.property column="TOTAL_TEST_COUNT"
   * unique="false" null="false"
   */
  public int getTotalTestCount() {
    return totalTestCount;
  }


  public void setTotalTestCount(final int totalTestCount) {
    this.totalTestCount = totalTestCount;
  }


  /**
   * @hibernate.property column="FAILED_TEST_PERCENT"
   * unique="false" null="false"
   */
  public int getFailedTestPercent() {
    return failedTestPercent;
  }


  public void setFailedTestPercent(final int failedTestPercent) {
    this.failedTestPercent = failedTestPercent;
  }


  /**
   * @hibernate.property column="TEST_CODE"
   * unique="false" null="false"
   */
  public byte getTestCode() {
    return testCode;
  }


  public void setTestCode(final byte testCode) {
    this.testCode = testCode;
  }


  /**
   * @hibernate.property column="ERROR_TEST_COUNT"
   * unique="false" null="false"
   */
  public int getErrorTestCount() {
    return errorTestCount;
  }


  public void setErrorTestCount(final int errorTestCount) {
    this.errorTestCount = errorTestCount;
  }


  /**
   * @hibernate.property column="ERROR_TEST_PERCENT"
   * unique="false" null="false"
   */
  public int getErrorTestPercent() {
    return errorTestPercent;
  }


  public void setErrorTestPercent(final int errorTestPercent) {
    this.errorTestPercent = errorTestPercent;
  }


  /**
   * @hibernate.property column="BUILD_COUNT"
   * unique="false" null="false"
   */
  public int getBuildCount() {
    return buildCount;
  }


  public void setBuildCount(final int buildCount) {
    this.buildCount = buildCount;
  }


  public String toString() {
    return "HourlyTestStats{" +
      "ID=" + ID +
      ", activeBuildID=" + activeBuildID +
      ", sampleTime=" + sampleTime +
      ", testCode=" + testCode +
      ", successfulTestCount=" + successfulTestCount +
      ", failedTestCount=" + failedTestCount +
      ", totalTestCount=" + totalTestCount +
      ", errorTestCount=" + errorTestCount +
      ", failedTestPercent=" + failedTestPercent +
      ", successfulTestPercent=" + successfulTestPercent +
      ", errorTestPercent=" + errorTestPercent +
      ", buildCount=" + buildCount +
      '}';
  }
}
