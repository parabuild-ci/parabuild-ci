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
package org.parabuild.ci.versioncontrol;

import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;

import java.io.File;
import java.util.Map;

/**
 */
public class SSTestCVSUsersParser extends ServersideTestCase {

  private CVSUsersParser usersParser = null;
  private ErrorManager errorManager = null;


  public SSTestCVSUsersParser(final String s) {
    super(s);
  }


  public void test_parse() {
    usersParser.setUsersFile(new File(TestHelper.getTestDataDir(), "test_cvs_cvsroot_users.txt"));
    final Map result = usersParser.parse();
    assertEquals(2, result.size());

    assertNotNull(result.get("test1"));
    assertEquals("test1@test", result.get("test1"));

    assertNotNull(result.get("test2"));
    assertEquals("test2@test", result.get("test2"));

    assertEquals(1, errorManager.errorCount());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestCVSUsersParser.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    usersParser = new CVSUsersParser();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    errorManager.enableNotification(false);
    enableErrorManagerStackTraces();
  }
}
