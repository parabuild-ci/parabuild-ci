package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class SaveButton extends Button {


  private static final String CAPTION = "Save";


  /**
   * Creates a cancel button with caption "Cancel" and the given click listener.
   *
   * @param handler the click handler
   */
  public SaveButton(final ClickHandler handler) {

    super(CAPTION, handler);
  }
}
