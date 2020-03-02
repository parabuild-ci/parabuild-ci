package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.ui.DialogBox;
import org.parabuild.ci.webui.vcs.repository.client.VCSRepositoryDialogBox;

/**
 * Reusable DialogBox. It has Parabuild "form" style.
 */
@SuppressWarnings("GWTStyleCheck")
public abstract class ParabuildDialogBox extends DialogBox {

  /**
   * Creates this {@link VCSRepositoryDialogBox}.
   *
   * @param captionText the text inside the caption widget.
   * @param autoHide    <code>true</code> if the dialog should be automatically
   *                    hidden when the user clicks outside of it
   * @param modal       <code>true</code> if keyboard and mouse events for widgets not
   *                    contained by the dialog should be ignored
   */
  public ParabuildDialogBox(final String captionText, final boolean autoHide, final boolean modal) {
    super(autoHide, modal);

    // Add form style
    addStyleName("form");

    // Set the dialog box's caption.
    setText(captionText);

    // Disable animation.
    setAnimationEnabled(false);

    // Enable glass background.
    setGlassEnabled(true);

    super.center();
  }
}
