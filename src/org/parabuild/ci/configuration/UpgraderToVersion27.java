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

import java.sql.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.util.*;

/**
 * Upgrades to version 27. Adds manual label field to build run.
 */
final class UpgraderToVersion27 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion27.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    try {
      // create statement
      conn.setAutoCommit(true);
      st = conn.createStatement();

      log.debug("Altering tables");

      addBuildCountToTestStatsTable("DAILY_TEST_STATS", st);
      addBuildCountToTestStatsTable("HOURLY_TEST_STATS", st);
      addBuildCountToTestStatsTable("MONTHLY_TEST_STATS", st);
      addBuildCountToTestStatsTable("YEARLY_TEST_STATS", st);

      log.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();

      // request post-startup config manager action
      System.setProperty(SystemConstants.SYSTEM_PROPERTY_INIT_ADVANCED_SETTINGS, "true");
    } finally {
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  private static void addBuildCountToTestStatsTable(final String tableName, final Statement st) throws SQLException {
    final String [] update = {" delete from " + tableName,
      " alter table " + tableName + " add column BUILD_COUNT integer default 0 not null ",
      " alter table " + tableName + " alter column BUILD_COUNT drop default ",
    };
    PersistanceUtils.executeDDLs(st, update);
  }


  public int upgraderVersion() {
    return 27;
  }
}
