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
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.SystemProperty;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * This class is responsible for automatic upgrading database
 * schema
 *
 * @noinspection ClassHasNoToStringMethod
 */
public final class DatabaseSchemeUpgrader {

  public static final int CURRENT_DB_SCHEMA_VERSION = 81;

  private static final Log LOG = LogFactory.getLog(DatabaseSchemeUpgrader.class); // NOPMD

  private final SingleStepSchemaUpgrader[] upgraders = { // NOPMD (SingularField)
          null,
          null,
          null,
          new UpgraderToVersion4(),
          new UpgraderToVersion5(),
          new UpgraderToVersion6(),
          new UpgraderToVersion7(),
          new UpgraderToVersion8(),
          new UpgraderToVersion9(),
          new UpgraderToVersion10(),
          new UpgraderToVersion11(),
          new UpgraderToVersion12(),
          new UpgraderToVersion13(),
          new UpgraderToVersion14(),
          new UpgraderToVersion15(),
          new UpgraderToVersion16(),
          new UpgraderToVersion17(),
          new UpgraderToVersion18(),
          new UpgraderToVersion19(),
          new UpgraderToVersion20(),
          new UpgraderToVersion21(),
          new UpgraderToVersion22(),
          new UpgraderToVersion23(),
          new UpgraderToVersion24(),
          new UpgraderToVersion25(),
          new UpgraderToVersion26(),
          new UpgraderToVersion27(),
          new UpgraderToVersion28(),
          new UpgraderToVersion29(),
          new UpgraderToVersion30(),
          new UpgraderToVersion31(),
          new UpgraderToVersion32(),
          new UpgraderToVersion33(),
          new UpgraderToVersion34(),
          new UpgraderToVersion35(),
          new UpgraderToVersion36(),
          new UpgraderToVersion37(),
          new UpgraderToVersion38(),
          new UpgraderToVersion39(),
          new UpgraderToVersion40(),
          new UpgraderToVersion41(),
          new UpgraderToVersion42(),
          new UpgraderToVersion43(),
          new UpgraderToVersion44(),
          new UpgraderToVersion45(),
          new UpgraderToVersion46(),
          new UpgraderToVersion47(),
          new UpgraderToVersion48(),
          new UpgraderToVersion49(),
          new UpgraderToVersion50(),
          new UpgraderToVersion51(),
          new UpgraderToVersion52(),
          new UpgraderToVersion53(),
          new UpgraderToVersion54(),
          new UpgraderToVersion55(),
          new UpgraderToVersion56(),
          new UpgraderToVersion57(),
          new UpgraderToVersion58(),
          new UpgraderToVersion59(),
          new UpgraderToVersion60(),
          new UpgraderToVersion61(),
          new UpgraderToVersion62(),
          new UpgraderToVersion63(),
          new UpgraderToVersion64(),
          new UpgraderToVersion65(),
          new UpgraderToVersion66(),
          new UpgraderToVersion67(),
          new UpgraderToVersion68(),
          new UpgraderToVersion69(),
          new UpgraderToVersion70(),
          new UpgraderToVersion71(),
          new UpgraderToVersion72(),
          new UpgraderToVersion73(),
          new UpgraderToVersion74(),
          new UpgraderToVersion75(),
          new UpgraderToVersion76(),
          new UpgraderToVersion77(),
          new UpgraderToVersion78(),
          new UpgraderToVersion79(),
          new UpgraderToVersion80(),
          new UpgraderToVersion81(),
  };


  /**
   * Performs upgrade
   *
   * @noinspection JDBCResourceOpenedButNotSafelyClosed
   */
  public void process() throws BuildException {
    Connection conn = null; // NOPMD
    Statement stmt = null; // NOPMD
    ResultSet rs = null; // NOPMD
    try {
      // get connection characteristics
      conn = HSQLDBUtils.createHSQLConnection(ConfigurationConstants.DATABASE_HOME);
      conn.setAutoCommit(true);

      // get version
      if (LOG.isDebugEnabled()) {
        LOG.debug("Getting schema version");
      }
      stmt = conn.createStatement();
      rs = stmt.executeQuery("select VALUE from SYSTEM_PROPERTY where NAME = '" + SystemProperty.SCHEMA_VERSION + "' ");
      String stringVersion = null;
      if (rs.next()) {

        stringVersion = rs.getString(1);
      }

      // Check if we got the verion
      if (StringUtils.isBlank(stringVersion)) {
        throw new IllegalStateException("Version of database schema is undefined");
      }

      // Convert to integer
      final int userVersion = Integer.parseInt(stringVersion);

      // traverse available upgrades
      if (LOG.isDebugEnabled()) {
        LOG.debug("Schema version: " + userVersion);
      }
      for (int version = userVersion; version < CURRENT_DB_SCHEMA_VERSION; version++) {
        final int targetVersion = version + 1;
        if (LOG.isDebugEnabled()) {
          LOG.debug("Upgrading to schema version #" + targetVersion);
        }
        final SingleStepSchemaUpgrader upgrader = upgraders[version];
        validateVersion(targetVersion, upgrader.upgraderVersion());
        upgrader.upgrade(conn, targetVersion);
      }

      // was there an upgrade?
      if (userVersion < CURRENT_DB_SCHEMA_VERSION && "true".equals(System.getProperty("parabuild.compact.database", "false"))) {
        // there was an upgrade, so we'll run compacting
        stmt.execute("shutdown compact");
      }
    } catch (SQLException e) {
      throw new BuildException("Error while performing upgrade of database scheme", e);
    } finally {
      IoUtils.closeHard(rs);
      IoUtils.closeHard(stmt);
      IoUtils.closeHard(conn);
    }
  }


  private void validateVersion(final int targetVersion, final int upgraderVersion) throws SQLException {
    if (targetVersion != upgraderVersion) {
      throw new SQLException("Upgrader version mismatch. Expected "
              + targetVersion + " but it was " + upgraderVersion);
    }
  }


  public String toString() {
    return "DatabaseSchemeUpgrader{" +
            "upgraders=" + (upgraders == null ? null : Arrays.asList(upgraders)) +
            '}';
  }
}
