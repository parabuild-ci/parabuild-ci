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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.ExceptionUtils;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.AgentEnvironment;

/**
 * Concrete (Windows) implementation of ProcessManager interface.
 * Uses 'pv.exe' command to manage processes
 */
final class WindowsProcessManager implements ProcessManager {

  private static final Log log = LogFactory.getLog(WindowsProcessManager.class);

  /**
   * Win32-specific executable
   */
  private String PV = null;

  /**
   * RemoteCommand used to query list of processes (-q: quiet, no
   * headers, tabs are separators; -e: show hidden processes; -l:
   * display command lines
   */
  private String LIST_PROCESSES = null;

  /**
   * RemoteCommand used to query list of processes tree (PID and
   * PPID) (-q: quiet, no headers, tabs are separators; -e: show
   * hidden processes; -t: display PPID
   */
  private String LIST_PPIDS = null;

  /**
   * RemoteCommand used to query list of processes PID (-q:
   * quiet, no headers, tabs are separators; -e: show hidden
   * processes;
   */
  private String LIST_PIDS = null;

  /**
   * RemoteCommand used to query environment of processes (-q:
   * quiet, no headers, tabs are separators; -e: show hidden
   * processes; -t: display PPID -i: use PID
   */
  private String LIST_ENV = null;

  /**
   * RemoteCommand used to kill process (-q: quiet, no headers,
   * tabs are separators; -e: show hidden processes; -f: force
   * -k: kill -i: use PID
   */
  private String KILL = null;

  private boolean deep = false;
  private AgentEnvironment agentEnvironment = null;


  /**
   * Constructor
   *
   * @param agentEnvironment
   */
  WindowsProcessManager(final AgentEnvironment agentEnvironment) throws IOException {
    this.agentEnvironment = agentEnvironment;
    final String separator;
    try {
      separator = agentEnvironment.separator();
      this.PV = agentEnvironment.getSystemProperty("catalina.home") + separator + ".." + separator + "bin" + separator + "win" + separator + "p6.exe";
      this.LIST_PROCESSES = PV + " -q -e -l";
      this.LIST_PPIDS = PV + " -q -e -t";
      this.LIST_PIDS = PV + " -q -e";
      this.LIST_ENV = PV + " -q -e -g -i ";
      this.KILL = PV + " -q -e -f -k -i ";
    } catch (final AgentFailureException e) {
      throw ExceptionUtils.createIOException(e);
    }
  }


  /**
   * Implementation method
   *
   * @see ProcessManager#getProcesses(byte)
   */
  public List getProcesses(final byte sortOrder) throws BuildException {
    return getProcesses(sortOrder, null, false);
  }


