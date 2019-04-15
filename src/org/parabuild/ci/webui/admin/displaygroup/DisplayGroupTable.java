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

import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Link;

/**
 * This table displays list of build groups.
 */
public final class DisplayGroupTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(DisplayGroupTable.class); // NOPMD

  private static final int COLUMN_COUNT = 3;

  private static final int COL_NAME = 0;
  private static final int COL_DESCRIPTION = 1;
  private static final int COL_ACTION = 2;

  public static final String STR_GROUP_NAME = "Display group name";
  public static final String STR_GROUP_DESCRIPTION = "Description";
  public static final String STR_ACTION = "Action";

  private List displayGroupList = null;


  public DisplayGroupTable() {
    super(COLUMN_COUNT, false);
    setTitle("Display Group List");
    setWidth("100%");
    setGridColor(Pages.COLOR_PANEL_BORDER);
    populate(DisplayGroupManager.getInstance().getAllDisplayGroups());
  }


  /**
   * Returs array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   *
   * @return array of components containing table headers.
   */
  public Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_NAME] = new TableHeaderLabel(STR_GROUP_NAME, 300);
    headers[COL_DESCRIPTION] = new TableHeaderLabel(STR_GROUP_DESCRIPTION, 300);
    headers[COL_ACTION] = new TableHeaderLabel(STR_ACTION, 140);
    return headers;
  }


  /**
   * Returs array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= displayGroupList.size()) return TBL_NO_MORE_ROWS;
    final DisplayGroup displayGroup = (DisplayGroup)displayGroupList.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((Label)row[COL_NAME]).setText(displayGroup.getName());
    ((Label)row[COL_DESCRIPTION]).setText(displayGroup.getDescription());
    ((ActionMenuList)row[COL_ACTION]).setDisplayGroupID(displayGroup.getID());
    return TBL_ROW_FETCHED;
  }


  /**
   * Makes row
   *
   * @return array of components containing table row.
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_NAME] = new CommonLabel();
    result[COL_DESCRIPTION] = new CommonLabel();
    result[COL_ACTION] = new ActionMenuList();
    return result;
  }


  /**
   * Populates table with groups. This list is reused in fetchRow
   * method.
   *
   * @see BuildState
   */
  public final void populate(final List displayGroupList) {
    this.displayGroupList = displayGroupList;
    super.populate();
  }


  /**
   * List of commands available for a parcticular group in a
   * groups table.
   */
  private static final class ActionMenuList extends Flow {

    private static final long serialVersionUID = 6166310697804129602L;
    private final Link lnkEdit;
    private final Link lnkDelete;


    /**
     * Constructor.
     *
     * @param displayGroupID ID to use to compose command links.
     */
    public ActionMenuList(final int displayGroupID) {
      this();
      setDisplayGroupID(displayGroupID);
    }


    /**
     * Constructor.
     */
    public ActionMenuList() {
      lnkEdit = new CommonLink("Edit", Pages.ADMIN_EDIT_DISPLAY_GROUP);
      lnkDelete = new CommonLink("Delete", Pages.ADMIN_DELETE_DISPLAY_GROUP);
      this.add(lnkEdit);
      this.add(new Label(" | "));
      this.add(lnkDelete);
    }


    /**
     * Sets display group ID
     *
     * @param displayGroupID to set
     */
    public void setDisplayGroupID(final int displayGroupID) {
      final Properties param = new Properties();
      param.setProperty(Pages.PARAM_DISPLAY_GROUP_ID, Integer.toString(displayGroupID));
      lnkDelete.setParameters(param);
      lnkEdit.setParameters(param);
    }
  }
}
