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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.StderrLineProcessor;

/**
 * MKS createsandbox command
 */
final class MKSCreatesandboxCommand extends MKSCommand {

  private final MKSCreatesandboxCommandParameters parameters;


  /**
   * Creates MKSCommand.
   *
   * @param agent
   */
  MKSCreatesandboxCommand(final Agent agent, final MKSCreatesandboxCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
    super.setStderrLineProcessor(new MKSCreatesandboxStderrLineProcessor(agent.getCheckoutDirName()));
  }


  protected String mksCommand() {
    return "createsandbox";
  }


  protected String mksCommandArguments() throws IOException, AgentFailureException {
    final StringBuffer result = new StringBuffer(100);
    appendCommand(result, "--nopopulate");
    appendCommand(result, "--lineTerminator=" + parameters.getFormattedLineTerminator());
    if (!StringUtils.isBlank(parameters.getProjectRevision())) {
      appendCommand(result, "--projectRevision=" + parameters.getProjectRevision());
    }
    appendCommand(result, "-R");
    appendCommand(result, "-P", StringUtils.putIntoDoubleQuotes(parameters.getProject()));
    appendCommand(result, StringUtils.putIntoDoubleQuotes(agent.getCheckoutDirName()));
    return result.toString();
  }


  private static final class MKSCreatesandboxStderrLineProcessor implements StderrLineProcessor {

    private final CommonStderrLineProcessor commonStderrLineProcessor;


    /**
     * Create common stderr analzer.
     *
     * @param checkoutDir String is used to excluded staerr lines
     *                    that start with this dir.
     */
    public MKSCreatesandboxStderrLineProcessor(final String checkoutDir) {
      this.commonStderrLineProcessor = new CommonStderrLineProcessor(checkoutDir);
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
      if (line.startsWith("Creating sandbox...")) return RESULT_IGNORE;
      if (line.startsWith("Resynchronizing files...")) return RESULT_IGNORE;
      if (line.startsWith("Resynchronizing file...")) return RESULT_IGNORE;
      if (line.startsWith("Failed to create sandbox") && line.indexOf("There is already a registered entry for the path") > 0)
        return RESULT_IGNORE;
      return commonStderrLineProcessor.processLine(index, line);
    }
  }
}
