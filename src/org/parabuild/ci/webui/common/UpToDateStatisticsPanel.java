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
package org.parabuild.ci.webui.common;

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.statistics.BuildStatistics;
import org.parabuild.ci.statistics.StatisticsManager;
import org.parabuild.ci.statistics.StatisticsManagerFactory;
import viewtier.ui.Flow;
import viewtier.ui.Image;
import viewtier.ui.Panel;

/**
 * Panel to display cumulative up-to-date build statistics.
 */
public final class UpToDateStatisticsPanel extends MessagePanel {

  // captions
  private static final String CAPTION_CHANGE_LISTS_TO_DATE = "Change lists up to date:  ";
  private static final String CAPTION_FAILED_BUILDS_TO_DATE = "Failed build runs up to date:  ";
  private static final String CAPTION_SUCC_BUILDS_TO_DATE = "Successful build runs up to date:  ";
  private static final String CAPTION_TOTAL_BUILDS_TO_DATE = "Total build runs up to date:  ";
  private static final String CAPTION_AVERAGE_TIME_TO_FIX = "Average time to fix:  ";

  public static final int STATS_VALUE_COLUMN_WIDTH = 30;


  /**
   * Constructor.
   */
  public UpToDateStatisticsPanel() {
    setWidth(Pages.PAGE_WIDTH);
  }


  public void setBuildID(final int activeBuildID) {
    // get statistics
    final StatisticsManager stm = StatisticsManagerFactory.getStatisticsManager(activeBuildID);
    final BuildStatistics utds = stm.getUpToDateBuildStatistics();

    setTitle("Overview");

    // create grid iterator
    final Panel up = super.getUserPanel();
    up.clear();
    final GridIterator gi = new GridIterator(up, 3);

    // successful
    final int successfulBuildsPercent = utds.getSuccessfulBuildsPercent();
    final Image imgSuccessfulBar = new StatisticsPercentBarImage(StatisticsPercentBarImage.GREEN_DOT_IMAGE_URL, "", successfulBuildsPercent);
    final StatisticsLabel lbSucc = new StatisticsItemLabel(CAPTION_SUCC_BUILDS_TO_DATE);
    final StatisticsLabel lbSuccValue = new StatisticsLabel(Integer.toString(utds.getSuccessfulBuilds()), STATS_VALUE_COLUMN_WIDTH);
    final StatisticsLabel lbSuccPercent = new StatisticsLabel(' ' + Integer.toString(successfulBuildsPercent) + '%', STATS_VALUE_COLUMN_WIDTH);
    gi.add(lbSucc).add(lbSuccValue).add(new Flow().add(imgSuccessfulBar).add(lbSuccPercent));

    // failed
    final int failedBuildsPercent = utds.getFailedBuildsPercent();
    final Image imgFailedBar = new StatisticsPercentBarImage(StatisticsPercentBarImage.RED_DOT_IMAGE_URL, "", failedBuildsPercent);
    final StatisticsLabel lbFailed = new StatisticsItemLabel(CAPTION_FAILED_BUILDS_TO_DATE);
    final StatisticsLabel lbFailedValue = new StatisticsLabel(Integer.toString(utds.getFailedBuilds()), STATS_VALUE_COLUMN_WIDTH);
    final StatisticsLabel lbFailedPercent = new StatisticsLabel(' ' + Integer.toString(failedBuildsPercent) + '%', STATS_VALUE_COLUMN_WIDTH);
    gi.add(lbFailed).add(lbFailedValue).add(new Flow().add(imgFailedBar).add(lbFailedPercent));

    // total
    final StatisticsLabel lbTotal = new StatisticsItemLabel(CAPTION_TOTAL_BUILDS_TO_DATE);
    final StatisticsLabel lbTotalValue = new StatisticsLabel(Integer.toString(utds.getTotalBuilds()), STATS_VALUE_COLUMN_WIDTH);
    gi.add(lbTotal).add(lbTotalValue).add(new StatisticsLabel());

    // change lists
    final StatisticsLabel lbChangelistsLabel = new StatisticsItemLabel(CAPTION_CHANGE_LISTS_TO_DATE);
    final StatisticsLabel lbChangelistsValue = new StatisticsLabel(Integer.toString(utds.getChangeLists()), STATS_VALUE_COLUMN_WIDTH);
    gi.add(lbChangelistsLabel).add(lbChangelistsValue).add(new StatisticsLabel());

    // total
    final Integer activeBuildAttributeValue = ConfigurationManager.getInstance().getActiveBuildAttributeValue(activeBuildID, ActiveBuildAttribute.STAT_AVERAGE_TIME_TO_FIX, (Integer) null);
    if (activeBuildAttributeValue != null) {
      final StatisticsLabel lbAverageTimeToFix = new StatisticsItemLabel(CAPTION_AVERAGE_TIME_TO_FIX);
      final StatisticsLabel lbAverageTimeToFixValue = new StatisticsLabel(StringUtils.durationToString(activeBuildAttributeValue, false).toString());
      gi.add(lbAverageTimeToFix).add(lbAverageTimeToFixValue, 2);
    }
  }
}
