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
package org.parabuild.ci.webui.admin.project;

import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * Page with a list of projects
 */
public final class ProjectListPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = -2472052514871569348L; // NOPMD
  private static final String CAPTION_PROJECT_LIST = "Project List";


  public ProjectListPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_QUICK_SEARCH | FLAG_SHOW_HEADER_SEPARATOR);
    setTitle(makeTitle(CAPTION_PROJECT_LIST));
  }


  protected Result executeSystemConfigurationPage(final Parameters params) {
    final GridIterator gi = new GridIterator(getRightPanel(), 2);

    // Add project list table
    final ProjectListTable projectsTable = new ProjectListTable(super.isValidAdminUser());
    projectsTable.hideTitle();
    gi.add(projectsTable, 2);

    // Add new project link - bottom
    if (isValidAdminUser()) {
      gi.add(WebuiUtils.makeHorizontalDivider(5), 2);
      gi.add(makeNewProjectLink(), 2);
    }
    return Result.Done();
  }


  private CommonCommandLinkWithImage makeNewProjectLink() {
    final CommonCommandLinkWithImage lnkAddNewProject = new AddProjectLink();
    lnkAddNewProject.setAlignX(Layout.LEFT);
    lnkAddNewProject.setAlignY(Layout.TOP);
    return lnkAddNewProject;
  }


  private static final class AddProjectLink extends CommonCommandLinkWithImage {

    private static final String CAPTION_ADD_NEW_PROJECT = "Add Project";


    AddProjectLink() {
      super(CAPTION_ADD_NEW_PROJECT, Pages.PAGE_EDIT_PROJECT);
    }
  }
}
