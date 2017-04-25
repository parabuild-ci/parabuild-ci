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
package org.parabuild.ci.webui.secured;

import java.util.*;

import org.parabuild.ci.webui.*;
import org.parabuild.ci.webui.common.*;

/**
 */
final class MaintenanceCommandsPanelImpl extends MaintenanceCommandsPanel {

  private static final String CAPTION_MAINTENANCE_COMMANDS = "Maintenance Commands:";
  private static final String CAPTION_CLEANUP_LOGS = "Cleanup Logs";
  private static final String CAPTION_CLEANUP_RESULTS = "Cleanup Results";
  
  private final AnnotatedCommandLink flwCleanupLogs = new AnnotatedCommandLink(CAPTION_CLEANUP_LOGS, Pages.ADMIN_CLEANUP_LOGS, "deletes old build logs from archive.", true);
  private final AnnotatedCommandLink flwCleanupResults = new AnnotatedCommandLink(CAPTION_CLEANUP_RESULTS, Pages.ADMIN_CLEANUP_RESULTS, "deletes old build results from archive.", true);


  public MaintenanceCommandsPanelImpl() {
    add(new BoldCommonLabel(CAPTION_MAINTENANCE_COMMANDS));
    add(flwCleanupLogs);
    add(flwCleanupResults);
  }


  /**
   * Sets command parameters.
   *
   * @param params command parameters to set.
   */
  public void setParameters(final Properties params) {
    flwCleanupLogs.setParameters(params);
    flwCleanupResults.setParameters(params);
  }


  public int commandsAvailable() {
    return 2; // FIXME
  }
}
