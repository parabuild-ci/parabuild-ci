package org.parabuild.ci.webui.common;

import org.parabuild.ci.common.HasInputValue;
import viewtier.ui.DropDown;

public class CommonDropDown extends DropDown implements HasInputValue {

  private static final long serialVersionUID = 6568533082357899582L;


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
