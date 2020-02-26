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

import java.util.*;

import org.parabuild.ci.util.*;


/**
 * ProcessManager represents platform-independent process controller
 * that allows to view, search and kill processes on target computer
 * uniformly.
 */
public interface ProcessManager {

  // Sort order constants
  /**
   * Sort process list by their PID
   * @see #getProcesses(byte)
   * @see #getProcesses(byte,String[],boolean)
   */
  byte SORT_BY_PID = 0;

  /**
   * Sort process list by their PPID
   * @see #getProcesses(byte)
   * @see #getProcesses(byte,String[],boolean)
   */
  byte SORT_BY_PPID = 1;

  /**
   * Sort process list by their names
   * @see #getProcesses(byte)
   * @see #getProcesses(byte,String[],boolean)
   */
  byte SORT_BY_NAME = 2;


  /**
   * Returns list of running processes sorted by specified order.
   * @param sortOrder desired sort order
   * @see #SORT_BY_PID
   * @see #SORT_BY_PPID
   * @see #SORT_BY_NAME
   * @return list of <code>OSProcess</code> objects
   */
  List getProcesses(byte sortOrder) throws BuildException;


  /**
   * Returns list of running processes that matches specified patterns
   * sorted by specified order.
   * Patterns are grouped using logical OR operator.
   * Search method: <code>String.indexOf(pattern).
   * Search locations: Process name, process arguments and process
   * environment if <code>setDeep(true)</code> is set.
   * @param sortOrder desired sort order
   * @param searchPatterns list of patterns to search for
   * @param returnChildren if set to true then result list will
   * contain also list of OSProcesses that have found processes
   * as parents.
   * @see #SORT_BY_PID
   * @see #SORT_BY_PPID
   * @see #SORT_BY_NAME
   * @see #setDeep(boolean)
   * @return list of <code>OSProcess</code> objects
   */
  List getProcesses(byte sortOrder,
    String[] searchPatterns,
    boolean returnChildren)
    throws BuildException;


  /**
   * Kills process specified by it's PID
   * and all it's childrens.
   * @param processId process ID to kill
   * @return processes that are left unkilled after second try
   */
  List killProcess(int processId) throws BuildException;


  /**
   * Kills given list of processes specified by their PIDs
   * and all their's childrens.
   * @param processes list of <code>Integer</code> processes ID to kill
   * @return list of <code>Integer</code> where each element is process PID
   * that still left not killed after second try
   */
  List killProcesses(List processes) throws BuildException;


  /**
   * If deep flag is set to true then <code>getProcesses(int, java.lang.String[])</code>
   * will search for specified patterns in processes environment too.
   * By default this flag is not set.
   * @see #getProcesses(byte,String[],boolean)
   */
  void setDeep(boolean deep);
}
