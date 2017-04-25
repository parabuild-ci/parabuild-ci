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
 * Stored build properties
 *
 * @hibernate.class table="USER_ATTRIBUTE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class UserProperty implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -6426274907331328221L; // NOPMD

  public static final String AUTOCREATE_CREATE_BUILDER_FORAGENT = "autocreate.builder.for.agent";
  public static final String BUILD_STATUS_REFRESH_RATE = "refresh.rate";
  public static final String DASHBOARD_ROW_SIZE = "dashboard.row.size";
  public static final String DEFAULT_DISPLAY_GROUP = "default.display.group";
  public static final String FAILED_BUILD_COLOR = "red.color";
  public static final String IM_SEND_FAILURES = "im.send.failures";
  public static final String IM_SEND_SUCCESSES = "im.send.successes";
  public static final String IM_SEND_SYSTEM_ERRORS = "im.send.system.errs";
  public static final String MAX_RECENT_BUILDS = "max.recent.builds";
  public static final String REMEMBER_ME = "remember.me";
  public static final String SHOW_INACTIVE_BUILDS = "show.inactive.builds";
  public static final String SUCCESSFUL_BUILD_COLOR = "green.color";
  public static final String TAIL_WINDOW_SIZE = "tail.window.size";

  private int userID = User.UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private String name = null;
  private String value = null;
  private long timestamp = 1;


  /**
   * Default constructor. Required by hibernate.
   */
  public UserProperty() {
  }


  /**
   * Constructor.
   *
   * @param userID
   * @param name
   */
  public UserProperty(final int userID, final String name, final String value) {
    this.userID = userID;
    this.name = name;
    this.value = value;
  }


  /**
   * Returns build ID
   *
   * @return String
   *
   * @hibernate.property column="USER_ID" unique="false"
   * null="false"
   */
  public int getUserID() {
    return userID;
  }


  public void setUserID(final int userID) {
    this.userID = userID;
  }


  /**
   * The getter method for this property ID generator-parameter-1="SEQUENCE_GENERATOR"
   * generator-parameter-2="SEQUENCE_ID"
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
   * Returns property name
   *
   * @return String
   *
   * @hibernate.property column="NAME" unique="true"
   * null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Returns property value
   *
   * @return String
   *
   * @hibernate.property column="VALUE" unique="true"
   * null="false"
   */
  public String getValue() {
    return value;
  }


  public void setValue(final String value) {
    this.value = value;
  }


  public void setValue(final int propertyValue) {
    this.value = Integer.toString(propertyValue);
  }


  /**
   * Returns property value as int
   *
   * @return int property value
   */
  public int getValueAsInteger() throws NumberFormatException {
    return Integer.parseInt(value);
  }


  /**
   * Returns timestamp
   *
   * @return long
   *
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimestamp() {
    return timestamp;
  }


  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }


  public String toString() {
    return "UserProperty{" +
      "userID=" + userID +
      ", ID=" + ID +
      ", name='" + name + '\'' +
      ", value='" + value + '\'' +
      ", timestamp=" + timestamp +
      '}';
  }
}

