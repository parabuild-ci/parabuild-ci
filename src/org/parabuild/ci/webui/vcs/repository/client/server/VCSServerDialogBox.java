package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.webui.vcs.repository.common.CancelButton;
import org.parabuild.ci.webui.vcs.repository.common.CancelButtonClickHandler;
import org.parabuild.ci.webui.vcs.repository.common.EditDialogBox;
import org.parabuild.ci.webui.vcs.repository.common.FlexTableIterator;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildTextBox;
import org.parabuild.ci.webui.vcs.repository.common.SaveButton;
import org.parabuild.ci.webui.vcs.repository.common.VCSServerVO;

import java.util.List;

import static org.parabuild.ci.common.VersionControlSystem.SCM_COUNT;
import static org.parabuild.ci.common.VersionControlSystem.SCM_GIT;

/**
 * Repository dialog box is responsible for editing and displaying Repository information.
 */
@SuppressWarnings("WeakerAccess")
public final class VCSServerDialogBox extends EditDialogBox {

  // Lookup table for attribute panels
  private final VCSServerAttributePanel[] attributePanels = new VCSServerAttributePanel[SCM_COUNT];

  // Layout widgets
  private final ParabuildFlexTable flexTable = new ParabuildFlexTable(2);

  private final Panel attributeContainerPanel = new SimplePanel();

  // UI widgets
  private final Label lbType = new Label("Server type:");

  private final Label lbDescription = new Label("Server description:");

  private final Label lbName = new Label("Server name:");

  private final TextBox flDescription = new ParabuildTextBox(100, 70);

  private final ParabuildTextBox flName = new ParabuildTextBox(50, 50);

  private final ListBox flTypes = new VCSServerTypeListBox();

  // Current attribute panel
  private VCSServerAttributePanel currentAttributePanel;


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
    flexTableIterator.add(attributeContainerPanel, 2);
    flexTableIterator.add(new SaveButton(new SaveVCSServerClickHandler(this)));
    flexTableIterator.add(new CancelButton(new CancelButtonClickHandler(this)));


    // Set up switching of the panels
    flTypes.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(final ChangeEvent changeEvent) {

        showAttributePanel(VCSServerDialogBox.this.flTypes);
      }
    });

    // Do initial selection
    showAttributePanel(flTypes);

    // Add layout panel
    setWidget(flexTable);
  }


  /**
   * Shows a selected server attribute panel per the list box selection.
   *
   * @param flServerTypes the VCS server types dropdown
   */
  private void showAttributePanel(final ListBox flServerTypes) {

    final int selectedCode = getSelectedCode(flServerTypes);

    // Clear the container
    if (currentAttributePanel != null) {
      attributeContainerPanel.remove(currentAttributePanel);
    }

    // Get the matching panel
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
    result.setName(flName.getValue());

    final List<VCSServerAttributeVO> updatedProperties = currentAttributePanel.propertyToInputMap().getUpdatedProperties();
    result.setAttributes(updatedProperties);

    // Return result
    return result;
  }
}