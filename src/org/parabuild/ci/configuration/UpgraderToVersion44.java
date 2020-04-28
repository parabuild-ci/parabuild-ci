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
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.StepRunAttribute;
import org.parabuild.ci.util.IoUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This upgrader just adds step statistics on PMD to build run statistics on PMD.
 */
final class UpgraderToVersion44 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion44.class);


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

      log.debug("Preparing selector");
      final PreparedStatement psSelect = conn.prepareStatement(" select SR.BUILD_RUN_ID, sum(convert(SRA.VALUE, integer)) " +
        "   from STEP_RUN SR, STEP_RUN_ATTRIBUTE SRA " +
        "   where SRA.NAME = ? and SR.ID = SRA.STEP_RUN_ID" +
        "   group by SR.BUILD_RUN_ID");

      log.debug("Preparing inserter");
      final PreparedStatement psInsert = conn.prepareStatement("insert into BUILD_RUN_ATTRIBUTE (BUILD_RUN_ID, NAME, VALUE, TIMESTAMP) values (?, ?, ?, 1)");

      log.debug("Updating");
      psSelect.setString(1, StepRunAttribute.ATTR_PMD_PROBLEMS);
      final ResultSet rs = psSelect.executeQuery(); // NOPMD CloseResource
      while (rs.next()) {
        final int buildRunID = rs.getInt(1);
        final int pmdErrors = rs.getInt(2);
        if (log.isDebugEnabled()) log.debug("buildRunID: " + buildRunID + ", pmdErrors: " + pmdErrors);
        psInsert.setInt(1, buildRunID);
        psInsert.setString(2, BuildRunAttribute.ATTR_PMD_PROBLEMS);
        psInsert.setInt(3, pmdErrors);
        psInsert.executeUpdate();
      }

      IoUtils.closeHard(rs);
      IoUtils.closeHard(psInsert);
      IoUtils.closeHard(psSelect);

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
    return 44;
  }
}
