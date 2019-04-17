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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.merge.MergeState;
import org.parabuild.ci.merge.MergeStatus;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Color;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is repsonsible for resetting data for all
 * paused merges.
 */
public final class ResetAllMergesPage extends BasePage implements ConversationalTierlet {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(ResetAllMergesPage.class); // NOPMD
  private static final long serialVersionUID = -6608031121284992231L; // NOPMD

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public ResetAllMergesPage() {
    setTitle(makeTitle("Reset All Paused Merges"));
  }


  /**
   * Strategy method to be implemented by classes inheriting
   * BasePage.
   *
   * @param parameters
   *
   * @return result of page execution
   */
  protected Result executePage(final Parameters parameters) {

    // authenticate
    if (!isValidAdminUser()) return WebuiUtils.showNotAuthorized(this);

    if (isNew()) {
      // display form and hand control to user
      baseContentPanel().getUserPanel().add(makeConfirmResetPanel());
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return processCancel();
      } else if (action == ACTION_DELETE) {
        return processReset();
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeConfirmResetPanel() {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel("You are about to reset data for ALL paused merges. Press \"Reset\" button to confirm.");
    confirmationRequestLabel.setHeight(30);
    confirmationRequestLabel.setAlignY(Layout.CENTER);

    // buttons
    final CommonButton cancelButton = new CancelButton();
    final CommonButton confimButton = new CommonButton(" Reset ");
    final Flow buttons = new Flow();
    buttons.add(cancelButton).add(new BoldCommonLabel("    ")).add(confimButton);

    // panel and layout
    final MessagePanel panel = new MessagePanel(false);
    panel.getUserPanel().add(confirmationRequestLabel);
    panel.getUserPanel().add(buttons);

    // messaging
    confimButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 4776484113326682559L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ResetAllMergesPage.ACTION_DELETE;
        return null;
      }
    });
    cancelButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -1704744827788079412L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ResetAllMergesPage.ACTION_CANCEL;
        return null;
      }
    });
    return panel;
  }


  /**
   * Processes delete request.
   *
   * @return Result.Done() if deleted or Result.Continue() if
   *         preconditions didn't hold and delete was not
   *         performed.
   */
  private Result processReset() {
    // clear panel
    baseContentPanel().getUserPanel().clear();

    // process
    final MergeManager mm = MergeManager.getInstance();
    final List mergeStatuses = mm.getMergeStatuses();
    for (int i = 0; i < mergeStatuses.size(); i++) {
      final MergeState state = (MergeState)mergeStatuses.get(i);
      if (state.getStatus().equals(MergeStatus.PAUSED)) {
        // reset
        try {
          mm.resetMerge(state.getActiveMergeConfigurationID());
        } catch (final Exception e) {
          final BoldCommonLabel boldCommonLabel = new BoldCommonLabel("Error while deleting: " + StringUtils.toString(e));
          boldCommonLabel.setForeground(Color.Red);
          baseContentPanel().getUserPanel().add(boldCommonLabel);
        }
      }
    }

    // show success message
    baseContentPanel().getUserPanel().add(new Flow()
      .add(new BoldCommonLabel("Data has been reset"))
      .add(WebuiUtils.clickHereToContinue(Pages.PAGE_MERGE_LIST)));
    return Result.Done();
  }


  private Result processCancel() {
    // return to the coomand list
    return Result.Done(Pages.PAGE_UNDOCUMENTED_COMMANDS);
  }
}
