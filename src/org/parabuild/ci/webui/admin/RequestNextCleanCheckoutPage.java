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
import org.parabuild.ci.services.BuildService;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 *
 */
public final class RequestNextCleanCheckoutPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -4958811997344302265L; // NOPMD


  /**
   * Constructor
   */
  public RequestNextCleanCheckoutPage() {
    super.setTitle(makeTitle("Requesting clean checkout for next build run"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.ADMIN_STOP_BUILD, params);
    }

    // check if build exists, show error message if not
    final BuildConfig buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (buildConfig == null) {
      return WebuiUtils.showBuildNotFound(this);
    } else {
      try {
        // authorise
        if (!super.getUserRights(buildConfig.getActiveBuildID()).isAllowedToStartBuild()) {
          return WebuiUtils.showNotAuthorized(this);
        }
        // set title
        super.setTitle(makeTitle("Requesting to perform clean checkout for the next build run for build \"" + buildConfig.getBuildName() + "\"."));
        // request clean checkout
        final BuildService build = ServiceManager.getInstance().getBuildListService().getBuild(buildConfig.getActiveBuildID());
        // show message
        build.requestCleanCheckout();
        baseContentPanel().getUserPanel().clear();
        baseContentPanel().getUserPanel().add(new Flow()
          .add(new BoldCommonLabel("Request to perform clean checkout for the next build run for build \"" + buildConfig.getBuildName() + "\" has been sent. "))
          .add(WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILD_COMMANDS_LIST, buildConfig.getActiveBuildID())));
        return Result.Done();
      } catch (final Exception e) {
        // Show error
        super.baseContentPanel().showErrorMessage("Unxpected error while stopping the build \"" + buildConfig.getBuildName() + "\": " + StringUtils.toString(e));
      }
    }
    return Result.Done();
  }
}
