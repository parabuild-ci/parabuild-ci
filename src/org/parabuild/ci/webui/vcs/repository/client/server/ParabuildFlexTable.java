package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.user.client.ui.FlexTable;
import org.parabuild.ci.webui.vcs.repository.common.FlexTableIterator;

/**
 * A flex table that allows adding Widgets as a sequence of add() calls while automatically moving column and row forward.
 *
 * @see #flexTableIterator
 */
public class ParabuildFlexTable extends FlexTable {

  /**
   * The cell iterator.
   */
  private final FlexTableIterator flexTableIterator;


  /**
   * Creates {@link ParabuildFlexTable}.
   *
   * @param columnCount number of columns managed by {@link #flexTableIterator}.
   */
  public ParabuildFlexTable(final int columnCount) {

    flexTableIterator = new FlexTableIterator(this, columnCount);
  }


  /**
   * Return the iterator that can be used to manage adding cells to the table.
   *
   * @return the iterator that can be used to manage adding cells to the table.
   * @see #ParabuildFlexTable(int)
   */
  public final FlexTableIterator flexTableIterator() {

    return flexTableIterator;
  }
}
