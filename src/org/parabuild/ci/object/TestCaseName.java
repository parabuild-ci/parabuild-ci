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
 * Reference to hold a name of a test case.
 *
 * @hibernate.class table="TEST_CASE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class TestCaseName implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 136142447371206393L; // NOPMD

  private int ID = UNSAVED_ID;
  private int testSuiteNameID = TestSuiteName.UNSAVED_ID;
  private String name;


  /**
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
   * Returns package ID
   *
   * @hibernate.property column="TEST_PACKAGE_ID" unique="false"
   * null="false"
   */
  public int getTestSuiteNameID() {
    return testSuiteNameID;
  }

  public void setTestSuiteNameID(final int testSuiteNameID) {
    this.testSuiteNameID = testSuiteNameID;
  }

  /**
   * @hibernate.property column = "NAME" unique="true"
   * null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }

  public String toString() {
    return "TestCase{" +
            "ID=" + ID +
            ", packageID=" + testSuiteNameID +
            ", name='" + name + '\'' +
            '}';
  }
}
