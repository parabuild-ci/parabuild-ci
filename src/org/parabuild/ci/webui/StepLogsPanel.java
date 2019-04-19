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
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Panel;

import java.util.Iterator;
import java.util.List;


/**
 * This panels shows a vertical list of links pointing to
 * seqience logs
 */
public final class StepLogsPanel extends Panel {

  private static final long serialVersionUID = -3687139639623236209L; // NOPMD


  public void setStepRun(final StepRun stepRun) {
    clear();
    final boolean stepRunIsSuccessful = stepRun.isSuccessful();
    final ConfigurationManager configManager = ConfigurationManager.getInstance();
    final List stepLogs = configManager.getAllStepLogs(stepRun.getID());
    for (final Iterator iter = stepLogs.iterator(); iter.hasNext();) {
      final StepLog stepLog = (StepLog) iter.next();
      if (skipThisErrorWindow(stepRun, stepLog)) continue;
      final CommonLink lnkLog = new LogLink(stepRun.getBuildRunID(), stepLog);
      if (!stepRunIsSuccessful && stepLog.getType() == StepLog.TYPE_MAIN) {
        lnkLog.setForeground(WebuiUtils.getBuildResultColor(getTierletContext(), stepRun));
      }
      lnkLog.setPadding(2);
      add(lnkLog);
    }
  }


  /**
   * Returns true is this error window should be skipped
   */
  private static boolean skipThisErrorWindow(final StepRun stepRun, final StepLog stepLog) {
    return stepRun.getResultID() == BuildRun.BUILD_RESULT_SUCCESS
            && stepLog.getType() == StepLog.TYPE_WINDOW;
  }
}