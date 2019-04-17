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

import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * Page with a list of promotion policies
 */
public final class PromotionPolicyListPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = -2472052514871569348L; // NOPMD
  private static final String CAPTION_PROMOTION_POLICIES = "Promotion Policies";


  public PromotionPolicyListPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_QUICK_SEARCH | FLAG_SHOW_HEADER_SEPARATOR);
    setTitle(makeTitle(CAPTION_PROMOTION_POLICIES));
  }


  protected Result executeSystemConfigurationPage(final Parameters params) {
    final GridIterator gi = new GridIterator(getRightPanel(), 2);

    // add policy list table
    final PromotionPolicyListTable policiesTable = new PromotionPolicyListTable(super.isValidAdminUser());
    policiesTable.hideTitle();
    gi.add(policiesTable, 2);

    // add new policy link - bottom
    gi.add(WebuiUtils.makeHorizontalDivider(5), 2);
    gi.add(makeNewPromotionPolicyLink(), 2);
    return Result.Done();
  }


  private static AddPromotionPolicyLink makeNewPromotionPolicyLink() {
    final AddPromotionPolicyLink lnkAddNewPromotionPolicy = new AddPromotionPolicyLink();
    lnkAddNewPromotionPolicy.setAlignX(Layout.LEFT);
    lnkAddNewPromotionPolicy.setAlignY(Layout.TOP);
    return lnkAddNewPromotionPolicy;
  }


  private static final class AddPromotionPolicyLink extends CommonCommandLinkWithImage {

    private static final String CAPTION_ADD_NEW_POLICY = "Add Policy";
    private static final long serialVersionUID = 3851156663746715132L;


    AddPromotionPolicyLink() {
      super(CAPTION_ADD_NEW_POLICY, Pages.PAGE_EDIT_PROMOTION_POLICY);
    }
  }
}
