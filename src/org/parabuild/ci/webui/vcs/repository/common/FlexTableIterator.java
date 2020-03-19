package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class FlexTableIterator {

  private final FlexTable flexTable;
  private final int maxCells;


  public FlexTableIterator(final FlexTable layoutPanel, final int maxCells) {

    this.flexTable = layoutPanel;
    this.maxCells = maxCells;
  }


  public FlexTableIterator add(final Widget widget) {

    return add(widget, 1);
  }


  /**
   * Adds a pair of widgets.
   *
   * @param widget0 the first widget.
   * @param widget1 the second widget.
   * @return this iterator to support chaining.
   */
  public FlexTableIterator addPair(final Widget widget0, final Widget widget1) {
    add(widget0);
    add(widget1);
    return this;
  }


  public FlexTableIterator add(final Widget widget, final int columnSpan) {


    // Calculate next row and column
    final int rowCount = flexTable.getRowCount();
    final int column;
    int row;
    if (rowCount <= 0) {

      row = 0;
      column = 0;
    } else {

      row = rowCount - 1;


      final int columnCount = getColumnCount(row);
      if (columnCount >= maxCells) {

        // Beyond last column, move to the beginning of the next row
        column = 0;
        row++;
      } else {

        // Deal with column spans
        if (columnSpan <= 1) {

          // Single column
          column = columnCount;
        } else {

          // Span
          if (columnCount + columnSpan >= maxCells) {
            column = 0;
            row++;
          } else {

            column = columnCount;
          }
        }
      }
    }

    // Figure out span

    // Set widget
    flexTable.setWidget(row, column, widget);

    // Set col span
    if (columnSpan > 1) {

      final FlexTable.FlexCellFormatter flexCellFormatter = flexTable.getFlexCellFormatter();
      flexCellFormatter.setColSpan(row, column, columnSpan);
    }

    return this;
  }


  private int getColumnCount(final int row) {

    int result = 0;
    final FlexTable.FlexCellFormatter flexCellFormatter = flexTable.getFlexCellFormatter();
    final int cellCount = flexTable.getCellCount(row);
    for (int cell = 0; cell < cellCount; cell++) {
      final int colSpan = flexCellFormatter.getColSpan(row, cell);
      result += colSpan;
    }

    return result;
  }
}
