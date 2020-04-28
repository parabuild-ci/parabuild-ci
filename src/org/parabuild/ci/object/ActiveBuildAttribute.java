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
 * Stored build properties
 *
 * @hibernate.class table="ACTIVE_BUILD_ATTRIBUTE"
 * dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ActiveBuildAttribute implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -6426274907331328221L; // NOPMD

  public static final String BUILD_NUMBER_SEQUENCE = "build.number.sequence";
  public static final String VERSION_COUNTER_SEQUENCE = "version.counter.sequence";
  public static final String SKIP_NEXT_SCHEDULED_BUILD = "skip.next.scheduled.build";

  /**
   * Persistent last agent host for sticky agents.
   */
  public static final String LAST_AGENT_HOST = "last.agent.host";

  // statistics attributes
  public static final String STAT_PREFIX = "build.stat.";
  public static final String STAT_SUCC_BUILDS_TO_DATE = STAT_PREFIX + "successful.builds.to.date";
  public static final String STAT_FAILED_BUILDS_TO_DATE = STAT_PREFIX + "failed.builds.to.date";
  public static final String STAT_CHANGE_LISTS_TO_DATE = STAT_PREFIX + "change.lists.to.date";
  public static final String STAT_ISSUES_TO_DATE = STAT_PREFIX + "issues.to.date";
  public static final String STAT_AVERAGE_TIME_TO_FIX = "average.time.to.fix";


  private int buildID = BuildConfig.UNSAVED_ID;
  private int propertyID = UNSAVED_ID;
  private String propertyName;
  private String propertyValue;
  private long propertyTimeStamp = 1;


  /**
   * Default constructor. Required by hibernate.
   */
  public ActiveBuildAttribute() {
  }


  /**
   * Constructor.
   *
   * @param buildID
   * @param propertyName
   */
  public ActiveBuildAttribute(final int buildID, final String propertyName, final String propertyValue) {
    this.buildID = buildID;
    this.propertyName = propertyName;
    this.propertyValue = propertyValue;
  }


  /**
   * Constructor.
   *
   * @param buildID
   * @param propertyName
   */
  public ActiveBuildAttribute(final int buildID, final String propertyName, final int propertyValue) {
    this(buildID, propertyName, Integer.toString(propertyValue));
  }


  /**
   * Constructor.
   *
   * @param buildID
   * @param propertyName
   */
  public ActiveBuildAttribute(final int buildID, final String propertyName, final long propertyValue) {
    this(buildID, propertyName, Long.toString(propertyValue));
  }


  /**
   * Returns build ID
   *
   * @return String
   * @hibernate.property column="ACTIVE_BUILD_ID" unique="false"
   * null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * The getter method for this property ID generator-parameter-1="SEQUENCE_GENERATOR"
   * generator-parameter-2="SEQUENCE_ID"
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getPropertyID() {
    return propertyID;
  }


  public void setPropertyID(final int propertyID) {
    this.propertyID = propertyID;
  }


  /**
   * Returns property name
   *
   * @return String
   * @hibernate.property column="NAME" unique="true"
   * null="false"
   */
  public String getPropertyName() {
    return propertyName;
  }


  public void setPropertyName(final String propertyName) {
    this.propertyName = propertyName;
  }


  /**
   * Returns property value
   *
   * @return String
   * @hibernate.property column="VALUE" unique="true"
   * null="false"
   */
  public String getPropertyValue() {
    return propertyValue;
  }


  public void setPropertyValue(final String propertyValue) {
    this.propertyValue = propertyValue;
  }


  public void setPropertyValue(final int propertyValue) {
    this.propertyValue = Integer.toString(propertyValue);
  }


  public void setPropertyValue(final long propertyValue) {
    this.propertyValue = Long.toString(propertyValue);
  }


  /**
   * Returns property value as int
   *
   * @return int property value
   */
  public int getPropertyValueAsInteger() throws NumberFormatException {
    return Integer.parseInt(propertyValue);
  }


  /**
   * Returns timestamp
   *
   * @return long
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getPropertyTimeStamp() {
    return propertyTimeStamp;
  }


  public void setPropertyTimeStamp(final long propertyTimeStamp) {
    this.propertyTimeStamp = propertyTimeStamp;
  }


  public String toString() {
    return "BuildConfigAttribute [" +
            "buildID=" + buildID +
            ", propertyID=" + propertyID +
            ", propertyName=" + propertyName +
            ", propertyValue=" + propertyValue +
            ", propertyTimeStamp=" + propertyTimeStamp +
            ']';
  }

}
