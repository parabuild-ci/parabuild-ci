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
 * Upgrades to version 79. Adds manual label field to build run.
 */
final class UpgraderToVersion79 implements SingleStepSchemaUpgrader {

  private static final Log LOG = LogFactory.getLog(UpgraderToVersion79.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();

    PreparedStatement ps = null;
    ResultSet rs = null;
    Statement st = null; // NOPMD
    try {
      // create statement
      conn.setAutoCommit(true);
      st = conn.createStatement();

      LOG.debug("Altering table");

      PersistanceUtils.executeDDLs(st, new String[]{
              " alter table GROUPS add column ALLOWED_TO_ACTIVATE_BUILD char(1)  default 'N' not null ",
              " update GROUPS set ALLOWED_TO_ACTIVATE_BUILD='N' ",
              " alter table GROUPS alter column ALLOWED_TO_ACTIVATE_BUILD drop default ",
      });

      LOG.debug("Updating table");

      ps = conn.prepareStatement("update GROUPS set ALLOWED_TO_ACTIVATE_BUILD = ? where ID = ?");
      rs = st.executeQuery("select ID, ALLOWED_TO_START_BUILD, ALLOWED_TO_STOP_BUILD from GROUPS");
      while (rs.next()) {

        final int id = rs.getInt(1);
        final boolean allowedToStart = "y".equalsIgnoreCase(rs.getString(2));
        final boolean allowedToStop = "y".equalsIgnoreCase(rs.getString(3));
        final boolean allowedToActivate = allowedToStart || allowedToStop;
        ps.setString(1, (allowedToActivate ? "Y" : "N"));
        ps.setInt(2, id);
        ps.execute();
      }

      LOG.debug("Updating version");
      
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();

      // request post-startup config manager action
      System.setProperty(SystemConstants.SYSTEM_PROPERTY_INIT_ADVANCED_SETTINGS, "true");
    } finally {
      IoUtils.closeHard(st);
      IoUtils.closeHard(rs);
      IoUtils.closeHard(ps);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  public int upgraderVersion() {
    return 79;
  }
}
