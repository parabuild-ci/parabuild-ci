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
 * Upgrades to version 20
 */
final class UpgraderToVersion20 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion20.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    try {
      conn.setAutoCommit(false);
      final String[] ddls = {
        "create cached table DISPLAY_GROUP (" +
          "  ID integer not null identity," +
          "  NAME varchar(254) not null," +
          "  DESCR varchar(254) not null," +
          "  ENABLED char(1) not null," +
          "  TIMESTAMP bigint not null," +
          "  constraint DISPLAY_GROUPS_UC1 unique (ID)," +
          "  constraint DISPLAY_GROUPS_UC2 unique (NAME)" +
          ')',

        "create cached table DISPLAY_GROUP_ATTRIBUTE (" +
          "  DISPLAY_GROUP_ID integer not null," +
          "  ID integer not null identity," +
          "  NAME varchar(80) not null," +
          "  VALUE varchar(1024)," +
          "  TIMESTAMP bigint not null," +
          "  constraint DISPLAY_GROUP_ATTRIBUTE_UC1 unique (ID)," +
          "  constraint DISPLAY_GROUP_ATTRIBUTE_UC2 unique (DISPLAY_GROUP_ID, NAME)," +
          "  constraint DISPLAY_GROUP_ATTRIBUTE_FC1 foreign key (DISPLAY_GROUP_ID) references DISPLAY_GROUP(ID) ON DELETE CASCADE" +
          ')',

        "create cached table DISPLAY_GROUP_BUILD (" +
          "  ID integer not null identity," +
          "  DISPLAY_GROUP_ID integer not null," +
          "  BUILD_ID integer not null," +
          "  TIMESTAMP bigint not null," +
          "  constraint DISPLAY_GROUP_BUILD_UC1 unique (ID)," +
          "  constraint DISPLAY_GROUP_BUILD_UC2 unique (BUILD_ID, DISPLAY_GROUP_ID)," +
          "  constraint DISPLAY_GROUP_BUILD_FC1 foreign key (BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE," +
          "  constraint DISPLAY_GROUP_BUILD_FC2 foreign key (DISPLAY_GROUP_ID) references DISPLAY_GROUP(ID) ON DELETE CASCADE" +
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
    return 20;
  }
}
