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
 * @hibernate.class table="STEP_RUN_ATTRIBUTE"
 * dynamic-update="true"
 */
public final class StepRunAttribute implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -3080999678374310690L; // NOPMD

  // JUNIT STATS - DO NOT CHANGE!
  public static final String ATTR_JUNIT_ERRORS = "stat.parabuild.junit.errors";
  public static final String ATTR_JUNIT_FAILURES = "stat.parabuild.junit.failures";
  public static final String ATTR_JUNIT_SUCCESSES = "stat.parabuild.junit.successes";
  public static final String ATTR_JUNIT_TESTS = "stat.parabuild.junit.tests";
  public static final String ATTR_JUNIT_SKIPS = "stat.parabuild.junit.skips";
  public static final String ATTR_JUNIT_FATALS = "stat.parabuild.junit.fatals";
  public static final String ATTR_JUNIT_EXPECTED_FAILS = "stat.parabuild.junit.expected.fails";
  public static final String ATTR_JUNIT_UNEXPECTED_PASSES = "stat.parabuild.junit.unexpected.passes";
  public static final String ATTR_JUNIT_WARNINGS = "stat.parabuild.junit.warnings";
  public static final String ATTR_JUNIT_TESTCASES = "stat.parabuild.junit.testcases";

  // CPPUNIT STATS - DO NOT CHANGE!
  public static final String ATTR_CPPUNIT_ERRORS = "stat.parabuild.cppunit.errors";
  public static final String ATTR_CPPUNIT_FAILURES = "stat.parabuild.cppunit.failures";
  public static final String ATTR_CPPUNIT_SUCCESSES = "stat.parabuild.cppunit.successes";
  public static final String ATTR_CPPUNIT_TESTS = "stat.parabuild.cppunit.tests";

  // NUNIT STATS - DO NOT CHANGE!
  public static final String ATTR_NUNIT_NOTRUN = "stat.parabuild.nunit.notrun";
  public static final String ATTR_NUNIT_FAILURES = "stat.parabuild.nunit.failures";
  public static final String ATTR_NUNIT_SUCCESSES = "stat.parabuild.nunit.successes";
  public static final String ATTR_NUNIT_TESTS = "stat.parabuild.nunit.tests";

  // PMD STATS - DO NOT CHANGE!
  public static final String ATTR_PMD_PROBLEMS = "stat.parabuild.pmd.violations";

  // Agent's time stamp
  public static final String ATTR_BUILDER_TIMESTAMP = "builder.time.stamp";

  // FIDNBUGS STATS - DO NOT CHANGE!
  public static final String ATTR_FINDBUGS_PROBLEMS = "stat.parabuild.findbugs.bugs";

  // CHECKSTYLE STATS - DO NOT CHANGE!
  public static final String ATTR_CHECKSTYLE_ERRORS = "stat.parabuild.checkstyle.errors";
  public static final String ATTR_CHECKSTYLE_FILES_WITH_PROBLEMS = "stat.parabuild.checkstyle.problem.files";
  public static final String ATTR_CHECKSTYLE_FILES = "stat.parabuild.checkstyle.files";

  // PHPUNIT STATS - DO NOT CHANGE!
  public static final String ATTR_PHPUNIT_FAILURES = "stat.parabuild.phpunit.failures";
  public static final String ATTR_PHPUNIT_SUCCESSES = "stat.parabuild.phpunit.successes";
  public static final String ATTR_PHPUNIT_TESTS = "stat.parabuild.phpunit.tests";
  public static final String ATTR_PHPUNIT_ERRORS = "stat.parabuild.phpunit.errors";

  // UNITESTPP STATS - DO NOT CHANGE!
  public static final String ATTR_UNITTESTPP_FAILURES = "stat.parabuild.unittestpp.failures";
  public static final String ATTR_UNITTESTPP_SUCCESSES = "stat.parabuild.unittestpp.successes";
  public static final String ATTR_UNITTESTPP_TESTS = "stat.parabuild.unittestpp.tests";
  public static final String ATTR_UNITTESTPP_ERRORS = "stat.parabuild.unittestpp.errors";

  // Boost tests - do not change!
  public static final String ATTR_BOOST_TEST_ERRORS = "stat.parabuild.boost.test.errors";
  public static final String ATTR_BOOST_TEST_SUCCESSES = "stat.parabuild.boost.test.successes";
  public static final String ATTR_BOOST_TEST_TESTS = "stat.parabuild.boost.test.tests";

  private int stepRunID = StepRun.UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private String name = null;
  private String value = null;
  private long timeStamp = 1L;


  /**
   * Default constructor
   */
  public StepRunAttribute() {
  }


  /**
   * Constructor
   *
   * @param name of the attribute
   * @param value of the attribute
   */
  public StepRunAttribute(final int stepRunID, final String name, final String value) {
    this.stepRunID = stepRunID;
    this.value = value;
    this.name = name;
  }


  /**
   * Constructor
   *
   * @param name of the attribute
   * @param intValue int of the attribute
   */
  public StepRunAttribute(final int stepRunID, final String name, final int intValue) {
    this(stepRunID, name, Integer.toString(intValue));
  }


  /**
   * Constructor
   *
   * @param name of the attribute
   * @param longValue int of the attribute
   */
  public StepRunAttribute(final int stepRunID, final String name, final long longValue) {
    this(stepRunID, name, Long.toString(longValue));
  }


  /**
   * Returns build ID
   *
   * @return String
   *
   * @hibernate.property column="STEP_RUN_ID" unique="false"
   * null="false"
   */
  public int getStepRunID() {
    return stepRunID;
  }


  public void setStepRunID(final int stepRunID) {
    this.stepRunID = stepRunID;
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


  public void setID(final int id) {
    this.ID = id;
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


  public void setValue(final int value) {
    this.value = Integer.toString(value);
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


  public String toString() {
    return "StepRunAttribute{" +
      "stepRunID=" + stepRunID +
      ", ID=" + ID +
      ", name='" + name + '\'' +
      ", value='" + value + '\'' +
      ", timeStamp=" + timeStamp +
      '}';
  }
}
