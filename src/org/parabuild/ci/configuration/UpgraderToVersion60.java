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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.IoUtils;

/**
 * Upgrades to version 60.
 */
final class UpgraderToVersion60 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion60.class);


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

        " alter table MANUAL_RUN_PARAMETER add column PRESENTATION tinyint default 0 not null ",
        " update MANUAL_RUN_PARAMETER set PRESENTATION=TYPE ",
        " update MANUAL_RUN_PARAMETER set TYPE=0",
        " alter table MANUAL_RUN_PARAMETER alter column PRESENTATION drop default ",

        "alter table MANUAL_RUN_PARAMETER drop constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_UC2",
        "alter table MANUAL_RUN_PARAMETER add constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_UC2 unique (BUILD_ID, TYPE, NAME)",

        "create index MANUAL_RUN_PARAMETER_IX1 on MANUAL_RUN_PARAMETER(BUILD_ID, TYPE)",
      });

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
    return 60;
  }
}
