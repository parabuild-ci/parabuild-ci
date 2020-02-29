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

import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.object.PromotionPolicyStep;
import org.parabuild.ci.promotion.PromotionConfigurationManager;
import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * Edits given promotion policy step configuration.
 */
public final class PromotionPolicyStepDetailsPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = 4542828122356933097L;

  private static final String CAPTION_PROMOTION_POLICY_DETAILS = "Promotion Policy Step Details: ";


  protected Result executeSystemConfigurationPage(final Parameters params) {
    if (!params.isParameterPresent(Pages.PARAM_PROMOTION_POLICY_STEP_ID)) {
      return WebuiUtils.showNotFound(this);
    }

    final Panel rightPanel = getRightPanel();

    // Validate
    final String stringPromotionPolicyStepID = params.getParameterValue(Pages.PARAM_PROMOTION_POLICY_STEP_ID);
    if (!StringUtils.isValidInteger(stringPromotionPolicyStepID)) {
      return WebuiUtils.showNotFound(this);
    }

    // Get policy
    final PromotionPolicyStep promotionPolicyStep = PromotionConfigurationManager.getInstance().getPromotionPolicyStep(Integer.parseInt(stringPromotionPolicyStepID));
    if (promotionPolicyStep == null) {
      return WebuiUtils.showNotFound(this);
    }

    // Set title
    setPageHeaderAndTitle(CAPTION_PROMOTION_POLICY_DETAILS + promotionPolicyStep.getName());

    // Display promotion policy config
    final EditPromotionPolicyStepPanel pnlEdit = new EditPromotionPolicyStepPanel(WebUIConstants.MODE_VIEW);
    rightPanel.add(pnlEdit);
    pnlEdit.load(promotionPolicyStep);

    // Display table with promotion step permissions
    // surrender control
    return Result.Done();
  }
}
