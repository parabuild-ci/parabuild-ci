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
 * Represents cluster.
 *
 * @hibernate.class table="CLUSTER" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class BuilderConfiguration implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -5907298521657059063L; // NOPMD

  private int ID = UNSAVED_ID;
  private long timeStamp;
  private String description = "";
  private String name;
  private boolean enabled = true;
  private boolean deleted;


  /**
   * The getter method for this goup ID
   *
   * @return int
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
   * @hibernate.property column = "NAME" unique="true"
   * null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @hibernate.property column = "DESCR" unique="true"
   * null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Returns true if this cluster is enabled
   *
   * @return String
   * @hibernate.property column="ENABLED"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isEnabled() {
    return enabled;
  }


  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * Returns true if this builder was deleted
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
    return "BuilderConfiguration{" +
            "ID=" + ID +
            ", timeStamp=" + timeStamp +
            ", description='" + description + '\'' +
            ", name='" + name + '\'' +
            ", enabled=" + enabled +
            ", deleted=" + deleted +
            '}';
  }
}
