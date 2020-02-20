package org.parabuild.ci.webui.vcs.repository.client;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A click handler to launch the {@link RepositoryDialogBox}.
 */
final class AddRepositoryClickHandler implements ClickHandler {

  /**
   * Launches the {@link RepositoryDialogBox}.
   *
   * @param event the click event.
   */
  @Override
  public void onClick(final ClickEvent event) {

    final RepositoryDialogBox repositoryDialogBox = new RepositoryDialogBox("Add Repository");
    RootPanel.get().add(repositoryDialogBox);
    repositoryDialogBox.center();
    repositoryDialogBox.show();
  }
}
