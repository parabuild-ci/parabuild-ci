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
package org.parabuild.ci.webui.admin.displaygroup;

import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page shows list List of display groups.
 */
public final class DisplayGroupListPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = -2472052514871569348L; // NOPMD


  public DisplayGroupListPage() {
    setTitle(makeTitle("Display Groups"));
  }


  protected Result executeSystemConfigurationPage(final Parameters params) {
    // add new group link - top
    final GridIterator gi = new GridIterator(getRightPanel(), 1);

    // add admin builds table
    final DisplayGroupTable groupsTable = new DisplayGroupTable();
    gi.add(groupsTable, 1);

    // add new group link - bottom
    gi.add(WebuiUtils.makeHorizontalDivider(5), 1);
    gi.add(makeNewDisplayGroupLink());
    return Result.Done();
  }


  private CommonCommandLinkWithImage makeNewDisplayGroupLink() {
    final CommonCommandLinkWithImage lnkAddNewDisplayGroup = new AddDisplayGroupLink();
    lnkAddNewDisplayGroup.setAlignY(Layout.TOP);
    return lnkAddNewDisplayGroup;
  }


  private static final class AddDisplayGroupLink extends CommonCommandLinkWithImage {

    private static final long serialVersionUID = -3469444188818738936L;


    AddDisplayGroupLink() {
      super("Add Display Group", Pages.ADMIN_EDIT_DISPLAY_GROUP);
    }
  }
}
