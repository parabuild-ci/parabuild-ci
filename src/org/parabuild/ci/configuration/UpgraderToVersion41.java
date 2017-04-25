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
 * Upgrades to version 41.
 *
 */
final class UpgraderToVersion41 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion41.class);


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

      log.debug("Altering tables");
      PersistanceUtils.executeDDLs(st, new String[]{
        "create cached table PROJECT ( " +
          "  ID integer not null identity, " +
          "  NAME varchar(80) not null, " +
          "  DESCRIPTION varchar(1024) not null, " +
          "  KEY varchar(80) not null, " +
          "  TYPE tinyint not null, " +
          "  DELETED char(1) not null, " +
          "  TIMESTAMP bigint not null, " +
          "  constraint PROJECT_UC1 unique (ID), " +
          "  constraint PROJECT_UC2 unique (KEY) " +
          ')',
        "create index PROJECT_IX1 on PROJECT(NAME)",
        "create index PROJECT_IX2 on PROJECT(TYPE, DELETED)",

        "create cached table PROJECT_ATTRIBUTE ( " +
          "  PROJECT_ID integer not null, " +
          "  ID integer not null identity, " +
          "  NAME varchar(80) not null, " +
          "  VALUE varchar(1024), " +
          "  TIMESTAMP bigint not null, " +
          "  constraint PROJECT_ATTRIBUTE_UC1 unique (ID), " +
          "  constraint PROJECT_ATTRIBUTE_UC2 unique (PROJECT_ID, NAME), " +
          "  constraint PROJECT_ATTRIBUTE_FC1 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE " +
          ')',

        "create cached table PROJECT_BUILD ( " +
          "  ID integer not null identity, " +
          "  PROJECT_ID integer not null, " +
          "  ACTIVE_BUILD_ID integer not null, " +
          "  constraint PROJECT_BUILD_UC1 unique (ID), " +
          "  constraint PROJECT_BUILD_UC2 unique (PROJECT_ID, ACTIVE_BUILD_ID), " +
          "  constraint PROJECT_BUILD_UC3 unique (ACTIVE_BUILD_ID), " +
          "  constraint PROJECT_BUILD_FC1 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE, " +
          "  constraint PROJECT_BUILD_FC2 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE " +
          ')',

        "create cached table PROJECT_RESULT_GROUP ( " +
          "  ID integer not null identity, " +
          "  PROJECT_ID integer not null, " +
          "  RESULT_GROUP_ID integer not null, " +
          "  constraint PROJECT_RESULT_GROUP_UC1 unique (ID), " +
          "  constraint PROJECT_RESULT_GROUP_UC2 unique (PROJECT_ID, RESULT_GROUP_ID), " +
          "  constraint PROJECT_RESULT_GROUP_UC3 unique (RESULT_GROUP_ID), " +
          "  constraint PROJECT_RESULT_GROUP_FC1 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE, " +
          "  constraint PROJECT_RESULT_GROUP_FC2 foreign key (RESULT_GROUP_ID) references RESULT_GROUP(ID) ON DELETE CASCADE " +
          ')',

        "insert into PROJECT (ID, NAME, DESCRIPTION, KEY, TYPE, DELETED, TIMESTAMP) values (0, 'System', 'System project', 'SYSTEM', 1, 'N', 0)",

        "insert into PROJECT (ID, NAME, DESCRIPTION, KEY, TYPE, DELETED, TIMESTAMP) values (1, 'My project', 'This project is created as a part of migration to the project-based build management', 'FIRSTPROJECT', 2, 'N', 0)",
      });

      // go through all the builds and attach them to the initial project
      attachBuildsToInitialProject(conn);

      // go through all the result groups and attach them to the initial project
      attachResultGroupsToInitialProject(conn);

      //
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


  private void attachBuildsToInitialProject(final Connection conn) throws SQLException {
    PreparedStatement psActiveBuilds = null;
    PreparedStatement psInsertProjectBuild = null;
    ResultSet rsActiveBuilds = null; // NOPMD CloseResource
    try {
      psActiveBuilds = conn.prepareStatement("select ID from ACTIVE_BUILD");
      psInsertProjectBuild = conn.prepareStatement("insert into PROJECT_BUILD (PROJECT_ID, ACTIVE_BUILD_ID) values (1, ?)");
      rsActiveBuilds = psActiveBuilds.executeQuery();
      while (rsActiveBuilds.next()) {
        final int activeBuildID = rsActiveBuilds.getInt(1);
        psInsertProjectBuild.setInt(1, activeBuildID);
        psInsertProjectBuild.executeUpdate();
      }
    } finally {
      IoUtils.closeHard(rsActiveBuilds);
      IoUtils.closeHard(psActiveBuilds);
      IoUtils.closeHard(psInsertProjectBuild);
    }
  }


  private void attachResultGroupsToInitialProject(final Connection conn) throws SQLException {
    PreparedStatement psResultGroups = null; // NOPMD CloseResource
    PreparedStatement psInsertProjectResultGroup = null; // NOPMD CloseResource
    ResultSet rsResultGroups = null; // NOPMD CloseResource
    try {
      psResultGroups = conn.prepareStatement("select ID from RESULT_GROUP");
      psInsertProjectResultGroup = conn.prepareStatement("insert into PROJECT_RESULT_GROUP (PROJECT_ID, RESULT_GROUP_ID) values (1, ?)");
      rsResultGroups = psResultGroups.executeQuery();
      while (rsResultGroups.next()) {
        final int resultGroupID = rsResultGroups.getInt(1);
        psInsertProjectResultGroup.setInt(1, resultGroupID);
        psInsertProjectResultGroup.executeUpdate();
      }
    } finally {
      IoUtils.closeHard(rsResultGroups);
      IoUtils.closeHard(psResultGroups);
      IoUtils.closeHard(psInsertProjectResultGroup);
    }
  }


  public int upgraderVersion() {
    return 41;
  }
}
