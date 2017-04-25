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
package org.parabuild.ci.webui.admin.parameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows list of start parameters.
 *
 * @noinspection MethodOverloadsMethodOfSuperclass
 */
final class SystemVariableListTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(SystemVariableListTable.class); // NOPMD

  private static final int COLUMN_COUNT = 4;

  private static final int COL_NAME = 0;
  private static final int COL_VALUE = 1;
  private static final int COL_DESCRIPTION = 2;
  private static final int COL_ACTION = 3;

  public static final String CAPTION_NAME = "Shell Variable Name";
  public static final String CAPTION_VALUE = "Value";
  public static final String CAPTION_DESCRIPTION = "Description";
  public static final String CAPTION_ACTION = "Action";

  private List startParameters = null;
  private final byte variableType;
  private final int variableOwner;


  SystemVariableListTable(final String title, final byte variableType, final int variableOwner, final boolean showControls) {
    super(showControls ? COLUMN_COUNT : COLUMN_COUNT - 1, false);
    this.setHeaderVisible(true);
    this.setTitle(title);
    this.variableType = variableType;
    this.variableOwner = variableOwner;
    setWidth("100%");
    setGridColor(Pages.COLOR_PANEL_BORDER);
    populate(ConfigurationManager.getInstance().getStartParameters(StartParameterType.byteToType(variableType), variableOwner));
  }


  /**
   * Returs array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   */
  protected Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_NAME] = new TableHeaderLabel(CAPTION_NAME, "15%");
    headers[COL_VALUE] = new TableHeaderLabel(CAPTION_VALUE, "10%", Layout.CENTER);
    headers[COL_DESCRIPTION] = new TableHeaderLabel(CAPTION_DESCRIPTION, "35%");
    if (columnCount() == COLUMN_COUNT) {
      headers[COL_ACTION] = new TableHeaderLabel(CAPTION_ACTION, "20%");
    }
    return headers;
  }


  /**
   * Returs array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= startParameters.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final StartParameter startParameter = (StartParameter) startParameters.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((Label) row[COL_NAME]).setText(startParameter.getName());
    ((Label) row[COL_VALUE]).setText(startParameter.getValue());
    ((Label) row[COL_DESCRIPTION]).setText(startParameter.getDescription());
    if (columnCount() == COLUMN_COUNT) {
      ((SystemVariableCommandsFlow) row[COL_ACTION]).setParameter(startParameter.getID(), variableType, variableOwner);
    }
    return TBL_ROW_FETCHED;
  }


  /**
   * Makes row
   */
  protected Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_NAME] = new CommonLabel();
    result[COL_VALUE] = new CommonLabel();
    result[COL_DESCRIPTION] = new CommonLabel();
    if (columnCount() == COLUMN_COUNT) {
      result[COL_ACTION] = new SystemVariableCommandsFlow();
    }
    return result;
  }


  /**
   * Populates table with parameters. This list is reused in fetchRow
   * method.
   *
   * @noinspection ParameterHidesMemberVariable
   * @see BuildState
   */
  public void populate(final List startParameters) {
    this.startParameters = new ArrayList(startParameters);
    populate();
  }


  public String toString() {
    return "SystemVariableListTable{" +
            "startParameters=" + startParameters +
            '}';
  }
}