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
}
