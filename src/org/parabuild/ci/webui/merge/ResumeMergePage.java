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
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page is repsonsible for resuming stopped/paused merge.
 */
public final class ResumeMergePage extends BaseMergePage implements StatelessTierlet {

  private static final long serialVersionUID = -194369566029085673L; // NOPMD
  private static final String CAPTION_RESUMING_MERGE = "Resuming Merge";


  /**
   * Constructor
   */
  public ResumeMergePage() {
    super.setTitle(makeTitle(CAPTION_RESUMING_MERGE));
  }


  /**
   * This method should be implemented by inheriting classes
   *
   * @param parameters
   * @param mergeConfiguration
   * @return Result
   */
  protected Result executeMergePage(final Parameters parameters, final MergeConfiguration mergeConfiguration) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.PAGE_MERGE_RESUME, parameters);
    }

    try {
      // authorize
      if (!super.getMergeUserRights(mergeConfiguration.getActiveMergeID()).isAllowedToResumeMerge()) {
        return WebuiUtils.showNotAuthorized(this);
      }
      // start merge
      super.setTitle(makeTitle(CAPTION_RESUMING_MERGE + " \"" + mergeConfiguration.getName() + '\"'));
      MergeManager.getInstance().resumeMerge(mergeConfiguration.getID());
      return Result.Done(Pages.PAGE_MERGE_LIST);
    } catch (final Exception e) {
      // Show error
      super.baseContentPanel().showErrorMessage("Unxpected error while resuming merge \"" + mergeConfiguration.getName() + "\": " + StringUtils.toString(e));
    }
    return Result.Done();
  }
}
