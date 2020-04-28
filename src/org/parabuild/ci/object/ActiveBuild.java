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
 * Stored build configuration
 *
 * @hibernate.class table="ACTIVE_BUILD" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ActiveBuild implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 2749534373687207758L; // NOPMD

  private int ID = UNSAVED_ID;
  private boolean deleted = false;
  private int startupStatus = UNSAVED_ID;
  private int sequenceNumber = 0;
  private long timeStamp = 0;


  /**
   * The getter method for this build ID
   *
   * @return int
   * @hibernate.id generator-class="assigned" column="ID" unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int id) {
    this.ID = id;
  }


  /**
   * Returns startupService status
   *
   * @return int
   *
   * @hibernate.property column="STARTUP_STATUS" unique="false"
   * null="false"
   */
  public int getStartupStatus() {
    return startupStatus;
  }


  public void setStartupStatus(final int startupStatus) {
    this.startupStatus = startupStatus;
  }


  /**
   * Returns true if this build was deleted.
   *
   * @return String
   * @hibernate.property column="DELETED"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isDeleted() {
    return deleted;
  }


  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  /**
   * Returns this build's sequence number.
   *
   * @hibernate.property column="SEQUENCE_NUMBER" unique="false" null="false"
   */
  public int getSequenceNumber() {
    return sequenceNumber;
  }


  public void setSequenceNumber(final int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }


  /**
   * Returns timestamp
   *
   * @return long
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  public String toString() {
    return "ActiveBuild{" +
      "ID=" + ID +
      ", deleted=" + deleted +
      ", startupStatus=" + startupStatus +
      ", sequenceNumber=" + sequenceNumber +
      ", timeStamp=" + timeStamp +
      '}';
  }
}
