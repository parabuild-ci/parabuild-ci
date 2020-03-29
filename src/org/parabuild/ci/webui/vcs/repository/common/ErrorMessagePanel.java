package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * A error panel is used by Pa to display field validation errors in forms.
 */
public class ErrorMessagePanel extends FlowPanel {


  /**
   * Error label have a red border around them.
   */
  private static final String VALIDATION_ERRORS_PANEL = "error-message-panel";


  public ErrorMessagePanel() {

    addStyleName(VALIDATION_ERRORS_PANEL);

  }


  public void addError(final String error) {

    add(new Label(error));
  }
}
