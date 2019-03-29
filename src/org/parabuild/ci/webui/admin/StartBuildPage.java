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
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This page is responsible for editing Parabuild system
 * properties.
 */
public final class StartBuildPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -194369566029085673L; // NOPMD


  /**
   * Constructor
   */
  public StartBuildPage() {
    super(FLAG_SHOW_HEADER_SEPARATOR | FLAG_SHOW_PAGE_HEADER_LABEL);
    super.setTitle(makeTitle("Starting the Build"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.ADMIN_START_BUILD, params);
    }

    // check if build exists, show error message if not
    final BuildConfig buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (buildConfig == null) {
      return WebuiUtils.showBuildNotFound(this);
    }

    // authorise
    if (!super.getUserRights(buildConfig.getActiveBuildID()).isAllowedToStartBuild()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // parallel builds cannot be started manually
    if (buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
      return WebuiUtils.showNotSupported(this);
    }

    final String title = "Starting Build " + buildConfig.getBuildName();
    super.setTitle(makeTitle(title));
    // NOTE: simeshev@parabuilci.org - 2006-02-16 - either directly
    // start the build if there are no parameters, or pass control
    // to the edit panel.
    if (isNew()) {
      // start edit session, start/stop is handled
      // by the panel controls.
      super.setPageHeader(title);
      final StartBuildPanel pnlStartBuild = new StartBuildPanel(buildConfig, false);
      this.baseContentPanel().add(pnlStartBuild);
      return Result.Continue();
    } else {
      return Result.Continue();
    }
  }
}
