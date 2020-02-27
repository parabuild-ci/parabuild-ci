package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import org.parabuild.ci.webui.vcs.repository.common.CancelButton;
import org.parabuild.ci.webui.vcs.repository.common.CancelButtonClickHandler;
import org.parabuild.ci.webui.vcs.repository.common.ErrorDialogBox;
import org.parabuild.ci.webui.vcs.repository.common.FlexTableIterator;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildDialogBox;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildTextBox;

/**
 * Repository dialog box is responsible for editing and displaying Repository information.
 */
@SuppressWarnings("WeakerAccess")
public final class VCSRepositoryDialogBox extends ParabuildDialogBox {

  private final FlexTable flexTable = new FlexTable();
  private final Label lbDescription = new Label("Repository description:");
  private final Label lbName = new Label("Repository name:");
  private final Label lbType = new Label("Server:");
  private final TextBox tbDescription = new ParabuildTextBox(100, 70);
  private final TextBox tbName = new ParabuildTextBox(50, 50);
  private final ListBox lbServer = new ListBox();


  /**
   * Creates this {@link VCSRepositoryDialogBox}.
   *
   * @param captionText the text inside the caption widget.
   */
  public VCSRepositoryDialogBox(final String captionText) {


    super(captionText, false, true);
    super.center();

    // Disable lbServer until it's loaded
    lbServer.setEnabled(false);

    // Populate server names list box
    final VCSServerServiceAsync serverServiceAsync = GWT.create(VCSServerService.class);
    final AsyncCallback<VCSServerClientVO[]> callback = new AsyncCallback<VCSServerClientVO[]>() {

      @Override
      public void onFailure(final Throwable caught) {

        // Show error dialog
        final ErrorDialogBox errorDialogBox = new ErrorDialogBox();
        errorDialogBox.setErrorMessage(caught.getMessage());
        errorDialogBox.center();
        errorDialogBox.show();
      }


      /**
       * Called when an asynchronous call completes successfully.
       *
       * @param result the return value of the remote produced call
       */
      @Override
      public void onSuccess(final VCSServerClientVO[] result) {

        for (final VCSServerClientVO vcsServerClientVO : result) {

          final String item = vcsServerClientVO.getName() + " (" + vcsServerClientVO.getTypeAsString() + ')';
          final String value = Integer.toString(vcsServerClientVO.getId());
          lbServer.addItem(item, value);
        }

        lbServer.setEnabled(true);
      }
    };
    serverServiceAsync.getVCSServers(callback);

    // Add fields
    final FlexTableIterator flexTableIterator = new FlexTableIterator(flexTable, 2);
    flexTableIterator.add(lbType).add(lbServer);
    flexTableIterator.add(lbName).add(tbName);
    flexTableIterator.add(lbDescription).add(tbDescription);

    // Add "Save" button
    flexTableIterator.add(new Button("Save", new SaveVCSRepositoryClickHandler(this)));

    // Add "Cancel" button
    flexTableIterator.add(new CancelButton(new CancelButtonClickHandler(this)));

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
    final int serverId = Integer.parseInt(lbServer.getSelectedValue());
    repositoryClientVO.setDescription(tbDescription.getValue());
    repositoryClientVO.setName(tbName.getValue());
    repositoryClientVO.setServerId(serverId);

    return repositoryClientVO;
  }
}