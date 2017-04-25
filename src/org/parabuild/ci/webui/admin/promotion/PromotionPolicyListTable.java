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

import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.promotion.PromotionConfigurationManager;
import org.parabuild.ci.promotion.PromotionVO;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Link;

/**
 * Shows list of promotion policies in the system.
 */
final class PromotionPolicyListTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(PromotionPolicyListTable.class); // NOPMD

  private static final int COLUMN_COUNT = 4;

  private static final int COL_NAME = 0;
  private static final int COL_PROJECT = 1;
  private static final int COL_DESCRIPTION = 2;
  private static final int COL_ACTION = 3;

  public static final String CAPTION_NAME = "Promotion Policy";
  public static final String CAPTION_PROJECT = "Project";
  public static final String CAPTION_DESCRIPTION = "Description";
  public static final String CAPTION_ACTION = "Action";

  private List policies = null;


  PromotionPolicyListTable(final boolean showControls) {
    super(showControls ? COLUMN_COUNT : COLUMN_COUNT - 1, false);
    setTitle("Promotion Policies");
    setWidth("100%");
    setGridColor(Pages.COLOR_PANEL_BORDER);
    populate(PromotionConfigurationManager.getInstance().getPromotionList());
  }


  /**
   * Returs array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   *
   */
  public Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_NAME] = new TableHeaderLabel(CAPTION_NAME, "30%");
    headers[COL_PROJECT] = new TableHeaderLabel(CAPTION_PROJECT, "10%");
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
    if (rowIndex >= policies.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final PromotionVO policy = (PromotionVO)policies.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    // Set link to the screen edition promotion details
    final Link link = (Link) row[COL_NAME];
    link.setText(policy.getPromotionName());
    link.setUrl(Pages.PAGE_VIEW_PROMOTION_POLICY_DETAILS);
    link.setParameters(makeParameters(policy.getPromotionID()));
    //
    ((Label)row[COL_PROJECT]).setText(String.valueOf(policy.getProjectName())); // FIXME
    ((Label)row[COL_DESCRIPTION]).setText(policy.getDescription());
    if (columnCount() == COLUMN_COUNT) {
      ((PromotionCommandsFlow)row[COL_ACTION]).setPolicyID(policy.getPromotionID());
    }
    return TBL_ROW_FETCHED;
  }


  private Properties makeParameters(final int promotionID) {
    final Properties params = new Properties();
    params.setProperty(Pages.PARAM_PROMOTION_POLICY_ID, Integer.toString(promotionID));
    return params;
  }


  /**
   * Makes row
   *
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_NAME] = new CommonLink("", "");
    result[COL_PROJECT] = new CommonLabel();
    result[COL_DESCRIPTION] = new CommonLabel();
    if (columnCount() == COLUMN_COUNT) {
      result[COL_ACTION] = new PromotionCommandsFlow();
    }
    return result;
  }


  /**
   * Populates table with projects. This list is reused in fetchRow
   * method.
   *
   * @see BuildState
   */
  public void populate(final List projects) {
    this.policies = Collections.unmodifiableList(projects);
    super.populate();
  }


  public String toString() {
    return "PromotionPolicyListTable{" +
            "policies=" + policies +
            '}';
  }
}
