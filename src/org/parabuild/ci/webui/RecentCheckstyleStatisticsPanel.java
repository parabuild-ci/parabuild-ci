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
package org.parabuild.ci.webui;

import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.statistics.StatisticsUtils;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Image;

/**
 * Panel to display recent Checkstyle violations.
 */
public final class RecentCheckstyleStatisticsPanel extends MessagePanel {

  private static final long serialVersionUID = -6540063662660515069L;


  /**
   * Constructor.
   */
  public RecentCheckstyleStatisticsPanel(final ActiveBuildConfig bc) {
    setTitle("Recent Checkstyle Errors for " + bc.getBuildName());
    setStyle(PAGE_SECTION);
    getUserPanel().add(new Image(WebuiUtils.makeStatisticsChartURL(bc.getBuildID(),
      StatisticsImageServlet.STATISTICS_CHECKSTYLE_IMAGE_RECENT_BUILDS),
      "Recent Checkstyle Erors",
      StatisticsUtils.IMG_WIDTH,
      StatisticsUtils.IMG_HEIGHT));
    setWidth(Pages.PAGE_WIDTH);
  }
}
