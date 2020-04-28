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
 * Upgrades to version 37. Alters source control properties to
 * hold 4 kilobytes property values.
 */
final class UpgraderToVersion37 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion37.class);


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
        " alter table BUILD_SEQUENCE add column FINALIZER char(1) default 'N' not null ",
        " update BUILD_SEQUENCE set FINALIZER='N' ",
        " alter table BUILD_SEQUENCE alter column FINALIZER drop default ",

        " alter table BUILD_RUN add column DEPENDENCE tinyint default 0 not null ",
        " update BUILD_RUN set DEPENDENCE=0 ",
        " alter table BUILD_RUN alter column DEPENDENCE drop default ",

        " create cached table BUILD_RUN_DEPENDENCE ( " +
          "  ID integer not null identity, " +
          "  LEADER_BUILD_RUN_ID integer not null, " +
          "  DEPENDENT_BUILD_RUN_ID integer not null, " +
          "  constraint BUILD_RUN_DEPENDENCE_UC1 unique (ID), " +
          "  constraint BUILD_RUN_DEPENDENCE_FC1 foreign key (LEADER_BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE, " +
          "  constraint BUILD_RUN_DEPENDENCE_FC2 foreign key (DEPENDENT_BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE " +
          ')',
        "create unique index BUILD_RUN_DEPENDENCE_AK1 on BUILD_RUN_DEPENDENCE(LEADER_BUILD_RUN_ID, DEPENDENT_BUILD_RUN_ID)",
        "create index BUILD_RUN_DEPENDENCE_IX1 on BUILD_RUN_DEPENDENCE(LEADER_BUILD_RUN_ID)",
        "create index BUILD_RUN_DEPENDENCE_IX2 on BUILD_RUN_DEPENDENCE(DEPENDENT_BUILD_RUN_ID)"
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


  public int upgraderVersion() {
    return 37;
  }
}
