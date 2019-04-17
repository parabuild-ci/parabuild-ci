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

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 *
 */
final class ResultsActionsTable extends AbstractFlatTable {

  private static final int COLUMN_COUNT = 4;

  private static final int COL_ACTION = 0;
  private static final int COL_DATE = 1;
  private static final int COL_USER = 2;
  private static final int COL_DESCRIPTION = 3;
  private static final String CAPTION_ACTION = "Result action log";
  private static final String CAPTION_DATE = "Date";
  private static final String CAPTION_USER = "User";
  private static final String CAPTION_DESCRIPTION = "Description";
  private static final long serialVersionUID = -6333056828222856518L;

  private final List buildRunActionVOs;
  private final SimpleDateFormat dateTimeFormat;


  /**
   * Constructor.
   *
   * @param buildRunActionVOs List of {@link BuildRunActionVO}
   */
  public ResultsActionsTable(final List buildRunActionVOs) {
    super(COLUMN_COUNT, false);
    this.buildRunActionVOs = buildRunActionVOs;
    this.dateTimeFormat = new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat());
    populate();
  }


  /**
   */
  protected Component[] makeHeader() {
    return new Component[]{
      new TableHeaderLabel(CAPTION_ACTION, 270),
      new TableHeaderLabel(CAPTION_DATE, 130),
      new TableHeaderLabel(CAPTION_USER, 130),
      new TableHeaderLabel(CAPTION_DESCRIPTION, 270),
    };
  }


  /**
   * Makes row, should be implemented by successor class
   *
   */
  protected Component[] makeRow(final int rowIndex) {
    return new Component[]{
      new CommonLabel(),
      new CommonLabel(),
      new CommonLabel(),
      new CommonLabel(),
    };
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
    if (rowIndex >= buildRunActionVOs.size()) return TBL_NO_MORE_ROWS;
    final Component[] row = getRow(rowIndex);
    final BuildRunActionVO buildRunActionVO = (BuildRunActionVO)buildRunActionVOs.get(rowIndex);
    ((Label)row[COL_ACTION]).setText(buildRunActionVO.getAction());
    ((Label)row[COL_DATE]).setText(buildRunActionVO.getDateAsString(dateTimeFormat));
    ((Label)row[COL_USER]).setText(buildRunActionVO.getUser());
    ((Label)row[COL_DESCRIPTION]).setText(buildRunActionVO.getDescription());
    return TBL_ROW_FETCHED;
  }
}
