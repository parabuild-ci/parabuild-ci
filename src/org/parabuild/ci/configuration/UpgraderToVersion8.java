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
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;

/**
 * Upgrades to version 8
 */
final class UpgraderToVersion8 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion8.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    try {
      conn.setAutoCommit(false);

      // encrypt BZ passwords
      encryptPasswordProperty(conn, "ISSUE_TRACKER_PROPERTY", "ID", "NAME", "VALUE",
        IssueTrackerProperty.BUGZILLA_MYSQL_PASSWORD);

      // encrypt CVS passwords
      encryptPasswordProperty(conn, "SOURCE_CONTROL_PROPERTY", "ID", "NAME", "VALUE",
        SourceControlSetting.CVS_PASSWORD);

      // encrypt P4 passwords
      encryptPasswordProperty(conn, "SOURCE_CONTROL_PROPERTY", "ID", "NAME", "VALUE",
        SourceControlSetting.P4_PASSWORD);

      // encrypt VSS passwords
      encryptPasswordProperty(conn, "SOURCE_CONTROL_PROPERTY", "ID", "NAME", "VALUE",
        SourceControlSetting.VSS_PASSWORD);

      // encrypt SVN passwords
      encryptPasswordProperty(conn, "SOURCE_CONTROL_PROPERTY", "ID", "NAME", "VALUE",
        SourceControlSetting.SVN_PASSWORD);


      // encrypt SMTP SERVER PASSWORD passwords
      encryptPasswordProperty(conn, "SYSTEM_PROPERTY", "ID", "NAME", "VALUE",
        SystemProperty.SMTP_SERVER_PASSWORD);


      // encrypt JABBER_LOGIN_PASSWORD passwords
      encryptPasswordProperty(conn, "SYSTEM_PROPERTY", "ID", "NAME", "VALUE",
        SystemProperty.JABBER_LOGIN_PASSWORD);


      // update version
      log.debug("Updating version");
      st = conn.createStatement();
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '8' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();
    } finally {
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  private void encryptPasswordProperty(final Connection conn,
    final String tableName, final String idFieldName, final String nameFieldName,
    final String valueFieldName, final String passwordPropertyName) throws SQLException {

    PreparedStatement psSelect = null; // NOPMD
    PreparedStatement psUpdate = null; // NOPMD
    ResultSet rs = null; // NOPMD
    try {
      psUpdate = conn.prepareStatement("update " + tableName + " set " + valueFieldName + " = ? where " + idFieldName + " = ?");
      psSelect = conn.prepareStatement("select " + idFieldName + ", " + valueFieldName + " from " + tableName + "  where " + nameFieldName + " = ?");
      psSelect.setString(1, passwordPropertyName);
      rs = psSelect.executeQuery();
      while (rs.next()) {
        psUpdate.setString(1, SecurityManager.encryptPassword(rs.getString(2)));
        psUpdate.setInt(2, rs.getInt(1));
        psUpdate.execute();
      }
    } finally {
      IoUtils.closeHard(rs);
      IoUtils.closeHard(psUpdate);
      IoUtils.closeHard(psSelect);
    }
  }


  /**
   * @return version this upgrader upgrades to.
   */
  public int upgraderVersion() {
    return 8;
  }
}