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
 * Build run result
 *
 * @hibernate.class table="STEP_RUN" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class StepRun implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -7050766001138645007L; // NOPMD

  private Date finishedAt = null;
  private Date startedAt = null;
  private int buildRunID = -1;
  private int duration = 0;
  private byte resultID = 0;
  private int ID = -1;
  private long timeStamp = 0;
  private String name = null;
  private String resultDescription = null;


  /**
   * Returns build ID
   *
   * @return int
   *
   * @hibernate.property column="BUILD_RUN_ID" unique="false"
   * null="false"
   */
  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  /**
   * Returns build run ID
   *
   * @return int
   *
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
   * Name of the sequence run
   *
   * @return String
   *
   * @hibernate.property column="NAME" unique="false"
   * null="true"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Returns run result ID
   *
   * @return int
   *
   * @hibernate.property column="RESULT" unique="false"
   * null="true"
   */
  public byte getResultID() {
    return resultID;
  }


  public void setResultID(final byte resultID) {
    this.resultID = resultID;
  }


  /**
   * Returns completion state
   *
   * @return int
   *
   * @hibernate.property column="RESULT_DESCRIPTION"
   * unique="false" null="true"
   */
  public String getResultDescription() {
    return resultDescription;
  }


  public void setResultDescription(final String resultDescription) {
    this.resultDescription = resultDescription;
  }


  /**
   * Returns finish date
   *
   * @return Date
   *
   * @hibernate.property column="FINISHED_AT" unique="false"
   * null="true"
   */
  public Date getFinishedAt() {
    return finishedAt;
  }


  public void setFinishedAt(final Date finishedAt) {
    this.finishedAt = finishedAt;
    adjustDuration();
  }


  /**
   * Returns start date
   *
   * @return Date
   *
   * @hibernate.property column="STARTED_AT" unique="false"
   * null="true"
   */
  public Date getStartedAt() {
    return startedAt;
  }


  public void setStartedAt(final Date startedAt) {
    this.startedAt = startedAt;
    adjustDuration();
  }


  /**
   * Returns duration in seconds
   *
   * @return int
   */
  public int getDuration() {
    return duration;
  }


  /**
   * Returns timestamp
   *
   * @return long
   *
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  public boolean isComplete() {
    return startedAt != null && finishedAt != null;
  }


  public boolean isSuccessful() {
    return resultID == BuildRun.BUILD_RESULT_SUCCESS;
  }


  /**
   * Helper method
   */
  private void adjustDuration() {
    if (finishedAt != null && startedAt != null) {
      duration = (int)((finishedAt.getTime() - startedAt.getTime()) / 1000);
    }
  }


  public String toString() {
    return "StepRun{" +
      "finishedAt=" + finishedAt +
      ", startedAt=" + startedAt +
      ", buildRunID=" + buildRunID +
      ", duration=" + duration +
      ", resultID=" + resultID +
      ", ID=" + ID +
      ", timeStamp=" + timeStamp +
      ", name='" + name + '\'' +
      ", resultDescription='" + resultDescription + '\'' +
      '}';
  }
}
