package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.parabuild.ci.webui.vcs.repository.common.ErrorDialogBox;

/**
 * A click handler for "Save" button.
 */
final class SaveVCSServerClickHandler implements ClickHandler {

  private final VCSRepositoryServerDialogBox repositoryServerDialogBox;


  /**
   * Creates {@link SaveVCSServerClickHandler}.
   *
   * @param repositoryServerDialogBox the dialog box for editing repository information.
   */
  SaveVCSServerClickHandler(final VCSRepositoryServerDialogBox repositoryServerDialogBox) {
    this.repositoryServerDialogBox = repositoryServerDialogBox;
  }


  /**
   * Processes button click.
   *
   * @param event the event to process.
   */
  @SuppressWarnings("rawtypes")
  public void onClick(final ClickEvent event) {

    // (1) Create the client proxy.
    final VCSServerServiceAsync serverServiceAsync = GWT.create(VCSServerService.class);

    // (2) Create an asynchronous callback to handle the result.
    final AsyncCallback callback = new SaveServerAsyncCallback();

    // (3) Make the call. Control flow will continue immediately and later
    // 'callback' will be invoked when the RPC completes.
    final VCSServerClientVO serverVO = repositoryServerDialogBox.getServerVO();
    serverServiceAsync.saveServer(serverVO, callback);
  }


  private class SaveServerAsyncCallback implements AsyncCallback {

    public void onFailure(final Throwable caught) {

      // Show error dialog
      final ErrorDialogBox errorDialogBox = new ErrorDialogBox();
      errorDialogBox.setErrorMessage(caught.getMessage());
      errorDialogBox.center();
      errorDialogBox.show();
    }


    @Override
    public void onSuccess(final Object result) {

      // Close the dialog
      repositoryServerDialogBox.hide();

      // Refresh
      Window.Location.reload();
    }
  }
}
