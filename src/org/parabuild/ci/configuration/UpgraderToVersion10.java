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
 * Upgrades to version 10
 */
final class UpgraderToVersion10 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion10.class);


  /**
   * Perform upgrade.
   */
  public final void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    PreparedStatement psInsertToActiveBuild = null;  // NOPMD
    ResultSet rs = null;  // NOPMD
    try {
      conn.setAutoCommit(false);
      final String[] sqlsPre = {
        "drop  index BUILD_ACCESS_AK1 ",
        "drop  index USER_GROUP_AK1 ",
        "drop  index USER_ATTRIBUTE_AK1 ",
        "drop  index GROUPS_AK1 ",
        "drop  index GROUP_ATTRIBUTE_AK1 ",
        "drop  index SYSTEM_PROPERTY_AK1 ",
        "drop  index SUBORDINATE_ATTRIBUTE_AK1 ",
        "drop  index SOURCE_CONTROL_PROPERTY_AK1 ",
        "drop  index SCHEDULE_PROPERTY_AK1 ",
        "drop  index BUILD_ATTRIBUTE_AK1 ",
        "drop  index LABEL_PROPERTY_AK1 ",
        "drop  index LOG_CONFIG_PROPERTY_AK1 ",
        "drop  index RESULT_CONFIG_PROPERTY_AK1 ",
        "drop  index VCS_USER_TO_EMAIL_MAP_AK1 ",
        "drop  index BUILD_WATCHER_AK1 ",
        "drop  index BUILD_SEQUENCE_AK1 ",
        "drop  index ISSUE_TRACKER_PROPERTY_AK1 ",
        "drop  index BUILD_RUN_ATTRIBUTE_AK1 ",
        "drop  index STEP_RESULT_ATTRIBUTE_AK1 ",
        "drop  index ISSUE_ATTR_AK1 ",
        "drop  index PENDING_ISSUE_AK1 ",
        "drop  index ISSUE_CHANGELIST_AK1 ",
        "drop  index RELEASE_NOTE_AK1 ",
        "drop  index BUILD_CHANGELIST_IX1 ",
        "drop  index BUILD_CONFIG_IX1 ",

        "alter table BUILD_ACCESS drop constraint BUILD_ACCESS_FC1 ",
        "alter table BUILD_CHANGELIST drop constraint BUILD_CHANGELIST_FC1 ",
        "alter table PENDING_ISSUE drop constraint PENDING_ISSUE_FC1 ",

        "drop   table BUILD_CONFIG_ATTRIBUTE ",
        "alter  table BUILD_CONFIG add column DISCRIMINATOR char(1) default 'C' not null ",
        "alter  table BUILD_CONFIG add column ORIGNL_CONFIG_ID integer default '-1' not null ",
        "alter  table BUILD_RUN add column ACTIVE_BUILD_ID integer default '-1' not null ",
        "update BUILD_CONFIG set DISCRIMINATOR='C', ORIGNL_CONFIG_ID = ID ",
        "update BUILD_RUN set ACTIVE_BUILD_ID = BUILD_ID ",

        "create cached table ACTIVE_BUILD ( " +
        "  ID integer not null, " +
        "  STARTUP_STATUS integer not null, " +
        "  DELETED char(1) not null, " +
        "  TIMESTAMP bigint not null, " +
        "  constraint ACTIVE_BUILD_UC1 unique (ID), " +
        "  constraint ACTIVE_BUILD_FC1 foreign key (ID) references BUILD_CONFIG(ID) ON DELETE CASCADE " +
        ") ",

        "create cached table ACTIVE_BUILD_ATTRIBUTE ( " +
        "  ID integer not null identity, " +
        "  ACTIVE_BUILD_ID integer not null, " +
        "  NAME varchar(80) not null, " +
        "  VALUE varchar(1024), " +
        "  TIMESTAMP bigint not null, " +
        "  constraint ACTIVE_BUILD_ATTRIBUTE_UC1 unique (ID), " +
        "  constraint ACTIVE_BUILD_ATTRIBUTE_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE " +
        ") ",
      };

      final String[] sqlAfter = {
        "alter table STEP_RUN drop column LABEL ",
        "alter table BUILD_CONFIG drop column STARTUP_STATUS ",
        "alter table BUILD_CONFIG drop column DELETED ",
        "alter table BUILD_CONFIG alter column DISCRIMINATOR drop default ",
        "alter table BUILD_CONFIG alter column ORIGNL_CONFIG_ID drop default ",
        "alter table BUILD_CONFIG add constraint BUILD_CONFIG_UC2 unique (ID, DISCRIMINATOR) ",
        "alter table BUILD_CONFIG add constraint BUILD_CONFIG_FC1 foreign key (ORIGNL_CONFIG_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE ",
        "alter table SCHEDULE_PROPERTY add constraint SCHEDULE_PROPERTY_UC2 unique (BUILD_ID, NAME) ",
        "alter table BUILD_ACCESS add constraint BUILD_ACCESS_FC1 foreign key (BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE ",
        "alter table BUILD_ACCESS add constraint BUILD_ACCESS_UC2 unique (BUILD_ID, GROUP_ID) ",
        "alter table USER_GROUP add constraint USER_GROUP_UC2 unique (USER_ID, GROUP_ID) ",
        "alter table BUILD_RUN alter column ACTIVE_BUILD_ID drop default ",
        "alter table BUILD_RUN add constraint BUILD_RUN_FC2 foreign key (ACTIVE_BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE ",
        "alter table PENDING_ISSUE add constraint PENDING_ISSUE_FC1 foreign key (BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE ",
        "alter table BUILD_CHANGELIST add constraint BUILD_CHANGELIST_FC1 foreign key (BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE ",
        "alter table BUILD_CHANGELIST add constraint BUILD_CHANGELIST_UC2  unique (BUILD_ID, CHANGELIST_ID) ",

        "alter table USER_ATTRIBUTE add constraint USER_ATTRIBUTE_UC2 unique (USER_ID, NAME) ",
        "alter table GROUPS add constraint GROUPS_UC2  unique (NAME) ",
        "alter table GROUP_ATTRIBUTE add constraint GROUP_ATTRIBUTE_UC2  unique (GROUP_ID, NAME) ",
        "alter table SYSTEM_PROPERTY add constraint SYSTEM_PROPERTY_UC2  unique (NAME) ",
        "alter table ACTIVE_BUILD_ATTRIBUTE add constraint ACTIVE_BUILD_ATTRIBUTE_UC2  unique (ID, NAME) ",
        "alter table SUBORDINATE_ATTRIBUTE add constraint SUBORDINATE_ATTRIBUTE_UC2  unique (SUBORDINATE_ID, NAME) ",
        "alter table SOURCE_CONTROL_PROPERTY add constraint SOURCE_CONTROL_PROPERTY_UC2  unique (BUILD_ID, NAME) ",
        "alter table BUILD_ATTRIBUTE add constraint BUILD_ATTRIBUTE_UC2  unique (BUILD_ID, NAME) ",
        "alter table LABEL_PROPERTY add constraint LABEL_PROPERTY_UC2  unique (BUILD_ID, NAME) ",
        "alter table LOG_CONFIG_PROPERTY add constraint LOG_CONFIG_PROPERTY_UC2  unique (LOG_CONFIG_ID, NAME) ",
        "alter table RESULT_CONFIG_PROPERTY add constraint RESULT_CONFIG_PROPERTY_UC2  unique (RESULT_CONFIG_ID, NAME) ",
        "alter table VCS_USER_TO_EMAIL_MAP add constraint VCS_USER_TO_EMAIL_MAP_UC2  unique (BUILD_ID, USER_NAME) ",
        "alter table BUILD_WATCHER add constraint BUILD_WATCHER_UC2  unique (BUILD_ID, EMAIL) ",
        "alter table BUILD_SEQUENCE add constraint BUILD_SEQUENCE_UC2  unique (BUILD_ID, NAME) ",
        "alter table ISSUE_TRACKER_PROPERTY add constraint ISSUE_TRACKER_PROPERTY_UC2  unique (ISSUE_TRACKER_ID, NAME) ",
        "alter table BUILD_RUN_ATTRIBUTE add constraint BUILD_RUN_ATTRIBUTE_UC2  unique (BUILD_RUN_ID, NAME) ",
        "alter table STEP_RESULT_ATTRIBUTE add constraint STEP_RESULT_ATTRIBUTE_UC2  unique (STEP_RESULT_ID, NAME) ",
        "alter table ISSUE_ATTR add constraint ISSUE_ATTR_UC2  unique (ISSUE_ID, NAME) ",
        "alter table PENDING_ISSUE add constraint PENDING_ISSUE_UC2  unique (BUILD_ID, ISSUE_ID) ",
        "alter table ISSUE_CHANGELIST add constraint ISSUE_CHANGELIST_UC2  unique (ISSUE_ID, CHANGELIST_ID) ",
        "alter table RELEASE_NOTE add constraint RELEASE_NOTE_UC2  unique (BUILD_RUN_ID, ISSUE_ID) ",

        "create unique index ACTIVE_BUILD_ATTRIBUTE_AK1 on ACTIVE_BUILD_ATTRIBUTE(ACTIVE_BUILD_ID, NAME) ",
        "create index BUILD_RUN_IX3 on BUILD_RUN(ACTIVE_BUILD_ID, COMPLETE) ",
        "create index ISSUE_TRACKER_IX1 on ISSUE_TRACKER(BUILD_ID, TYPE) "
      };

      log.debug("Alter structure");
      st = conn.createStatement();
      PersistanceUtils.executeDDLs(st, sqlsPre);

      log.debug("Populate active build");
      psInsertToActiveBuild = conn.prepareStatement(" insert into ACTIVE_BUILD " +
        "   (ID, STARTUP_STATUS, DELETED, TIMESTAMP ) values (?, ?, ?, 1) ");

      // interate builds
      rs = st.executeQuery("select ID, STARTUP_STATUS, DELETED  from BUILD_CONFIG");
      while (rs.next()) {
        final int configID = rs.getInt(1);
        final int startupStatus = rs.getInt(2);
        final String deleted = rs.getString(3);
        // copy active build info
        psInsertToActiveBuild.setInt(1, configID);
        psInsertToActiveBuild.setInt(2, startupStatus);
        psInsertToActiveBuild.setString(3, deleted);
        psInsertToActiveBuild.executeUpdate();
      }
      IoUtils.closeHard(rs);
      IoUtils.closeHard(psInsertToActiveBuild);


      // move attribute
      log.debug("Populate active build attributes");
      moveAttributesFromBuildConfigToActiveBuild(conn,
        new String[]{"build.number.sequence",
                     "build.stat.successful.builds.to.date",
                     "build.stat.failed.builds.to.date",
                     "build.stat.change.lists.to.date"});


      // final ddls
      PersistanceUtils.executeDDLs(st, sqlAfter);

      // update version
      log.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");
      IoUtils.closeHard(st);

      // finish
      conn.commit();

      // request post-startup config manager action
      System.setProperty(SystemConstants.SYSTEM_PROPERTY_POPULATE_BUILD_RUN_CONFIGS, "true");
    } finally {
      IoUtils.closeHard(rs);
      IoUtils.closeHard(psInsertToActiveBuild);
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  /**
   * Moves build configuration attribute from build config attr
   * to active build attr.
   */
  private static void moveAttributesFromBuildConfigToActiveBuild(final Connection conn, final String[] names) throws SQLException {

    PreparedStatement psSelect = null;
    PreparedStatement psInsert = null;
    PreparedStatement psDelete = null;
    try {


      psSelect = conn.prepareStatement("select VALUE, BUILD_ID from BUILD_ATTRIBUTE where NAME = ?");
      psInsert = conn.prepareStatement("insert into ACTIVE_BUILD_ATTRIBUTE (ID, NAME, VALUE, ACTIVE_BUILD_ID, TIMESTAMP) " +
        " values (null, ?, ?, ?, 1)");
      psDelete = conn.prepareStatement("delete from BUILD_ATTRIBUTE where NAME = ?");

      // copy
      for (int i = 0; i < names.length; i++) {
        final String name = names[i];
        ResultSet rsSelect = null; // NOPMD
        try {
          psSelect.setString(1, name);
          rsSelect = psSelect.executeQuery();
          while (rsSelect.next()) {
            final String value = rsSelect.getString(1);
            final int buildID = rsSelect.getInt(2);
            psInsert.setString(1, name);
            psInsert.setString(2, value);
            psInsert.setInt(3, buildID);
            psInsert.executeUpdate();
          }
        } finally {
          IoUtils.closeHard(rsSelect);
        }
      }

      // bulk delete old
      for (int i = 0; i < names.length; i++) {
        final String name = names[i];
        psDelete.setString(1, name);
        psDelete.executeUpdate();
      }
    } finally {
      IoUtils.closeHard(psSelect);
      IoUtils.closeHard(psInsert);
      IoUtils.closeHard(psDelete);
    }
  }


  public final int upgraderVersion() {
    return 10;
  }
}