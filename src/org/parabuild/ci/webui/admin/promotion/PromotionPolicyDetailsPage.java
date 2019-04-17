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

import java.util.List;

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.PromotionPolicy;
import org.parabuild.ci.promotion.PromotionConfigurationManager;
import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import org.parabuild.ci.webui.common.CommonBoldLink;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Link;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * Edits given promotion policy configuration. If the promotion policy
 * configuration is not provided, edits a new one.
 */
public final class PromotionPolicyDetailsPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = 4542828122356933097L;

  private static final String CAPTION_PROMOTION_POLICY_DETAILS = "Promotion Policy Details: ";


  protected Result executeSystemConfigurationPage(final Parameters params) {
    if (params.isParameterPresent(Pages.PARAM_PROMOTION_POLICY_ID)) {
      // Validate
      final String stringPromotionPolicyID = params.getParameterValue(Pages.PARAM_PROMOTION_POLICY_ID);
      if (!StringUtils.isValidInteger(stringPromotionPolicyID)) {
        return WebuiUtils.showNotFound(this);
      }

      // Get policy
      final PromotionPolicy promotionPolicy = PromotionConfigurationManager.getInstance().getPromotionPolicy(Integer.parseInt(stringPromotionPolicyID));
      if (promotionPolicy == null) {
        return WebuiUtils.showNotFound(this);
      }

      // Set title
      setPageHeaderAndTitle(CAPTION_PROMOTION_POLICY_DETAILS + promotionPolicy.getName());


      final GridIterator gi = new GridIterator(getRightPanel(), 2);

      // Display promotion policy config
      final EditPromotionPolicyPanel pnlPolicyHeader = new EditPromotionPolicyPanel(WebUIConstants.MODE_VIEW);
      pnlPolicyHeader.setTitle("Promotion Policy: " + promotionPolicy.getName());
      pnlPolicyHeader.showContentBorder(true);
      pnlPolicyHeader.setWidth("100%");
      gi.add(pnlPolicyHeader,2);
      pnlPolicyHeader.load(promotionPolicy);

      // Add promotion policy control
      final Link lnkEdit = new CommonBoldLink("Edit Policy", Pages.PAGE_EDIT_PROMOTION_POLICY, PromotionUtils.createProperties(promotionPolicy.getID()));
      final CommonFlow linkFlow = new CommonFlow(WebuiUtils.makeBlueBulletSquareImage16x16(), lnkEdit);
      linkFlow.setAlignX(Layout.CENTER);
      gi.add(linkFlow, 2);
      gi.add(WebuiUtils.makePanelDivider(),2);

      // Display R/O table with promotion steps
      final List promotionStepsList = PromotionConfigurationManager.getInstance().getPromotionStepsList(promotionPolicy.getID());
      final PromotionPolicyStepListTable promotionPolicyStepListTable = new PromotionPolicyStepListTable(true);
      promotionPolicyStepListTable.populate(promotionStepsList);
      gi.add(createSpacer());
      gi.add(promotionPolicyStepListTable);

      // add new policy link - bottom
      if (isValidAdminUser()) {
        gi.add(WebuiUtils.makeHorizontalDivider(5),2);
        gi.add(createSpacer());
        gi.add(createNewPromotionPolicyStepLink(promotionPolicy.getID()));
      }

      // surrender control
      return Result.Done();
    } else {
      return WebuiUtils.showNotFound(this);
    }
  }


  private static Label createSpacer() {
    final Label spacer = new Label(" ");
    spacer.setWidth("3%");
    return spacer;
  }


  private static Component createNewPromotionPolicyStepLink(final int policyID) {
    final AddPromotionPolicyStepLink lnkAddNewPromotionPolicyStep = new AddPromotionPolicyStepLink(policyID);
    lnkAddNewPromotionPolicyStep.setAlignX(Layout.LEFT);
    lnkAddNewPromotionPolicyStep.setAlignY(Layout.TOP);
    return lnkAddNewPromotionPolicyStep;
  }


  private static final class AddPromotionPolicyStepLink extends CommonCommandLinkWithImage {

    private static final String CAPTION_ADD_NEW_POLICY_STEP = "Add Step";
    private static final long serialVersionUID = 5440319210795562653L;


    AddPromotionPolicyStepLink(final int policyID) {
      super(CAPTION_ADD_NEW_POLICY_STEP, Pages.PAGE_EDIT_PROMOTION_POLICY_STEP, PromotionUtils.createProperties(policyID));
    }
  }
}
