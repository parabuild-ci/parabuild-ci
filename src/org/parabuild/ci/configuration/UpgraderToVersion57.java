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

import org.parabuild.ci.util.IoUtils;

/**
 * Upgrades to version 57.
 */
final class UpgraderToVersion57 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion57.class);


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

        " alter table BUILD_SEQUENCE add column TYPE tinyint default 0 not null ",
        " update BUILD_SEQUENCE set TYPE=0 ",
        " alter table BUILD_SEQUENCE alter column TYPE drop default ",

        "drop index BUILD_SEQUENCE_IX1",
        "drop index BUILD_SEQUENCE_IX2",

        "create index BUILD_SEQUENCE_IX1 on BUILD_SEQUENCE(BUILD_ID, TYPE, NAME)",
        "create index BUILD_SEQUENCE_IX2 on BUILD_SEQUENCE(BUILD_ID, TYPE, LINE_NUMBER)",
        "create index BUILD_SEQUENCE_IX3 on BUILD_SEQUENCE(BUILD_ID, TYPE, DISABLED)",
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
    return 57;
  }
}
