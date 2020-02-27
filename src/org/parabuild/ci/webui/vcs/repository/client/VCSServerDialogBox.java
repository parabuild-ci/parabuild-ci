package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.webui.vcs.repository.common.CancelButton;
import org.parabuild.ci.webui.vcs.repository.common.CancelButtonClickHandler;
import org.parabuild.ci.webui.vcs.repository.common.FlexTableIterator;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildDialogBox;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildTextBox;
import org.parabuild.ci.webui.vcs.repository.common.SaveButton;

/**
 * Repository dialog box is responsible for editing and displaying Repository information.
 */
@SuppressWarnings("WeakerAccess")
public final class VCSServerDialogBox extends ParabuildDialogBox {

  private final FlexTable flexTable = new FlexTable();
  private final Label lbType = new Label("Server type:");
  private final Label lbDescription = new Label("Server description:");
  private final Label lbName = new Label("Server name:");
  private final TextBox flDescription = new ParabuildTextBox(100, 70);
  private final TextBox flName = new ParabuildTextBox(50, 50);
  private final ListBox flTypes = new ListBox();


  /**
   * Creates this {@link VCSServerDialogBox}.
   *
   * @param captionText the text inside the caption widget.
   */
  public VCSServerDialogBox(final String captionText) {


    super(captionText, false, true);
    super.center();

    // Populate type dropdown
    flTypes.addItem(VersionControlSystem.NAME_SCM_GIT, Integer.toString(VersionControlSystem.SCM_GIT));
    flTypes.addItem(VersionControlSystem.NAME_SCM_PERFORCE, Integer.toString(VersionControlSystem.SCM_PERFORCE));
    flTypes.addItem(VersionControlSystem.NAME_SCM_SVN, Integer.toString(VersionControlSystem.SCM_SVN));
    flTypes.addItem(VersionControlSystem.NAME_SCM_ACCUREV, Integer.toString(VersionControlSystem.SCM_ACCUREV));
    flTypes.addItem(VersionControlSystem.NAME_SCM_CLEARCASE, Integer.toString(VersionControlSystem.SCM_CLEARCASE));
    flTypes.addItem(VersionControlSystem.NAME_BAZAAR, Integer.toString(VersionControlSystem.SCM_BAZAAR));
    flTypes.addItem(VersionControlSystem.NAME_SCM_CVS, Integer.toString(VersionControlSystem.SCM_CVS));
    flTypes.addItem(VersionControlSystem.NAME_SCM_FILESYSTEM, Integer.toString(VersionControlSystem.SCM_FILESYSTEM));
    flTypes.addItem(VersionControlSystem.NAME_SCM_GENERIC, Integer.toString(VersionControlSystem.SCM_GENERIC));
    flTypes.addItem(VersionControlSystem.NAME_SCM_MERCURIAL, Integer.toString(VersionControlSystem.SCM_MERCURIAL));
    flTypes.addItem(VersionControlSystem.NAME_SCM_MKS, Integer.toString(VersionControlSystem.SCM_MKS));
    flTypes.addItem(VersionControlSystem.NAME_SCM_PVCS, Integer.toString(VersionControlSystem.SCM_PVCS));
    flTypes.addItem(VersionControlSystem.NAME_SCM_REFERENCE, Integer.toString(VersionControlSystem.SCM_REFERENCE));
    flTypes.addItem(VersionControlSystem.NAME_SCM_STARTEAM, Integer.toString(VersionControlSystem.SCM_STARTEAM));
    flTypes.addItem(VersionControlSystem.NAME_SCM_SURROUND, Integer.toString(VersionControlSystem.SCM_SURROUND));
    flTypes.addItem(VersionControlSystem.NAME_SCM_VAULT, Integer.toString(VersionControlSystem.SCM_VAULT));
    flTypes.addItem(VersionControlSystem.NAME_SCM_VSS, Integer.toString(VersionControlSystem.SCM_VSS));

    // Layout fields
    final FlexTableIterator flexTableIterator = new FlexTableIterator(flexTable, 2);
    flexTableIterator.add(lbType).add(flTypes);
    flexTableIterator.add(lbName).add(flName);
    flexTableIterator.add(lbDescription).add(flDescription);

    // Layout"Save" button
    final Button btnSave = new SaveButton(new SaveVCSServerClickHandler(this));
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
    result.setType(Integer.parseInt(flTypes.getSelectedValue()));
    result.setDescription(flDescription.getValue());
    result.setName(flName.getValue());

    // Return result
    return result;
  }
}