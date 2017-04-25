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

import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This class is a generic error page
 */
public final class ErrorListPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = 644441224120456213L; // NOPMD
  // private static final Log log = LogFactory.getLog(ErrorListPage.class);

  private static final String CAPTION_SYSTEM_ERRORS = "System Errors and Messages";
  private static final String CAPTION_CLEAR_ALL_ERRORS = "Clear All Messages";

  private final ErrorTable errorsTable = new ErrorTable(false);


  public ErrorListPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_PAGE_HEADER_LABEL | FLAG_SHOW_HEADER_SEPARATOR);
    super.markTopMenuItemSelected(MENU_SELECTION_ERRORS);
    setTitle(makeTitle(CAPTION_SYSTEM_ERRORS));
    setPageHeader(CAPTION_SYSTEM_ERRORS);
    baseContentPanel().getUserPanel().add(errorsTable);
  }


  public Result executePage(final Parameters parameters) {

    // Authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_ERROR_LIST, parameters);
    }

    if (!(super.isValidAdminUser() || SecurityManager.getInstance().isAllowedToSeeErrors(getUser()))) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // Populate
    errorsTable.populate();


    // Add "clean all" command
    if (errorsTable.getRowCount() > 0) {

      baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
      baseContentPanel().getUserPanel().add(new CommonCommandLink(CAPTION_CLEAR_ALL_ERRORS, Pages.ADMIN_CLEAR_ERROR, Pages.PARAM_ERROR_ID, Pages.PARAM_VALUE_ALL_ERRORS));
    }
    return Result.Done();
  }


  public String toString() {
    return "ErrorListPage{" +
            "errorsTable=" + errorsTable +
            '}';
  }
}

