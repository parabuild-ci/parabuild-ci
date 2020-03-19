package org.parabuild.ci.webui.vcs.repository.client.server;

/**
 * An empty panel to show when VCS configuration is not supported yet.
 */
public final class DummyServerAttributePanel extends VCSServerAttributePanel {

  /**
   * Creates {@link ParabuildFlexTable}.
   *
   * @param columnCount number of columns managed by {@link #flexTableIterator()}.
   */
  public DummyServerAttributePanel(final int columnCount) {

    super(columnCount);
  }
}
