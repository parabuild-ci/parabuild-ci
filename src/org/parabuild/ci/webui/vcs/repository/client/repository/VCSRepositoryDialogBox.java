package org.parabuild.ci.webui.vcs.repository.client.repository;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.webui.vcs.repository.client.server.VCSServerService;
import org.parabuild.ci.webui.vcs.repository.client.server.VCSServerServiceAsync;
import org.parabuild.ci.webui.vcs.repository.common.CancelButton;
import org.parabuild.ci.webui.vcs.repository.common.CancelButtonClickHandler;
import org.parabuild.ci.webui.vcs.repository.common.EditDialogBox;
import org.parabuild.ci.webui.vcs.repository.common.FlexTableIterator;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildAsyncCallback;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildFlexTable;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildListBox;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildTextBox;
import org.parabuild.ci.webui.vcs.repository.common.SaveButton;
import org.parabuild.ci.webui.vcs.repository.common.VCSServerVO;

import java.util.ArrayList;

/**
 * Repository dialog box is responsible for editing and displaying Repository information.
 */
@SuppressWarnings("WeakerAccess")
public final class VCSRepositoryDialogBox extends EditDialogBox {

  private static final String CAPTION_REPOSITORY_DESCRIPTION = "Repository description:";

  private static final String CAPTION_REPOSITORY_NAME = "Repository name:";

  private static final String CAPTION_SERVER = "Server:";

  private final SaveButton saveButton = new SaveButton(new SaveVCSRepositoryClickHandler(this));

  private final CancelButton cancelButton = new CancelButton(new CancelButtonClickHandler(this));

  private final ParabuildFlexTable inputsTable = new ParabuildFlexTable(2);

  private final Label lbDescription = new Label(CAPTION_REPOSITORY_DESCRIPTION);

  private final Label lbName = new Label(CAPTION_REPOSITORY_NAME);

  private final Label lbType = new Label(CAPTION_SERVER);

  private final ParabuildTextBox flDescription = new ParabuildTextBox(100, 70);

  private final ParabuildTextBox flName = new ParabuildTextBox(50, 50);

  private final ParabuildListBox flServer = new ParabuildListBox();


  /**
   * Creates this {@link VCSRepositoryDialogBox}.
   *
   * @param captionText the text inside the caption widget.
   */
  public VCSRepositoryDialogBox(final String captionText) {


    super(captionText, false, true);
    super.center();

    // Populate server names list box
    populateServers();

    // Add fields
    final FlexTableIterator tableIterator = inputsTable.flexTableIterator();
    tableIterator.addPair(lbType, flServer);
    tableIterator.addPair(lbName, flName);
    tableIterator.addPair(lbDescription, flDescription);
    inputsPanel().add(inputsTable);

    // Add controls
    controlsPanel().add(saveButton);
    controlsPanel().add(cancelButton);
  }


  /**
   * Populates {@link #flServer} drop down.
   */
  private void populateServers() {

    // Disable lbServer until it's loaded
    flServer.setEnabled(false);

    final VCSServerServiceAsync serverServiceAsync = GWT.create(VCSServerService.class);
    final AsyncCallback<VCSServerVO[]> callback = new ParabuildAsyncCallback<VCSServerVO[]>() {

      /**
       * Called when an asynchronous call completes successfully.
       *
       * @param result the return value of the remote produced call
       */
      @Override
      public void onSuccess(final VCSServerVO[] result) {

        for (final VCSServerVO vcsServerClientVO : result) {

          final String item = vcsServerClientVO.getName() + " (" + vcsServerClientVO.getTypeAsString() + ')';
          final String value = Integer.toString(vcsServerClientVO.getId());
          flServer.addItem(item, value);
        }

        // Enable
        flServer.setEnabled(true);
      }
    };
    serverServiceAsync.getVCSServers(callback);
  }


  /**
   * Returns the VO representing the repository being edited.
   *
   * @return the VO representing the repository being edited.
   */
  VCSRepositoryClientVO getRepositoryVO() {

    final VCSRepositoryClientVO repositoryClientVO = new VCSRepositoryClientVO();
    final int serverId = Integer.parseInt(flServer.getSelectedValue());
    repositoryClientVO.setDescription(flDescription.getValue());
    repositoryClientVO.setName(flName.getValue());
    repositoryClientVO.setServerId(serverId);
    return repositoryClientVO;
  }


  /**
   * Validates the input. Returns <code>true</code> if input is valid. Shows errors and returns <code>false</code> if
   * input is invalid.
   *
   * @return <code>true</code> if input is valid. Shows errors and returns <code>false</code> if input is invalid.
   */
  @Override
  public boolean validate() {


    // Validate this dialog's fields
    final ArrayList<String> errors = new ArrayList<>(1);
    InputValidator.validateFieldNotBlank(errors, CAPTION_SERVER, flServer);
    InputValidator.validateFieldNotBlank(errors, CAPTION_REPOSITORY_DESCRIPTION, flDescription);
    InputValidator.validateFieldNotBlank(errors, CAPTION_REPOSITORY_NAME, flName);

    // Display errors if any
    for (final String error : errors) {
      errorPanel().addError(error);
    }

    // Return result
    return errors.isEmpty();
  }
}