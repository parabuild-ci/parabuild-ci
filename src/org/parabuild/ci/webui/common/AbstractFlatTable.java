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
package org.parabuild.ci.webui.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.util.MailUtils;
import org.parabuild.ci.util.StringUtils;
import viewtier.ui.Border;
import viewtier.ui.CheckBox;
import viewtier.ui.Color;
import viewtier.ui.Component;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class implements a simple flat table to be reused in the
 * application.
 * <p/>
 * Classes inheriting this table should implement a constructor
 * providing number of columns and implement makeHeader, fetchRow
 * and getRowCount abstract methods.
 *
 * @noinspection AssignmentToForLoopParameter
 */
public abstract class AbstractFlatTable extends MessagePanel {

  private static final long serialVersionUID = 8695672343405760041L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(AbstractFlatTable.class); // NOPMD

  public static final int DEFAULT_HEADER_HEIGHT = 25;
  public static final int DEFAULT_ROW_HEIGHT = 30;

  public static final int ROW_NEW = 0x0002;
  public static final int TBL_NO_MORE_ROWS = 2;
  public static final int TBL_ROW_FETCHED = 1;


  private boolean editable = false;
  private boolean headerVisible = true;
  private boolean rightMostCellBorderVisible = false;
  private Color gridColor = Pages.TABLE_GRID_COLOR;
  private Color headerBackground = Pages.TABLE_COLOR_HEADER_BG;
  private Color headerForeground = Pages.TABLE_COLOR_HEADER_FG;
  private Color oddRowBackground = Pages.TABLE_COLOR_ODD_ROW_BACKGROUND;
  //  private Color oddRowBackground = new Color(0xFBFBEE);
  private Color evenRowBackground = Pages.TABLE_COLOR_EVEN_ROW_BACKGROUND;
  private GridIterator gridIter = null;
  private int columnCount = 0;
  private int gridWidth = 1;
  private int headerHeight = DEFAULT_HEADER_HEIGHT;
  private int rowHeight = DEFAULT_ROW_HEIGHT;
  private TableColumn[] columns = null;

  private final List rows = new ArrayList(20);

  /**
   * Optional edit commands.
   */
  private TableCommands tableCommands = null;

  /**
   * If true table will alternate colors automatically.
   */
  private boolean automaticRowBackground = true;


  /**
   * Constructor - creates an instance of flat table with given
   * number of columns
   *
   * @param columnCount number of columns ih the table
   * @param editable    true if editing is allowed
   */
  public AbstractFlatTable(final int columnCount, final boolean editable) {
    this(columnCount, editable, true);
  }


  /**
   * Constructor - creates an instance of flat table with given
   * number of columns
   *
   * @param columnCount number of columns ih the table
   * @param editable    true if editing is allowed
   */
  public AbstractFlatTable(final int columnCount, final boolean editable, final boolean createHeaderOnStartup) {

    this.editable = editable;
    this.columnCount = columnCount;

    int gridSize = columnCount;
    if (editable) {
      gridSize++; // add a column check box
      // add commands to the bottom of the table
      //noinspection OverriddenMethodCallInConstructor,OverridableMethodCallInConstructor
      tableCommands = makeEditableCommands(); // NOPMD ConstructorCallsOverridableMethod
      //noinspection OverridableMethodCallInConstructor
      final Component cmpTableCommands = tableCommands.getComponent();
      cmpTableCommands.setAlignX(Layout.LEFT);
      super.add(cmpTableCommands); // add next to main content
    }
    getUserPanel().setWidth("100%");
    gridIter = new GridIterator(super.getUserPanel(), gridSize);
    if (createHeaderOnStartup) {
      populate(0);
    }
  }


  public static boolean validateColumnNotBlank(final List errors, final int rowIndex, final String name, final String value) {

    if (StringUtils.isBlank(value)) {
      errors.add("Column \"" + name + "\" at row number " + (rowIndex + 1) + " is blank. This column can not be blank.");
      return false;
    } else {
      return true;
    }
  }


  public static void validateColumnValidEmail(final List errors, final int rowIndex, final String name, final String value) {

    if (!MailUtils.isValidEmail(value)) {
      errors.add("Column \"" + name + "\" at row number " + (rowIndex + 1) + " is not a valid e-mail.");
    }
  }


