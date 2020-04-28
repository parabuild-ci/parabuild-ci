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

/**
 * Reference to hold a test case that was run as a part of a build run.
 *
 * @hibernate.class table="BUILD_RUN_TEST" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class BuildRunTest implements Serializable, ObjectConstants {

  public static final byte RESULT_UNKNWON = 0;
  public static final byte RESULT_SUCCESS = 1;
  public static final byte RESULT_FAILURE = 2;
  public static final byte RESULT_ERROR = 3;
  public static final byte RESULT_SKIPPED = 4;


  private static final long serialVersionUID = 136142447371206393L; // NOPMD

  private int ID = UNSAVED_ID;
  private int testCaseNameID = TestCaseName.UNSAVED_ID;
  private int buildRunID = BuildRun.UNSAVED_ID;
  private short resultCode;
  private long durationMillis;
  private boolean newFailure;
  private boolean newTest;
  private boolean broken;
  private boolean fix;
  private int brokenBuildRunCount;
  private int brokenSinceBuildRunID = BuildRun.UNSAVED_ID;
  private String message;

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
   * Returns package ID
   *
   * @hibernate.property column="TEST_CASE_ID" unique="false"
   * null="false"
   */
  public int getTestCaseNameID() {
    return testCaseNameID;
  }

  public void setTestCaseNameID(final int testCaseNameID) {
    this.testCaseNameID = testCaseNameID;
  }

  /**
   * @hibernate.property column = "BUILD_RUN_ID" unique="true"
   * null="false"
   */
  public int getBuildRunID() {
    return buildRunID;
  }

  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }

  /**
   * @hibernate.property column = "RESULT_CODE" unique="true"
   * null="false"
   */
  public short getResultCode() {
    return resultCode;
  }

  public void setResultCode(final short resultCode) {
    this.resultCode = resultCode;
  }

  /**
   * @hibernate.property column = "DURATION_MILLS" unique="true"
   * null="false"
   */
  public long getDurationMillis() {
    return durationMillis;
  }

  public void setDurationMillis(final long durationMillis) {
    this.durationMillis = durationMillis;
  }

  /**
   * @hibernate.property column = "NEW_FAILURE" unique="true" type="yes_no"
   * null="false"
   */
  public boolean isNewFailure() {
    return newFailure;
  }

  public void setNewFailure(final boolean newFailure) {
    this.newFailure = newFailure;
  }

  /**
   * @hibernate.property column = "NEW" unique="true" type="yes_no"
   * null="false"
   */
  public boolean isNewTest() {
    return newTest;
  }

  public void setNewTest(final boolean newTest) {
    this.newTest = newTest;
  }

  /**
   * @hibernate.property column = "FIX" unique="true" type="yes_no"
   * null="false"
   */
  public boolean isFix() {
    return fix;
  }

  public void setFix(final boolean fix) {
    this.fix = fix;
  }

  /**
   * @hibernate.property column = "BROKEN" unique="true" type="yes_no"
   * null="false"
   */
  public boolean isBroken() {
    return broken;
  }

  public void setBroken(final boolean broken) {
    this.broken = broken;
  }

  /**
   * @hibernate.property column = "BROKEN_BUILD_RUN_COUNT" unique="true"
   * null="false"
   */
  public int getBrokenBuildRunCount() {
    return brokenBuildRunCount;
  }

  public void setBrokenBuildRunCount(final int brokenBuildRunCount) {
    this.brokenBuildRunCount = brokenBuildRunCount;
  }

  /**
   * @hibernate.property column = "BROKEN_SINCE_BUILD_RUN_ID" unique="true"
   * null="false"
   */
  public int getBrokenSinceBuildRunID() {
    return brokenSinceBuildRunID;
  }

  public void setBrokenSinceBuildRunID(final int brokenSinceBuildRunID) {
    this.brokenSinceBuildRunID = brokenSinceBuildRunID;
  }

  /**
   * @hibernate.property column = "MESSAGE" unique="true"
   * null="false"
   */
  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }


  public String toString() {
    return "BuildRunTest{" +
            "ID=" + ID +
            ", testCaseNameID=" + testCaseNameID +
            ", buildRunID=" + buildRunID +
            ", resultCode=" + resultCode +
            ", durationMillis=" + durationMillis +
            ", newInThisBuildRun=" + newFailure +
            ", broken=" + broken +
            ", brokenBuildRunCount=" + brokenBuildRunCount +
            ", brokenSinceBuildRunID=" + brokenSinceBuildRunID +
            ", message='" + message + '\'' +
            '}';
  }
}
