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
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Upgrades to version 70.
 */
final class UpgraderToVersion70 implements SingleStepSchemaUpgrader {

  private static final Log LOG = LogFactory.getLog(UpgraderToVersion70.class); // NOPMD


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

      LOG.debug("Altering table");
      PersistanceUtils.executeDDLs(st, new String[]{
              //
              // BUILD_CHANGELIST_ATTRIBUTE table
              //
              "create cached table BUILD_CHANGELIST_ATTRIBUTE (" +
                      "    BUILD_CHANGELIST_ID integer not null," +
                      "    ID integer not null identity," +
                      "    NAME varchar(80) not null," +
                      "    VALUE varchar(1024)," +
                      "    TIMESTAMP bigint not null," +
                      "    constraint BUILD_CHANGELIST_ATTRIBUTE_UC1 unique (ID)," +
                      "    constraint BUILD_CHANGELIST_ATTRIBUTE_UC2 unique (BUILD_CHANGELIST_ID, NAME)," +
                      "    constraint BUILD_CHANGELIST_ATTRIBUTE_FC1 foreign key (BUILD_CHANGELIST_ID) references BUILD_CHANGELIST(ID) ON DELETE CASCADE" +
                      ')',
      });

      LOG.debug("Updating version");
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
    return 70;
  }
}