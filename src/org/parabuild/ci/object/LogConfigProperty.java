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
 * Stored log config properties
 *
 * @hibernate.class table="LOG_CONFIG_PROPERTY" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class LogConfigProperty implements Serializable, ObjectConstants {

  public static final String ATTR_FILE_EXTENSIONS = "log.config.file.extensions";

  public static final String ATTR_INCLUDE_SUBDIRS = "log.config.include.subdirs";

  public static final String ATTR_HTML_INDEX_FILE = "log.config.html.index";

  public static final String ATTR_NOTIFY_ABOUT_MISSING_INDEX = "notify.about.missing.index";

  public static final String ATTR_IGNORE_TIMESTAMP = "ignore.time.stamp";

  private static final long serialVersionUID = 3380452351942166204L; // NOPMD

  private int logConfigID = UNSAVED_ID;

  private int ID = -1;

  private String name;

  private String value;

  private long timeStamp = 1;


  /**
   * The getter method for this property ID
   *
   * @return int
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
   * @hibernate.property column="LOG_CONFIG_ID" unique="false"
   * null="false"
   */
  public int getLogConfigID() {

    return logConfigID;
  }


  public void setLogConfigID(final int logConfigID) {

    this.logConfigID = logConfigID;
  }


  /**
   * Returns property name
   *
   * @return String
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
   * @throws NumberFormatException if there was an error converting {@link #value} to integer.
   */
  public int getValueAsInteger() {

    return Integer.parseInt(value);
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

    return "LogConfigProperty{" +
            "logConfigID=" + logConfigID +
            ", ID=" + ID +
            ", name='" + name + '\'' +
            ", value='" + value + '\'' +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
