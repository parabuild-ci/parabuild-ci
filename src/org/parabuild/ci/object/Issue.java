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
import java.util.Date;

/**
 * Received issue. Currently it accomodates BZ and Jira
 *
 * @hibernate.class table="ISSUE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class Issue implements Serializable, ObjectConstants {

  // Issue types - DO NOT CHANGE, ONLY ADD!!!
  public static final byte TYPE_UNDEFINED = 0;
  public static final byte TYPE_JIRA = 1;
  public static final byte TYPE_BUGZILLA = 2;
  public static final byte TYPE_PERFORCE = 3;

  private static final long serialVersionUID = -8936184017787430177L; // NOPMD

  private int ID = UNSAVED_ID;
  private byte trackerType = TYPE_UNDEFINED;
  private String key;
  private String description;
  private String product = "";
  private String version = "";
  private String project = "";
  private String status = "";
  private String closedBy = "";
  private String priority = "";
  private Date closed;
  private Date received;
  private String url = "";


  /**
   * Issue ID
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
   * Returns issue key
   *
   * @return String
   *
   * @hibernate.property column="KEY" unique="false"
   * null="false"
   */
  public String getKey() {
    return key;
  }


  public void setKey(final String key) {
    this.key = key;
  }


  /**
   * Returns build name
   *
   * @return byte
   *
   * @hibernate.property column="TRACKER_TYPE" unique="false"
   * null="false" type="byte"
   */
  public byte getTrackerType() {
    return trackerType;
  }


  public void setTrackerType(final byte trackerType) {
    this.trackerType = trackerType;
  }


  /**
   * Issue description (or title)
   *
   * @hibernate.property column="DESCRIPTION" unique="false"
   * null="false" length="500"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Issue product (BZ)
   *
   * @hibernate.property column="PRODUCT" unique="false"
   * null="true"
   */
  public String getProduct() {
    return product;
  }


  public void setProduct(final String product) {
    this.product = product;
  }


  /**
   * Issue project (Jira)
   *
   * @hibernate.property column="PROJECT" unique="false"
   * null="true"
   */
  public String getProject() {
    return project;
  }


  public void setProject(final String project) {
    this.project = project;
  }


  /**
   * Issue version
   *
   * @hibernate.property column="VERSION" unique="false"
   * null="true"
   */
  public String getVersion() {
    return version;
  }


  public void setVersion(final String version) {
    this.version = version;
  }


  /**
   * Issue status
   *
   * @hibernate.property column="STATUS" unique="false"
   * null="true"
   */
  public String getStatus() {
    return status;
  }


  public void setStatus(final String status) {
    this.status = status;
  }


  /**
   * Issue priority
   *
   * @hibernate.property column="PRIORITY" unique="false"
   * null="true"
   */
  public String getPriority() {
    return priority;
  }


  public void setPriority(final String priority) {
    this.priority = priority;
  }


  /**
   * Issue closed by
   *
   * @hibernate.property column="CLOSED_BY" unique="false"
   * null="true"
   */
  public String getClosedBy() {
    return closedBy;
  }


  public void setClosedBy(final String closedBy) {
    this.closedBy = closedBy;
  }


  /**
   * Issue closed when
   *
   * @hibernate.property column="CLOSED" unique="false"
   * null="true"
   */
  public Date getClosed() {
    return closed;
  }


  public void setClosed(final Date closed) {
    this.closed = closed;
  }


  /**
   * Date/time issue was received by Parabuild
   *
   * @hibernate.property column="RECEIVED" unique="false"
   * null="false"
   */
  public Date getReceived() {
    return received;
  }


  public void setReceived(final Date received) {
    this.received = received;
  }


  /**
   * Returns issue tracker URL template
   *
   * @return String
   *
   * @hibernate.property column="URL" unique="false"
   * null="false"
   */
  public String getUrl() {
    return url;
  }


  public void setUrl(final String url) {
    this.url = url;
  }


  public String toString() {
    return "Issue{" +
      "ID=" + ID +
      ", trackerType=" + trackerType +
      ", key='" + key + '\'' +
      ", description='" + description + '\'' +
      ", product='" + product + '\'' +
      ", version='" + version + '\'' +
      ", project='" + project + '\'' +
      ", status='" + status + '\'' +
      ", closedBy='" + closedBy + '\'' +
      ", priority='" + priority + '\'' +
      ", closed=" + closed +
      ", received=" + received +
      ", url='" + url + '\'' +
      '}';
  }
}
