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

import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.statistics.StatisticsUtils;
import org.parabuild.ci.webui.StatisticsImageServlet;
import viewtier.ui.Image;

/**
 * Panel to display cumulative day of week build breakage
 * distribution.
 */
public final class DayOfWeekBuildBreakageDistributionPanel extends MessagePanel {

  private static final long serialVersionUID = -3065782878534710887L;


  /**
   * Constructor.
   */
  public DayOfWeekBuildBreakageDistributionPanel(final ActiveBuildConfig buildConfig) {
    setTitle("Build Breakage By Day Of Week for " + buildConfig.getBuildName());
    setStyle(PAGE_SECTION);
    final String url = WebuiUtils.makeStatisticsChartURL(buildConfig.getBuildID(),
      StatisticsImageServlet.STATISTICS_DAY_OF_WEEK_BREAKAGE_DISTRIBUTION_TO_DATE);
    super.getUserPanel().add(new Image(url, "Build breakage by day of week",
      StatisticsUtils.IMG_WIDTH,
      StatisticsUtils.IMG_HEIGHT));
    setWidth(Pages.PAGE_WIDTH);
  }
}
