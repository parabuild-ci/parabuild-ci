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

import java.util.*;

import org.parabuild.ci.util.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.statistics.*;
import viewtier.ui.*;

/**
 * Panel to display cumulative up-to-date build statistics.
 */
public final class MonthToDateStatisticsPanel extends MessagePanel {

  public static final String GREEN_DOT_IMAGE_URL = StatisticsPercentBarImage.GREEN_DOT_IMAGE_URL;
  public static final String RED_DOT_IMAGE_URL = StatisticsPercentBarImage.RED_DOT_IMAGE_URL;
  private static final long serialVersionUID = -4810626415714290792L;


  /**
   * Constructor.
   *
   * @param buildID build id to show.
   */
  public MonthToDateStatisticsPanel(final int buildID) {
    setBuildID(buildID);
    setWidth(Pages.PAGE_WIDTH);
  }


  private void setBuildID(final int buildID) {
    final String dateFormat = SystemConfigurationManagerFactory.getManager().getDateFormat();
    // get panel
    final Panel up = super.getUserPanel();
    up.clear();

    // get statistics
    final StatisticsManager stm = StatisticsManagerFactory.getStatisticsManager(buildID);
    final SortedMap mtds = stm.getMonthToDateBuildStatistics();

    // clculate maximum
    int max = 0;
    final Collection collection = mtds.values();
    for (final Iterator iter = collection.iterator(); iter.hasNext();) {
      final BuildStatistics bst = (BuildStatistics)iter.next();
      max = Math.max(max, bst.getTotalBuilds());
    }

    // show statistics
    int i = 0;
    for (final Iterator iter = mtds.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry entry = (Map.Entry)iter.next();
      final Date date = (Date)entry.getKey();
      final BuildStatistics bst = (BuildStatistics)entry.getValue();

      // date
      final StatisticsItemLabel lbDate = new StatisticsItemLabel("  " + StringUtils.formatDate(date, dateFormat) + "  ");
      lbDate.setAlignY(Layout.CENTER);
      lbDate.setBorder(Border.BOTTOM, 1, Pages.TABLE_GRID_COLOR);
      up.add(lbDate, new Layout(0, i << 1, 1, 2));

      // successful
      final Image imgSuccessfulBar = new StatisticsQuantityBarImage(GREEN_DOT_IMAGE_URL, "", max, bst.getSuccessfulBuilds());
      final Flow flSucc = new CommonFlow(imgSuccessfulBar, new StatisticsItemLabel(successfulBuilsToString(bst)));
      imgSuccessfulBar.setPadding(0);
      flSucc.setPadding(0);
      flSucc.setBorder(Border.LEFT, 1, Pages.TABLE_GRID_COLOR);
      up.add(flSucc, new Layout(1, i << 1, 1, 1));

      // failed
      final Image imgFailedBar = new StatisticsQuantityBarImage(RED_DOT_IMAGE_URL, "", max, bst.getFailedBuilds());
      final Flow flFail = new CommonFlow(imgFailedBar, new StatisticsItemLabel(failedBuildstoString(bst)));
      imgFailedBar.setPadding(0);
      flFail.setPadding(0);
      flFail.setBorder(Border.LEFT, 1, Pages.TABLE_GRID_COLOR);
      up.add(flFail, new Layout(1, (i << 1) + 1, 1, 1));

      i++;
    }
  }


  private static String failedBuildstoString(final BuildStatistics bst) {
    if (bst.getFailedBuilds() == 0) return "";
    return " " + bst.getFailedBuilds();
  }


  private static String successfulBuilsToString(final BuildStatistics bst) {
    if (bst.getSuccessfulBuilds() == 0) return "";
    return " " + bst.getSuccessfulBuilds();
  }
}
