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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.merge.MergeQueueReport;
import org.parabuild.ci.object.Merge;
import org.parabuild.ci.object.MergeChangeList;
import org.parabuild.ci.webui.BuildResultLink;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Image;
import viewtier.ui.Label;
import viewtier.ui.Layout;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * Shows merge report. The minila coumns are:
 *
 * 1. Status - merged/no merged
 * 2. Change list number
 * 3. Change list date
 * 4. Change list user
 * 5. Change list description
 */
final class MergeQueueTable extends AbstractFlatTable {

  private static final int COLUMN_COUNT = 9;

  private static final int COL_IMAGE = 0;
  private static final int COL_MERGE_CHANGE_LIST_RESULT = 1;
  private static final int COL_MERGE_CHANGE_LIST_RESULT_DESCRIPTION = 2;
  private static final int COL_NUMBER = 3;
  private static final int COL_DATE = 4;
  private static final int COL_USER = 5;
  private static final int COL_DESCRIPTION = 6;
  private static final int COL_MERGE_VERIFIED = 7;
  private static final int COL_MERGE_RESULT_DESCRIPTION = 8;

  private static final String CAPTION_RESULT = "Integration Result";
  private static final String CAPTION_RESULT_DESCRIPTION = "Result Description";
  private static final String CAPTION_NUMBER = "Change";
  private static final String CAPTION_DATE = "Date";
  private static final String CAPTION_USER = "User";
  private static final String CAPTION_DESCRIPTION = "Description";
  private static final String CAPTION_GROUP_VERIFIED = "Group Verified";
  private static final String CAPTION_GROUP_RESULT = "Group Result";
  private static final long serialVersionUID = -4634051626335212061L;

  private List report = Collections.emptyList();
  private final SimpleDateFormat simpleDateFormat;


  /**
   * Constructor - creates an instance of flat table with given
   * number of columns
   */
  public MergeQueueTable() {
    super(COLUMN_COUNT, false);
    setWidth("100%");
    simpleDateFormat = new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat());
  }


  /**
   */
  protected Component[] makeHeader() {
    return new Component[]{
      new TableHeaderLabel("", 30),
      new TableHeaderLabel(CAPTION_RESULT, "9%", Layout.CENTER),
      new TableHeaderLabel(CAPTION_RESULT_DESCRIPTION, "15%", Layout.CENTER),
      new TableHeaderLabel(CAPTION_NUMBER, "5%", Layout.CENTER),
      new TableHeaderLabel(CAPTION_DATE, "11%", Layout.CENTER),
      new TableHeaderLabel(CAPTION_USER, "10%", Layout.CENTER),
      new TableHeaderLabel(CAPTION_DESCRIPTION, "45%"),
      new TableHeaderLabel(CAPTION_GROUP_VERIFIED, "2%"),
      new TableHeaderLabel(CAPTION_GROUP_RESULT, "10%"),
    };
  }


  /**
   * Makes row, should be implemented by successor class
   *
   */
  protected Component[] makeRow(final int rowIndex) {
    return new Component[]{
      new Image("", "", 24, 24),
      new ResultFlow(),
      new CommonLabel(Layout.CENTER),
      new CommonLabel(Layout.CENTER),
      new CommonLabel(Layout.CENTER),
      new CommonLabel(Layout.CENTER),
      new CommonLabel(),
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
    final MergeQueueReport item = (MergeQueueReport)report.get(rowIndex);
    final Image image = (Image)row[COL_IMAGE];
    image.setUrl(makeImageURL(item));
    image.setComment(item.getStringMergeChangeListResult());
    ((ResultFlow)row[COL_MERGE_CHANGE_LIST_RESULT]).setReport(item);
    ((Label)row[COL_MERGE_CHANGE_LIST_RESULT_DESCRIPTION]).setText(item.getMergeChangeListResultDescription());
    ((Label)row[COL_NUMBER]).setText(item.getChangeListNumber());
    ((Label)row[COL_DATE]).setText(simpleDateFormat.format(item.getCreatedAt()));
    ((Label)row[COL_USER]).setText(item.getUser());
    ((Label)row[COL_DESCRIPTION]).setText(item.getDescription());
    ((Label)row[COL_MERGE_VERIFIED]).setText(Boolean.toString(item.isValidated()));
    ((Label)row[COL_MERGE_RESULT_DESCRIPTION]).setText(item.getStringMergeResult());
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


  private static String makeImageURL(final MergeQueueReport item) {
    final String imageURL;
    if (item.getMergeChangeListResultID() == MergeChangeList.RESULT_SUCCESS) {
      imageURL = WebUIConstants.IMAGE_32x32_BULLET_BALL_GLASS_GREEN_GIF;
    } else if (item.getMergeChangeListResultID() == MergeChangeList.RESULT_NOT_MERGED) {
      imageURL = WebUIConstants.IMAGE_32x32_BULLET_BALL_GLASS_BLUE_GIF;
    } else if (item.getMergeChangeListResultID() == MergeChangeList.RESULT_CONFLICTS) {
      imageURL = WebUIConstants.IMAGE_32x32_BULLET_BALL_GLASS_RED_GIF;
    } else if (item.getMergeChangeListResultID() == MergeChangeList.RESULT_NOTHING_TO_MERGE) {
      imageURL = WebUIConstants.IMAGE_32x32_BULLET_BALL_GLASS_RED_GIF;
    } else {
      imageURL = WebUIConstants.IMAGE_32x32_BULLET_BALL_GLASS_GRAY_GIF;
    }
    return imageURL;
  }


  /**
   * ResultFlow is a mutating type component that shows text
   * or a link for merge result depending on the result.
   */
  private static class ResultFlow extends Flow {

    private static final String CAPTION_VALIDATION_FAILED = "Validation failed";
    private static final long serialVersionUID = 6645665753810980862L;


    /**
     * Constuctor.
     */
    public ResultFlow() {
      setAlignX(Layout.CENTER);
    }


    void setReport(final MergeQueueReport report) {
      // create caption
      final String caption;
      if (report.getMergeResultID() == Merge.RESULT_VALIDATION_FAILED) {
        caption = CAPTION_VALIDATION_FAILED;
      } else {
        caption = report.getStringMergeChangeListResult();
      }

      // careate and add component
      if (report.getTargetBuildRunID() == null) {
        add(new CommonLabel(caption));
      } else {
        add(new BuildResultLink(caption, ConfigurationManager.getInstance().getBuildRun(report.getTargetBuildRunID())));
      }
    }
  }
}
