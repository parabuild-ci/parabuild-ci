package org.parabuild.ci.common;

public interface HasInputValue {

  void setInputValue(String value);

  boolean isInputEditable();

  String getInputValue();
}
