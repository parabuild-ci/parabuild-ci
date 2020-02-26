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
package org.parabuild.ci.versioncontrol.mks;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.StderrLineProcessor;
import org.parabuild.ci.versioncontrol.VersionControlRemoteCommand;

/**
 * Base command for all MKS commands
 */
abstract class MKSCommand extends VersionControlRemoteCommand {

  private static final Log log = LogFactory.getLog(MKSCommand.class);

  private final MKSCommandParameters parameters;


  /**
   * Creates MKSCommand that uses system-wide
   * timeout for version control commands
   *
   * @param agent
   */
  protected MKSCommand(final Agent agent, final MKSCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, true);
    super.setCurrentDirectory(agent.getCheckoutDirName());
    super.signatureRegistry.register(remoteCurrentDir);
    super.setStderrLineProcessor(new CommonStderrLineProcessor(agent.getCheckoutDirName()));
    this.parameters = parameters;
  }


  /**
   * Callback method - this method is called before execute.
   */
  protected void preExecute() throws IOException, AgentFailureException {
    super.preExecute();

    // compose commnon command line
    final StringBuffer cmd = new StringBuffer(200);
    cmd.append(StringUtils.putIntoDoubleQuotes(parameters.getExePath())).append(' ');
    appendCommand(cmd, mksCommand());
    appendCommand(cmd, StringUtils.putIntoDoubleQuotes("--hostname=" + parameters.getHost()));
    appendCommand(cmd, StringUtils.putIntoDoubleQuotes("--port=" + parameters.getPort()));
    appendCommand(cmd, StringUtils.putIntoDoubleQuotes("--password=" + parameters.getPassword()));
    appendCommand(cmd, StringUtils.putIntoDoubleQuotes("--user=" + parameters.getUser()));
    appendCommand(cmd, "--quiet");
    appendCommand(cmd, "--batch");
    if (!StringUtils.isBlank(parameters.getDevelopmentPath())) {
      appendCommand(cmd, StringUtils.putIntoDoubleQuotes("--devpath=" + parameters.getDevelopmentPath()));
    }

    // request command-specific details
    appendCommand(cmd, mksCommandArguments());

    // set resulting command
    setCommand(cmd.toString());
    if (log.isDebugEnabled()) log.debug("cmd: " + removePasswordFromDebugString(cmd.toString()));
  }


  protected abstract String mksCommand();


  protected abstract String mksCommandArguments() throws IOException, AgentFailureException;


  /**
   * Commmon StderrLineProcessor - just reponds that has to add
   * line to errors.
   */
  static final class CommonStderrLineProcessor implements StderrLineProcessor {

    private final String checkoutDir;


    /**
     * Create common stderr analyzer.
     *
     * @param checkoutDir String is used to excluded staerr lines
     *                    that start with this dir.
     */
    public CommonStderrLineProcessor(final String checkoutDir) {
      this.checkoutDir = checkoutDir.toLowerCase();
    }


    /**
     * Process line index
     *
     * @param index
     * @param line
     * @return result code
     * @see #RESULT_ADD_TO_ERRORS
     * @see #RESULT_IGNORE
     */
    public int processLine(final int index, final String line) {
      if (line.startsWith("Connecting to")) return RESULT_IGNORE;
      if (line.toLowerCase().startsWith(checkoutDir)) return RESULT_IGNORE;
      return RESULT_ADD_TO_ERRORS;
    }
  }
}
