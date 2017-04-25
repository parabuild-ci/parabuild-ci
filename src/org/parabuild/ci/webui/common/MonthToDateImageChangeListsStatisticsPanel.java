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

import org.parabuild.ci.object.*;
import org.parabuild.ci.statistics.*;
import org.parabuild.ci.webui.*;
import viewtier.ui.*;

/**
 * Panel to display cumulative month-to-date change lists
 * statistics.
 */
public final class MonthToDateImageChangeListsStatisticsPanel extends MessagePanel {

  /**
   * Constructor.
   */
  public MonthToDateImageChangeListsStatisticsPanel(final ActiveBuildConfig buildConfig) {
    setTitle("Month To Date Change Statistics for  " + buildConfig.getBuildName());
    setStyle(PAGE_SECTION);
    getUserPanel().add(new Image(WebuiUtils.makeStatisticsChartURL(buildConfig.getBuildID(),
      StatisticsImageServlet.STATISTICS_CHANGE_LISTS_IMAGE_MONTH_TO_DATE),
      "Month to date statistics",
      StatisticsUtils.IMG_WIDTH,
      StatisticsUtils.IMG_HEIGHT));
    setWidth(Pages.PAGE_WIDTH);
  }
}
