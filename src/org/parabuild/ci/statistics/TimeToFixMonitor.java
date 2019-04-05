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
package org.parabuild.ci.statistics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.services.BuildFinishedEvent;
import org.parabuild.ci.services.BuildFinishedSubscriber;

import java.util.Date;
import java.util.Iterator;
import java.util.SortedMap;

/**
 * Watches build for failures and adjusts time to fix accordingly.
 */
public class TimeToFixMonitor implements BuildFinishedSubscriber {

  private static final Log log = LogFactory.getLog(TimeToFixMonitor.class);

  private final ConfigurationManager cm = ConfigurationManager.getInstance();

  private final MovingAverager movingAverager = new MovingAverager();
  private final Averager averager = new Averager();

  private final int activeBuildID;

  // state for moving average
  private int firstBrokenBuildRunID = BuildRun.UNSAVED_ID;
  private Date firstBrokenTime = null;


  public TimeToFixMonitor(final int activeBuildID) {
    // set fields
    this.activeBuildID = ArgumentValidator.validateBuildIDInitialized(activeBuildID);

    // load previous moving average window
    final StatisticsManager sm = StatisticsManagerFactory.getStatisticsManager(activeBuildID);
    final SortedMap movingAverages = sm.getRecentTimeToFixMovingAverage(movingAverager.getWindowSize());
    for (final Iterator i = movingAverages.values().iterator(); i.hasNext();) {
      movingAverager.add((Integer) i.next());
    }

    // load previous average
    final Integer average = getAverageTimeToFix(activeBuildID);
    if (average != null) {
      averager.add(average);
    }
  }


  public void buildFinished(final BuildFinishedEvent event) {
    final int currentBuildRunID = event.getBuildRunID();
    if (log.isDebugEnabled()) log.debug("event: " + event);
    if (log.isDebugEnabled()) log.debug("activeBuildID: " + activeBuildID);

    final BuildRun buildRun = cm.getBuildRun(currentBuildRunID);
    if (buildRun.getStartedAt() == null || buildRun.getFinishedAt() == null) return;

    if (event.getBuildResultCode() == BuildRun.BUILD_RESULT_SUCCESS) {
      if (firstBrokenTime != null) {
        // fixed

        // calculate and store time to fix
        final long timeToFixSecs = (buildRun.getStartedAt().getTime() - firstBrokenTime.getTime()) / 1000L;
        if (timeToFixSecs > 0) {
          if (log.isDebugEnabled()) log.debug("timeToFixSecs: " + timeToFixSecs);
          cm.saveObject(new BuildRunAttribute(currentBuildRunID, BuildRunAttribute.ATTR_TIME_TO_FIX, timeToFixSecs));

          // calculate and store MA
          final long movingAverage = movingAverager.add(timeToFixSecs);
          if (log.isDebugEnabled()) log.debug("movingAverage: " + movingAverage);
          cm.saveObject(new BuildRunAttribute(currentBuildRunID, BuildRunAttribute.ATTR_TIME_TO_FIX_MOVING_AVERAGE, movingAverage));

          // store fixed build ID
          cm.saveObject(new BuildRunAttribute(currentBuildRunID, BuildRunAttribute.ATTR_FIXES_BUILD_ID, firstBrokenBuildRunID));

          // calculate and store average
          final double average = averager.add(timeToFixSecs);
          if (log.isDebugEnabled()) log.debug("average: " + average);
          final Integer storedAverage = getAverageTimeToFix(activeBuildID);
          if (storedAverage == null) {
            // create
            cm.saveObject(new ActiveBuildAttribute(activeBuildID, ActiveBuildAttribute.STAT_AVERAGE_TIME_TO_FIX, (long) average));
          } else {
            // update
            final ActiveBuildAttribute activeBuildAttribute = cm.getActiveBuildAttribute(activeBuildID, ActiveBuildAttribute.STAT_AVERAGE_TIME_TO_FIX);
            activeBuildAttribute.setPropertyValue((long) average);
            cm.saveObject(activeBuildAttribute);
          }
        }

        // reset calculations
        firstBrokenBuildRunID = BuildRun.UNSAVED_ID;
        firstBrokenTime = null;
      }
    } else {
      // current one is not successful
      if (event.getBuildResultCode() == BuildRun.BUILD_RESULT_BROKEN) {
        // broken

        if (firstBrokenTime == null) {
          // first time broken
          firstBrokenBuildRunID = currentBuildRunID;
          firstBrokenTime = buildRun.getFinishedAt();
        }
      } else {
        // anything else, reset
        firstBrokenBuildRunID = BuildRun.UNSAVED_ID;
        firstBrokenTime = null;
      }
    }
  }


  /**
   * Helper method.
   */
  private Integer getAverageTimeToFix(final int activeBuildID) {
    return cm.getActiveBuildAttributeValue(activeBuildID, ActiveBuildAttribute.STAT_AVERAGE_TIME_TO_FIX, (Integer) null);
  }
}
