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
import java.util.*;

/**
 * BT user
 *
 * @hibernate.class table="RESULT_GROUP" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ResultGroup implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -5237501290837750448L; // NOPMD

  private int ID = UNSAVED_ID;
  private boolean deleted = false;
  private boolean enabled = true;
  private Date lastPublished = null;
  private long timeStamp = 0;
  private String description = "";
  private String name = null;


  /**
   * The getter method for this ID
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
   * @hibernate.property column = "NAME" unique="false"
   * null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @hibernate.property column = "DESCRIPTION" unique="false"
   * null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
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


  /**
   * @return String password
   *
   * @hibernate.property column="LAST_PUBLISHED" null="true"
   */
  public Date getLastPublished() {
    return lastPublished;
  }


  public void setLastPublished(final Date lastPublished) {
    this.lastPublished = lastPublished;
  }


  /**
   * Returns true if this user is enabled
   *
   * @return String
   *
   * @hibernate.property column="ENABLED"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isEnabled() {
    return enabled;
  }


  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Returns true if this user is enabled
   *
   * @return String
   *
   * @hibernate.property column="DELETED"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isDeleted() {
    return deleted;
  }


  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  public String toString() {
    return "ResultGroup{" +
      "ID=" + ID +
      ", deleted=" + deleted +
      ", enabled=" + enabled +
      ", lastPublished=" + lastPublished +
      ", timeStamp=" + timeStamp +
      ", description='" + description + '\'' +
      ", name='" + name + '\'' +
      '}';
  }
}
