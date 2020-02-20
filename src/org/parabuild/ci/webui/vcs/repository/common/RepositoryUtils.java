package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Shared GWT client utilities.
 */
public final class RepositoryUtils {

  /**
   * Adds adding a click handlers to a host page button.
   *
   * @param elementId a unique HTML button element ID from the host page.
   * @param handler   a handler to add.
   */
  public static void addButtonClickHandler(final String elementId, final ClickHandler handler) {

    final Element element = RootPanel.get(elementId).getElement();
    final ButtonElement buttonElement = ButtonElement.as(element);
    final Button topAddButton = Button.wrap(buttonElement);
    topAddButton.addClickHandler(handler);
  }
}
