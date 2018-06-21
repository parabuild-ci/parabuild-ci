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
import org.parabuild.ci.common.IoUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Upgrades to version 63
 */
final class UpgraderToVersion63 implements SingleStepSchemaUpgrader {

  private static final Log LOG = LogFactory.getLog(UpgraderToVersion63.class); // NOPMD


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    conn.setAutoCommit(true);
    Statement st = null; // NOPMD
    try {

      // Create statement
      st = conn.createStatement();

      LOG.info("Adding OLD_VALUE if necessary");
      try {
        final ResultSet resultSet = st.executeQuery("select count(OLD_VALUE) from SOURCE_CONTROL_PROPERTY");
        IoUtils.closeHard(resultSet);
        // Column exist, do nothing
      } catch (final SQLException e) {
        // Column OLD_VALUE does not exist
        PersistanceUtils.executeDDLs(st, new String[]{"alter table SOURCE_CONTROL_PROPERTY add column OLD_VALUE varchar(4097) null"}, true);
      }

      LOG.info("Copying VALUE to OLD_VALUE");
      final PreparedStatement selectPstmt = conn.prepareStatement("select ID from SOURCE_CONTROL_PROPERTY");
      final PreparedStatement copyCurrenttoOldPstmt = conn.prepareStatement("update SOURCE_CONTROL_PROPERTY set OLD_VALUE=VALUE where ID=? ");
      ResultSet selectRs = selectPstmt.executeQuery();
      conn.setAutoCommit(false);
      int count = 0;
      while (selectRs.next()) {
        final int id = selectRs.getInt(1);
        copyCurrenttoOldPstmt.setInt(1, id);
        copyCurrenttoOldPstmt.executeUpdate();
        count++;
        if (count % 100 == 0) {
          conn.commit();
        }
      }
      IoUtils.closeHard(selectRs);
      IoUtils.closeHard(copyCurrenttoOldPstmt);
      conn.setAutoCommit(true);

      LOG.info("Change VALUE size");
      PersistanceUtils.executeDDLs(st, new String[]{"alter table SOURCE_CONTROL_PROPERTY drop column VALUE"}, true);
      PersistanceUtils.executeDDLs(st, new String[]{"alter table SOURCE_CONTROL_PROPERTY add column VALUE varchar(32552) default '' not null"}, true);


      // Copy OLD_VALUE to VALUE
      LOG.info("Copying OLD_VALUE to VALUE");
      final PreparedStatement copyOldToCurrent = conn.prepareStatement("update SOURCE_CONTROL_PROPERTY set VALUE=OLD_VALUE where ID=? ");
      selectRs = selectPstmt.executeQuery();
      count = 0;
      conn.setAutoCommit(false);
      while (selectRs.next()) {
        final int id = selectRs.getInt(1);
        copyOldToCurrent.setInt(1, id);
        copyOldToCurrent.executeUpdate();
        count++;
        if (count % 100 == 0) {
          conn.commit();
        }
      }
      IoUtils.closeHard(selectRs);
      IoUtils.closeHard(copyOldToCurrent);
      conn.setAutoCommit(true);

      // Drop OLD_VALUE
      LOG.info("Dropping OLD_VALUE");
      PersistanceUtils.executeDDLs(st, new String[]{"alter table SOURCE_CONTROL_PROPERTY drop column OLD_VALUE"}, true);


      // update version
      LOG.info("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");
      IoUtils.closeHard(st);
    } finally {
      conn.setAutoCommit(savedAutoCommit);
      IoUtils.closeHard(st);
    }
  }


  public int upgraderVersion() {
    return 63;
  }
}
