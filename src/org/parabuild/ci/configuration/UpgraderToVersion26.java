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
 * Upgrades to version 26
 */
final class UpgraderToVersion26 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion26.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    try {
      conn.setAutoCommit(false);
      final String[] ddls = {
        "create cached table HOURLY_TEST_STATS (" +
          "  ID integer not null identity," +
          "  ACTIVE_BUILD_ID integer not null," +
          "  TEST_CODE tinyint not null," +
          "  SAMPLE_TIME datetime," +
          "  SUCCESSFUL_TEST_COUNT integer not null," +
          "  SUCCESSFUL_TEST_PERCENT integer not null," +
          "  ERROR_TEST_COUNT integer not null," +
          "  ERROR_TEST_PERCENT integer not null," +
          "  FAILED_TEST_COUNT integer not null," +
          "  FAILED_TEST_PERCENT integer not null," +
          "  TOTAL_TEST_COUNT integer not null," +
          "  constraint HOURLY_TEST_STATS_UC1 unique (ID)," +
          "  constraint HOURLY_TEST_STATS_UC2 unique (ACTIVE_BUILD_ID, TEST_CODE, SAMPLE_TIME)," +
          "  constraint HOURLY_TEST_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE" +
          ')',

        "create cached table DAILY_TEST_STATS (" +
          "  ID integer not null identity," +
          "  ACTIVE_BUILD_ID integer not null," +
          "  TEST_CODE tinyint not null," +
          "  SAMPLE_TIME datetime," +
          "  SUCCESSFUL_TEST_COUNT integer not null," +
          "  SUCCESSFUL_TEST_PERCENT integer not null," +
          "  ERROR_TEST_COUNT integer not null," +
          "  ERROR_TEST_PERCENT integer not null," +
          "  FAILED_TEST_COUNT integer not null," +
          "  FAILED_TEST_PERCENT integer not null," +
          "  TOTAL_TEST_COUNT integer not null," +
          "  constraint DAILY_TEST_STATS_UC1 unique (ID)," +
          "  constraint DAILY_TEST_STATS_UC2 unique (ACTIVE_BUILD_ID, TEST_CODE, SAMPLE_TIME)," +
          "  constraint DAILY_TEST_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE" +
          ')',

        "create cached table MONTHLY_TEST_STATS (" +
          "  ID integer not null identity," +
          "  ACTIVE_BUILD_ID integer not null," +
          "  TEST_CODE tinyint not null," +
          "  SAMPLE_TIME datetime," +
          "  SUCCESSFUL_TEST_COUNT integer not null," +
          "  SUCCESSFUL_TEST_PERCENT integer not null," +
          "  ERROR_TEST_COUNT integer not null," +
          "  ERROR_TEST_PERCENT integer not null," +
          "  FAILED_TEST_COUNT integer not null," +
          "  FAILED_TEST_PERCENT integer not null," +
          "  TOTAL_TEST_COUNT integer not null," +
          "  constraint MONTHLY_TEST_STATS_UC1 unique (ID)," +
          "  constraint MONTHLY_TEST_STATS_UC2 unique (ACTIVE_BUILD_ID, TEST_CODE, SAMPLE_TIME)," +
          "  constraint MONTHLY_TEST_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE" +
          ')',

        "create cached table YEARLY_TEST_STATS (" +
          "  ID integer not null identity," +
          "  ACTIVE_BUILD_ID integer not null," +
          "  TEST_CODE tinyint not null," +
          "  SAMPLE_TIME datetime," +
          "  SUCCESSFUL_TEST_COUNT integer not null," +
          "  SUCCESSFUL_TEST_PERCENT integer not null," +
          "  ERROR_TEST_COUNT integer not null," +
          "  ERROR_TEST_PERCENT integer not null," +
          "  FAILED_TEST_COUNT integer not null," +
          "  FAILED_TEST_PERCENT integer not null," +
          "  TOTAL_TEST_COUNT integer not null," +
          "  constraint YEARLY_TEST_STATS_UC1 unique (ID)," +
          "  constraint YEARLY_TEST_STATS_UC2 unique (ACTIVE_BUILD_ID, TEST_CODE, SAMPLE_TIME)," +
          "  constraint YEARLY_TEST_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE" +
          ')',
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
    return 26;
  }
}
