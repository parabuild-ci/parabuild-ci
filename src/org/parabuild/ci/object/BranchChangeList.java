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
 * Branch change list describes a change list in a brach.
 *
 * All branch change lists carry infromation about
 * MergeConfiguration that was used to find a given branch
 * change list. This allows to continue the merge process,
 * if necessary, if user changes the merge configuration.
 *
 * BranchChangeList may or maybe not subject of merging.
 *
 * @hibernate.class table="BRANCH_CHANGELIST" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class BranchChangeList implements ObjectConstants, Serializable {

  private static final long serialVersionUID = 3664492072273519425L;

  public static final byte MERGE_STATUS_NOT_MERGED = 0;
  public static final byte MERGE_STATUS_MERGED = 1;
  public static final byte MERGE_STATUS_UNKNOWN = 2;

  private int ID = UNSAVED_ID;
  private int mergeConfigurationID = MergeConfiguration.UNSAVED_ID;
  private int changeListID = ChangeList.UNSAVED_ID;
  private byte mergeStatus = MERGE_STATUS_NOT_MERGED;


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
   * @hibernate.property column="MERGE_CONFIGURATION_ID" unique="false"
   * null="false"
   */
  public int getMergeConfigurationID() {
    return mergeConfigurationID;
  }


  public void setMergeConfigurationID(final int mergeConfigurationID) {
    this.mergeConfigurationID = mergeConfigurationID;
  }


  /**
   * @hibernate.property column="CHANGELIST_ID" unique="false"
   * null="false"
   */
  public int getChangeListID() {
    return changeListID;
  }


  public void setChangeListID(final int changeListID) {
    this.changeListID = changeListID;
  }


  /**
   * @hibernate.property column="MERGE_STATUS" unique="false"
   * null="false"
   */
  public byte getMergeStatus() {
    return mergeStatus;
  }


  public void setMergeStatus(final byte mergeStatus) {
    this.mergeStatus = mergeStatus;
  }


  public String toString() {
    return "BranchChangeList{" +
      "ID=" + ID +
      ", mergeConfigurationID=" + mergeConfigurationID +
      ", changeListID=" + changeListID +
      ", mergeStatus=" + mergeStatus +
      '}';
  }
}
