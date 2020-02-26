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
import org.parabuild.ci.object.*;

/**
 * Upgrades to version 33. Adds manual label field to build run.
 */
final class UpgraderToVersion33 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion33.class);


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
      final String [] update = {" alter table SCHEDULE_ITEM add column RUN_IF_NO_CHANGES char(1) default 'N' not null ",
        " update SCHEDULE_ITEM set RUN_IF_NO_CHANGES='N' ",
        " alter table SCHEDULE_ITEM alter column RUN_IF_NO_CHANGES drop default ",
      };
      PersistanceUtils.executeDDLs(st, update);

      log.debug("Adjusting table settings accoding to the build setting ");
      // NOTE: vimeshev - 2006-11-06 - set values in all
      // rows according to the value stored in the schedule
      // configuration field.
      final PreparedStatement stSelectBuildConfigs = conn.prepareStatement("select ID from BUILD_CONFIG where SCHEDULE = ?");
      final PreparedStatement stSelectScheduleProperty = conn.prepareStatement("select VALUE from SCHEDULE_PROPERTY where BUILD_ID = ? and NAME = ?");
      final PreparedStatement stUpdateScheduleItem = conn.prepareStatement("update SCHEDULE_ITEM set RUN_IF_NO_CHANGES=? where BUILD_ID = ?");

      stSelectBuildConfigs.setInt(1, BuildConfig.SCHEDULE_TYPE_RECURRENT);
      final ResultSet rsSelectBuildConfigs = stSelectBuildConfigs.executeQuery(); // NOPMD
      while(rsSelectBuildConfigs.next()) {
        // have build config id
        final int buildID = rsSelectBuildConfigs.getInt(1);
        // get schedule property value
        stSelectScheduleProperty.setInt(1, buildID);
        stSelectScheduleProperty.setString(2, ScheduleProperty.RUN_IF_NO_CHANGES);
        final ResultSet rsSelectScheduleProperty = stSelectScheduleProperty.executeQuery(); // NOPMD CloseResource
        String runIfNoChanges = null;
        if (rsSelectScheduleProperty.next()) {
          runIfNoChanges = rsSelectScheduleProperty.getString(1);
        } else {
          runIfNoChanges = ScheduleProperty.OPTION_UNCHECKED;
        }
        // update the SCHEDULE_ITEM table
        stUpdateScheduleItem.setString(1, runIfNoChanges.equals(ScheduleProperty.OPTION_CHECKED) ? "Y" : "N");
        stUpdateScheduleItem.setInt(2, buildID);
        stUpdateScheduleItem.executeUpdate();
        IoUtils.closeHard(rsSelectScheduleProperty);
      }
      IoUtils.closeHard(rsSelectBuildConfigs);
      IoUtils.closeHard(stUpdateScheduleItem);
      IoUtils.closeHard(stSelectScheduleProperty);
      IoUtils.closeHard(stSelectBuildConfigs);


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
    return 33;
  }
}
