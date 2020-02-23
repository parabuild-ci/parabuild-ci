package org.parabuild.ci.webui.vcs.repository.client;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A click handler to launch the {@link VCSRepositoryServerDialogBox}.
 */
final class AddVCSServerClickHandler implements ClickHandler {

  /**
   * Launches the {@link VCSRepositoryServerDialogBox}.
   *
   * @param event the click event.
   */
  @Override
  public void onClick(final ClickEvent event) {

    final VCSRepositoryServerDialogBox repositoryServerDialogBox = new VCSRepositoryServerDialogBox("Add Server");
    RootPanel.get().add(repositoryServerDialogBox);
    repositoryServerDialogBox.center();
    repositoryServerDialogBox.show();
  }
}
