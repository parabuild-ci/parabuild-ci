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
package org.parabuild.ci.webui.result;

import org.parabuild.ci.util.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This page shows list of result groups
 */
public final class UnpublishResultPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -2472052514871569348L;  // NOPMD
  private static final String CAPTION_REQUESTED_RESULT_NOT_FOUND = "Requested result not found";
  private static final String CAPTION_UNPUBLISHING_RESULT = "Unpublishing result";
  private static final String CAPTION_NOT_ALLOWED = "You are not allowed to perform this operation.";


  public UnpublishResultPage() {
    setTitle(makeTitle(CAPTION_UNPUBLISHING_RESULT));
  }


  public Result executePage(final Parameters params) {
    final Integer publishedResultID = ParameterUtils.getPublishedResultIDFromParameters(params);
    if (publishedResultID == null) {
      baseContentPanel().showErrorMessage(CAPTION_REQUESTED_RESULT_NOT_FOUND);
      return Result.Done();
    } else {
      final PublishedStepResult publishedStepResult = ResultGroupManager.getInstance().getPublishedStepResult(publishedResultID);
      if (publishedStepResult == null) {
        baseContentPanel().showErrorMessage(CAPTION_REQUESTED_RESULT_NOT_FOUND);
        return Result.Done();
      }

      // check rights
      final BuildRights userBuildRights = SecurityManager.getInstance().getUserBuildRights(getUser(), publishedStepResult.getActiveBuildID());
      if (userBuildRights.isAllowedToPublishResults()) {

        // unpublish
        ResultGroupManager.getInstance().unpublishStepResult(publishedResultID);

        // return to the content of the result group
        final String resultGroupIDString = params.getParameterValue(Pages.PARAM_RESULT_GROUP_ID);
        final int resultGroupID = StringUtils.isValidInteger(resultGroupIDString) ? Integer.parseInt(resultGroupIDString) : -1;
        if (resultGroupID == -1) {
          return Result.Done(Pages.RESULT_GROUPS);
        } else {
          final Parameters returnParameters = new Parameters();
          returnParameters.addParameter(Pages.PARAM_RESULT_GROUP_ID, Integer.toString(resultGroupID));
          return Result.Done(Pages.RESULT_GROUP_CONTENT, returnParameters);
        }
      } else {
        baseContentPanel().showErrorMessage(CAPTION_NOT_ALLOWED);
        return Result.Done(403);
      }
    }
  }
}