  /**
   * Implementation method
   */
  public List getProcesses(final byte sortOrder,
                           final String[] searchPatterns,
                           final boolean returnChildren)
          throws BuildException {
    // Stage 0: get list of processes
    InputStream is = ProcessUtils.execute(agentEnvironment, LIST_PROCESSES);

    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Retrieving list");
    final ArrayList ret = new ArrayList();
    try {
      getProcesses(is, ret);
    } finally {
      IoUtils.closeHard(is);
    }

    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Retrieving tree");
    // Stage 1: get processes tree PID->PPID
    is = ProcessUtils.execute(agentEnvironment, LIST_PPIDS);

    // Mapping PID->PPID
    Map pids = null;
    // Mapping PPID->list of PIDs
    Map tree = null;
    // Result set
    final Set found = new HashSet();
    // Mappin PID->OSProcess
    final Map processes = new HashMap();

    try {
      pids = getPPIDs(is);
      if (returnChildren) {
        is.reset();
        tree = getTree(is);
      }
    } catch (final IOException ex) {
      throw new BuildException("Unable to parse data: I/O error", ex, agentEnvironment);
    } finally {
      IoUtils.closeHard(is);
    }

    // Assign process PPID and fill PID->OSProcess mapping
    for (int i = 0, n = ret.size(); i < n; i++) {
      final OSProcess p = (OSProcess) ret.get(i);
      final int pid = p.getPID();
      final Integer iPid = new Integer(pid);
      final Integer ppid = (Integer) pids.get(iPid);
      if (ppid != null) {
        p.setPPID(ppid.intValue());
      }

      processes.put(iPid, p);
    }

    // Search in process name, path and command line
    // and in environment if needed
    for (int i = 0, n = ret.size(); i < n; i++) {
      boolean matches = false;

      final OSProcess p = (OSProcess) ret.get(i);
      final int pid = p.getPID();

      if (searchPatterns != null) {
        if (ProcessUtils.matches(p.getName(), searchPatterns) ||
                ProcessUtils.matches(p.getPath(), searchPatterns) ||
                ProcessUtils.matches(p.getCommandLine(), searchPatterns)) {
          matches = true;
        }
        // deep scan in environment
        if (!matches && deep) {
          final String env = getEnvironment(pid);
          matches = ProcessUtils.matches(env, searchPatterns);
        }
      } else {
        matches = true;
      }

      // Ok, given process matches specified criterias
      // or no criterias are specified
      // let's add this process to 'found' set
      if (matches) {
        ProcessUtils.collectChildren(found, p, tree, processes, returnChildren);
      }
    }

    // Ok, now we have all data collected in set
    // Let's tranform it into list
    ret.clear();
    ret.addAll(found);
    // Let's sort this data
    Collections.sort(ret, new ProcessComparator(sortOrder));
    return ret;
  }


  /**
   * Implementation method
   *
   * @see ProcessManager#killProcess(int)
   */
  public List killProcess(final int pid) throws BuildException {
    // stage 0: retrieve list of processes and build a mapping
    // like: process->list of childrens
    final InputStream is = ProcessUtils.execute(agentEnvironment, LIST_PPIDS);

    Map tree = null;

    try {
      tree = getTree(is);
    } finally {
      IoUtils.closeHard(is);
    }

    // collect all children processes
    final List childrens = new ArrayList();
    ProcessUtils.getChildren(new Integer(pid), tree, childrens);

    return kill(childrens);
  }


  /**
   * Implementation method
   *
   * @see ProcessManager#killProcesses(List)
   */
  public List killProcesses(final List processes) throws BuildException {
    // Collect all processes that should be killed
    // stage 0: retrieve list of processes
    final InputStream is = ProcessUtils.execute(agentEnvironment, LIST_PPIDS);
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Retrieving list");

    Map tree = null;

    try {
      tree = getTree(is);
    } finally {
      IoUtils.closeHard(is);
    }

    // collect ALL children processes
    final List childrens = new ArrayList();
    for (int i = 0, n = processes.size(); i < n; i++) {
      ProcessUtils.getChildren((Integer) processes.get(i), tree, childrens);
    }

    return kill(childrens);
  }


  /**
   * Implementation method
   *
   * @see ProcessManager#setDeep(boolean)
   */
  public void setDeep(final boolean deep) {
    this.deep = deep;
  }


  /**
   * Retrieves list of processes in format: name PID priority
   * path command Fills process object with given data and
   * appends OSProcess object to given list
   *
   * @param in  InputStream to parse data from
   * @param ret List to store collected data
   * @throws BuildException in case of
   *                        I/O error or if data supplied by PV is in unknown format
   */
  private void getProcesses(final InputStream in, final List ret) throws BuildException {
    // TODO: What about charsets? E.g. if command name is russian or chinese?
    final String ownCmd = normalizePath(PV);
    final BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
    String line;
    try {
      while ((line = rdr.readLine()) != null) {
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (StringUtils.isBlank(line)) {
          continue;
        }
        if (st.countTokens() != 5) {
          throw new BuildException("Process list is returned in unknown format: " + line, agentEnvironment);
        }
        final String name = st.nextToken();
        final String pid = st.nextToken();
        st.nextToken();
        String path = st.nextToken();
        final String nPath = normalizePath(path);
        if (ownCmd.equals(nPath) ||
                ownCmd.equals('\"' + nPath + '\"')) {
          continue;
        }
        final String command = st.nextToken();
        if (path.endsWith(name)) {
          path = path.substring(0, path.length() - name.length() - 1);
        }
        final OSProcess p = new OSProcess(ProcessUtils.getPID(pid), -1, name, path, command, null);
        if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Retrieved:" + p.toString());
        ret.add(p);
      }
    } catch (final IOException ex) {
      throw new BuildException("I/O error while reading data", ex, agentEnvironment);
    }
  }


