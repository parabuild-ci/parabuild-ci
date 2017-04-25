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
 * Upgrades to version 11
 */
final class UpgraderToVersion11 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion11.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null;  // NOPMD
    try {
      conn.setAutoCommit(false);
      final String[] ddls = {
        "create cached table DAILY_STATS ( " +
        "  ID integer not null identity, " +
        "  ACTIVE_BUILD_ID integer not null, " +
        "  SAMPLE_TIME datetime, " +
        "  SUCCESSFUL_BUILD_COUNT integer not null, " +
        "  SUCCESSFUL_BUILD_PERCENT integer not null, " +
        "  FAILED_BUILD_COUNT integer not null, " +
        "  FAILED_BUILD_PERCENT integer not null, " +
        "  TOTAL_BUILD_COUNT integer not null, " +
        "  CHANGE_LIST_COUNT integer not null, " +
        "  ISSUE_COUNT integer not null, " +
        "  constraint DAILY_STATS_UC1 unique (ID), " +
        "  constraint DAILY_STATS_UC2 unique (ACTIVE_BUILD_ID, SAMPLE_TIME), " +
        "  constraint DAILY_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE " +
        ") "
        ,
        "create cached table HOURLY_STATS ( " +
        "  ID integer not null identity, " +
        "  ACTIVE_BUILD_ID integer not null, " +
        "  SAMPLE_TIME datetime, " +
        "  SUCCESSFUL_BUILD_COUNT integer not null, " +
        "  SUCCESSFUL_BUILD_PERCENT integer not null, " +
        "  FAILED_BUILD_COUNT integer not null, " +
        "  FAILED_BUILD_PERCENT integer not null, " +
        "  TOTAL_BUILD_COUNT integer not null, " +
        "  CHANGE_LIST_COUNT integer not null, " +
        "  ISSUE_COUNT integer not null, " +
        "  constraint HOURLY_STATS_UC1 unique (ID), " +
        "  constraint HOURLY_STATS_UC2 unique (ACTIVE_BUILD_ID, SAMPLE_TIME), " +
        "  constraint HOURLY_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE " +
        ") "
        ,
        "create cached table MONTHLY_STATS ( " +
        "  ID integer not null identity, " +
        "  ACTIVE_BUILD_ID integer not null, " +
        "  SAMPLE_TIME datetime, " +
        "  SUCCESSFUL_BUILD_COUNT integer not null, " +
        "  SUCCESSFUL_BUILD_PERCENT integer not null, " +
        "  FAILED_BUILD_COUNT integer not null, " +
        "  FAILED_BUILD_PERCENT integer not null, " +
        "  TOTAL_BUILD_COUNT integer not null, " +
        "  CHANGE_LIST_COUNT integer not null, " +
        "  ISSUE_COUNT integer not null, " +
        "  constraint MONTHLY_STATS_UC1 unique (ID), " +
        "  constraint MONTHLY_STATS_UC2 unique (ACTIVE_BUILD_ID, SAMPLE_TIME), " +
        "  constraint MONTHLY_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE " +
        ") "
        ,
        "create cached table YEARLY_STATS ( " +
        "  ID integer not null identity, " +
        "  ACTIVE_BUILD_ID integer not null, " +
        "  SAMPLE_TIME datetime, " +
        "  SUCCESSFUL_BUILD_COUNT integer not null, " +
        "  SUCCESSFUL_BUILD_PERCENT integer not null, " +
        "  FAILED_BUILD_COUNT integer not null, " +
        "  FAILED_BUILD_PERCENT integer not null, " +
        "  TOTAL_BUILD_COUNT integer not null, " +
        "  CHANGE_LIST_COUNT integer not null, " +
        "  ISSUE_COUNT integer not null, " +
        "  constraint YEARLY_STATS_UC1 unique (ID), " +
        "  constraint YEARLY_STATS_UC2 unique (ACTIVE_BUILD_ID, SAMPLE_TIME), " +
        "  constraint YEARLY_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE " +
        ") "
        ,
        "create cached table HOURLY_DISTRIBUTION ( " +
        "  ID integer not null identity, " +
        "  ACTIVE_BUILD_ID integer not null, " +
        "  TARGET integer, " +
        "  SUCCESSFUL_BUILD_COUNT integer not null, " +
        "  FAILED_BUILD_COUNT integer not null, " +
        "  TOTAL_BUILD_COUNT integer not null, " +
        "  CHANGE_LIST_COUNT integer not null, " +
        "  ISSUE_COUNT integer not null, " +
        "  constraint HOURLY_DISTRIBUTION_UC1 unique (ID), " +
        "  constraint HOURLY_DISTRIBUTION_UC2 unique (ACTIVE_BUILD_ID, TARGET), " +
        "  constraint HOURLY_DISTRIBUTION_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE " +
        ") "
        ,
        "create cached table WEEKDAY_DISTRIBUTION ( " +
        "  ID integer not null identity, " +
        "  ACTIVE_BUILD_ID integer not null, " +
        "  TARGET integer, " +
        "  SUCCESSFUL_BUILD_COUNT integer not null, " +
        "  FAILED_BUILD_COUNT integer not null, " +
        "  TOTAL_BUILD_COUNT integer not null, " +
        "  CHANGE_LIST_COUNT integer not null, " +
        "  ISSUE_COUNT integer not null, " +
        "  constraint WEEKDAY_DISTRIBUTION_UC1 unique (ID), " +
        "  constraint WEEKDAY_DISTRIBUTION_UC2 unique (ACTIVE_BUILD_ID, TARGET), " +
        "  constraint WEEKDAY_DISTRIBUTION_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE " +
          ')'
        ,

        "create index BUILD_RUN_PARTICIPANT_IX2 on BUILD_RUN_PARTICIPANT(BUILD_RUN_ID, FIRST_BUILD_RUN_ID)",

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
    return 11;
  }
}