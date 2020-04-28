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
import org.parabuild.ci.util.IoUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Upgrades to version 28. Adds manual label field to build run.
 */
final class UpgraderToVersion28 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion28.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    PreparedStatement psUpdateCount = null; // NOPMD
    Statement st = null; // NOPMD
    ResultSet rsCounts = null; // NOPMD
    try {
      // create statement
      conn.setAutoCommit(true);
      st = conn.createStatement();

      log.debug("Altering table");
      final String [] update = {" alter table CHANGELIST add column TRUNCATED char(1) default 'N' not null ",
        " update CHANGELIST set TRUNCATED='N' ",
        " alter table CHANGELIST alter column TRUNCATED drop default ",
        // ORIGINAL_SIZE column
        " alter table CHANGELIST add column ORIGINAL_SIZE integer default 0 not null ",
        " update CHANGELIST set ORIGINAL_SIZE='0' ",
        " alter table CHANGELIST alter column ORIGINAL_SIZE drop default ",
      };
      PersistanceUtils.executeDDLs(st, update);

      log.debug("Update change list sizes");
      psUpdateCount = conn.prepareStatement("update CHANGELIST set ORIGINAL_SIZE = ? where ID = ?");
      rsCounts = st.executeQuery("select CHANGELIST_ID, COUNT(*) from CHANGE group by CHANGELIST_ID");
      while (rsCounts.next()) {
        final int changeListID = rsCounts.getInt(1);
        final int count = rsCounts.getInt(2);
        psUpdateCount.setInt(1, count);
        psUpdateCount.setInt(2, changeListID);
        psUpdateCount.executeUpdate();
        if (log.isDebugEnabled()) log.debug("changeListID: " + changeListID + " count: " + count);
      }


      log.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();

      // request post-startup config manager action
      System.setProperty(SystemConstants.SYSTEM_PROPERTY_INIT_ADVANCED_SETTINGS, "true");
    } finally {
      IoUtils.closeHard(st);
      IoUtils.closeHard(rsCounts);
      IoUtils.closeHard(psUpdateCount);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  public int upgraderVersion() {
    return 28;
  }
}
