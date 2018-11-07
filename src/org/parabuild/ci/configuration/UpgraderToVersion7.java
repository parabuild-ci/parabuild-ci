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
import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.common.*;

/**
 * Upgrades to version 7
 */
final class UpgraderToVersion7 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion7.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    PreparedStatement psUpdateBuildRunParticipant = null; // NOPMD
    PreparedStatement psFindFirstBuildRun = null; // NOPMD
    PreparedStatement psGetBuildRun = null; // NOPMD
    ResultSet rs = null; // NOPMD
    final HashMap buildNumberLookup = new HashMap(1111);
    try {
      conn.setAutoCommit(false);
      final String[] ddlsPre = {
        "alter table BUILD_RUN_PARTICIPANT add column FIRST_BUILD_RUN_ID integer default -1 not null",
        "alter table BUILD_RUN_PARTICIPANT add column FIRST_BUILD_RUN_NUMBER integer default -1 not null "
      };

      final String[] ddlAfter = {
        "alter table BUILD_RUN_PARTICIPANT alter column FIRST_BUILD_RUN_ID drop default",
        "alter table BUILD_RUN_PARTICIPANT alter column FIRST_BUILD_RUN_NUMBER drop default",
        "alter table BUILD_RUN_PARTICIPANT add constraint BUILD_RUN_PARTICIPANT_FC3 foreign key (FIRST_BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE",
        "create index BUILD_RUN_PARTICIPANT_FK3 on BUILD_RUN_PARTICIPANT(FIRST_BUILD_RUN_ID)"
      };

      log.debug("Alter structure");
      st = conn.createStatement();
      PersistanceUtils.executeDDLs(st, ddlsPre);

      log.debug("Set fields");

      // PS to update BUILD_RUN_PARTICIPANT
      psUpdateBuildRunParticipant = conn.prepareStatement(" update BUILD_RUN_PARTICIPANT " +
        "   set FIRST_BUILD_RUN_ID = ?, " +
        "       FIRST_BUILD_RUN_NUMBER = ? " +
        "   where ID = ? ");

      psFindFirstBuildRun = conn.prepareStatement("select min(BR.ID) " +
        "  from BUILD_RUN BR, BUILD_RUN_PARTICIPANT BRP " +
        "  where BR.ID = BRP.BUILD_RUN_ID " +
        "    and BR.BUILD_ID = ? " +
        "    and BRP.CHANGELIST_ID = ? ");

      psGetBuildRun = conn.prepareStatement("select NUMBER " +
        "  from BUILD_RUN BR " +
        "  where BR.ID = ? ");

      // ST to fetch data to update
      rs = st.executeQuery(" select BRP.ID, BRP.CHANGELIST_ID, BR.BUILD_ID, BRP.BUILD_RUN_ID " +
        "   from BUILD_RUN_PARTICIPANT BRP, BUILD_RUN BR " +
        "   where BRP.BUILD_RUN_ID = BR.ID ");

      // traverse participants
      while (rs.next()) {
        final int brpID = rs.getInt(1);
        final int brpChangeListID = rs.getInt(2);
        final int brpBuildID = rs.getInt(3);
        final int brpBuildRunID = rs.getInt(4);

        // find first build run ID
        int firstBuildRunID = -1;
        psFindFirstBuildRun.setInt(1, brpBuildID);
        psFindFirstBuildRun.setInt(2, brpChangeListID);
        final ResultSet rsFoundFirstRun = psFindFirstBuildRun.executeQuery(); // NOPMD
        if (rsFoundFirstRun.next()) {
          firstBuildRunID = rsFoundFirstRun.getInt(1);
        } else {
          firstBuildRunID = brpBuildRunID;
        }
        IoUtils.closeHard(rsFoundFirstRun);

        // get first build run number
        final Integer lookupKey = new Integer(firstBuildRunID);
        Integer firstBuildRunNumber = (Integer)buildNumberLookup.get(lookupKey);
        if (firstBuildRunNumber == null) {
          psGetBuildRun.setInt(1, firstBuildRunID);
          final ResultSet rsGetBuildRun = psGetBuildRun.executeQuery(); // NOPMD
          rsGetBuildRun.next();
          firstBuildRunNumber = new Integer(rsGetBuildRun.getInt(1));
          buildNumberLookup.put(lookupKey, firstBuildRunNumber);
          IoUtils.closeHard(rsGetBuildRun);
        }


        psUpdateBuildRunParticipant.setInt(1, firstBuildRunID);
        psUpdateBuildRunParticipant.setInt(2, firstBuildRunNumber);
        psUpdateBuildRunParticipant.setInt(3, brpID);
        psUpdateBuildRunParticipant.executeUpdate();
      }
      IoUtils.closeHard(rs);

      // final ddls
      PersistanceUtils.executeDDLs(st, ddlAfter);

      // update version
      log.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");
      IoUtils.closeHard(st);

      // finish
      conn.commit();
    } finally {
      IoUtils.closeHard(rs);
      IoUtils.closeHard(psUpdateBuildRunParticipant);
      IoUtils.closeHard(psFindFirstBuildRun);
      IoUtils.closeHard(psGetBuildRun);
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  public int upgraderVersion() {
    return 7;
  }
}