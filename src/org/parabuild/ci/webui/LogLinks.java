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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.MenuDividerLabel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;

import java.util.Iterator;
import java.util.List;

/**
 * Build log links
 */
public final class LogLinks extends Flow {

  private final int logCount;


  /**
   * Constructor - creates log links for the given build run.
   *
   * @param buildRunID  to show log links for.
   * @param showAllLogs if true will show all logs, if false will show
   *                    only main logs.
   */
  public LogLinks(final int buildRunID, final boolean showAllLogs) {
    // collect logs from step runs.
    int total = 0;
    final List stepRuns = ConfigurationManager.getInstance().getStepRuns(buildRunID);
    for (final Iterator i = stepRuns.iterator(); i.hasNext();) {
      final StepRun stepRun = (StepRun) i.next();
      final boolean stepRunIsSuccessful = stepRun.isSuccessful();
      final List stepLogs = getStepLogs(stepRun, showAllLogs);
      // show log links
      final int n = stepLogs.size();
      for (int j = 0; j < n; j++) {
        final StepLog stepLog = (StepLog) stepLogs.get(j);
        // NOTE: simeshev@parabuildci.org -> 2007-05-12 - we
        // skip creating a link to this log temporarily
        // because we don't know how to render it yet.
        if (stepLog.getType() == StepLog.PATH_TYPE_FINDBUGS_XML) continue;

        final CommonLink lnkLog = makeLogLink(buildRunID, stepLog);
        lnkLog.setPadding(2);
        if (!stepRunIsSuccessful && stepLog.getType() == StepLog.TYPE_MAIN) {
          lnkLog.setForeground(WebuiUtils.getBuildResultColor(getTierletContext(), stepRun));
        }
        add(lnkLog);
        total++;
        if (i.hasNext() || !i.hasNext() && j < n - 1) {
          add(new MenuDividerLabel());
        }
      }
    }
    logCount = total;
  }


  /**
   * @return Number of log links
   */
  public int getLogCount() {
    return logCount;
  }


  /**
   * Helper method to return step logs for the given step run.
   */
  private List getStepLogs(final StepRun stepRun, final boolean showAllLogs) {
    if (showAllLogs) {
      return ConfigurationManager.getInstance().getAllStepLogs(stepRun.getID());
    } else {
      return ConfigurationManager.getInstance().getMainAndWindowStepLogs(stepRun);
    }
  }


  private CommonLink makeLogLink(final int buildRunID, final StepLog stepLog) {
    final CommonLink logLink = new LogLink(buildRunID, stepLog);
    if (stepLog.getPathType() == StepLog.PATH_TYPE_HTML_DIR
            || stepLog.getPathType() == StepLog.PATH_TYPE_HTML_FILE) {
      logLink.setTarget("_blank");
    }
    return logLink;
  }
}
