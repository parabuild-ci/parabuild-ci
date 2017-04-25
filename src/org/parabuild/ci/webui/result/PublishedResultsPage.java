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
package org.parabuild.ci.webui.result;

import org.parabuild.ci.object.ResultGroup;
import org.parabuild.ci.security.ResultGroupRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page shows published results belonging
 * to the given result group.
 */
public final class PublishedResultsPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -2472052514871569348L;  // NOPMD
  private static final String PUBLISHED_RESULTS = "Published Results";


  public PublishedResultsPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_QUICK_SEARCH | FLAG_SHOW_PAGE_HEADER_LABEL);
    super.markTopMenuItemSelected(MENU_SELECTION_RESULTS);
    setTitle(makeTitle(PUBLISHED_RESULTS));
  }


  public Result executePage(final Parameters params) {

    // validate group exists
    final ResultGroup resultGroup = ParameterUtils.getResultGroupFromParameters(params);
    if (resultGroup == null) {
      baseContentPanel().showErrorMessage("Requested result group cannot be found");
      return Result.Done();
    }

    // validate group can bee seen by the user
    final ResultGroupRights userResultGroupRights = SecurityManager.getInstance().getUserResultGroupRights(getTierletContext(), resultGroup.getID());
    if (!userResultGroupRights.isAllowedToViewResultGroup()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // set title to the group
    setTitle(makeTitle(PUBLISHED_RESULTS + " >> " + resultGroup.getName()));

    // show content
    setPageHeader(resultGroup.getName());
    baseContentPanel().getUserPanel().add(new PublishedResultsPanel(resultGroup));
    return Result.Done();
  }
}
