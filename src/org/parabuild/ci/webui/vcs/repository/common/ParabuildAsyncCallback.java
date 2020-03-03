package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An async callback that displays a error dialog box if a failure occured.
 *
 * @param <T>
 */
public abstract class ParabuildAsyncCallback<T> implements AsyncCallback<T> {

  /**
   * Handles a failure by displaying a error box.
   *
   * @param caught an error.
   */
  @Override
  public final void onFailure(final Throwable caught) {

    // Show error dialog
    final ErrorDialogBox errorDialogBox = new ErrorDialogBox();
    errorDialogBox.setErrorMessage(caught.getMessage());
    errorDialogBox.center();
    errorDialogBox.show();
  }
}
