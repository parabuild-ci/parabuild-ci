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
package org.parabuild.ci.webui.result;

import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;
import org.parabuild.ci.security.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This table displays list of result groups.
 */
public final class ResultGroupTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(ResultGroupTable.class); // NOPMD

  private static final int COLUMN_COUNT = 2;

  private static final int COL_NAME = 0;
  private static final int COL_DESCRIPTION = 1;
//  private static final int COL_ACTION = 2;

  public static final String CAPTION_NAME = "Name";
  public static final String CAPTION_DESCRIPTION = "Description";
//  public static final String CAPTION_ACTION = "Action";

  private List resultGroupList = Collections.emptyList();


  /**
   * Creates and automatically pupulates the resultGroup list table.
   */
  public ResultGroupTable() {
    super(COLUMN_COUNT, false);
    setGridColor(Pages.COLOR_PANEL_BORDER);

    // populate
    resultGroupList = SecurityManager.getInstance().getUserResultGroups(getTierletContext());
    super.populate();
  }


  /**
   * Returs array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   *
   * @return array of components containing table headers.
   */
  public Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_NAME] = new TableHeaderLabel(CAPTION_NAME, "60%");
    headers[COL_DESCRIPTION] = new TableHeaderLabel(CAPTION_DESCRIPTION, "40%");
    return headers;
  }


  /**
   * Returs array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= resultGroupList.size()) return TBL_NO_MORE_ROWS;
    final ResultGroup resultGroup = (ResultGroup)resultGroupList.get(rowIndex);
    final int resultGroupID = resultGroup.getID();
    final ResultGroupRights rights = SecurityManager.getInstance().getUserResultGroupRights(getTierletContext(), resultGroupID);
    final Component[] row = getRow(rowIndex);
    ((NameActionHolderFlow)row[COL_NAME]).setResultGroup(resultGroup.getName(), resultGroupID, rights);
    ((Label)row[COL_DESCRIPTION]).setText(resultGroup.getDescription());
    return TBL_ROW_FETCHED;
  }


  /**
   * Makes row
   *
   * @return array of components containing table row.
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_NAME] = new NameActionHolderFlow();
    result[COL_DESCRIPTION] = new CommonLabel();
    return result;
  }


  private static final class NameActionHolderFlow extends Flow {

    private static final long serialVersionUID = -1865380003770128625L;
    private final CommonLink lnkGroupName = new CommonLink("", "");
    private final ActionMenuList actionMenuList = new ActionMenuList();
    private final BreakLabel lbBreak = new BreakLabel();


    /**
     */
    public NameActionHolderFlow() {
      add(lnkGroupName);
//      add(breakLabel)
      add(actionMenuList);
      lbBreak.setVisible(false);
      actionMenuList.setVisible(false);
    }


    public void setResultGroup(final String name, final int resultGroupID, final ResultGroupRights rights) {

      // group name link

      final Properties params = new Properties();
      params.setProperty(Pages.PARAM_RESULT_GROUP_ID, Integer.toString(resultGroupID));
      lnkGroupName.setText(name);
      lnkGroupName.setUrl(Pages.RESULT_GROUP_CONTENT);
      lnkGroupName.setParameters(params);

      // edit controls with conditional visibility

      lbBreak.setVisible(rights.isAllowedToListCommands());
      actionMenuList.setVisible(rights.isAllowedToListCommands());
      actionMenuList.setResultGroup(resultGroupID, rights);
    }
  }


  /**
   * List of commands available for a parcticular resultGroup in a
   * resultGroups table.
   */
  private static final class ActionMenuList extends Flow {

    private static final long serialVersionUID = -5623572818915657857L;
    private final Link lnkEdit;
    private final Link lnkDelete;
    private final Label lbDeleteSeparator = new Label(" | ");


    /**
     * Constructor.
     */
    public ActionMenuList() {
//      lnkEdit = new CommonCommandLink("Edit", Pages.ADMIN_EDIT_RESULT_GROUP);
//      lnkDelete = new CommonCommandLink("Delete", Pages.ADMIN_DELETE_RESULT_GROUP);
      lnkEdit = new CommonLink("Edit", Pages.RESULT_GROUP_EDIT);
      lnkDelete = new CommonLink("Delete", Pages.RESULT_GROUP_DELETE);
      this.add(new Label("  ("));
      this.add(lnkEdit);
      this.add(lbDeleteSeparator);
      this.add(lnkDelete);
      this.add(new Label(")"));
    }


    /**
     * Sets resultGroup ID
     *
     * @param resultGroupID to set
     * @param rights
     */
    public void setResultGroup(final int resultGroupID, final ResultGroupRights rights) {
      final Properties param = new Properties();
      param.setProperty(Pages.PARAM_RESULT_GROUP_ID, Integer.toString(resultGroupID));
      lnkDelete.setParameters(param);
      lnkEdit.setParameters(param);
      lnkDelete.setVisible(rights.isAllowedToDeleteResultGroup());
      lbDeleteSeparator.setVisible(rights.isAllowedToDeleteResultGroup());
      lnkEdit.setVisible(rights.isAllowedToUpdateResultGroup());
    }
  }
}
