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
package org.parabuild.ci.webui;

import java.text.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.configuration.BuildRunParticipantVO;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;

/**
 * Shows change lists headers ONLY.
 *
 * @see ChangeList
 * @see BuildRun
 * @see DetailedBuildStatusPanel
 */
public final class BuildRunParticipantsTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(BuildRunParticipantsTable.class); // NOPMD

  private static final int COLUMN_COUNT = 5;

  private static final int COL_USER = 0;
  private static final int COL_DESCRPTION = 1;
  private static final int COL_TIME = 2;
  private static final int COL_NUMBER = 3;
  private static final int COL_BUILD_NUMBER = 4;

  public static final String STR_USER = "User";
  public static final String STR_DESCRPTION = "Description";
  public static final String STR_NUMBER = "Change List";
  public static final String STR_TIME = "Time";
  public static final String STR_BUILD_NUMBER = "Build Number";

  private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat(), Locale.US); // NOPMD
  private List participants = null;
  private final boolean showChangeListDescriptions;
  private final ChangeURLFactory changeURLFactory;


  public BuildRunParticipantsTable(final BuildRun buildRun, final boolean showChangeListDescriptions) {
    super(COLUMN_COUNT, false);
    super.setGridColor(Pages.COLOR_PANEL_BORDER);
    super.setRowHeight(15);
    this.participants = ConfigurationManager.getInstance().getBuildRunParticipantsOrderedByDate(buildRun.getBuildRunID());
    this.showChangeListDescriptions = showChangeListDescriptions;
    this.changeURLFactory = WebuiUtils.makeChangeURLFactory(buildRun.getActiveBuildID());
    super.populate();
  }


  /**
   * Returs array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   *
   */
  public Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_USER] = new TableHeaderLabel(STR_USER, "14%");
    headers[COL_DESCRPTION] = new TableHeaderLabel(STR_DESCRPTION, "47%");
    headers[COL_TIME] = new TableHeaderLabel(STR_TIME, "20%");
    headers[COL_NUMBER] = new TableHeaderLabel(STR_NUMBER, "8%");
    headers[COL_BUILD_NUMBER] = new TableHeaderLabel(STR_BUILD_NUMBER, "9%");

    headers[COL_NUMBER].setAlignX(Layout.CENTER);
    headers[COL_BUILD_NUMBER].setAlignX(Layout.CENTER);
    return headers;
  }


  /**
   * Returs array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= participants.size()) return TBL_NO_MORE_ROWS;
    final BuildRunParticipantVO vo = (BuildRunParticipantVO)participants.get(rowIndex);
    final ChangeList changeList = vo.getChangeList();
    final Component[] row = getRow(rowIndex);
    ((Label)row[COL_USER]).setText(changeList.getUser());
    ((Label)row[COL_DESCRPTION]).setText(showChangeListDescriptions ? changeList.getDescription() : "");
    ((ChangelistNumberFlow)row[COL_NUMBER]).setChangelist(changeList);
    ((Label)row[COL_TIME]).setText(changeList.getCreatedAt(dateTimeFormat));
    ((Label)row[COL_BUILD_NUMBER]).setText(vo.getFirstBuildRunNumberAsString());
    return TBL_ROW_FETCHED;
  }


  /**
   * Makes row
   *
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_USER] = new Label();
    result[COL_DESCRPTION] = new Label();
    result[COL_TIME] = new Label();
    result[COL_NUMBER] = new ChangelistNumberFlow(changeURLFactory);
    result[COL_NUMBER].setAlignX(Layout.CENTER);
    result[COL_BUILD_NUMBER] = new Label();
    result[COL_BUILD_NUMBER].setAlignX(Layout.CENTER);
    return result;
  }


  /**
   * Flow to hold either label or a link to a change list
   * depending on what changeURLFactory returns.
   */
  static final class ChangelistNumberFlow extends Flow {

    private final ChangeURLFactory changeURLFactory;


    /**
     * Constructor.
     *
     * @param changeURLFactory
     */
    public ChangelistNumberFlow(final ChangeURLFactory changeURLFactory) {
      this.changeURLFactory = changeURLFactory;
    }


    /**
     * Sets change list.
     */
    void setChangelist(final ChangeList changelist) {
      if (changeURLFactory == null) {
        addLabel(changelist);
      } else {
        final ChangeURL changeURL = changeURLFactory.makeChangeListNumberURL(changelist);
        if (changeURL == null) {
          addLabel(changelist);
        } else {
          add(new CommonLink(changeURL.getCaption(), changeURL.getURL()));
        }
      }
    }


    /**
     * Helper method.
     */
    private void addLabel(final ChangeList number) {
      add(new Label(number.getNumber()));
    }
  }
}
