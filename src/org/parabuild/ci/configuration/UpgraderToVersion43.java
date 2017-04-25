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

import org.parabuild.ci.common.*;

/**
 * Upgrades to version 43. Alters source control properties to
 * hold 4 kilobytes property values.
 */
final class UpgraderToVersion43 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion43.class);


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

      log.debug("Altering table");
      PersistanceUtils.executeDDLs(st, new String[]{
        " alter table STEP_RESULT add column PINNED char(1) default 'N' not null ",
        " update STEP_RESULT set PINNED='N' ",
        " alter table STEP_RESULT alter column PINNED drop default ",
      });

      log.debug("Preparing selector");
      final PreparedStatement psSelect = conn.prepareStatement(" select SRES.ID from BUILD_RUN BR, STEP_RUN SRUN, STEP_RESULT SRES" +
        " where BR.PINNED = 'Y' " +
        "   and BR.ID = SRUN.BUILD_RUN_ID" +
        "   and SRUN.ID = SRES.STEP_RUN_ID");

      log.debug("Preparing updater");
      final PreparedStatement psUpdate = conn.prepareStatement("update STEP_RESULT set PINNED = 'Y' where ID = ?");

      log.debug("Updating");
      final ResultSet rs = psSelect.executeQuery(); // NOPMD CloseResource
      while(rs.next()) {
        final int resultID = rs.getInt(1);
        psUpdate.setInt(1, resultID);
        psUpdate.executeUpdate();
      }

      IoUtils.closeHard(rs);
      IoUtils.closeHard(psUpdate);
      IoUtils.closeHard(psSelect);

      log.debug("Dropping column");
      PersistanceUtils.executeDDLs(st, new String[]{
        " drop index BUILD_RUN_IX5 ",
        " alter table BUILD_RUN drop column PINNED ",
        " create index STEP_RESULT_IX3 on STEP_RESULT(STEP_RUN_ID, PINNED, FOUND, FILE) ",
      });


      // See bug # 1112
      log.debug("Altering build config");
      PersistanceUtils.executeDDLs(st, new String[]{
        "alter table BUILD_CONFIG add column NEW_NAME varchar(80)",
        "update BUILD_CONFIG set NEW_NAME=NAME",
        "alter table BUILD_CONFIG drop column NAME",
        "alter table BUILD_CONFIG alter column NEW_NAME rename to NAME"});


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


  public int upgraderVersion() {
    return 43;
  }
}
