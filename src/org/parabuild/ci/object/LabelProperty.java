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
 * @hibernate.class table="LABEL_PROPERTY" dynamic-update="true"
 */
public final class LabelProperty implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -4414663957957415079L; // NOPMD

  // Label type
  public static final String LABEL_TYPE = "build.label.type";

  // Constant values

  // Label types
  public static final String LABEL_TYPE_NONE = "build.label.type.none";
  public static final String LABEL_TYPE_CUSTOM = "build.label.type.custom";

  // Attributes
  public static final String LABEL_CUSTOM_VALUE = "build.label.custom.value";
  public static final String LABEL_DELETE_ENABLED = "build.label.delete.enabled";
  public static final String LABEL_DELETE_OLD_DAYS = "build.label.delete.days";

  private int buildID = BuildConfig.UNSAVED_ID;
  private int propertyID = UNSAVED_ID;
  private String propertyName;
  private String propertyValue;
  private long propertyTimeStamp = 1;


  /**
   * Returns build ID
   *
   * @return String
   *
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
   * @return int
   *
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
   *
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
   *
   * @hibernate.property column="VALUE" unique="true"
   * null="false"
   */
  public String getPropertyValue() {
    return propertyValue;
  }


  public void setPropertyValue(final String propertyValue) {
    this.propertyValue = propertyValue;
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
   *
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getPropertyTimeStamp() {
    return propertyTimeStamp;
  }


  public void setPropertyTimeStamp(final long propertyTimeStamp) {
    this.propertyTimeStamp = propertyTimeStamp;
  }


  public String toString() {
    return "LabelProperty{" +
      "buildID=" + buildID +
      ", propertyID=" + propertyID +
      ", propertyName='" + propertyName + '\'' +
      ", propertyValue='" + propertyValue + '\'' +
      ", propertyTimeStamp=" + propertyTimeStamp +
      '}';
  }
}
