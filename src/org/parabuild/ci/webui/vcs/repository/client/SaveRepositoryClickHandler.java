package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.parabuild.ci.webui.vcs.repository.common.ErrorDialogBox;

/**
 * A click handler for "Save" button.
 */
final class SaveRepositoryClickHandler implements ClickHandler {

  private final RepositoryDialogBox repositoryDialogBox;


  /**
   * Creates {@link SaveRepositoryClickHandler}.
   *
   * @param repositoryDialogBox the dialog box for editing repository information.
   */
  SaveRepositoryClickHandler(final RepositoryDialogBox repositoryDialogBox) {
    this.repositoryDialogBox = repositoryDialogBox;
  }


  /**
   * Processes button click.
   *
   * @param event the event to process.
   */
  @SuppressWarnings("rawtypes")
  public void onClick(final ClickEvent event) {

    // (1) Create the client proxy.
    final RepositoryServiceAsync repositoryService = GWT.create(RepositoryService.class);

    // (2) Create an asynchronous callback to handle the result.
    final AsyncCallback callback = new AsyncCallback() {
      public void onFailure(final Throwable caught) {

        // Show error dialog
        final ErrorDialogBox errorDialogBox = new ErrorDialogBox();
        errorDialogBox.setErrorMessage(caught.getMessage());
        errorDialogBox.show();
      }


      @Override
      public void onSuccess(final Object result) {

        // Close the dialog
        repositoryDialogBox.hide();
      }
    };

    // (3) Make the call. Control flow will continue immediately and later
    // 'callback' will be invoked when the RPC completes.
    final VCSRepositoryClientVO repositoryVO = repositoryDialogBox.getRepositoryVO();
    repositoryService.saveRepository(repositoryVO, callback);
  }
}
