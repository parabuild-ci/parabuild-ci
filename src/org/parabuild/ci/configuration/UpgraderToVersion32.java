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
 * Upgrades to version 32. Adds manual label field to build run.
 */
final class UpgraderToVersion32 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion32.class);


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
      addBooleanColumnToGroup(st, "ALLOWED_TO_PUBLISH_RESULTS");
      addBooleanColumnToGroup(st, "ALLOWED_TO_DELETE_RESULTS");
      addBooleanColumnToGroup(st, "ALLOWED_TO_CREATE_RESULT_GROUP");
      addBooleanColumnToGroup(st, "ALLOWED_TO_DELETE_RESULT_GROUP");
      addBooleanColumnToGroup(st, "ALLOWED_TO_UPDATE_RESULT_GROUP");
      addBooleanColumnToGroup(st, "ALLOWED_TO_VIEW_RESULT_GROUP");

      final String [] update = {"create cached table RESULT_GROUP (" +
        "  ID integer not null identity," +
        "  NAME varchar(100) not null," +
        "  DESCRIPTION varchar(1024) not null," +
        "  LAST_PUBLISHED datetime null," +
        "  ENABLED char(1) not null," +
        "  DELETED char(1) not null," +
        "  TIMESTAMP bigint not null," +
        "  constraint RESULT_GROUP_UC1 unique (ID)" +
        ')',
        "create index RESULT_GROUP_IX1 on RESULT_GROUP(ENABLED, DELETED);",

        "create cached table RESULT_GROUP_ATTRIBUTE (" +
          "  ID integer not null identity," +
          "  RESULT_GROUP_ID integer not null," +
          "  NAME varchar(80) not null," +
          "  VALUE varchar(1024)," +
          "  TIMESTAMP bigint not null," +
          "  constraint RESULT_GROUP_ATTRIBUTE_UC1 unique (ID)," +
          "  constraint RESULT_GROUP_ATTRIBUTE_UC2 unique (RESULT_GROUP_ID, NAME)," +
          "  constraint RESULT_GROUP_ATTRIBUTE_FC1 foreign key (RESULT_GROUP_ID) references RESULT_GROUP(ID) ON DELETE CASCADE" +
          ')',

        "create cached table PUBLISHED_STEP_RESULT (" +
          "  ID integer not null identity," +
          "  RESULT_GROUP_ID integer not null," +
          "  ACTIVE_BUILD_ID integer not null," +
          "  BUILD_RUN_ID integer not null," +
          "  STEP_RESULT_ID integer not null," +
          "  PUBLISH_DATE  datetime not null," +
          "  BUILD_NAME  varchar(254) not null," +
          "  BUILD_RUN_DATE  datetime not null," +
          "  BUILD_RUN_NUMBER integer not null," +
          "  constraint PUBLISHED_STEP_RESULT_UC1 unique (ID)," +
          "  constraint PUBLISHED_STEP_RESULT_UC2 unique (RESULT_GROUP_ID, STEP_RESULT_ID)," +
          "  constraint PUBLISHED_STEP_RESULT_FC1 foreign key (RESULT_GROUP_ID) references RESULT_GROUP(ID) ON DELETE CASCADE," +
          "  constraint PUBLISHED_STEP_RESULT_FC2 foreign key (STEP_RESULT_ID) references STEP_RESULT(ID) ON DELETE CASCADE," +
          "  constraint PUBLISHED_STEP_RESULT_FC3 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE," +
          "  constraint PUBLISHED_STEP_RESULT_FC4 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE" +
          ')',
        "create index PUBLISHED_STEP_RESULT_IX1 on PUBLISHED_STEP_RESULT(RESULT_GROUP_ID, PUBLISH_DATE)",

        "create cached table PUBLISHED_STEP_RESULT_ATTRIBUTE (" +
          "  ID integer not null identity," +
          "  PUBLISHED_STEP_RESULT_ID integer not null," +
          "  NAME varchar(80) not null," +
          "  VALUE varchar(1024)," +
          "  TIMESTAMP bigint not null," +
          "  constraint PUBLISHED_STEP_RESULT_ATTRIBUTE_UC1 unique (ID)," +
          "  constraint PUBLISHED_STEP_RESULT_ATTRIBUTE_UC2 unique (PUBLISHED_STEP_RESULT_ID, NAME)," +
          "  constraint PUBLISHED_STEP_RESULT_ATTRIBUTE_FC1 foreign key (PUBLISHED_STEP_RESULT_ID) references PUBLISHED_STEP_RESULT(ID) ON DELETE CASCADE" +
          ')',

        "create cached table RESULT_GROUP_ACCESS (" +
          "  ID integer not null identity," +
          "  GROUP_ID integer not null," +
          "  RESULT_GROUP_ID integer not null," +
          "  TIMESTAMP bigint not null," +
          "  constraint RESULT_GROUP_ACCESS_UC1 unique (ID)," +
          "  constraint RESULT_GROUP_ACCESS_UC2 unique (RESULT_GROUP_ID, GROUP_ID)," +
          "  constraint RESULT_GROUP_ACCESS_FC1 foreign key (RESULT_GROUP_ID) references RESULT_GROUP(ID) ON DELETE CASCADE," +
          "  constraint RESULT_GROUP_ACCESS_FC2 foreign key (GROUP_ID) references GROUPS(ID) ON DELETE CASCADE" +
          ')',

        "create cached table BUILD_RUN_ACTION (" +
          "  ID integer not null identity," +
          "  BUILD_RUN_ID integer not null," +
          "  CODE tinyint not null," +
          "  ACTION varchar(1024) not null," +
          "  ACTION_DATE datetime not null," +
          "  DESCRIPTION varchar(1024) not null," +
          "  USER_ID integer not null," +
          "  constraint BUILD_RUN_ACTION_UC1 unique (ID)," +
          "  constraint BUILD_RUN_ACTION_FC1 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE," +
          "  constraint BUILD_RUN_ACTION_FC2 foreign key (USER_ID) references USERS(ID) ON DELETE CASCADE" +
          ')',
        "create index BUILD_RUN_ACTION_IX1 on BUILD_RUN_ACTION(BUILD_RUN_ID, ACTION_DATE)",
      };
      PersistanceUtils.executeDDLs(st, update);

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


  private void addBooleanColumnToGroup(final Statement st, final String colName) throws SQLException {
    final String [] update = {" alter table GROUPS add column " + colName + " char(1)  default 'N' not null ",
      " update GROUPS set " + colName + "='N' ",
      " alter table GROUPS alter column " + colName + " drop default ",
    };
    PersistanceUtils.executeDDLs(st, update);
  }


  public int upgraderVersion() {
    return 32;
  }
}
