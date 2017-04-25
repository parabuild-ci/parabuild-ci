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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.ExceptionUtils;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.remote.AgentEnvironment;


/**
 * Implementation of ProcessManager interface for *nix
 * platforms.
 */
public final class UnixProcessManager implements ProcessManager {

  private static final Log log = LogFactory.getLog(UnixProcessManager.class);

  private boolean deep = false;
  private final ProcessParser parser;


  /**
   * Constructor
   *
   * @param agentEnvironment
   */
  UnixProcessManager(final AgentEnvironment agentEnvironment) throws IOException {
    try {
      if (agentEnvironment.systemType() == AgentEnvironment.SYSTEM_TYPE_SUNOS) {
        parser = new SolarisProcessParser(agentEnvironment);
      } else {
        parser = new LinuxProcessParser(agentEnvironment);
      }
    } catch (AgentFailureException e) {
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
   *
   * @see ProcessManager#getProcesses(byte,String[],boolean)
   */
  public List getProcesses(final byte sortOrder, final String[] searchPatterns, final boolean returnChildren) throws BuildException {
    // list of OSProcess object      
    final List ret = new ArrayList();
    // mapping PPID->list of children PID
    final Map tree = new HashMap();
    // mapping PID->OSProcess
    final Map processes = new HashMap();
    // set of found processes
    final Set found = new HashSet();

    InputStream is = parser.getProcesses();
    // Collect processes list and tree
    try {
      parser.parse(is, ret, processes, tree);
    } finally {
      IoUtils.closeHard(is);
    }

    // mapping PID->OSProcess
    final Map processes_env = new HashMap();

    // If we need to perform deep scan list processes
    // with environment
    if (deep) {
      is = parser.getProcessesEnvironment();
      // List of OSProcess objects with environment
      try {
        parser.parseEnvironment(is, processes_env);
      } finally {
        IoUtils.closeHard(is);
      }
    }

    // list is ready, let's extract correct data
    // and childrens if needed
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
          final String proc_env = (String) processes_env.get(new Integer(pid));
          if (proc_env != null) {
            matches = ProcessUtils.matches(proc_env, searchPatterns);
          }
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
    // list of OSProcess object      
    final List ret = new ArrayList();
    // mapping PPID->list of children PID
    final Map tree = new HashMap();
    // mapping PID->OSProcess
    final Map processes = new HashMap();

    // Collect processes list and tree
    final InputStream is = parser.getProcesses();
    try {
      parser.parse(is, ret, processes, tree);
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
    // list of OSProcess object      
    final List ret = new ArrayList();
    // mapping PPID->list of children PID
    final Map tree = new HashMap();
    // mapping PID->OSProcess
    final Map procList = new HashMap();

    final InputStream is = parser.getProcesses();
    // Collect processes list and tree
    try {
      parser.parse(is, ret, procList, tree);
    } finally {
      IoUtils.closeHard(is);
    }
    // collect ALL children processes
    final List children = new ArrayList();
    for (int i = 0, n = processes.size(); i < n; i++) {
      ProcessUtils.getChildren((Integer) processes.get(i), tree, children);
    }

    return kill(children);
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
   * Kills given list of PID. This method calls 'kill' command.
   * Behavior: 1) Kill specified PIDS 2) Check if there is no
   * more processes with specified PIDs 3) Kill again if any
   * process left 4) Check again if there is no more processes
   * with specified PIDs 5) Return list of processes left
   */
  private List kill(final List children) throws BuildException {
    // let's remove given list
    InputStream is = null;
    try {
      is = parser.killProcesses(children);
    } finally {
      IoUtils.closeHard(is);
    }
    // re-retrieve list of children
    final List ret = new ArrayList();
    // mapping PPID->list of children PID
    final Map tree = new HashMap();
    // mapping PID->OSProcess
    final Map procList = new HashMap();

    is = parser.getProcesses();
    try {
      // Collect processes list and tree
      parser.parse(is, ret, procList, tree);
    } finally {
      IoUtils.closeHard(is);
    }

    // Looking for processes left
    int i = 0;
    while (i < children.size()) {
      final Integer id = (Integer) children.get(i);
      if (procList.containsKey(id)) {
        i++;
      } else {
        children.remove(i);
      }
    }

    if (children.isEmpty()) {
      return new ArrayList();
    }

    try {
      is = parser.killProcesses(children);
    } finally {
      IoUtils.closeHard(is);
    }

    // re-retrieve list of children
    tree.clear();
    procList.clear();
    ret.clear();

    is = parser.getProcesses();
    try {
      // Collect processes list and tree
      parser.parse(is, ret, procList, tree);
    } finally {
      IoUtils.closeHard(is);
    }

    // Looking for processes left
    ret.clear();
    final StringBuffer buf = new StringBuffer(100);
    final int n = children.size();
    for (i = 0; i < n; i++) {
      final Integer id = (Integer) children.get(i);
      if (procList.containsKey(id)) {
        ret.add(id);
        buf.append(id).append(' ');
      }
    }
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Left after second try: " + buf);
    return ret;
  }


  public String toString() {
    return "UnixProcessManager{" +
            "deep=" + deep +
            ", parser=" + parser +
            '}';
  }
}
