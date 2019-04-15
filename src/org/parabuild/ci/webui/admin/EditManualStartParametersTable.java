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
package org.parabuild.ci.webui.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.services.BuildStartRequestParameter;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.WebUIConstants;
import viewtier.ui.Component;
import viewtier.ui.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * This panel is responsible for showning parameter that are
 * going to be passed to the build runner as a part of a manual
 * build run/re-run request.
 */
public final class EditManualStartParametersTable extends AbstractFlatTable {

  private static final long serialVersionUID = -194369566029085673L; // NOPMD

  private static final Log log = LogFactory.getLog(EditManualStartParametersTable.class);

  private static final String CAPTION_NAME = "Shell Variable Name";
  private static final String CAPTION_VALUE = "Set to Value";
  private static final String CAPTION_DESCRIPTION = "Description";

  private static final int COL_NAME = 0;
  private static final int COL_VALUE = 1;
  private static final int COL_DESCRIPTION = 2;

  private final boolean useFirstParameterValueAsDefault;
  private int mode = WebUIConstants.MODE_EDIT;

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private List parameters;
  private static final boolean USE_FIRST_PARAMETER_VALUE_AS_DEFAULT = true;


  /**
   * @param parameters List of {@link StartParameter} objects.
   */
  public EditManualStartParametersTable(final List parameters, final boolean useFirstParameterValueAsDefault, final int mode) {
    this(useFirstParameterValueAsDefault, mode);
    populate(parameters);
  }


  /**
   *
   */
  public EditManualStartParametersTable(final boolean useFirstParameterValueAsDefault, final int mode) {
    super(3, false);
    this.useFirstParameterValueAsDefault = useFirstParameterValueAsDefault;
    this.mode = mode;
  }


  /**
   * @return List of {@link BuildStartRequestParameter} object or
   *         empty list in to parameters are provided.
   */
  public List getUpdatedParameterList() {
    final List result = new ArrayList(getRowCount() << 1);
    for (int index = 0, n = getRowCount(); index < n; index++) {
      final Component[] row = getRow(index);
      final CommonLabel lbName = (CommonLabel) row[COL_NAME];
      final VariableValueHolderFlow valueHolder = (VariableValueHolderFlow) row[COL_VALUE];
      final List values = valueHolder.getValues();
      if (!values.isEmpty()) {
        final StartParameter parameter = (StartParameter) parameters.get(index);
        result.add(new BuildStartRequestParameter(lbName.getText(), parameter.getDescription(), values, index));
      }
    }
    if (log.isDebugEnabled()) log.debug("result: " + result);
    return result;
  }


  /**
   */
  protected Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_NAME] = new TableHeaderLabel(CAPTION_NAME, 150);
    headers[COL_VALUE] = new TableHeaderLabel(CAPTION_VALUE, 450);
    headers[COL_DESCRIPTION] = new TableHeaderLabel(CAPTION_DESCRIPTION, 150);
    return headers;
  }


  /**
   * Makes row, should be implemented by successor class
   */
  protected Component[] makeRow(final int rowIndex) {
    final boolean editable = mode == WebUIConstants.MODE_EDIT;
    final Component[] result = new Component[columnCount()];
    result[COL_NAME] = new CommonLabel();
    result[COL_VALUE] = new VariableValueHolderFlow(editable);
    result[COL_DESCRIPTION] = new CommonLabel();
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
    if (rowIndex >= parameters.size()) return TBL_NO_MORE_ROWS;
    final StartParameter prm = (StartParameter) parameters.get(rowIndex);
    if (log.isDebugEnabled()) log.debug("prm: " + prm);
    final Component[] row = getRow(rowIndex);
    ((Label) row[COL_NAME]).setText(prm.getName());
    final VariableValueHolderFlow variableValueHolder = (VariableValueHolderFlow) row[COL_VALUE];
    variableValueHolder.setParameterDefinition(prm, useFirstParameterValueAsDefault);
    ((Label) row[COL_DESCRIPTION]).setText(prm.getDescription());
    return TBL_ROW_FETCHED;
  }


  public boolean validate() {
    final List errors = new ArrayList(3);
    for (int index = 0, n = getRowCount(); index < n; index++) {
      final Component[] row = getRow(index);
      final VariableValueHolderFlow valueHolder = (VariableValueHolderFlow) row[COL_VALUE];
      final StartParameter parameter = (StartParameter) parameters.get(index);
      if (parameter.isRequired() && !valueHolder.isValueSet()) {
        errors.add("Required variable \"" + parameter.getName() + "\" is not set");
      }
    }
    // result
    if (errors.isEmpty()) return USE_FIRST_PARAMETER_VALUE_AS_DEFAULT;
    // there are errors
    showErrorMessage(errors);
    return false;
  }


  /**
   * Populates with parameter defintions.
   *
   * @param parameterDefinitions
   */
  public void populate(final List parameterDefinitions) {
    this.parameters = parameterDefinitions;
    populate();
  }

  public String toString() {
    return "EditManualStartParametersTable{" +
            "useFirstParameterValueAsDefault=" + useFirstParameterValueAsDefault +
            ", mode=" + mode +
            ", parameterList=" + parameters +
            '}';
  }
}
