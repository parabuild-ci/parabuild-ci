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
package org.parabuild.ci.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RemoteCommandManager is a singleton that holds a list currently
 * active RemoteCommands and allows to stop them when requested.
 */
public final class RemoteCommandManager {

  private static final Log log = LogFactory.getLog(RemoteCommandManager.class);
  private static final RemoteCommandManager instance = new RemoteCommandManager();

  private final Map commandListMap = new HashMap(11); // NOPMD


  /**
   * @return reference to the singleton instance of remote command
   *         singleton.
   */
  public static RemoteCommandManager getInstance() {
    return instance;
  }


  /**
   * Singleton constructor.
   */
  private RemoteCommandManager() {
  }


  /**
   * Registers comand about to be executed. Registered commands can be
   * stopped.
   *
   * @param buildID build ID this command belongs to
   * @param command that is about to be executed.
   */
  public synchronized void registerBuildCommand(final int buildID, final RemoteCommand command) {
    getCommandList(buildID).add(command);
  }


  /**
   * Unregister command that finished execution. The command is no
   * longer a subject of a forceful stop.
   *
   * @param command
   */
  public synchronized void unregisterBuildCommand(final int buildID, final RemoteCommand command) {
    getCommandList(buildID).remove(command);
  }


  private synchronized List getCommandList(final int buildID) {
    final Integer key = new Integer(buildID);
    List commands = (List) commandListMap.get(key);
    if (commands == null) {
      commands = new ArrayList(11);
      commandListMap.put(key, commands);
    }
    return commands;
  }


  /**
   * Forcefully kills build commands.
   *
   * @param buildID
   */
  public void killBuildCommands(final int buildID) {
    // get list of commands
    final List commands = getCommandList(buildID);
    // synchronize on a list for the given build
    synchronized (commands) {
      final int commandCount = commands.size();
      if (commandCount == 0) return;
      for (int i = 0; i < commandCount; i++) {
        try {
          final RemoteCommand command = (RemoteCommand) commands.get(i);
          // Kill all by signatures
          final String[] sigs = StringUtils.toStringArray(command.getSignatures());
          if (sigs.length == 0) return; // nothing to search for
          if (log.isDebugEnabled()) log.debug("sgs.length: " + sigs.length);
          final AgentEnvironment agentEnvironment = AgentManager.getInstance().getAgentEnvironment(command.getAgentHost());
          final ProcessManager pm = ProcessManagerFactory.getProcessManager(agentEnvironment);
          pm.setDeep(true);
          final List pids = ProcessUtils.processListToPIDList(pm.getProcesses(ProcessManager.SORT_BY_PID, sigs, true));
          pm.setDeep(false);
          pm.killProcesses(pids);
        } catch (final Exception e) {
          IoUtils.ignoreExpectedException(e); // we want to process as many commands as possible
        }
      }
    }
  }


  public String toString() {
    return "RemoteCommandManager{" +
            "commandListMap=" + commandListMap +
            '}';
  }
}
