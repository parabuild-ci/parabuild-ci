/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.webui.merge;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.object.MergeServiceConfiguration;
import org.parabuild.ci.object.Project;
import org.parabuild.ci.object.User;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MenuDividerLabel;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.SaveButton;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.CheckBox;
import viewtier.ui.Color;
import viewtier.ui.Component;
import viewtier.ui.DropDownSelectedEvent;
import viewtier.ui.DropDownSelectedListener;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Text;
import viewtier.ui.Tierlet;

/**
 */
final class EditMergePanel extends MessagePanel {

  private static final Log log = LogFactory.getLog(EditMergePanel.class);
  private static final long serialVersionUID = 6574406891458409409L;

  private static final Color SECTION_HEADER_COLOR = new Color(0x006699);

  private static final String CAPTION_BRANCH_DEFINITION = "Branch Definition:";
  private static final String CAPTION_BRANCH_VIEW_SOURCE = "Branch view source: ";
  private static final String CAPTION_CONFLICT_RESOLUTION_MODE = "Conflict resolution mode: ";  // NOPMD
  private static final String CAPTION_DESCRIPTION = "Description: ";
  private static final String CAPTION_MARKER = "Marker: ";
  private static final String CAPTION_MERGE_MODE = "Integration mode: ";
  private static final String CAPTION_MERGE_NAME = "Automerge name: ";
  private static final String CAPTION_MERGE_VERIFICATION = "Merge Verification:";
  private static final String CAPTION_PRESERVE_MARKER = "Preserve marker: ";
  private static final String CAPTION_PROJECT = "Project: ";
  private static final String CAPTION_REVERSE_BRANCH_VIEW = "Reverse branch view: ";
  private static final String CAPTION_SOURCE = "Source: ";
  private static final String CAPTION_TARGET = "Target: ";
  private static final String CAPTION_EDIT = "Edit";
  private static final String CAPTION_COMMANDS = "Commands";
//  private static final String WIDTH_100_PCT = "100%";

  private final Label lbBranchDefinition = new CommonFieldLabel(CAPTION_BRANCH_DEFINITION, SECTION_HEADER_COLOR);   // NOPMD
  private final Label lbBranchVerification = new CommonFieldLabel(CAPTION_MERGE_VERIFICATION, SECTION_HEADER_COLOR);  // NOPMD
  private final Label lbBranchViewSeparator = new Label();  // NOPMD
  private final Label lbBranchViewSource = new CommonFieldLabel(CAPTION_BRANCH_VIEW_SOURCE);
  private final Label lbConflictResolutionMode = new CommonFieldLabel(CAPTION_CONFLICT_RESOLUTION_MODE);  // NOPMD
  private final Label lbDescription = new CommonFieldLabel(CAPTION_DESCRIPTION);  // NOPMD
  private final Label lbIndirectMerges = new CommonFieldLabel("Indirect merges: ");
  private final Label lbMaker = new CommonFieldLabel(CAPTION_MARKER);  // NOPMD
  private final Label lbMergeMode = new CommonFieldLabel(CAPTION_MERGE_MODE);  // NOPMD
  private final Label lbName = new CommonFieldLabel(CAPTION_MERGE_NAME, SECTION_HEADER_COLOR); // NOPMD
  private final Label lbNamedBranchViewSeparator = new Label();  // NOPMD
  private final Label lbPreserveMarker = new CommonFieldLabel(CAPTION_PRESERVE_MARKER);
  private final Label lbProjectName = new CommonFieldLabel(CAPTION_PROJECT); // NOPMD
  private final Label lbProjectNameValue = new CommonLabel(); // NOPMD
  private final Label lbReverseBranchView = new CommonFieldLabel(CAPTION_REVERSE_BRANCH_VIEW);
  private final Label lbSource = new CommonFieldLabel(CAPTION_SOURCE); // NOPMD
  private final Label lbSourceValueSeparator = new Label();  // NOPMD
  private final Label lbTarget = new CommonFieldLabel(CAPTION_TARGET); // NOPMD
  private final Label lbTargetValueSeparator = new Label();  // NOPMD
  private final Label lbCommandsSeparator = new Label();

