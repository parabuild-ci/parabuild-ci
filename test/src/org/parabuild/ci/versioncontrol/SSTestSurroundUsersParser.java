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

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.error.*;

/**
 */
public class SSTestSurroundUsersParser extends ServersideTestCase {

  private SurroundUsersParser usersParser = null;
  private ErrorManager errorManager = null;


  public SSTestSurroundUsersParser(final String s) {
    super(s);
  }


  public void test_parse() throws Exception {
    final Map result = usersParser.parse();
    assertEquals(2, result.size());

    assertNotNull(result.get("test_user"));
    assertEquals("test@user.com", result.get("test_user"));

    assertNotNull(result.get("test_user1"));
    assertEquals("test1@user.com", result.get("test_user1"));

    //assertEquals(1, errorManager.errorCount());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestSurroundUsersParser.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    usersParser = new SurroundUsersParser(new File(TestHelper.getTestDataDir(), "test_surround_users.txt"));
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
  }
}
