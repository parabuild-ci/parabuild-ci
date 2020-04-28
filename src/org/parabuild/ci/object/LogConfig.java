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

import net.sf.hibernate.CallbackException;
import net.sf.hibernate.Lifecycle;
import net.sf.hibernate.Session;

import java.io.Serializable;

/**
 * Build log header
 *
 * @hibernate.class table="LOG_CONFIG" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 * @noinspection StaticInheritance
 */
public final class LogConfig implements Serializable, ObjectConstants, Lifecycle {

  private static final long serialVersionUID = -126501075513148853L; // NOPMD

  public static final byte LOG_TYPE_TEXT_FILE = 1;
  public static final byte LOG_TYPE_TEXT_DIR = 2;
  public static final byte LOG_TYPE_JNUIT_XML_DIR = 3;
  public static final byte LOG_TYPE_PMD_XML_FILE = 4;
  public static final byte LOG_TYPE_HTML_FILE = 5;
  public static final byte LOG_TYPE_HTML_DIR = 6;
  public static final byte LOG_TYPE_NUNIT_XML_DIR = 7;
  public static final byte LOG_TYPE_FINDBUGS_XML_FILE = 8;
  public static final byte LOG_TYPE_CPPUNIT_XML_DIR = 9;
  public static final byte LOG_TYPE_CHECKSTYLE_XML_FILE = 10;
  public static final byte LOG_TYPE_PHPUNIT_XML_DIR = 11;
  public static final byte LOG_TYPE_UNITTESTPP_XML_DIR = 12;
  public static final byte LOG_TYPE_GENERIC_TEST_RESULT = 13;
  public static final byte LOG_TYPE_BOOST_TEST_XML_DIR = 14;
  public static final byte LOG_TYPE_GOOGLETEST_XML_FILE = 15;
  public static final byte LOG_TYPE_SQUISH_TESTER_XML_FILE = 16;

  private int ID = -1;
  private int buildID = BuildConfig.UNSAVED_ID;
  private String description;
  private String path = "";
  private long timeStamp;
  private byte type;


  /**
   * Returns change ID
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID" unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Returns change set ID
   *
   * @return int
   * @hibernate.property column = "BUILD_ID" unique="false" null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * @hibernate.property column = "PATH" unique="false"
   * null="false"
   */
  public String getPath() {
    return path;
  }


  public void setPath(final String path) {
    this.path = path;
  }


  /**
   * @hibernate.property column = "DESCRIPTION" unique="false"
   * null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @hibernate.property column = "TYPE" unique="false"
   * null="false"
   */
  public byte getType() {
    return type;
  }


  public void setType(final byte type) {
    this.type = type;
  }


  /**
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  public boolean onDelete(final Session session) {
    return NO_VETO;
  }


  public void onLoad(final Session session, final Serializable serializable) {
  }


  public boolean onSave(final Session session) throws CallbackException {
    validate();
    return NO_VETO;
  }


  private void validate() throws CallbackException {
    if (type <= 0) {
      throw new CallbackException("Log type is undefined");
    }
  }


  public boolean onUpdate(final Session session) throws CallbackException {
    validate();
    return NO_VETO;
  }


  public String toString() {
    return "LogConfig {" +
            "buildID=" + buildID +
            ", ID=" + ID +
            ", description='" + description + '\'' +
            ", path='" + path + '\'' +
            ", timeStamp=" + timeStamp +
            ", type=" + type +
            '}';
  }
}

