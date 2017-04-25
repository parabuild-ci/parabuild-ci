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
package org.parabuild.ci.webui.admin.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Color;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * ErrorTable list active errors
 */
final class ErrorTable extends AbstractFlatTable {

  private static final long serialVersionUID = -1414647855166903549L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ErrorTable.class); // NOPMD

  private static final int COLUMN_COUNT = 6;
  private static final int MAX_ERRORS = Integer.MAX_VALUE;

  private List errorList = new ArrayList(COL_SEVERITY);
  private static final int COL_TIME = 0;
  private static final int COL_SEVERITY = 1;
  private static final int COL_BUILD_NAME = 2;
  private static final int COL_AGENT = 3;
  private static final int COL_DESCRIPTION = 4;
  private static final int COL_COMMANDS = 5;


  ErrorTable(final boolean editable) {
    super(COLUMN_COUNT, editable);
    setWidth("100%");
  }


  /**
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {

    if (rowIndex + COL_SEVERITY > errorList.size()) {
      return TBL_NO_MORE_ROWS;
    }

    // load error
    final String errorID = (String) errorList.get(rowIndex);
    final Error error = ErrorManagerFactory.getErrorManager().loadActiveError(errorID);

    // make link param
    final Properties params = new Properties();
    params.setProperty(Pages.PARAM_ERROR_ID, errorID);

    // fill row
    final Component[] row = getRow(rowIndex);
    ((Link) row[COL_TIME]).setText(SystemConfigurationManagerFactory.getManager().formatDateTime(error.getTime()));
    ((Link) row[COL_TIME]).setParameters(params);
    ((Label) row[COL_SEVERITY]).setText(error.getErrorLevelAsString());
    ((Label) row[COL_AGENT]).setText(error.getHostName());
    ((Link) row[COL_DESCRIPTION]).setText(error.getDescription());
    ((Link) row[COL_DESCRIPTION]).setParameters(params);
    ((Link) row[COL_COMMANDS]).setParameters(params);

    // Set optional build name link
    ((BuildNameLinkFlow) row[COL_BUILD_NAME]).setBuildName(error.getBuildName(), error.getBuildID());

    // Adjust severity color
    final int errorLevel = error.getErrorLevel();
    if (errorLevel == Error.ERROR_LEVEL_ERROR || errorLevel == Error.ERROR_LEVEL_FATAL) {
      row[COL_SEVERITY].setForeground(Color.Red);
    } else if (errorLevel == Error.ERROR_LEVEL_WARNING) {
      row[COL_SEVERITY].setForeground(Color.DarkRed);
    } else if (errorLevel == Error.ERROR_LEVEL_INFO) {
      row[COL_SEVERITY].setForeground(Color.DarkGreen);
    }

    return TBL_ROW_FETCHED;
  }


  /**
   * Constructor - creates an instance of flat table with given
   * number of columns
   *
   * @param columnCount number of columns ih the table
   * @param editable    true if editting is allowed
   */
  ErrorTable(final int columnCount, final boolean editable) {
    super(columnCount, editable);    //To change body of overridden methods use File | Settings | File Templates.
  }


  /**
   */
  public Component[] makeHeader() {
    final Component[] headers = new Label[COLUMN_COUNT];
    headers[COL_TIME] = new TableHeaderLabel("Time", "10%");
    headers[COL_SEVERITY] = new TableHeaderLabel("Severity", "9%");
    headers[COL_BUILD_NAME] = new TableHeaderLabel("Build Name", "10%");
    headers[COL_AGENT] = new TableHeaderLabel("Agent", "10%");
    headers[COL_DESCRIPTION] = new TableHeaderLabel("Brief Error Description", "53%");
    headers[COL_COMMANDS] = new TableHeaderLabel("", "9%");
    return headers;
  }


  /**
   * Makes row, should be implemented by successor class
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[COLUMN_COUNT];
    result[COL_TIME] = new CommonLink("", Pages.ADMIN_ERROR_DETAILS);
    result[COL_SEVERITY] = new BoldCommonLabel("");
    result[COL_BUILD_NAME] = new BuildNameLinkFlow();
    result[COL_AGENT] = new CommonLabel("");
    result[COL_DESCRIPTION] = new CommonLink("", Pages.ADMIN_ERROR_DETAILS);
    result[COL_COMMANDS] = new CommonCommandLink("Clear", Pages.ADMIN_CLEAR_ERROR);
    return result;
  }


  /**
   * Populates table
   */
  public void populate() {
    errorList = ErrorManagerFactory.getErrorManager().getActiveErrorIDs(MAX_ERRORS);
    super.populate();
  }


  /**
   * Shows build name link possible aligned if parallel
   */
  private static final class BuildNameLinkFlow extends Flow {

    private static final long serialVersionUID = 7164863564917773715L;


    public void setBuildName(final String buildName, final int buildID) {
      clear();
      // Check if preconditions are met
      if (StringUtils.isBlank(buildName) || buildID == BuildConfig.UNSAVED_ID) {
        return;
      }
      // Create link
      final Properties params = new Properties();
      params.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(buildID));
      params.setProperty(Pages.PARAM_STATUS_VIEW, Pages.STATUS_VIEW_DETAILED);
      // We have to set the group to All because otherwise the build status
      // page may pick a remembered group that may not contain the target
      // build. See PARABUILD-1435 for more information.
      params.setProperty(Pages.PARAM_GROUP_ID, Integer.toString(DisplayGroup.DISPLAY_GROUP_ID_ALL));
      final CommonLink lnkBuildName = new CommonLink(buildName, Pages.PUBLIC_BUILDS);
      lnkBuildName.setParameters(params);
      // Add link
      add(lnkBuildName);
    }
  }


  public String toString() {
    return "ErrorTable{" +
            "errorList=" + errorList +
            '}';
  }
}