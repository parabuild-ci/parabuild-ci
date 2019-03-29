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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page is repsonsible for editing Parabuild system
 * properties.
 */
public final class StopBuildPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -4958811997344302265L; // NOPMD
  private static final String CAPTION_STOPPING_THE_BUILD = "Stopping the build";


  /**
   * Constructor
   */
  public StopBuildPage() {
    super.setTitle(makeTitle(CAPTION_STOPPING_THE_BUILD));
  }


  /**
   * Lifecycle callback
   */
  protected Result executePage(final Parameters parameters) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_STOP_BUILD, parameters);
    }

    // check if build exists, show error message if not
    final BuildConfig buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(parameters);
    if (buildConfig == null) {
      return WebuiUtils.showBuildNotFound(this);
    }
    try {
      // authorise
      if (!super.getUserRights(buildConfig.getActiveBuildID()).isAllowedToStopBuild()) {
        return WebuiUtils.showNotAuthorized(this);
      }
      // start build
      super.setTitle(makeTitle("Stopping the build \"" + buildConfig.getBuildName() + '\"'));
      final int buildID = buildConfig.getBuildID();
      final int userID = SecurityManager.getInstance().getUserIDFromContext(getTierletContext());
      BuildManager.getInstance().stopBuild(buildID, userID);
      return WebuiUtils.createBuildActionReturnResult(getTierletContext());
    } catch (final Exception e) {
      // Show error
      super.baseContentPanel().showErrorMessage("Unxpected error while stopping the build \"" + buildConfig.getBuildName() + "\": " + StringUtils.toString(e));
    }
    return Result.Done();
  }


  public String toString() {
    return "StopBuildPage{}";
  }
}
