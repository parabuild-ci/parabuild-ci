package org.parabuild.ci.webui.repository.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Client-side Java source for the entry-point class.
 */
public final class RepositoryClient implements EntryPoint {

  @Override
  public void onModuleLoad() {

    // Create a repository dialog
    final RepositoryDialogBox repositoryDialogBox = new RepositoryDialogBox();

    // Add it to the home page
    RootPanel.get().add(repositoryDialogBox);
  }
}
