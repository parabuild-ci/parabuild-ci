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
 * Source Control Change
 *
 * @hibernate.class table="CHANGE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class Change implements Serializable, SimpleChange, ObjectConstants {

  private static final long serialVersionUID = 236142447373206397L; // NOPMD

  public static final byte TYPE_UNKNOWN = 0;
  public static final byte TYPE_ADDED = 1;
  public static final byte TYPE_MODIFIED = 2;
  public static final byte TYPE_DELETED = 3;
  public static final byte TYPE_BRANCHED = 4;
  public static final byte TYPE_RENAMED = 5;
  public static final byte TYPE_DESTROYED = 6;
  public static final byte TYPE_RECOVERED = 7;
  public static final byte TYPE_CHECKIN = 8;
  public static final byte TYPE_LABEL = 9;
  public static final byte TYPE_CREATE_ELEMENT = 10;
  public static final byte TYPE_NULL = 11;
  public static final byte TYPE_INTEGRATED = 12;
  public static final byte TYPE_MOVED = 13;
  public static final byte TYPE_UNDELETED = 14;
  public static final byte TYPE_ROLLEDBACK = 15;
  public static final byte TYPE_PROMOTE = 16;
  public static final byte TYPE_KEEP = 17;
  public static final byte TYPE_DEFUNCT = 18;
  public static final byte TYPE_COPIED = 19;
  public static final byte TYPE_TYPE_CHANGED = 20;
  public static final byte TYPE_UNMERGED = 21;
  public static final byte TYPE_PARING_BROKEN = 22;
  public static final byte TYPE_REMOVED = 23;
  public static final byte TYPE_KIND_CHANGED = 24;
  public static final byte TYPE_CONFLICTS = 25;

  private int changeID = UNSAVED_ID;
  private int changeListID = ChangeList.UNSAVED_ID;
  private byte changeType = TYPE_UNKNOWN;
  private String filePath = "";
  private String revision = null;
  private ChangeList changeList = null;


  public Change() {
  }


  public Change(final String filePath, final String revision, final byte changeType) {
    this.revision = revision;
    this.filePath = filePath;
    this.changeType = changeType;
  }


  /**
   * Returns change ID
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID" unsaved-value="-1"
   */
  public int getChangeID() {
    return changeID;
  }


  public void setChangeID(final int changeID) {
    this.changeID = changeID;
  }


  /**
   * Returns change set ID
   *
   * @return int
   * @hibernate.property column = "CHANGELIST_ID" unique="false" null="false"
   */
  public int getChangeListID() {
    return changeListID;
  }


  public void setChangeListID(final int changeListID) {
    this.changeListID = changeListID;
  }


  /**
   * @hibernate.property column = "TYPE" unique="false" null="false"
   */
  public byte getChangeType() {
    return changeType;
  }


  public void setChangeType(final byte changeType) {
    this.changeType = changeType;
  }


  /**
   * Helper method to return change type as a string
   *
   * @return change type as a string
   */
  public String getChangeTypeAsString() {
    switch (changeType) {
      case TYPE_ADDED:
        return "added";
      case TYPE_DELETED:
        return "deleted";
      case TYPE_MODIFIED:
        return "modified";
      case TYPE_BRANCHED:
        return "branched";
      case TYPE_RENAMED:
        return "renamed";
      case TYPE_DESTROYED:
        return "destroyed";
      case TYPE_CHECKIN:
        return "checked in";
      case TYPE_MOVED:
        return "moved";
      case TYPE_UNDELETED:
        return "undeleted";
      case TYPE_ROLLEDBACK:
        return "rolledback";
      case TYPE_CREATE_ELEMENT:
        return "created";
      case TYPE_NULL:
        return "null";
      case TYPE_INTEGRATED:
        return "integrated";
      case TYPE_PROMOTE:
        return "promoted";
      case TYPE_KEEP:
        return "kept";
      case TYPE_DEFUNCT:
        return "defunct";
      case TYPE_COPIED:
        return "copied";
      case TYPE_TYPE_CHANGED:
        return "type changed";
      case TYPE_REMOVED:
        return "removed";
      default:
        return "unknown";
    }
  }


  /**
   * @hibernate.property column = "FILE_PATH" unique="false" null="false"
   */
  public String getFilePath() {
    return filePath;
  }


  public void setFilePath(final String filePath) {
    this.filePath = filePath;
  }


  /**
   * @hibernate.property column = "REVISION" unique="false" null="false"
   */
  public String getRevision() {
    return revision;
  }


  public void setRevision(final String revision) {
    this.revision = revision;
  }


  public String toString() {
    return "Change{" +
            "changeID=" + changeID +
            ", changeListID=" + changeListID +
            ", changeType=" + changeType +
            ", filePath='" + filePath + '\'' +
            ", revision='" + revision + '\'' +
            ", changeList=" + changeList +
            '}';
  }
}