  private final BranchViewSourceDropdown ddBranchViewSource = new BranchViewSourceDropdown(); // NOPMD
  private final Button btnCancel = new CancelButton(); // NOPMD
  private final Button btnSave = new SaveButton(); // NOPMD
  private final CheckBox cbIndirectMerges = new CheckBox(); // NOPMD
  private final CheckBox cbReverseBranchMapping = new CheckBox(); // NOPMD
  private final CheckBox cbPreserveMarker = new CheckBox(); // NOPMD
  private final CodeNameDropDown flConflictResolutionMode = new ConflictResolutionModeDropDown();  // NOPMD
  private final DepotViewPanel pnlSourceValue = new DepotViewPanel();  // NOPMD
  private final DepotViewPanel pnlTargetValue = new DepotViewPanel();  // NOPMD
  private final Field flDescription = new CommonField(100, 60); // NOPMD
  private final Field flMarker = new CommonField(30, 30); // NOPMD
  private final Field flName = new CommonField(100, 60); // NOPMD
  private final Field flBranchViewName = new CommonField(256, 60); // NOPMD
  private final MergeBuildNameDropdown ddSource = new MergeBuildNameDropdown(); // NOPMD
  private final MergeBuildNameDropdown ddTarget = new MergeBuildNameDropdown(); // NOPMD
  private final MergeModeDropDown ddMode = new MergeModeDropDown(); // NOPMD
  private final Text flBranchView = new Text(100, 6); // NOPMD
  private final CommonCommandLink lnkEdit = new CommonCommandLink(CAPTION_EDIT, Pages.PAGE_MERGE_EDIT); // NOPMD
  private final CommonCommandLink lnkCommands = new CommonCommandLink(CAPTION_COMMANDS, Pages.PAGE_MERGE_COMMANDS); // NOPMD
  private final CommonFlow flwCommands = new CommonFlow(lnkEdit, new MenuDividerLabel(), lnkCommands); // NOPMD

  private int mergeID = MergeConfiguration.UNSAVED_ID; // NOPMD
  private int projectID = Project.UNSAVED_ID;
  private final byte mode;


  public EditMergePanel(final byte mode) {
    this.mode = mode;
//    setWidth(WIDTH_100_PCT);
    showContentBorder(false);
    setWidth(Pages.PAGE_WIDTH);
    setAlignX(Layout.CENTER);
//    getUserPanel().setWidth(WIDTH_100_PCT);

    // layout
    final GridIterator gi = new GridIterator(getUserPanel(), 2);
    gi.add(lbProjectName).add(lbProjectNameValue);
    gi.add(lbName).add(new RequiredFieldMarker(flName));
    if (mode == WebUIConstants.MODE_VIEW) {
      gi.add(lbCommandsSeparator).add(flwCommands);
    }
    gi.add(lbDescription).add(new RequiredFieldMarker(flDescription));
    gi.add(lbMaker).add(new RequiredFieldMarker(flMarker));
    gi.add(lbPreserveMarker).add(cbPreserveMarker);
    gi.add(lbMergeMode).add(ddMode);
    gi.add(lbConflictResolutionMode).add(flConflictResolutionMode);
    gi.add(lbBranchDefinition).add(new Label());
    gi.add(lbBranchViewSource).add(ddBranchViewSource);
    gi.add(lbNamedBranchViewSeparator).add(flBranchViewName);
    gi.add(lbBranchViewSeparator).add(flBranchView);
    gi.add(lbReverseBranchView).add(cbReverseBranchMapping);
    gi.add(lbIndirectMerges).add(cbIndirectMerges);
    gi.add(lbBranchVerification).add(new Label());
    gi.add(lbSource).add(new RequiredFieldMarker(ddSource));
    gi.add(lbSourceValueSeparator).add(pnlSourceValue);
    gi.add(lbTarget).add(new RequiredFieldMarker(ddTarget));
    gi.add(lbTargetValueSeparator).add(pnlTargetValue);
    gi.addBlankLine();
    gi.add(btnSave).add(new CommonFlow(new Label("  "), btnCancel));

    // set up listeners
    btnSave.addListener(makeSaveButtonListener());
    btnCancel.addListener(makeCancelButtonListener());
    ddSource.addListener(makeDepotSelectedListener(lbSourceValueSeparator, pnlSourceValue));
    ddTarget.addListener(makeDepotSelectedListener(lbTargetValueSeparator, pnlTargetValue));
    ddBranchViewSource.addListener(makeBranchViewSelected());

    // appearance
    btnSave.setAlignX(Layout.RIGHT);
    lbSourceValueSeparator.setVisible(false);
    lbTargetValueSeparator.setVisible(false);
    pnlSourceValue.setVisible(false);
    pnlTargetValue.setVisible(false);

    setVisible(lbNamedBranchViewSeparator, flBranchViewName, false);
    setVisible(lbBranchViewSeparator, flBranchView, false);
    setVisible(lbCommandsSeparator, flwCommands, false);

    // defaults
    cbIndirectMerges.setChecked(true);
    cbReverseBranchMapping.setChecked(true);


    setMode(mode);
  }


  public EditMergePanel() {
    this(WebUIConstants.MODE_EDIT);
  }