  /**
   * Retrieves list of processes in format: name PID PPID Fills
   * mapping PID->PPID
   *
   * @param in InputStream to parse data from
   * @return map that stores collected data
   * @throws BuildException in case of
   *                        I/O error or if data supplied by PV is in unknown format
   */
  private Map getPPIDs(final InputStream in) throws BuildException {
    final Map ret = new HashMap();
    // TODO: What about charsets? E.g. if command name is russian or chinese?
    final BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
    String line;
    try {
      while ((line = rdr.readLine()) != null) {
        if (StringUtils.isBlank(line)) {
          continue;
        }
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (st.countTokens() != 3) {
          throw new BuildException("Tree is returned in unknown format: " + line, agentEnvironment);
        }
        final String s_pid = st.nextToken();
        final String s_ppid = st.nextToken();
        final Integer pid = new Integer(ProcessUtils.getPID(s_pid));
        final Integer ppid = new Integer(ProcessUtils.getPID(s_ppid));
        ret.put(pid, ppid);
        if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Tree retrieved:" + ppid + '/' + pid);
      }
    } catch (final IOException ex) {
      throw new BuildException("I/O error while reading data", ex, agentEnvironment);
    }
    return ret;
  }


  /**
   * Retrieves list of processes in format: name PID PPID Fills
   * mapping PPID->list of PID
   *
   * @param in InputStream to parse data from
   * @return map that contains collected data.
   * @throws BuildException in case of
   *                        I/O error or if data supplied by PV is in unknown format
   */
  private Map getTree(final InputStream in) throws BuildException {
    final Map ret = new HashMap();
    // TODO: What about charsets? E.g. if command name is russian or chinese?
    final BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
    String line;
    try {
      while ((line = rdr.readLine()) != null) {
        if (StringUtils.isBlank(line)) {
          continue;
        }
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (st.countTokens() != 3) {
          throw new BuildException("Tree is returned in unknown format: " + line, agentEnvironment);
        }
        final String s_pid = st.nextToken();
        final String s_ppid = st.nextToken();
        final Integer pid = new Integer(ProcessUtils.getPID(s_pid));
        final Integer ppid = new Integer(ProcessUtils.getPID(s_ppid));
        // retrieve list of alread known childrens
        List l = (List) ret.get(ppid);
        if (l == null) {
          l = new ArrayList();
          ret.put(ppid, l);
        }
        l.add(pid);
      }
    } catch (final IOException ex) {
      throw new BuildException("I/O error while reading data", ex, agentEnvironment);
    }
    return ret;
  }


  /**
   * Retrieves process environment for given process ID
   *
   * @param pid process ID to retrieve data from
   * @return process environment
   * @throws BuildException in case of
   *                        I/O error or if data supplied by PV is in unknown format
   */
  private String getEnvironment(final int pid) throws BuildException {
    // TODO: What about charsets? E.g. if command name is russian or chinese?
    final InputStream is = ProcessUtils.execute(agentEnvironment, LIST_ENV + pid);
    final StringBuffer buf = new StringBuffer(100);
    final Reader r = new InputStreamReader(is);
    try {
      final char[] c = new char[1024];
      int count = 0;
      while ((count = r.read(c)) > 0) {
        buf.append(c, 0, count).append('\n');
      }
    } catch (final IOException ex) {
      throw new BuildException("Error getting environment", ex, agentEnvironment);
    } finally {
      IoUtils.closeHard(r);
    }
    return buf.toString();
  }


