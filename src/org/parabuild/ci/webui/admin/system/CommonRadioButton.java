package org.parabuild.ci.webui.admin.system;

import org.parabuild.ci.webui.common.HasInputValue;
import viewtier.ui.RadioButton;

public class CommonRadioButton extends RadioButton implements HasInputValue {

  private static final long serialVersionUID = -686825471924421578L;


  public CommonRadioButton() {
  }


  public CommonRadioButton(final String text) {
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
