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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.List;

/**
 * This table displays list of steps run in the current build
 */
public final class StepRunSummaryTable extends AbstractFlatTable {

  private static final long serialVersionUID = -8419200063026690493L; // NOPMD

  private static final int COLUMN_NUMBER = 4;
  private static final int COL_SEQ_NAME = 0;
  private static final int COL_SEQ_RESULT = 1;
  private static final int COL_SEQ_TIME = 2;
  private static final int COL_SEQ_LOGS = 3;

  private static final String COL_NAME_STEP_NAME = "Step Name";
  private static final String COL_NAME_SEQ_RESULT = "Result";
  private static final String COL_NAME_TIME = "Time";
  private static final String COL_NAME_SEQ_LOGS = "Logs";

  private List steps = null;


  /**
   * Constructor
   */
  public StepRunSummaryTable() {
    super(COLUMN_NUMBER, false);
    setTitle("Build Steps");
  }


  /**
   * Sets build run for which to diplay all steps summaries
   *
   * @see #setStepRun
   */
  public void setBuildRun(final BuildRun buildRun) {
    steps = ConfigurationManager.getInstance().getStepRuns(buildRun.getBuildRunID());
    populate();
  }


  /**
   * Sets sequence run. The summary only for this sequence will
   * be shown
   *
   * @see #setBuildRun
   */
  public void setStepRun(final StepRun stepRun) {
    steps = new ArrayList(1);
    steps.add(stepRun);
    populate();
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
   * @see AbstractFlatTable#TBL_ROW_FETCHED
   * @see AbstractFlatTable#TBL_NO_MORE_ROWS
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex == steps.size()) return TBL_NO_MORE_ROWS;
    final Component[] row = getRow(rowIndex);
    final StepRun stepRun = (StepRun) steps.get(rowIndex);
    ((Label) row[COL_SEQ_NAME]).setText(stepRun.getName());
    row[COL_SEQ_NAME].setForeground(WebuiUtils.getBuildResultColor(getTierletContext(), stepRun));
    ((Label) row[COL_SEQ_RESULT]).setText(BuildRun.buildResultToString(stepRun.getResultID()));
    ((Label) row[COL_SEQ_TIME]).setText(getDurationAsString(stepRun));
    ((StepLogsPanel) row[COL_SEQ_LOGS]).setStepRun(stepRun);
    return TBL_ROW_FETCHED;
  }


  /**
   * Helper methods to convert sequence duration to a String with
   * minutes and seconds.
   */
  private String getDurationAsString(final StepRun stepRun) {
    return StringUtils.durationToString(stepRun.getDuration(), false).toString();
  }


  /**
   * Makes row, should be implemented by successor class
   */
  protected Component[] makeRow(final int rowIndex) {
    final Component[] row = new Component[COLUMN_NUMBER];
    row[COL_SEQ_NAME] = new CommonLabel();
    row[COL_SEQ_RESULT] = new CommonLabel();
    row[COL_SEQ_TIME] = new CommonLabel();
    row[COL_SEQ_LOGS] = new StepLogsPanel();
    return row;
  }


  /**
   * @return header
   */
  protected Component[] makeHeader() {
    final Component[] header = new Component[COLUMN_NUMBER];
    header[COL_SEQ_NAME] = new TableHeaderLabel(COL_NAME_STEP_NAME, 100);
    header[COL_SEQ_RESULT] = new TableHeaderLabel(COL_NAME_SEQ_RESULT, 50);
    header[COL_SEQ_TIME] = new TableHeaderLabel(COL_NAME_TIME, 60, Layout.CENTER);
    header[COL_SEQ_LOGS] = new TableHeaderLabel(COL_NAME_SEQ_LOGS, 100);
    return header;
  }
}
