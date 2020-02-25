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
    flTypes.setEnabled(false);
    final VCSServerServiceAsync vcsServerService = GWT.create(VCSServerService.class);
    vcsServerService.getVCSServerTypes(new AsyncCallback<VCSServerType[]>() {

      @Override
      public void onFailure(final Throwable caught) {

        final ErrorDialogBox errorDialogBox = new ErrorDialogBox();
        errorDialogBox.setErrorMessage(caught.getMessage());
        errorDialogBox.center();
        errorDialogBox.show();
      }


      @Override
      public void onSuccess(final VCSServerType[] result) {

        for (final VCSServerType vcsServerType : result) {

          final String typeName = vcsServerType.getName();
          final String typeValue = Integer.toString(vcsServerType.getType());
          flTypes.addItem(typeName, typeValue);
        }

        flTypes.setEnabled(true);
      }
    });

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