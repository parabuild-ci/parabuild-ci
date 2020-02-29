package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * A click handler for "Save" button.
 */
final class SaveVCSRepositoryClickHandler implements ClickHandler {

  private final VCSRepositoryDialogBox vcsRepositoryDialogBox;


  /**
   * Creates {@link SaveVCSRepositoryClickHandler}.
   *
   * @param vcsRepositoryDialogBox the dialog box for editing repository information.
   */
  SaveVCSRepositoryClickHandler(final VCSRepositoryDialogBox vcsRepositoryDialogBox) {

    this.vcsRepositoryDialogBox = vcsRepositoryDialogBox;
  }


  /**
   * Processes button click.
   *
   * @param event the event to process.
   */
  @SuppressWarnings("rawtypes")
  public void onClick(final ClickEvent event) {

    // (1) Create the client proxy.
    final VCSRepositoryServiceAsync repositoryService = GWT.create(VCSRepositoryService.class);

    // (2) Create an asynchronous callback to handle the result.
    final AsyncCallback callback = new SaveDialogAsyncCallback(vcsRepositoryDialogBox);

    // (3) Make the call. Control flow will continue immediately and later
    // 'callback' will be invoked when the RPC completes.
    final VCSRepositoryClientVO repositoryVO = vcsRepositoryDialogBox.getRepositoryVO();
    repositoryService.saveRepository(repositoryVO, callback);
  }


  private static class SaveDialogAsyncCallback<Void> extends ParabuildAsyncCallback<Void> {


    private final DialogBox dialogBox;


    public SaveDialogAsyncCallback(final DialogBox dialogBox) {
      this.dialogBox = dialogBox;
    }


    /**
     * Called when an asynchronous call completes successfully.
     *
     * @param result the return value of the remote produced call
     */
    @Override
    public void onSuccess(final Object result) {

      // Close the dialog
      dialogBox.hide();

      // Refresh
      Window.Location.reload();
    }
  }
}
