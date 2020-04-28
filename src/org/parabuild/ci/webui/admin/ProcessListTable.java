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
package org.parabuild.ci.webui.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.process.OSProcess;
import org.parabuild.ci.process.ProcessManager;
import org.parabuild.ci.process.ProcessManagerFactory;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;

import java.util.List;


/**
 * ErrorTable list active errors
 */
public final class ProcessListTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4964283596313349866L; // NOPMD

  private static final Log log = LogFactory.getLog(ProcessListTable.class);

  private static final int COLUMN_COUNT = 5;

  private List processList;


  public ProcessListTable(final boolean editable) {
    super(COLUMN_COUNT, editable);
    setWidth(Pages.PAGE_WIDTH);
  }


  /**
   * @return
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {

    // check boundaries
    if (rowIndex + 1 > processList.size()) return TBL_NO_MORE_ROWS;

    // getProcess
    final OSProcess process = (OSProcess) processList.get(rowIndex);

    // fill row
    final Component[] row = getRow(rowIndex);
    ((Label) row[0]).setText(process.getUser());
    ((Label) row[1]).setText(Integer.toString(process.getPID()));
    ((Label) row[2]).setText(Integer.toString(process.getPPID()));
    ((Label) row[3]).setText(process.getName());
    ((Label) row[4]).setText(process.getCommandLine());

    return TBL_ROW_FETCHED;
  }


  /**
   * @return
   */
  public Component[] makeHeader() {
    final Component[] headers = new Label[COLUMN_COUNT];
    headers[0] = new TableHeaderLabel("User", 60);
    headers[1] = new TableHeaderLabel("PID", 60);
    headers[2] = new TableHeaderLabel("PPID", 60);
    headers[3] = new TableHeaderLabel("Name", 70);
    headers[4] = new TableHeaderLabel("RemoteCommand line", 320);
    return headers;
  }


  /**
   * Makes row, should be implemented by successor class
   *
   * @return
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[COLUMN_COUNT];
    result[0] = makeCell();
    result[1] = makeCell();
    result[2] = makeCell();
    result[3] = makeCell();
    result[4] = makeCell();
    return result;
  }


  private static CommonLabel makeCell() {
    final CommonLabel c = new CommonLabel("");
    c.setAlignY(Layout.TOP);
    return c;
  }


  /**
   * Populates table
   */
  public void populate() {
    try {
      // REVIEWME: currently shows only local host
      final AgentEnvironment agentEnv = AgentManager.getInstance().getAgentEnvironment(new AgentHost(AgentConfig.BUILD_MANAGER, ""));
      processList = ProcessManagerFactory.getProcessManager(agentEnv).getProcesses(ProcessManager.SORT_BY_PID);
    } catch (final Exception e) {
      super.showErrorMessage("There was a error retrieving process list");
      log.warn(STR_IGNORED_EXCEPTION, e);
    }
    super.populate();
  }
}