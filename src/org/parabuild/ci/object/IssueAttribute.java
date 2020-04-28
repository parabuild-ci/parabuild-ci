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
 * Stored issue attributes
 *
 * @hibernate.class table="ISSUE_ATTR" dynamic-update="true"
 */
public final class IssueAttribute implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -3080999678374310690L; // NOPMD

  public static final String JIRA_PROJECT = IssueTrackerProperty.JIRA_PROJECT;
  public static final String JIRA_VERSIONS = IssueTrackerProperty.JIRA_VERSIONS;
  public static final String JIRA_FIX_VERSIONS = IssueTrackerProperty.JIRA_FIX_VERSIONS;

  private int issueID = Issue.UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private String name;
  private String value;


  /**
   * Default constructor
   */
  public IssueAttribute() {
  }


  /**
   * Constructor
   */
  public IssueAttribute(final String name, final String value) {
    this.name = name;
    this.value = value;
  }


  /**
   * Returns issue ID that attr belongs to
   *
   * @return String
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
   * The getter method for this property ID
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
   * Returns property name
   *
   * @return String
   *
   * @hibernate.property column="NAME" unique="true" null="false"
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
   * @hibernate.property column="VALUE" unique="true" null="false"
   */
  public String getValue() {
    return value;
  }


  public void setValue(final String value) {
    this.value = value;
  }


  public void setPropertyValue(final int propertyValue) {
    this.value = Integer.toString(propertyValue);
  }


  /**
   * Returns property value as int
   *
   * @return int property value
   */
  public int getPropertyValueAsInteger() throws NumberFormatException {
    return Integer.parseInt(value);
  }


  public String toString() {
    return "IssueAttribute{" +
      "issueID=" + issueID +
      ", ID=" + ID +
      ", name='" + name + '\'' +
      ", value='" + value + '\'' +
      '}';
  }
}
