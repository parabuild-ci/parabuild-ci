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

import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page is responsible for editing Parabuild system
 * properties.
 */
public final class ActivateBuildPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -5872091536862586702L; // NOPMD


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.ADMIN_ACTIVATE_BUILD, params);
    }

    // check if build exists, show error message if not
    final BuildConfig buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (buildConfig == null) {
      return WebuiUtils.showBuildNotFound(this);
    }

    // authorise
    if (!getUserRigths(buildConfig.getActiveBuildID()).isAllowedToActivateBuild()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // get active
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ActiveBuild activeBuild = cm.getActiveBuild(buildConfig.getBuildID());
    if (activeBuild == null) {
      return WebuiUtils.showBuildNotFound(this);
    }

    // process
    setTitle(makeTitle("Activating Build \"" + buildConfig.getBuildName() + '\"'));
    // check if the build is inactive
    if (activeBuild.getStartupStatus() == BuildStatus.INACTIVE_VALUE) {
      // activate build
      BuildManager.getInstance().activateBuild(buildConfig.getBuildID());
      return WebuiUtils.createBuildActionReturnResult(getTierletContext());
    } else if (activeBuild.getStartupStatus() == BuildStatus.PAUSED_VALUE) {
      BuildManager.getInstance().resumeBuild(buildConfig.getBuildID());
      return WebuiUtils.createBuildActionReturnResult(getTierletContext());
    } else {
      // show notice that build can not be activated
      super.baseContentPanel().getUserPanel().clear();
      super.baseContentPanel().getUserPanel().add(new Flow()
              .add(new BoldCommonLabel("Build \"" + buildConfig.getBuildName() + "\" has already been activated. "))
              .add(WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILDS)));
      return Result.Done();
    }
  }
}
