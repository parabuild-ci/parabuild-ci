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
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;

import java.util.Iterator;
import java.util.List;

/**
 * Tests ProcessManager functionality
 */
public class SATestTimeoutWatchdog extends TestCase {

  public static final int TEST_TIME_OUT_SECONDS = 15;
  public static final int TEST_COOL_DOWN_SECONDS = TEST_TIME_OUT_SECONDS + 10;

  public static final int TEST_SHORT_TIME_TO_RUN = TEST_TIME_OUT_SECONDS >> 1;
  public static final int TEST_LONG_TIME_TO_RUN = 1000;

  static final String SLEEP_SIGNATURE = "sleep";
  static final String TEST_PRCMANAGER_SH = "test_prcmanager.sh";
  static final String PRCMANAGER_SPAWN_SH = "prcmanager_spawn.sh";
  private String LONG_SLEEP_COMMAND = null;
  private String SHOR_SLEEP_COMMAND = null;

  private MockTimeoutCallback mockTimeoutHandler = null;
  private ProcessManager deepProcman = null;
  private TimeoutWatchdog watchdog = null;
  private AgentEnvironment agentEnv;


  public SATestTimeoutWatchdog(final String s) {
    super(s);
  }


  /**
   * Tests that we can find and kill single process (sleep)
   */
  public void test_timesOutSingleProcess() throws Exception {

    // create and start long running task (sleep command)
    final Thread longRunningThread = new TaskToKill(agentEnv, LONG_SLEEP_COMMAND);
    longRunningThread.start();

    // create watch dog
    watchdog.addTimeoutMatches(new String[]{SLEEP_SIGNATURE, TaskToKill.TEST_SIGNATURE_VALUE});
    watchdog.setTimeoutSeconds(TEST_TIME_OUT_SECONDS);
    watchdog.start();

    // wait for timeout plus five seconds
    Thread.sleep((TEST_COOL_DOWN_SECONDS) * 1000);

    // assert watchdog timed out
    assertTrue(watchdog.isTimedOut());
    assertTrue(!longRunningThread.isAlive());

    // assert handler was called
    assertTrue(mockTimeoutHandler.isTimedOut());
    assertTrue(!mockTimeoutHandler.isHung());

    // assert watchdog killed everyting listing signed process
    List procList = deepProcman.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{SLEEP_SIGNATURE}, true);
    for (Iterator iter = procList.iterator(); iter.hasNext();) {
      final OSProcess process = (OSProcess) iter.next();
//      System.out.println("DEBUG: process.getName() = " + process.getName());
//      System.out.println("DEBUG: process.getPID() = " + process.getPID());
//      System.out.println("DEBUG: process.getPPID() = " + process.getPPID());
    }
    assertTrue(procList.isEmpty());

    // assert watchdog killed everyting listing sleep
    procList = deepProcman.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{SLEEP_SIGNATURE}, true);
    assertTrue(procList.isEmpty());
  }


  /**
   * Tests that we don't kill process that finished before timeout
   */
  public void test_doesntTimeOutSingleProcess() throws Exception {

    // create and start long running task (sleep command)
    final Thread longRunningThread = new TaskToKill(agentEnv, SHOR_SLEEP_COMMAND);
    longRunningThread.start();

    // create watch dog
    watchdog.addTimeoutMatches(new String[]{SLEEP_SIGNATURE, TaskToKill.TEST_SIGNATURE_VALUE});
    watchdog.setTimeoutSeconds(TEST_TIME_OUT_SECONDS);
    watchdog.start();


    Thread.sleep((TEST_SHORT_TIME_TO_RUN + 3) * 1000);

    // assert watchdog NOT timed out
    assertTrue(!longRunningThread.isAlive());
    assertTrue(!watchdog.isTimedOut());

    // stop
    watchdog.stop();

    // assert handler was called
    assertTrue(!mockTimeoutHandler.isTimedOut());
    assertTrue(!mockTimeoutHandler.isHung());

    // get proc list
    final List procList = deepProcman.getProcesses(ProcessManager.SORT_BY_PID,
            new String[]{SLEEP_SIGNATURE, TaskToKill.TEST_SIGNATURE_VALUE}, true);

    // assert process finished itself
    assertTrue(procList.isEmpty());
  }


  protected void setUp() throws Exception {
    super.setUp();
    agentEnv = AgentManager.getInstance().getAgentEnvironment(new AgentHost(AgentConfig.BUILD_MANAGER, ""));
    deepProcman = ProcessManagerFactory.getProcessManager(agentEnv);
    deepProcman.setDeep(true);

    mockTimeoutHandler = new MockTimeoutCallback();
    watchdog = new TimeoutWatchdog(agentEnv);
    watchdog.setTimeoutCallback(mockTimeoutHandler);

    LONG_SLEEP_COMMAND = ((agentEnv.isWindows()) ? "cmd /C " : "") + "sleep " + TEST_LONG_TIME_TO_RUN;
    SHOR_SLEEP_COMMAND = ((agentEnv.isWindows()) ? "cmd /C " : "") + "sleep " + TEST_SHORT_TIME_TO_RUN;
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestTimeoutWatchdog.class);
  }
}
