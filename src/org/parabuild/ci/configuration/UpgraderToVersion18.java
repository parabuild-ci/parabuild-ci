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
 * Upgrades to version 18
 */
final class UpgraderToVersion18 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion18.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    try {
      conn.setAutoCommit(false);
      final String[] ddls = {
        "create cached table MANUAL_RUN_PARAMETER (" +
          "  ID integer not null identity," +
          "  BUILD_ID integer not null," +
          "  DESCRIPTION varchar(100) not null," +
          "  ENABLED char(1) not null," +
          "  NAME varchar(100) not null," +
          "  REQUIRED char(1) not null," +
          "  TIMESTAMP bigint not null," +
          "  TYPE tinyint not null," +
          "  VALUE varchar(1024)," +
          "  constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_UC1 unique (ID)," +
          "  constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_UC2 unique (BUILD_ID, NAME)," +
          "  constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE" +
          ')'
      };

      log.debug("Alter structure");
      st = conn.createStatement();
      PersistanceUtils.executeDDLs(st, ddls);

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
    return 18;
  }
}
