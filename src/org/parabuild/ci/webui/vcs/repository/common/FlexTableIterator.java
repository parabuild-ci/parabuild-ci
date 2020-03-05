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

    // Calculate next row and column
    final int rowCount = flexTable.getRowCount();
    int column = 0;
    int row;
    if (rowCount <= 0) {
      row = 0;
    } else {
      row = rowCount - 1;
      final int cellCount = flexTable.getCellCount(row);
      if (cellCount >= maxCells) {
        column = 0;
        row++;
      } else {
        column = cellCount;
      }
    }

    // Set widget
    flexTable.setWidget(row, column, widget);

    return this;
  }


  /**
   * Adds a pair of widgets.
   *
   * @param widget0 the first widget.
   * @param widget1 the second widget.
   *
   * @return this iterator to support chaining.
   */
  public FlexTableIterator addPair(final Widget widget0, final Widget widget1) {
    add(widget0);
    add(widget1);
    return this;
  }
}
