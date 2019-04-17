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

import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.build.BuildState;
import viewtier.ui.*;

public final class GroupsTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(GroupsTable.class); // NOPMD

  private static final int COLUMN_COUNT = 3;

  private static final int COL_NAME = 0;
  private static final int COL_DESCRIPTION = 1;
  private static final int COL_ACTION = 2;

  public static final String STR_GROUP_NAME = "Group name";
  public static final String STR_GROUP_DESCRIPTION = "Description";
  public static final String STR_ACTION = "Action";

  private List groups = null;


  public GroupsTable() {
    super(COLUMN_COUNT, false);
    setTitle("Group List");
    setWidth(Pages.PAGE_WIDTH);
    setGridColor(Pages.COLOR_PANEL_BORDER);
    populate(SecurityManager.getInstance().getAllGroups());
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
    if (rowIndex >= groups.size()) return TBL_NO_MORE_ROWS;
    final Group group = (Group)groups.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((Label)row[COL_NAME]).setText(group.getName());
    ((Label)row[COL_DESCRIPTION]).setText(group.getDescription());
    ((GroupActionMenuList)row[COL_ACTION]).setGroupID(group.getID());
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
    result[COL_ACTION] = new GroupActionMenuList();
    return result;
  }


  /**
   * Populates table with groups. This list is reused in fetchRow
   * method.
   *
   * @see BuildState
   */
  public final void populate(final List groups) {
    this.groups = groups;
    super.populate();
  }


  /**
   * List of commands available for a parcticular group in a
   * groups table.
   */
  private static final class GroupActionMenuList extends Flow {

    private static final long serialVersionUID = -2944698083772523636L;
    private Link lnkEdit = null;
    private Link lnkDelete = null;


    /**
     * Constructor.
     *
     * @param groupID ID to use to compose command links.
     */
    public GroupActionMenuList(final int groupID) {
      this();
      setGroupID(groupID);
    }


    /**
     * Constructor.
     */
    public GroupActionMenuList() {
      lnkEdit = new CommonLink("Edit", Pages.ADMIN_EDIT_GROUP);
      lnkDelete = new CommonLink("Delete", Pages.ADMIN_DELETE_GROUP);
      this.add(lnkEdit);
      this.add(new Label(" | "));
      this.add(lnkDelete);
    }


    /**
     * Sets group ID
     *
     * @param groupID to set
     */
    public void setGroupID(final int groupID) {
      final Properties param = new Properties();
      param.setProperty(Pages.PARAM_GROUP_ID, Integer.toString(groupID));
      lnkDelete.setParameters(param);
      lnkEdit.setParameters(param);
    }
  }
}
