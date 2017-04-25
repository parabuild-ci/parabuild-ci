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

import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page shows list of result groups
 */
public final class ResultGroupListPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -2472052514871569348L;  // NOPMD
  private static final String CAPTION_RESULT_GROUPS = "Result Groups";


  public ResultGroupListPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_QUICK_SEARCH | FLAG_SHOW_PAGE_HEADER_LABEL);
    setTitle(makeTitle(CAPTION_RESULT_GROUPS));
    setPageHeader(CAPTION_RESULT_GROUPS);
  }


  public Result executePage(final Parameters params) {

    if (!super.isValidUser() && !org.parabuild.ci.security.SecurityManager.getInstance().isAnonymousAccessEnabled()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.RESULT_GROUPS, params);
    }

    final GridIterator gi = new GridIterator(baseContentPanel().getUserPanel(), 1);
    if (super.isValidAdminUser()) gi.add(makeNewResultGroupLink());

    // add admin builds table
    final ResultGroupTable resultGroupTable = new ResultGroupTable();
    resultGroupTable.setWidth("100%");
    gi.add(resultGroupTable, 1);

    // add new cluster link - bottom
    gi.add(WebuiUtils.makeHorizontalDivider(5), 1);
    if (super.isValidAdminUser()) gi.add(makeNewResultGroupLink());
    return Result.Done();
  }


  private CommonCommandLinkWithImage makeNewResultGroupLink() {
    final CommonCommandLinkWithImage lnkAddNewResultGroup = new AddResultGroupLink();
    lnkAddNewResultGroup.setAlignY(Layout.TOP);
    return lnkAddNewResultGroup;
  }


  private static final class AddResultGroupLink extends CommonCommandLinkWithImage {

    protected static final String CAPTION_ADD_RESULT_GROUP = "Add Result Group";


    public AddResultGroupLink() {
      super(CAPTION_ADD_RESULT_GROUP, Pages.RESULT_GROUP_EDIT);
    }
  }
}
