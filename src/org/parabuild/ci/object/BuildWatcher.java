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
 * Build watcher
 *
 * @hibernate.class table="BUILD_WATCHER" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class BuildWatcher implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 1202447165126030920L; // NOPMD

  public static final byte LEVEL_SYSTEM_ERROR = 0; // max messages
  public static final byte LEVEL_SUCCESS = 1; // success and broken
  public static final byte LEVEL_BROKEN = 2; // broken only


  private int buildID = BuildConfig.UNSAVED_ID;
  private int watcherID = UNSAVED_ID;
  private byte instantMessengerType = IM_TYPE_NONE;
  private byte level = LEVEL_BROKEN; // default devel is broken
  private String email = null;
  private String instantMessengerAddress = null;
  private long timeStamp = 1;
  private boolean disabled = false;


  /**
   * Default constructor required by hibernate.
   */
  public BuildWatcher() {
  }


  /**
   * Constrcutor.
   *
   * @param email
   * @param level
   */
  public BuildWatcher(final String email, final byte level) {
    this.level = level;
    this.email = email;
  }


  /**
   * Returns build ID
   *
   * @hibernate.property column="BUILD_ID" unique="false"
   * null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
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
  public int getWatcherID() {
    return watcherID;
  }


  public void setWatcherID(final int watcherID) {
    this.watcherID = watcherID;
  }


  /**
   * @hibernate.property column="EMAIL" unique="true"
   * null="false"
   */
  public String getEmail() {
    return email;
  }


  public void setEmail(final String email) {
    this.email = email;
  }


  /**
   * Returns property name
   *
   * @hibernate.property column="LEVEL" unique="true"
   * null="false"
   */
  public byte getLevel() {
    return level;
  }


  public void setLevel(final byte level) {
    this.level = level;
  }


  /**
   * Returns build name
   *
   * @hibernate.property column="DISABLED" unique="false"
   * type="yes_no" null="false"
   */
  public boolean getDisabled() {
    return disabled;
  }


  public void setDisabled(final boolean disabled) {
    this.disabled = disabled;
  }


  /**
   * Returns IM address
   *
   * @hibernate.property column="IM_ADDRESS" unique="false"
   * null="true"
   */
  public String getInstantMessengerAddress() {
    return instantMessengerAddress;
  }


  public void setInstantMessengerAddress(final String instantMessengerAddress) {
    this.instantMessengerAddress = instantMessengerAddress;
  }


  /**
   * Returns IM name
   *
   * @hibernate.property column="IM_TYPE" unique="false"
   * null="false"
   */
  public byte getInstantMessengerType() {
    return instantMessengerType;
  }


  public void setInstantMessengerType(final byte instantMessengerType) {
    this.instantMessengerType = instantMessengerType;
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
    return "BuildWatcher{" +
      "buildID=" + buildID +
      ", watcherID=" + watcherID +
      ", instantMessengerType=" + instantMessengerType +
      ", level=" + level +
      ", email='" + email + '\'' +
      ", instantMessengerAddress='" + instantMessengerAddress + '\'' +
      ", timeStamp=" + timeStamp +
      ", disabled=" + disabled +
      '}';
  }
}
