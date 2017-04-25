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
package org.parabuild.ci.webui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page is repsonsible for editing Parabuild system
 * properties.
 */
public final class LogoutPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -2354330756414771139L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(LogoutPage.class); // NOPMD


  /**
   * Constructor
   */
  public LogoutPage() {
    super.markTopMenuItemSelected(MENU_SELECTION_LOGINLOGOUT);
    setTitle(makeTitle("Logout"));
    setLoginUsingRememberedUser(false);
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters parameters) {

    // authenticate
    if (super.isValidUser()) {

      // logout
      super.logoutUser();

      // create success message
      final Flow flwMsgSuccess = new Flow()
              .add(new BoldCommonLabel("You have successfuly logged out. "))
              .add(WebuiUtils.clickHereToContinue(Pages.PUBLIC_BUILDS));
      flwMsgSuccess.setAlignY(Layout.TOP);

      // show success message
      super.baseContentPanel().getUserPanel().clear();
      super.baseContentPanel().getUserPanel().add(flwMsgSuccess);

    } else {

      // show "not logged in"
      final Flow flwMsgNotLogged = new Flow()
              .add(new BoldCommonLabel("You are currently not logged in. "))
              .add(WebuiUtils.clickHereToContinue(Pages.PUBLIC_BUILDS));
      flwMsgNotLogged.setAlignY(Layout.TOP);

      super.baseContentPanel().getUserPanel().add(flwMsgNotLogged);
    }

    // reset menu mode to public
    setTopMenuMode(PageHeaderPanel.MODE_ANONYMOUS);

    // return
    return Result.Done();
  }
}
