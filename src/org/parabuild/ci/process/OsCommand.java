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

import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.RuntimeUtils;

/**
 */
public final class OsCommand {

  private static final Map commands = new HashMap(111);

  /**
   * Counter to produce executor handles.
   */
  private static int executorHandleCounter = 0;


  private final File mergedFile;
  private final File stderrFile;
  private final File stdoutFile;
  private final int handle;
  private final Tailer tailer;
  private final Map environment;
  private final String cmd;
  private final String directoryToExecuteIn;


  /**
   * Creates command.
   *
   * @param handle
   * @param directoryToExecuteIn
   * @param cmd
   * @param environment
   * @param tailBufferSize
   * @param stdoutFile
   * @param stderrFile
   * @param mergedFile
   */
  public OsCommand(final int handle, final String directoryToExecuteIn,
    final String cmd, final Map environment, final TailBufferSize tailBufferSize,
    final File stdoutFile, final File stderrFile, final File mergedFile) {

    this.cmd = cmd;
    this.directoryToExecuteIn = directoryToExecuteIn;
    this.environment = environment;
    this.handle = handle == 0 ? createCommandHandle() : handle;
    this.mergedFile = mergedFile;
    this.stderrFile = stderrFile;
    this.stdoutFile = stdoutFile;
    this.tailer = makeLogTail(tailBufferSize);
  }


  private static Tailer makeLogTail(final TailBufferSize tailBufferSize) {
    if (tailBufferSize.getMaxLineCount() <= 0 || tailBufferSize.getMaxLineLength() <= 0) {
      return new ZeroLengthTailer();
    } else {
      return new TailerImpl(tailBufferSize);
    }
  }


  /**
   * Executes command
   * @return
   * @throws IOException
   * @throws CommandStoppedException
   */
  public int execute() throws IOException, CommandStoppedException {
    try {
      register(this);
      return RuntimeUtils.execute(directoryToExecuteIn == null ? null : new File(directoryToExecuteIn), cmd, environment, tailer, stdoutFile, stderrFile, mergedFile);
    } finally {
      unregister(this);
    }
  }


  /**
   * Returns currently running command with the given handle
   * or null if not found.
   *
   * @param handle
   *
   * @return currently running command with the given handle
   * or null if not found.
   */
  public static OsCommand getCommand(final int handle) {
    return (OsCommand)commands.get(new Integer(handle));
  }


  public List getLogTailLines() {
    return tailer.getLines();
  }


  /**
   * Unregisters command from the list of currently runnint
   * commands.
   */
  private static void unregister(final OsCommand command) {
    synchronized (commands) {
      commands.remove(new Integer(command.handle));
    }
  }


  /**
   * Registers command in the list of currently runnint
   * commands.
   */
  private void register(final OsCommand command) {
    synchronized (commands) {
      commands.put(new Integer(handle), command);
    }
  }


  /**
   * @return new handle
   */
  public static int createCommandHandle() {
    return ++executorHandleCounter;
  }


  public String toString() {
    return "OsCommand{" +
      "mergedFile=" + mergedFile +
      ", stderrFile=" + stderrFile +
      ", stdoutFile=" + stdoutFile +
      ", handle=" + handle +
      ", tailer=" + tailer +
      ", environment=" + environment +
      ", cmd='" + cmd + '\'' +
      ", directoryToExecuteIn='" + directoryToExecuteIn + '\'' +
      '}';
  }
}
