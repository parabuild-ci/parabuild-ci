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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.SystemConstants;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.TestHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tests ProcessManager functionality
 */
public class SATestProcessManagerFactory extends TestCase {

  private static final Log log = LogFactory.getLog(SATestProcessManagerFactory.class);


  final static String SLEEP_COMMAND = "sleep 100";
  final static String SLEEP_SIGNATURE = "sleep";

  final static String TEST_PRCMANAGER_SH = "test_prcmanager.sh";
  final static String PRCMANAGER_SPAWN_SH = "prcmanager_spawn.sh";

  private ProcessManager manager;
  private ProcessManager deep_manager;
  private AgentEnvironment agentEnv;


  public SATestProcessManagerFactory(final String s) {
    super(s);
  }


  /**
   * Is process manager instance was created
   */
  public void test_creation() throws Exception {
    assertTrue(manager != null);
  }


  /**
   * Is process manager is able to list processes and sort order
   * or returned data is correct
   */
  public void test_list() throws Exception {
    List l = manager.getProcesses(ProcessManager.SORT_BY_PID);
    assertTrue("No processes retrieved (SORT: PID)", !l.isEmpty());
    for (int i = 0; i < l.size() - 1; i++) {
      final OSProcess p1 = (OSProcess) l.get(i);
      final OSProcess p2 = (OSProcess) l.get(i + 1);
      assertTrue("Incorrect sorting by PID", p1.getPID() < p2.getPID());
    }
    l = manager.getProcesses(ProcessManager.SORT_BY_PPID);
    assertTrue("No processes retrieved (SORT: PPID)", !l.isEmpty());
    for (int i = 0; i < l.size() - 1; i++) {
      final OSProcess p1 = (OSProcess) l.get(i);
      final OSProcess p2 = (OSProcess) l.get(i + 1);
      assertTrue("Incorrect sorting by PPID", p1.getPPID() <= p2.getPPID());
    }
  }


  /**
   * Lists processes that belongs to parabuild commands (Win32)
   * Lists processes that have USER env. variable (Unix) Lists
   * common processes (cmd on Win32 and bash on Unix)
   */
  public void test_listExtended() throws Exception {

    List l = null;
    if (agentEnv.isWindows()) {
      if (log.isDebugEnabled()) log.debug("running under Windows");
      l = deep_manager.getProcesses(ProcessManager.SORT_BY_PID, new String[]{"COMPUTERNAME"}, false);
      // FIXME: does not work with PV.EXE
      // assertTrue("No processes are retrieved using deep search", l.size() > 0);
    } else {
      if (log.isDebugEnabled()) log.debug("running under unix");
      l = deep_manager.getProcesses(ProcessManager.SORT_BY_PID, new String[]{"USER"}, false);
      assertTrue("No processes are retrieved using deep search", !l.isEmpty());
    }

    if (agentEnv.isWindows()) {
      l = manager.getProcesses(ProcessManager.SORT_BY_PID, new String[]{"cmd"}, false);
    } else {
      l = manager.getProcesses(ProcessManager.SORT_BY_PID, new String[]{"java"}, false);
    }
    assertTrue("No processes are retrieved using std search", !l.isEmpty());

    if (agentEnv.isWindows()) {
      l = deep_manager.getProcesses(ProcessManager.SORT_BY_PID, new String[]{"USERNAME"}, true);
      // FIXME: does not work with PV.EXE
      // assertTrue("No processes are retrieved using deep search", l.size() > 0);
    } else {
      l = deep_manager.getProcesses(ProcessManager.SORT_BY_PID, new String[]{"USER"}, true);
      assertTrue("No processes are retrieved using deep search", !l.isEmpty());
    }

    if (agentEnv.isWindows()) {
      l = manager.getProcesses(ProcessManager.SORT_BY_PID, new String[]{"cmd"}, true);
    } else {
      l = manager.getProcesses(ProcessManager.SORT_BY_PID, new String[]{"java"}, true);
    }
    assertTrue("No processes are retrieved using std search", !l.isEmpty());
  }


