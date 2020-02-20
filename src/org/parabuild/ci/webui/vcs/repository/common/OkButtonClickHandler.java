package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public final class OkButtonClickHandler implements ClickHandler {

  private final ErrorDialogBox errorDialogBox;


  public OkButtonClickHandler(final ErrorDialogBox errorDialogBox) {
    this.errorDialogBox = errorDialogBox;
  }


  /**
   * Called when a native click event is fired.
   *
   * @param event the {@link ClickEvent} that was fired
   */
  @Override
  public void onClick(final ClickEvent event) {
    errorDialogBox.hide(true);
  }
}
