package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * A dialog box responsible for displaying error messages.
 */
@SuppressWarnings("WeakerAccess")
public final class ErrorDialogBox extends ParabuildDialogBox {

  private final Label lbErrorCaption = new Label("Error:");
  private final FlexTable layoutPanel = new FlexTable();
  private final Label lbErrorText = new Label();


  /**
   * Creates this {@link ErrorDialogBox}.
   */
  public ErrorDialogBox() {

    super("Unexpected Error", false, true);

    // Appearance
    lbErrorCaption.addStyleName("error");

    // Create flex table iterator
    final FlexTableIterator flexTableIterator = new FlexTableIterator(layoutPanel, 2);

    // Fill the container
    flexTableIterator.add(lbErrorCaption).add(lbErrorText);
    flexTableIterator.add(new Button("OK", new OkButtonClickHandler(this)));

    // Add layout panel
    setWidget(layoutPanel);
  }


  /**
   * Set error message.
   *
   * @param message error message.
   */
  public void setErrorMessage(final String message) {

    lbErrorText.setText(message);
  }


  @Override
  public String toString() {

    return "ErrorDialogBox{" +
            "lbErrorCaption=" + lbErrorCaption +
            ", layoutPanel=" + layoutPanel +
            ", lbErrorText=" + lbErrorText +
            '}';
  }
}