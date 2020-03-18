package org.parabuild.ci.webui.common;

import org.parabuild.ci.common.HasInputValue;
import viewtier.ui.CheckBox;

/**
 * Common check box implementing {@link HasInputValue}.
 */
public class CommonCheckBox extends CheckBox implements HasInputValue {

  private static final long serialVersionUID = 7075748614814405703L;


  public CommonCheckBox() {
  }


  public CommonCheckBox(final String text) {
    super(text);
  }


  @Override
  public void setInputValue(final String value) {
    setValue(value);
  }


  @Override
  public boolean isInputEditable() {
    return isEditable();
  }


  @Override
  public String getInputValue() {
    return getValue();
  }
}
