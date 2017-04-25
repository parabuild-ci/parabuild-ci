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
 * Build run depedent runs
 *
 * @hibernate.class table="BUILD_RUN_DEPENDENCE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class BuildRunDependence implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -4591276259024162394L; // NOPMD

  private int ID = UNSAVED_ID;
  private int dependentBuildRunID = BuildRun.UNSAVED_ID;
  private int leadingBuildRunID = BuildRun.UNSAVED_ID;


  public BuildRunDependence(final int leadingBuildRunID, final int dependentBuildRunID) {
    this.dependentBuildRunID = dependentBuildRunID;
    this.leadingBuildRunID = leadingBuildRunID;
  }


  public BuildRunDependence() {
  }


  /**
   * Returns ID
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
   * Returns dependent build ID
   *
   * @return int
   *
   * @hibernate.property column="DEPENDENT_BUILD_RUN_ID" unique="false" null="false"
   */
  public int getDependentBuildRunID() {
    return dependentBuildRunID;
  }


  public void setDependentBuildRunID(final int dependentBuildRunID) {
    this.dependentBuildRunID = dependentBuildRunID;
  }


  /**
   * Returns leader build ID
   *
   * @return int
   *
   * @hibernate.property column="LEADER_BUILD_RUN_ID" unique="false" null="false"
   */
  public int getLeadingBuildRunID() {
    return leadingBuildRunID;
  }


  public void setLeadingBuildRunID(final int leadingBuildRunID) {
    this.leadingBuildRunID = leadingBuildRunID;
  }


  public String toString() {
    return "BuildRunDependence{" +
      "ID=" + ID +
      ", dependentBuildRunID=" + dependentBuildRunID +
      ", leadingBuildRunID=" + leadingBuildRunID +
      '}';
  }
}
