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
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * List of groups page.
 */
public final class GroupsPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = -2472052514871569348L; // NOPMD


  public GroupsPage() {
    setTitle(makeTitle("Groups"));
  }


  protected Result executeSystemConfigurationPage(final Parameters params) {
    // Add groups table
    final GroupsTable groupsTable = new GroupsTable();
    groupsTable.setWidth("100%");
    getRightPanel().add(groupsTable);

    // Add new group link - bottom
    getRightPanel().add(makeNewGroupLink());
    return Result.Done();
  }


  private CommonCommandLinkWithImage makeNewGroupLink() {
    final CommonCommandLinkWithImage lnkAddNewGroup = new AddGroupLink();
    lnkAddNewGroup.setAlignY(Layout.TOP);
    return lnkAddNewGroup;
  }


  private static final class AddGroupLink extends CommonCommandLinkWithImage {

    private static final long serialVersionUID = -6314335766142783156L;


    AddGroupLink() {
      super("Add Group", Pages.ADMIN_EDIT_GROUP);
    }
  }
}
