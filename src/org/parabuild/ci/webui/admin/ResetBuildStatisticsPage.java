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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.object.*;
import org.parabuild.ci.statistics.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This page is repsonsible for resetting build statistics
 * caches.
 */
public final class ResetBuildStatisticsPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = 1290119674488931729L; // NOPMD


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {
    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.ADMIN_RESET_BUILD_STATS_CACHES, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // check if build exists, show error message if not
    final BuildConfig buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (buildConfig != null) {
      setTitle(makeTitle("Resetting statistics for build \"" + buildConfig.getBuildName() + '\"'));
      final StatisticsManager sm = StatisticsManagerFactory.getStatisticsManager(buildConfig.getBuildID());
      sm.initStatistics();
      super.baseContentPanel().showInfoMessage("Statistics was reset.");
      super.baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILD_COMMANDS_LIST, buildConfig.getBuildID()));
      return Result.Done();
    } else {
      return WebuiUtils.showBuildNotFound(this);
    }
  }
}
