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
 * Upgrades to version 9
 */
final class UpgraderToVersion9 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion9.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    try {
      conn.setAutoCommit(false);

      st = conn.createStatement();
      st.execute("drop index USERS_PK");
      st.execute("drop index USER_ATTRIBUTE_PK");
      st.execute("drop index USER_ATTRIBUTE_FK1");
      st.execute("drop index GROUPS_PK");
      st.execute("drop index GROUP_ATTRIBUTE_PK");
      st.execute("drop index GROUP_ATTRIBUTE_FK1");
      st.execute("drop index USER_GROUP_PK");
      st.execute("drop index USER_GROUP_FK1");
      st.execute("drop index USER_GROUP_FK2");
      st.execute("drop index SYSTEM_PROPERTY_PK");
      st.execute("drop index BUILD_CONFIG_PK");
      st.execute("drop index BUILD_CONFIG_ATTRIBUTE_PK");
      st.execute("drop index BUILD_CONFIG_ATTRIBUTE_FK1");
      st.execute("drop index BUILD_ACCESS_PK");
      st.execute("drop index BUILD_ACCESS_FK1");
      st.execute("drop index BUILD_ACCESS_FK2");
      st.execute("drop index SUBORDINATE_PK");
      st.execute("drop index SUBORDINATE_FK1");
      st.execute("drop index SUBORDINATE_FK2");
      st.execute("drop index SUBORDINATE_ATTRIBUTE_PK");
      st.execute("drop index SUBORDINATE_ATTRIBUTE_FK1");
      st.execute("drop index SOURCE_CONTROL_PROPERTY_PK");
      st.execute("drop index SOURCE_CONTROL_PROPERTY_FK1");
      st.execute("drop index SCM_PATH_PK");
      st.execute("drop index SCM_PATH_FK1");
      st.execute("drop index SCHEDULE_PROPERTY_PK");
      st.execute("drop index SCHEDULE_PROPERTY_FK1");
      st.execute("drop index BUILD_ATTRIBUTE_PK");
      st.execute("drop index BUILD_ATTRIBUTE_FK1");
      st.execute("drop index LABEL_PROPERTY_PK");
      st.execute("drop index LABEL_PROPERTY_FK1");
      st.execute("drop index LOG_CONFIG_PK");
      st.execute("drop index LOG_CONFIG_FK1");
      st.execute("drop index LOG_CONFIG_PROPERTY_PK");
      st.execute("drop index LOG_CONFIG_PROPERTY_FK1");
      st.execute("drop index RESULT_CONFIG_PK");
      st.execute("drop index RESULT_CONFIG_FK1");
      st.execute("drop index RESULT_CONFIG_PROPERTY_PK");
      st.execute("drop index RESULT_CONFIG_PROPERTY_FK1");
      st.execute("drop index VCS_USER_TO_EMAIL_MAP_PK");
      st.execute("drop index VCS_USER_TO_EMAIL_MAP_FK1");
      st.execute("drop index BUILD_WATCHER_PK");
      st.execute("drop index BUILD_WATCHER_FK1");
      st.execute("drop index BUILD_SEQUENCE_PK");
      st.execute("drop index BUILD_SEQUENCE_FK1");
      st.execute("drop index SCHEDULE_ITEM_PK");
      st.execute("drop index SCHEDULE_ITEM_FK1");
      st.execute("drop index ISSUE_TRACKER_PK");
      st.execute("drop index ISSUE_TRACKER_FK1");
      st.execute("drop index ISSUE_TRACKER_PROPERTY_PK");
      st.execute("drop index ISSUE_TRACKER_PROPERTY_FK1");
      st.execute("drop index BUILD_RUN_PK");
      st.execute("drop index BUILD_RUN_FK1");
      st.execute("drop index BUILD_RUN_ATTRIBUTE_PK");
      st.execute("drop index BUILD_RUN_ATTRIBUTE_FK1");
      st.execute("drop index STEP_RUN_PK");
      st.execute("drop index STEP_RUN_FK1");
      st.execute("drop index STEP_RUN_ATTRIBUTE_PK");
      st.execute("drop index STEP_RUN_ATTRIBUTE_FK1");
      st.execute("drop index STEP_LOG_FK1");
      st.execute("drop index STEP_RESULT_PK");
      st.execute("drop index STEP_RESULT_FK1");
      st.execute("drop index STEP_RESULT_ATTRIBUTE_PK");
      st.execute("drop index STEP_RESULT_ATTRIBUTE_FK1");
      st.execute("drop index CHANGELIST_PK");
      st.execute("drop index BUILD_CHANGELIST_PK");
      st.execute("drop index BUILD_CHANGELIST_FK1");
      st.execute("drop index BUILD_CHANGELIST_FK2");
      st.execute("drop index BUILD_RUN_PARTICIPANT_PK");
      st.execute("drop index BUILD_RUN_PARTICIPANT_FK1");
      st.execute("drop index BUILD_RUN_PARTICIPANT_FK2");
      st.execute("drop index BUILD_RUN_PARTICIPANT_FK3");
      st.execute("drop index CHANGE_PK");
      st.execute("drop index CHANGE_FK1");
      st.execute("drop index ISSUE_PK");
      st.execute("drop index ISSUE_ATTR_PK");
      st.execute("drop index ISSUE_ATTR_FK1");
      st.execute("drop index PENDING_ISSUE_PK");
      st.execute("drop index PENDING_ISSUE_FK1");
      st.execute("drop index PENDING_ISSUE_FK2");
      st.execute("drop index ISSUE_CHANGELIST_PK");
      st.execute("drop index ISSUE_CHANGELIST_FK1");
      st.execute("drop index ISSUE_CHANGELIST_FK2");
      st.execute("drop index RELEASE_NOTE_PK");
      st.execute("drop index RELEASE_NOTE_FK1");
      st.execute("drop index RELEASE_NOTE_FK2");

      // update version
      log.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '9' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();
    } finally {
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  /**
   * @return version this upgrader upgrades to.
   */
  public int upgraderVersion() {
    return 9;
  }
}