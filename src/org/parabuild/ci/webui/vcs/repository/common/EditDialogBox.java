package org.parabuild.ci.webui.vcs.repository.common;

public class EditDialogBox extends ParabuildDialogBox {

  /**
   * Creates this {@link EditDialogBox}.
   *
   * @param captionText the text inside the caption widget.
   * @param autoHide    <code>true</code> if the dialog should be automatically
   *                    hidden when the user clicks outside of it
   * @param modal       <code>true</code> if keyboard and mouse events for widgets not
   */
  public EditDialogBox(final String captionText, final boolean autoHide, final boolean modal) {

    super(captionText, autoHide, modal);

    addStyleName("edit");
  }
}
