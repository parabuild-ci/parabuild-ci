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
 * Upgrades to version 12
 */
final class UpgraderToVersion12 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion12.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    try {
      conn.setAutoCommit(true);
      final String[] ddls = {
        "alter table BUILD_CONFIG drop constraint BUILD_CONFIG_FC1 "
      };

      log.debug("Alter structure");
      st = conn.createStatement();
      try {
        PersistanceUtils.executeDDLs(st, ddls);
      } catch (SQLException e) {
        // NOTE: vimeshev - have to ignore because it looks like
        // some of the already released versions didn't drop
        // BUILD_CONFIG_FC1
        if (log.isDebugEnabled()) log.debug("Expected exception: " + StringUtils.toString(e));
        IoUtils.ignoreExpectedException(e);
      }

      // update version
      log.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");
      IoUtils.closeHard(st);

      // finish
      conn.commit();

      // request post-startup config manager action
      System.setProperty(SystemConstants.SYSTEM_PROPERTY_INIT_STATISTICS, "true");
    } finally {
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  public int upgraderVersion() {
    return 12;
  }
}