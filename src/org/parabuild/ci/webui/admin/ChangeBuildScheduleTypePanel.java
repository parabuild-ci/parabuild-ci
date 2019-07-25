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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.DropDownSelectedEvent;
import viewtier.ui.DropDownSelectedListener;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel to display form for selecting anew schedule type.
 */
final class ChangeBuildScheduleTypePanel extends MessagePanel {

  private static final long serialVersionUID = -5288483670599577222L;

  private static final String CAPTION_RUN_THIS_BUILD_IN_PARALLEL_WITH = "Run this build in parallel with:";

  private final CodeNameDropDown ddTargetSchedule = new CodeNameDropDown(false);
  private final Button btnConvert = createConvertButton();
  private final Button btnCancel = createCancelButton();
  private final ReferenceableBuildNameDropdown ddParentBuildName = new ReferenceableBuildNameDropdown();
  private final CommonFieldLabel lbParentBuildName = new CommonFieldLabel(CAPTION_RUN_THIS_BUILD_IN_PARALLEL_WITH);

  private final int activeBuildID;
  private final byte sourceScheduleType;


  /**
   * Constructor.
   *
   * @param buildConfig
   * @noinspection ThisEscapedInObjectConstruction
   */
  ChangeBuildScheduleTypePanel(final BuildConfig buildConfig) {
    super.showContentBorder(false);
    sourceScheduleType = buildConfig.getScheduleType();
    activeBuildID = buildConfig.getActiveBuildID();

    //
    ddParentBuildName.excludeBuildID(buildConfig.getBuildID());

    //
    ddTargetSchedule.addListener(new ScheduleDropDownSelectedListener(this));
    switch (sourceScheduleType) {
      case BuildConfig.SCHEDULE_TYPE_MANUAL:
        addTypeToDropDown(BuildConfig.SCHEDULE_TYPE_AUTOMATIC);
        break;
      case BuildConfig.SCHEDULE_TYPE_AUTOMATIC:
        addTypeToDropDown(BuildConfig.SCHEDULE_TYPE_MANUAL);
        addTypeToDropDown(BuildConfig.SCHEDULE_TYPE_PARALLEL);
        break;
      default:
        break;
    }

    // Layout
    final CommonFlow flwControls = new CommonFlow(btnConvert, new Label("   "), btnCancel);
    flwControls.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    flwControls.setAlignX(Layout.CENTER);
    final GridIterator gi = new GridIterator(getUserPanel(), 2);
    gi.addPair(new CommonFieldLabel("Change schedule type from " + BuildConfig.getScheduleTypeAsString(sourceScheduleType) + " to: "), ddTargetSchedule);
    gi.addPair(lbParentBuildName, ddParentBuildName);
    gi.addBlankLine();
    gi.add(flwControls, 2);

    //
    showParentBuildName(false);
  }


  private void showParentBuildName(final boolean show) {
    lbParentBuildName.setVisible(show);
    ddParentBuildName.setVisible(show);
  }


  private Tierlet.Result processConvert() {
    try {

      // Convert
      final BuildScheduleConverter converter = createConverter();
      converter.convert(activeBuildID);

      // Reload configuration
      final BuildManager buildManager = BuildManager.getInstance();
      buildManager.notifyConfigurationsChanged();
      buildManager.notifyConfigurationChanged(activeBuildID);

      // Forward to build statuses page
      return Tierlet.Result.Done(Pages.PUBLIC_BUILDS);

    } catch (final ValidationException e) {
      showErrorMessage(StringUtils.toString(e));
      return Tierlet.Result.Continue();
    }
  }


