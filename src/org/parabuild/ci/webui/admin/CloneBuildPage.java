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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.BuildConfigCloner;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.security.BuildRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.OrderedList;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page is repsonsible for cloning a build configuration.
 * <p/>
 * This pages accepts parameter defined by Pages.PARAM_BUILD_ID.
 *
 * @see Pages#PARAM_BUILD_ID
 */
public final class CloneBuildPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -4958811997344302265L; // NOPMD
  private static final Log LOG = LogFactory.getLog(CloneBuildPage.class); // NOPMD

  public static final String CAPTION_RESUME = "Resume created build";
  public static final String CAPTION_BUILD_LIST = "Go to build list";
  public static final String CAPTION_EDIT = "Edit created build";


  /**
   * Constructor
   */
  public CloneBuildPage() {
    super.setTitle(makeTitle("Cloning a Build"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters parameters) {

    // Authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_CLONE_BUILD, parameters);
    }

    // Check if build exists, show error message if not
    final BuildConfig buildToClone = ParameterUtils.getActiveBuildConfigFromParameters(parameters);
    final Panel userPanel = baseContentPanel().getUserPanel();
    if (buildToClone == null) {
      return WebuiUtils.showBuildNotFound(this);
    }

    try {

      final BuildRights userRights = getUserRights(buildToClone.getActiveBuildID());
      if (!super.isValidAdminUser() && !userRights.isAllowedToUpdateBuild()) {
        return WebuiUtils.showNotAuthorized(this);
      }

      // Do copy
      final BuildConfigCloner cloner = new BuildConfigCloner();
      final BuildConfig createdBuild = cloner.createActiveBuildConfig(buildToClone.getBuildID());
      if (createdBuild == null) {
        super.baseContentPanel().showErrorMessage("Could not make a copy. Please check admin errors.");
        return Result.Done();
      }

      // Attach creator to the build configuration
      SecurityManager.getInstance().assignBuildCreator(createdBuild.getActiveBuildID(), getUserID());

      // Show success notice
      userPanel.add(new CommonFlow(new CommonLabel("A copy of build configuration for " + buildToClone.getBuildName()
              + " has been successfuly created. The status of the new build is Paused. Click on the Resume link below to start building."),
              new BoldCommonLabel("Created build name is " + createdBuild.getBuildName()
                      + ". The status of the new build is Paused. Click on the Resume link below to start building.")));
      // Show action links
      final OrderedList olOptions = new OrderedList();
      olOptions.add(new CommonLink(CAPTION_EDIT, Pages.ADMIN_EDIT_BUILD, Pages.PARAM_BUILD_ID, createdBuild.getBuildID()));
      olOptions.add(new CommonLink(CAPTION_RESUME, Pages.ADMIN_RESUME_BUILD, Pages.PARAM_BUILD_ID, createdBuild.getBuildID()));
      olOptions.add(new CommonLink(CAPTION_BUILD_LIST, Pages.ADMIN_BUILDS));
      userPanel.add(olOptions);
      return Result.Done();
    } catch (final Exception e) {
      // Show error
      LOG.warn("Error while cloning the build", e);
      final Error error = new Error(buildToClone.getActiveBuildID(), StringUtils.toString(e), Error.ERROR_LEVEL_ERROR);
      error.setDetails(e);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
      super.baseContentPanel().showErrorMessage("Unxpected error while cloning build \"" + buildToClone.getBuildName()
              + "\": " + StringUtils.toString(e) + ". Please Parabuild system error log for details.");
    }
    return Result.Done();
  }
}
