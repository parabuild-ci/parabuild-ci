package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import org.parabuild.ci.webui.vcs.repository.common.CancelButton;
import org.parabuild.ci.webui.vcs.repository.common.CancelButtonClickHandler;
import org.parabuild.ci.webui.vcs.repository.common.EditDialogBox;
import org.parabuild.ci.webui.vcs.repository.common.FlexTableIterator;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildTextBox;
import org.parabuild.ci.webui.vcs.repository.common.SaveButton;

/**
 * Repository dialog box is responsible for editing and displaying Repository information.
 */
@SuppressWarnings("WeakerAccess")
public final class VCSServerDialogBox extends EditDialogBox {

  private final ParabuildFlexTable flexTable = new ParabuildFlexTable(2);
  private final Label lbType = new Label("Server type:");
  private final Label lbDescription = new Label("Server description:");
  private final Label lbName = new Label("Server name:");
  private final TextBox flDescription = new ParabuildTextBox(100, 70);
  private final TextBox flName = new ParabuildTextBox(50, 50);
  private final ListBox flTypes = new VCSServerTypeListBox();


  /**
   * Creates this {@link VCSServerDialogBox}.
   *
   * @param captionText the text inside the caption widget.
   */
  public VCSServerDialogBox(final String captionText) {


    super(captionText, false, true);

    // Layout
    final FlexTableIterator flexTableIterator = flexTable.flexTableIterator();
    flexTableIterator.add(lbType).add(flTypes);
    flexTableIterator.add(lbName).add(flName);
    flexTableIterator.add(lbDescription).add(flDescription);
    flexTableIterator.add(new SaveButton(new SaveVCSServerClickHandler(this)));
    flexTableIterator.add(new CancelButton(new CancelButtonClickHandler(this)));

    // Add layout panel
    setWidget(flexTable);
  }


  /**
   * Returns the VO representing the repository server being edited.
   *
   * @return the VO representing the repository server being edited.
   */
  public VCSServerClientVO getServerVO() {

    // Create the VO
    final VCSServerClientVO result = new VCSServerClientVO();
    result.setType(Integer.parseInt(flTypes.getSelectedValue()));
    result.setDescription(flDescription.getValue());
    result.setName(flName.getValue());

    // Return result
    return result;
  }
}