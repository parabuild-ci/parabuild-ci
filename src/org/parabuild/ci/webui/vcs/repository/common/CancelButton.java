package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class CancelButton extends Button {


  private static final String CAPTION = "Cancel";


  /**
   * Creates a cancel button with caption "Cancel" and the given click listener.
   *
   * @param handler the click handler
   */
  public CancelButton(final ClickHandler handler) {

    super(CAPTION, handler);
  }
}
