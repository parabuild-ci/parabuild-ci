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
 * Stored pending issue - an issue that has not gotten attached to a build run yet
 *
 * @hibernate.class table="PENDING_ISSUE" dynamic-update="true"
 */
public final class PendingIssue implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 7087649256925416289L; // NOPMD

  private int issueID = Issue.UNSAVED_ID;
  private int buildID = BuildConfig.UNSAVED_ID;
  private int ID = UNSAVED_ID;


  /**
   * Default constructor
   */
  public PendingIssue() {
  }


  /**
   * Constructor
   */
  public PendingIssue(final int buildID, final int issueID) {
    this.buildID = buildID;
    this.issueID = issueID;
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
   * Returns build ID
   *
   * @return int
   *
   * @hibernate.property column="BUILD_ID" unique="false" null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
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
    return "PendingIssue{" +
      "issueID=" + issueID +
      ", buildID=" + buildID +
      ", ID=" + ID +
      '}';
  }
}
