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

import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Label;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Displays a list of changes for a given file.
 */
public final class FileChangeListsTable extends AbstractFlatTable {

  private static final long serialVersionUID = -4042223366773378958L;
  private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat(), Locale.US); // NOPMD

  private static final int COLUMN_COUNT = 4;
  private static final int COL_USER_NAME = 0;
  private static final int COL_CHANGE_LIST_DESCRIPTION = 1;
  private static final int COL_CHANGE_LIST_NUMBER = 2;
  private static final int COL_CHANGE_LIST_TIME = 3;
  private List changeLists;


  /**
   * Constructor, creates an instance of FileChangeListsTable.
   */
  public FileChangeListsTable() {
    super(COLUMN_COUNT, false);
    super.setWidth("100%");
    super.setHeaderVisible(false);
  }


  /**
   * Sets a list of {@link ChangeList} object to display.
   *
   * @param changeLists
   */
  public void setChangeLists(final List changeLists) {

    this.changeLists = changeLists;
  }


  /**
   */
  protected Component[] makeHeader() {

    final Component[] headers = new Label[columnCount()];
    headers[COL_USER_NAME] = new TableHeaderLabel("User Name", "10%");
    headers[COL_CHANGE_LIST_DESCRIPTION] = new TableHeaderLabel("Change list description", "75%");
    headers[COL_CHANGE_LIST_NUMBER] = new TableHeaderLabel("Change list number", "5%");
    headers[COL_CHANGE_LIST_TIME] = new TableHeaderLabel("Change list time", "10%");
    return headers;
  }


  /**
   * Makes row, should be implemented by successor class
   */
  protected Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_USER_NAME] = new CommonLabel();
    result[COL_CHANGE_LIST_DESCRIPTION] = new CommonLabel();
    result[COL_CHANGE_LIST_NUMBER] = new CommonLabel();
    result[COL_CHANGE_LIST_TIME] = new CommonLabel();
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

    if (rowIndex >= changeLists.size()) {
      return TBL_NO_MORE_ROWS;
    }

    final ChangeList changeList = (ChangeList) changeLists.get(rowIndex);
    final Component[] row = getRow(rowIndex);

    ((Label) row[COL_USER_NAME]).setText(changeList.getUser());
    ((Label) row[COL_CHANGE_LIST_DESCRIPTION]).setText(changeList.getDescription());
    ((Label) row[COL_CHANGE_LIST_NUMBER]).setText(changeList.getNumber());
    ((Label) row[COL_CHANGE_LIST_TIME]).setText(changeList.getCreatedAt(dateTimeFormat));


    return TBL_ROW_FETCHED;
  }
}
