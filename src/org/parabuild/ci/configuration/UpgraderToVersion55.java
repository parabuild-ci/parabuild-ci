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
 * Upgrades to version 55.
 */
final class UpgraderToVersion55 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion55.class);


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
        "drop table MERGE_ATTRIBUTE",
        "drop table MERGE",
        "drop table ACTIVE_MERGE",
        "create cached table MERGE_CONFIGURATION (\n" +
          "  ID integer not null identity,\n" +
          "  ACTIVE_MERGE_CONFIGURATION_ID integer null,\n" +
          "  BRANCH_VIEW varchar(32000) not null,\n" +
          "  BRANCH_VIEW_NAME varchar(512) not null,\n" +
          "  BRANCH_VIEW_SOURCE tinyint not null,\n" +
          "  CONFLICT_RESOLUTION_MODE tinyint not null,\n" +
          "  DESCRIPTION varchar(1024) not null,\n" +
          "  DISCRIMINATOR char(1) not null,\n" +
          "  INDIRECT_MERGE char(1) not null,\n" +
          "  MARKER varchar(1024),\n" +
          "  MERGE_MODE tinyint not null,\n" +
          "  NAME varchar(80) not null,\n" +
          "  PRESERVE_MARKER char(1) not null,\n" +
          "  REVERSE_BRANCH_VIEW char(1) not null,\n" +
          "  SOURCE_BUILD_ID integer not null,\n" +
          "  TARGET_BUILD_ID integer not null,\n" +
          "  TIMESTAMP bigint not null,\n" +
          "  constraint MERGE_CONFIGURATION_UC1 unique (ID),\n" +
          "  constraint MERGE_CONFIGURATION_FC1 foreign key (SOURCE_BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE,\n" +
          "  constraint MERGE_CONFIGURATION_FC2 foreign key (TARGET_BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE\n" +
          ')',
        "create index MERGE_CONFIGURATION_IX1 on MERGE_CONFIGURATION(ID, DISCRIMINATOR)",

        "create cached table MERGE_CONFIGURATION_ATTRIBUTE (\n" +
          "  MERGE_CONFIGURATION_ID integer not null,\n" +
          "  ID integer not null identity,\n" +
          "  NAME varchar(80) not null,\n" +
          "  VALUE varchar(1024),\n" +
          "  TIMESTAMP bigint not null,\n" +
          "  constraint MERGE_CONFIGURATION_ATTRIBUTE_UC1 unique (ID),\n" +
          "  constraint MERGE_CONFIGURATION_ATTRIBUTE_UC2 unique (MERGE_CONFIGURATION_ID, NAME),\n" +
          "  constraint MERGE_CONFIGURATION_ATTRIBUTE_FC1 foreign key (MERGE_CONFIGURATION_ID) references MERGE_CONFIGURATION(ID) ON DELETE CASCADE\n" +
          ')',

        "create cached table MERGE_SERVICE_CONFIGURATION (\n" +
          "  ID integer not null,\n" +
          "  PROJECT_ID integer not null,\n" +
          "  DELETED char(1) not null,\n" +
          "  STARTUP_MODE tinyint not null,\n" +
          "  TIMESTAMP bigint not null,\n" +
          "  constraint MERGE_SERVICE_CONFIGURATION_UC1 unique (ID),\n" +
          "  constraint MERGE_SERVICE_CONFIGURATION_UC2 unique (ID, DELETED),\n" +
          "  constraint MERGE_SERVICE_CONFIGURATION_FC1 foreign key (ID) references MERGE_CONFIGURATION(ID) ON DELETE CASCADE,\n" +
          "  constraint MERGE_SERVICE_CONFIGURATION_FC2 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE\n" +
          ')',

        "create cached table BRANCH_CHANGELIST (\n" +
          "  ID integer not null identity,\n" +
          "  MERGE_CONFIGURATION_ID integer not null,\n" +
          "  CHANGELIST_ID integer not null,\n" +
          "  MERGE_STATUS tinyint not null,\n" +
          "  constraint BRANCH_CHANGELIST_CONFIGURATION_UC1 unique (ID),\n" +
          "  constraint BRANCH_CHANGELIST_CONFIGURATION_UC2 unique (MERGE_CONFIGURATION_ID, CHANGELIST_ID),\n" +
          "  constraint BRANCH_CHANGELIST_FC1 foreign key (MERGE_CONFIGURATION_ID) references MERGE_CONFIGURATION(ID) ON DELETE CASCADE,\n" +
          "  constraint BRANCH_CHANGELIST_FC2 foreign key (CHANGELIST_ID) references CHANGELIST(ID) ON DELETE CASCADE\n" +
          ')',
        "create index BRANCH_CHANGELIST_IX1 on BRANCH_CHANGELIST(MERGE_CONFIGURATION_ID, MERGE_STATUS)",

        "create cached table MERGE_QUEUE (\n" +
          "  ID integer not null identity,\n" +
          "  MERGE_CONFIGURATION_ID integer not null,\n" +
          "  VALIDATED char(1) not null,\n" +
          "  constraint MERGE_QUEUE_UC1 unique (ID),\n" +
          "  constraint MERGE_QUEUE_FC1 foreign key (MERGE_CONFIGURATION_ID) references MERGE_CONFIGURATION(ID) ON DELETE CASCADE\n" +
          ')',

        "create cached table MERGE_QUEUE_MEMBER (\n" +
          "  ID integer not null identity,\n" +
          "  MERGE_QUEUE_ID integer not null,\n" +
          "  BRANCH_CHANGELIST_ID integer not null,\n" +
          "  constraint MERGE_QUEUE_MEMBER_UC1 unique (ID),\n" +
          "  constraint MERGE_QUEUE_MEMBER_FC1 foreign key (MERGE_QUEUE_ID) references MERGE_QUEUE(ID) ON DELETE CASCADE,\n" +
          "  constraint MERGE_QUEUE_MEMBER_FC2 foreign key (BRANCH_CHANGELIST_ID) references BRANCH_CHANGELIST(ID) ON DELETE CASCADE\n" +
          ')',

        "create cached table MERGE_QUEUE_BUILD_RUN (\n" +
          "  ID integer not null identity,\n" +
          "  MERGE_QUEUE_ID integer not null,\n" +
          "  BUILD_RUN_ID integer not null,\n" +
          "  constraint MERGE_QUEUE_BUILD_RUN_UC1 unique (ID),\n" +
          "  constraint MERGE_QUEUE_BUILD_RUN_UC2 unique (MERGE_QUEUE_ID),\n" +
          "  constraint MERGE_QUEUE_BUILD_RUN_FC1 foreign key (MERGE_QUEUE_ID) references MERGE_QUEUE(ID) ON DELETE CASCADE,\n" +
          "  constraint MERGE_QUEUE_BUILD_RUN_FC2 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE\n" +
          ')',

        "create cached table BRANCH_BUILD_RUN_PARTICIPANT (\n" +
          "  ID integer not null identity,\n" +
          "  BRANCH_CHANGELIST_ID integer not null,\n" +
          "  BUILD_RUN_PARTICIPANT_ID integer not null,\n" +
          "  constraint BRANCH_BUILD_RUN_PARTICIPANT_UC1 unique (ID),\n" +
          "  constraint BRANCH_BUILD_RUN_PARTICIPANT_UC2 unique (BRANCH_CHANGELIST_ID, BUILD_RUN_PARTICIPANT_ID),\n" +
          "  constraint BRANCH_BUILD_RUN_PARTICIPANT_FC1 foreign key (BRANCH_CHANGELIST_ID) references BRANCH_CHANGELIST(ID) ON DELETE CASCADE,\n" +
          "  constraint BRANCH_BUILD_RUN_PARTICIPANT_FC2 foreign key (BUILD_RUN_PARTICIPANT_ID) references BUILD_RUN_PARTICIPANT(ID) ON DELETE CASCADE\n" +
          ')',

          "create index CHANGELIST_IX2 on CHANGELIST(NUMBER, USER, CREATED)",
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
    return 55;
  }
}
