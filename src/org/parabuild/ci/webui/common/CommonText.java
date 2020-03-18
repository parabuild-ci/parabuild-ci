package org.parabuild.ci.webui.common;

import org.parabuild.ci.common.HasInputValue;
import viewtier.ui.Text;

public class CommonText extends Text implements HasInputValue {

  private static final long serialVersionUID = -5181103029365641958L;


  public CommonText() {
  }


  public CommonText(final int numberOfColumns, final int numberOfRows) {
    super(numberOfColumns, numberOfRows);
  }


  public CommonText(final int numberOfColumns, final int numberOfRows, final String initialValue) {
    super(numberOfColumns, numberOfRows, initialValue);
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
