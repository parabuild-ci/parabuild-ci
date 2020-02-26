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
package org.parabuild.ci.configuration;

import java.io.*;
import java.sql.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.*;

/**
 * Tests DatabaseCreator
 */
public class SSTestDatabaseCreator extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestDatabaseCreator.class);
  private File testDatabasePath = null;


  public SSTestDatabaseCreator(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_process() throws Exception {
    //create
    final DatabaseCreator databaseCreator = new DatabaseCreator();
    databaseCreator.createDatabase(testDatabasePath);
    TestHelper.assertExists(new File(testDatabasePath + ".data"));
    TestHelper.assertExists(new File(testDatabasePath + ".properties"));
    TestHelper.assertExists(new File(testDatabasePath + ".script"));

    // assert can connect to the newly created database.
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      conn = HSQLDBUtils.createHSQLConnection(testDatabasePath);
      stmt = conn.createStatement();
      rs = stmt.executeQuery("select * from SYSTEM_PROPERTY");
      int count = 0;
      while (rs.next()) count++;
      assertTrue(count > 0);
      stmt.execute("SHUTDOWN");
    } finally {
      IoUtils.closeHard(rs);
      IoUtils.closeHard(stmt);
      IoUtils.closeHard(conn);
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestDatabaseCreator.class);
  }


  protected void setUp() throws Exception {
//    super.setUp();
    this.testDatabasePath = new File(TestHelper.getTestTempDir(), SSTestDatabaseCreator.class.getName() + "/test_db");
    IoUtils.deleteFileHard(testDatabasePath.getParentFile());
    testDatabasePath.getParentFile().mkdirs();
  }
}
