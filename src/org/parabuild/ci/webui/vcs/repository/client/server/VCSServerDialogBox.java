package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.common.PropertyToInputMap;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.webui.vcs.repository.common.CancelButton;
import org.parabuild.ci.webui.vcs.repository.common.CancelButtonClickHandler;
import org.parabuild.ci.webui.vcs.repository.common.EditDialogBox;
import org.parabuild.ci.webui.vcs.repository.common.FlexTableIterator;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildFlexTable;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildTextBox;
import org.parabuild.ci.webui.vcs.repository.common.SaveButton;
import org.parabuild.ci.webui.vcs.repository.common.VCSServerVO;

import java.util.ArrayList;
import java.util.List;

import static org.parabuild.ci.common.VersionControlSystem.SCM_COUNT;
import static org.parabuild.ci.common.VersionControlSystem.SCM_GIT;

/**
 * Repository dialog box is responsible for editing and displaying Repository information.
 */
@SuppressWarnings("WeakerAccess")
public final class VCSServerDialogBox extends EditDialogBox {

  private static final String CAPTION_SERVER_TYPE = "Server type:";

  private static final String CAPTION_SERVER_DESCRIPTION = "Server description:";

  private static final String CAPTION_SERVER_NAME = "Server name:";

  // Lookup table for attribute panels
  private final VCSServerAttributePanel[] attributePanels = new VCSServerAttributePanel[SCM_COUNT];


  private final SimplePanel attributeContainerPanel = new SimplePanel();

  // UI widgets
  private final Label lbType = new Label(CAPTION_SERVER_TYPE);

  private final Label lbDescription = new Label(CAPTION_SERVER_DESCRIPTION);

  private final Label lbName = new Label(CAPTION_SERVER_NAME);

  private final ParabuildTextBox flDescription = new ParabuildTextBox(100, 70);

  private final ParabuildTextBox flServerName = new ParabuildTextBox(50, 50);

  private final VCSServerTypeListBox flTypes = new VCSServerTypeListBox();

  // Current attribute panel
  private VCSServerAttributePanel currentAttributePanel;


  /**
   * Creates this {@link VCSServerDialogBox}.
   *
   * @param captionText the text inside the caption widget.
   */
  public VCSServerDialogBox(final String captionText) {

    super(captionText, false, true);

    // Add inputs
    final ParabuildFlexTable inputsTable = new ParabuildFlexTable(2);
    final FlexTableIterator flexTableIterator = inputsTable.flexTableIterator();
    flexTableIterator.add(lbType).add(flTypes);
    flexTableIterator.add(lbName).add(flServerName);
    flexTableIterator.add(lbDescription).add(flDescription);
    flexTableIterator.add(attributeContainerPanel, 2);
    inputsPanel().add(inputsTable);

    // Add controls
    controlsPanel().add(new SaveButton(new SaveVCSServerClickHandler(this)));
    controlsPanel().add(new CancelButton(new CancelButtonClickHandler(this)));


    // Set up switching of the panels
    flTypes.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(final ChangeEvent changeEvent) {

        showAttributePanel(VCSServerDialogBox.this.flTypes);
      }
    });

    // Do initial selection
    showAttributePanel(flTypes);
  }


  @Override
  public boolean validate() {

    // Validate this dialog's fields
    final ArrayList<String> errors = new ArrayList<>(1);
    InputValidator.validateFieldNotBlank(errors, CAPTION_SERVER_NAME, flServerName);
    InputValidator.validateFieldNotBlank(errors, CAPTION_SERVER_DESCRIPTION, flDescription);
    InputValidator.validateFieldNotBlank(errors, CAPTION_SERVER_DESCRIPTION, flTypes);

    // Validate attributes
    currentAttributePanel.validate(errors);

    // Display errors if any
    for (final String error : errors) {
      errorPanel().addError(error);
    }

    // Return result
    return errors.isEmpty();
  }


  /**
   * Returns the VO representing the repository server being edited.
   *
   * @return the VO representing the repository server being edited.
   */
  public VCSServerVO getServerVO() {

    // Create the VO
    final VCSServerVO result = new VCSServerVO();
    result.setType(Integer.parseInt(flTypes.getSelectedValue()));
    result.setDescription(flDescription.getValue());
    result.setName(flServerName.getValue());

    final PropertyToInputMap<VCSServerAttributeVO> propertyToInputMap = currentAttributePanel.propertyToInputMap();
    final List<VCSServerAttributeVO> updatedProperties = propertyToInputMap.getUpdatedProperties();
    result.setAttributes(updatedProperties);

    // Return result
    return result;
  }


  /**
   * Shows a selected server attribute panel per the list box selection.
   *
   * @param flServerTypes the VCS server types dropdown
   */
  private void showAttributePanel(final ListBox flServerTypes) {

    // Clear the container
    if (currentAttributePanel != null) {
      attributeContainerPanel.remove(currentAttributePanel);
    }

    // Get the matching panel
    final int selectedCode = getSelectedCode(flServerTypes);
    if (attributePanels[selectedCode] == null) {
      attributePanels[selectedCode] = createAttributePanel(selectedCode);
    }
    currentAttributePanel = attributePanels[selectedCode];

    // Add new panel
    attributeContainerPanel.add(currentAttributePanel);
  }


  /**
   * Creates a VCS server attribute panel.
   *
   * @param code the VCS code per {@link VersionControlSystem}
   * @return a new {@link VCSServerAttributePanel}.
   * @see VersionControlSystem
   */
  private VCSServerAttributePanel createAttributePanel(final int code) {

    if (code == SCM_GIT) {
      return new GitServerAttributePanel();
    } else {
      return new DummyServerAttributePanel(2);
    }
  }


  /**
   * Returns a selected VCS type.
   *
   * @param flServerTypes the VCS server types dropdown
   * @return selected type code.
   */
  private int getSelectedCode(final ListBox flServerTypes) {

    final String selectedValue = flServerTypes.getSelectedValue();
    return Integer.parseInt(selectedValue);
  }
}