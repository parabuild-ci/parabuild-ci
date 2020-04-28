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
 * Published result group membership
 *
 * @hibernate.class table="RESULT_GROUP_ACCESS" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ResultGroupAccess implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -5237501290837750448L; // NOPMD

  private int ID = UNSAVED_ID;
  private int resultGroupID = ResultGroup.UNSAVED_ID;
  private int groupID = Group.UNSAVED_ID;
  private long timeStamp;


  public ResultGroupAccess() {
  }


  public ResultGroupAccess(final int groupID, final int resultGroupID) {
    this.resultGroupID = resultGroupID;
    this.groupID = groupID;
  }


  /**
   * The getter method for ID
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
   * The getter method for member published result ID
   *
   * @return int
   *
   * @hibernate.property column = "RESULT_GROUP_ID" unique="false"
   * null="false"
   */
  public int getResultGroupID() {
    return resultGroupID;
  }


  public void setResultGroupID(final int resultGroupID) {
    this.resultGroupID = resultGroupID;
  }


  /**
   * The getter method for a group ID which published result is member of
   *
   * @return int
   *
   * @hibernate.property column = "GROUP_ID" unique="false"
   * null="false"
   */
  public int getGroupID() {
    return groupID;
  }


  public void setGroupID(final int groupID) {
    this.groupID = groupID;
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


  public String toString() {
    return "ResultGroupAccess{" +
      "ID=" + ID +
      ", resultGroupID=" + resultGroupID +
      ", groupID=" + groupID +
      ", timeStamp=" + timeStamp +
      '}';
  }
}
