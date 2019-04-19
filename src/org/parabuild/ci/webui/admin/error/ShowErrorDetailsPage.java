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
package org.parabuild.ci.webui.admin.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This class is a page to show error deatails.
 */
public final class ShowErrorDetailsPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = 4253039248045114350L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ShowErrorDetailsPage.class); // NOPMD


  public ShowErrorDetailsPage() {
    super.markTopMenuItemSelected(MENU_SELECTION_ERRORS);
    setTitle(makeTitle("Error details"));
  }


  public Result executePage(final Parameters parameters) {
    // Check if admin is logged in
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_ERROR_DETAILS, parameters);
    }

    // authorise
    if (!(super.isValidAdminUser() || SecurityManager.getInstance().isAllowedToSeeErrors(getUser()))) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // get error
    final Error error = getErrorFromParameters(parameters);
    if (error == null) {
      // show error not found
      baseContentPanel().showErrorMessage("Requested error can not be found.");
      baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_ERROR_LIST));
      return Result.Done();
    } else {
      // create error details panel
      final ErrorPanel errorPanel = new ErrorPanel(error);
      baseContentPanel().getUserPanel().add(errorPanel);
      baseContentPanel().getUserPanel().add(WebuiUtils.makeHorizontalDivider(10));
      baseContentPanel().getUserPanel().add(new CommonCommandLink("Clear error", Pages.ADMIN_CLEAR_ERROR, Pages.PARAM_ERROR_ID, parameters.getParameterValue(Pages.PARAM_ERROR_ID)));
      return Result.Done();
    }
  }


  /**
   * Retrieves error from params based on expected error ID. If
   * not found or if there are errors, return null;
   *
   * @param params
   */
  private static Error getErrorFromParameters(final Parameters params) {
    final String errorID = params.getParameterValue(Pages.PARAM_ERROR_ID);
    if (errorID == null) {
      return null;
    }
    return ErrorManagerFactory.getErrorManager().loadActiveError(errorID);
  }
}

