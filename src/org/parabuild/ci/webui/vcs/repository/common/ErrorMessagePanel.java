package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.parabuild.ci.common.ErrorDisplay;

/**
 * A error panel is used by Pa to display field validation errors in forms.
 */
public class ErrorMessagePanel extends FlowPanel implements ErrorDisplay {


  /**
   * Error label have a red border around them.
   */
  private static final String VALIDATION_ERRORS_PANEL = "error-message-panel";


  public ErrorMessagePanel() {

    addStyleName(VALIDATION_ERRORS_PANEL);

  }


  /**
   * {@inheritDoc}.
   */
  @Override
  public void addError(final String error) {

    add(new Label(error));
  }


  /**
   * Clears all errors.
   */
  @Override
  public void clearErrors() {

    clear();
  }
}
