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
import org.parabuild.ci.object.*;

/**
 * Upgrades to version 39. Adds manual label field to build run.
 */
final class UpgraderToVersion39 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion39.class);


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
      PersistanceUtils.executeDDLs(st, new String[]{" alter table ACTIVE_BUILD add column SEQUENCE_NUMBER integer default 0 not null ",
        " insert into SYSTEM_PROPERTY (NAME, VALUE, TIMESTAMP) values ('" + SystemProperty.BUILD_SEQUENCE_NUMBER + "', '0', 0) "
      });

      log.debug("Populating  ");
      final BuildSequenceNumberIncrementer incrementer = new BuildSequenceNumberIncrementer();
      final PreparedStatement stSelectIDs = conn.prepareStatement("select ID from ACTIVE_BUILD");
      final PreparedStatement stUpdate = conn.prepareStatement("update ACTIVE_BUILD set SEQUENCE_NUMBER = ? where ID = ?");
      final ResultSet rsIDs = stSelectIDs.executeQuery(); // NOPMD
      while (rsIDs.next()) {
        final int activeBuildID = rsIDs.getInt(1);
        final int sequenceNumber = incrementer.incrementBuildSequenceNumber(conn);
        stUpdate.setInt(1, sequenceNumber);
        stUpdate.setInt(2, activeBuildID);
        stUpdate.executeUpdate();
      }
      IoUtils.closeHard(rsIDs);
      IoUtils.closeHard(stUpdate);
      IoUtils.closeHard(stSelectIDs);


      log.debug("Finishing altering table");
      PersistanceUtils.executeDDLs(st, new String[]{" alter table ACTIVE_BUILD alter column SEQUENCE_NUMBER drop default ",
        " alter table ACTIVE_BUILD add constraint ACTIVE_BUILD_UC2 unique (SEQUENCE_NUMBER) "
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
    return 39;
  }
}
