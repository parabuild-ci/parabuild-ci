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
 * @hibernate.class table="BUILD_ATTRIBUTE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 * @noinspection StaticInheritance
 */
public final class BuildConfigAttribute implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -6426274907331328221L; // NOPMD

  public static final String LOG_RETENTION_DAYS = "build.log.retention.days";
  public static final String LOG_PACK_DAYS = "build.log.pack.days";
  public static final String MESSAGE_PREFIX = "build.msg.prefix";
  public static final String RESULT_RETENTION_DAYS = "build.result.retention.days";
  public static final String SEND_FAILURE_ONCE = "build.send.failure.once";
  public static final String SEND_ONLY_LAST_STEP_RESULT = "build.send.only.last.step.result";
  public static final String SEND_FAILURES_ONLY = "build.send.failures.only";
  public static final String SEND_FILE_DETAILS = "build.send.email.file.details";
  public static final String SEND_START_NOTICE_FOR_FIRST_STEP_ONLY = "build.send.start.notice.for.first.step.only";
  public static final String SOURCE_BUILD_CONFIG_ID = "build.source.config.id";
  public static final String ENABLE_VERSION = "enable.version";
  public static final String VERSION_TEMPLATE = "version.template";
  public static final String VERSION_COUNTER_INCREMENT_MODE = "version.counter.increment.mode";
  public static final String VERSION_COUNTER_INCREMENT_IF_BROKEN = "version.counter.increment.if.broken";
  public static final String LAST_SAVED_TAB = "last.saved.tab";
  public static final String USE_FIRST_PARAMETER_VALUE_AS_DEFAULT = "use.first.parameter.value.as.default";
  public static final String PROMOTION_POLICY_ID = "promotion.policy.id";
  public static final String BUILD_CREATOR = "build.creator";

  /**
   * Increment version counter automatically, with an ability to
   * move counter forward or reset it.
   */
  public static final byte VERSION_COUNTER_INCREMENT_MODE_AUTOMATIC = 0;

  /**
   * Increment version counter manually, with an ability to move
   * counter forward or reset it.
   */
  public static final byte VERSION_COUNTER_INCREMENT_MODE_MANUAL = 1;

  /**
   * If checked automatic deleting of old build results is
   * eneabled. Default is disabled (unchecked).
   *
   * @see #OPTION_CHECKED
   * @see #OPTION_UNCHECKED
   */
  public static final String ENABLE_AUTOMATIC_DELETING_OLD_BUILD_RESULTS = "enable.deleting.old.build.results";

  /**
   * If checked notifications are set to watchers only.
   * Default is disabled (unchecked).
   *
   * @see #OPTION_CHECKED
   * @see #OPTION_UNCHECKED
   */
  public static final String NOTIFY_WATCHERS_ONLY = "notify.watchers.only";

  /**
   * If checked notifications about build starts are sent.
   *
   * @see #OPTION_CHECKED
   * @see #OPTION_UNCHECKED
   */
  public static final String SEND_START_NOTICE = "send.start.notice";

  /**
   * If checked Parabuild will show parallel's build results
   * on it's leader's results page.
   *
   * @see #OPTION_CHECKED
   * @see #OPTION_UNCHECKED
   */
  public static final String SHOW_RESULTS_ON_LEADER_PAGE = "show.results.on.leader.page";

  /**
   * If checked Parabuild will include build administrator
   * to the notification list.
   *
   * @see #OPTION_CHECKED
   * @see #OPTION_UNCHECKED
   */
  public static final String NOTIFY_BUILD_ADMINISTRATOR = "notify.build.admin";

  /**
   * An optional ID of a dependent build.
   *
   * @see #OPTION_CHECKED
   * @see #OPTION_UNCHECKED
   */
  public static final String DEPENDENT_BUILD_ID = "dependent.build.id";

  /**
   * Defines if a build should fail if it cannot start the dependent build.
   *
   * @see #OPTION_CHECKED
   * @see #OPTION_UNCHECKED
   */
  public static final String FAIL_IF_DEPENDENT_BUILD_CANNOT_BE_STARTED = "fail.if.dependent.build.cannot.be.started";

  /**
   * Contains optional build start instructions.
   */
  public static final String BUILD_INSTRUCTIONS = "build.instructions";

  /**
   * Contains optional build start instructions URL.
   */
  public static final String BUILD_INSTRUCTIONS_URL = "build.instructions.url";

  /**
   * Indicates if Perforce parameters should be displayed at the build start screen
   */
  public static final String SHOW_PERFORCE_PARAMETERS = "show.perforce.parameters";

  /**
   * Show Bazaar parameters.
   */
  public static final String SHOW_BAZAAR_PARAMETERS = "show.bazaar.parameters";

  /**
   * Show Mercurial parameters.
   */
  public static final String SHOW_MERCURIAL_PARAMETERS = "show.mercurial.parameters";

  private int buildID = BuildConfig.UNSAVED_ID;
  private int propertyID = UNSAVED_ID;
  private String propertyName = null;
  private String propertyValue = null;
  private long propertyTimeStamp = 1;


  /**
   * Default constructor. Required by hibernate.
   */
  public BuildConfigAttribute() {
  }


  /**
   * Constructor.
   *
   * @param buildID
   * @param propertyName
   */
  public BuildConfigAttribute(final int buildID, final String propertyName, final String propertyValue) {
    this.buildID = buildID;
    this.propertyName = propertyName;
    this.propertyValue = propertyValue;
  }


  /**
   * Constructor.
   *
   * @param buildID
   * @param propertyName
   * @param propertyValue
   */
  public BuildConfigAttribute(final int buildID, final String propertyName, final int propertyValue) {
    this(buildID, propertyName, Integer.toString(propertyValue));
  }


  /**
   * Returns build ID
   *
   * @return String
   * @hibernate.property column="BUILD_ID" unique="false"
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
