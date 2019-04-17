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
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.webui.secured.BuildCommandsLinks;
import org.parabuild.ci.webui.secured.SecuredComponentFactory;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class BuildStatusesTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BuildStatusesTable.class); // NOPMD

  public static final int SHOW_COMMANDS = 1 << 1;
  public static final int SHOW_IP_ADDRESS = 1 << 2;
  public static final int SHOW_NEXT_RUN = 1 << 3;

  public static final String STR_BUILD_NAME = "Build Name";
  public static final String STR_BUILD_STATUS = "Status";
  public static final String STR_BUILD_NUMBER = "Build Number";
  public static final String STR_BUILD_TIME = "Build Time";
  public static final String STR_RESULT = "Result";
  public static final String STR_FINISHED = "Finished at";
  public static final String STR_LOGS = "Logs";
  public static final String STR_IP_ADDRESS = "Agent";
  public static final String STR_NEXT_BUILD = "Next Build";
  public static final String STR_COMMANDS = "Commands";

  /**
   * Lazily initializable leader IDs.
   */
  protected Set leaderIDs = null;

  protected List statuses = null;
  private final String dateTimeFormat;

  private final int flags;


  /**
   * @see BuildState
   */
  public BuildStatusesTable(final int flags) {
    super(calculateColumnCount(flags), false, false);
    //noinspection OverridableMethodCallInConstructor
    setWidth("100%");
    getUserPanel().setWidth("100%");
    setRowHeight(35);
    setGridColor(Pages.COLOR_PANEL_BORDER);
    this.flags = flags;
    this.dateTimeFormat = SystemConfigurationManagerFactory.getManager().getDateTimeFormat();
  }


  /**
   * Returs array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   */
  public Component[] makeHeader() {
    final List result = new ArrayList(11);
    result.add(new TableHeaderLabel(STR_BUILD_NAME, "17%"));
    addIfFlagSet(new TableHeaderLabel(STR_IP_ADDRESS, "10%", Layout.CENTER), SHOW_IP_ADDRESS, result);
    addIfFlagSet(new TableHeaderLabel(STR_NEXT_BUILD, "10%", Layout.CENTER), SHOW_NEXT_RUN, result);
    result.add(new TableHeaderLabel(STR_BUILD_STATUS, "14%", Layout.CENTER));
    result.add(new TableHeaderLabel(STR_BUILD_NUMBER, "10%", Layout.CENTER));
    result.add(new TableHeaderLabel(STR_BUILD_TIME, "14%", Layout.CENTER));
    result.add(new TableHeaderLabel(STR_RESULT, "40%"));
    addIfFlagSet(new TableHeaderLabel(STR_COMMANDS, "17%"), SHOW_COMMANDS, result);
    //noinspection SuspiciousToArrayCall
    return (Component[]) result.toArray(new TableHeaderLabel[0]);
  }


  /**
   * Makes row
   */
  public Component[] makeRow(final int rowIndex) {
    final List result = new ArrayList(11);
    result.add(new BuildNameLinkFlow());
    addIfFlagSet(new CommonLabel(Layout.CENTER), SHOW_IP_ADDRESS, result);
    addIfFlagSet(new CommonLabel(Layout.CENTER), SHOW_NEXT_RUN, result);
    result.add(new BuildStatusFlow());
    result.add(new BuildNumberFlow());
    result.add(new BuildTimeFlow(dateTimeFormat));
    result.add(new BuildResultFlow(dateTimeFormat));
    addIfFlagSet(SecuredComponentFactory.getInstance().makeBuildCommandsLinks(), SHOW_COMMANDS, result);
    return (Component[]) result.toArray(new Component[0]);
  }


  /**
   * Returs array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= statuses.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final BuildState state = (BuildState) statuses.get(rowIndex);
    final Component[] row = getRow(rowIndex);

    // build name link
    if (state.isParallel() && leaderIDs == null) {
      leaderIDs = WebuiUtils.getLeaderBuildIDs(statuses);
    }
    int columnIndex = -1;
    final BuildNameLinkFlow nameLinkFlow = (BuildNameLinkFlow) row[++columnIndex];
    nameLinkFlow.setBuildState(state, leaderIDs);

    // IP Address
    if (isFlagSet(SHOW_IP_ADDRESS)) {
      ((Label) row[++columnIndex]).setText(getIPAddress(state));
    }

    // Next run
    if (isFlagSet(SHOW_NEXT_RUN)) {
      ((Label) row[++columnIndex]).setText(getNextRun(state));
    }

    // status
    ((BuildStatusFlow) row[++columnIndex]).setState(state);


    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRun currentBuildRun = cm.getBuildRun(state.getCurrentlyRunningBuildRunID());
    final BuildRun lastCompleteBuildRun = state.getLastCompleteBuildRun();

    // number
    ((BuildNumberFlow) row[++columnIndex]).setState(currentBuildRun, lastCompleteBuildRun);

    // time
    ((BuildTimeFlow) row[++columnIndex]).setState(currentBuildRun, lastCompleteBuildRun);

    // result/status
    ((BuildResultFlow) row[++columnIndex]).setState(state);

    // Commands
    if (isFlagSet(SHOW_COMMANDS)) {
      ((BuildCommandsLinks) row[++columnIndex]).setBuildStatus(state);
    }


    return TBL_ROW_FETCHED;
  }


  /**
   * Populates table with builds statuses. This list is reused in
   * fetchRow method.
   *
   * @see BuildState
   */
  public final void populate(final List buildStatusList) {
    statuses = buildStatusList;
    super.populate();
  }


  /**
   * Returns next time for build run.
   *
   * @param state state
   * @return next time for build run.
   */
  private String getNextRun(final BuildState state) {
    final Date time = state.getNextBuildTime();
    if (time == null) {
      if (BuildStatus.INACTIVE.equals(state.getStatus())) {
        return "";
      }
      if (state.getSchedule() == BuildConfig.SCHEDULE_TYPE_AUTOMATIC) {
        return "Automatic";
      } else if (state.getSchedule() == BuildConfig.SCHEDULE_TYPE_MANUAL) {
        return "Manual";
      } else if (state.getSchedule() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
        return "Parallel";
      }
    } else {
      return StringUtils.formatDate(time, dateTimeFormat);
    }
    return "";
  }


  private static int calculateColumnCount(final int flags) {
    int columnIndex = 0;

    // build name link
    ++columnIndex;

    // IP Address
    if (isFlagSet(flags, SHOW_IP_ADDRESS)) {
      ++columnIndex;
    }

    // Next run
    if (isFlagSet(flags, SHOW_NEXT_RUN)) {
      ++columnIndex;
    }

    // status
    ++columnIndex;


    // number
    ++columnIndex;

    // time
    ++columnIndex;

    // result/status
    ++columnIndex;

    // Commands
    if (isFlagSet(flags, SHOW_COMMANDS)) {
      ++columnIndex;
    }

    return columnIndex;
  }


  /**
   * Returns IP address.
   *
   * @param state state
   * @return IP address.
   */
  private static String getIPAddress(final BuildState state) {
    return state.getCurrentlyRunningOnBuildHost();
  }


  private void addIfFlagSet(final Component component, final int flag, final List result) {
    if (isFlagSet(flag)) {
      result.add(component);
    }
  }


  private boolean isFlagSet(final int flag) {
    return isFlagSet(flags, flag);
  }


  private static boolean isFlagSet(final int flags, final int flag) {
    return (flags & flag) != 0;
  }


  public String toString() {
    return "BuildStatusesTable{" +
            "statuses=" + statuses +
            ", dateTimeFormat='" + dateTimeFormat + '\'' +
            '}';
  }
}
