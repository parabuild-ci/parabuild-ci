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
 * Build run participant
 *
 * @hibernate.class table="BUILD_RUN_PARTICIPANT"
 * dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class BuildRunParticipant implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 3095136277247820792L; // NOPMD

  private int participantID = -1;
  private int buildRunID = -1;
  private int changeListID = -1;
  private int firstBuildRunID = -1;
  private int firstBuildRunNumber = -1;


  /**
   * Returns change list ID
   *
   * @return int
   *
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getParticipantID() {
    return participantID;
  }


  public void setParticipantID(final int participantID) {
    this.participantID = participantID;
  }


  /**
   * Returns build ID
   *
   * @return int
   *
   * @hibernate.property column = "BUILD_RUN_ID" unique="false"
   * null="false"
   */
  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  /**
   * Returns build ID
   *
   * @return int
   *
   * @hibernate.property column = "CHANGELIST_ID" unique="false"
   * null="false"
   */
  public int getChangeListID() {
    return changeListID;
  }


  public void setChangeListID(final int changeListID) {
    this.changeListID = changeListID;
  }


  /**
   * Returns first build ID
   *
   * @return int
   *
   * @hibernate.property column = "FIRST_BUILD_RUN_ID" unique="false"
   * null="false"
   */
  public int getFirstBuildRunID() {
    return firstBuildRunID;
  }


  public void setFirstBuildRunID(final int firstBuildRunID) {
    this.firstBuildRunID = firstBuildRunID;
  }


  /**
   * Returns first build number
   *
   * @return int
   *
   * @hibernate.property column = "FIRST_BUILD_RUN_NUMBER" unique="false"
   * null="false"
   */
  public int getFirstBuildRunNumber() {
    return firstBuildRunNumber;
  }


  public void setFirstBuildRunNumber(final int firstBuildRunNumber) {
    this.firstBuildRunNumber = firstBuildRunNumber;
  }


  public String toString() {
    return "BuildRunParticipant{" +
      "participantID=" + participantID +
      ", buildRunID=" + buildRunID +
      ", changeListID=" + changeListID +
      ", firstBuildRunID=" + firstBuildRunID +
      ", firstBuildRunNumber=" + firstBuildRunNumber +
      '}';
  }
}