  /**
   * @noinspection NestedSwitchStatement
   */
  private BuildScheduleConverter createConverter() throws ValidationException {
    final int destinationScheduleType = ddTargetSchedule.getCode();
    switch (sourceScheduleType) {
      case BuildConfig.SCHEDULE_TYPE_MANUAL:
        if (destinationScheduleType == BuildConfig.SCHEDULE_TYPE_AUTOMATIC) {
          return new ManualToAutomaticConverter();
        }
        throw new ValidationException("This combination is not supported: " + sourceScheduleType + '/' + destinationScheduleType);
      case BuildConfig.SCHEDULE_TYPE_AUTOMATIC:
        switch (destinationScheduleType) {
          case BuildConfig.SCHEDULE_TYPE_MANUAL:
            return new AutomaticToManualConverter();
          case BuildConfig.SCHEDULE_TYPE_PARALLEL:
            return new AutomaticToParallelConverter(ddParentBuildName.getCode());
          default:
            throw new ValidationException("This combination is not supported: " + sourceScheduleType + '/' + destinationScheduleType);
        }
      default:
        throw new ValidationException("This combination is not supported: " + sourceScheduleType + '/' + destinationScheduleType);
    }
  }


  /**
   * Processes cancelling build change.
   *
   * @return result that forwards back to the list of commands.
   */
  private Tierlet.Result processCancel() {
    final Parameters properties = new Parameters();
    properties.addParameter(Pages.PARAM_BUILD_ID, activeBuildID);
    return Tierlet.Result.Done(Pages.ADMIN_BUILD_COMMANDS_LIST, properties);
  }


  /**
   * @param scheduleType
   */
  private void addTypeToDropDown(final byte scheduleType) {
    ddTargetSchedule.addCodeNamePair(scheduleType, BuildConfig.getScheduleTypeAsString(scheduleType));
  }


  /**
   * Creates Convert button.
   *
   * @return Convert button.
   */
  private Button createConvertButton() {
    final Button button = new Button(" Convert ");
    button.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -5239993944004061315L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        if (validate()) {
          return processConvert();
        } else {
          return Tierlet.Result.Continue();
        }
      }
    });
    return button;
  }


  /**
   * Validates input.
   *
   * @return true if input is valid
   */
  private boolean validate() {

    final List errors = new ArrayList(1);

    if (ddTargetSchedule.getCode() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
      // Check if parent build name is selected
      if (ddParentBuildName.getCode() == BuildConfig.UNSAVED_ID) {
        errors.add("Please select \"" + CAPTION_RUN_THIS_BUILD_IN_PARALLEL_WITH + '\"');
      }
    }

    // Return if it is OK
    if (errors.isEmpty()) {
      return true;
    }

    // Display errors
    showErrorMessage(errors);
    return false;
  }


  /**
   * Creates Cancel button.
   *
   * @return Cancel button.
   */
  private CancelButton createCancelButton() {
    final CancelButton button = new CancelButton();
    button.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -6226902600894731940L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        return processCancel();
      }
    });
    return button;
  }


  public String toString() {
    return "ChangeBuildScheduleTypePanel{" +
            "codeNameDropDown=" + ddTargetSchedule +
            ", btnConvert=" + btnConvert +
            ", btnCancel=" + btnCancel +
            '}';
  }


  private static final class ScheduleDropDownSelectedListener implements DropDownSelectedListener {

    private static final long serialVersionUID = -3563689341714719922L;
    private final ChangeBuildScheduleTypePanel pnlChangeBuildScheduleType;


    ScheduleDropDownSelectedListener(final ChangeBuildScheduleTypePanel pnlChangeBuildScheduleType) {
      this.pnlChangeBuildScheduleType = pnlChangeBuildScheduleType;
    }


    public Tierlet.Result dropDownSelected(final DropDownSelectedEvent dropDownSelectedEvent) {
      final CodeNameDropDown dropDown = (CodeNameDropDown) dropDownSelectedEvent.getDropDown();
      if (dropDown.getCode() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
        // Show leader selection
        pnlChangeBuildScheduleType.showParentBuildName(true);
      } else {
        // Hide leader selection
        pnlChangeBuildScheduleType.showParentBuildName(false);
      }
      return Tierlet.Result.Continue();
    }


    public String toString() {
      return "ScheduleDropDownSelectedListener{" +
              "pnlChangeBuildScheduleType=" + pnlChangeBuildScheduleType +
              '}';
    }
  }
}
