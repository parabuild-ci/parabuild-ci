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
 * Stored result config properties
 *
 * @hibernate.class table="RESULT_CONFIG_PROPERTY" dynamic-update="true"
 * dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ResultConfigProperty implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 3380452351942166204L; // NOPMD

  public static final String ATTR_FILE_EXTENSIONS = "result.config.file.extensions";
  public static final String ATTR_INCLUDE_SUBDIRS = "result.config.include.subdirs";
  public static final String ATTR_HTML_INDEX_FILE = "result.config.html.index";
  public static final String ATTR_TEST_URL = "result.config.test.url";

  private int resultConfigID = UNSAVED_ID;
  private int ID = -1;
  private String name = null;
  private String value = null;
  private long timeStamp = 1;


  public ResultConfigProperty() {
  }


  public ResultConfigProperty(final int resultConfigID, final String name, final String value) {
    this.resultConfigID = resultConfigID;
    this.name = name;
    this.value = value;
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
   * Returns build ID
   *
   * @return String
   *
   * @hibernate.property column="RESULT_CONFIG_ID" unique="false"
   * null="false"
   */
  public int getResultConfigID() {
    return resultConfigID;
  }


  public void setResultConfigID(final int resultConfigID) {
    this.resultConfigID = resultConfigID;
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


  public void setValue(final int propertyValue) {
    this.value = Integer.toString(propertyValue);
  }


  /**
   * Returns property value as int
   *
   * @return int property value
   */
  public int getValueAsInteger() throws NumberFormatException {
    return Integer.parseInt(value);
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


  public String toString() {
    return "ResultConfigProperty{" +
      "resultConfigID=" + resultConfigID +
      ", ID=" + ID +
      ", name='" + name + '\'' +
      ", value='" + value + '\'' +
      ", timeStamp=" + timeStamp +
      '}';
  }
}
