package org.parabuild.ci.webui.repository.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Repository dialog box is responsible for editing and displaying Respository information.
 */
@SuppressWarnings("WeakerAccess")
public final class RepositoryDialogBox extends DialogBox {

  private final SimpleLayoutPanel layoutPanel = new SimpleLayoutPanel();
  private final Label lbDescription = new Label("Repository Description:");
  private final Label lbName = new Label("Repository name:");
  private final TextBox tbDescription = new TextBox();
  private final TextBox tbName = new TextBox();


  /**
   * Creates this {@link RepositoryDialogBox}.
   */
  public RepositoryDialogBox() {

    super(false, true);

    // Set the dialog box's caption.
    setText("Repository");

    // Enable animation.
    setAnimationEnabled(true);

    // Enable glass background.
    setGlassEnabled(true);

    // Create a container for fields
    layoutPanel.add(lbName);
    layoutPanel.add(tbName);
    layoutPanel.add(lbDescription);
    layoutPanel.add(tbDescription);

    // Add "Save" button
    final Button btnSave = new Button("Save", new SaveButtonClickHandler());
    layoutPanel.add(btnSave);

    // Add "Cancel" button
    final Button btnCancel = new Button("Cancel", new CancelButtonClickHandler());
    layoutPanel.add(btnCancel);

    // Add layout panel
    setWidget(layoutPanel);
  }


  /**
   * Returns the VO representing the repository being edited.
   *
   * @return the VO representing the repository being edited.
   */
  private RepositoryVO getRepositoryVO() {
    return null;
  }


  /**
   * A click handler for "Save" button.
   */
  private class SaveButtonClickHandler implements ClickHandler {

    public void onClick(final ClickEvent event) {

      // (1) Create the client proxy.
      final RepositoryServiceAsync repositoryService = GWT.create(RepositoryService.class);

      // (2) Create an asynchronous callback to handle the result.
      final AsyncCallback callback = new AsyncCallback() {
        public void onFailure(final Throwable caught) {
          // ... Show error dialog

          // ... Close the dialog
        }


        @Override
        public void onSuccess(final Object result) {

          // Close the dialog


        }
      };

      // (3) Make the call. Control flow will continue immediately and later
      // 'callback' will be invoked when the RPC completes.
      repositoryService.saveRepository(getRepositoryVO(), callback);
    }
  }

  /**
   * A click handler for "Save" button.
   */
  private final class CancelButtonClickHandler implements ClickHandler {

    /**
     * Called when cancel button is called.
     *
     * @param event the {@link ClickEvent} that was fired
     */
    @Override
    public void onClick(final ClickEvent event) {

      RepositoryDialogBox.this.hide();
    }
  }

}