  /**
   * Sets editability.
   *
   * @param mode
   */
  private void setMode(final byte mode) {
    final boolean editable = mode == WebUIConstants.MODE_EDIT;
    cbIndirectMerges.setEditable(editable);
    cbReverseBranchMapping.setEditable(editable);
    cbPreserveMarker.setEditable(editable);
    ddBranchViewSource.setEditable(editable);
    ddMode.setEditable(editable);
    ddSource.setEditable(editable);
    ddTarget.setEditable(editable);
    flBranchView.setEditable(editable);
    flConflictResolutionMode.setEditable(editable);
    flDescription.setEditable(editable);
    flMarker.setEditable(editable);
    flName.setEditable(editable);
    flBranchViewName.setEditable(editable);
    pnlSourceValue.setMode(mode);
    pnlTargetValue.setMode(mode);
    // buttons
    if (!editable) {
      btnCancel.setVisible(false);
      btnSave.setVisible(false);
    }
  }


  private DropDownSelectedListener makeBranchViewSelected() {
    return new DropDownSelectedListener() {
      private static final long serialVersionUID = 8455176902878515503L;


      public Tierlet.Result dropDownSelected(final DropDownSelectedEvent event) {
        final BranchViewSourceDropdown dropdown = (BranchViewSourceDropdown)event.getDropDown();
        processBranchViewSelection(dropdown.getCode());
        return Tierlet.Result.Continue();
      }
    };
  }


  private void processBranchViewSelection(final int code) {
    if (code == BranchViewSourceDropdown.BRANCH_VIEW_SOURCE_BRANCH_NAME) {
      setVisible(lbNamedBranchViewSeparator, flBranchViewName, true);
      setVisible(lbBranchViewSeparator, flBranchView, false);
    } else if (code == BranchViewSourceDropdown.BRANCH_VIEW_SOURCE_DIRECT) {
      setVisible(lbNamedBranchViewSeparator, flBranchViewName, false);
      setVisible(lbBranchViewSeparator, flBranchView, true);
    } else {
      showErrorMessage("Unknown branch view code: " + code);
    }
  }


  private DropDownSelectedListener makeDepotSelectedListener(final Label lbValueSeparator, final DepotViewPanel pnlValue) {
    return new DropDownSelectedListener() {
      private static final long serialVersionUID = 7990622070558849619L;


      public Tierlet.Result dropDownSelected(final DropDownSelectedEvent event) {
        final MergeBuildNameDropdown dropDown = (MergeBuildNameDropdown)event.getDropDown();
        final int activeBuildID = dropDown.getCode();
        processBuildSelection(activeBuildID, lbValueSeparator, pnlValue);
        return Tierlet.Result.Continue();
      }
    };
  }


  private static void processBuildSelection(final int activeBuildID, final Label lbValueSeparator, final DepotViewPanel pnlValue) {
    if (activeBuildID == BuildConfig.UNSAVED_ID) {
      lbValueSeparator.setVisible(false);
      pnlValue.setVisible(false);
    } else {
      lbValueSeparator.setVisible(true);
      pnlValue.setVisible(true);
      pnlValue.load(activeBuildID);
    }
  }


