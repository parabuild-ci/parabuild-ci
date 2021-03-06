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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.DeleteButton;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is repsonsible for deleting a merge.
 */
public final class DeleteMergePage extends BaseMergePage implements ConversationalTierlet {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(DeleteMergePage.class); // NOPMD
  private static final long serialVersionUID = -6608031121284992231L; // NOPMD

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeleteMergePage() {
    setTitle(makeTitle("Delete Merge"));
  }


  /**
   * Lifecycle callback
   */
  public Result executeMergePage(final Parameters params, final MergeConfiguration mergeConfiguration) {

    // authenticate
    if (!isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.PAGE_MERGE_DELETE, params);
    }

    // authorise
    if (!super.getMergeUserRigths(mergeConfiguration.getActiveMergeID()).isAllowedToDeleteMerge()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    final MergeManager cm = MergeManager.getInstance();
    final ActiveMergeConfiguration activeMergeConfig = cm.getActiveMergeConfiguration(mergeConfiguration.getActiveMergeID());
    setTitle(makeTitle("Delete Merge Configuration >> " + activeMergeConfig.getName()));

    if (isNew()) {
      // display form and hand control to user
      baseContentPanel().getUserPanel().add(makeConfirmDeletePanel(activeMergeConfig.getName()));
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return processCancel(params);
      } else if (action == ACTION_DELETE) {
        return processDelete(mergeConfiguration, activeMergeConfig.getName());
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeConfirmDeletePanel(final String mergeName) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to delete merge \"" + mergeName
      + "\". Press \"Delete\" button to confirm.");
    confirmationRequestLabel.setHeight(30);
    confirmationRequestLabel.setAlignY(Layout.CENTER);

    // buttons
    final CommonButton cancelDeleteButton = new CancelButton();
    final CommonButton confimDeleteButton = new DeleteButton();
    final Flow buttons = new Flow();
    buttons.add(cancelDeleteButton).add(new BoldCommonLabel("    ")).add(confimDeleteButton);

    // panel and layout
    final MessagePanel panel = new MessagePanel(false);
    panel.getUserPanel().add(confirmationRequestLabel);
    panel.getUserPanel().add(buttons);

    // messaging
    confimDeleteButton.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        action = DeleteMergePage.ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      public Result buttonPressed(final ButtonPressedEvent event) {
        action = DeleteMergePage.ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }


  /**
   * Processes delete request.
   *
   * @param activeMerge
   *
   * @return Result.Done() if deleted or Result.Continue() if
   *         preconditions didn't hold and delete was not
   *         performed.
   */
  private Result processDelete(final MergeConfiguration activeMerge, final String name) {
    // delete merge config
    MergeManager.getInstance().removeMerge(activeMerge.getID());

    // show success message
    baseContentPanel().getUserPanel().clear();
    baseContentPanel().getUserPanel().add(new Flow()
      .add(new BoldCommonLabel("Merge has been deleted: " + name))
      .add(WebuiUtils.clickHereToContinue(Pages.PAGE_MERGE_LIST)));
    return Result.Done();
  }


  private Result processCancel(final Parameters params) {
    // return to the coomand list
    return Result.Done(Pages.PAGE_MERGE_COMMANDS, params);
  }
}
