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

import java.text.*;
import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * Shows pending change lists for the given active build.
 *
 * @see ChangeList
 * @see BuildRun
 * @see DetailedBuildStatusPanel
 */
public final class PendingChangeListsTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(PendingChangeListsTable.class); // NOPMD

  private static final int COLUMN_COUNT = 4;

  private static final int COL_USER = 0;
  private static final int COL_DESCRPTN = 1;
  private static final int COL_TIME = 2;
  private static final int COL_NUMBER = 3;

  public static final String STR_USER = "User";
  public static final String STR_DESCRPTN = "Description";
  public static final String STR_NUMBER = "Change list";
  public static final String STR_TIME = "Time";

  private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat(), Locale.US); // NOPMD
  private List pendingChangeLists = null;


  public PendingChangeListsTable(final List pendingChangeLists) {
    super(COLUMN_COUNT, false);
    super.setGridColor(Pages.COLOR_PANEL_BORDER);
    super.setRowHeight(15);
    this.pendingChangeLists = pendingChangeLists;
    super.populate();
  }


  /**
   * Returs array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   */
  public Component[] makeHeader() {
    // create
    final Component[] headers = new Label[columnCount()];
    headers[COL_USER] = new TableHeaderLabel(STR_USER, 80);
    headers[COL_DESCRPTN] = new TableHeaderLabel(STR_DESCRPTN, 315);
    headers[COL_TIME] = new TableHeaderLabel(STR_TIME, 125);
    headers[COL_NUMBER] = new TableHeaderLabel(STR_NUMBER, 47);

    // appearance
    headers[COL_NUMBER].setAlignX(Layout.CENTER);
    return headers;
  }


  /**
   * Returs array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= pendingChangeLists.size()) return TBL_NO_MORE_ROWS;
    final ChangeList changeList = (ChangeList)pendingChangeLists.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((Label)row[COL_USER]).setText(changeList.getUser());
    ((Label)row[COL_DESCRPTN]).setText(changeList.getDescription());
    ((Label)row[COL_NUMBER]).setText(changeList.getNumber());
    ((Label)row[COL_TIME]).setText(changeList.getCreatedAt(dateTimeFormat));
    return TBL_ROW_FETCHED;
  }


  /**
   * Makes row
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_USER] = new Label();
    result[COL_DESCRPTN] = new Label();
    result[COL_TIME] = new Label();
    result[COL_NUMBER] = new Label();
    result[COL_NUMBER].setAlignX(Layout.CENTER);
    return result;
  }
}
