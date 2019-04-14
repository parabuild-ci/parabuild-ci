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
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.AgentEnvironment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * LinuxProcessParser is Linux implementation of ProcessParser
 * interface
 */
public final class LinuxProcessParser implements ProcessParser {

  private static final Log log = LogFactory.getLog(LinuxProcessParser.class);

  private static final String CMD_PS = "/bin/ps";

  /**
   * Kills specified processes
   */
  private static final String KILL = "/bin/kill -9 ";


  /**
   * RemoteCommand used to list processes -e - all running
   * processes -o - display PID PPID USER COMMAND h - not include
   * headers cols - number of columns on screen (fake, to have
   * full lines)
   */
  private static final String LIST_PROCESSES = CMD_PS + " -e h -o pid,ppid,user,command --cols=512000";


  /**
   * RemoteCommand used to retrieve processed environment -e -
   * all running processes -o - display PID PPID USER COMMAND e -
   * include system environment h - not include headers cols -
   * number of columns on screen (fake, to have full lines)
   */
  private static final String LIST_PROCESSES_ENV = CMD_PS + " -e he -o pid,command --cols=512000";

  private AgentEnvironment agentEnvironment = null;


  public LinuxProcessParser(final AgentEnvironment agentEnvironment) {
    this.agentEnvironment = agentEnvironment;
  }


  public InputStream getProcesses() throws BuildException {
    return ProcessUtils.execute(agentEnvironment, LIST_PROCESSES);
  }


  public InputStream getProcessesEnvironment() throws BuildException {
    return ProcessUtils.execute(agentEnvironment, LIST_PROCESSES_ENV);
  }


  public InputStream killProcesses(final List processes) throws BuildException {
    final StringBuilder buf = new StringBuilder(100);
    for (int i = 0, n = processes.size(); i < n; i++) {
      buf.append(processes.get(i)).append(' ');
    }
    return ProcessUtils.execute(agentEnvironment, KILL + buf);
  }


  public void parse(final InputStream is, final List ret, final Map processes, final Map tree) throws BuildException {

    final BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
    
    // fill process list and mapping PID->OSProcess
    try {
      String line;
      while ((line = rdr.readLine()) != null) {
        if (StringUtils.isBlank(line)) {
          continue;
        }
        line = line.trim();
        final String copy = line;
        int pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + line);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        String value = line.substring(0, pos);
        final int pid = ProcessUtils.getPID(value);
        line = line.substring(pos + 1).trim();
        pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + copy);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        value = line.substring(0, pos);
        final int ppid = ProcessUtils.getPID(value);
        line = line.substring(pos + 1).trim();
        pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + copy);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        final String user = line.substring(0, pos);
        final String command = line.substring(pos + 1).trim();

        final String[] data = new String[3];

        if (command.equals(LIST_PROCESSES)) {
          continue;
        }

        ProcessUtils.parseCommand(command, data);
        final OSProcess p = new OSProcess(pid, ppid, data[0], data[1], data[2], user);
        if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Retrieved:" + p.toString());
        ret.add(p);

        final Integer i_pid = new Integer(pid);
        final Integer i_ppid = new Integer(ppid);

        processes.put(i_pid, p);

        List l = (List)tree.get(i_ppid);
        if (l == null) {
          l = new ArrayList();
          tree.put(i_ppid, l);
        }
        l.add(i_pid);

      }
    } catch (final IOException ex) {
      final BuildException buildException = new BuildException("I/O error while reading data", ex);
      buildException.setHostName(agentEnvironment.getHost());
      throw buildException;
    }
  }


  public void parseEnvironment(final InputStream is, final Map ret) throws BuildException {
    final BufferedReader rdr = new BufferedReader(new InputStreamReader(is));

    try {
      String line;
      while ((line = rdr.readLine()) != null) {
        if (StringUtils.isBlank(line)) {
          continue;
        }
        line = line.trim();
        final int pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + line);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        final String value = line.substring(0, pos);
        final int pid = ProcessUtils.getPID(value);
        line = line.substring(pos + 1).trim();

        ret.put(new Integer(pid), line);
      }
    } catch (final IOException ex) {
      final BuildException buildException = new BuildException("I/O error while reading data:", ex);
      buildException.setHostName(agentEnvironment.getHost());
      throw buildException;
    }
  }


  public String toString() {
    return "LinuxProcessParser{" +
      "agentEnvironment=" + agentEnvironment +
      '}';
  }
}
