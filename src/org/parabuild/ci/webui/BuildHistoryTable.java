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
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Font;
import viewtier.ui.Label;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Shows build history table
 */
public final class BuildHistoryTable extends AbstractFlatTable {

  private static final long serialVersionUID = -8343239998355727922L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BuildHistoryTable.class); // NOPMD

  public static final int MAX_BUILDS = 25;

  private static final int COLUMN_COUNT = 8;

  // col indexes
  private static final int COL_BUILD_NAME = 0;
  private static final int COL_BUILD_NUMBER = 1;
  private static final int COL_CHANGE_LIST = 2;
  private static final int COL_FINISHED_AT = 3;
  private static final int COL_BUILD_TIME = 4;
  private static final int COL_BUILD_RESULT = 5;
  private static final int COL_NOTE = 6;
  private static final int COL_RESULTS = 7;

  private final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager(); // NOPMD
  private final ConfigurationManager cm = ConfigurationManager.getInstance();
  private List buildRunList = null;
  private String buildNameColumnName;


  /**
   * Constructor
   *
   * @param buildNameColumnName
   */
  public BuildHistoryTable(final String buildNameColumnName) {
    super(COLUMN_COUNT, false);
    this.buildNameColumnName = buildNameColumnName;
    setWidth("100%");
  }


  /**
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= buildRunList.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final Component[] row = getRow(rowIndex);
    final BuildRun buildRun = (BuildRun) buildRunList.get(rowIndex);
    ((BuildChangesLink) row[COL_BUILD_NAME]).setBuildRun(buildRun.getBuildName(), buildRun);
    ((BuildChangesLink) row[COL_BUILD_NUMBER]).setBuildRun(buildRun.getBuildRunNumberAsString(), buildRun);
    ((BuildChangesLink) row[COL_CHANGE_LIST]).setBuildRun(buildRun.getChangeListNumber(), buildRun);
    // handle not-finished yet case
    if (buildRun.getFinishedAt() != null) {
      ((BuildChangesLink) row[COL_FINISHED_AT]).setBuildRun(systemCM.formatDateTime(buildRun.getFinishedAt()), buildRun);
      ((BuildChangesLink) row[COL_BUILD_TIME]).setBuildRun(StringUtils.durationToString((buildRun.getFinishedAt().getTime() - buildRun.getStartedAt().getTime()) / 1000L, false).toString(), buildRun);
      ((BuildResultLink) row[COL_BUILD_RESULT]).setBuildRun(buildRun);
    } else {
      row[COL_FINISHED_AT].setVisible(false);
      row[COL_BUILD_RESULT].setVisible(false);
    }

    final String note = cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_NOTE);
    ((Label) row[COL_NOTE]).setText(StringUtils.isBlank(note) ? "" : note);
    ((BuildRunResultsLinksFlow) row[COL_RESULTS]).setBuildRun(buildRun, false, false, false, false, false);

    return TBL_ROW_FETCHED;
  }


  /**
   */
  public Component[] makeHeader() {
    final Component[] headers = new Label[COLUMN_COUNT];
    headers[COL_BUILD_NAME] = new TableHeaderLabel(buildNameColumnName, "10%", Layout.CENTER);
    headers[COL_BUILD_NUMBER] = new TableHeaderLabel("Build Number", "5%", Layout.CENTER);
    headers[COL_CHANGE_LIST] = new TableHeaderLabel("Change List", "5%", Layout.CENTER);
    headers[COL_FINISHED_AT] = new TableHeaderLabel("Finished at", "5%", Layout.CENTER);
    headers[COL_BUILD_TIME] = new TableHeaderLabel("Build Time", "5%", Layout.CENTER);
    headers[COL_BUILD_RESULT] = new TableHeaderLabel("Build Result", "10%");
    headers[COL_NOTE] = new TableHeaderLabel("Note", "10%");
    headers[COL_RESULTS] = new TableHeaderLabel("Results", "40%");
    return headers;
  }


  /**
   * Makes row, should be implemented by successor class
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[COLUMN_COUNT];
    result[COL_BUILD_NAME] = new BuildChangesLink(Layout.CENTER, BuildChangesLink.UNDERLINE);
    result[COL_BUILD_NUMBER] = new BuildChangesLink(Layout.CENTER, BuildChangesLink.UNDERLINE);
    result[COL_CHANGE_LIST] = new BuildChangesLink(Layout.CENTER, BuildChangesLink.UNDERLINE);
    result[COL_FINISHED_AT] = new BuildChangesLink(Layout.CENTER, BuildChangesLink.UNDERLINE);
    result[COL_BUILD_TIME] = new BuildChangesLink(Layout.CENTER, BuildChangesLink.NO_UNDERLINE);
    result[COL_BUILD_RESULT] = new BuildResultLink();
    result[COL_NOTE] = new CommonLabel();
    result[COL_RESULTS] = new BuildRunResultsLinksFlow();
    return result;
  }


  public void populate(final List completedBuildRuns) {
    buildRunList = new ArrayList(completedBuildRuns);
    super.populate();
  }


  private static final class BuildChangesLink extends CommonLink {

    private static final long serialVersionUID = 0L;
    private static final Font FONT_UNDERLINE = new Font(Pages.COMMON_FONT_FAMILY, Font.Plain | Font.Underlined, Pages.COMMMON_FONT_SIZE);
    private static final Font FONT_NO_UNDERLINE = new Font(Pages.COMMON_FONT_FAMILY, Font.Plain | Font.None, Pages.COMMMON_FONT_SIZE);
    private static final Font FONT_UNDERLINE_BOLD = new Font(Pages.COMMON_FONT_FAMILY, Font.Bold | Font.Underlined, Pages.COMMMON_FONT_SIZE);
    private static final Font FONT_NO_UNDERLINE_BOLD = new Font(Pages.COMMON_FONT_FAMILY, Font.Bold | Font.None, Pages.COMMMON_FONT_SIZE);
    private static final long HOUR = 60L * 60L * 1000L;
    private static final boolean UNDERLINE = true;
    private static final boolean NO_UNDERLINE = false;
    private final boolean underline;


    private BuildChangesLink(final int alignX, final boolean underline) {
      super("", Pages.BUILD_CHANGES);
      this.underline = underline;
      setFont(underline ? FONT_UNDERLINE : FONT_NO_UNDERLINE);
      setForeground(Pages.COLOR_COMMON_LINK_FG);
      setAlignX(alignX);
    }


    public void setBuildRun(final String caption, final BuildRun buildRun) {
      setText(caption);
      setParameters(createParameters(buildRun.getBuildRunIDAsString()));
      if (isYoungerThanHour(buildRun)) {
        setFont(underline ? FONT_UNDERLINE_BOLD : FONT_NO_UNDERLINE_BOLD);
      } else {
        setFont(underline ? FONT_UNDERLINE : FONT_NO_UNDERLINE);
      }
    }


    private boolean isYoungerThanHour(final BuildRun buildRun) {
      if (buildRun.getFinishedAt() == null) {
        return false;
      }

      if ((System.currentTimeMillis() - buildRun.getFinishedAt().getTime()) <= HOUR) {
        return true;
      }
      return false;
    }


    private Properties createParameters(final String buildRunIDAsString) {
      final Properties result = new Properties();
      result.setProperty(Pages.PARAM_BUILD_RUN_ID, buildRunIDAsString);
      return result;
    }
  }


  public String toString() {
    return "BuildHistoryTable{" +
            "systemCM=" + systemCM +
            ", buildRunList=" + buildRunList +
            '}';
  }
}