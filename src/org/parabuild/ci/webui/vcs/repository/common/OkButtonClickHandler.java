package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * A {@link ClickHandler} for a "OK" button. Just closes the dialog the button belongs to.
 */
public final class OkButtonClickHandler implements ClickHandler {

  private final DialogBox dialogBox;


  public OkButtonClickHandler(final DialogBox dialogBox) {

    this.dialogBox = dialogBox;
  }


  /**
   * Called when a native click event is fired.
   *
   * @param event the {@link ClickEvent} that was fired
   */
  @Override
  public void onClick(final ClickEvent event) {

    dialogBox.hide(true);
  }
}
