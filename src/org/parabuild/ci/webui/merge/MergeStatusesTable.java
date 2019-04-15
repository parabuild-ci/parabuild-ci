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

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.merge.MergeState;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Component;
import viewtier.ui.Font;
import viewtier.ui.Label;

/**
 * Table to show merge statuses.
 */
final class MergeStatusesTable extends AbstractFlatTable {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(MergeStatusesTable.class); // NOPMD
  private static final long serialVersionUID = -3459072689860457755L;

  private static final String CAPTION_MARKER = "Automerge Marker";
  private static final String CAPTION_MERGE_NAME = "Automerge Name";
  private static final String CAPTION_DESCRIPTION = "Description";
  private static final String CAPTION_STATUS = "Status";
  private static final String CAPTION_ACTIONS = "Actions";

  private static final int INDEX_MARKER = 0;
  private static final int INDEX_MERGE_NAME = 1;
  private static final int INDEX_DESCRIPTION = 2;
  private static final int INDEX_STATUS = 3;
  private static final int INDEX_COMMANDS = 4;

  private final transient User user;
  private List statuses = null;


  public MergeStatusesTable(final boolean adminMode) {
    super(adminMode ? 5 : 4, false);
    setWidth("100%");
    getUserPanel().setWidth("100%");
    user = SecurityManager.getInstance().getUserFromContext(getTierletContext());
  }


  /**
   */
  protected Component[] makeHeader() {
    final List header = new ArrayList(5);
    header.add(new TableHeaderLabel(CAPTION_MARKER, "10%"));
    header.add(new TableHeaderLabel(CAPTION_MERGE_NAME, "30%"));
    header.add(new TableHeaderLabel(CAPTION_DESCRIPTION, "20%"));
    header.add(new TableHeaderLabel(CAPTION_STATUS, "10%"));
    if (columnCount() == 5) header.add(new TableHeaderLabel(CAPTION_ACTIONS, "30%"));

    final Component[] result = new Component[header.size()];
    for (int i = 0, n = header.size(); i < n; i++) result[i] = (Component)header.get(i);
    return result;
  }


  /**
   * Makes row, should be implemented by successor class
   *
   */
  protected Component[] makeRow(final int rowIndex) {
    final List row = new ArrayList(5);
    row.add(new BoldCommonLabel());
    row.add(new MergeNameLink());
    row.add(new Label());
    row.add(new Label());
    if (columnCount() == 5) row.add(new MergeCommandFlow());

    final Component[] result = new Component[row.size()];
    for (int i = 0, n = row.size(); i < n; i++) result[i] = (Component)row.get(i);
    return result;
  }


  /**
   * This implementation of this abstract method is called when
   * the table wants to fetch a row with a given rowIndex.
   * Implementing method should fill the data corresponding the
   * given rowIndex.
   *
   * @return this method should return either TBL_ROW_FETCHED or
   *         TBL_NO_MORE_ROWS if the requested row is out of
   *         range.
   *
   * @see AbstractFlatTable#TBL_ROW_FETCHED
   * @see AbstractFlatTable#TBL_NO_MORE_ROWS
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
//    if (log.isDebugEnabled()) log.debug("rowIndex: " + rowIndex);
    if (rowIndex >= statuses.size()) return TBL_NO_MORE_ROWS;
    final MergeState state = (MergeState)statuses.get(rowIndex);
    final Component[] row = getRow(rowIndex);

    ((Label)row[INDEX_MARKER]).setText(state.getMarker());

    final MergeNameLink mergeLink = (MergeNameLink)row[INDEX_MERGE_NAME];
    mergeLink.setMergeID(state.getActiveMergeConfigurationID());
    mergeLink.setText(state.getName());

    ((Label)row[INDEX_DESCRIPTION]).setText(state.getDescription());

    ((Label)row[INDEX_STATUS]).setText(state.getStatus().toString());

    if (columnCount() == 5) {
      if (SecurityManager.getInstance().getUserMergeRights(user, state.getActiveMergeConfigurationID()).isAllowedToListCommands()) {
        final MergeCommandFlow mergeCommandFlow = (MergeCommandFlow)row[INDEX_COMMANDS];
        mergeCommandFlow.setMergeID(state.getActiveMergeConfigurationID());
        mergeCommandFlow.setMergeStatus(state.getStatus());
      }
    }

    return TBL_ROW_FETCHED;
  }


  public void populate(final List currentMergeStatuses) {
//    if (log.isDebugEnabled()) log.debug("currentMergeStatuses: " + currentMergeStatuses);
    this.statuses = currentMergeStatuses;
    populate();
  }


  /**
   * Specialized link to display build in the build statuses
   * table - is not underlined.
   */
  private static final class MergeNameLink extends CommonLink {

    public static final Font FONT_MERGE_NAME_LINK = new Font(Pages.COMMON_FONT_FAMILY, Font.Bold, Pages.COMMMON_FONT_SIZE);
    private static final long serialVersionUID = 6400243169737437393L;


    /**
     * Constructor.
     */
    public MergeNameLink() {
      super("", Pages.PAGE_MERGE_REPORT);
      setFont(FONT_MERGE_NAME_LINK);
    }


    /**
     * Sets merge ID.
     */
    public void setMergeID(final int activeMergeID) {
      setParameters(WebuiUtils.makeMergeIDParameters(activeMergeID));
    }
  }


  public String toString() {
    return "MergeStatusesTable{" +
      "user=" + user +
      ", statuses=" + statuses +
      '}';
  }
}
