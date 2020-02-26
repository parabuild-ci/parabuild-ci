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
import org.parabuild.ci.versioncontrol.VersionControlRemoteCommand;

/**
 * MKS addlabel command
 */
final class MKSAddlabelCommand extends MKSCommand {

  private final MKSAddlabelCommandParameters parameters;


  /**
   * Creates MKSCommand.
   *
   * @param agent
   */
  MKSAddlabelCommand(final Agent agent, final MKSAddlabelCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
    super.setStderrLineProcessor(new MKSAddlabelStderrLineProcessor(agent.getCheckoutDirName()));
    super.setRespectErrorCode(true);
  }


  protected String mksCommand() {
    return "addlabel";
  }


  protected String mksCommandArguments() throws IOException, AgentFailureException {
    final StringBuffer result = new StringBuffer(100);
    VersionControlRemoteCommand.appendCommand(result, "-R");
    VersionControlRemoteCommand.appendCommand(result, "--quiet");
    VersionControlRemoteCommand.appendCommand(result, "--nomoveLabel");
    VersionControlRemoteCommand.appendCommand(result, "-S", StringUtils.putIntoDoubleQuotes(agent.getFileDescriptor(agent.getCheckoutDirName() + '/' + parameters.getProjectName()).getCanonicalPath()));
    VersionControlRemoteCommand.appendCommand(result, "-L", StringUtils.putIntoDoubleQuotes(parameters.getLabel()));
    return result.toString();
  }


  private static final class MKSAddlabelStderrLineProcessor implements StderrLineProcessor {

    private final CommonStderrLineProcessor commonStderrLineProcessor;


    /**
     * Create common stderr analyzer.
     *
     * @param checkoutDir String is used to excluded staerr lines
     *                    that start with this dir.
     */
    public MKSAddlabelStderrLineProcessor(final String checkoutDir) {
      this.commonStderrLineProcessor = new CommonStderrLineProcessor(checkoutDir);
    }


    /**
     * Process line index.
     * <p/>
     * If there we files that should have been deleted as a part
     * of sync will accumulate them for deletion in {@link
     * MKSCoCommand#postExecute(int)}.
     *
     * @param index
     * @param line
     * @return result code
     * @see #RESULT_ADD_TO_ERRORS
     * @see #RESULT_IGNORE
     * @see MKSAddlabelCommand#postExecute(int)
     */
    public int processLine(final int index, final String line) {
      if (line.startsWith("Checking out members...")) return RESULT_IGNORE;
      return commonStderrLineProcessor.processLine(index, line);
    }
  }
}
