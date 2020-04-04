package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.ui.FlowPanel;
import org.parabuild.ci.common.InputValidator;

public abstract class EditDialogBox extends ParabuildDialogBox {

  /**
   * A style name that we add to this {@link EditDialogBox}. See {@link EditDialogBox#EditDialogBox(String, boolean, boolean)} for details.
   */
  private static final String EDIT_STYLE_NAME = "edit";

  /**
   * A top-level layout panel for the dialog box.
   */
  private final FlowPanel topLayoutPanel = new FlowPanel();

  /**
   * A panel used to display dialog box's validation errors.
   */
  private final ErrorMessagePanel errorMessagePanel = new ErrorMessagePanel();

  /**
   * A panel to display dialog box inputs.
   */
  private final FlowPanel inputPanel = new FlowPanel();

  /**
   * A panel to display controls such as Save and cancel buttons.
   */
  private final FlowPanel controlPanel = new FlowPanel();

  /**
   * Input validator.
   */
  private final InputValidator inputValidator = new InputValidator(errorMessagePanel);


  /**
   * Creates this {@link EditDialogBox}.
   *
   * @param captionText the text inside the caption widget.
   * @param autoHide    <code>true</code> if the dialog should be automatically
   *                    hidden when the user clicks outside of it
   * @param modal       <code>true</code> if keyboard and mouse events for widgets not
   */
  public EditDialogBox(final String captionText, final boolean autoHide, final boolean modal) {

    super(captionText, autoHide, modal);

    // Set style
    addStyleName(EDIT_STYLE_NAME);

    // Add error panel to the layout panel
    topLayoutPanel.add(errorMessagePanel);

    // Add dialog box input panel
    topLayoutPanel.add(inputPanel);

    // Add dialog box controls panel
    topLayoutPanel.add(controlPanel);

    // Add layout panel
    setWidget(topLayoutPanel);
  }


  /**
   * Validates the input. Returns <code>true</code> if input is valid. Shows errors and returns <code>false</code> if
   * input is invalid.
   *
   * @return <code>true</code> if input is valid. Shows errors and returns <code>false</code> if input is invalid.
   */
  public abstract boolean validate();


  /**
   * Creates a panel containing the dialog box inputs.
   *
   * @return a panel containing the dialog box inputs.
   */
  protected final FlowPanel inputsPanel() {

    return inputPanel;
  }


  /**
   * Returns an input validator associated with this <code>EditDialogBox</code>.
   *
   * @return input validator associated with this <code>EditDialogBox</code>.
   */
  public InputValidator inputValidator() {

    return inputValidator;
  }


  /**
   * Returns a panel to populate with controls.
   *
   * @return a panel to populate with controls.
   */
  protected final FlowPanel controlsPanel() {

    return controlPanel;
  }
}
