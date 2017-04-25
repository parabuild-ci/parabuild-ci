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
 * Upgrades to version 5
 */
final class UpgraderToVersion5 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion5.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    Statement st = null; // NOPMD
    try {

      // add build result settings objects
      final String[] ddls = {
        // RESULT_CONFIG table
        "create cached table RESULT_CONFIG (" +
        "   ID integer not null identity," +
        "   BUILD_ID integer not null," +
        "   DESCRIPTION varchar(80) not null," +
        "   PATH varchar(1024) not null," +
        "   TYPE tinyint not null," +
        "   TIMESTAMP bigint not null," +
        "   constraint RESULT_CONFIG_UC1 unique (ID)," +
        "   constraint RESULT_CONFIG_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE" +
        "   )",
        "create unique index RESULT_CONFIG_PK on RESULT_CONFIG(ID)",
        "create index RESULT_CONFIG_FK1 on RESULT_CONFIG(BUILD_ID)",

        // RESULT_CONFIG_PROPERTY table
        "create cached table RESULT_CONFIG_PROPERTY (" +
        "  ID integer not null identity," +
        "  RESULT_CONFIG_ID integer not null," +
        "  NAME varchar(80) not null," +
        "  VALUE varchar(1024)," +
        "  TIMESTAMP bigint not null," +
        "  constraint RESULT_CONFIG_PROPERTY_UC1 unique (ID)," +
        "  constraint RESULT_CONFIG_PROPERTY_FC1 foreign key (RESULT_CONFIG_ID) references RESULT_CONFIG(ID) ON DELETE CASCADE" +
          ')',
        "create unique index RESULT_CONFIG_PROPERTY_PK on RESULT_CONFIG_PROPERTY(ID)",
        "create unique index RESULT_CONFIG_PROPERTY_AK1 on RESULT_CONFIG_PROPERTY(RESULT_CONFIG_ID, NAME)",
        "create index RESULT_CONFIG_PROPERTY_FK1 on RESULT_CONFIG_PROPERTY(RESULT_CONFIG_ID)"
      };


      st = conn.createStatement();
      PersistanceUtils.executeDDLs(st, ddls);

      // update version
      log.info("Updating version");
      st = conn.createStatement();
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '5' where NAME = 'parabuild.schema.version' ");
      IoUtils.closeHard(st);
    } finally {
      IoUtils.closeHard(st);
    }
  }


  public int upgraderVersion() {
    return 5;
  }
}