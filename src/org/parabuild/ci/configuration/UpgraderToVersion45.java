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
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.statistics.Averager;
import org.parabuild.ci.statistics.MovingAverager;

/**
 * This upgrader calculates time to fix.
 */
final class UpgraderToVersion45 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion45.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    PreparedStatement psSelectActiveBuilds = null;
    PreparedStatement psSelectBuildRuns = null;
    PreparedStatement psInsertBuildRunAttr = null;
    PreparedStatement psInserAverageTimeToFix = null;
    try {
      // create statement
      conn.setAutoCommit(true);
      st = conn.createStatement();

      log.debug("Preparing selectors");
      psSelectActiveBuilds = conn.prepareStatement(" select ID from ACTIVE_BUILD");

      psSelectBuildRuns = conn.prepareStatement(" select ID, RESULT, STARTED_AT, FINISHED_AT from BUILD_RUN" +
        "  where ACTIVE_BUILD_ID = ?" +
        "  order by ID ");

      psInsertBuildRunAttr = conn.prepareStatement("insert into BUILD_RUN_ATTRIBUTE (BUILD_RUN_ID, NAME, VALUE, TIMESTAMP) " +
        "values (?, ?, ?, 0)");

      psInserAverageTimeToFix = conn.prepareStatement("insert into ACTIVE_BUILD_ATTRIBUTE (ACTIVE_BUILD_ID, NAME, VALUE, TIMESTAMP) " +
        "values (?, ?, ?, 0)");

      // calculate time to fix
      final ResultSet rsActiveBuilds = psSelectActiveBuilds.executeQuery(); // NOPMD CloseResource
      while (rsActiveBuilds.next()) {

        final Averager averager = new Averager();
        final MovingAverager movingAverager = new MovingAverager();

        final int activeBuildID = rsActiveBuilds.getInt(1);
        psSelectBuildRuns.setInt(1, activeBuildID);
        final ResultSet rsBuildRuns = psSelectBuildRuns.executeQuery(); // NOPMD CloseResource

        // state machine variables
        int firstBrokenBuildRunID = BuildRun.UNSAVED_ID;
        Timestamp firstBrokenTime = null;

        //
        while (rsBuildRuns.next()) {
          final int currentBuildRunID = rsBuildRuns.getInt(1);
          final byte currentBuildRunResult = rsBuildRuns.getByte(2);
          if (currentBuildRunResult != BuildRun.BUILD_RESULT_BROKEN && currentBuildRunResult != BuildRun.BUILD_RESULT_SUCCESS) {
            // NOTE: simeshev@parabuilci.org - 2007-05-18 -
            // reset because any result that is not broken
            // or successful will screw up the timing
            firstBrokenBuildRunID = BuildRun.UNSAVED_ID;
            firstBrokenTime = null;
            continue;
          }

//          psGetLastChangeList.setInt(1, currentBuildRunID);
//          final ResultSet rsLastChangeList = psGetLastChangeList.executeQuery(); // NOPMD CloseResource
          final Timestamp startedAt = rsBuildRuns.getTimestamp(3);
          final Timestamp finishedAt = rsBuildRuns.getTimestamp(4);
          if (startedAt == null || finishedAt == null) continue;

          if (firstBrokenTime != null) {
            // previous was broken
            if (currentBuildRunResult == BuildRun.BUILD_RESULT_SUCCESS) {
              // now fixed, record time to fix

              // found
              final long timeToFixSecs = (startedAt.getTime() - firstBrokenTime.getTime()) / 1000L;
              if (timeToFixSecs > 0) {
//                  log.debug("firstBrokenBuildRunAt: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(firstBrokenChangeList) + ", fixed: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentBuildRunChangeList) + ", TTF: " + StringUtils.durationToString(timeToFixSecs, false));
                // store time to fix
                psInsertBuildRunAttr.setInt(1, currentBuildRunID);
                psInsertBuildRunAttr.setString(2, BuildRunAttribute.ATTR_TIME_TO_FIX);
                psInsertBuildRunAttr.setString(3, Long.toString(timeToFixSecs));
                psInsertBuildRunAttr.execute();

                // store moving average time to fix
                final long movingAverage = movingAverager.add(timeToFixSecs);
                psInsertBuildRunAttr.setInt(1, currentBuildRunID);
                psInsertBuildRunAttr.setString(2, BuildRunAttribute.ATTR_TIME_TO_FIX_MOVING_AVERAGE);
                psInsertBuildRunAttr.setString(3, Long.toString(movingAverage));
                psInsertBuildRunAttr.execute();

                // store the build ID we fixed
                psInsertBuildRunAttr.setInt(1, currentBuildRunID);
                psInsertBuildRunAttr.setString(2, BuildRunAttribute.ATTR_FIXES_BUILD_ID);
                psInsertBuildRunAttr.setString(3, Long.toString(firstBrokenBuildRunID));
                psInsertBuildRunAttr.execute();

                // calculate average time to fix
                averager.add(timeToFixSecs);
              }
              // reset
              firstBrokenBuildRunID = BuildRun.UNSAVED_ID;
              firstBrokenTime = null;
            }
          } else {
            // previous clean
            if (currentBuildRunResult != BuildRun.BUILD_RESULT_SUCCESS) {
              // this is the first broken build
              firstBrokenTime = finishedAt;
              firstBrokenBuildRunID = currentBuildRunID;
            }
          }
        }
        IoUtils.closeHard(rsBuildRuns);
        // store average time to fix
        psInserAverageTimeToFix.setInt(1, activeBuildID);
        psInserAverageTimeToFix.setString(2, ActiveBuildAttribute.STAT_AVERAGE_TIME_TO_FIX);
        psInserAverageTimeToFix.setString(3, Long.toString(averager.getAverage()));
        psInserAverageTimeToFix.execute();
      }
      IoUtils.closeHard(rsActiveBuilds);

      log.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();

      // request post-startup config manager action
      System.setProperty(SystemConstants.SYSTEM_PROPERTY_INIT_ADVANCED_SETTINGS, "true");
    } finally {
      IoUtils.closeHard(st);
      IoUtils.closeHard(psInserAverageTimeToFix);
      IoUtils.closeHard(psInsertBuildRunAttr);
      IoUtils.closeHard(psSelectActiveBuilds);
      IoUtils.closeHard(psSelectBuildRuns);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  public int upgraderVersion() {
    return 45;
  }
}