  public static void validateColumnNotBlank(final List errors, final int rowIndex, final String name, final Field field) {
    validateColumnNotBlank(errors, rowIndex, name, field.getValue());
  }


  public static void validateVariableName(final List errors, final Field field) {

    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    if (scm.isCustomVariableNameValidation()) {
      final String regex = scm.getCustomVariableNameRegex();
      if (!Pattern.compile(regex).matcher(field.getValue()).matches()) {
        errors.add("Variable name \"" + field.getValue() + "\" does not match a custom regex defined in the system user interface properties: " + regex);
      }
    } else {
      // Default build name format validation
      if (!StringUtils.isValidStrictName(field.getValue())) {
        errors.add("Variable name can contain only alphanumeric characters, \"-\" and \"_\".");
      }
    }
  }


  /**
   * If true table will alternate row colors automatically.
   *
   * @param automaticRowBackground
   */
  public final void setAutomaticRowBackground(final boolean automaticRowBackground) {
    this.automaticRowBackground = automaticRowBackground;
  }


  /**
   * Shows/hides column.
   *
   * @param index           column index
   * @param columnIsVisible true if visible, false if is
   *                        invisible.
   */
  public final void setColumnVisible(final int index, final boolean columnIsVisible) {
    columns[index].setVisible(columnIsVisible);
  }


  /**
   * Hides or makes visible "Add row" command if the table in
   * editable mode. Otherwise does nothing.
   */
  public final void setAddCommandVisible(final boolean visible) {
    if (tableCommands == null) return;
    tableCommands.setAddRowCommandVisible(visible);
  }


  /**
   * Hides or makes visible "Insert row" command if the table in
   * editable mode. Otherwise does nothing.
   */
  public final void setInsertCommandVisible(final boolean visible) {
    if (tableCommands == null) return;
    tableCommands.setInsertRowCommandVisible(visible);
  }


  /**
   * Hides or makes visible "Move row" command if the table in
   * editable mode. Otherwise does nothing.
   */
  public final void setMoveRowCommandsVisible(final boolean visible) {
    if (tableCommands == null) return;
    tableCommands.setMoveRowCommandsVisible(visible);
  }


  /**
   * Sets grid color
   *
   * @param gridColor
   */
  public final void setGridColor(final Color gridColor) {
    this.gridColor = gridColor;
  }


  /**
   * Sets grid width for both horizontal an vertical grids.
   *
   * @param gridWidth
   */
  public final void setGridWidth(final int gridWidth) {
    this.gridWidth = gridWidth;
  }


  /**
   * @return true if the table support adding and removing rows
   */
  public final boolean isEditable() {
    return editable;
  }


  /**
   * Sets header background color
   *
   * @param color
   */
  public final void setHeaderBackground(final Color color) {
    this.headerBackground = color;
  }


  /**
   * Sets header foreground color
   *
   * @param color
   */
  public final void setHeaderForeground(final Color color) {
    this.headerForeground = color;
  }


  /**
   * Sets header height
   *
   * @param headerHeight int to set
   */
  public final void setHeaderHeight(final int headerHeight) {
    this.headerHeight = headerHeight;
  }


  /**
   * Sets row height
   */
  public final void setRowHeight(final int rowHeight) {
    this.rowHeight = rowHeight;
  }


  /**
   * Sets odd rows background color
   *
   * @param color
   */
  public final void setOddRowBackground(final Color color) {
    this.oddRowBackground = color;
  }


  /**
   * Sets even rows background color
   *
   * @param color
   */
  public final void setEvenRowBackground(final Color color) {
    this.evenRowBackground = color;
  }


  /**
   * If false, table is displayed without a header
   *
   * @param headerVisible
   */
  public final void setHeaderVisible(final boolean headerVisible) {
    this.headerVisible = headerVisible;
  }


  /**
   * If true, rightmost cells will have right border shown.
   *
   * @param rightMostCellBorderVisible
   */
  public final void setRightMostCellBorderVisible(final boolean rightMostCellBorderVisible) {
    this.rightMostCellBorderVisible = rightMostCellBorderVisible;
  }


  /**
   */
  protected abstract Component[] makeHeader();


  /**
   * Makes row, should be implemented by successor class
   */
  protected abstract Component[] makeRow(int rowIndex);


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
  protected abstract int fetchRow(int rowIndex, int rowFlags);


