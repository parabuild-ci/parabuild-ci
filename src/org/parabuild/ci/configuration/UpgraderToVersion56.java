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
 * Upgrades to version 56.
 */
final class UpgraderToVersion56 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion56.class);


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

      log.debug("Altering table");
      PersistanceUtils.executeDDLs(st, new String[]{
        "drop table MERGE_QUEUE_MEMBER",

        "drop table MERGE_QUEUE_BUILD_RUN",

        "drop table MERGE_QUEUE",

        "create cached table MERGE (\n" +
          "    ID integer not null identity,\n" +
          "    MERGE_CONFIGURATION_ID integer not null,\n" +
          "    CREATED  datetime not null,\n" +
          "    VALIDATED char(1) not null,\n" +
          "    RESULT_CODE tinyint not null,\n" +
          "    constraint MERGE_UC1 unique (ID),\n" +
          "    constraint MERGE_FC1 foreign key (MERGE_CONFIGURATION_ID) references MERGE_CONFIGURATION(ID) ON DELETE CASCADE\n" +
          ')',

        "create cached table MERGE_CHANGELIST (\n" +
          "    ID integer not null identity,\n" +
          "    MERGE_ID integer not null,\n" +
          "    BRANCH_CHANGELIST_ID integer not null,\n" +
          "    RESULT_CODE tinyint not null,\n" +
          "    RESULT_DESCRIPTION varchar(50000) not null,\n" +
          "    constraint MERGE_CHANGELIST_UC1 unique (ID),\n" +
          "    constraint MERGE_CHANGELIST_FC1 foreign key (MERGE_ID) references MERGE(ID) ON DELETE CASCADE,\n" +
          "    constraint MERGE_CHANGELIST_FC2 foreign key (BRANCH_CHANGELIST_ID) references BRANCH_CHANGELIST(ID) ON DELETE CASCADE\n" +
          ')',

        "create cached table MERGE_SOURCE_BUILD_RUN (\n" +
          "    ID integer not null identity,\n" +
          "    MERGE_ID integer not null,\n" +
          "    BUILD_RUN_ID integer not null,\n" +
          "    constraint MERGE_SOURCE_BUILD_RUN_UC1 unique (ID),\n" +
          "    constraint MERGE_SOURCE_BUILD_RUN_UC2 unique (MERGE_ID),\n" +
          "    constraint MERGE_SOURCE_BUILD_RUN_FC1 foreign key (MERGE_ID) references MERGE(ID) ON DELETE CASCADE,\n" +
          "    constraint MERGE_SOURCE_BUILD_RUN_FC2 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE\n" +
          ')',

        "create cached table MERGE_TARGET_BUILD_RUN (\n" +
          "    ID integer not null identity,\n" +
          "    MERGE_ID integer not null,\n" +
          "    BUILD_RUN_ID integer not null,\n" +
          "    constraint MERGE_TARGET_BUILD_RUN_UC1 unique (ID),\n" +
          "    constraint MERGE_TARGET_BUILD_RUN_UC2 unique (MERGE_ID),\n" +
          "    constraint MERGE_TARGET_BUILD_RUN_FC1 foreign key (MERGE_ID) references MERGE(ID) ON DELETE CASCADE,\n" +
          "    constraint MERGE_TARGET_BUILD_RUN_FC2 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE\n" +
          ')',

        " alter table BUILD_RUN add column TYPE tinyint default 0 not null ",
        " update BUILD_RUN set TYPE=0 ",
        " alter table BUILD_RUN alter column TYPE drop default ",

        "drop index BUILD_RUN_IX1",
        "drop index BUILD_RUN_IX2",
        "drop index BUILD_RUN_IX3",

        "create unique index BUILD_RUN_IX1 on BUILD_RUN(ID, COMPLETE, TYPE)",
        "create index BUILD_RUN_IX2 on BUILD_RUN(BUILD_ID, COMPLETE, TYPE, RESULT, RERUN)",
        "create index BUILD_RUN_IX3 on BUILD_RUN(ACTIVE_BUILD_ID, COMPLETE, TYPE)",
      });

      log.debug("Updating version");
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
    return 56;
  }
}
