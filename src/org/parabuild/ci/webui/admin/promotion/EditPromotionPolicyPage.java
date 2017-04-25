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
import org.parabuild.ci.object.Project;
import org.parabuild.ci.object.PromotionPolicy;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.promotion.PromotionConfigurationManager;
import org.parabuild.ci.webui.admin.system.AuthenticatedSystemConfigurationPage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.SelectProjectPanel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Parameters;

/**
 * Edits given promotion policy configuration. If the promotion policy
 * configuration is not provided, edits a new one.
 */
public final class EditPromotionPolicyPage extends AuthenticatedSystemConfigurationPage implements ConversationalTierlet {

  private static final long serialVersionUID = 4542828122356933097L;

  private static final String CAPTION_ADDING_PROMOTION_POLICY = "Adding Promotion Policy";
  private static final String CAPTION_EDITING_PROMOTION_POLICY = "Editing Promotion Policy: ";
  private static final String MESSAGE_REQUESTED_PROJECT_NOT_FOUND = "Requested project not found";


  public EditPromotionPolicyPage() {
    super(FLAG_SHOW_HEADER_SEPARATOR | FLAG_SHOW_PAGE_HEADER_LABEL);
  }


  /**
   * Strategy method to be implemented by classes inheriting
   * BasePage.
   *
   * @param parameters
   *
   * @return result of page execution
   */
  protected Result executeAuthenticatedPage(final Parameters parameters) {
    if (parameters.isParameterPresent(Pages.PARAM_PROMOTION_POLICY_ID)) {
      // process promotion policy edit

      // validate
      final String stringPromotionPolicyID = parameters.getParameterValue(Pages.PARAM_PROMOTION_POLICY_ID);
      if (!StringUtils.isValidInteger(stringPromotionPolicyID)) {
        return WebuiUtils.showNotFound(this);
      }
      final PromotionPolicy promotionPolicy = PromotionConfigurationManager.getInstance().getPromotionPolicy(Integer.parseInt(stringPromotionPolicyID));
      if (promotionPolicy == null) {
        return WebuiUtils.showNotFound(this);
      }

      setPageHeaderAndTitle(CAPTION_EDITING_PROMOTION_POLICY + promotionPolicy.getName());

      // proceed to editing promotion policy config
      final EditPromotionPolicyPanel pnlEdit = new EditPromotionPolicyPanel();
      baseContentPanel().add(pnlEdit);
      pnlEdit.load(promotionPolicy);

      // surrender control
      return Result.Continue();
    } else if (parameters.isParameterPresent(Pages.PARAM_PROJECT_ID)) {
      // process project section

      // validate
      final String projectID = parameters.getParameterValue(Pages.PARAM_PROJECT_ID);
      if (!StringUtils.isValidInteger(projectID)) {
        return showProjectNotFound();
      }
      final Project project = ProjectManager.getInstance().getProject(Integer.parseInt(projectID));
      if (project == null) {
        return showProjectNotFound();
      }


      setPageHeaderAndTitle(CAPTION_ADDING_PROMOTION_POLICY);

      // process new promotion policy with project selected
      final EditPromotionPolicyPanel pnlEdit = new EditPromotionPolicyPanel();
      baseContentPanel().add(pnlEdit);
      pnlEdit.load(project);

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
  private Result showProjectNotFound() {
    baseContentPanel().showErrorMessage(MESSAGE_REQUESTED_PROJECT_NOT_FOUND);
    return Result.Done();
  }
}
