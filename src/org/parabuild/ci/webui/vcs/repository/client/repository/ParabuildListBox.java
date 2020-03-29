package org.parabuild.ci.webui.vcs.repository.client.repository;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ListBox;
import org.parabuild.ci.common.HasInputValue;

public class ParabuildListBox extends ListBox implements HasInputValue {

  public ParabuildListBox() {

  }


  public ParabuildListBox(final Element element) {

    super(element);
  }


  @Override
  public void setInputValue(final String value) {

    setSelectedIndex(Integer.parseInt(value));
  }


  @Override
  public boolean isInputEditable() {

    return isEnabled();
  }


  @Override
  public String getInputValue() {

    return Integer.toString(getSelectedIndex());
  }
}
