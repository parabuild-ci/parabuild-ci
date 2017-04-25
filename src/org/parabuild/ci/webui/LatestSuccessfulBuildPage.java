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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Label;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page is repsonsible for forwarding the request to a
 * last clean build or displaying a message that the last
 * clean build is not available.
 */
public final class LatestSuccessfulBuildPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -5872091536862586702L; // NOPMD


  public LatestSuccessfulBuildPage() {
    super(FLAG_SHOW_QUICK_SEARCH | FLAG_SHOW_PAGE_HEADER_LABEL | FLAG_FLOATING_WIDTH);
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // check if build exists, show error message if not
    final BuildConfig buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (buildConfig == null) return WebuiUtils.showBuildNotFound(this);

    // get active
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ActiveBuild activeBuild = cm.getActiveBuild(buildConfig.getBuildID());
    if (activeBuild == null) return WebuiUtils.showBuildNotFound(this);

    // get last clean build
    final BuildRun lastCleanBuildRun = cm.getLastCleanBuildRun(activeBuild.getID());
    if (lastCleanBuildRun != null) {
      final Parameters parameters = new Parameters();
      parameters.addParameter(Pages.PARAM_BUILD_RUN_ID, lastCleanBuildRun.getBuildRunID());
      // redirect to the last build
      return Result.Done(Pages.BUILD_CHANGES, parameters);
    }

    // process case where the build is not found
    final String title = "Last Clean \"" + buildConfig.getBuildName() + '\"';
    setTitle(makeTitle(title));
    setPageHeader(title);
    baseContentPanel().showInfoMessage("There are no clean runs for this build yet.");
    baseContentPanel().getUserPanel().add(new CommonFlow(WebuiUtils.makeBlueBulletSquareImage16x16(), new Label(" "),
      new CommonLink("Current status for " + buildConfig.getBuildName(), Pages.PUBLIC_BUILDS,
        Pages.PARAM_STATUS_VIEW, Pages.STATUS_VIEW_DETAILED,
        Pages.PARAM_BUILD_ID, buildConfig.getActiveBuildID())));

    // return
    return Result.Done();
  }
}
