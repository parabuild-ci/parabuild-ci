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
 * Member of the merge queue. Merge queue is a header
 * of a group of a branch change list pending merge.
 *
 * @hibernate.class table="MERGE_CHANGELIST" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class MergeChangeList implements ObjectConstants, Serializable {

  private static final long serialVersionUID = 6183009882187891947L;

  public static final byte RESULT_SUCCESS = 0;
  public static final byte RESULT_NOT_MERGED = 1;
  public static final byte RESULT_NOTHING_TO_MERGE = 2;
  public static final byte RESULT_CONFLICTS = 3;


  private byte resultCode = RESULT_NOT_MERGED;
  private int branchChangeListID = BranchChangeList.UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private int mergeID = Merge.UNSAVED_ID;
  private String mergeResultDescription = "";


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
   * @hibernate.property column="MERGE_ID"
   *  unique="false" null="false"
   */
  public int getMergeID() {
    return mergeID;
  }


  public void setMergeID(final int mergeID) {
    this.mergeID = mergeID;
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
   * @hibernate.property column="RESULT_CODE"
   *  unique="false" null="false"
   */
  public byte getResultCode() {
    return resultCode;
  }


  public void setResultCode(final byte mergeResultCode) {
    this.resultCode = mergeResultCode;
  }


  /**
   * @hibernate.property column="RESULT_DESCRIPTION" 
   *  unique="false" null="false"
   */
  public String getMergeResultDescription() {
    return mergeResultDescription;
  }


  public void setMergeResultDescription(final String mergeResultDescription) {
    this.mergeResultDescription = mergeResultDescription;
  }


  public String toString() {
    return "MergeChangeList{" +
      "resultCode=" + resultCode +
      ", branchChangeListID=" + branchChangeListID +
      ", ID=" + ID +
      ", mergeID=" + mergeID +
      ", mergeResultDescription='" + mergeResultDescription + '\'' +
      '}';
  }
}