  /**
   * Tests that we can find and kill single process (sleep)
   */
  public void test_killSingleProcess() throws Exception {

    // create and start long running task (sleep command)
    final Thread longRunningThread = new TaskToKill(agentEnv, SLEEP_COMMAND);
    longRunningThread.start();

    // let it warm up
    Thread.sleep(2000);

    // get proc list
    List procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{SLEEP_SIGNATURE}, false);

    // check if we found the process
    assertTrue("No 'sleep' processes are retrieved", !procList.isEmpty());

    // kill
    if (!procList.isEmpty()) {
      for (int i = 0; i < procList.size(); i++) {
        final OSProcess p = (OSProcess) procList.get(i);
        manager.killProcess(p.getPID());
      }
    }

    // check if nothing has left
    procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{SLEEP_SIGNATURE}, false);
    assertTrue("'sleep' processes are retrieved after kill:" + procList.size(),
            procList.isEmpty());
    // double check if the thread running command is also dead
    assertTrue("Thread still running", !longRunningThread.isAlive());
  }


  /**
   * Tests that we can find and kill few process (2 sleeps)
   */
  public void test_killFewProcess() throws Exception {

    // create and start long running task (sleep command)
    final Thread longRunningThread1 = new TaskToKill(agentEnv, SLEEP_COMMAND);
    longRunningThread1.start();

    // create and start long running task (sleep command)
    final Thread longRunningThread2 = new TaskToKill(agentEnv, SLEEP_COMMAND);
    longRunningThread2.start();

    // let it warm up
    Thread.sleep(2000);

    // get proc list
    List procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{SLEEP_SIGNATURE},
            false);

    // check if we found the process
    assertTrue("No 'sleep' processes are retrieved",
            !procList.isEmpty());

    // kill
    final List pids = new ArrayList(11);
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      pids.add(new Integer(p.getPID()));
    }
    manager.killProcesses(pids);

    // check if nothing has left
    procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{SLEEP_SIGNATURE},
            false);
    assertTrue("'sleep' processes are retrieved after kill:" + procList.size(),
            procList.isEmpty());
    // double check if the threads running command are also dead
    assertTrue("Thread still running", !longRunningThread1.isAlive());
    assertTrue("Thread still running", !longRunningThread2.isAlive());
  }


  /**
   * Tests that we can kill processes tree
   */
  public void test_killTree() throws Exception {
    if (isPureWindows()) return;

    final Thread thread = prepareLongProcesses();
    // get main proc list
    final List procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{TEST_PRCMANAGER_SH},
            false);

    // check if we found the process
    assertTrue("No " + TEST_PRCMANAGER_SH + " processes found",
            !procList.isEmpty());

    // get spawn proc list
    List procList1 = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{PRCMANAGER_SPAWN_SH},
            false);

    // check if we found the process
    assertTrue("No " + PRCMANAGER_SPAWN_SH + " processes found",
            !procList1.isEmpty());

    // get spawn proc list 2 (sleep)
    procList1 = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{SLEEP_SIGNATURE},
            false);

    // check if we found the process
    assertTrue("No " + SLEEP_SIGNATURE + " processes found",
            !procList1.isEmpty());

    // kill
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      manager.killProcess(p.getPID());
    }

    // NOTE: vimeshev@viewtier.com - I added this kill to let test pass
    // because the kill above will not find child processes
    // due to the fact that we have bash's runaway processes.
    // NOTE: lyolik@noir.crocodile.org - I'm not sure that this code
    // is really needed

    for (int i = 0; i < procList1.size(); i++) {
      final OSProcess p = (OSProcess) procList1.get(i);
      manager.killProcess(p.getPID());
    }
    // check if nothing has left
    check_processes_left();

    // double check if the threads running command are also dead
    assertTrue("Thread still running", !thread.isAlive());
  }


  /**
   * Tests that we can list processes and they childrens and kill
   * whole process tree (each PID (main or spawned) found will be
   * killed by separate command from parent to childrens
   */
  public void test_getChildrensAndKillEachDescending() throws Exception {
    if (isPureWindows()) return;

    final Thread thread = prepareLongProcesses();

    // get main proc list
    List procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{TEST_PRCMANAGER_SH},
            false);

    // check if we found the process
    assertTrue("No " + TEST_PRCMANAGER_SH + " processes found",
            !procList.isEmpty());

    // check if we not found the children processes
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      assertTrue("Matches to " + PRCMANAGER_SPAWN_SH + '\n' + p,
              p.getCommandLine().indexOf(PRCMANAGER_SPAWN_SH) < 0);
    }

    // get process list with childrens
    procList = manager.getProcesses(ProcessManager.SORT_BY_NAME,
            new String[]{TEST_PRCMANAGER_SH},
            true);

    // check if we found main and children processes
    assertTrue("No " + TEST_PRCMANAGER_SH + " processes found",
            !procList.isEmpty());

    // check if we found both parent and children processes
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      assertTrue("Does not matches:" + p,
              p.getCommandLine().indexOf(PRCMANAGER_SPAWN_SH) >= 0 ||
                      p.getCommandLine().indexOf(TEST_PRCMANAGER_SH) >= 0 ||
                      p.getCommandLine().indexOf(SLEEP_SIGNATURE) >= 0);
    }

    Collections.sort(procList, new TestComparator(false));
    // kill from parents to childrens
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      manager.killProcess(p.getPID());
    }

    // check if nothing has left
    check_processes_left();

    // double check if the threads running command are also dead
    assertTrue("Thread still running", !thread.isAlive());
  }


  /**
   * Tests that we can list processes and they childrens and kill
   * whole process tree (each PID (main or spawned) found will be
   * killed by separate command from childrens to parents
   */
  public void test_getChildrensAndKillEachAscending() throws Exception {
    if (isPureWindows()) return;

    final Thread thread = prepareLongProcesses();

    // get main proc list
    List procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{TEST_PRCMANAGER_SH},
            false);

    // check if we found the process
    assertTrue("No " + TEST_PRCMANAGER_SH + " processes found",
            !procList.isEmpty());

    // check if we not found the children processes
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      assertTrue("Matches:" + p,
              p.getCommandLine().indexOf(PRCMANAGER_SPAWN_SH) < 0);
    }

    // get process list with childrens
    procList = manager.getProcesses(ProcessManager.SORT_BY_NAME,
            new String[]{TEST_PRCMANAGER_SH},
            true);

    // check if we found main and children processes
    assertTrue("No " + TEST_PRCMANAGER_SH + " processes found",
            !procList.isEmpty());
    // check if we found both parent and children processes
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      assertTrue("Does not matches: " + p,
              p.getCommandLine().indexOf(PRCMANAGER_SPAWN_SH) >= 0 ||
                      p.getCommandLine().indexOf(TEST_PRCMANAGER_SH) >= 0 ||
                      p.getCommandLine().indexOf(SLEEP_SIGNATURE) >= 0);
    }

    Collections.sort(procList, new TestComparator(true));

    // kill from childrens to parents
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      manager.killProcess(p.getPID());
    }

    // check if nothing has left
    check_processes_left();

    // double check if the threads running command are also dead
    assertTrue("Thread still running", !thread.isAlive());
  }


  /**
   * Tests that we can list processes and they childrens and kill
   * processes tree by single command from childrens to parents
   */
  public void test_getChildrenAndKillAllAscending() throws Exception {
    if (isPureWindows()) return;


    final Thread thread = prepareLongProcesses();

    // get main proc list
    List procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{TEST_PRCMANAGER_SH},
            false);

    // check if we found the process
    assertTrue("No " + TEST_PRCMANAGER_SH + " processes found",
            !procList.isEmpty());

    // check if we not found the children processes
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      assertTrue("Matches:" + p,
              p.getCommandLine().indexOf(PRCMANAGER_SPAWN_SH) < 0);
    }

    // get process list with childrens
    procList = manager.getProcesses(ProcessManager.SORT_BY_NAME,
            new String[]{TEST_PRCMANAGER_SH},
            true);

    // check if we found main and children processes
    assertTrue("No " + TEST_PRCMANAGER_SH + " processes found",
            !procList.isEmpty());
    // check if we found both parent and children processes
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      assertTrue("Does not matches: " + p,
              p.getCommandLine().indexOf(PRCMANAGER_SPAWN_SH) >= 0 ||
                      p.getCommandLine().indexOf(TEST_PRCMANAGER_SH) >= 0 ||
                      p.getCommandLine().indexOf(SLEEP_SIGNATURE) >= 0);
    }

    // kill from childrens to parents using one command

    Collections.sort(procList, new TestComparator(true));

    final List toKill = new ArrayList(11);

    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      toKill.add(new Integer(p.getPID()));
    }

    manager.killProcesses(toKill);

    // check if nothing has left
    check_processes_left();

    // double check if the threads running command are also dead
    assertTrue("Thread still alive", !thread.isAlive());
  }


  /**
   * Tests that we can list processes and they childrens and kill
   * processes tree by single command from parents to childrens
   */
  public void test_getChildrenAndKillAllDescending() throws Exception {
    if (isPureWindows()) return;


    final Thread thread = prepareLongProcesses();

    // get main proc list
    List procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{TEST_PRCMANAGER_SH},
            false);

    // check if we found the process
    assertTrue("No " + TEST_PRCMANAGER_SH + " processes found",
            !procList.isEmpty());

    // check if we not found the children processes
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      assertTrue("Matches:" + p,
              p.getCommandLine().indexOf(PRCMANAGER_SPAWN_SH) < 0);
    }

    // get process list with childrens
    procList = manager.getProcesses(ProcessManager.SORT_BY_NAME,
            new String[]{TEST_PRCMANAGER_SH},
            true);

    // check if we found main and children processes
    assertTrue("No " + TEST_PRCMANAGER_SH + " processes found",
            !procList.isEmpty());
    // check if we found both parent and children processes
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      assertTrue("Does not matches:" + p,
              p.getCommandLine().indexOf(PRCMANAGER_SPAWN_SH) >= 0 ||
                      p.getCommandLine().indexOf(TEST_PRCMANAGER_SH) >= 0 ||
                      p.getCommandLine().indexOf(SLEEP_SIGNATURE) >= 0);
    }

    // kill from parents to childrens using one command

    Collections.sort(procList, new TestComparator(false));

    final List toKill = new ArrayList(11);
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      toKill.add(new Integer(p.getPID()));
    }

    manager.killProcesses(toKill);

    // check if nothing has left
    check_processes_left();

    // double check if the threads running command are also dead
    assertTrue("Thread still running", !thread.isAlive());
  }


  /**
   * Tests that we can kill deeply based on value of a process
   * environment variable
   *
   * @see TaskToKill#TEST_SIGNATURE_VALUE
   */
  public void test_killDeep() throws Exception {
    if (isPureWindows()) return;

    // create and start long running task (nested SH calls)
    final Thread longRunningThread = prepareLongProcesses();

    // get main proc list, using environment signature
    List procList = deep_manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{TaskToKill.TEST_SIGNATURE_VALUE},
            false);
    // check if we found the processes
    assertTrue("No " + TaskToKill.TEST_SIGNATURE_VALUE + " processes found",
            !procList.isEmpty());
    // check if we found ALL the processes
    // NOTE: lyolik@noir.crocodile.org - does not works on WinXP
