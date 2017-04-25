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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.security.MergeRights;
import org.parabuild.ci.webui.ChangelistContentTable;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * Displays details of a change list beloging to a merge.
 */
public final class BrancheChangeListPage extends BaseMergePage implements StatelessTierlet {

  public static final int PAGE_LENGTH = 50;

  private static final String CHANGE_LIST = "Change List";


  public BrancheChangeListPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_HEADER_SEPARATOR | FLAG_SHOW_PAGE_HEADER_LABEL);
    setTitle(CHANGE_LIST);
    setPageHeader(CHANGE_LIST);
  }


  protected Result executeMergePage(final Parameters parameters, final MergeConfiguration mergeConfiguration) {
    // authorise
    final MergeRights userRights = org.parabuild.ci.security.SecurityManager.getInstance().getUserMergeRights(getUser(), mergeConfiguration.getActiveMergeID());
    if (!userRights.isAllowedToViewMerge()) return WebuiUtils.showNotAuthorized(this);

    // get change list ID
    if (!parameters.isParameterPresent(Pages.PARAM_CHANGE_LIST_ID)) return WebuiUtils.showNotFound(this);
    final String stringChangeListID = parameters.getParameterValue(Pages.PARAM_CHANGE_LIST_ID);
    if (!StringUtils.isValidInteger(stringChangeListID)) return WebuiUtils.showNotSupported(this);

    // get branch change list ID
    if (!parameters.isParameterPresent(Pages.PARAM_BRANCH_CHANGE_LIST_ID)) return WebuiUtils.showNotFound(this);
    final String stringBranchChangeListID = parameters.getParameterValue(Pages.PARAM_BRANCH_CHANGE_LIST_ID);
    if (!StringUtils.isValidInteger(stringChangeListID)) return WebuiUtils.showNotSupported(this);

    // get change list
    final ChangeList chl = MergeManager.getInstance().getChangeList(Integer.parseInt(stringBranchChangeListID), Integer.parseInt(stringChangeListID));
    if (chl == null) return WebuiUtils.showNotAuthorized(this);

    // set page title and header
    final String title = CHANGE_LIST + " # " + chl.getNumber();
    setTitle(title);
    setPageHeader(title);

    // add change listr header panel
    final Panel pnlHeader = new Panel();
    final GridIterator gi = new GridIterator(pnlHeader, 2);
    gi.addPair(new CommonLabel("Number: "), new BoldCommonLabel(chl.getNumber()));
    gi.addPair(new CommonLabel("User: "), new BoldCommonLabel(chl.getUser()));
    gi.addPair(new CommonLabel("Description: "), new BoldCommonLabel(chl.getDescription()));
    baseContentPanel().add(pnlHeader);

    // add change list content
    final ChangelistContentTable content = new ChangelistContentTable(null, true);
    content.setHeaderVisible(true);
    content.populate(ConfigurationManager.getInstance().getChanges(chl));
    baseContentPanel().add(content);

    return Result.Done();
  }
}
