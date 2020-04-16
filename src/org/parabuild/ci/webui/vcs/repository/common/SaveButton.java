package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * A button with a caption Save".
 */
public class SaveButton extends Button {


  private static final String CAPTION = "Save";


  /**
   * Creates a button with a caption "Save" and the given click listener.
   *
   * @param handler the click handler
   */
  public SaveButton(final ClickHandler handler) {

    super(CAPTION, handler);
  }
}
