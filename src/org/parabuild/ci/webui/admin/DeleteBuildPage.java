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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonLabel;
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

import java.util.Iterator;
import java.util.List;

/**
 * This page is repsonsible for editing Parabuild system
 * properties.
 */
public final class DeleteBuildPage extends BasePage implements ConversationalTierlet {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(DeleteBuildPage.class); // NOPMD
  private static final long serialVersionUID = -6608031121284992231L; // NOPMD

  private static final int ACTION_DELETE = 1;
  private static final int ACTION_CANCEL = 2;
  private static final int ACTION_DEACTIVATE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeleteBuildPage() {
    setTitle(makeTitle("Delete build"));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters parameters) {

    // authenticate
    if (!isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.ADMIN_DELTE_BUILD, parameters);
    }

    // check if build exists, show error message if not
    final ActiveBuild activeBuild = ParameterUtils.getActiveBuildFromParameters(parameters);
    if (activeBuild == null) {
      return WebuiUtils.showBuildNotFound(this);
    }

    // authorise
    if (!super.getUserRights(activeBuild.getID()).isAllowedToDeleteBuild()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ActiveBuildConfig activeBuildConfig = cm.getActiveBuildConfig(activeBuild.getID());
    setTitle(makeTitle("Delete build >> " + activeBuildConfig.getBuildName()));

    if (isNew()) {
      // validate no referencing
      final List referencingConfigs = cm.getReferencingBuildConfigs(activeBuildConfig.getBuildID());
      if (!referencingConfigs.isEmpty()) {
        warnReferencesFound(activeBuildConfig.getBuildName(), referencingConfigs);
        baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILD_COMMANDS_LIST, activeBuildConfig.getBuildID()));
        return Result.Done(); // exit
      }

      // validate no merges

      final List mergeNameList = MergeManager.getInstance().getMergeNamesByBuildID(activeBuildConfig.getBuildID());
      if (!mergeNameList.isEmpty()) {
        warnMergesFound(activeBuildConfig.getBuildName(), mergeNameList);
        baseContentPanel().getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILD_COMMANDS_LIST, activeBuildConfig.getBuildID()));
        return Result.Done(); // exit
      }

      // display form and hand control to user
      baseContentPanel().getUserPanel().add(makeConfirmDeletePanel(activeBuildConfig.getBuildName()));
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return processCancel(parameters);
      } else if (action == ACTION_DELETE) {
        return processDelete(activeBuild, activeBuildConfig.getBuildName());
      } else if (action == ACTION_DEACTIVATE) {
        return processDeactivate(activeBuild, activeBuildConfig.getBuildName());
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeConfirmDeletePanel(final String buildName) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to delete build \"" + buildName
            + "\". Deleting the build will also delete all build results! " +
            "To preserve the build results, deactivate the build. " +
            "Are you sure you want to delete the build? Press \"Delete\" button to confirm.");
    confirmationRequestLabel.setHeight(30);
    confirmationRequestLabel.setAlignY(Layout.CENTER);

    // buttons
    final CommonButton cancelDeleteButton = new CancelButton();
    final CommonButton confimDeleteButton = new DeleteButton();
    final CommonButton confimDeactivateButton = new CommonButton(" Deactivate ");
    final Flow buttons = new Flow();
    buttons.add(cancelDeleteButton).add(new CommonLabel("    ")).add(confimDeleteButton).add(new CommonLabel("    ")).add(confimDeactivateButton);

    // panel and layout
    final MessagePanel panel = new MessagePanel(false);
    panel.getUserPanel().add(confirmationRequestLabel);
    panel.getUserPanel().add(buttons);

    // messaging
    confimDeleteButton.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    confimDeactivateButton.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DEACTIVATE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }


  /**
   * Processes delete request.
   *
   * @param activeBuild
   * @return Result.Done() if deleted or Result.Continue() if
   *         preconditions didn't hold and delete was not
   *         performed.
   */
  private Result processDelete(final ActiveBuild activeBuild, final String name) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final List referencingConfigs = cm.getReferencingBuildConfigs(activeBuild.getID());
    if (!referencingConfigs.isEmpty()) {
      warnReferencesFound(name, referencingConfigs);
      return Result.Continue();
    }


    final List dependingConfigs = cm.getDependingBuildConfigs(activeBuild.getID());
    if (!dependingConfigs.isEmpty()) {
      warnReferencesFound(name, dependingConfigs);
      return Result.Continue();
    }

    // delete build config
    final BuildListService buildService = ServiceManager.getInstance().getBuildListService();
    buildService.removeBuild(activeBuild.getID());

    // Go to statuses page
    return Result.Done(Pages.ADMIN_BUILDS);
  }


  /**
   * Handles request to deactivate the build.
   *
   * @param activeBuild
   * @param name
   * @return
   */
  private Result processDeactivate(final ActiveBuild activeBuild, final String name) {
    // deactivate build config
    BuildManager.getInstance().deactivateBuild(activeBuild.getID(), getUserID());

    // Go to statuses page
    return Result.Done(Pages.ADMIN_BUILDS);
  }


  private void warnReferencesFound(final String name, final List referencingConfigs) {
    // show error message
    baseContentPanel().clearMessage();
    final StringBuffer err = new StringBuffer(200);
    err.append("The build configuration \"").append(name)
            .append("\" cannot be removed because this build configuration is referred by the following dependant build configurations: ");
    for (final Iterator i = referencingConfigs.iterator(); i.hasNext();) {
      final BuildConfig referencingConfig = (BuildConfig) i.next();
      err.append('\"').append(referencingConfig.getBuildName()).append('\"');
      if (i.hasNext()) {
        err.append(", ");
      }
    }
    err.append(". Remove these configurations before removing \"").append(name).append("\".");
    baseContentPanel().showErrorMessage(err);
  }


  private void warnMergesFound(final String name, final List mergeNameList) {
    // show error message
    baseContentPanel().clearMessage();
    final StringBuffer err = new StringBuffer(200);
    err.append("The build configuration \"").append(name)
            .append("\" cannot be removed because this build configuration is referred by the following automatic merge configurations: ");
    for (final Iterator i = mergeNameList.iterator(); i.hasNext();) {
      err.append('\"').append(i.next()).append('\"');
      if (i.hasNext()) {
        err.append(", ");
      }
    }
    err.append(". Remove these configurations before removing \"").append(name).append("\".");
    baseContentPanel().showErrorMessage(err);
  }


  private Result processCancel(final Parameters params) {
    // return to the coomand list
    return Result.Done(Pages.ADMIN_BUILD_COMMANDS_LIST, params);
  }
}
