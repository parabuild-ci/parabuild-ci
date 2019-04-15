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
package org.parabuild.ci.webui.admin.usermanagement;

import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * List of users page.
 */
public final class UsersPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = -2472052514871569348L; // NOPMD


  public UsersPage() {
    setTitle(makeTitle("User List"));
  }


  protected Result executeSystemConfigurationPage(final Parameters params) {
    final Panel rightPanel = getRightPanel();

    // add new user link - top
    rightPanel.add(makeNewUserLink());
    rightPanel.add(WebuiUtils.makeHorizontalDivider(5));

    // add user list table
    final UsersTable usersTable = new UsersTable();
    usersTable.setWidth("100%");
    rightPanel.add(usersTable);

    // add new user link - bottom
    rightPanel.add(WebuiUtils.makeHorizontalDivider(5));
    rightPanel.add(makeNewUserLink());

    return Result.Done();
  }


  private CommonCommandLinkWithImage makeNewUserLink() {
    final CommonCommandLinkWithImage lnkAddNewUser = new AddUserLink();
    lnkAddNewUser.setAlignX(Layout.LEFT);
    lnkAddNewUser.setAlignY(Layout.TOP);
    return lnkAddNewUser;
  }


  private static final class AddUserLink extends CommonCommandLinkWithImage {

    private static final long serialVersionUID = -4740013610744281425L;


    AddUserLink() {
      super("Add New User", Pages.ADMIN_EDIT_USER);
    }
  }
}
