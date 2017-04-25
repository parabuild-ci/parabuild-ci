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
 * Stored issue attributes
 *
 * @hibernate.class table="RELEASE_NOTE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ReleaseNote implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -3080999678374310690L; // NOPMD

  private int issueID = Issue.UNSAVED_ID;
  private int buildRunID = BuildRun.UNSAVED_ID;
  private int ID = UNSAVED_ID;


  /**
   * Default constructor.
   */
  public ReleaseNote() {
  }


  /**
   * Creates release note
   */
  public ReleaseNote(final int buildRunID, final int issueID) {
    this.issueID = issueID;
    this.buildRunID = buildRunID;
  }


  /**
   * Returns issue ID
   *
   * @return int
   *
   * @hibernate.property column="ISSUE_ID" unique="false" null="false"
   */
  public int getIssueID() {
    return issueID;
  }


  public void setIssueID(final int issueID) {
    this.issueID = issueID;
  }


  /**
   * Returns build run ID
   *
   * @return int
   *
   * @hibernate.property column="BUILD_RUN_ID" unique="false" null="false"
   */
  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  /**
   * The getter method for this rel note ID
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


  public String toString() {
    return "ReleaseNote{" +
      "issueID=" + issueID +
      ", buildRunID=" + buildRunID +
      ", ID=" + ID +
      '}';
  }
}
