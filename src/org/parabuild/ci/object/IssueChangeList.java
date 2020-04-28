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
 * Many - to - many relation table for issues attached to a
 * changelist
 *
 * @hibernate.class table="ISSUE_CHANGELIST" dynamic-update="true"
 */
public final class IssueChangeList implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 7087649256925416289L; // NOPMD

  private int issueID = Issue.UNSAVED_ID;
  private int changeListID = ChangeList.UNSAVED_ID;
  private int ID = UNSAVED_ID;


  /**
   * Default constructor
   */
  public IssueChangeList() {
  }


  /**
   * Constructor
   */
  public IssueChangeList(final int issueID, final int changelistID) {
    this.issueID = issueID;
    this.changeListID = changelistID;
  }


  /**
   * Constructor
   */
  public IssueChangeList(final Integer issueID, final int changelistID) {
    this(issueID.intValue(), changelistID);
  }


  /**
   * Returns issue ID
   *
   * @return int
   *
   * @hibernate.property column="ISSUE_ID" unique="false"
   * null="false"
   */
  public int getIssueID() {
    return issueID;
  }


  public void setIssueID(final int issueID) {
    this.issueID = issueID;
  }


  /**
   * Returns change list ID
   *
   * @return int
   *
   * @hibernate.property column="CHANGELIST_ID" unique="false"
   * null="false"
   */
  public int getChangeListID() {
    return changeListID;
  }


  public void setChangeListID(final int changeListID) {
    this.changeListID = changeListID;
  }


  /**
   * The getter method for this rel note ID
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


  public String toString() {
    return "IssueChangeList{" +
      "ID=" + ID +
      ", issueID=" + issueID +
      ", changeListID=" + changeListID +
      '}';
  }
}
