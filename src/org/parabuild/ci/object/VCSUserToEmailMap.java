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
 * Version control system user to e-mail mapping
 *
 * @hibernate.class table="VCS_USER_TO_EMAIL_MAP" dynamic-update="true"
 */
public final class VCSUserToEmailMap implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 2313249332734749486L; // NOPMD

  private int buildID = BuildConfig.UNSAVED_ID;
  private int mapID = UNSAVED_ID;
  private byte instantMessengerType = IM_TYPE_NONE;
  private String userName = null;
  private String userEmail = null;
  private String instantMessengerAddress = null;
  private long timeStamp = 1;
  private boolean disabled = false;


  /**
   * Returns build ID
   *
   * @return String
   *
   * @hibernate.property column="BUILD_ID" unique="false" null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * The getter method for this property ID generator-parameter-1="SEQUENCE_GENERATOR" generator-parameter-2="SEQUENCE_ID"
   *
   * @return int
   *
   * @hibernate.id generator-class="identity" column="ID" unsaved-value="-1"
   */
  public int getMapID() {
    return mapID;
  }


  public void setMapID(final int mapID) {
    this.mapID = mapID;
  }


  /**
   * Returns property name
   *
   * @return String
   *
   * @hibernate.property column="USER_NAME" unique="true" null="false"
   */
  public String getUserName() {
    return userName;
  }


  public void setUserName(final String userName) {
    this.userName = userName;
  }


  /**
   * Returns build name
   *
   * @return String
   *
   * @hibernate.property column="USER_EMAIL" unique="true" null="false"
   */
  public String getUserEmail() {
    return userEmail;
  }


  public void setUserEmail(final String userEmail) {
    this.userEmail = userEmail;
  }


  /**
   * Returns build name
   *
   * @hibernate.property column="DISABLED" unique="false" type="yes_no" null="false"
   */
  public boolean getDisabled() {
    return disabled;
  }


  public void setDisabled(final boolean disabled) {
    this.disabled = disabled;
  }


  /**
   * Returns IM name
   *
   * @hibernate.property column="IM_ADDRESS" unique="false" null="true"
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
   * @hibernate.property column="IM_TYPE" unique="false" null="false"
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
    return "VCSUserToEmailMap{" +
      "buildID=" + buildID +
      ", mapID=" + mapID +
      ", instantMessengerType=" + instantMessengerType +
      ", userName='" + userName + '\'' +
      ", userEmail='" + userEmail + '\'' +
      ", instantMessengerAddress='" + instantMessengerAddress + '\'' +
      ", timeStamp=" + timeStamp +
      ", disabled=" + disabled +
      '}';
  }
}
