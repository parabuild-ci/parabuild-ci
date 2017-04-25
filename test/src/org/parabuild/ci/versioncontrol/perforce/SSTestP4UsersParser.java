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
package org.parabuild.ci.versioncontrol.perforce;

import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;

import java.io.File;
import java.util.Map;

/**
 */
public class SSTestP4UsersParser extends ServersideTestCase {

  private static final String BUILD_USER = "BuildUser";
  private static final String BUILD_USER_LOWER_CASE = BUILD_USER.toLowerCase();

  private P4UsersParser usersParser = null;
  private ErrorManager errorManager = null;


  public SSTestP4UsersParser(final String s) {
    super(s);
  }


  public void test_parse() throws Exception {
    usersParser.setUsersFile(new File(TestHelper.getTestDataDir(), "test_p4_users.txt"));
    final Map result = usersParser.parse();
    assertEquals(2, result.size());

    assertNotNull(result.get("test1"));
    assertEquals("test1@test", result.get("test1"));

    assertNotNull(result.get("test2"));
    assertEquals("test2@test", result.get("test2"));

    assertEquals(1, errorManager.errorCount());
  }


  public void test_parseProblemUser() throws Exception {
    usersParser.setUsersFile(new File(TestHelper.getTestDataDir(), "test_p4_problem.user.list.txt"));
    final Map result = usersParser.parse();
    assertEquals(1, result.size());

    assertNotNull(result.get(BUILD_USER));
    assertEquals("buildmeister@inin.com", result.get(BUILD_USER));
    assertEquals(0, errorManager.errorCount());
  }


  public void test_ignoresUserNameCase() throws Exception {
    usersParser.setUsersFile(new File(TestHelper.getTestDataDir(), "test_p4_problem.user.list.txt"));
    usersParser.setCaseSensitiveUserNames(false);
    final Map result = usersParser.parse();
    assertEquals(1, result.size());

    assertNull(result.get(BUILD_USER));
    assertNotNull(result.get(BUILD_USER_LOWER_CASE));
    assertEquals("buildmeister@inin.com", result.get(BUILD_USER_LOWER_CASE));
    assertEquals(0, errorManager.errorCount());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestP4UsersParser.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    usersParser = new P4UsersParser();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    enableErrorManagerStackTraces();
  }
}