  /**
   * @return number of rows in the table
   */
  public final int getRowCount() {
    return rows.size();
  }


  /**
   * Populates table
   */
  public void populate() {
    populate(Integer.MAX_VALUE);
  }


  /**
   * Populates table with rowCount number of rows
   */
  public final void populate(final int rowCount) {
    super.getUserPanel().clear();
    rows.clear();
    gridIter.reset();
    addHeader();
    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
      if (!addRow(rowIndex, ROW_NEW, true)) break;
    }
  }


  private boolean addRow(final int rowIndex, final int rowFlags, final boolean doFetch) {
    final Component[] rowComponents = makeRow(rowIndex);
    final TableRow row = new TableRow();
    row.setRowComponents(rowComponents);
    rows.add(rowIndex, row);


    if (doFetch && fetchRow(rowIndex, rowFlags) == TBL_NO_MORE_ROWS) {
      rows.remove(rowIndex);
      return false;
    }

    // add check box, if necessary
    if (editable) {
      final CheckBox rowCheckBox = new CheckBox();
      row.setRowSelectCheckBox(rowCheckBox);
      addCell(rowCheckBox);
    }

    // add row columns
    for (int colIndex = 0; colIndex < columnCount; colIndex++) {
      final Component cell = rowComponents[colIndex];
      final TableColumn column = columns[colIndex];
      if (column.isWidthRelative()) {
        cell.setWidth(column.getRelativeWidth());
      } else {
        cell.setWidth(column.getWidth());
      }
      if (rowHeight > 0) cell.setHeight(rowHeight);
      addCell(cell);
    }

    return true;
  }


  /**
   * This notification method is called when a row is deleted.
   * Implementing class can use it to keep track of deleted rows
   */
  public void notifyRowDeleted(final int deletedRowIndex) {
  }


  /**
   * This notification method is called when a new row is added.
   * Implementing class can use it to keep track of deleted rows
   */
  public void notifyRowAdded(final int addedRowIndex) {
  }


  /**
   * Adds row to the end of the table
   */
  public final int addRow() {
    final int newRowIndex = getRowCount();
    addRow(newRowIndex, ROW_NEW, false);
    notifyRowAdded(newRowIndex);
    return newRowIndex;
  }


  /**
   * Inserts above the first selected row. Does nothing is there is no a selected row.
   */
  public final void insertRow() {

    final int selectedRow = getSelectedRow();

    // process insertion if there was a row selected.
    if (selectedRow < 0) {
      return;
    }

    final Component[] newRowComponents = makeRow(selectedRow);
    final TableRow row = new TableRow();
    row.setRowComponents(newRowComponents);
    if (editable) {
      final CheckBox rowCheckBox = new CheckBox();
      row.setRowSelectCheckBox(rowCheckBox);
    }
    rows.add(selectedRow, row);

    // notify, overriding method *must* alter it's model.
    notifyRowAdded(selectedRow);

    // NOTE: vimeshev - with no better option, we clean-up
    // the content panel and re-insert all the columns.

    // reset
    reset();
  }


  /**
   * Returns row components. This method is to be used by
   * fetchRow
   */
  public final Component[] getRow(final int rowIndex) {
    return ((TableRow) rows.get(rowIndex)).getRowComponents();
  }


  /**
   * @return table column count
   */
  public final int columnCount() {
    return columnCount;
  }


  /**
   * Adds header
   */
  private void addHeader() {
    // get header and validate
    final Component[] headerColumns = makeHeader();
    if (headerColumns.length != columnCount)
      throw new IllegalStateException("Length of header array \"" + headerColumns.length + "\" is not equal number of columns \"" + columnCount + '\"');

    // fill columns' widths array
    columns = new TableColumn[columnCount()];
    for (int index = 0; index < columnCount; index++) {
      columns[index] = new TableColumn();
      final Component headerColumn = headerColumns[index];
      if (headerColumn.widthIsRelative()) {
        columns[index].setRelativeWidth(headerColumn.getWidth() + "%");
      } else {
        int columnWidth = headerColumn.getWidth();
        if (columnWidth <= 0) columnWidth = 50;
        columns[index].setWidth(headerColumn.getWidth());
      }
    }

    // add header content
    if (!headerVisible) return;

    // noname column for column checkbox
    if (editable) {
      final Label checkBoxLabel = new Label("");
      checkBoxLabel.setWidth(20); // REVIEWME: simeshev@parabuilci.org -> width
      addHeaderCell(checkBoxLabel);
    }

    for (int index = 0; index < columnCount; index++) {
      addHeaderCell(headerColumns[index]);
    }
  }


  /**
   * Deletes selected rows
   */
  protected final void deleteSelectedRows() {
    final Panel panel = super.getUserPanel(); // holds row components
    for (int index = 0; index < rows.size(); index++) {
//      if (log.isDebugEnabled()) log.debug("index: " + index);
//      if (log.isDebugEnabled()) log.debug("rows.size(): " + rows.size());
      final TableRow row = (TableRow) rows.get(index);
      final CheckBox selectionCheckBox = row.getRowSelectCheckBox();
      // Find selected rows.
      if (selectionCheckBox.isChecked()) {
        // delete row
        // remove checkbox
        panel.remove(selectionCheckBox);
        for (int columnIndex = 0; columnIndex < row.columnCount(); columnIndex++) {
          // remove components from list of rows
          final Component rowComponent = row.getColumnComponent(columnIndex);
          panel.remove(rowComponent);
        }
        // remove row from the row list
        rows.remove(index);
        // notification to implementing class
        notifyRowDeleted(index);
        // adjust to the fact the next row has become current
        if (!rows.isEmpty()) index--;
      }
    }
  }


  /**
   * Helper method to sequentially add cell to the grid
   *
   * @param cell
   */
  private void addCell(final Component cell) {
    setCellBackground(cell);
    setGrid(cell);
    gridIter.add(cell);
  }


  /**
   * Sets cell's background
   *
   * @param cell
   */
  private void setCellBackground(final Component cell) {
    if (automaticRowBackground) {
      if (gridIter.getCumulativeLayout().positionY % 2 == 0) {
        cell.setBackground(oddRowBackground);
      } else {
        cell.setBackground(evenRowBackground);
      }
    }
  }


  /**
   * Helper method to add header cell
   *
   * @param headerComponent
   */
  private void addHeaderCell(final Component headerComponent) {
    headerComponent.setBackground(headerBackground);
    headerComponent.setForeground(headerForeground);
    headerComponent.setHeight(headerHeight);
    headerComponent.setAlignY(Layout.CENTER);
    setGrid(headerComponent);
    gridIter.add(headerComponent);
  }


  /**
   * Helper method to set up table grid
   *
   * @param component
   */
  private void setGrid(final Component component) {
    int border = 0;
    final int currentPositionX = gridIter.getCumulativeLayout().positionX;
    if (currentPositionX > 0 && currentPositionX <= this.columnCount - 1) {
      border |= Border.LEFT;
    }
    if (rightMostCellBorderVisible && currentPositionX == this.columnCount - 1) {
      border |= Border.RIGHT;
    }
    border |= Border.BOTTOM;
    component.setBorder(border, gridWidth, gridColor);
  }


  /**
   * Adds footer with editable commands. This method provides
   * default implementation. It can be overridden by successors to
   * provide specialized commands
   */
  protected TableCommands makeEditableCommands() {
    return new DefaultTableCommands(this);
  }


  protected final TableCommands getTableCommands() {
    return tableCommands;
  }


  /**
   * @param rowIndex
   * @return true if row with a given index is selected.
   */
  protected final boolean isRowSelected(final int rowIndex) {
    return ((TableRow) rows.get(rowIndex)).getRowSelectCheckBox().isChecked();
  }

    /**
     * Moves a selected row implementors that a row has been moved.
     * @param count number of positions the row moved.
     */
    public void moveSelectedRow(final int count) {

        final int selectedRow = getSelectedRow();

        // process move if there was a row selected.
        if (selectedRow < 0) {
            return;
        }

        // Check if this is a top or bottom of the list
        final int newPosition = selectedRow + count;
        if (newPosition < 0 || newPosition >= rows.size()) {
          return;
        }

        final TableRow rowToMove = (TableRow) rows.remove(selectedRow);
        rows.add(newPosition, rowToMove);

        // notify, overriding method *must* alter it's model.
        notifyRowMoved(selectedRow, count);

        // NOTE: vimeshev - with no better option, we clean-up
        // the content panel and re-insert all the columns.

        // reset
        reset();
    }

    /**
     * Notifies implementors that a row has been moved.
     *
     * @param selectedRow the original row index.
     * @param count number of positions the row moved.
     */
    protected void notifyRowMoved(final int selectedRow, final int count) {

    }

    private void reset() {

    getUserPanel().clear();
    gridIter.reset();

    // add header
    addHeader();

    // show rows
    for (int i = 0; i < rows.size(); i++) {
      final TableRow tableRow = (TableRow) rows.get(i);

      // add check box, if necessary
      final CheckBox rowSelectCheckBox = tableRow.getRowSelectCheckBox();
      if (rowSelectCheckBox != null) {
        rowSelectCheckBox.setChecked(false);
        addCell(rowSelectCheckBox);
      }

      // add columns
      final Component[] rowComponents = tableRow.getRowComponents();
      for (int colIndex = 0; colIndex < columnCount; colIndex++) {
        final Component cell = rowComponents[colIndex];
        final TableColumn column = columns[colIndex];
        if (column.isWidthRelative()) {
          cell.setWidth(column.getRelativeWidth());
        } else {
          cell.setWidth(column.getWidth());
        }
        if (rowHeight > 0) cell.setHeight(rowHeight);
        addCell(cell);
      }
    }
  }


    /**
     * Returns a selected row. Returns -1 if no row selected.
     *
     * @return selected row. Returns -1 if no row selected.
     */
    private int getSelectedRow() {

    //noinspection UNUSED_SYMBOL,UnusedDeclaration
    final Panel panel = super.getUserPanel(); // holds row components // NOPMD
    // Find selected rows.
    int selectedRow = -1;
    for (int index = 0, n = rows.size(); index < n; index++) {
      final TableRow row = (TableRow) rows.get(index);
      final CheckBox selectionCheckBox = row.getRowSelectCheckBox();
      if (selectionCheckBox.isChecked()) {
        selectedRow = index;
        break;
      }
    }
    return selectedRow;
  }

    /**
   * This interface defines an object containing controls for
   * editable tables such as "Add row" or "Delete selected".
   */
  public interface TableCommands {

    /**
     * @return a component containing controls.
     */
    Component getComponent();


    /**
     * @param visible true if add row command should be visible.
     */
    void setAddRowCommandVisible(final boolean visible);


    /**
     * @param visible true if insert command should be visible.
     */
    void setInsertRowCommandVisible(final boolean visible);

      void setMoveRowCommandsVisible(boolean visible);
  }


  /**
   * Placeholder for components composing a row
   */
  private static final class TableRow {

    private CheckBox rowSelectCheckBox = null;
    private Component[] rowComponents = null;


    /**
     * Returns checkbox responsible for holding row selection
     * data
     */
    public CheckBox getRowSelectCheckBox() {
      return rowSelectCheckBox;
    }


    public void setRowSelectCheckBox(final CheckBox rowSelectCheckBox) {
      this.rowSelectCheckBox = rowSelectCheckBox;
    }


    public Component[] getRowComponents() {
      // REVIEWME: simeshev@parabuilci.org - there could be better
      // design instead to returning an array.
      return rowComponents; // NOPMD
    }


    /**
     * @return number of columns on this row.
     */
    public int columnCount() {
      return rowComponents.length;
    }


    /**
     * Returns Component stored at the given index.
     *
     * @return Component stored at the given index.
     */
    public Component getColumnComponent(final int index) {
      return rowComponents[index];
    }


    public void setRowComponents(final Component[] rowComponents) { // NOPMD - "A user given array is stored directly"
      this.rowComponents = rowComponents;
    }
  }

  /**
   * Placeholder for table column definition.
   */
  private static final class TableColumn {

    private String header = null;
    private boolean visible = true;
    private int width = 50;
    private String relativeWidth = null;


    public String getHeader() {
      return header;
    }


    public void setHeader(final String header) {
      this.header = header;
    }


    public boolean isVisible() {
      return visible;
    }


    public void setVisible(final boolean visible) {
      this.visible = visible;
    }


    public int getWidth() {
      return width;
    }


    public void setWidth(final int width) {
      this.width = width;
    }


    public void setRelativeWidth(final String width) {
      this.relativeWidth = width;
    }


    public String getRelativeWidth() {
      return relativeWidth;
    }


    public boolean isWidthRelative() {
      return relativeWidth != null;
    }
  }
}
