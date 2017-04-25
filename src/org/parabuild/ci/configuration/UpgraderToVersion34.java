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

import org.parabuild.ci.object.*;
import org.parabuild.ci.common.*;

/**
 * Upgrades to version 34. Adds manual label field to build run.
 */
final class UpgraderToVersion34 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion34.class);


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
      final String [] update = {" alter table PUBLISHED_STEP_RESULT add column DESCRIPTION varchar(1024) default '' not null ",
        " update PUBLISHED_STEP_RESULT set DESCRIPTION='' ",
        " alter table PUBLISHED_STEP_RESULT alter column DESCRIPTION drop default ",
      };
      PersistanceUtils.executeDDLs(st, update);

      log.debug("Copying descriptions from logs ");
      // NOTE: vimeshev - 2006-11-06 - set values in all
      // rows according to the value stored in the schedule
      // configuration field.
      final PreparedStatement stSelectBuildRunActions = conn.prepareStatement("select distinct BUILD_RUN_ID, ACTION_DATE, DESCRIPTION from BUILD_RUN_ACTION");
      final PreparedStatement stUpdateScheduleItem = conn.prepareStatement("update PUBLISHED_STEP_RESULT set DESCRIPTION = ? where BUILD_RUN_ID = ? and PUBLISH_DATE = ?");

      final ResultSet rsSelectBuildConfigs = stSelectBuildRunActions.executeQuery(); // NOPMD
      while(rsSelectBuildConfigs.next()) {
        // have build config id
        final int buildRunID = rsSelectBuildConfigs.getInt(1);
        final Date actionDate = rsSelectBuildConfigs.getDate(2);
        final String description = rsSelectBuildConfigs.getString(3);
        // update the PUBLISHED_STEP_RESULT table
        stUpdateScheduleItem.setString(1, description);
        stUpdateScheduleItem.setInt(2, buildRunID);
        stUpdateScheduleItem.setDate(3, actionDate);
        stUpdateScheduleItem.executeUpdate();
      }
      IoUtils.closeHard(rsSelectBuildConfigs);
      IoUtils.closeHard(stUpdateScheduleItem);
      IoUtils.closeHard(stSelectBuildRunActions);


      log.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();
    } finally {
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  public int upgraderVersion() {
    return 34;
  }
}
