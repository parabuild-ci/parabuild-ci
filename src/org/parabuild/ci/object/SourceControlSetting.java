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

import viewtier.util.StringUtils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Stored build configuration
 *
 * @hibernate.class table="SOURCE_CONTROL_PROPERTY"
 * dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class SourceControlSetting implements Serializable, ObjectConstants {

  /**
   * This comparator is used to sort SourceControlSetting lists
   * in direct property name order.
   */
  public static final Comparator PROPERTY_NAME_COMPARATOR = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      final SourceControlSetting c1 = (SourceControlSetting) o1;
      final SourceControlSetting c2 = (SourceControlSetting) o2;
      //noinspection ObjectEquality
      if (c1 == c2) {
        return 0; // NOPMD
      }
      return c1.getPropertyName().compareTo(c2.getPropertyName());
    }
  };


  private static final long serialVersionUID = -4844436413027227167L; // NOPMD


  private int buildID = BuildConfig.UNSAVED_ID;
  private int propertyID = UNSAVED_ID;
  private String propertyName = null;
  private String propertyValue = null;
  private long propertyTimeStamp = 1;


  /**
   * Default constructor.
   */
  public SourceControlSetting() {
  }


  /**
   * Constructor.
   *
   * @param buildID       build ID
   * @param propertyName  property name
   * @param propertyValue property value
   */
  public SourceControlSetting(final int buildID, final String propertyName, final String propertyValue) {
    this.buildID = buildID;
    this.propertyName = propertyName;
    this.propertyValue = propertyValue;
  }


  /**
   * Constructor.
   *
   * @param buildID       build ID
   * @param propertyName  property name
   * @param propertyValue property value
   */
  public SourceControlSetting(final int buildID, final String propertyName, final int propertyValue) {
    this.buildID = buildID;
    this.propertyName = propertyName;
    this.propertyValue = Integer.toString(propertyValue);
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
   * Returns configuration name
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


  public void setPropertyValue(final int intPropertyValue) {
    this.propertyValue = Integer.toString(intPropertyValue);
  }


  /**
   * Helper method to return property value as int
   *
   * @return return property value as int.
   * @throws NumberFormatException if this setting is not a valid integer.
   */
  public int getPropertyValueAsInt() throws NumberFormatException {
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


  public static String getValue(final SourceControlSetting setting, final String defaultValue) {
    if (setting == null) {
      return defaultValue;
    }
    return StringUtils.isBlank(setting.propertyValue) ? defaultValue : setting.propertyValue;
  }


  public String toString() {
    return "SourceControlSetting{" +
            "buildID=" + buildID +
            ", propertyID=" + propertyID +
            ", propertyName='" + propertyName + '\'' +
            ", propertyValue='" + propertyValue + '\'' +
            ", propertyTimeStamp=" + propertyTimeStamp +
            '}';
  }
}
