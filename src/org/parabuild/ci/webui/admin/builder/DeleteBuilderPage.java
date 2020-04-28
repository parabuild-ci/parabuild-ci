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
package org.parabuild.ci.webui.admin.builder;

import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This page is repsonsible for editing Parabuild system
 * properties.
 */
public final class DeleteBuilderPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -6608031121284992231L;  // NOPMD

  private static final String DELETE_BUILDER = "Delete Build Farm";
  private static final String REQUESTED_BUILDER_CAN_NOT_BE_FOUND = "Requested build farm not found.";

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeleteBuilderPage() {
    setTitle(makeTitle(DELETE_BUILDER));
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters parameters) {

    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_DELETE_BUILDER, parameters);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // check if builderedBuilderConfiguration exists, show error message if not
    final BuilderConfiguration builderConfig = ParameterUtils.getBuilderFromParameters(parameters);
    final MessagePanel cp = super.baseContentPanel();
    if (builderConfig == null) {
      cp.showErrorMessage(REQUESTED_BUILDER_CAN_NOT_BE_FOUND);
      cp.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILDERS));
      return Result.Done();
    }

    setTitle(makeTitle("Delete Build Farm >> " + builderConfig.getName()));

    if (isNew()) {
      // Check if there are active builds associated with this builder.
      final List usedIn = new ArrayList(1);
      final List buildsConfigs = ConfigurationManager.getInstance().getExistingBuildConfigsOrderedByID();
      for (final Iterator i = buildsConfigs.iterator(); i.hasNext();) {
        final BuildConfig buildConfig = (BuildConfig) i.next();
        if (buildConfig.getBuilderID() == builderConfig.getID()) {
          usedIn.add(buildConfig);
        }
      }
      if (!usedIn.isEmpty()) {
        final StringBuffer errorMessage = new StringBuffer(200);
        errorMessage.append("This build farm cannot be deleted because it is used by the following active build(s): ");
        for (int i = 0; i < usedIn.size(); i++) {
          final BuildConfig buildConfig = (BuildConfig) usedIn.get(i);
          errorMessage.append(buildConfig.getBuildName());
          if (i < usedIn.size() - 1) {
            errorMessage.append(", ");
          }
        }
        errorMessage.append(". Change the configurations and then try to delete this build farm again.");
        cp.showErrorMessage(errorMessage);
        cp.getUserPanel().add(WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILDERS));
        return Result.Done();
      }

      // display form and hand control to builderedBuilderConfiguration
      final MessagePanel confirmDeletePanel = makeContent(builderConfig.getName());
      cp.getUserPanel().add(confirmDeletePanel);
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return Result.Done(Pages.ADMIN_BUILDERS);
      } else if (action == ACTION_DELETE) {
        // delete builderedBuilderConfiguration
        BuilderConfigurationManager.getInstance().deleteBuilder(builderConfig);
        // show success message
        cp.getUserPanel().clear();
        cp.getUserPanel().add(new Flow()
                .add(new BoldCommonLabel("Build farm has been deleted. "))
                .add(WebuiUtils.clickHereToContinue(Pages.ADMIN_BUILDERS)));
        return Result.Done();
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeContent(final String builderName) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to delete build farm \"" + builderName + "\". Press \"Delete\" button to confirm.");
    confirmationRequestLabel.setHeight(30);
    confirmationRequestLabel.setAlignY(Layout.CENTER);

    // buttons
    final Flow buttons = new Flow();
    final CommonButton cancelDeleteButton = new CancelButton();
    final CommonButton confimDeleteButton = new DeleteButton();
    buttons.add(cancelDeleteButton).add(new BoldCommonLabel("    ")).add(confimDeleteButton);

    // panel and layout
    final MessagePanel panel = new MessagePanel(false);
    panel.getUserPanel().add(confirmationRequestLabel);
    panel.getUserPanel().add(buttons);

    // messaging
    confimDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -507376257058854463L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 5380893921462649489L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }


  public String toString() {
    return "DeleteBuilderPage{" +
            "action=" + action +
            '}';
  }
}
