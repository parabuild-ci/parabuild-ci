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

import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.GridIterator;
import viewtier.ui.Panel;
import viewtier.ui.TierletContext;

import java.text.SimpleDateFormat;
import java.util.List;

final class DashboardImagesPanel extends Panel {

  /**
   * Populates with current build statruses.
   *
   * @param currentBuildsStatuses
   */
  public void populate(final List currentBuildsStatuses) {
    clear();

    final int numberOfBuildsPerRow = dashboardRowSize();
    final int buildCount = currentBuildsStatuses.size();
    if (buildCount == 0) return;

    // calcualte width
    final int widthPercent;
    if (buildCount > numberOfBuildsPerRow) {
      widthPercent = 100 / numberOfBuildsPerRow;
    } else {
      widthPercent = 100 / buildCount;
    }
    final String stringWidthPercent = Integer.toString(widthPercent) + '%';

    // iterate statuses
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    final boolean numberAndChangeListOnDashboard = scm.isShowNumberAndChangeListOnDashboard();
    final SimpleDateFormat dateFormat = new SimpleDateFormat(scm.getDateTimeFormat());
    final GridIterator gi = new GridIterator(this, numberOfBuildsPerRow);
    for (int i = 0; i < buildCount; i++) {
      // add status
      final BuildState buildState = (BuildState)currentBuildsStatuses.get(i);
      final DashboardBuildStatusComponent statusComponent = new DashboardBuildStatusComponent();
      statusComponent.setWidth(stringWidthPercent);
//      statusComponent.setHeight(50);
      statusComponent.setBuildState(buildState);
      statusComponent.setDateFormat(dateFormat);
      statusComponent.setShowDetails(numberAndChangeListOnDashboard);
      gi.add(statusComponent);
    }
  }


  /**
   * @return dashboard row size
   */
  private int dashboardRowSize() {

    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();

    final TierletContext tierletContext = getTierletContext();
    if (tierletContext == null) return scm.getDashboardRowSize();

    // get user
    final SecurityManager sm = SecurityManager.getInstance();
    final User userFromRequest = sm.getUserFromRequest(tierletContext.getHttpServletRequest());
    if (userFromRequest == null) return scm.getDashboardRowSize();

    // get user preference
    final String rowSize = sm.getUserPropertyValue(userFromRequest.getUserID(), UserProperty.DASHBOARD_ROW_SIZE);
    if (!StringUtils.isValidInteger(rowSize)) return scm.getDashboardRowSize();

    return Integer.parseInt(rowSize);
  }
}