  private ButtonPressedListener makeCancelButtonListener() {
    return new ButtonPressedListener() {
      private static final long serialVersionUID = 8442531338170420817L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        return mergeListHome();
      }
    };
  }


  private ButtonPressedListener makeSaveButtonListener() {
    return new ButtonPressedListener() {
      private static final long serialVersionUID = -4767172291877393616L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        if (!validate()) return Tierlet.Result.Continue();
        if (save()) return mergeListHome();
        return Tierlet.Result.Continue();
      }
    };
  }


  private boolean save() {
    final ActiveMergeConfiguration mergeConfiguration;
    if (mergeID == MergeConfiguration.UNSAVED_ID) {
      // new merge
      mergeConfiguration = new ActiveMergeConfiguration();
    } else {
      // existing merge
      mergeConfiguration = MergeManager.getInstance().getActiveMergeConfiguration(mergeID);
    }
    mergeConfiguration.setBranchView(flBranchView.getValue());
    mergeConfiguration.setBranchViewName(flBranchViewName.getValue());
    mergeConfiguration.setBranchViewSource((byte)ddBranchViewSource.getCode());
    mergeConfiguration.setMergeMode((byte)ddMode.getCode());
    mergeConfiguration.setConflictResolutionMode((byte)flConflictResolutionMode.getCode());
    mergeConfiguration.setDescription(flDescription.getValue());
    mergeConfiguration.setIndirectMerge(cbIndirectMerges.isChecked());
    mergeConfiguration.setMarker(flMarker.getValue());
    mergeConfiguration.setName(flName.getValue());
    mergeConfiguration.setPreserveMarker(cbPreserveMarker.isChecked());
    mergeConfiguration.setReverseBranchView(cbReverseBranchMapping.isChecked());
    mergeConfiguration.setSourceBuildID(ddSource.getCode());
    mergeConfiguration.setTargetBuildID(ddTarget.getCode());

    MergeManager.getInstance().save(mergeConfiguration);

    if (mergeID == MergeConfiguration.UNSAVED_ID) {
      final MergeServiceConfiguration mergeServiceConfiguration = new MergeServiceConfiguration();
      mergeServiceConfiguration.setProjectID(projectID);
      mergeServiceConfiguration.setID(mergeConfiguration.getID());
      MergeManager.getInstance().save(mergeServiceConfiguration);
    }
    return true;
  }


  private boolean validate() {
    final List errors = new ArrayList(11);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_DESCRIPTION, flDescription);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_MARKER, flMarker);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_MERGE_NAME, flName);
    WebuiUtils.validateDropDownSelected(errors, CAPTION_SOURCE, ddSource, MergeBuildNameDropdown.NOT_SELECTED);
    WebuiUtils.validateDropDownSelected(errors, CAPTION_TARGET, ddTarget, MergeBuildNameDropdown.NOT_SELECTED);
    if (ddSource.getCode() == ddTarget.getCode()) errors.add("Source and target cannot be the same. Please select different target.");
    if (!errors.isEmpty()) showErrorMessage(errors);
    return errors.isEmpty();
  }


  /**
   * Loads only project.
   *
   * @param project
   */
  public void load(final Project project) {
    lbProjectNameValue.setText(project.getName());
    projectID = project.getID();
    ddSource.populate(projectID);
    ddTarget.populate(projectID);
    ddBranchViewSource.setCode(BranchViewSourceDropdown.BRANCH_VIEW_SOURCE_BRANCH_NAME);
    processBranchViewSelection(BranchViewSourceDropdown.BRANCH_VIEW_SOURCE_BRANCH_NAME);
  }


  /**
   * Loads merge.
   * @param mergeConfiguration
   */
  public void load(final MergeConfiguration mergeConfiguration) {
    final int activeMergeID = mergeConfiguration.getActiveMergeID();
    final MergeServiceConfiguration mergeServiceConfiguration = MergeManager.getInstance().getActiveMerge(activeMergeID);
    if (log.isDebugEnabled()) log.debug("activeMerge: " + mergeServiceConfiguration);
    final Project project = ProjectManager.getInstance().getProject(mergeServiceConfiguration.getProjectID());
    if (log.isDebugEnabled()) log.debug("project: " + project);
    load(project);
    mergeID = mergeConfiguration.getID();

    cbIndirectMerges.setChecked(mergeConfiguration.isIndirectMerge());
    cbReverseBranchMapping.setChecked(mergeConfiguration.isReverseBranchView());
    ddBranchViewSource.setCode(mergeConfiguration.getBranchViewSource());
    ddMode.setCode(mergeConfiguration.getMergeMode());
    ddSource.setCode(mergeConfiguration.getSourceBuildID());
    ddTarget.setCode(mergeConfiguration.getTargetBuildID());
    flBranchView.setValue(mergeConfiguration.getBranchView());
    flBranchViewName.setValue(mergeConfiguration.getBranchViewName());
    flConflictResolutionMode.setCode(mergeConfiguration.getConflictResolutionMode());
    flDescription.setValue(mergeConfiguration.getDescription());
    flMarker.setValue(mergeConfiguration.getMarker());
    flName.setValue(mergeConfiguration.getName());

    // show edit link if allowed
    if (mode == WebUIConstants.MODE_VIEW) {
      final SecurityManager sm = SecurityManager.getInstance();
      final User user = sm.getUserFromContext(getTierletContext());
      if (sm.getUserMergeRights(user, activeMergeID).isAllowedToListCommands()) {
        setVisible(lbCommandsSeparator, flwCommands, true);
        lnkCommands.setParameters(WebuiUtils.makeMergeIDParameters(activeMergeID));
        lnkEdit.setParameters(WebuiUtils.makeMergeIDParameters(activeMergeID));
      }
    }

    // process build selection selection
    processBuildSelection(mergeConfiguration.getSourceBuildID(), lbSourceValueSeparator, pnlSourceValue);
    processBuildSelection(mergeConfiguration.getTargetBuildID(), lbTargetValueSeparator, pnlTargetValue);

    // process branch vew selection
    processBranchViewSelection(mergeConfiguration.getBranchViewSource());
  }


  private static Tierlet.Result mergeListHome() {
    return Tierlet.Result.Done(Pages.PAGE_MERGE_LIST);
  }


  private static void setVisible(final Label label, final Component input, final boolean visible) {
    label.setVisible(visible);
    input.setVisible(visible);
  }
}
