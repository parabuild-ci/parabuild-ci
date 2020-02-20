package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.ui.TextBox;

public class ParabuildTextBox extends TextBox {

  /**
   * Creates an empty text box.
   */
  public ParabuildTextBox(int maxLength, int visibleLength) {
    setVisibleLength(visibleLength);
    setMaxLength(maxLength);
  }
}
