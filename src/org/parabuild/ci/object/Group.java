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
 * Group
 *
 * @hibernate.class table="GROUPS" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class Group implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -8907298511657059064L; // NOPMD

  public static final String SYSTEM_ADMIN_GROUP = "Administrators";
  public static final String SYSTEM_ANONYMOUS_GROUP = "Anonymous";

  private int ID = UNSAVED_ID;
  private long timeStamp = 0;
  private String description = "";
  private String name = null;
  private boolean enabled = true;

  // build rights
  private boolean allowedToStartBuild = false;
  private boolean allowedToStopBuild = false;
  private boolean allowedToCreateBuild = false;
  private boolean allowedToUpdateBuild = false;
  private boolean allowedToDeleteBuild = false;
  private boolean allowedToViewBuild = true;
  private boolean allowedToPublishResults = false;
  private boolean allowedToDeleteResults = false;

  // result rights
  private boolean allowedToCreateResultGroup = false;
  private boolean allowedToDeleteResultGroup = false;
  private boolean allowedToUpdateResultGroup = false;
  private boolean allowedToViewResultGroup = true;
  private boolean allowedToActivateBuild = false;


  /**
   * The getter method for this group ID.
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID" unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * @hibernate.property column = "NAME" unique="true" null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @hibernate.property column = "DESCR" unique="true" null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Returns true if this user is enabled
   *
   * @return String
   * @hibernate.property column="ENABLED"  type="yes_no" unique="false" null="false"
   */
  public boolean isEnabled() {
    return enabled;
  }


  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * Returns true if this group can start build.
   *
   * @return String
   * @hibernate.property column="ALLOWED_TO_START_BUILD" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToStartBuild() {
    return allowedToStartBuild;
  }


  public void setAllowedToStartBuild(final boolean allowedToStartBuild) {
    this.allowedToStartBuild = allowedToStartBuild;
  }


  /**
   * Returns true if this group can start build.
   *
   * @return String
   * @hibernate.property column="ALLOWED_TO_STOP_BUILD" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToStopBuild() {
    return allowedToStopBuild;
  }


  public void setAllowedToStopBuild(final boolean allowedToStopBuild) {
    this.allowedToStopBuild = allowedToStopBuild;
  }


  /**
   * Returns true if this group can start build.
   *
   * @return String
   * @hibernate.property column="ALLOWED_TO_CREATE_BUILD" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToCreateBuild() {
    return allowedToCreateBuild;
  }


  public void setAllowedToCreateBuild(final boolean allowedToCreateBuild) {
    this.allowedToCreateBuild = allowedToCreateBuild;
  }


  /**
   * Returns true if this group can start build.
   *
   * @return String
   * @hibernate.property column="ALLOWED_TO_UPDATE_BUILD" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToUpdateBuild() {
    return allowedToUpdateBuild;
  }


  public void setAllowedToUpdateBuild(final boolean allowedToUpdateBuild) {
    this.allowedToUpdateBuild = allowedToUpdateBuild;
  }


  /**
   * Returns true if this group can start build.
   *
   * @return String
   * @hibernate.property column="ALLOWED_TO_DELETE_BUILD" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToDeleteBuild() {
    return allowedToDeleteBuild;
  }


  public void setAllowedToDeleteBuild(final boolean allowedToDeleteBuild) {
    this.allowedToDeleteBuild = allowedToDeleteBuild;
  }


  /**
   * Returns true if this group can start build.
   *
   * @return String
   * @hibernate.property column="ALLOWED_TO_VIEW_BUILD" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToViewBuild() {
    return allowedToViewBuild;
  }


  public void setAllowedToViewBuild(final boolean allowedToViewBuild) {
    this.allowedToViewBuild = allowedToViewBuild;
  }


  /**
   * @hibernate.property column="ALLOWED_TO_PUBLISH_RESULTS" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToPublishResults() {
    return allowedToPublishResults;
  }


  public void setAllowedToPublishResults(final boolean allowedToPublishResults) {
    this.allowedToPublishResults = allowedToPublishResults;
  }


  /**
   * @hibernate.property column="ALLOWED_TO_DELETE_RESULTS" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToDeleteResults() {
    return allowedToDeleteResults;
  }


  public void setAllowedToDeleteResults(final boolean allowedToDeleteResults) {
    this.allowedToDeleteResults = allowedToDeleteResults;
  }


  /**
   * @hibernate.property column="ALLOWED_TO_CREATE_RESULT_GROUP" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToCreateResultGroup() {
    return allowedToCreateResultGroup;
  }


  public void setAllowedToCreateResultGroup(final boolean allowedToCreateResultGroup) {
    this.allowedToCreateResultGroup = allowedToCreateResultGroup;
  }


  /**
   * @hibernate.property column="ALLOWED_TO_DELETE_RESULT_GROUP" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToDeleteResultGroup() {
    return allowedToDeleteResultGroup;
  }


  public void setAllowedToDeleteResultGroup(final boolean allowedToDeleteResultGroup) {
    this.allowedToDeleteResultGroup = allowedToDeleteResultGroup;
  }


  /**
   * @hibernate.property column="ALLOWED_TO_UPDATE_RESULT_GROUP" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToUpdateResultGroup() {
    return allowedToUpdateResultGroup;
  }


  public void setAllowedToUpdateResultGroup(final boolean allowedToUpdateResultGroup) {
    this.allowedToUpdateResultGroup = allowedToUpdateResultGroup;
  }


  /**
   * @hibernate.property column="ALLOWED_TO_VIEW_RESULT_GROUP" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToViewResultGroup() {
    return allowedToViewResultGroup;
  }


  public void setAllowedToViewResultGroup(final boolean allowedToViewResultGroup) {
    this.allowedToViewResultGroup = allowedToViewResultGroup;
  }


  /**
   * @hibernate.property column="ALLOWED_TO_ACTIVATE_BUILD" type="yes_no" unique="false" null="false"
   */
  public boolean isAllowedToActivateBuild() {
    return allowedToActivateBuild;
  }


  public void setAllowedToActivateBuild(final boolean allowedToActivateBuild) {
    this.allowedToActivateBuild = allowedToActivateBuild;
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


  public String toString() {
    return "Group{" +
            "ID=" + ID +
            ", timeStamp=" + timeStamp +
            ", description='" + description + '\'' +
            ", name='" + name + '\'' +
            ", enabled=" + enabled +
            ", allowedToStartBuild=" + allowedToStartBuild +
            ", allowedToStopBuild=" + allowedToStopBuild +
            ", allowedToCreateBuild=" + allowedToCreateBuild +
            ", allowedToUpdateBuild=" + allowedToUpdateBuild +
            ", allowedToDeleteBuild=" + allowedToDeleteBuild +
            ", allowedToViewBuild=" + allowedToViewBuild +
            ", allowedToPublishResults=" + allowedToPublishResults +
            ", allowedToDeleteResults=" + allowedToDeleteResults +
            ", allowedToCreateResultGroup=" + allowedToCreateResultGroup +
            ", allowedToDeleteResultGroup=" + allowedToDeleteResultGroup +
            ", allowedToUpdateResultGroup=" + allowedToUpdateResultGroup +
            ", allowedToViewResultGroup=" + allowedToViewResultGroup +
            '}';
  }
}
