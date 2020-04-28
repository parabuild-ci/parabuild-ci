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
import viewtier.ui.Panel;

/**
 * Panel to display recent build times
 */
public final class RecentBuildTimesImageStatisticsPanel extends MessagePanel {

  private static final long serialVersionUID = -8883098200696126467L;


  public RecentBuildTimesImageStatisticsPanel() {
    setTitle("Recent Build Time");
  }


  /**
   * Constructor.
   */
  public RecentBuildTimesImageStatisticsPanel(final ActiveBuildConfig buildConfig) {
    setBuildConfig(buildConfig);
  }


  public void setBuildConfig(final ActiveBuildConfig buildConfig) {
    setTitle("Recent Build Time for  " + buildConfig.getBuildName());
    setStyle(PAGE_SECTION);
    final Panel userPanel = getUserPanel();
    userPanel.clear();
    userPanel.add(new Image(WebuiUtils.makeStatisticsChartURL(buildConfig.getBuildID(),
      StatisticsImageServlet.STATISTICS_RECENT_BUILD_TIMES_IMAGE),
      "Recent build time",
      StatisticsUtils.IMG_WIDTH,
      StatisticsUtils.IMG_HEIGHT));
    setWidth(Pages.PAGE_WIDTH);
  }
}
