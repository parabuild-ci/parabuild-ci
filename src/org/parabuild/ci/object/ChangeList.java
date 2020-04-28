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

import org.parabuild.ci.util.StringUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Source Control change list
 *
 * @hibernate.class table="CHANGELIST" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ChangeList implements Serializable, ObjectConstants {

  /**
   * This comparator is used to sort change lists in direct
   * change date order.
   */
  public static final Comparator CHANGE_DATE_COMPARATOR = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      final ChangeList c1 = (ChangeList) o1;
      final ChangeList c2 = (ChangeList) o2;
      if (c1 == c2) return 0; // NOPMD
      // dates
      return c1.getCreatedAt().compareTo(c2.getCreatedAt());
    }
  };


  /**
   * This comparator is used to sort change lists in reverse
   * change date order.
   */
  public static final Comparator REVERSE_CHANGE_DATE_COMPARATOR = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      final ChangeList c1 = (ChangeList) o1;
      final ChangeList c2 = (ChangeList) o2;
      return c2.getCreatedAt().compareTo(c1.getCreatedAt());
    }
  };


  /**
   * This comparator is used to sort change lists in reverse
   * change number order.
   */
  public static final Comparator REVERSE_CHANGE_NUMBER_COMPARATOR = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      final ChangeList c1 = (ChangeList) o1;
      final ChangeList c2 = (ChangeList) o2;
      return Integer.valueOf(c2.getNumber()).compareTo(Integer.valueOf(c1.getNumber()));
    }
  };

  private static final long serialVersionUID = -4372844970766934869L; // NOPMD

  private int changeListID = -1;
  private Date createdAt;
  private Set changes = new HashSet(11);
  private String client;
  private String description;
  private String email;
  private String number;
  private String user;
  private String branch;
  private boolean truncated;
  private int originalSize;


  /**
   * Returns change list ID
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getChangeListID() {
    return changeListID;
  }


  public void setChangeListID(final int changeListID) {
    this.changeListID = changeListID;
  }


  public String getChangeListIDAsString() {
    return Integer.toString(changeListID);
  }


  /**
   * @hibernate.property column = "CREATED" unique="false"
   * null="false"
   */
  public Date getCreatedAt() {
    return createdAt;
  }


  public void setCreatedAt(final Date createdAt) {
    this.createdAt = createdAt;
  }


  /**
   * Returns formatted createdAt date.
   *
   * @param format to use.
   * @return formatted createdAt date.
   */
  public String getCreatedAt(final DateFormat format) {
    return format.format(createdAt);
  }


  /**
   * Returns change list number, null if not provided
   *
   * @hibernate.property column = "NUMBER" unique="false"
   * null="true"
   */
  public String getNumber() {
    return number;
  }


  public void setNumber(final String number) {
    this.number = StringUtils.truncate(number, 10);
  }


  /**
   * Returns description
   *
   * @hibernate.property column = "DESCRIPTION" unique="false"
   * null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = StringUtils.truncate(description, 1024);
  }


  /**
   * Returns user
   *
   * @hibernate.property column = "USER" unique="false"
   * null="false"
   */
  public String getUser() {
    return user;
  }


  public void setUser(final String user) {
    this.user = StringUtils.truncate(user, 50);
  }


  /**
   * Returns branch name, null if not provided
   *
   * @hibernate.property column = "BRANCH" unique="false"
   * null="true"
   */
  public String getBranch() {
    return branch;
  }


  public void setBranch(final String branch) {
    this.branch = StringUtils.truncate(branch, 50);
  }


  /**
   * Returns change list number, null if not provided
   *
   * @hibernate.property column = "CLIENT" unique="false"
   * null="true"
   */
  public String getClient() {
    return client;
  }


  public void setClient(final String client) {
    this.client = StringUtils.truncate(client, 50);
  }


  /**
   * The getter method for this Customer's orders.
   * //* <p/>
   * //* //   * @hibernate.set  role="change" order-by="CHANGE_ID"
   * //* table="CHANGE" cascade="all" readonly="true" //   *
   * //* // @hibernate.collection-key  column="ID" //   *
   * //* // @hibernate.collection-one-to-many  class="org.parabuild.ci.object.Change"
   * //* column="CHANGE_ID"
   */
  public Set getChanges() {
    return changes;
  }


  public void setChanges(final Set changes) {
    this.changes = changes;
  }


  /**
   * Returns user email if available
   *
   * @hibernate.property column = "EMAIL" unique="false"
   * null="true" length="50"
   */
  public String getEmail() {
    return email;
  }


  public void setEmail(final String email) {
    this.email = StringUtils.truncate(email, 50);
  }


  /**
   * Returns true if this change list has been truncated
   *
   * @return String
   * @hibernate.property column="TRUNCATED"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isTruncated() {
    return truncated;
  }


  public void setTruncated(final boolean truncated) {
    this.truncated = truncated;
  }


  /**
   * Returns true if this change list has been truncated
   *
   * @return String
   * @hibernate.property column="ORIGINAL_SIZE"
   * unique="false" null="false"
   */
  public int getOriginalSize() {
    return originalSize;
  }


  public void setOriginalSize(final int originalSize) {
    this.originalSize = originalSize;
  }


  /**
   * Increments original size.
   */
  public void incrementOriginalSize() {
    originalSize++;
  }


  public String toString() {
    return "ChangeList{" +
            "changeListID=" + changeListID +
            ", number='" + number + '\'' +
            ", createdAt=" + createdAt +
            ", client='" + client + '\'' +
            ", description='" + description + '\'' +
            ", email='" + email + '\'' +
            ", user='" + user + '\'' +
            ", branch='" + branch + '\'' +
            ", truncated=" + truncated +
            ", originalSize=" + originalSize +
            ", changes=" + changes +
            '}';
  }
}
