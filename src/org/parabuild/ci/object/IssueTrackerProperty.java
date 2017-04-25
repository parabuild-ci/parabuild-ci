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
 * Stored build configuration
 *
 * @hibernate.class table="ISSUE_TRACKER_PROPERTY"
 * dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class IssueTrackerProperty implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -5713571700425080953L; // NOPMD

  // //////////////////////////////////////////////////////////////////////////
  // Common properties - DO NOT CHANGE!!!

  /**
   * Issue filter defines content to be present in issue
   * description to include an issue into build run's release
   * notes.
   */
  public static final String ISSUE_FILTER = "issue.filter";
  public static final String ISSUE_URL_TEMPLATE = "issue.url.template";
  public static final String ISSUE_LINK_PATTERN = "issue.link.pattern";

  // //////////////////////////////////////////////////////////////////////////
  // Jira config properties - DO NOT CHANGE!!!

  public static final String JIRA_PROJECT = "jira.project";
  public static final String JIRA_VERSIONS = "jira.versions";
  public static final String JIRA_FIX_VERSIONS = "jira.fix.versions";

  // //////////////////////////////////////////////////////////////////////////
  // Bugzilla config properties - DO NOT CHANGE!!!

  public static final String BUGZILLA_PRODUCT = "bugzilla.product";
  public static final String BUGZILLA_VERSION = "bugzilla.version";
  public static final String BUGZILLA_STATUSES = "bugzilla.statuses";
  public static final String BUGZILLA_MYSQL_HOST = "bugzilla.mysql.host";
  public static final String BUGZILLA_MYSQL_PORT = "bugzilla.mysql.port";
  public static final String BUGZILLA_MYSQL_DB = "bugzilla.mysql.db";
  public static final String BUGZILLA_MYSQL_USER = "bugzilla.mysql.user";
  public static final String BUGZILLA_MYSQL_PASSWORD = "bugzilla.mysql.password";

  // //////////////////////////////////////////////////////////////////////////
  // FogBugz config properties - DO NOT CHANGE!!!

  public static final String FOGBUGZ_DB_HOST = "fogbugz.db.host";
  public static final String FOGBUGZ_DB_PORT = "fogbugz.db.port";
  public static final String FOGBUGZ_DB_DB = "fogbugz.db.db";
  public static final String FOGBUGZ_DB_USER = "fogbugz.db.user";
  public static final String FOGBUGZ_DB_PASSWORD = "fogbugz.db.password";
  public static final String FOGBUGZ_PROJECT_ID = "fogbugz.project.id";
  public static final String FOGBUGZ_STATUS_CLOSED = "fogbugz.status.closed";
  public static final String FOGBUGZ_STATUS_FIXED = "fogbugz.status.fixed";

  private int trackerID = IssueTracker.UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private String name = null;
  private String value = null;
  private long timeStamp = 1;


  /**
   * Returns issue tracker ID
   *
   * @return int
   *
   * @hibernate.property column="ISSUE_TRACKER_ID" unique="false"
   * null="false"
   */
  public int getTrackerID() {
    return trackerID;
  }


  public void setTrackerID(final int trackerID) {
    this.trackerID = trackerID;
  }


  /**
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
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  public String toString() {
    return "IssueTrackerProperty{" +
      "trackerID=" + trackerID +
      ", ID=" + ID +
      ", name='" + name + '\'' +
      ", value='" + value + '\'' +
      ", timeStamp=" + timeStamp +
      '}';
  }
}
