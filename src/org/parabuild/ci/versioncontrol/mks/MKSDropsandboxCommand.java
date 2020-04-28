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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.versioncontrol.StderrLineProcessor;

import java.io.IOException;

/**
 * MKS dropsandpox command
 */
final class MKSDropsandboxCommand extends MKSCommand {

  private final MKSCommandParameters parameters;


  /**
   * Creates MKSCommand.
   *
   * @param agent
   */
  MKSDropsandboxCommand(final Agent agent, final MKSCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
    super.setStderrLineProcessor(new MKSDropandboxStderrLineProcessor(agent.getCheckoutDirName(), parameters.getProjectName()));
  }


  protected String mksCommand() {
    return "dropsandbox";
  }


  protected String mksCommandArguments() throws IOException, AgentFailureException {
    final StringBuffer result = new StringBuffer(100);
    appendCommand(result, "-f");
    appendCommand(result, "--delete=none");
    appendCommand(result, StringUtils.putIntoDoubleQuotes("--cwd=" + agent.getCheckoutDirName()));
    appendCommand(result, StringUtils.putIntoDoubleQuotes(parameters.getProjectName()));
    return result.toString();
  }


  private static final class MKSDropandboxStderrLineProcessor implements StderrLineProcessor {

    private final CommonStderrLineProcessor commonStderrLineProcessor;
    private final String projectName;


    /**
     * Create common stderr analyzer.
     *
     * @param checkoutDir String is used to excluded staerr lines
     *                    that start with this dir.
     */
    public MKSDropandboxStderrLineProcessor(final String checkoutDir, final String projectName) {
      this.commonStderrLineProcessor = new CommonStderrLineProcessor(checkoutDir);
      this.projectName = projectName;
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
      if (line.startsWith("Dropping sandbox...")) return RESULT_IGNORE;
      if (line.startsWith(projectName)) return RESULT_IGNORE;
      if (line.startsWith("*** The sandbox file") && line.endsWith("as a top level sandbox.")) return RESULT_IGNORE;
      return commonStderrLineProcessor.processLine(index, line);
    }
  }
}
