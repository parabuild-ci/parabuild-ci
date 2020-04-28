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

import org.parabuild.ci.util.StringUtils;

import java.io.Serializable;

/**
 * Stored build properties
 *
 * @hibernate.class table="BUILD_RUN_ATTRIBUTE"
 * dynamic-update="true"
 */
public final class BuildRunAttribute implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -3080999678374310690L; // NOPMD

  /**
   * Accumulated from all steps errors.
   */
  public static final String ATTR_JUNIT_ERRORS = "stat.parabuild.junit.errors";

  /**
   * Accumulated from all steps failures.
   */
  public static final String ATTR_JUNIT_FAILURES = "stat.parabuild.junit.failures";

  /**
   * Accumulated from all steps successes.
   */
  public static final String ATTR_JUNIT_SUCCESSES = "stat.parabuild.junit.successes";

  /**
   * Accumulated from all steps totals.
   */
  public static final String ATTR_JUNIT_TESTS = "stat.parabuild.junit.tests";


  /**
   * Number of tests that didn't run.
   */
  public static final String ATTR_JUNIT_NOTRUN = "stat.parabuild.junit.notrun";

  public static final String ATTR_JUNIT_FATALS = "stat.parabuild.junit.fatals";
  public static final String ATTR_JUNIT_EXPECTED_FAILS = "stat.parabuild.junit.expected.fails";
  public static final String ATTR_JUNIT_UNEXPECTED_PASSES = "stat.parabuild.junit.unexpected.passes";
  public static final String ATTR_JUNIT_WARNINGS = "stat.parabuild.junit.warnings";


  public static final String ATTR_CLEAN_CHECKOUT = "attr.clean.checkout";
  public static final String ATTR_BUILDER_TIMESTAMP = "attr.builder.timestamp";
  public static final String ATTR_STARTED_USER_ID = "attr.started.user.id";
  public static final String ATTR_RE_RUN_BUILD_RUN_ID = "attr.re.run.build.run.id";
  public static final String ATTR_NOTE = "attr.note";

  /**
   * This attribute contains build version if it was used
   * by this build run.
   */
  public static final String VERSION = "version";

  /**
   * This attribute contains build version counter if it was
   * used by this build run.
   */
  public static final String VERSION_COUNTER = "version.counter";

  /**
   * This attribute contains build version if it was generated
   * for this build run.
   * <p/>
   * The difference from the {@link #VERSION} is that {@link
   * #VERSION} is stored only once if a build is successful
   * and is checked for duplicates.
   */
  public static final String GENERATED_VERSION = "generated.version";

  /**
   * This attribute contains build version counter if it was
   * generated for this build run.
   * <p/>
   * The difference from the {@link
   * #VERSION_COUNTER} is that <code>GENERATED_VERSION_COUNTER</code> is stored only once if a
   * build is successful and is checked for duplicates.
   */
  public static final String GENERATED_VERSION_COUNTER = "generated.version.counter";

  /**
   * This attribute contains an ID of leading build run when
   * this build run is a dependent parallel build.
   */
  public static final String ATTR_LEAD_BUILD_RUN_ID = "lead.build.run.id";


  /**
   * PMD violations statistics.
   */
  public static final String ATTR_PMD_PROBLEMS = "stat.parabuild.pmd.violations";

  /**
   * Findbugs bugs statistics.
   */
  public static final String ATTR_FINDBUGS_PROBLEMS = "stat.parabuild.findbugs.bugs";


  /**
   * Checkstyle error statistics.
   */
  public static final String ATTR_CHECKSTYLE_ERRORS = "stat.parabuild.checkstyle.errors";
  public static final String ATTR_CHECKSTYLE_FILES_WITH_PROBLEMS = "stat.parabuild.checkstyle.problem.files";
  public static final String ATTR_CHECKSTYLE_FILES = "stat.parabuild.checkstyle.files";

  /**
   * Time to fix.
   */
  public static final String ATTR_TIME_TO_FIX = "stat.time.to.fix";

  /**
   * Time to fix moving average.
   */
  public static final String ATTR_TIME_TO_FIX_MOVING_AVERAGE = "stat.time.to.fix.ma";

  /**
   * The build ID this build run id fixes.
   */
  public static final String ATTR_FIXES_BUILD_ID = "fixes.build.run.id";


  /**
   * Number of new change lists. Can be missing if a build
   * run comes from the time when we were not storing it.
   */
  public static final String NEW_CHANGE_LIST_IN_THIS_BUILD = "new.change.lists";

  /**
   * Checkout directory. Maybe null if wasn't set before.
   */
  public static final String CHECKOUT_DIRECTORY = "checkout.dir";

  /**
   * Time for the build run to sync to the build change list.
   */
  public static final String SYNC_TIME = "sync.time";

  /**
   * Optional ID of the user that stopped the build.
   */
  public static final String STOPPED_BY_USER_ID = "stopped.by.user.id";

  /**
   * Contains value "yes" if this build run has tests. Used for quick finding of information about
   * tests in previous build runs.
   */
  public static final String HAS_TESTS = "has.tests";

  /**
   * Value "yes"
   */
  public static final String VALUE_YES = "yes";

  /**
   * Number of new broken tests since last build run.
   */
  public static final String NEW_BROKEN_TESTS = "new.broken.tests";

  /**
   * Counter that shows number of new tests.
   */
  public static final String NEW_TESTS = "new.tests";


  /**
   * Actual agent host.
   */
  public static final String AGENT_HOST = "agent.host";

  /**
   * Perforce client name
   */
  public static final String REFERENCE_P4_CLIENT_NAME = "reference.p4.client";

  private int buildRunID = BuildRun.UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private String name;
  private String value;
  private long timeStamp = 1;


  /**
   * Default constructor.
   */
  public BuildRunAttribute() {
  }


  /**
   * Constructor for new (unsaved) BuildRunAttribute
   */
  public BuildRunAttribute(final int buildRunID, final String name, final String value) {
    this.buildRunID = buildRunID;
    this.name = name;
    this.value = value;
  }


  /**
   * Constructor for new (unsaved) BuildRunAttribute
   */
  public BuildRunAttribute(final int buildRunID, final String name, final int value) {
    this(buildRunID, name, Integer.toString(value));
  }


  /**
   * Constructor for new (unsaved) BuildRunAttribute with boolean
   * value.
   */
  public BuildRunAttribute(final int buildRunID, final String name, final boolean value) {
    this(buildRunID, name, Boolean.toString(value));
  }


  /**
   * Constructor for new (unsaved) BuildRunAttribute with long
   * value.
   */
  public BuildRunAttribute(final int buildRunID, final String name, final long value) {
    this(buildRunID, name, Long.toString(value));
  }


  /**
   * Returns build ID
   *
   * @return String
   * @hibernate.property column="BUILD_RUN_ID" unique="false"
   * null="false"
   */
  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


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
   */
  public int getValueAsInteger() throws NumberFormatException {
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
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  public boolean isValueBlank() {
    return StringUtils.isBlank(value);
  }


  public String toString() {
    return "BuildRunAttribute{" +
            "buildRunID=" + buildRunID +
            ", ID=" + ID +
            ", name='" + name + '\'' +
            ", value='" + value + '\'' +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
