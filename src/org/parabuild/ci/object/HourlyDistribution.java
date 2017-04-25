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

import net.sf.hibernate.*;

/**
 * Horly distribution.
 *
 * @hibernate.class table="HOURLY_DISTRIBUTION"
 * dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class HourlyDistribution implements Serializable, ObjectConstants, PersistentDistribution, Lifecycle {

  private static final long serialVersionUID = -5907298521657059063L; // NOPMD

  private int ID = UNSAVED_ID;
  private int activeBuildID = UNSAVED_ID;
  private int target = 1;
  private int successfulBuildCount = 0;
  private int failedBuildCount = 0;
  private int totalBuildCount = 0;
  private int changeListCount = 0;
  private int issueCount = 0;


  /**
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
   * @hibernate.property column="ACTIVE_BUILD_ID" unique="false"
   * null="false"
   */
  public int getActiveBuildID() {
    return activeBuildID;
  }


  public void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  /**
   * @hibernate.property column="TARGET" unique="false"
   * null="false"
   */
  public int getTarget() {
    return target;
  }


  public void setTarget(final int target) {
    this.target = target;
  }


  /**
   * @hibernate.property column="SUCCESSFUL_BUILD_COUNT"
   * unique="false" null="false"
   */
  public int getSuccessfulBuildCount() {
    return successfulBuildCount;
  }


  public void setSuccessfulBuildCount(final int successfulBuildCount) {
    this.successfulBuildCount = successfulBuildCount;
  }


  /**
   * @hibernate.property column="FAILED_BUILD_COUNT"
   * unique="false" null="false"
   */
  public int getFailedBuildCount() {
    return failedBuildCount;
  }


  public void setFailedBuildCount(final int failedBuildCount) {
    this.failedBuildCount = failedBuildCount;
  }


  /**
   * @hibernate.property column="TOTAL_BUILD_COUNT"
   * unique="false" null="false"
   */
  public int getTotalBuildCount() {
    return totalBuildCount;
  }


  public void setTotalBuildCount(final int totalBuildCount) {
    this.totalBuildCount = totalBuildCount;
  }


  /**
   * @hibernate.property column="CHANGE_LIST_COUNT"
   * unique="false" null="false"
   */
  public int getChangeListCount() {
    return changeListCount;
  }


  public void setChangeListCount(final int changeListCount) {
    this.changeListCount = changeListCount;
  }


  /**
   * @hibernate.property column="ISSUE_COUNT" unique="false"
   * null="false"
   */
  public int getIssueCount() {
    return issueCount;
  }


  public void setIssueCount(final int issueCount) {
    this.issueCount = issueCount;
  }


  public String toString() {
    return "HourlyDistribution{" +
      "ID=" + ID +
      ", activeBuildID=" + activeBuildID +
      ", hour=" + target +
      ", successfulBuildCount=" + successfulBuildCount +
      ", failedBuildCount=" + failedBuildCount +
      ", totalBuildCount=" + totalBuildCount +
      ", changeListCount=" + changeListCount +
      ", issueCount=" + issueCount +
      '}';
  }


  public boolean onSave(final Session session) throws CallbackException {
    validateHour();
    return NO_VETO;
  }


  public boolean onUpdate(final Session session) throws CallbackException {
    validateHour();
    return NO_VETO;
  }


  public boolean onDelete(final Session session) {
    return NO_VETO;
  }


  public void onLoad(final Session session, final Serializable serializable) {
  }


  private void validateHour() throws CallbackException {
    if (target > 24 || target < 0) throw new CallbackException("Invalid hour: " + 24);
  }
}
