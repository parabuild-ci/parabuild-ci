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
package org.parabuild.ci.relnotes;

import java.sql.Date;
import java.text.*;
import java.util.*;
import junit.framework.*;
import org.apache.cactus.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.error.*;

/**
 * Tests Bugzilla218DatabaseConnector on the server side.
 */
public class SSTestBugzilla216DatabaseConnector extends ServletTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestBugzilla216DatabaseConnector.class); // NOPMD

  private BugzillaDatabaseConnector bugzillaDatabaseConnector;
  private ErrorManager em;
  public static final String TEST_PRODUCT_NAME = "devenv";
  public static final String TEST_PRODUCT_VERSION = "devenv";


  public SSTestBugzilla216DatabaseConnector(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_requestBugsFromBugzilla() throws Exception {
    final Date fromDate = stringToDate("2003-02-21 21:03:14");
    final Date toDate = stringToDate("2003-05-15 23:07:06");
    final Collection result = bugzillaDatabaseConnector.
      requestBugsFromBugzilla(TEST_PRODUCT_NAME, TEST_PRODUCT_VERSION, fromDate, toDate);
    assertEquals(7, result.size());
    assertEquals("Number of errors", 0, em.errorCount());
  }


  /**
   *
   */
  public void test_requestBugsFromBugzillaCanHandleEmptyProductVersion() throws Exception {
    final Date fromDate = stringToDate("2003-02-21 21:03:14");
    final Date toDate = stringToDate("2003-05-15 23:07:06");
    // empty string as a version
    Collection result = bugzillaDatabaseConnector.
      requestBugsFromBugzilla(TEST_PRODUCT_NAME, "", fromDate, toDate);
    assertEquals(7, result.size());
    assertEquals("Number of errors", 0, em.errorCount());
    // null string as a version
    result = bugzillaDatabaseConnector.
      requestBugsFromBugzilla(TEST_PRODUCT_NAME, null, fromDate, toDate);
    assertEquals(7, result.size());
    assertEquals("Number of errors", 0, em.errorCount());
  }


  public static Date stringToDate(final String s) throws ParseException {
    return new Date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(s).getTime());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBugzilla216DatabaseConnector.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    em = ErrorManagerFactory.getErrorManager();
    em.clearAllActiveErrors();
    bugzillaDatabaseConnector = new Bugzilla216DatabaseConnector(new BugzillaMySQLConnectionFactory("localhost", 3307, "test_bugs", "test_bugs", "test_bugs"));
  }
}
