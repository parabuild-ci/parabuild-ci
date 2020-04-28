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
 * Stored build configuration
 *
 * @hibernate.class table="SCHEDULE_PROPERTY"
 * dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ScheduleProperty implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -6833252781560090419L; // NOPMD

  // defaults for props that are not set
  public static final int DEFAULT_COOLDOWN_INTERVAL = 0;

  // Default poll interval
  public static final int DEFAULT_POLL_INTERVAL = 10;

  // Automatic build proerties
  public static final String AUTO_POLL_INTERVAL = "auto.poll.interval";
  public static final String AUTO_CLEAN_CHECKOUT = "auto.clean.checkout.interval";
  public static final String AUTO_CLEAN_CHECKOUT_IF_BROKEN = "auto.clean.checkout.if.broken";
  public static final String AUTO_REBUILD_IF_BROKEN = "auto.rebuild.if.broken";
  public static final String AUTO_COOLDOWN_INTERVAL = "auto.cooldown.interval";
  public static final String AUTO_BUILD_ONE_BY_ONE = "build.one.by.one";
  public static final String STICKY_AGENT = "sticky.agent";
  public static final String CLEAN_CHECKOUT_ON_AGENT_CHANGE = "clean.checkout.on.agent.change";

  public static final String SERIALIZE = "serialize";

  /**
   * If set to OPTION_CHECKED, the scheduled build will run even
   * if there are no changes.
   *
   * @see #OPTION_CHECKED
   */
  public static final String RUN_IF_NO_CHANGES = "run.if.no.changes";

  private int buildID = BuildConfig.UNSAVED_ID;
  private int propertyID = UNSAVED_ID;
  private String propertyName;
  private String propertyValue;
  private long propertyTimeStamp = 1L;


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
   * The getter method for this property.
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
    return "ScheduleProperty{" +
            "buildID=" + buildID +
            ", propertyID=" + propertyID +
            ", propertyName='" + propertyName + '\'' +
            ", propertyValue='" + propertyValue + '\'' +
            ", propertyTimeStamp=" + propertyTimeStamp +
            '}';
  }
}
