package org.parabuild.ci.webui.vcs.repository.client.server;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A click handler to launch the {@link VCSServerDialogBox}.
 */
final class AddVCSServerClickHandler implements ClickHandler {

  /**
   * Launches the {@link VCSServerDialogBox}.
   *
   * @param event the click event.
   */
  @Override
  public void onClick(final ClickEvent event) {

    final VCSServerDialogBox repositoryServerDialogBox = new VCSServerDialogBox("Add Server");
    RootPanel.get().add(repositoryServerDialogBox);
    repositoryServerDialogBox.center();
    repositoryServerDialogBox.show();
  }
}
