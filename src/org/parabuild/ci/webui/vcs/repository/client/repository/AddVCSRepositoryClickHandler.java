package org.parabuild.ci.webui.vcs.repository.client.repository;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A click handler to launch the {@link VCSRepositoryDialogBox}.
 */
final class AddVCSRepositoryClickHandler implements ClickHandler {

  /**
   * Launches the {@link VCSRepositoryDialogBox}.
   *
   * @param event the click event.
   */
  @Override
  public void onClick(final ClickEvent event) {

    final VCSRepositoryDialogBox repositoryDialogBox = new VCSRepositoryDialogBox("Add Repository");
    RootPanel.get().add(repositoryDialogBox);
    repositoryDialogBox.center();
    repositoryDialogBox.show();
  }
}
