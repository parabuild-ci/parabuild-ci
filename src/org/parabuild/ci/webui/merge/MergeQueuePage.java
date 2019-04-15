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

import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.security.MergeRights;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.PaginatorFlow;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * Merge queue page displays current content of the merge queue
 */
public final class MergeQueuePage extends BaseMergePage implements StatelessTierlet {

  private static final long serialVersionUID = 8743372351109384370L;


  public MergeQueuePage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_HEADER_SEPARATOR | FLAG_SHOW_PAGE_HEADER_LABEL);
  }


  protected Result executeMergePage(final Parameters parameters, final MergeConfiguration mergeConfiguration) {
    // authorise
    final MergeRights userRights = org.parabuild.ci.security.SecurityManager.getInstance().getUserMergeRights(getUser(), mergeConfiguration.getActiveMergeID());
    if (!userRights.isAllowedToViewMerge()) return WebuiUtils.showNotAuthorized(this);

    // set title
    final String title = "Integration Queue For: " + mergeConfiguration.getName();
    setTitle(title);
    setPageHeader(title);

    // display header
    final EditMergePanel mergePanel = new EditMergePanel(WebUIConstants.MODE_VIEW);
    mergePanel.load(mergeConfiguration);
    baseContentPanel().add(mergePanel);
    baseContentPanel().add(WebuiUtils.makePanelDivider());

    // paginator
    final int mergeReportCount = MergeManager.getInstance().getQueueReportCount(mergeConfiguration.getActiveMergeID());
    final PaginatorFlow paginatorFlow = new PaginatorFlow(Pages.PAGE_MERGE_QUEUE_REPORT, Pages.PARAM_MERGE_ID, mergeConfiguration.getActiveMergeID(), mergeReportCount, parameters, MergeReportPage.PAGE_LENGTH);
    if (paginatorFlow.getPageCount() > 1) {
      baseContentPanel().add(paginatorFlow);
    }

    // display table
    final MergeQueueTable queueTable = new MergeQueueTable();
    queueTable.populate(MergeManager.getInstance().getQueueReport(mergeConfiguration.getActiveMergeID(), 0, 100));
    baseContentPanel().add(queueTable);
    return Result.Done();
  }
}
