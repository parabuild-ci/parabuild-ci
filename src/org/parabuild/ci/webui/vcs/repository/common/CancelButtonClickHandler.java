package org.parabuild.ci.webui.vcs.repository.common;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * A click handler for "Cancel" button.
 */

public final class CancelButtonClickHandler implements ClickHandler {

  /**
   * The popup panel the button belongs to.
   */
  private final PopupPanel popupPanel;


  /**
   * Creates {@link CancelButtonClickHandler}.
   *
   * @param popupPanel the popup panel the button belongs to.
   */
  public CancelButtonClickHandler(final PopupPanel popupPanel) {
    this.popupPanel = popupPanel;
  }


  /**
   * Called when cancel button is called. Hides the {@link #popupPanel}.
   *
   * @param event the {@link ClickEvent} that was fired
   */
  @Override
  public void onClick(final ClickEvent event) {

    popupPanel.hide();
  }
}

