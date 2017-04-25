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

import java.sql.*;
import org.apache.cactus.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.common.*;

/**
 * Tests BugzillaDatabaseConnectorFactory on the server side.
 */
public final class SSTestBugzillaMySQLConnectionFactory extends ServletTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestBugzillaMySQLConnectionFactory.class); // NOPMD
  private BugzillaMySQLConnectionFactory connectionFactory;


  public SSTestBugzillaMySQLConnectionFactory(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_testConnectionToDB() throws Exception {
    final ConnectionTestResult connectionTestResult = connectionFactory.testConnectionToDB();
    assertTrue(connectionTestResult.successful());
    assertEquals("", connectionTestResult.message());
  }


  /**
   *
   */
  public void test_connect() throws Exception {
    final Connection conn = connectionFactory.connect();
    assertNotNull(conn);
    IoUtils.closeHard(conn);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBugzillaMySQLConnectionFactory.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    connectionFactory = new BugzillaMySQLConnectionFactory("localhost", 3307, "test_bugs", "test_bugs", "test_bugs");
  }
}
