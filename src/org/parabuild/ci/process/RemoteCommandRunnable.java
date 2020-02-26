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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.remote.Agent;

import java.io.File;
import java.io.IOException;

final class RemoteCommandRunnable implements Runnable {

  // Parameters
  private final int handle;
  private final Agent builder;
  private final String commandLineToExecute;
  private final TailBufferSize tailBufferSize;
  private final File stdoutFile;
  private final File stderrFile;
  private final File mergedFile;

  // Result
  private volatile int resultCode = 0;
  private volatile IOException ioException = null;
  private volatile CommandStoppedException commandStoppedException = null;
  private volatile AgentFailureException agentFailureException = null;
  private volatile boolean resultAvailable = false;


  /**
   * Constructor.
   *
   * @param handle
   * @param agent
   * @param commandLineToExecute
   * @param tailBufferSize
   * @param stdoutFile
   * @param stderrFile
   * @param mergedFile
   */
  RemoteCommandRunnable(final int handle, final Agent agent, final String commandLineToExecute,
                        final TailBufferSize tailBufferSize, final File stdoutFile,
                        final File stderrFile, final File mergedFile) {
    this.handle = handle;
    this.builder = agent;
    this.commandLineToExecute = commandLineToExecute;
    this.tailBufferSize = tailBufferSize;
    this.stdoutFile = stdoutFile;
    this.stderrFile = stderrFile;
    this.mergedFile = mergedFile;
  }


  public void run() {
    try {
      // NOTE: vimeshev - 2006-02-10 - we set current dir to null
      // this to minimize the amount of environment information
      // passed around in attempt to fix a native memory leak under Linux.
      // Switching to the current dir is done in the generated shell script.
      resultCode = builder.execute(handle, null, commandLineToExecute, null, tailBufferSize, stdoutFile, stderrFile, mergedFile);
    } catch (final IOException e) {
      this.ioException = e;
    } catch (final CommandStoppedException e) {
      this.commandStoppedException = e;
    } catch (final AgentFailureException e) {
      agentFailureException = e;
    }
    resultAvailable = true;
  }


  /**
   * Returns result code.
   *
   * @return result code.
   * @throws IOException
   * @throws CommandStoppedException
   */
  public int getResultCode() throws IOException, CommandStoppedException, AgentFailureException {
    if (ioException != null) {
      throw ioException;
    }
    if (commandStoppedException != null) {
      throw commandStoppedException;
    }
    if (agentFailureException != null) {
      throw agentFailureException;
    }
    return resultCode;
  }


  /**
   * Returns true if the result is available.
   *
   * @return true if the result is available.
   */
  public boolean isResultAvailable() {
    return resultAvailable;
  }
}


