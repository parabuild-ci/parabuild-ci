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
package org.parabuild.ci.build;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.apache.commons.logging.*;
import org.dbunit.database.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;


/**
 * Tests ServersideTestCase
 */
public class SSTestServersideTestCase extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestServersideTestCase.class);

  public static final File DATASET_FILE = new File(System.getProperty("test.dataset"));
  public static final int TEST_RUN_COUNT = 1;


  public SSTestServersideTestCase(final String s) {
    super(s);
  }


  public void test_performance1() throws Exception {
    for (int i = 0; i < TEST_RUN_COUNT; i++) {
      final long start = System.currentTimeMillis();
      super.setUp();
      if (log.isDebugEnabled() && i % 10 == 0) log.debug("set up time:" + (System.currentTimeMillis() - start) + " at index: " + i);
    }
  }


  /**
   * GOOD test.
   */
  public void test_performance2() throws Exception {

    // init database content if there are other test cases
    IDatabaseConnection dbUnitConn = null;
    Connection conn = null;
    try {
      // run dbunit initializing database
      conn = getConnection();
      dbUnitConn = new DatabaseConnection(conn);
      for (int i = 0; i < TEST_RUN_COUNT; i++) {
        final long start = System.currentTimeMillis();
        DatabaseOperation.CLEAN_INSERT.execute(dbUnitConn, new FlatXmlDataSet(DATASET_FILE));
        conn.commit();
        if (log.isDebugEnabled() && i % 10 == 0) log.debug("set up time: " + (System.currentTimeMillis() - start));
      }
    } finally {
      IoUtils.closeHard(conn);
      closeDBUnitConnection(dbUnitConn);
    }
  }


  /**
   * BAD test - roundtrip time grows very quickly:
   * <p/>
   * [java] [HttpProcessor[8080][2]] 18:51:35,203 DEBUG: stServersideTestCase( 70) - set up time: 234 - 10
   * [java] [HttpProcessor[8080][2]] 18:51:37,937 DEBUG: stServersideTestCase( 70) - set up time: 312 - 20
   * [java] [HttpProcessor[8080][2]] 18:51:41,922 DEBUG: stServersideTestCase( 70) - set up time: 469 - 30
   * [java] [HttpProcessor[8080][2]] 18:51:47,640 DEBUG: stServersideTestCase( 70) - set up time: 656 - 40
   */
  public void test_performance4() throws Exception {
    IDatabaseConnection dbUnitConn = null;
    for (int i = 0; i < TEST_RUN_COUNT; i++) {
      try {
        dbUnitConn = new DatabaseConnection(getConnection());
        final long start = System.currentTimeMillis();
        DatabaseOperation.CLEAN_INSERT.execute(dbUnitConn, new FlatXmlDataSet(DATASET_FILE));
        if (log.isDebugEnabled() && i % 10 == 0) log.debug("set up time: " + (System.currentTimeMillis() - start));
      } finally {
        closeDBUnitConnection(dbUnitConn);
      }
    }
  }


  private Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    final String databaseHome = (new File(System.getProperty("catalina.base"), "data/parabuild")).getAbsolutePath();
    final Properties properties = new Properties();
    properties.setProperty("user", PersistanceConstants.DATABASE_USER_NAME);
    properties.setProperty("password", PersistanceConstants.DATABASE_PASSWORD);
    properties.setProperty("ifexists", "true");
    final Driver driver = (Driver)Class.forName("org.hsqldb.jdbcDriver").newInstance();
    final Connection conn = driver.connect("jdbc:hsqldb:" + databaseHome, properties);
    conn.setAutoCommit(false);
    return conn;
  }


  private void closeDBUnitConnection(final IDatabaseConnection dbUnitConn) {
    if (dbUnitConn != null) {
      try {
        dbUnitConn.close();
      } catch (Exception e) {
        log.warn("Unexpected error while closing dbUnit connection", e);
      }
    }
  }


  protected void setUp() throws Exception {
    // Do NOT call our set up
    // super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestServersideTestCase.class, new String[]{
      "test_performance2",
      "test_performance4"
    });
  }
}
