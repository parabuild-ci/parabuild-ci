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
import org.parabuild.ci.object.PromotionPolicyStep;
import org.parabuild.ci.promotion.PromotionConfigurationManager;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.admin.system.AuthenticatedSystemConfigurationPage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.List;

/**
 */
public final class ModifyPromotionPolicyStepOrderPage extends AuthenticatedSystemConfigurationPage implements StatelessTierlet {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(ModifyPromotionPolicyStepOrderPage.class); // NOPMD
  private static final long serialVersionUID = 4542828122356933097L;


  public ModifyPromotionPolicyStepOrderPage() {
    super(FLAG_SHOW_HEADER_SEPARATOR | FLAG_SHOW_PAGE_HEADER_LABEL);
  }


  protected Result executeAuthenticatedPage(final Parameters params) {
    if (params.isParameterPresent(Pages.PARAM_PROMOTION_POLICY_ID) && params.isParameterPresent(Pages.PARAM_PROMOTION_POLICY_STEP_ID) && params.isParameterPresent(Pages.PARAM_PROMOTION_STEP_OPERATION_CODE)) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Changing order of promotion steps");
      }

      // Validate
      final String stringPromotionPolicyID = params.getParameterValue(Pages.PARAM_PROMOTION_POLICY_ID);
      if (!StringUtils.isValidInteger(stringPromotionPolicyID)) {
        return WebuiUtils.showNotFound(this);
      }

      final String stringPromotionPolicyStepID = params.getParameterValue(Pages.PARAM_PROMOTION_POLICY_STEP_ID);
      if (!StringUtils.isValidInteger(stringPromotionPolicyStepID)) {
        return WebuiUtils.showNotFound(this);
      }

      // Get policy step
      final PromotionPolicyStep promotionPolicyStep = PromotionConfigurationManager.getInstance().getPromotionPolicyStep(Integer.parseInt(stringPromotionPolicyStepID));
      if (promotionPolicyStep == null) {
        return WebuiUtils.showNotFound(this);
      }

      // Set title
      setPageHeaderAndTitle("Changing Order of " + promotionPolicyStep.getName());

      // Get steps
      final List list = new ArrayList(PromotionConfigurationManager.getInstance().getPromotionStepsList(promotionPolicyStep.getPromotionID()));

      // Create list copy
      for (int i = 0; i < list.size(); i++) {
        final PromotionPolicyStep current = (PromotionPolicyStep) list.get(i);
        if (current.getID() == promotionPolicyStep.getID()) {
          if (isMoveUp(params)) {
            // Process move up
            if (i > 0) {
              final PromotionPolicyStep old = (PromotionPolicyStep) list.get(i - 1);
              list.set(i - 1, current);
              list.set(i, old);
            }
            break;
          } else if (isMoveDown(params)) {
            // Process moves down
            if (i < list.size() - 1) {
              final PromotionPolicyStep old = (PromotionPolicyStep) list.get(i + 1);
              list.set(i, old);
              list.set(i + 1, current);
            }
            break;
          } else {
            baseContentPanel().getUserPanel().clear();
            baseContentPanel().getUserPanel().add(new Flow().add(new BoldCommonLabel("Unknown operation")));
            return Tierlet.Result.Done();
          }
        }
      }

      // Renumber
      for (int i = 0; i < list.size(); i++) {
        final PromotionPolicyStep policyStep = (PromotionPolicyStep) list.get(i);
        policyStep.setLineNumber(i);
        PromotionConfigurationManager.getInstance().save(policyStep);
      }

      // surrender control
      final Parameters forwardParams = PromotionUtils.createPromotionParameters(promotionPolicyStep.getPromotionID());
      return Result.Done(Pages.PAGE_VIEW_PROMOTION_POLICY_DETAILS, forwardParams);
    } else {
      return WebuiUtils.showNotFound(this);
    }
  }


  private static boolean isMoveUp(final Parameters params) {
    return params.getParameterValue(Pages.PARAM_PROMOTION_STEP_OPERATION_CODE).equalsIgnoreCase(Pages.PARAM_PROMOTION_STEP_OPERATION_UP);
  }


  private static boolean isMoveDown(final Parameters params) {
    return params.getParameterValue(Pages.PARAM_PROMOTION_STEP_OPERATION_CODE).equalsIgnoreCase(Pages.PARAM_PROMOTION_STEP_OPERATION_DOWN);
  }
}