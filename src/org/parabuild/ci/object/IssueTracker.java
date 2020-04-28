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
 * Basic issue tracker configuration.
 *
 * @hibernate.class table="ISSUE_TRACKER" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class IssueTracker implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -8936184017787430177L; // NOPMD

  // Issue tracker types - DO NOT CHANGE, ONLY ADD!!!
  public static final byte TYPE_UNDEFINED = 0;
  public static final byte TYPE_JIRA_LISTENER = 1;
  public static final byte TYPE_BUGZILLA_DIRECT = 2;
  public static final byte TYPE_PERFORCE = 3;
  public static final byte TYPE_FOGBUGZ = 4;

  private int ID = UNSAVED_ID;
  private int buildID = BuildConfig.UNSAVED_ID;
  private byte type = TYPE_UNDEFINED;
  private long timeStamp = 1;


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
   * Sequence ID
   *
   * @return int
   *
   * @hibernate.id generator-class="identity" column="ID" unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Returns build name
   *
   * @return byte
   *
   * @hibernate.property column="TYPE" unique="true" null="false"
   */
  public byte getType() {
    return type;
  }


  public void setType(final byte type) {
    this.type = type;
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
    return "IssueTracker{" +
      "ID=" + ID +
      ", buildID=" + buildID +
      ", type=" + type +
      ", timeStamp=" + timeStamp +
      '}';
  }
}
