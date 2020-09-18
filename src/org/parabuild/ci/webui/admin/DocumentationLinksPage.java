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

import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.OrderedList;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This class is a generic error page
 */
public final class DocumentationLinksPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = 644441224120456213L; // NOPMD
  // private static final Log log = LogFactory.getLog(ErrorListPage.class);

  public static final String HREF_ADMIN_MANUAL = "/parabuild/admin/docs/admin_manual.html";
  public static final String HREF_INSTALL_MANUAL = "/parabuild/admin/docs/install_guide.html";
  public static final String HREF_USER_MANUAL = "/parabuild/admin/docs/user_manual.html";
  private static final String HREF_API_MANUAL_HTML = "/parabuild/admin/docs/api_manual.html";


  /**
   * Constructor.
   */
  public DocumentationLinksPage() {
    super(FLAG_SHOW_HEADER_SEPARATOR);
    super.markTopMenuItemSelected(MENU_SELECTION_DOCUMENTATION);
    setTitle(makeTitle("Documentation"));
    final CommonLabel lbDocsHeader = new CommonLabel("Parabuild Documentation:");
    final OrderedList lstDocs = new OrderedList();

    final CommonLink lnkInstallManual = new CommonLink("Parabuild Installation Guide", HREF_INSTALL_MANUAL);
    lnkInstallManual.setTarget("_blank");
    lstDocs.add(lnkInstallManual);

    final CommonLink lnkAdminManual = new CommonLink("Parabuild Administrator's Manual", HREF_ADMIN_MANUAL);
    lnkAdminManual.setTarget("_blank");
    lstDocs.add(lnkAdminManual);

    final CommonLink lnkAPIManual = new CommonLink("Parabuild Web Service API Manual", HREF_API_MANUAL_HTML);
    lnkAPIManual.setTarget("_blank");
    lstDocs.add(lnkAPIManual);

    final CommonLink lnkUserManual = new CommonLink("Parabuild User's Manual", HREF_USER_MANUAL);
    lnkUserManual.setTarget("_blank");
    lstDocs.add(lnkUserManual);

    baseContentPanel().getUserPanel().setWidth(Pages.PAGE_WIDTH);
    baseContentPanel().getUserPanel().add(lbDocsHeader);
    baseContentPanel().getUserPanel().add(lstDocs);
  }


  public Result executePage(final Parameters params) {
    // authenticate
//    if (!super.isValidUser()) {
//      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, params, Pages.ADMIN_ERROR_LIST);
//    }
    return Result.Done();
  }
}

