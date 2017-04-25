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

/**
 * Stored build properties
 *
 * @hibernate.class table="MERGE_CONFIGURATION_ATTRIBUTE"
 * dynamic-update="true"
 */
public final class MergeConfigurationAttribute implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -3080999678374310690L; // NOPMD

  public static final String NAG_DAY_SENT_LAST_TIME = "nag.day.sent.last.time";
  public static final String SOURCE_BUILD_CONFIGURATION_ID_CHECKED_LAST_TIME = "source.build.configuration.id.checked.last.time";
  public static final String SOURCE_BUILD_RUN_ID_CHECKED_LAST_TIME = "source.build.run.id.checked.last.time";

  private int mergeConfigurationID = MergeConfiguration.UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private String name = null;
  private String value = null;
  private long timeStamp = 1;


  /**
   * Default constructor
   */
  public MergeConfigurationAttribute() {
  }


  /**
   * Constructor
   *
   * @param name of the attribute
   * @param value of the attribute
   */
  public MergeConfigurationAttribute(final int mergeConfigurationID, final String name, final String value) {
    this.mergeConfigurationID = mergeConfigurationID;
    this.value = value;
    this.name = name;
  }


  /**
   * Constructor
   *
   * @param name of the attribute
   * @param intValue int of the attribute
   */
  public MergeConfigurationAttribute(final int stepRunID, final String name, final int intValue) {
    this(stepRunID, name, Integer.toString(intValue));
  }


  /**
   * Constructor
   *
   * @param name of the attribute
   * @param longValue int of the attribute
   */
  public MergeConfigurationAttribute(final int stepRunID, final String name, final long longValue) {
    this(stepRunID, name, Long.toString(longValue));
  }


  /**
   * Returns build ID
   *
   * @return String
   *
   * @hibernate.property column="MERGE_CONFIGURATION_ID" unique="false"
   * null="false"
   */
  public int getMergeConfigurationID() {
    return mergeConfigurationID;
  }


  public void setMergeConfigurationID(final int mergeConfigurationID) {
    this.mergeConfigurationID = mergeConfigurationID;
  }


  /**
   * The getter method for this property ID
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
   * Returns property name
   *
   * @return String
   *
   * @hibernate.property column="NAME" unique="true"
   * null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Returns property value
   *
   * @return String
   *
   * @hibernate.property column="VALUE" unique="true"
   * null="false"
   */
  public String getValue() {
    return value;
  }


  public void setValue(final String value) {
    this.value = value;
  }


  public void setValue(final int intValue) {
    this.value = Integer.toString(intValue);
  }


  /**
   * Returns property value as int
   *
   * @return int property value
   */
  public int getValueAsInt() throws NumberFormatException {
    return Integer.parseInt(value);
  }


  /**
   * Returns property value as int
   *
   * @return int property value
   */
  public long getValueAsLong() throws NumberFormatException {
    return Long.parseLong(value);
  }


  /**
   * Returns timestamp
   *
   * @return long
   *
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * @return integer value
   *
   * @exception NumberFormatException if the string does not
   *  contain a parsable integer.
   */
  public int getIntValue() {
    return Integer.parseInt(value);
  }


  public String toString() {
    return "MergeConfigurationAttribute{" +
      "mergeConfigurationID=" + mergeConfigurationID +
      ", ID=" + ID +
      ", name='" + name + '\'' +
      ", value='" + value + '\'' +
      ", timeStamp=" + timeStamp +
      '}';
  }
}
