package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.ui.TextBox;
import org.parabuild.ci.common.HasInputValue;

/**
 * Reusable text box field.
 */
public class ParabuildTextBox extends TextBox implements HasInputValue {

  /**
   * Creates an empty text box.
   */
  public ParabuildTextBox(final int maxLength, final int visibleLength) {

    setVisibleLength(visibleLength);
    setMaxLength(maxLength);
  }


  @Override
  public void setInputValue(final String value) {
    setValue(value);
  }


  @Override
  public boolean isInputEditable() {
    return isEnabled();
  }


  @Override
  public String getInputValue() {
    return getValue();
  }
}
