package org.parabuild.ci.webui.vcs.repository.client.server;

/**
 * VCS server attribute panel serves as a contrainer for entering captions, fields
 */
public class VCSServerAttributePanel extends ParabuildFlexTable {

  /**
   * Creates {@link ParabuildFlexTable}.
   *
   * @param columnCount number of columns managed by {@link #flexTableIterator()}.
   */
  public VCSServerAttributePanel(final int columnCount) {
    super(columnCount);
  }
}
