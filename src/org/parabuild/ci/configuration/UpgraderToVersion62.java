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
 * Upgrades to version 62
 */
final class UpgraderToVersion62 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion62.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    Statement st = null; // NOPMD
    try {
      // add build result settings objects
      final String[] ddls = {
        // TEST_SUITE table
        "create cached table TEST_PACKAGE (" +
          "   ID integer not null identity," +
          "   NAME varchar(1024) not null," +
          "   constraint TEST_PACKAGE_UC1 unique (ID)," +
          "   constraint TEST_PACKAGE_UC2 unique (NAME)" +
          "   )",

        // TEST_CASE table
        "create cached table TEST_CASE (" +
          "   ID integer not null identity," +
          "   TEST_PACKAGE_ID integer not null," +
          "   NAME varchar(1024) not null," +
          "   constraint TEST_CASE_UC1 unique (ID)," +
          "   constraint TEST_CASE_UC2 unique (NAME)," +
          "   constraint TEST_CASE_FC1 foreign key (TEST_PACKAGE_ID) references TEST_PACKAGE(ID) ON DELETE CASCADE" +
          "   )",

        // BUILD_RUN_TEST table
        "create cached table BUILD_RUN_TEST (" +
          "  ID integer identity," +
          "  TEST_CASE_ID integer not null," +
          "  BUILD_RUN_ID integer not null," +
          "  RESULT_CODE  tinyint not null," +
          "  DURATION_MILLS integer not null," +
          "  NEW char(1) not null," +
          "  BROKEN char(1) not null," +
          "  BROKEN_BUILD_RUN_COUNT integer not null," +
          "  BROKEN_SINCE_BUILD_RUN_ID integer null," +
          "  MESSAGE varchar(16000) not null," +
          "  constraint BUILD_RUN_TEST_UC1 unique (ID)," +
          "  constraint BUILD_RUN_TEST_FC1 foreign key (TEST_CASE_ID) references TEST_CASE(ID) on delete cascade," +
          "  constraint BUILD_RUN_TEST_FC2 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) on delete cascade," +
          "  constraint BUILD_RUN_TEST_FC3 foreign key (BROKEN_SINCE_BUILD_RUN_ID) references  BUILD_RUN(ID) on delete cascade" +
                ')',
        "create index BUILD_RUN_TEST_IX1 on BUILD_RUN_TEST(BUILD_RUN_ID, NEW, BROKEN)",
        "create index BUILD_RUN_TEST_IX2 on BUILD_RUN_TEST(BUILD_RUN_ID, BROKEN)",
      };

      st = conn.createStatement();
      PersistanceUtils.executeDDLs(st, ddls);

      // update version
      log.info("Updating version");
      st = conn.createStatement();
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");
      IoUtils.closeHard(st);
    } finally {
      IoUtils.closeHard(st);
    }
  }


  public int upgraderVersion() {
    return 62;
  }
}
