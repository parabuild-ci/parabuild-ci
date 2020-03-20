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
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.Validatable;
import viewtier.ui.AbstractInput;
import viewtier.ui.CheckBox;
import viewtier.ui.Component;
import viewtier.ui.Field;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This table is used to entry build run parameters.
 */
public final class ManualStartSettingsTable extends AbstractFlatTable implements Validatable {

  private static final long serialVersionUID = -554537606480131927L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ManualStartSettingsTable.class); // NOPMD

  private static final int COLUMN_COUNT = 5;

  private static final int COL_NAME = 0;
  private static final int COL_DESCRIPTION = 1;
  private static final int COL_TYPE = 2;
  private static final int COL_VALUE = 3;
  private static final int COL_REQUIRED = 4;

  public static final String CAPTION_NAME = "Variable Name";
  public static final String CAPTION_DESCRIPTION = "Description";
  public static final String CAPTION_TYPE = "Type";
  public static final String CAPTION_VALUES = "Values";
  public static final String CAPTION_REQUIRED = "Required";

  private List parameters = new ArrayList(5);
  private final List deleted = new ArrayList(5);
  private int buildID = BuildConfig.UNSAVED_ID;
  private final StartParameterType parameterType;


  public ManualStartSettingsTable(final String title, final StartParameterType parameterType) {
    super(COLUMN_COUNT, true);
    setTitle(title);
    this.parameterType = parameterType;
    this.setMoveRowCommandsVisible(true);
  }


  /**
   * Sets user to e-mail map
   *
   * @param parameters
   */
  public void populate(final List parameters) {
    this.parameters = parameters;
    populate();
  }


  /**
   * Returns modified list of user to e-mail mappings
   *
   * @return list of user to e-mail mappings
   */
  public List getRunParameters() {
    return parameters;
  }


  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();

    // validate rows
    final ArrayList errors = new ArrayList(5);
    for (int index = 0, n = getRowCount(); index < n; index++) {
      final Component[] row = getRow(index);
      AbstractFlatTable.validateColumnNotBlank(errors, index, CAPTION_NAME, (Field) row[COL_NAME]);
      AbstractFlatTable.validateColumnNotBlank(errors, index, CAPTION_DESCRIPTION, (Field) row[COL_DESCRIPTION]);
      if (((CodeNameDropDown) row[COL_TYPE]).getCode() != StartParameter.PRESENTATION_SINGLE_VALUE) {
        validateColumnNotBlank(errors, index, CAPTION_VALUES, (Field) row[COL_VALUE]);
      }

      validateVariableName(errors, (Field) row[COL_NAME]);
    }

    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }

    return true;
  }


  /**
   * Saves table data.
   */
  public boolean save() {
    // validate
    ArgumentValidator.validateBuildIDInitialized(buildID);

    // delete deleted
    for (final Iterator iter = deleted.iterator(); iter.hasNext();) {
      final StartParameter startParameter = (StartParameter) iter.next();
      if (startParameter.getID() != StartParameter.UNSAVED_ID) {
        ConfigurationManager.getInstance().delete(startParameter);
      }
    }

    // save modified
    for (int index = 0, n = getRowCount(); index < n; index++) {
      final Component[] row = getRow(index);
      final StartParameter startParameter = (StartParameter) parameters.get(index);
      if (isRowNewAndBlank(startParameter, row)) {
        continue;
      }
      if (startParameter.getBuildID() == BuildConfig.UNSAVED_ID) {
        startParameter.setBuildID(buildID);
        startParameter.setType(parameterType.byteValue());
        startParameter.setModifiable(true);
        startParameter.setEnabled(true);
      }
      startParameter.setDescription(((AbstractInput) row[COL_DESCRIPTION]).getValue());
      startParameter.setName(((AbstractInput) row[COL_NAME]).getValue());
      startParameter.setPresentation((byte) ((CodeNameDropDown) row[COL_TYPE]).getCode());
      startParameter.setValue(((AbstractInput) row[COL_VALUE]).getValue());
      startParameter.setRequired(((CheckBox) row[COL_REQUIRED]).isChecked());
      startParameter.setOrder(index);
      ConfigurationManager.getInstance().save(startParameter);
    }
    return true;
  }


  /**
   * Returns true if a row is new and blank
   *
   * @param startParameter
   * @param row
   */
  private static boolean isRowNewAndBlank(final StartParameter startParameter, final Component[] row) {
    return startParameter.getBuildID() == BuildConfig.UNSAVED_ID
            && StringUtils.isBlank(((AbstractInput) row[COL_NAME]).getValue())
            && StringUtils.isBlank(((AbstractInput) row[COL_DESCRIPTION]).getValue())
            && StringUtils.isBlank(((AbstractInput) row[COL_VALUE]).getValue())
            ;
  }

  // =============================================================================================
  // == Table lifecycle methods                                                                 ==
  // =============================================================================================


  /**
   * This notification method is called when a row is deleted.
   */
  public void notifyRowDeleted(final int deletedRowIndex) {
    deleted.add(parameters.remove(deletedRowIndex));
  }


  /**
   * This notification method is called when a new row is added.
   * Implementing class can use it to keep track of deleted rows
   */
  public void notifyRowAdded(final int addedRowIndex) {
    parameters.add(addedRowIndex, new StartParameter());
  }


  /**
   */
  protected Component[] makeHeader() {
    final Component[] header = new Component[COLUMN_COUNT];
    header[COL_NAME] = new TableHeaderLabel(CAPTION_NAME, 180);
    header[COL_DESCRIPTION] = new TableHeaderLabel(CAPTION_DESCRIPTION, 180);
    header[COL_TYPE] = new TableHeaderLabel(CAPTION_TYPE, 140);
    header[COL_VALUE] = new TableHeaderLabel(CAPTION_VALUES, 200);
    header[COL_REQUIRED] = new TableHeaderLabel(CAPTION_REQUIRED, 20);
    return header;
  }


  /**
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= parameters.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final Component[] row = getRow(rowIndex);
    ((AbstractInput) row[COL_NAME]).setValue(get(rowIndex).getName());
    ((AbstractInput) row[COL_DESCRIPTION]).setValue(get(rowIndex).getDescription());
    ((AbstractInput) row[COL_VALUE]).setValue(get(rowIndex).getValue());
    ((CodeNameDropDown) row[COL_TYPE]).setCode(get(rowIndex).getPresentation());
    ((CheckBox) row[COL_REQUIRED]).setChecked(get(rowIndex).isRequired());
    return TBL_ROW_FETCHED;
  }


  /**
   * Helper method to get typed object out of the list
   *
   * @param index
   */
  private StartParameter get(final int index) {
    return (StartParameter) parameters.get(index);
  }


  protected Component[] makeRow(final int rowIndex) {
    final Component[] row = new Component[COLUMN_COUNT];
    row[COL_NAME] = new CommonField(100, 30);
    row[COL_DESCRIPTION] = new CommonField(100, 25);
    row[COL_TYPE] = new ManualStartParameterPresentationDropDown();
    row[COL_VALUE] = new CommonField(1024, 30);
    row[COL_REQUIRED] = makeRequiredCheckBox();
    return row;
  }


  /**
   * {@inheritDoc}
   */
  protected final void notifyRowMoved(final int selectedRow, final int count) {

    final Object remove = parameters.remove(selectedRow);
    parameters.add(selectedRow + count, remove);
  }

  public void load(final BuildConfig buildConfig) {
    populate(ConfigurationManager.getInstance().getStartParameters(parameterType, buildConfig.getBuildID()));
  }


  /**
   * Helper method to create a CheckBox to fill "Required" column.
   */
  private static CheckBox makeRequiredCheckBox() {
    final CheckBox cb = new CheckBox();
    cb.setAlignX(Layout.CENTER);
    return cb;
  }


  public String toString() {
    return "ManualStartSettingsTable{" +
            "buildID=" + buildID +
            ", deleted=" + deleted +
            ", parameters=" + parameters +
            ", parameterType=" + parameterType +
            "} " + super.toString();
  }
}
