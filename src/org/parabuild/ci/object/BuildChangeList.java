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
 * New change list association
 *
 * @hibernate.class table="BUILD_CHANGELIST" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class BuildChangeList implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 6385251220083437776L; // NOPMD

  private int ID = UNSAVED_ID;
  private int changeListID = ChangeList.UNSAVED_ID;
  private int buildID = BuildConfig.UNSAVED_ID;
  private Date changeListCreatedAt = null;
  private String isNew = "Y";


  /**
   * Returns change list ID
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
   * Returns change list ID
   *
   * @return int
   *
   * @hibernate.property column = "CHANGELIST_ID" unique="false" null="false"
   */
  public int getChangeListID() {
    return changeListID;
  }


  public void setChangeListID(final int changeListID) {
    this.changeListID = changeListID;
  }


  /**
   * @hibernate.property column = "CHANGELIST_CREATED" unique="false" null="false"
   */
  public Date getChangeListCreatedAt() {
    return changeListCreatedAt;
  }


  public void setChangeListCreatedAt(final Date changeListCreatedAt) {
    this.changeListCreatedAt = changeListCreatedAt;
  }


  /**
   * Returns build ID
   *
   * @return int
   *
   * @hibernate.property column = "BUILD_ID" unique="false" null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * @hibernate.property column = "NEW" unique="false" null="false"
   */
  public String getNew() {
    return isNew;
  }


  public void setNew(final String aNew) {
    isNew = aNew;
  }


  public String toString() {
    return "BuildChangeList {" +
      "buildID=" + buildID +
      ", ID=" + ID +
      ", changeListID=" + changeListID +
      ", changeListCreatedAt=" + changeListCreatedAt +
      ", isNew='" + isNew + '\'' +
      '}';
  }
}

