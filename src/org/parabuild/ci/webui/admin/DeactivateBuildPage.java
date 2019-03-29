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

import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page is repsonsible for pausing Parabuild.
 */
public final class DeactivateBuildPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = 1290119674488931729L; // NOPMD


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {
    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_DEACTIVATE_BUILD, params);
    }

    // check if build exists, show error message if not
    final BuildConfig buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (buildConfig != null) {
      // authorise
      if (!super.getUserRights(buildConfig.getActiveBuildID()).isAllowedToDeactivateBuild()) {
        return WebuiUtils.showNotAuthorized(this);
      }
      setTitle(makeTitle("Deactivating build \"" + buildConfig.getBuildName() + '\"'));
      BuildManager.getInstance().deactivateBuild(buildConfig.getBuildID(), getUserID());
      return WebuiUtils.createBuildActionReturnResult(getTierletContext());
    } else {
      return WebuiUtils.showBuildNotFound(this);
    }
  }
}
