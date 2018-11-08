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

import java.io.*;
import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.remote.*;


/**
 * SolarisProcessParser is Solaris implementation of
 * ProcessParser interface
 */
public final class SolarisProcessParser implements ProcessParser {

  private static final Log log = LogFactory.getLog(SolarisProcessParser.class);

  private static final String CMD_PS = "/usr/ucb/ps";

  /**
   * Kills specified processes
   */
  private static final String KILL = "/bin/kill -9 ";

  /**
   * RemoteCommand used to list processes -e - all running
   * processes -o - display PID PPID USER COMMAND cols - number
   * of columns on screen (fake, to have full lines)
   */
  private static final String LIST_PROCESSES = CMD_PS + " -e -o user,pid,ppid,args";

  /**
   * RemoteCommand used to retrieve processed environment -e -
   * all running processes -o - display PID PPID USER COMMAND e -
   * include system environment h - not include headers cols -
   * number of columns on screen (fake, to have full lines)
   */
  private static final String LIST_PROCESSES_ENV = CMD_PS + " -ae";

  private static final String COLUMNS = "COLUMNS";
  private static final String LARGE_COLUMNS = "128000";

  private static final Map ENV;
  private final AgentEnvironment agentEnvironment;


  public SolarisProcessParser(final AgentEnvironment agentEnvironment) {
    this.agentEnvironment = agentEnvironment;
  }


  static {
    ENV = new HashMap();
    ENV.put(COLUMNS, LARGE_COLUMNS);
  }


  public InputStream getProcesses() throws BuildException {
    return ProcessUtils.execute(agentEnvironment, LIST_PROCESSES, ENV);
  }


  public InputStream getProcessesEnvironment() throws BuildException {
    return ProcessUtils.execute(agentEnvironment, LIST_PROCESSES_ENV, ENV);
  }


  public InputStream killProcesses(final List processes) throws BuildException {

    final StringBuilder buf = new StringBuilder(100);
    for (int i = 0, n = processes.size(); i < n; i++) {
      buf.append(processes.get(i)).append(' ');
    }

    return ProcessUtils.execute(agentEnvironment, KILL + buf);
  }


  public void parse(final InputStream is,
    final List ret,
    final Map processes,
    final Map tree) throws BuildException {
    final BufferedReader rdr = new BufferedReader(new InputStreamReader(is));

    // fill process list and mapping PID->OSProcess
    boolean first_line = true;
    String line;
    try {
      while ((line = rdr.readLine()) != null) {

        // skip headers line
        if (first_line) {
          first_line = false;
          continue;
        }

        if (StringUtils.isBlank(line)) {
          continue;
        }

        line = line.trim();
        final String copy = line;
        // extract user
        int pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + line);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        final String user = line.substring(0, pos).trim();
        line = line.substring(pos + 1).trim();
        // extract PID
        pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + copy);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        String value = line.substring(0, pos);
        final int pid = ProcessUtils.getPID(value);
        line = line.substring(pos + 1).trim();
        // extract PPID
        pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + copy);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        value = line.substring(0, pos);
        final int ppid = ProcessUtils.getPID(value);
        line = line.substring(pos + 1).trim();
        final String command = line.trim();

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

    // fill process list and mapping PID->OSProcess
    String line;
    boolean first_line = true;
    try {
      while ((line = rdr.readLine()) != null) {

        // skip headers line
        if (first_line) {
          first_line = false;
          continue;
        }

        if (StringUtils.isBlank(line)) {
          continue;
        }
        line = line.trim();
        final String copy = line;
        // extract PID
        int pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + line);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        final String value = line.substring(0, pos);
        final int pid = ProcessUtils.getPID(value);
        line = line.substring(pos + 1).trim();
        // skip TTY
        pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + copy);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        line = line.substring(pos + 1).trim();
        // skip priority
        pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + copy);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        line = line.substring(pos + 1).trim();
        // skip TIME
        pos = line.indexOf(' ');
        if (pos < 0) {
          final BuildException buildException = new BuildException("Unrecognized line data:" + copy);
          buildException.setHostName(agentEnvironment.getHost());
          throw buildException;
        }
        line = line.substring(pos + 1).trim();
        // left command and environment
        final String command = line.trim();

        ret.put(new Integer(pid), command);
      }
    } catch (final IOException ex) {
      final BuildException buildException = new BuildException("I/O error while reading data", ex);
      buildException.setHostName(agentEnvironment.getHost());
      throw buildException;
    }
  }


  public String toString() {
    return "SolarisProcessParser{" +
      "agentEnvironment=" + agentEnvironment +
      '}';
  }
}
