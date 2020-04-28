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
 * User group memebership
 *
 * @hibernate.class table="DISPLAY_GROUP_BUILD" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class DisplayGroupBuild implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -5237501290837750448L; // NOPMD

  private int ID = UNSAVED_ID;
  private int buildID = BuildConfig.UNSAVED_ID;
  private int displayGroupID = Group.UNSAVED_ID;
  private long timeStamp = 0;


  public DisplayGroupBuild() {
  }


  public DisplayGroupBuild(final int displayGroupID, final int buildID) {
    this.displayGroupID = displayGroupID;
    this.buildID = buildID;
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
   * @hibernate.property column = "BUILD_ID" unique="false"
   * null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * The getter method for a group ID which user is member of
   *
   * @return int
   *
   * @hibernate.property column = "DISPLAY_GROUP_ID" unique="false"
   * null="false"
   */
  public int getDisplayGroupID() {
    return displayGroupID;
  }


  public void setDisplayGroupID(final int displayGroupID) {
    this.displayGroupID = displayGroupID;
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
    return "DisplayGroupBuild{" +
      "ID=" + ID +
      ", buildID=" + buildID +
      ", displayGroupID=" + displayGroupID +
      ", timeStamp=" + timeStamp +
      '}';
  }
}
