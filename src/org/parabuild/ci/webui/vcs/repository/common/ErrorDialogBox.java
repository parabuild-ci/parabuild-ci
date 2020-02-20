package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * A dialog box responsible for displaying error messages.
 */
@SuppressWarnings("WeakerAccess")
public final class ErrorDialogBox extends DialogBox {

  private final FlowPanel layoutPanel = new FlowPanel();
  private final Label lbError = new Label("Unexpected error occurred");


  /**
   * Creates this {@link ErrorDialogBox}.
   */
  public ErrorDialogBox() {


    super(false, true);
    super.center();

    // Set the dialog box's caption.
    setText("Error");

    // Disable animation.
    setAnimationEnabled(false);

    // Enable glass background.
    setGlassEnabled(true);

    // Create a container for fields
    layoutPanel.add(lbError);

    // Add "Save" button
    layoutPanel.add(new Button("OK", new OkButtonClickHandler(this)));

    // Add layout panel
    setWidget(layoutPanel);
  }


  public void setErrorMessage(final String message) {
    lbError.setText(message);
  }
}