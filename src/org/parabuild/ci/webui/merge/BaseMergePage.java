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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;

/**
 * BaseMergPage encapsulates functionality commen for pages
 * dealing with a single merge configuration.
 */
public abstract class BaseMergePage extends BasePage {

  protected BaseMergePage() {
  }


  protected BaseMergePage(final int flags) {
    super(flags);
  }


  /**
   * Strategy method to be implemented by classes inheriting
   * BaseMergePage.
   *
   * @param parameters
   *
   * @return result of page execution
   */
  protected final Result executePage(final Parameters parameters) {
    final Integer activeMergeConfigurationID;
    if (parameters.isParameterPresent(Pages.PARAM_MERGE_ID)) {
      // validate
      final String stringMergeID = parameters.getParameterValue(Pages.PARAM_MERGE_ID);
      if (StringUtils.isValidInteger(stringMergeID)) {
        activeMergeConfigurationID = new Integer(Integer.parseInt(stringMergeID));
      } else {
        return WebuiUtils.showMergeNotFound(this);
      }
    } else {
      // try merge change list
      if (parameters.isParameterPresent(Pages.PARAM_BRANCH_CHANGE_LIST_ID)) {
        final String branchChangeListID = parameters.getParameterValue(Pages.PARAM_BRANCH_CHANGE_LIST_ID);
        if (StringUtils.isValidInteger(branchChangeListID)) {
          activeMergeConfigurationID = MergeManager.getMergeConfigarationIDByBranchChangeListID(Integer.parseInt(branchChangeListID));
        } else {
          return WebuiUtils.showMergeNotFound(this);
        }
      } else {
        return WebuiUtils.showMergeNotFound(this);
      }
    }

    if (activeMergeConfigurationID == null) {
      return WebuiUtils.showMergeNotFound(this);
    }

    final MergeConfiguration mergeConfiguration = MergeManager.getInstance().getMergeConfiguration(activeMergeConfigurationID);
    if (mergeConfiguration == null) return WebuiUtils.showMergeNotFound(this);

    return executeMergePage(parameters, mergeConfiguration);
  }


  /**
   * This method should be implemented by inheriting classes
   *
   * @param parameters
   * @param mergeConfiguration
   * @return Result
   */
  protected abstract Result executeMergePage(final Parameters parameters, MergeConfiguration mergeConfiguration);
}
