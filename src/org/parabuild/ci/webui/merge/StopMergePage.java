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
package org.parabuild.ci.webui.merge;

import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page is repsonsible for stopping and pausing a merge.
 */
public final class StopMergePage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -4958811997344302265L; // NOPMD
  private static final String CAPTION_STOPPING_MERGE = "Stopping Merge";


  /**
   * Constructor
   */
  public StopMergePage() {
    super.setTitle(makeTitle(CAPTION_STOPPING_MERGE));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.PAGE_MERGE_STOP, params);
    }

    // check if build exists, show error message if not
    final ActiveMergeConfiguration mergeConfiguration = ParameterUtils.getActiveMergeConfigurationFromParameters(params);
    if (mergeConfiguration == null) {
      return WebuiUtils.showMergeNotFound(this);
    } else {
      try {
        // authorise
        if (!super.getMergeUserRights(mergeConfiguration.getID()).isAllowedToStopMerge()) {
          return WebuiUtils.showNotAuthorized(this);
        }
        // start build
        super.setTitle(makeTitle("Stopping Merge \"" + mergeConfiguration.getName() + '\"'));
        MergeManager.getInstance().stopMerge(mergeConfiguration.getID());
        return Result.Done(Pages.PAGE_MERGE_LIST);
      } catch (final Exception e) {
        // Show error
        super.baseContentPanel().showErrorMessage("Unxpected error while stopping merge \"" + mergeConfiguration.getName() + "\": " + StringUtils.toString(e));
      }
    }
    return Result.Done();
  }
}
