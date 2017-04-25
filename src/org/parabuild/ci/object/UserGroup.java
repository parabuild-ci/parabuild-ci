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
 * User group memebership
 *
 * @hibernate.class table="USER_GROUP" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class UserGroup implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -5237501290837750448L; // NOPMD

  private int ID = UNSAVED_ID;
  private int userID = User.UNSAVED_ID;
  private int groupID = Group.UNSAVED_ID;


  public UserGroup() {
  }


  public UserGroup(final int userID, final int groupID) {
    this.userID = userID;
    this.groupID = groupID;
  }


  /**
   * The getter method for member user ID
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
   * The getter method for member user ID
   *
   * @return int
   *
   * @hibernate.property column = "USER_ID" unique="false"
   * null="false"
   */
  public int getUserID() {
    return userID;
  }


  public void setUserID(final int userID) {
    this.userID = userID;
  }


  /**
   * The getter method for a group ID which user is member of
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


  public String toString() {
    return "UserGroup{" +
      "ID=" + ID +
      ", userID=" + userID +
      ", groupID=" + groupID +
      '}';
  }
}
