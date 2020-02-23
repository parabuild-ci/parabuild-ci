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
public final class VCSRepositoryDialogBox extends ParabuildDialogBox {

  private final FlexTable flexTable = new FlexTable();
  private final Label lbType = new Label("Repository type:");
  private final Label lbDescription = new Label("Repository description:");
  private final Label lbName = new Label("Repository name:");
  private final TextBox tbDescription = new ParabuildTextBox(100, 70);
  private final TextBox tbName = new ParabuildTextBox(50, 50);
  private final ListBox lboxType = new ListBox();


  /**
   * Creates this {@link VCSRepositoryDialogBox}.
   *
   * @param captionText the text inside the caption widget.
   */
  public VCSRepositoryDialogBox(final String captionText) {


    super(captionText, false, true);
    super.center();

    // Create repository type listbox
    lboxType.addItem("GitHub", "1");


    // Add fields
    final FlexTableIterator flexTableIterator = new FlexTableIterator(flexTable, 2);
    flexTableIterator.add(lbType).add(lboxType);
    flexTableIterator.add(lbName).add(tbName);
    flexTableIterator.add(lbDescription).add(tbDescription);

    // Add "Save" button
    final Button btnSave = new Button("Save", new SaveVCSRepositoryClickHandler(this));
    flexTableIterator.add(btnSave);

    // Add "Cancel" button
    final Button btnCancel = new CancelButton(new CancelButtonClickHandler(this));
    flexTableIterator.add(btnCancel);

    // Add layout panel
    setWidget(flexTable);
  }


  /**
   * Returns the VO representing the repository being edited.
   *
   * @return the VO representing the repository being edited.
   */
  VCSRepositoryClientVO getRepositoryVO() {

    final VCSRepositoryClientVO repositoryClientVO = new VCSRepositoryClientVO();
    repositoryClientVO.setType(Integer.parseInt(lboxType.getSelectedValue()));
    repositoryClientVO.setDescription(tbDescription.getValue());
    repositoryClientVO.setName(tbName.getValue());

    return repositoryClientVO;
  }
}