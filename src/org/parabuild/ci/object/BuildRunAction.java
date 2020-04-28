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
 * Build run action log.
 *
 * @hibernate.class table="BUILD_RUN_ACTION" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class BuildRunAction implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -4591276259024162394L; // NOPMD

  public static final byte CODE_PUBLISH_RESULT = 0;

  private int ID = UNSAVED_ID;
  private int buildRunID = UNSAVED_ID;
  private byte code = 0;
  private String action = null;
  private Date date = null;
  private String description = null;
  private int userID = UNSAVED_ID;

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
   * @hibernate.property column="CODE" unique="false"
   * null="false"
   */
  public byte getCode() {
    return code;
  }


  public void setCode(final byte code) {
    this.code = code;
  }


  /**
   * @hibernate.property column="ACTION" unique="false"
   * null="false"
   */
  public String getAction() {
    return action;
  }


  public void setAction(final String action) {
    this.action = action;
  }


  /**
   * @hibernate.property column="ACTION_DATE" unique="false"
   * null="false"
   */
  public Date getDate() {
    return date;
  }


  public void setDate(final Date date) {
    this.date = date;
  }


  /**
   * @hibernate.property column="DESCRIPTION" unique="false"
   * null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @hibernate.property column="USER_ID" unique="false"
   * null="false"
   */
  public int getUserID() {
    return userID;
  }


  public void setUserID(final int userID) {
    this.userID = userID;
  }


  public String toString() {
    return "BuildRunAction{" +
      "ID=" + ID +
      ", buildRunID=" + buildRunID +
      ", code=" + code +
      ", action='" + action + '\'' +
      ", date=" + date +
      ", description='" + description + '\'' +
      ", userID=" + userID +
      '}';
  }
}
