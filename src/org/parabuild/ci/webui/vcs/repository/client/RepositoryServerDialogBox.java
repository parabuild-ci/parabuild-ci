package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import org.parabuild.ci.webui.vcs.repository.common.CancelButton;
import org.parabuild.ci.webui.vcs.repository.common.CancelButtonClickHandler;
import org.parabuild.ci.webui.vcs.repository.common.FlexTableIterator;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildDialogBox;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildTextBox;

/**
 * Repository dialog box is responsible for editing and displaying Repository information.
 */
@SuppressWarnings("WeakerAccess")
public final class RepositoryServerDialogBox extends ParabuildDialogBox {

  private final FlexTable flexTable = new FlexTable();
  private final Label lbType = new Label("Server type:");
  private final Label lbDescription = new Label("Server description:");
  private final Label lbName = new Label("Server name:");
  private final TextBox tbDescription = new ParabuildTextBox(100, 70);
  private final TextBox tbName = new ParabuildTextBox(50, 50);
  private final ListBox listBoxType = new ListBox();


  /**
   * Creates this {@link RepositoryServerDialogBox}.
   *
   * @param captionText the text inside the caption widget.
   */
  public RepositoryServerDialogBox(final String captionText) {


    super(captionText, false, true);
    super.center();

    // Create repository type listbox
    listBoxType.addItem("GitHub", "1");


    // Add fields
    final FlexTableIterator flexTableIterator = new FlexTableIterator(flexTable, 2);
    flexTableIterator.add(lbType).add(listBoxType);
    flexTableIterator.add(lbName).add(tbName);
    flexTableIterator.add(lbDescription).add(tbDescription);

    // Add "Save" button
    final Button btnSave = new Button("Save", new SaveServerClickHandler(this));
    flexTableIterator.add(btnSave);

    // Add "Cancel" button
    final Button btnCancel = new CancelButton(new CancelButtonClickHandler(this));
    flexTableIterator.add(btnCancel);

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
    result.setType(Integer.parseInt(listBoxType.getSelectedValue()));
    result.setDescription(tbDescription.getValue());
    result.setName(tbName.getValue());

    // Return result
    return result;
  }
}