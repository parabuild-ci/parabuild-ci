package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import junit.framework.TestCase;
import org.parabuild.ci.webui.vcs.repository.common.CancelButtonClickHandler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

public final class CancelButtonClickHandlerTest extends TestCase {


  private CancelButtonClickHandler cancelButtonClickHandler;
  private PopupPanel popupPanel;


  /**
   * Constructs a test case with the given name.
   */
  public CancelButtonClickHandlerTest(final String name) {
    super(name);
  }


  public void testOnClick() {

    final ClickEvent mock = mock(ClickEvent.class);
    cancelButtonClickHandler.onClick(mock);
    verify(popupPanel, only()).hide();
  }


  public void setUp() throws Exception {
    super.setUp();

    popupPanel = mock(PopupPanel.class);
    cancelButtonClickHandler = new CancelButtonClickHandler(popupPanel);
  }


  public void tearDown() throws Exception {
    cancelButtonClickHandler = null;
    popupPanel = null;
    super.tearDown();
  }
}