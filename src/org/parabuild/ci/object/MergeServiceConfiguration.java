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
 * Stored merge configuration
 *
 * @hibernate.class table="MERGE_SERVICE_CONFIGURATION" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class MergeServiceConfiguration implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -6025488658797867741L;

  public static final byte STARTUP_MODE_ACTIVE = 0;
  public static final byte STARTUP_MODE_DISABLED = 1;

  private boolean deleted = false;
  private byte startupMode = STARTUP_MODE_ACTIVE;
  private int ID = MergeConfiguration.UNSAVED_ID;
  private int projectID = Project.UNSAVED_ID;
  private long timeStamp = 1;


  /**
   * @hibernate.id generator-class="assigned" column="ID" unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * @hibernate.property column="PROJECT_ID" unique="false"
   * null="false"
   */
  public int getProjectID() {
    return projectID;
  }


  public void setProjectID(final int projectID) {
    this.projectID = projectID;
  }


  /**
   * @hibernate.property column="STARTUP_MODE"
   *  unique="false" null="false"
   */
  public byte getStartupMode() {
    return startupMode;
  }


  public void setStartupMode(final byte startupMode) {
    this.startupMode = startupMode;
  }


  /**
   * @hibernate.property column="DELETED" type="yes_no"
   * unique="false" null="false"
   */
  public boolean isDeleted() {
    return deleted;
  }


  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
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
    return "MergeServiceConfiguration{" +
      "deleted=" + deleted +
      ", startupMode=" + startupMode +
      ", ID=" + ID +
      ", projectID=" + projectID +
      ", timeStamp=" + timeStamp +
      '}';
  }
}
