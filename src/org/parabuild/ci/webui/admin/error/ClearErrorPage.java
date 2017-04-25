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
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This class is an action page to clean up a error.
 */
public final class ClearErrorPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = 2672938262022877364L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ClearErrorPage.class); // NOPMD


  public ClearErrorPage() {
    super.markTopMenuItemSelected(MENU_SELECTION_ERRORS);
    setTitle(makeTitle("Clear Error"));
  }


  public Result executePage(final Parameters params) {
    // Check if admin is logged in
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_BUILD_COMMANDS_LIST, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // process
    if (params.isParameterPresent(Pages.PARAM_ERROR_ID)) {
      final ErrorManager em = ErrorManagerFactory.getErrorManager();
      final String errorID = params.getParameterValue(Pages.PARAM_ERROR_ID);
      // check if it is "all" errors to cleanup
      if (errorID.equalsIgnoreCase(Pages.PARAM_VALUE_ALL_ERRORS)) {
        em.clearAllActiveErrors();
      } else {
        em.clearActiveError(errorID);
      }
    }

    // forward to the list of errors
    return Result.Done(Pages.ADMIN_ERROR_LIST);
  }
}
