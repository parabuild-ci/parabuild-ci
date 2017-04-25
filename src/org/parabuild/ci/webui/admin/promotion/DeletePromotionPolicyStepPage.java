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
package org.parabuild.ci.webui.admin.promotion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.PromotionPolicyStep;
import org.parabuild.ci.promotion.PromotionConfigurationManager;
import org.parabuild.ci.webui.admin.system.AuthenticatedSystemConfigurationPage;
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
 * This page is repsonsible for deleting a promotion policy.
 */
public final class DeletePromotionPolicyStepPage extends AuthenticatedSystemConfigurationPage implements ConversationalTierlet {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(DeletePromotionPolicyStepPage.class); // NOPMD
  private static final long serialVersionUID = -6608031121284992231L; // NOPMD
  private static final String CAPTION_DELETING = "Deleting Promotion Policy Step";

  public static final int ACTION_DELETE = 1;
  public static final int ACTION_CANCEL = 2;
  public static final int ACTION_NONE = 3;

  private int action = ACTION_CANCEL;


  /**
   * Creates page
   */
  public DeletePromotionPolicyStepPage() {
    setTitle(makeTitle(CAPTION_DELETING));
  }


  /**
   * Strategy method to be implemented by classes inheriting
   * BasePage.
   *
   * @param params
   * @return result of page execution
   */
  protected Result executeAuthenticatedPage(final Parameters params) {

    final String stringStepID = params.getParameterValue(Pages.PARAM_PROMOTION_POLICY_STEP_ID);
    if (log.isDebugEnabled()) {
      log.debug("stringStepID: " + stringStepID);
    }
    if (!StringUtils.isValidInteger(stringStepID)) {
      return WebuiUtils.showNotFound(this);
    }


    final PromotionPolicyStep step = PromotionConfigurationManager.getInstance().getPromotionPolicyStep(Integer.parseInt(stringStepID));
    if (step == null) {
      return WebuiUtils.showNotFound(this);
    }

    setTitle(makeTitle(CAPTION_DELETING + " >> " + step.getName()));
    if (isNew()) {
      // display form and hand control to user
      baseContentPanel().getUserPanel().add(makeConfirmDeletePanel(step.getName()));
      return Result.Continue();
    } else {
      if (action == ACTION_CANCEL) {
        return processCancel(params);
      } else if (action == ACTION_DELETE) {
        return processDelete(step, step.getName());
      }
      return Result.Continue();
    }
  }


  private MessagePanel makeConfirmDeletePanel(final String stepName) {
    // request
    final Label confirmationRequestLabel = new BoldCommonLabel(
            "You are about to delete promotion policy step \"" + stepName
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
      private static final long serialVersionUID = 6611765807491996520L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        action = ACTION_DELETE;
        return null;
      }
    });
    cancelDeleteButton.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 2014835871841208746L;


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
   * @param step
   * @return Result.Done() if deleted or Result.Continue() if
   *         preconditions didn't hold and delete was not
   *         performed.
   */
  private Result processDelete(final PromotionPolicyStep step, final String name) {
    // delete promotion step
    PromotionConfigurationManager.getInstance().removePolicyStep(step.getID());

    // show success message
    baseContentPanel().getUserPanel().clear();
    baseContentPanel().getUserPanel().add(new Flow()
            .add(new BoldCommonLabel("Promotion step has been deleted: " + name))
            .add(WebuiUtils.clickHereToContinue(Pages.PAGE_VIEW_PROMOTION_POLICY_DETAILS)));
    return Result.Done();
  }


  private Result processCancel(final Parameters params) {
    // return to the coomand list
    return Result.Done(Pages.PAGE_VIEW_PROMOTION_POLICY_DETAILS, params);
  }


  public String toString() {
    return "DeletePromotionPolicyStepPage{" +
            "action=" + action +
            '}';
  }
}