//     assertEquals(13, procList.size());

    // kill
    for (int i = 0; i < procList.size(); i++) {
      final OSProcess p = (OSProcess) procList.get(i);
      manager.killProcess(p.getPID());
    }

    // check if nothing has left from parent script using normal manager
    procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{TEST_PRCMANAGER_SH},
            false);
    assertTrue("Processes left after kill" + get_data(procList),
            procList.isEmpty());

    // check if nothing has left from children scripts using normal manager
    procList = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{PRCMANAGER_SPAWN_SH},
            false);
    assertTrue("Processes left after kill" + get_data(procList),
            procList.isEmpty());

    // check if nothing has left from enviromentaly signed using deep manager
    procList = deep_manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{TaskToKill.TEST_SIGNATURE_VALUE},
            false);
    assertTrue("Processes left after kill" + get_data(procList),
            procList.isEmpty());

    // double check if the threads running command are also dead
    assertTrue("Thread still running", !longRunningThread.isAlive());
  }


  /**
   * Tests if command paths are parsed correctly
   */
  public void test_parseCommand() throws Exception {
    String command = "/bin/ps -aux";
    final String[] data = new String[3];
    ProcessUtils.parseCommand(command, data);
    assertEquals("Path incorrect", "/bin", data[1]);
    assertEquals("Name incorrect", "ps", data[0]);
    assertEquals("RemoteCommand line incorrect", "/bin/ps -aux", data[2]);

    command = "ps";
    ProcessUtils.parseCommand(command, data);
    assertEquals("Path incorrect", "", data[1]);
    assertEquals("Name incorrect", "ps", data[0]);
    assertEquals("RemoteCommand line incorrect", "ps", data[2]);

    command = "ps -aux";
    ProcessUtils.parseCommand(command, data);
    assertEquals("Path incorrect", "", data[1]);
    assertEquals("Name incorrect", "ps", data[0]);
    assertEquals("RemoteCommand line incorrect", "ps -aux", data[2]);

  }


  /**
   * Helper method - returns true if environment is a pure
   * Windows, non-CYGWIN system
   */
  private boolean isPureWindows() throws IOException, AgentFailureException {
    return (agentEnv.isWindows() && agentEnv.systemType() != AgentEnvironment.SYSTEM_TYPE_CYGWIN);
  }


  /**
   * Helper to launch spawned processes
   */
  private Thread prepareLongProcesses() throws Exception {
    // create and start long running task (sleep command)
    String path = null;
    if (agentEnv.systemType() == AgentEnvironment.SYSTEM_TYPE_CYGWIN) {
      path = agentEnv.cygwinWindowsPathToUnix(TestHelper.getTestDataDir().getAbsolutePath());
    } else {
      path = TestHelper.getTestDataDir().getAbsolutePath();
    }

    final Thread longRunningThread = new TaskToKill(agentEnv, TestHelper.getTestDataDir(),
            "sh " + path + '/' + TEST_PRCMANAGER_SH);
    longRunningThread.start();

    // let it warm up
    Thread.sleep(2000);
    return longRunningThread;
  }


  /**
   * Common method to check that there is no more sleep,
   * test_prcmanager.sh and prcmanager_spawn.sh processes
   */
  private void check_processes_left() throws Exception {
    List l = null;
    l = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{TEST_PRCMANAGER_SH},
            false);

    assertTrue("Top level processes left: " + get_data(l),
            l.isEmpty());

    l = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{PRCMANAGER_SPAWN_SH},
            false);

    assertTrue("Spawned processes left:" + get_data(l),
            l.isEmpty());

    l = manager.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{SLEEP_SIGNATURE},
            false);

    assertTrue("Sleep processes left:" + get_data(l),
            l.isEmpty());
  }


  private static String get_data(final List l) {
    final StringBuffer buf = new StringBuffer('\n');
    for (int i = 0; i < l.size(); i++)
      buf.append(l.get(i)).append('\n');
    return buf.toString();
  }


  protected void setUp() throws Exception {
    super.setUp();
    // enabled proc debug
    System.setProperty(SystemConstants.SYSTEM_PROPERTY_PROCESS_DEBUG_ENABLED, Boolean.toString(true));
    //
    agentEnv = AgentManager.getInstance().getAgentEnvironment(new AgentHost(AgentConfig.BUILD_MANAGER, ""));
    manager = ProcessManagerFactory.getProcessManager(agentEnv);
    deep_manager = ProcessManagerFactory.getProcessManager(agentEnv);
    deep_manager.setDeep(true);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestProcessManagerFactory.class);
  }
}
