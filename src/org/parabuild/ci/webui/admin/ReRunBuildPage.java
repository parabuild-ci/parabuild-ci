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
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Parameters;

/**
 * This page is repsonsible for displaying a request to re-run a
 * build run.
 */
public final class ReRunBuildPage extends BasePage implements ConversationalTierlet {

  private static final Log log = LogFactory.getLog(ReRunBuildPage.class);
  private static final long serialVersionUID = -194369566029085673L; // NOPMD


  /**
   * Constructor
   */
  public ReRunBuildPage() {
    super.setTitle(makeTitle("Re-running the build"));
    super.baseContentPanel().setWidth("100%");
    super.setFocusOnFirstInput(true);
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.ADMIN_RERUN_BUILD, params);
    }

    // check if build exists, show error message if not
    final ActiveBuildConfig activeBuildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (activeBuildConfig == null) {
      WebuiUtils.showBuildNotFound(this);
    } else {

      // authorise
      if (!super.getUserRights(activeBuildConfig.getActiveBuildID()).isAllowedToStartBuild()) {
        return WebuiUtils.showNotAuthorized(this);
      }

      // parallel builds cannot be started manually
      if (activeBuildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
        return WebuiUtils.showNotSupported(this);
      }

      if (isNew()) {
        final ReRunBuildPanel reRunBuildPanel = new ReRunBuildPanel(activeBuildConfig);
        reRunBuildPanel.setWidth(Pages.PAGE_WIDTH);
        super.setTitle(makeTitle("Re-run build for  \"" + activeBuildConfig.getBuildName() + '\"'));
        super.baseContentPanel().add(reRunBuildPanel);
        return Result.Continue();
      }
    }
    if (log.isDebugEnabled()) log.debug("continue");
    return Result.Continue();
  }
}
