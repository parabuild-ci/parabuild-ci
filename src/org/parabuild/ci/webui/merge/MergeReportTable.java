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

import java.text.*;
import java.util.*;

import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.merge.MergeReport;
import org.parabuild.ci.object.BranchChangeList;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Component;
import viewtier.ui.Image;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Link;

/**
 * Shows merge report. The minila coumns are:
 *
 * 1. Status - merged/no merged
 * 2. Change list number
 * 3. Change list date
 * 4. Change list user
 * 5. Change list description
 */
final class MergeReportTable extends AbstractFlatTable {

  private static final int COLUMN_COUNT = 6;
  private static final int COL_IMAGE = 0;
  private static final int COL_STATUS = 1;
  private static final int COL_NUMBER = 2;
  private static final int COL_DATE = 3;
  private static final int COL_USER = 4;
  private static final int COL_DESCRIPTION = 5;

  private static final String CAPTION_STATUS = "Integration Status";
  private static final String CAPTION_NUMBER = "Change";
  private static final String CAPTION_DATE = "Date";
  private static final String CAPTION_USER = "User";
  private static final String CAPTION_DESCRIPTION = "Description";

  private List report = Collections.EMPTY_LIST;
  private final SimpleDateFormat simpleDateFormat;


  /**
   * Constructor - creates an instance of flat table with given
   * number of columns
   */
  public MergeReportTable() {
    super(COLUMN_COUNT, false);
    setWidth("100%");
    simpleDateFormat = new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat());
  }


  /**
   */
  protected Component[] makeHeader() {
    return new Component[]{
      new TableHeaderLabel("", 30),
      new TableHeaderLabel(CAPTION_STATUS, "9%", Layout.CENTER),
      new TableHeaderLabel(CAPTION_NUMBER, "5%", Layout.CENTER),
      new TableHeaderLabel(CAPTION_DATE, "11%", Layout.CENTER),
      new TableHeaderLabel(CAPTION_USER, "10%", Layout.CENTER),
      new TableHeaderLabel(CAPTION_DESCRIPTION, "64 %"),
    };
  }


  /**
   * Makes row, should be implemented by successor class
   *
   */
  protected Component[] makeRow(final int rowIndex) {
    return new Component[]{
      new Image("", "", 24, 24),
      new CommonLabel(Layout.CENTER),
      new CommonLink(Layout.CENTER),
      new CommonLabel(Layout.CENTER),
      new CommonLabel(Layout.CENTER),
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
    if (rowIndex >= report.size()) return TBL_NO_MORE_ROWS;
    final Component[] row = getRow(rowIndex);
    final MergeReport item = (MergeReport)report.get(rowIndex);
    final Image image = (Image)row[COL_IMAGE];
    image.setUrl(makeImageURL(item));
    image.setComment(item.getStringStatus());
    ((Label)row[COL_STATUS]).setText(item.getStatus() == BranchChangeList.MERGE_STATUS_UNKNOWN ? "Calculating" : item.getStringStatus());
    ((Link)row[COL_NUMBER]).setText(item.getNumber());
    ((Link)row[COL_NUMBER]).setParameters(makeChangeListNumberParameters(item));
    ((Link)row[COL_NUMBER]).setUrl(Pages.PAGE_MERGE_CHANGE_LIST);
    ((Label)row[COL_DATE]).setText(simpleDateFormat.format(item.getDate()));
    ((Label)row[COL_USER]).setText(item.getUser());
    ((Label)row[COL_DESCRIPTION]).setText(item.getDescription());
    return TBL_ROW_FETCHED;
  }


  /**
   * Populates the table with report.
   * @param report
   */
  public void populate(final List report) {
    this.report = report;
    populate();
  }


  private String makeImageURL(final MergeReport item) {
    final String imageURL;
    if (item.getStatus() == BranchChangeList.MERGE_STATUS_MERGED) {
      imageURL = "/parabuild/images/3232/bullet_ball_glass_green.gif";
    } else if (item.getStatus() == BranchChangeList.MERGE_STATUS_NOT_MERGED) {
      imageURL = "/parabuild/images/3232/bullet_ball_glass_blue.gif";
    } else {
      imageURL = "/parabuild/images/3232/bullet_ball_glass_gray.gif";
    }
    return imageURL;
  }


  /**
   * Helper method.
   */
  private Properties makeChangeListNumberParameters(final MergeReport item) {
    final Properties properties = new Properties();
    properties.setProperty(Pages.PARAM_BRANCH_CHANGE_LIST_ID, Integer.toString(item.getBranchChangeListID()));
    properties.setProperty(Pages.PARAM_CHANGE_LIST_ID, Integer.toString(item.getChangeListID()));
    return properties;
  }
}
