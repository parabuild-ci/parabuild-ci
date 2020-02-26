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
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Upgrades to version 65.
 */
final class UpgraderToVersion66 implements SingleStepSchemaUpgrader {

  private static final Log LOG = LogFactory.getLog(UpgraderToVersion66.class); // NOPMD


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

      UpgraderToVersion66.LOG.debug("Altering table");
      PersistanceUtils.executeDDLs(st, new String[]{
              // TEST_CASE table
              "create cached table GLOBAL_VCS_USER_EMAIL_MAP (" +
                "   ID integer not null identity," +
                "   VCS_USER_NAME varchar(200) not null," +
                "   EMAIL varchar(200) not null," +
                "   DESCRIPTION varchar(1024) not null," +
                "   constraint GLOBAL_VCS_USER_EMAIL_MAP_UC1 unique (ID)," +
                "   constraint GLOBAL_VCS_USER_EMAIL_MAP_UC2 unique (VCS_USER_NAME) " +
                "   )",
              
                "create index GLOBAL_VCS_USER_EMAIL_MAP_IX1 on GLOBAL_VCS_USER_EMAIL_MAP(VCS_USER_NAME)",
      });

      UpgraderToVersion66.LOG.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();

    } finally {
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  public int upgraderVersion() {
    return 66;
  }
}
