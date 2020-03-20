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
import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.object.PromotionPolicy;
import org.parabuild.ci.object.PromotionPolicyStep;
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
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Color;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.List;

/**
 */
final class EditPromotionPolicyStepPanel extends MessagePanel {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(EditPromotionPolicyStepPanel.class); // NOPMD
  private static final long serialVersionUID = 6574406891458409409L;

  private static final Color SECTION_HEADER_COLOR = new Color(0x006699);

  private static final String CAPTION_DESCRIPTION = "Description: ";
  private static final String CAPTION_POLICY_STEP_NAME = "Policy step name: ";
  private static final String CAPTION_POLICY = "Policy: ";

  private final Label lbDescription = new CommonFieldLabel(CAPTION_DESCRIPTION);  // NOPMD
  private final Label lbName = new CommonFieldLabel(CAPTION_POLICY_STEP_NAME, SECTION_HEADER_COLOR); // NOPMD
  private final Label lbPolicyName = new CommonFieldLabel(CAPTION_POLICY); // NOPMD
  private final Label lbPolicyNameValue = new CommonLabel(); // NOPMD
  private final Button btnCancel = new CancelButton(); // NOPMD
  private final Button btnSave = new SaveButton(); // NOPMD
  private final CommonField flDescription = new CommonField(100, 60); // NOPMD
  private final CommonField flName = new CommonField(100, 60); // NOPMD

  private int policyID = PromotionPolicy.UNSAVED_ID; // NOPMD
  private int policyStepID = PromotionPolicyStep.UNSAVED_ID;


  EditPromotionPolicyStepPanel() {
    this(WebUIConstants.MODE_EDIT);
  }


  EditPromotionPolicyStepPanel(final byte mode) {
    showContentBorder(false);
    setWidth(Pages.PAGE_WIDTH);
    setAlignX(Layout.CENTER);

    // layout
    final GridIterator gi = new GridIterator(getUserPanel(), 2);
    gi.add(lbPolicyName).add(lbPolicyNameValue);
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


  private ButtonPressedListener makeCancelButtonListener() {
    return new ButtonPressedListener() {
      private static final long serialVersionUID = -5910676150776810497L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
        return promotionPolicyDetailHome(policyID);
      }
    };
  }


  private ButtonPressedListener makeSaveButtonListener() {
    return new ButtonPressedListener() {
      private static final long serialVersionUID = 6985234287027974746L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
        if (!validate()) {
          return Tierlet.Result.Continue();
        }
        if (save()) {
          return promotionPolicyDetailHome(policyID);
        }
        return Tierlet.Result.Continue();
      }
    };
  }


  private boolean save() {
    final PromotionPolicyStep step;
    final PromotionConfigurationManager pcm = PromotionConfigurationManager.getInstance();
    if (policyStepID == PromotionPolicy.UNSAVED_ID) {
      // New step
      step = new PromotionPolicyStep();
      step.setPromotionID(policyID);
      step.setLineNumber(pcm.getMaxStepLineNumber(policyID) +1);
    } else {
      // Existing step
      step = pcm.getPromotionPolicyStep(policyStepID);
    }
    step.setDescription(flDescription.getValue());
    step.setName(flName.getValue());

    pcm.save(step);
    return true;
  }


  private boolean validate() {
    final List errors = new ArrayList(11);
    InputValidator.validateFieldNotBlank(errors, CAPTION_DESCRIPTION, flDescription);
    InputValidator.validateFieldNotBlank(errors, CAPTION_POLICY_STEP_NAME, flName);
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
    }
    return errors.isEmpty();
  }


  /**
   * Loads only policy.
   *
   * @param policy
   */
  public void load(final PromotionPolicy policy) {
    lbPolicyNameValue.setText(policy.getName());
    policyID = policy.getID();
  }


  /**
   * Loads step
   *
   * @param step
   */
  public void load(final PromotionPolicyStep step) {
    final PromotionPolicy policy = PromotionConfigurationManager.getInstance().getPromotionPolicy(step.getPromotionID());
    load(policy);
    policyStepID = step.getID();
    flDescription.setValue(step.getDescription());
    flName.setValue(step.getName());
  }


  private static Tierlet.Result promotionPolicyDetailHome(final int policyID) {
    final Parameters params = new Parameters();
    params.addParameter(Pages.PARAM_PROMOTION_POLICY_ID, policyID);
    return Tierlet.Result.Done(Pages.PAGE_VIEW_PROMOTION_POLICY_DETAILS, params);
  }


  public String toString() {
    return "EditPromotionPolicyPanel{" +
            "lbDescription=" + lbDescription +
            ", lbName=" + lbName +
            ", lbPolicyName=" + lbPolicyName +
            ", lbPolicyNameValue=" + lbPolicyNameValue +
            ", btnCancel=" + btnCancel +
            ", btnSave=" + btnSave +
            ", flDescription=" + flDescription +
            ", flName=" + flName +
            ", policyID=" + policyID +
            ", policyStepID=" + policyStepID +
            '}';
  }
}
