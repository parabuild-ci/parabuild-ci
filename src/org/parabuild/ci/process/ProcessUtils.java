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
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.remote.*;

/**
 * ProcessManager utilities.
 */
public final class ProcessUtils {

  public static final boolean PROCESS_DEBUG_ENABLED = StringUtils.systemPropertyEquals(SystemConstants.SYSTEM_PROPERTY_PROCESS_DEBUG_ENABLED, "true");

  private static final Log log = LogFactory.getLog(ProcessUtils.class);


  /**
   * Returns true if given string contains at least one of
   * supplied patterns
   *
   * @param what string where search will be done
   * @param patterns list of param to search for
   *
   * @return true if at least on pattern is a substring of given
   *         string or there is no patterns supplied
   */
  public static boolean matches(final String what, final String[] patterns) {
    ArgumentValidator.validateArgumentNotNull(what, "what");

    // return false if 
    if (StringUtils.isBlank(what)) return false;

    // Nothing to compare
    if (patterns == null || patterns.length == 0) return true;
    for (int i = 0; i < patterns.length; i++) {
      ArgumentValidator.validateArgumentNotBlank(patterns[i], "pattern");
      if (what.indexOf(patterns[i]) >= 0) return true;
    }
    return false;
  }


  /**
   * Converts given string to PID (integer)
   *
   * @return PID as int or -1 if number format is incorrect
   */
  public static int getPID(final String value) {
    try {
      return Integer.parseInt(value);
    } catch (final NumberFormatException ex) {
      return -1;
    }
  }


  /**
   * Executes given command and returns stdout stream
   *
   * @param cmd command to execute
   *
   * @return commands results stdout as <code>InputStream</code>
   */
  public static InputStream execute(final AgentEnvironment agentEnironment, final String cmd) throws BuildException {
    return execute(agentEnironment, cmd, null);
  }


  /**
   * Executes given command with specified environment and
   * returns stdout stream
   *
   * @param cmd command to execute
   * @param env environment
   *
   * @return commands results stdout as <code>InputStream</code>
   */
  public static InputStream execute(final AgentEnvironment agentEnironment, final String cmd, final Map env) throws BuildException {
    if (PROCESS_DEBUG_ENABLED) log.debug("RemoteCommand:" + cmd);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      agentEnironment.execute(null, cmd, env, baos, new NullOutputStream());
    } catch (final RuntimeException ex) {
      throw ex;
    } catch (final Exception ex) {
      final BuildException buildException = new BuildException("Failed to launch: " + cmd + ": " + ex.getMessage());
      buildException.setHostName(agentEnironment.getHost());
      throw buildException;
    } finally {
      IoUtils.closeHard(baos);
    }
    return new ByteArrayInputStream(baos.toByteArray());
  }


  /**
   * Collects all childrens PIDs of specified process to given
   * list.
   *
   * @param pid PID of main process
   * @param tree - mapping PPID->list of PIDS (List object)
   * @param ret storage to keep results List order is: First PIDs
   * without childrens, then their parent and so on. Last item is
   * main PID
   */
  public static void getChildren(final Integer pid, final Map tree, final List ret) {
    if (ret.contains(pid)) {
      return;
    }
    final List children = (List)tree.get(pid);
    if (children != null) {
      for (int i = 0, n = children.size(); i < n; i++) {
        final Integer cId = (Integer)children.get(i);
        getChildren(cId, tree, ret);
      }
    }
    ret.add(pid);
  }


  /**
   * Adds specified OSProcess object to given set If
   * returnChildren is set then all childrens of given process
   * will be added to result set
   *
   * @param ret result set to fill
   * @param p OSProcess object to process
   * @param tree utility mapping PPID->list of PIDs
   * @param processes utility mapping PID->OSProcess
   * @param returnChildren process all childrens or not
   */
  public static void collectChildren(final Set ret,
    final OSProcess p,
    final Map tree,
    final Map processes,
    final boolean returnChildren) {
    ret.add(p);
    if (returnChildren && tree != null) {
      // Locate list of child processes PIDs in tree PPID->list of PIDs
      final List children = (List)tree.get(new Integer(p.getPID()));
      if (children == null) {
        return;
      }
      for (int i = 0, n = children.size(); i < n; i++) {
        final OSProcess child = (OSProcess)processes.get(children.get(i));
        // run the same operation for each children found
        if (child != null) {
          collectChildren(ret, child, tree, processes, true);
        }
      }
    }
  }


  /**
   * Parses given full command line. result[0] will contain
   * command line result[1] will contain command path result[2]
   * will contains full command line e.g. for command line
   * /bin/ps -aux result array should look like: ps, /bin,
   * /bin/ps -aux
   *
   * @param command command line to parse
   * @param result result array to fill with data
   */
  public static void parseCommand(final String command, final String[] result) {
    int pos = command.indexOf(' ');
    String cmd;
    result[2] = command;

    if (pos < 0) {
      cmd = command;
    } else {
      cmd = command.substring(0, pos);
    }

    cmd = cmd.trim();
    pos = cmd.lastIndexOf('/');
    if (pos < 0) {
      result[0] = cmd;
      result[1] = "";
    } else {
      result[0] = cmd.substring(pos + 1);
      result[1] = cmd.substring(0, pos);
    }
  }


  public static List processListToPIDList(final List processes) {
    final List pids = new ArrayList(100);
    for (final Iterator iter = processes.iterator(); iter.hasNext();) {
      final OSProcess process = (OSProcess)iter.next();
      pids.add(new Integer(process.getPID()));
    }
    return pids;
  }
}
