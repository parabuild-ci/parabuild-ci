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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.object.Project;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;

import java.util.List;

/**
 * Shows list of projects in the system.
 */
public final class ProjectListTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ProjectListTable.class); // NOPMD

  private static final int COLUMN_COUNT = 5;

  private static final int COL_NAME = 0;
  private static final int COL_KEY = 1;
  private static final int COL_DESCRIPTION = 2;
  private static final int COL_BUILD_COUNT = 3;
  private static final int COL_ACTION = 4;

  private static final String CAPTION_NAME = "Project";
  private static final String CAPTION_KEY = "Key";
  private static final String CAPTION_DESCRIPTION = "Description";
  private static final String CAPTION_BUILD_COUNT = "Build Configurations";
  private static final String CAPTION_ACTION = "Action";

  private List projects;


  public ProjectListTable(final boolean showControls) {

    super(showControls ? COLUMN_COUNT : COLUMN_COUNT - 1, false);
    setTitle("Project List");
    setWidth("100%");
    setGridColor(Pages.COLOR_PANEL_BORDER);
    populate(SecurityManager.getInstance().getUserProjects());
  }


  /**
   * Returns array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   */
  public Component[] makeHeader() {

    final Component[] headers = new Label[columnCount()];
    headers[COL_NAME] = new TableHeaderLabel(CAPTION_NAME, "30%");
    headers[COL_KEY] = new TableHeaderLabel(CAPTION_KEY, "15%");
    headers[COL_DESCRIPTION] = new TableHeaderLabel(CAPTION_DESCRIPTION, "20%");
    headers[COL_BUILD_COUNT] = new TableHeaderLabel(CAPTION_BUILD_COUNT, "15%", Layout.CENTER);
    if (columnCount() == COLUMN_COUNT) {
      headers[COL_ACTION] = new TableHeaderLabel(CAPTION_ACTION, "20%");
    }
    return headers;
  }


  /**
   * Returns array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {

    if (rowIndex >= projects.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final Project project = (Project) projects.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((Label) row[COL_NAME]).setText(project.getName());
    ((Label) row[COL_KEY]).setText(project.getKey());
    ((Label) row[COL_DESCRIPTION]).setText(project.getDescription());
    ((Label) row[COL_BUILD_COUNT]).setText(getBuildCount(project));
    if (columnCount() == COLUMN_COUNT) {
      ((ProjectCommandsFlow) row[COL_ACTION]).setProjectID(project.getID());
    }
    return TBL_ROW_FETCHED;
  }


  private static String getBuildCount(final Project project) {
    return Integer.toString(ProjectManager.getInstance().getBuildCount(project.getID()));
  }


  /**
   * Makes row
   */
  public Component[] makeRow(final int rowIndex) {

    final Component[] result = new Component[columnCount()];
    result[COL_NAME] = new CommonLabel();
    result[COL_KEY] = new CommonLabel();
    result[COL_DESCRIPTION] = new CommonLabel();
    result[COL_BUILD_COUNT] = new CommonLabel();
    result[COL_BUILD_COUNT].setAlignX(Layout.CENTER);
    if (columnCount() == COLUMN_COUNT) {
      result[COL_ACTION] = new ProjectCommandsFlow();
    }
    return result;
  }


  /**
   * Populates table with projects. This list is reused in fetchRow
   * method.
   *
   * @param projects the list of projects.
   * @see BuildState
   */
  final void populate(final List projects) {
    this.projects = projects;
    super.populate();
  }
}
