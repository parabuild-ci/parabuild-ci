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

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.object.PromotionPolicyStep;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Link;

/**
 * Shows list of promotion policySteps in the system.
 */
final class PromotionPolicyStepListTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(PromotionPolicyStepListTable.class); // NOPMD

  private static final int COLUMN_COUNT = 4;

  private static final int COL_NAME = 0;
  private static final int COL_ORDER = 1;
  private static final int COL_DESCRIPTION = 2;
  private static final int COL_ACTION = 3;

  public static final String CAPTION_NAME = "Promotion Policy Step";
  public static final String CAPTION_DESCRIPTION = "Description";
  public static final String CAPTION_ORDER = "Order";
  public static final String CAPTION_ACTION = "Action";


  private List policySteps = null;


  PromotionPolicyStepListTable(final boolean showControls) {
    super(showControls ? COLUMN_COUNT : COLUMN_COUNT - 1, false);
    setWidth("100%");
    setGridColor(Pages.COLOR_PANEL_BORDER);
    hideTitle();
  }


  /**
   * Returs array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   */
  public Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_NAME] = new TableHeaderLabel(CAPTION_NAME, "30%");
    headers[COL_ORDER] = new TableHeaderLabel(CAPTION_ORDER, "10%");
    headers[COL_DESCRIPTION] = new TableHeaderLabel(CAPTION_DESCRIPTION, "35%");
    if (columnCount() == COLUMN_COUNT) {
      headers[COL_ACTION] = new TableHeaderLabel(CAPTION_ACTION, "20%");
    }
    return headers;
  }


  /**
   * Returs array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= policySteps.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final Object o = policySteps.get(rowIndex);
    if (LOG.isDebugEnabled()) {
      LOG.debug("o: " + o);
    }
    final PromotionPolicyStep policyStep = (PromotionPolicyStep) o;
    final Component[] row = getRow(rowIndex);
    // Set link to the screen edition promotion details
    final Link link = (Link) row[COL_NAME];
    link.setText(policyStep.getName());
    link.setUrl(Pages.PAGE_VIEW_PROMOTION_POLICY_STEP_DETAILS);
    link.setParameters(makeParameters(policyStep.getID()));
    //
    ((Label) row[COL_DESCRIPTION]).setText(policyStep.getDescription());
    if (columnCount() == COLUMN_COUNT) {
      ((PromotionPolicyStepCommandsFlow) row[COL_ACTION]).setStepID(policyStep.getID());
    }

    // Order
    final OrderCommands orderCommands = (OrderCommands) row[COL_ORDER];
    orderCommands.setStep(policyStep);
    return TBL_ROW_FETCHED;
  }


  /**
   * Makes row
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_NAME] = new CommonLink("", "");
    result[COL_DESCRIPTION] = new CommonLabel();
    result[COL_ORDER] = new OrderCommands();
    if (columnCount() == COLUMN_COUNT) {
      result[COL_ACTION] = new PromotionPolicyStepCommandsFlow();
    }
    return result;
  }


  /**
   * @see PromotionPolicyStep
   */
  public void populate(final List policySteps) {
    this.policySteps = Collections.unmodifiableList(policySteps);
    super.populate();
  }


  private static Properties makeParameters(final int stepID) {
    final Properties params = new Properties();
    params.setProperty(Pages.PARAM_PROMOTION_POLICY_STEP_ID, Integer.toString(stepID));
    return params;
  }


  public String toString() {
    return "PromotionPolicyStepListTable{" +
            "policySteps=" + policySteps +
            '}';
  }


  private static final class OrderCommands extends Flow {

    private final Link lnkUp;
    private final Link lnkDown;
    private static final long serialVersionUID = 7439593171207381690L;


    private OrderCommands() {
      lnkUp = new CommonLink("Up", Pages.PAGE_CHANGE_PROMOTION_STEP_ORDER);
      lnkDown = new CommonLink("Down", Pages.PAGE_CHANGE_PROMOTION_STEP_ORDER);
      add(lnkUp);
      add(new Label(" "));
      add(lnkDown);
    }


    void setStep(final PromotionPolicyStep step) {
      // Up
      final Properties upProps = createProperties(step);
      upProps.setProperty(Pages.PARAM_PROMOTION_STEP_OPERATION_CODE, Pages.PARAM_PROMOTION_STEP_OPERATION_UP);
      lnkUp.setParameters(upProps);
      // Down
      final Properties downProps = createProperties(step);
      downProps.setProperty(Pages.PARAM_PROMOTION_STEP_OPERATION_CODE, Pages.PARAM_PROMOTION_STEP_OPERATION_DOWN);
      lnkDown.setParameters(downProps);
    }


    private static Properties createProperties(final PromotionPolicyStep step) {
      final Properties upProps = new Properties();
      upProps.setProperty(Pages.PARAM_PROMOTION_POLICY_STEP_ID, Integer.toString(step.getID()));
      upProps.setProperty(Pages.PARAM_PROMOTION_POLICY_ID, Integer.toString(step.getPromotionID()));
      return upProps;
    }


    public String toString() {
      return "OrderCommands{" +
              "lnkUp=" + lnkUp +
              ", lnkDown=" + lnkDown +
              "} " + super.toString();
    }
  }
}
