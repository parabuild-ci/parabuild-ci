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
 * Build result header
 *
 * @hibernate.class table="RESULT_CONFIG" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ResultConfig implements Serializable, ObjectConstants, Lifecycle {

  private static final long serialVersionUID = -126501075513148853L; // NOPMD

  public static final byte RESULT_TYPE_UNDEFINED = 0;
  public static final byte RESULT_TYPE_FILE_LIST = 1;
  public static final byte RESULT_TYPE_DIR = 2;
  public static final byte RESULT_TYPE_URL = 3;

  private boolean failIfNotFound;
  private boolean ignoreTimestamp;
  private byte type = RESULT_TYPE_UNDEFINED;
  private int buildID = BuildConfig.UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private Integer autopublishGroupID;
  private long timeStamp;
  private String description;
  private String path = "";
  private String shellVariable = "";


  /**
   * Returns change ID
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
   * Returns change set ID
   *
   * @return int
   *
   * @hibernate.property column = "BUILD_ID" unique="false"
   * null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * @hibernate.property column = "PATH" unique="false"
   * null="false"
   */
  public String getPath() {
    return path;
  }


  public void setPath(final String path) {
    this.path = path;
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
   * @hibernate.property column = "AUTOPUBLISH_GROUP_ID" unique="false"
   * null="true"
   *
   * @return resultGroupID or null if not set.
   */
  public Integer getAutopublishGroupID() {
    return autopublishGroupID;
  }


  public void setAutopublishGroupID(final Integer autopublishGroupID) {
    this.autopublishGroupID = autopublishGroupID;
  }


  /**
   * Returns true if this user is enabled
   *
   * @return String
   *
   * @hibernate.property column="FAIL_IF_NOT_FOUND"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isFailIfNotFound() {
    return failIfNotFound;
  }


  public void setFailIfNotFound(final boolean failIfNotFound) {
    this.failIfNotFound = failIfNotFound;
  }


  /**
   * Returns true if this user is enabled
   *
   * @return String
   *
   * @hibernate.property column="IGNORE_TIMESTAMP"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isIgnoreTimestamp() {
    return ignoreTimestamp;
  }


  public void setIgnoreTimestamp(final boolean ignoreTimestamp) {
    this.ignoreTimestamp = ignoreTimestamp;
  }


  /**
   * Returns shell variable to be used when publishing.
   *
   * @return String
   *
   * @hibernate.property column="SHELL_VARIABLE"
   * unique="false" null="false"
   */
  public String getShellVariable() {
    return shellVariable;
  }


  public void setShellVariable(final String shellVariable) {
    this.shellVariable = shellVariable;
  }


  /**
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  public boolean onDelete(final Session session) {
    return NO_VETO;
  }


  public void onLoad(final Session session, final Serializable serializable) {
  }


  public boolean onSave(final Session session) throws CallbackException {
    validate();
    return NO_VETO;
  }


  private void validate() throws CallbackException {
    if (type <= 0) throw new CallbackException("Result type is undefined");
  }


  public boolean onUpdate(final Session session) throws CallbackException {
    validate();
    return NO_VETO;
  }


  public String toString() {
    return "ResultConfig{" +
      "ID=" + ID +
      ", buildID=" + buildID +
      ", description='" + description + '\'' +
      ", path='" + path + '\'' +
      ", timeStamp=" + timeStamp +
      ", type=" + type +
      ", autopublishGroupID=" + autopublishGroupID +
      '}';
  }
}