  /**
   * Kills given list of PID. This method calls PV command.
   * Behavior: 1) Kill specified PIDS 2) Check if there is no
   * more processes with specified PIDs 3) Kill again if any
   * process left 4) Check again if there is no more processes
   * with specified PIDs 5) Return list of processes left
   */
  private List kill(final List children) throws BuildException {
    // let's remove given list
    final StringBuffer buf = new StringBuffer(100);
    for (int i = 0, n = children.size(); i < n; i++) {
      buf.append(children.get(i)).append(' ');
    }
    // call system command
    ProcessUtils.execute(agentEnvironment, KILL + buf);
    // re-retrieve list of children
    InputStream is = ProcessUtils.execute(agentEnvironment, LIST_PIDS);
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Re-retrieving list");

    Set pids = null;

    try {
      pids = getPIDs(is);
    } finally {
      IoUtils.closeHard(is);
    }

    // Looking for processes left
    buf.setLength(0);
    for (int i = 0, n = children.size(); i < n; i++) {
      final Integer id = (Integer) children.get(i);
      if (pids.contains(id)) {
        buf.append(id).append(' ');
      }
    }
    if (buf.length() == 0) {
      return new ArrayList();
    }
    // call system command
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Left: " + buf);
    ProcessUtils.execute(agentEnvironment, KILL + buf);
    // re-retrieve list of children
    is = ProcessUtils.execute(agentEnvironment, LIST_PIDS);
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Re-retrieving list");

    try {
      pids = getPIDs(is);
    } finally {
      IoUtils.closeHard(is);
    }

    // Looking for processes left
    final List ret = new ArrayList();
    buf.setLength(0);
    for (int i = 0, n = children.size(); i < n; i++) {
      final Integer id = (Integer) children.get(i);
      if (pids.contains(id)) {
        ret.add(id);
        buf.append(id).append(' ');
      }
    }
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Left after second try: " + buf);
    return ret;
  }


  /**
   * Retrieves list of processes in format: name PID PPID Fills
   * specified set with running PIDs
   *
   * @param in InputStream to parse data from
   * @return set that stores collected data
   * @throws BuildException in case of
   *                        I/O error or if data supplied by PV is in unknown format
   */
  private Set getPIDs(final InputStream in) throws BuildException {
    final Set ret = new HashSet();
    final BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
    String line;
    try {
      while ((line = rdr.readLine()) != null) {
        if (StringUtils.isBlank(line)) {
          continue;
        }
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (st.countTokens() != 4) {
          throw new BuildException("Tree is returned in unknown format: " + line, agentEnvironment);
        }
        st.nextToken();
        final String s_pid = st.nextToken();
        final Integer pid = new Integer(ProcessUtils.getPID(s_pid));
        ret.add(pid);
      }
    } catch (final IOException ex) {
      throw new BuildException("I/O error while reading data", ex, agentEnvironment);
    }
    return ret;
  }


  private static String normalizePath(final String path) {
    return path.replace('\\', '/');
  }


  public String toString() {
    return "WindowsProcessManager{" +
            "PV='" + PV + '\'' +
            ", LIST_PROCESSES='" + LIST_PROCESSES + '\'' +
            ", LIST_PPIDS='" + LIST_PPIDS + '\'' +
            ", LIST_PIDS='" + LIST_PIDS + '\'' +
            ", LIST_ENV='" + LIST_ENV + '\'' +
            ", KILL='" + KILL + '\'' +
            ", deep=" + deep +
            ", agentEnvironment=" + agentEnvironment +
            '}';
  }
}
