package org.parabuild.ci.webui.common;

public interface HasInputValue {

  void setInputValue(String value);

  boolean isInputEditable();

  String getInputValue();
}
