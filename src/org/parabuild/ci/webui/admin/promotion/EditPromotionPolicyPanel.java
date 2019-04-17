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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.object.Project;
import org.parabuild.ci.object.PromotionPolicy;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.promotion.PromotionConfigurationManager;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.SaveButton;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Tierlet;

/**
 */
final class EditPromotionPolicyPanel extends MessagePanel {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(EditPromotionPolicyPanel.class); // NOPMD
  private static final long serialVersionUID = 6574406891458409409L;

  private static final String CAPTION_DESCRIPTION = "Description: ";
  private static final String CAPTION_POLICY_NAME = "Policy name: ";
  private static final String CAPTION_PROJECT = "Project: ";

  private final Label lbDescription = new CommonFieldLabel(CAPTION_DESCRIPTION);  // NOPMD
  private final Label lbName = new CommonFieldLabel(CAPTION_POLICY_NAME, Pages.SECTION_HEADER_COLOR); // NOPMD
  private final Label lbProjectName = new CommonFieldLabel(CAPTION_PROJECT); // NOPMD
  private final Label lbProjectNameValue = new CommonLabel(); // NOPMD
  private final Button btnCancel = new CancelButton(); // NOPMD
  private final Button btnSave = new SaveButton(); // NOPMD
  private final Field flDescription = new CommonField(100, 60); // NOPMD
  private final Field flName = new CommonField(100, 60); // NOPMD

  private int promotionPolicyID = PromotionPolicy.UNSAVED_ID; // NOPMD
  private int projectID = Project.UNSAVED_ID;


  EditPromotionPolicyPanel(final byte mode) {
    showContentBorder(false);
    setWidth(Pages.PAGE_WIDTH);
    setAlignX(Layout.CENTER);

    // layout
    final GridIterator gi = new GridIterator(getUserPanel(), 2);
    gi.add(lbProjectName).add(lbProjectNameValue);
    gi.add(lbName).add(new RequiredFieldMarker(flName));
    gi.add(lbDescription).add(new RequiredFieldMarker(flDescription));

    final boolean editable = mode == WebUIConstants.MODE_EDIT;
    flDescription.setEditable(editable);
    flName.setEditable(editable);

    // buttons
    if (editable) {
      gi.addBlankLine();
      gi.add(btnSave).add(new CommonFlow(new Label("  "), btnCancel));
      // set up listeners
      btnSave.addListener(makeSaveButtonListener());
      btnCancel.addListener(makeCancelButtonListener());
      // appearance
      btnSave.setAlignX(Layout.RIGHT);
    }
  }


  EditPromotionPolicyPanel() {
    this(WebUIConstants.MODE_EDIT);
  }


  private ButtonPressedListener makeCancelButtonListener() {
    return new ButtonPressedListener() {
      private static final long serialVersionUID = 2450210024232205628L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        return promotionPolicyListHome();
      }
    };
  }


  private ButtonPressedListener makeSaveButtonListener() {
    return new ButtonPressedListener() {
      private static final long serialVersionUID = -3396030426532728486L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        if (!validate()) {
          return Tierlet.Result.Continue();
        }
        if (save()) {
          return promotionPolicyListHome();
        }
        return Tierlet.Result.Continue();
      }
    };
  }


  private boolean save() {
    final PromotionPolicy policy;
    if (promotionPolicyID == PromotionPolicy.UNSAVED_ID) {
      // new policy
      policy = new PromotionPolicy();
      policy.setProjectID(projectID);
    } else {
      // existing policy
      policy = PromotionConfigurationManager.getInstance().getPromotionPolicy(promotionPolicyID);
    }
    policy.setDescription(flDescription.getValue());
    policy.setName(flName.getValue());

    PromotionConfigurationManager.getInstance().save(policy);
    return true;
  }


  private boolean validate() {
    final List errors = new ArrayList(11);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_DESCRIPTION, flDescription);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_POLICY_NAME, flName);
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
    }
    return errors.isEmpty();
  }


  /**
   * Loads only project.
   *
   * @param project
   */
  public void load(final Project project) {
    lbProjectNameValue.setText(project.getName());
    projectID = project.getID();
  }


  /**
   * Loads policy
   * @param policy
   */
  public void load(final PromotionPolicy policy) {
    final Project project = ProjectManager.getInstance().getProject(policy.getProjectID());
    load(project);
    promotionPolicyID = policy.getID();
    flDescription.setValue(policy.getDescription());
    flName.setValue(policy.getName());
  }


  private static Tierlet.Result promotionPolicyListHome() {
    return Tierlet.Result.Done(Pages.PAGE_PROMOTION_POLICY_LIST);
  }


  public String toString() {
    return "EditPromotionPolicyPanel{" +
            "lbDescription=" + lbDescription +
            ", lbName=" + lbName +
            ", lbProjectName=" + lbProjectName +
            ", lbProjectNameValue=" + lbProjectNameValue +
            ", btnCancel=" + btnCancel +
            ", btnSave=" + btnSave +
            ", flDescription=" + flDescription +
            ", flName=" + flName +
            ", promotionPolicyID=" + promotionPolicyID +
            ", projectID=" + projectID +
            '}';
  }
}
