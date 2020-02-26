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
import org.parabuild.ci.object.Group;
import org.parabuild.ci.object.SystemProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Upgrades to version 4
 */
final class UpgraderToVersion4 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion4.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    log.info("Upgrading database to version: " + upgradeToVersion);
    Statement st = null; // NOPMD
    PreparedStatement ps = null; // NOPMD
    ResultSet rs = null; // NOPMD
    try {

      // add Anonymous group
      log.debug("Adding Anonymous group");
      ps = conn.prepareStatement("insert into GROUPS (NAME, DESCR, ENABLED, TIMESTAMP) values (?, 'Users that are not logged in', 'Y', 0)");
      ps.setString(1, Group.SYSTEM_ANONYMOUS_GROUP);
      ps.execute();
      IoUtils.closeHard(ps);

      // get Anonymous group ID
      log.debug("Getting Anonymous group ID");
      ps = conn.prepareStatement("select ID from GROUPS where NAME = ?");
      ps.setString(1, Group.SYSTEM_ANONYMOUS_GROUP);
      rs = ps.executeQuery();
      rs.next();
      final int anonGroupID = rs.getInt(1);
      IoUtils.closeHard(rs);
      IoUtils.closeHard(ps);

      // add all build configs to Anonymous group
      log.debug("Adding all build configs to Anonymous group");
      st = conn.createStatement();
      rs = st.executeQuery("select ID from BUILD_CONFIG");
      ps = conn.prepareStatement("insert into BUILD_ACCESS (BUILD_ID, GROUP_ID, TIMESTAMP) values (?, ?, 0)");
      while (rs.next()) {
        final int buildID = rs.getInt(1);
        ps.setInt(1, buildID);
        ps.setInt(2, anonGroupID);
        ps.execute();
      }
      IoUtils.closeHard(rs);
      IoUtils.closeHard(ps);
      IoUtils.closeHard(st);

      // enable system Anonymous access
      log.debug("Enabling system Anonymous access");
      ps = conn.prepareStatement("select ID from SYSTEM_PROPERTY where NAME = ?");
      ps.setString(1, SystemProperty.ENABLE_ANONYMOUS_BUILDS);
      rs = ps.executeQuery();
      if (rs.next()) {
        // record exists
        final int anonBuildsEnabledPropID = rs.getInt(1);
        final PreparedStatement ps1 = conn.prepareStatement("update SYSTEM_PROPERTY set VALUE = ? where ID = ?");
        ps1.setString(1, SystemProperty.OPTION_CHECKED);
        ps1.setInt(2, anonBuildsEnabledPropID);
        ps1.execute();
        IoUtils.closeHard(ps1);
      } else {
        // create record
        final PreparedStatement ps1 = conn.prepareStatement("insert into SYSTEM_PROPERTY (NAME, VALUE, TIMESTAMP) values (?, ?, 0)");
        ps1.setString(1, SystemProperty.ENABLE_ANONYMOUS_BUILDS);
        ps1.setString(2, SystemProperty.OPTION_CHECKED);
        ps1.execute();
        IoUtils.closeHard(ps1);
      }
      IoUtils.closeHard(rs);
      IoUtils.closeHard(ps);

      // update version
      log.info("Updating version");
      st = conn.createStatement();
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '4' where NAME = 'parabuild.schema.version' ");
      IoUtils.closeHard(st);
    } finally {
      IoUtils.closeHard(rs);
      IoUtils.closeHard(ps);
      IoUtils.closeHard(st);
    }
  }


  public int upgraderVersion() {
    return 4;
  }
}