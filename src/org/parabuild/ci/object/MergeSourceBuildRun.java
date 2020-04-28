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
 * A link between validated merge queue and a build run
 *
 * @hibernate.class table="MERGE_SOURCE_BUILD_RUN"
 * dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class MergeSourceBuildRun implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 8135950856483655946L;

  private int ID = UNSAVED_ID;
  private int mergeID = Merge.UNSAVED_ID;
  private int buildRunID = BuildRun.UNSAVED_ID;


  /**
   * Returns object's ID
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
   * @hibernate.property column="MERGE_ID"
   *  unique="false" null="false"
   */
  public int getMergeID() {
    return mergeID;
  }


  public void setMergeID(final int mergeID) {
    this.mergeID = mergeID;
  }


  /**
   * @hibernate.property column="BUILD_RUN_ID"
   *  unique="false" null="false"
   */
  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  public String toString() {
    return "MergeSourceBuildRun{" +
      "ID=" + ID +
      ", mergeID=" + mergeID +
      ", buildRunID=" + buildRunID +
      '}';
  }
}
