package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.ui.TextBox;

/**
 * Reusable text box field.
 */
public class ParabuildTextBox extends TextBox {

  /**
   * Creates an empty text box.
   */
  public ParabuildTextBox(final int maxLength, final int visibleLength) {

    setVisibleLength(visibleLength);
    setMaxLength(maxLength);
  }
}
