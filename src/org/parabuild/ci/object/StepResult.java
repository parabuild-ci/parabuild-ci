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

import net.sf.hibernate.Lifecycle;
import net.sf.hibernate.Session;

import java.io.Serializable;

/**
 * Sequence result. Describes a archived sequence result.
 *
 * @hibernate.class table="STEP_RESULT" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class StepResult implements Serializable, ObjectConstants, Lifecycle {

  private static final long serialVersionUID = -2506229207070111304L; // NOPMD

  /**
   * Path type is unknown.
   */
  public static final byte PATH_TYPE_UNKNOWN = ResultConfig.RESULT_TYPE_UNDEFINED;

  /**
   * Path type is a single text file.
   */
  public static final byte PATH_TYPE_SINGLE_FILE = ResultConfig.RESULT_TYPE_FILE_LIST;

  /**
   * Path type is a flat directory with text files.
   */
  public static final byte PATH_TYPE_DIR = ResultConfig.RESULT_TYPE_DIR;

  /**
   * Path type is an external URL
   */
  public static final byte PATH_TYPE_EXTERNAL_URL = ResultConfig.RESULT_TYPE_URL;

  // PK
  private int ID = -1;

  private byte pathType = PATH_TYPE_UNKNOWN;
  private int stepRunID = -1;
  private String archiveFileName = null;
  private String description = null;
  private String path = null;
  private boolean found = false;
  private boolean pinned = false;


  /**
   * Returns sequence ID
   *
   * @return int
   *
   * @hibernate.property column="STEP_RUN_ID" unique="false"
   * null="false"
   */
  public int getStepRunID() {
    return stepRunID;
  }


  public void setStepRunID(final int stepRunID) {
    this.stepRunID = stepRunID;
  }


  /**
   * Returns result ID
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
   * Returns result ID as string.
   *
   * @return String
   */
  public String getIDAsString() {
    return Integer.toString(ID);
  }


  /**
   * Returns an archive file name. The archive file name is a
   * name of a generated by ArchiveManager a file or a directory
   * that hosts the result.
   * <p/>
   * It will be a file for the following result path types:
   * <br/>PATH_TYPE_SINGLE_FILE
   * <p/>
   * It will be a directory for the following result path types:
   * <br/>PATH_TYPE_DIR
   *
   * @return String
   *
   * @hibernate.property column="FILE" unique="false"
   * null="false"
   * @see StepResult#PATH_TYPE_SINGLE_FILE
   * @see StepResult#PATH_TYPE_DIR
   */
  public String getArchiveFileName() {
    return archiveFileName;
  }


  public void setArchiveFileName(final String archiveFileName) {
    this.archiveFileName = archiveFileName;
  }


  /**
   * Returns relative result path as it was declared in the
   * result config.
   *
   * @return int
   *
   * @hibernate.property column="PATH" unique="false"
   * null="false"
   */
  public String getPath() {
    return path;
  }


  public void setPath(final String path) {
    this.path = path;
  }


  /**
   * Returns result description
   *
   * @return
   *
   * @hibernate.property column="DESCRIPTION" unique="false"
   * null="true"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Path type describes what kind of a file is stored in the
   * result archive under the result's path.
   * <p/>
   * Path type is used to determine how to display result(s)
   *
   * @hibernate.property column = "PATH_TYPE" unique="false"
   * null="false"
   * @see #PATH_TYPE_SINGLE_FILE
   * @see #PATH_TYPE_DIR
   * @see #PATH_TYPE_EXTERNAL_URL
   */
  public byte getPathType() {
    return pathType;
  }


  public void setPathType(final byte pathType) {
    this.pathType = pathType;
  }


  /**
   * @hibernate.property column = "FOUND" type="yes_no"
   * unique="false" null="false"
   */
  public boolean isFound() {
    return found;
  }


  public void setFound(final boolean found) {
    this.found = found;
  }


  /**
   * @hibernate.property column = "PINNED" type="yes_no"
   * unique="false" null="false"
   */
  public boolean isPinned() {
    return pinned;
  }


  public void setPinned(final boolean pinned) {
    this.pinned = pinned;
  }


  public boolean onSave(final Session session) {
    return NO_VETO;
  }


  public boolean onUpdate(final Session session) {
    if (!validForSave()) return VETO;
    return NO_VETO;
  }


  public boolean onDelete(final Session session) {
    return NO_VETO;
  }


  public void onLoad(final Session session, final Serializable serializable) {
  }


  /**
   * Validates for save operation.
   */
  private boolean validForSave() {
    return pathType != PATH_TYPE_UNKNOWN;
  }


  public String toString() {
    return "StepResult{" +
      "ID=" + ID +
      ", pathType=" + pathType +
      ", stepRunID=" + stepRunID +
      ", archiveFileName='" + archiveFileName + '\'' +
      ", description='" + description + '\'' +
      ", path='" + path + '\'' +
      ", found=" + found +
      ", pinned=" + pinned +
      '}';
  }
}
