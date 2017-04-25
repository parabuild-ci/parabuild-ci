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
 * Many-to-many relationship between a build and a project.
 *
 * @hibernate.class table="PROJECT_RESULT_GROUP" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class ProjectResultGroup implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 7269521258240154802L;  // NOPMD

  private int ID = UNSAVED_ID;
  private int resultGroupID = ActiveBuild.UNSAVED_ID;
  private int projectID = Project.UNSAVED_ID;


  /**
   * Creates project result group.
   *
   * @param resultGroupID
   * @param projectID
   */
  public ProjectResultGroup(final int resultGroupID, final int projectID) {
    this.resultGroupID = resultGroupID;
    this.projectID = projectID;
  }


  /**
   * Default constructor.
   */
  public ProjectResultGroup() {
  }


  /**
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
   * @hibernate.property column = "RESULT_GROUP_ID" unique="false" null="false"
   */
  public int getResultGroupID() {
    return resultGroupID;
  }


  public void setResultGroupID(final int resultGroupID) {
    this.resultGroupID = resultGroupID;
  }


  /**
   * @hibernate.property column = "PROJECT_ID" unique="false" null="false"
   */
  public int getProjectID() {
    return projectID;
  }


  public void setProjectID(final int projectID) {
    this.projectID = projectID;
  }


  public String toString() {
    return "ProjectBuild{" +
      "ID=" + ID +
      ", activeBuildID=" + resultGroupID +
      ", projectID=" + projectID +
      '}';
  }
}
