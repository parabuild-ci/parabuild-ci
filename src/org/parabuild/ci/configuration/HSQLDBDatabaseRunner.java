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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.CommonConstants;
import org.parabuild.ci.util.IoUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

public final class HSQLDBDatabaseRunner implements DatabaseRunner, CommonConstants, PersistanceConstants {

  private static final Log log = LogFactory.getLog(HSQLDBDatabaseRunner.class);


  /**
   * Starts database
   */
  public void startDatabase() {
    try {
      // create database if missing
      if (!databaseExists()) {
        log.info("creating database");
        final DatabaseCreator databaseCreator = new DatabaseCreator();
        databaseCreator.createDatabase(ConfigurationConstants.DATABASE_HOME);
      }
    } catch (final Exception e) {
      // NOTE: simeshev@parabuilci.org - we don't use error manager as
      // DB service in not up yet.
      log.error("Error while starting database service", e);
    }
  }


  /**
   * Checks if our HSQL database files are present.
   *
   * @return false if our HSQL database files are not present.
   */
  private static boolean databaseExists() {
    // general checks
    if (!ConfigurationConstants.DATABASE_HOME.getParentFile().exists()) return false;
    if (ConfigurationConstants.DATABASE_HOME.getParentFile().listFiles().length == 0) return false;
    // if dir is not empty, check if no one HSQL DB file exists.
    final File hsqlDataFile = new File(ConfigurationConstants.DATABASE_HOME + ".data");
    final File hsqlScriptFile = new File(ConfigurationConstants.DATABASE_HOME + ".script");
    final File hsqlBackupFile = new File(ConfigurationConstants.DATABASE_HOME + ".backup");
    return !(!hsqlDataFile.exists() && !hsqlScriptFile.exists() && !hsqlBackupFile.exists());
  }


  /**
   * Stops database
   */
  public void stopDatabase() {
    Connection conn = null; // NOPMD
    Statement stmt = null; // NOPMD
    try {
      conn = HSQLDBUtils.createHSQLConnection(
        ConfigurationConstants.DATABASE_HOME,
        DATABASE_USER_NAME,
        DATABASE_PASSWORD);

      // exec shutdown
      stmt = conn.createStatement();
      stmt.executeUpdate("SHUTDOWN");
      // if we get here, we did not get db running
    } catch (final Exception e) {
      log.error("Error while stopping database service", e);
    } finally {
      IoUtils.closeHard(conn);
      IoUtils.closeHard(stmt);
    }
  }
}
