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

import net.sf.hibernate.CallbackException;
import net.sf.hibernate.Lifecycle;
import net.sf.hibernate.Session;

import java.io.Serializable;

/**
 * @hibernate.class table="PROJECT" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class Project implements Serializable, ObjectConstants, Lifecycle {

  private static final long serialVersionUID = 236142447373206397L; // NOPMD

  // types
  public static final byte TYPE_UNKNOWN = 0;
  public static final byte TYPE_SYSTEM = 1;
  public static final byte TYPE_USER = 2;

  // keys
  public static final String KEY_SYSTEM = "SYSTEM";

  private int ID = UNSAVED_ID;
  private String name;
  private String description;
  private String key;
  private byte type = TYPE_USER;
  private boolean deleted;
  private long timeStamp;


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
   * @hibernate.property column = "DESCRIPTION" unique="false"
   * null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @hibernate.property column = "KEY" unique="false"
   * null="false"
   */
  public String getKey() {
    return key;
  }


  public void setKey(final String key) {
    this.key = key;
  }


  /**
   * @hibernate.property column="DELETED"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isDeleted() {
    return deleted;
  }


  public void
  setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  /**
   * @hibernate.property column = "TYPE" unique="false"
   * null="false"
   */
  public byte getType() {
    return type;
  }


  public void setType(final byte type) {
    this.type = type;
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


  public boolean onSave(final Session session) throws CallbackException {
    validateProjectType();
    return NO_VETO;
  }


  public boolean onUpdate(final Session session) throws CallbackException {
    validateProjectType();
    return NO_VETO;
  }


  public boolean onDelete(final Session session) throws CallbackException {
    if (ID == 0) throw new CallbackException("Deleting project with ID 0 is not allowed");
    return NO_VETO;
  }


  public void onLoad(final Session session, final Serializable serializable) {
  }


  private void validateProjectType() throws CallbackException {
    if (type == TYPE_UNKNOWN) throw new CallbackException("Project type is not set");
  }


  public String toString() {
    return "Project{" +
            "ID=" + ID +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", key='" + key + '\'' +
            ", type=" + type +
            ", deleted=" + deleted +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
