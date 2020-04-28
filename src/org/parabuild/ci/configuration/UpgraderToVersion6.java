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
 * Upgrades to version 6
 */
final class UpgraderToVersion6 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion6.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    Statement st = null; // NOPMD
    try {
      // add build result settings objects
      final String[] ddls = {
        // STEP_RESULT table
        "create cached table STEP_RESULT (" +
        "   ID integer not null identity," +
        "   STEP_RUN_ID integer not null," +
        "   FILE varchar(512) not null," +
        "   PATH varchar(512) not null," +
        "   DESCRIPTION varchar(1024) not null," +
        "   PATH_TYPE tinyint not null," +
        "   FOUND char(1) not null," +
        "   STORED_EXTERNALLY char(1) not null," +
        "   HANDLED_EXTERNALLY char(1) not null," +
        "   EXTERNAL_ARCHIVE_HOME varchar(512) not null," +
        "   constraint STEP_RESULT_UC1 unique (ID)," +
        "   constraint STEP_RESULT_FC1 foreign key (STEP_RUN_ID) references STEP_RUN(ID) ON DELETE CASCADE" +
        "   )",
        "create unique index STEP_RESULT_PK on STEP_RESULT (ID)",
        "create index STEP_RESULT_FK1 on STEP_RESULT(STEP_RUN_ID)",
        "create index STEP_RESULT_IX1 on STEP_RESULT(STEP_RUN_ID, FOUND)",

        // STEP_RESULT_ATTRIBUTE table
        "create cached table STEP_RESULT_ATTRIBUTE (" +
        "   ID integer not null identity," +
        "   STEP_RESULT_ID integer not null," +
        "   NAME varchar(80) not null," +
        "   VALUE varchar(1024)," +
        "   constraint STEP_RESULT_ATTRIBUTE_UC1 unique (ID)," +
        "   constraint STEP_RESULT_ATTRIBUTE_FC1 foreign key (STEP_RESULT_ID) references STEP_RESULT(ID) ON DELETE CASCADE" +
        "   )",
        "create unique index STEP_RESULT_ATTRIBUTE_PK on STEP_RESULT_ATTRIBUTE(ID)",
        "create unique index STEP_RESULT_ATTRIBUTE_AK1 on STEP_RESULT_ATTRIBUTE(STEP_RESULT_ID, NAME)",
        "create index STEP_RESULT_ATTRIBUTE_FK1 on STEP_RESULT_ATTRIBUTE(STEP_RESULT_ID)"
      };

      st = conn.createStatement();
      PersistanceUtils.executeDDLs(st, ddls);

      // update version
      log.info("Updating version");
      st = conn.createStatement();
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '6' where NAME = 'parabuild.schema.version' ");
      IoUtils.closeHard(st);
    } finally {
      IoUtils.closeHard(st);
    }
  }


  public int upgraderVersion() {
    return 6;
  }
}