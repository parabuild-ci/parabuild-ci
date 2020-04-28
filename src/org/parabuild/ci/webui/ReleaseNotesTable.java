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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.ReleaseNoteChangeList;
import org.parabuild.ci.configuration.ReleaseNoteReport;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.BreakLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Flow;

import java.util.List;
import java.util.Properties;

/**
 * Displays release notes associated with a given build run
 */
public final class ReleaseNotesTable extends AbstractFlatTable {

  private static final long serialVersionUID = 8817073869046278504L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(ReleaseNotesTable.class); //NOPMD

  private static final int COLUMN_COUNT = 2;
  private static final int COL_ISSUE_ID = 0;
  private static final int COL_ISSUE_DESCR = 1;

  private List releaseNotesReportList = null;
  private String buildRunIDAsString = "";


  /**
   * Default constructor.
   */
  public ReleaseNotesTable() {
    super(COLUMN_COUNT, false);
    setWidth(Pages.PAGE_WIDTH);
    populate(0);
  }


  public void populateFromBuildRun(final int buildRunID) {
    this.buildRunIDAsString = Integer.toString(buildRunID);
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    releaseNotesReportList = cm.getReleaseNotesReportList(buildRunID);
    populate();
  }


  /**
   */
  protected Component[] makeHeader() {
    return new Component[]{new TableHeaderLabel("ID", 90), new TableHeaderLabel("Description", 660)};
  }


  /**
   * Makes row, should be implemented by successor class
   */
  protected Component[] makeRow(final int rowIndex) {
    return new Component[]{new Flow(), new Flow()};
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
    // check boundaries
    if (releaseNotesReportList == null || rowIndex >= releaseNotesReportList.size()) return AbstractFlatTable.TBL_NO_MORE_ROWS;

    // get issue
    final ReleaseNoteReport releaseNoteReport = (ReleaseNoteReport)releaseNotesReportList.get(rowIndex);

    // set up columns
    final Flow flIssueID = (Flow)getRow(rowIndex)[COL_ISSUE_ID];
    if (StringUtils.isBlank(releaseNoteReport.getIssueURL())) {
      flIssueID.add(new CommonLabel(releaseNoteReport.getIssueKey()));
    } else {
      final CommonLink lnkIssueID = new CommonLink(releaseNoteReport.getIssueKey(), releaseNoteReport.getIssueURL());
      lnkIssueID.setTarget("_blank"); // will open link in a new window
      flIssueID.add(lnkIssueID);
    }

    // holds description and optional list of change lists.
    final Flow flIssueDescr = (Flow)getRow(rowIndex)[COL_ISSUE_DESCR];
    flIssueDescr.add(new CommonLabel(releaseNoteReport.getIssueDescription()));

    // add chanhe list numbers if any
    if (releaseNoteReport.chageListsSize() > 0) {
      final List releaseNoteChangeLists = releaseNoteReport.getChageLists();
      flIssueDescr.add(new BreakLabel()).add(new TableHeaderLabel("Change list(s): "));
      for (int i = 0, n = releaseNoteChangeLists.size(); i < n; i++) {
        final ReleaseNoteChangeList releaseNoteChangeList = (ReleaseNoteChangeList)releaseNoteChangeLists.get(i);
        final String caption = releaseNoteChangeList.getChangeListNumber() + (i == n - 1 ? ". " : ", ");
        flIssueDescr.add(new CommonLabel(caption));
      }
      // add link to details
      final Properties params = new Properties();
      params.setProperty(Pages.PARAM_BUILD_RUN_ID, buildRunIDAsString);
      params.setProperty(Pages.PARAM_RELEASE_NOTE_ID, releaseNoteReport.getRelnoteID().toString());
      flIssueDescr.add(new CommonLink("Details", Pages.RELEASE_NOTE_DETAILS, params));
    }

    // return
    return AbstractFlatTable.TBL_ROW_FETCHED;
  }
}
