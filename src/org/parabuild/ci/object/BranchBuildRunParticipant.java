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

import java.io.*;

/**
 * Connector between branch change list and [successful] build run paricipant.
 * Source Control change list
 *
 * @hibernate.class table="BRANCH_BUILD_RUN_PARTICIPANT"
 * dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class BranchBuildRunParticipant implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 6417512673580073054L;

  private int ID = UNSAVED_ID;
  private int branchChangeListID = BranchChangeList.UNSAVED_ID;
  private int buildRunParticipantID = BuildRunParticipant.UNSAVED_ID;


  /**
   * Returns change list ID
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
   * @hibernate.property column="BRANCH_CHANGELIST_ID"
   *  unique="false" null="false"
   */
  public int getBranchChangeListID() {
    return branchChangeListID;
  }


  public void setBranchChangeListID(final int branchChangeListID) {
    this.branchChangeListID = branchChangeListID;
  }


  /**
   * @hibernate.property column="BUILD_RUN_PARTICIPANT_ID"
   *  unique="false" null="false"
   */
  public int getBuildRunParticipantID() {
    return buildRunParticipantID;
  }


  public void setBuildRunParticipantID(final int buildRunParticipantID) {
    this.buildRunParticipantID = buildRunParticipantID;
  }


  public String toString() {
    return "BranchBuildRunParticipant{" +
      "ID=" + ID +
      ", branchChangeListID=" + branchChangeListID +
      ", buildRunParticipantID=" + buildRunParticipantID +
      '}';
  }
}
