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
package org.parabuild.ci.webui.admin.email;

import org.parabuild.ci.object.GlobalVCSUserMap;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * GlobalVersionControlUserMapTable
 * <p/>
 *
 * @author Slava Imeshev
 * @since Dec 27, 2008 3:38:10 PM
 */
final class GlobalVCSUserMapTable extends AbstractFlatTable {

  private static final String CAPTION_VCS_USER_NAME = "Version Control User Name";
  private static final String CAPTION_E_MAIL = "E-mail";
  private static final String CAPTION_NOTE = "Note";
  private static final String CAPTION_ACTION = "Action";

  private static final int COL_NAME = 0;
  private static final int COL_EMAIL = 1;
  private static final int COL_NOTE = 2;
  private static final int COL_ACTION = 3;

  private final List mappingList;


  /**
   * Constructor - creates an instance of flat table with given
   * number of columns
   *
   * @param columnCount number of columns ih the table
   * @param editable    true if editting is allowed
   */
  public GlobalVCSUserMapTable(final List mappingList) {
    super(4, false);
    this.mappingList = new ArrayList(mappingList);
    super.populate();
  }


  /**
   */
  protected Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_NAME] = new TableHeaderLabel(CAPTION_VCS_USER_NAME, "30%");
    headers[COL_EMAIL] = new TableHeaderLabel(CAPTION_E_MAIL, "30%");
    headers[COL_NOTE] = new TableHeaderLabel(CAPTION_NOTE, "30%");
    headers[COL_ACTION] = new TableHeaderLabel(CAPTION_ACTION, "10%");
    return headers;
  }


  /**
   * Makes row, should be implemented by successor class
   */
  protected Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_NAME] = new CommonLabel();
    result[COL_EMAIL] = new CommonLabel();
    result[COL_NOTE] = new CommonLabel();
    result[COL_ACTION] = new ActionMenuList();
    return result;
  }


  /**
   * This implementation of this abstract method is called when
   * the table wants to fetch a row with a given rowIndex.
   * Implementing method should fill the data corresponding the
   * given rowIndex.
   *
   * @return this method should return either TBL_ROW_FETCHED or
   *         TBL_NO_MORE_ROWS if the requested row is out of
   *         range.
   * @see AbstractFlatTable#TBL_ROW_FETCHED
   * @see AbstractFlatTable#TBL_NO_MORE_ROWS
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= mappingList.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final GlobalVCSUserMap map = (GlobalVCSUserMap) mappingList.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((Label) row[COL_NAME]).setText(map.getVcsUserName());
    ((Label) row[COL_EMAIL]).setText(map.getEmail());
    ((Label) row[COL_NOTE]).setText(map.getDescription());
    ((ActionMenuList) row[COL_ACTION]).setMappingID(map.getID());
    return TBL_ROW_FETCHED;
  }


  /**
   * List of commands available for a parcticular mapping in a
   * mapping table.
   */
  private static final class ActionMenuList extends Flow {

    private final Link lnkEdit;
    private final Link lnkDelete;


    /**
     * Constructor.
     */
    ActionMenuList() {
      lnkEdit = new CommonLink("Edit", Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP_EDIT);
      lnkDelete = new CommonLink("Delete", Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP_DELETE);
      this.add(lnkEdit);
      this.add(new Label(" | "));
      this.add(lnkDelete);
    }


    /**
     * Sets mapping ID
     *
     * @param mappingID to set
     */
    public void setMappingID(final int mappingID) {
      final Properties param = new Properties();
      param.setProperty(Pages.PARAM_VCS_MAPPING_ID, Integer.toString(mappingID));
      lnkDelete.setParameters(param);
      lnkEdit.setParameters(param);
    }


    public String toString() {
      return "ActionMenuList{" +
              "lnkEdit=" + lnkEdit +
              ", lnkDelete=" + lnkDelete +
              '}';
    }
  }


  public String toString() {
    return "GlobalVersionControlUserMapTable{" +
            "mappingList=" + mappingList +
            '}';
  }
}
