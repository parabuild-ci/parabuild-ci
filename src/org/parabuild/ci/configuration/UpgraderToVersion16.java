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
 * Upgrades to version 16. Adds permissions to group object.
 */
final class UpgraderToVersion16 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion16.class);


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
      final String [] update = {
        " alter table GROUPS add column ALLOWED_TO_CREATE_BUILD char(1) default 'N' not null ",
        " alter table GROUPS add column ALLOWED_TO_DELETE_BUILD char(1) default 'N' not null ",
        " alter table GROUPS add column ALLOWED_TO_START_BUILD char(1) default 'N' not null ",
        " alter table GROUPS add column ALLOWED_TO_STOP_BUILD char(1) default 'N' not null ",
        " alter table GROUPS add column ALLOWED_TO_UPDATE_BUILD char(1) default 'N' not null ",
        " alter table GROUPS add column ALLOWED_TO_VIEW_BUILD char(1) default 'Y' not null ",
        " update GROUPS set ALLOWED_TO_CREATE_BUILD='N', ALLOWED_TO_DELETE_BUILD='N', ALLOWED_TO_START_BUILD='N', ALLOWED_TO_STOP_BUILD='N', ALLOWED_TO_UPDATE_BUILD='N', ALLOWED_TO_VIEW_BUILD='Y' ",
        " alter table GROUPS alter column ALLOWED_TO_CREATE_BUILD drop default ",
        " alter table GROUPS alter column ALLOWED_TO_DELETE_BUILD drop default ",
        " alter table GROUPS alter column ALLOWED_TO_START_BUILD drop default ",
        " alter table GROUPS alter column ALLOWED_TO_STOP_BUILD drop default ",
        " alter table GROUPS alter column ALLOWED_TO_UPDATE_BUILD drop default " ,
        " alter table GROUPS alter column ALLOWED_TO_VIEW_BUILD drop default "};
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


  public int upgraderVersion() {
    return 16;
  }
}