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

import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.DeleteButton;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This page is repsonsible for removing build logs that are
 * older then number of days.
 */
public final class CleanUpBuildLogArchivePage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L; // NOPMD

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;
  private final CommonField flNumberOfDays = new CommonField(4, 4);
  private MessagePanel pnlDeleteBuild = null; // NOPMD


  /**
   * Creates page
   */
  public CleanUpBuildLogArchivePage() {
    setTitle(makeTitle("Delete logs"));
    setFocusOnFirstInput(true);
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.ADMIN_BUILD_COMMANDS_LIST, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // check if build exists, show error message if not
    final ActiveBuildConfig activeBuildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (activeBuildConfig == null) {
      return WebuiUtils.showBuildNotFound(this);
    }

    // set actual title
    setTitle(makeTitle("Delete logs for build " + activeBuildConfig.getBuildName()));

    // process
    if (isNew()) {
      // display form and hand control to user
      pnlDeleteBuild = makeContent(activeBuildConfig.getBuildName());
      super.baseContentPanel().getUserPanel().add(pnlDeleteBuild);
      return Result.Continue();
    } else {
      if (action == ACTION_DELETE) {

        // validate input
        final List errors = new ArrayList(1);
        InputValidator.validateFieldValidPositiveInteger(errors, "Number of days", flNumberOfDays);
        if (!errors.isEmpty()) {
          pnlDeleteBuild.showErrorMessage(errors);
          return Result.Continue();
        }

        // clenup logs using arhive manager.
        // REVIEWME: simeshev@parabuilci.org - deleteing logs from large
        // log archives can be very slow. consider async processing.
        final ArchiveManager archiveManager = ArchiveManagerFactory.getArchiveManager(activeBuildConfig.getBuildID());
        try {
          archiveManager.deleteExpiredBuildLogs(Integer.parseInt(flNumberOfDays.getValue()));
        } catch (final IOException e) {
          showPageErrorAndExit("Error while deleting expried logs: " + StringUtils.toString(e));
        }

        // display result
        super.baseContentPanel().getUserPanel().clear();
        super.baseContentPanel().getUserPanel().add(new Flow()
          .add(new BoldCommonLabel("Logs older than " + flNumberOfDays.getValue() + " days have been deleted. "))
          .add(WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILD_COMMANDS_LIST, activeBuildConfig.getBuildID())));
        return Result.Done();
      } else if (action == ACTION_CANCEL) {
        // return to
        return Result.Done(Pages.ADMIN_BUILD_COMMANDS_LIST, params);
      } else {
        // unknown command.
        return Result.Continue();
      }
    }
  }


  /**
   * Creates a panel to display request form to delete old build logs.
   */
  private MessagePanel makeContent(final String buildName) {
    final MessagePanel result = new MessagePanel(false);

    // request
    final Label lbConfirmation = new BoldCommonLabel("Warning: You are about to delete logs for build \"" + buildName + "\". Press \"Delete\" button to confirm.");
    lbConfirmation.setHeight(30);
    lbConfirmation.setAlignY(Layout.CENTER);

    final Label lbNumberOfDays = new CommonFieldLabel("Permanently delete build logs older than ");
    final Label lbDays = new CommonFieldLabel(" days");

    // buttons
    final CommonButton btnCancel = new CancelButton();
    final CommonButton btnConfirm = new DeleteButton();
    final Flow buttons = new CommonFlow(btnCancel, new Label("&nbsp;&nbsp;&nbsp;"), btnConfirm);

    // layout
    result.getUserPanel().add(lbConfirmation);
    result.getUserPanel().add(new CommonFlow(lbNumberOfDays, flNumberOfDays, lbDays));
    result.getUserPanel().add(WebuiUtils.makePanelDivider());
    result.getUserPanel().add(buttons);

    // messaging
    btnConfirm.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -2317189860778533691L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    btnCancel.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -9221503771473094397L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return result;
  }

  public String toString() {
    return "CleanUpBuildLogArchivePage{" +
            "action=" + action +
            ", flNumberOfDays=" + flNumberOfDays +
            ", pnlDeleteBuild=" + pnlDeleteBuild +
            '}';
  }
}
