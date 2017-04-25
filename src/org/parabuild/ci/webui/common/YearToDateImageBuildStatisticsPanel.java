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
 * Panel to display cumulative year-to-date build statistics.
 * <p/>
 * Year-to-date is presented as a chart with months on X and
 * builds results vertically.
 */
public final class YearToDateImageBuildStatisticsPanel extends MessagePanel {

  /**
   * Constructor.
   */
  public YearToDateImageBuildStatisticsPanel(final ActiveBuildConfig buildConfig) {
    setTitle("Year To Date Statistics for  " + buildConfig.getBuildName());
    setStyle(PAGE_SECTION);
    final String url = WebuiUtils.makeStatisticsChartURL(buildConfig.getBuildID(),
      StatisticsImageServlet.STATISTICS_BUILD_IMAGE_YEAR_TO_DATE);
    super.getUserPanel().add(new Image(url, "Year to date statistics",
      StatisticsUtils.IMG_WIDTH,
      StatisticsUtils.IMG_HEIGHT));
    setWidth(Pages.PAGE_WIDTH);
  }
}
