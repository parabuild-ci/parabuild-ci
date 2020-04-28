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
 * Defined published step result.
 * @hibernate.class table="PUBLISHED_STEP_RESULT" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class PublishedStepResult implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 3380452351942166204L; // NOPMD

  private int ID = UNSAVED_ID;
  private int resultGroupID = UNSAVED_ID;
  private int stepResultID = UNSAVED_ID;
  private int activeBuildID = UNSAVED_ID;
  private int buildRunID = UNSAVED_ID;
  private int publisherBuildRunID = UNSAVED_ID;
  private Date publishDate = null;
  private String buildName = null;
  private Date buildRunDate = null;
  private int buildRunNumber = UNSAVED_ID;
  private String description = null;


  /**
   * The getter method for this ID
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
   * @hibernate.property column = "RESULT_GROUP_ID" unique="false" null="false"
   */
  public int getResultGroupID() {
    return resultGroupID;
  }


  public void setResultGroupID(final int resultGroupID) {
    this.resultGroupID = resultGroupID;
  }


  /**
   * @hibernate.property column = "STEP_RESULT_ID" unique="false" null="false"
   */
  public int getStepResultID() {
    return stepResultID;
  }


  public void setStepResultID(final int stepResultID) {
    this.stepResultID = stepResultID;
  }


  /**
   * @hibernate.property column = "ACTIVE_BUILD_ID" unique="false" null="false"
   */
  public int getActiveBuildID() {
    return activeBuildID;
  }


  public void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  /**
   * @hibernate.property column = "BUILD_RUN_ID" unique="false" null="false"
   */
  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  /**
   * @hibernate.property column = "PUBLISHER_BUILD_RUN_ID" unique="false" null="false"
   */
  public int getPublisherBuildRunID() {
    return publisherBuildRunID;
  }


  public void setPublisherBuildRunID(final int publisherBuildRunID) {
    this.publisherBuildRunID = publisherBuildRunID;
  }


  /**
   * @hibernate.property column = "PUBLISH_DATE" unique="false" null="false"
   */
  public Date getPublishDate() {
    return publishDate;
  }


  public void setPublishDate(final Date publishDate) {
    this.publishDate = publishDate;
  }


  /**
   * @hibernate.property column = "BUILD_NAME" unique="false" null="false"
   */
  public String getBuildName() {
    return buildName;
  }


  public void setBuildName(final String buildName) {
    this.buildName = buildName;
  }


  /**
   * @hibernate.property column = "BUILD_RUN_DATE" unique="false" null="false"
   */
  public Date getBuildRunDate() {
    return buildRunDate;
  }


  public void setBuildRunDate(final Date buildRunDate) {
    this.buildRunDate = buildRunDate;
  }


  /**
   * @hibernate.property column = "BUILD_RUN_NUMBER" unique="false" null="false"
   */
  public int getBuildRunNumber() {
    return buildRunNumber;
  }


  public void setBuildRunNumber(final int buildRunNumber) {
    this.buildRunNumber = buildRunNumber;
  }


  /**
   * @hibernate.property column = "DESCRIPTION" unique="false" null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  public String toString() {
    return "PublishedStepResult{" +
      "ID=" + ID +
      ", resultGroupID=" + resultGroupID +
      ", stepResultID=" + stepResultID +
      ", activeBuildID=" + activeBuildID +
      ", buildRunID=" + buildRunID +
      ", publisherBuildRunID=" + publisherBuildRunID +
      ", publishDate=" + publishDate +
      ", buildName='" + buildName + '\'' +
      ", buildRunDate=" + buildRunDate +
      ", buildRunNumber=" + buildRunNumber +
      ", description='" + description + '\'' +
      '}';
  }
}
