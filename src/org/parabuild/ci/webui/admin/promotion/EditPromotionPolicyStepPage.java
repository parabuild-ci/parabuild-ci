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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.PromotionPolicy;
import org.parabuild.ci.object.PromotionPolicyStep;
import org.parabuild.ci.promotion.PromotionConfigurationManager;
import org.parabuild.ci.webui.admin.system.AuthenticatedSystemConfigurationPage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.SelectProjectPanel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Parameters;

/**
 * EditPromotionPolicyStepPage
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 9, 2009 7:50:34 PM
 */
public final class EditPromotionPolicyStepPage extends AuthenticatedSystemConfigurationPage implements ConversationalTierlet {

  private static final String CAPTION_ADDING_PROMOTION_POLICY = "Adding Promotion Policy Step";
  private static final String CAPTION_EDITING_PROMOTION_POLICY = "Editing Promotion Policy Step: ";
  private static final String MESSAGE_REQUESTED_POLICY_NOT_FOUND = "Requested policy not found";
  private static final long serialVersionUID = 2172947169611766581L;


  public EditPromotionPolicyStepPage() {
    super(FLAG_SHOW_HEADER_SEPARATOR | FLAG_SHOW_PAGE_HEADER_LABEL);
  }
  protected Result executeAuthenticatedPage(final Parameters parameters) {
    if (parameters.isParameterPresent(Pages.PARAM_PROMOTION_POLICY_STEP_ID)) {
      // Process promotion policy step edit

      // validate
      final String stringStepID = parameters.getParameterValue(Pages.PARAM_PROMOTION_POLICY_STEP_ID);
      if (!StringUtils.isValidInteger(stringStepID)) {
        return WebuiUtils.showNotFound(this);
      }
      final PromotionPolicyStep step = PromotionConfigurationManager.getInstance().getPromotionPolicyStep(Integer.parseInt(stringStepID));
      if (step == null) {
        return WebuiUtils.showNotFound(this);
      }

      // Proceed to editing promotion step config
      final EditPromotionPolicyStepPanel pnlEdit = new EditPromotionPolicyStepPanel();
      baseContentPanel().add(pnlEdit);
      pnlEdit.load(step);

      // Set title
      setPageHeaderAndTitle(CAPTION_EDITING_PROMOTION_POLICY + step.getName());

      // Surrender control
      return Result.Continue();
    } else if (parameters.isParameterPresent(Pages.PARAM_PROMOTION_POLICY_ID)) {
      // process policy section

      // validate
      final String policyID = parameters.getParameterValue(Pages.PARAM_PROMOTION_POLICY_ID);
      if (!StringUtils.isValidInteger(policyID)) {
        return showPolicyNotFound();
      }
      final PromotionPolicy policy = PromotionConfigurationManager.getInstance().getPromotionPolicy(Integer.parseInt(policyID));
      if (policy == null) {
        return showPolicyNotFound();
      }

      setPageHeaderAndTitle(CAPTION_ADDING_PROMOTION_POLICY);

      // Process new promotion policy step with policy selected
      final EditPromotionPolicyStepPanel pnlEdit = new EditPromotionPolicyStepPanel();
      baseContentPanel().add(pnlEdit);
      pnlEdit.load(policy);

      // surrender control
      return Result.Continue();
    } else {
      // proceed to project selection
      final SelectProjectPanel pnlSelectProject = new SelectProjectPanel(Pages.PAGE_EDIT_PROMOTION_POLICY, Pages.PAGE_PROMOTION_POLICY_LIST);
      baseContentPanel().add(pnlSelectProject);

      setPageHeaderAndTitle(CAPTION_ADDING_PROMOTION_POLICY);

      return Result.Continue();
    }
  }



  /**
   * Helper.
   */
  private Result showPolicyNotFound() {
    baseContentPanel().showErrorMessage(MESSAGE_REQUESTED_POLICY_NOT_FOUND);
    return Result.Done();
  }
}
