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

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.ThreadUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TimeoutWatchdog implements Runnable {

  private static final Log log = LogFactory.getLog(TimeoutWatchdog.class);

  public static final byte COMMAND_START = 0;
  public static final byte COMMAND_TIMEOUT = 1;
  public static final byte COMMAND_STOP = 2;

  // thread pool setup
  private static final int POOL_KEEP_ALIVE = 10000;
  private static final int POOL_MAX_SIZE = Integer.MAX_VALUE;
  private static final int POOL_INITIAL_THREAD_COUNT = 30;
  private static final PooledExecutor watchdogThreadPool = ThreadUtils.makeThreadPool(POOL_KEEP_ALIVE, POOL_MAX_SIZE, POOL_INITIAL_THREAD_COUNT, "TimeoutWatchdogThread");

  /**
   * Time to wait after timed out command was killed.
   */
  public static final int COOL_DOWN_SECONDS = 10;

  private boolean timedOut;
  private boolean hung;
  private byte command = COMMAND_START;
  private long timeOutMillis = 60 * 1000; // default is 60 seconds
  private final List timeoutMatches = new ArrayList(11);
  private TimeoutCallback timeoutCallback;
  private AgentEnvironment agentEnvironment;
  private boolean active;


  /**
   * Constructor
   *
   * @param agentEnvironment for which this watchdog is
   * created
   */
  public TimeoutWatchdog(final AgentEnvironment agentEnvironment) {
    this.agentEnvironment = agentEnvironment;
  }


  /**
   * Sets set of strings to be matched when looking for a
   * processes to kill in case of timeout
   *
   * @param timeoutMatches
   */
  public void addTimeoutMatches(final String[] timeoutMatches) {
    addTimeoutMatches(Arrays.asList(timeoutMatches));
  }


  /**
   * Sets set of strings to be matched when looking for a
   * processes to kill in case of timeout
   *
   * @param timeoutMatches
   */
  public void addTimeoutMatches(final List timeoutMatches) {
    this.timeoutMatches.addAll(timeoutMatches);
  }


  /**
   * Sets watchdog timeout in seconds
   */
  public void setTimeoutSeconds(final int timeOut) {
    this.timeOutMillis = (long)timeOut * 1000L;
  }


  /**
   * Sets timeout handler that handler timeout and hangs
   * callbacks
   */
  public void setTimeoutCallback(final TimeoutCallback timeoutCallback) {
    ArgumentValidator.validateArgumentNotNull(timeoutCallback, "timeout callback");
    this.timeoutCallback = timeoutCallback;
  }


  public boolean isTimedOut() {
    return timedOut;
  }


  public boolean isHung() {
    return hung;
  }


  /**
   * Starts watch dog
   */
  public void start() {
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Starting watching");
    if (timeOutMillis < 1000) throw new IllegalStateException("Timeout \"" + timeOutMillis / 1000 + "\" seconds is too short.");
    if (timeoutCallback == null) throw new IllegalStateException("Timeout callback is null");

    try {
      watchdogThreadPool.execute(this);
    } catch (final InterruptedException e) {
      IoUtils.ignoreExpectedException(e);
    }
//    watchdogThread = ThreadUtils.makeDaemonThread(this, "TimeoutWatchdog");
//    watchdogThread.start();
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Started watching");
  }


  /**
   * Stops watchdog.
   *
   * This command can be called in the following contexts:
   * The run methoed has not been called yet and the run
   * method is waiting for time out.
   *
   * In either case, this method will rise the COMMAND_STOP
   * and optionally notify the waiting run that the state
   * has change if the thread running run is active.
   */
  public synchronized void stop() {
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Stopping watching");

    // request stop
    command = COMMAND_STOP;

    // check if we need to notify
    if (!active) return;

    notifyAll();
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Stopped watching");
  }


  /**
   * It is possible that this command will be called *after*
   * it went through start -> stop sequence.
   */
  public synchronized void run() {
    active = true;
    try {
      if (command == COMMAND_STOP) {
        // while our thread was going to start us we have
        // already been stopped.
        return;
      }
      if (command != COMMAND_START) {
        // at this stage only COMMAND_START is accepted.
        throw new IllegalStateException("Illegal command code \"" + command + '\"');
      }
      try {
        // preset command to time out
        command = COMMAND_TIMEOUT;
        wait(timeOutMillis);
      } catch (final InterruptedException e) {
        IoUtils.ignoreExpectedException(e);
      }

      // check if there was stop requested externally
      if (command == COMMAND_STOP) return;

      // check if this is a timeout
      if (command == COMMAND_TIMEOUT) {
        timedOut = true;
        timeoutCallback.commandTimedOut();
        try {
          killCommand();
        } catch (final BuildException e) {
          reportErrorStoppingCommad(e);
        }
      }

    } finally {
      // clean up
      active = false;
//      watchdogThread = null;
    }
  }


  /**
   * Kills command
   *
   * @throws BuildException
   */
  private synchronized void killCommand() throws BuildException {
    // kill
    try {
      final ProcessManager pm = ProcessManagerFactory.getProcessManager(agentEnvironment);
      pm.setDeep(true);
      final List pids = getPIDs(pm);
      pm.setDeep(false);
      pm.killProcesses(pids);

      // check if we were stopped externally
      if (command == COMMAND_STOP) return;

      // cool down
      if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Cooling down after stop");
      for (int i = 0; i < COOL_DOWN_SECONDS && command != COMMAND_STOP; i++) {
        try {
          // wait so that command can issue "stop" when it finalizes.
          // sleep doesnt work because we have to run synchronized.
          // yet, wait surrenders lock.
          wait(1000);
        } catch (final InterruptedException e) {
          IoUtils.ignoreExpectedException(e);
        }
      }

      // check if we were stopped externally
      if (command == COMMAND_STOP) return;

      // watchable didn't call back and stopped us
      if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Was not able to stop");
      hung = true;
      timeoutCallback.commandHung();
    } catch (final IOException e) {
      throw new BuildException("Error", e, agentEnvironment);
    }
  }


  /**
   * Get list of PIDs belonging to a commands
   */
  private List getPIDs(final ProcessManager pm) throws BuildException {
    final long start = System.currentTimeMillis();
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Get list");
    final List processes = pm.getProcesses(ProcessManager.SORT_BY_PID, (String[])this.timeoutMatches.toArray(new String[0]), false);
    if (ProcessUtils.PROCESS_DEBUG_ENABLED) log.debug("Getting list took " + (System.currentTimeMillis() - start));
    return ProcessUtils.processListToPIDList(processes);
  }


  private static void reportErrorStoppingCommad(final BuildException e) {
    final Error error = new Error("Error while stopping a command");
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSendEmail(false);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public String toString() {
    return "TimeoutWatchdog{" +
      "timedOut=" + timedOut +
      ", command=" + command +
      ", timeOutMillis=" + timeOutMillis +
      ", timeoutMatches=" + timeoutMatches +
      ", timeoutCallback=" + timeoutCallback +
      ", agentEnvironment=" + agentEnvironment +
      ", active=" + active +
      '}';
  }
}