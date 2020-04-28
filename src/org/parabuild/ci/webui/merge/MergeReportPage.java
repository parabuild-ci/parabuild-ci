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
package org.parabuild.ci.webui.merge;

import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.security.MergeRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.PaginatorFlow;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * Merge report page displays current status for the merge configuration. This includes:
 *
 * 1. Merged change lists
 * 2. Change lists that are not merged.
 */
public final class MergeReportPage extends BaseMergePage implements StatelessTierlet {

  public static final int PAGE_LENGTH = 50;
  private static final long serialVersionUID = -7271478200243383493L;


  public MergeReportPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_HEADER_SEPARATOR | FLAG_SHOW_PAGE_HEADER_LABEL);
  }


  protected Result executeMergePage(final Parameters parameters, final MergeConfiguration mergeConfiguration) {
    // authorise
    final MergeRights userRights = SecurityManager.getInstance().getUserMergeRights(getUser(), mergeConfiguration.getActiveMergeID());
    if (!userRights.isAllowedToViewMerge()) return WebuiUtils.showNotAuthorized(this);

    // set title
    final String title = "Integration Report For: " + mergeConfiguration.getName();
    setTitle(title);
    setPageHeader(title);

    // display header
    final EditMergePanel mergePanel = new EditMergePanel(WebUIConstants.MODE_VIEW);
    mergePanel.load(mergeConfiguration);
    baseContentPanel().add(mergePanel);
    baseContentPanel().add(WebuiUtils.makePanelDivider());

    // paginator
    final int mergeReportCount = MergeManager.getInstance().getMergeReportCount(mergeConfiguration.getActiveMergeID());
    final PaginatorFlow paginatorFlow = new PaginatorFlow(Pages.PAGE_MERGE_REPORT, Pages.PARAM_MERGE_ID, mergeConfiguration.getActiveMergeID(), mergeReportCount, parameters, PAGE_LENGTH);
    if (paginatorFlow.getPageCount() > 1) {
      baseContentPanel().add(paginatorFlow);
    }

    // display table
    final MergeReportTable reportTable = new MergeReportTable();
    reportTable.populate(MergeManager.getInstance().getMergeReport(mergeConfiguration.getActiveMergeID(), (paginatorFlow.getSelectedPage() - 1) * PAGE_LENGTH, PAGE_LENGTH));
    baseContentPanel().add(reportTable);
    return Result.Done();
  }
}


