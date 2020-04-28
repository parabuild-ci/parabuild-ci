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
 * @hibernate.class table="PROJECT_ATTRIBUTE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ProjectAttribute implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -6426274907331328221L; // NOPMD

  private int projectID = User.UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private String name = null;
  private String value = null;
  private long timestamp = 1;


  /**
   * Default constructor.
   */
  public ProjectAttribute() {
  }


  /**
   * Constructor.
   *
   * @param userID
   * @param name
   */
  public ProjectAttribute(final int userID, final String name, final String value) {
    this.projectID = userID;
    this.name = name;
    this.value = value;
  }


  /**
   * @return String
   *
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
   * The getter method for this property ID generator-parameter-1="SEQUENCE_GENERATOR"
   * generator-parameter-2="SEQUENCE_ID"
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
   * Returns property name
   *
   * @return String
   *
   * @hibernate.property column="NAME" unique="true"
   * null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Returns property value
   *
   * @return String
   *
   * @hibernate.property column="VALUE" unique="true"
   * null="false"
   */
  public String getValue() {
    return value;
  }


  public void setValue(final String value) {
    this.value = value;
  }


  public void setValue(final int propertyValue) {
    this.value = Integer.toString(propertyValue);
  }


  /**
   * Returns property value as int
   *
   * @return int property value
   */
  public int getValueAsInteger() throws NumberFormatException {
    return Integer.parseInt(value);
  }


  /**
   * Returns timestamp
   *
   * @return long
   *
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimestamp() {
    return timestamp;
  }


  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }


  public String toString() {
    return "ProjectAttribute{" +
      "projectID=" + projectID +
      ", ID=" + ID +
      ", name='" + name + '\'' +
      ", value='" + value + '\'' +
      ", timestamp=" + timestamp +
      '}';
  }
}
