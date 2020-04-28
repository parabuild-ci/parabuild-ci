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
package org.parabuild.ci.versioncontrol;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;

/**
 * Executes ClearCase update command.
 */
final class ClearCaseUpdateCommand extends ClearCaseCommand {

  public ClearCaseUpdateCommand(final Agent agent, final String exePath, final String ignoreLines) throws IOException, AgentFailureException {
    super(agent, exePath);
    super.setStderrLineProcessor(new AbstractClearCaseStderrProcessor(ignoreLines) {
      protected int doProcessLine(final int index, final String line) {
        return RESULT_ADD_TO_ERRORS;
      }
    });
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected String getExeArguments() throws IOException, AgentFailureException {
    final String logOption = agent.isWindows() ? "nul" : "/dev/null";

    final StringBuilder sb = new StringBuilder(100);
    sb.append(" update ");
    sb.append(" -force "); // Suppresses the confirmation prompts
    sb.append(" -overwrite "); // Overwrites all hijacked files with the version selected by the config spec
    sb.append(" -ptime "); // Sets the time stamp of a file element to the time at which the version was checked in to the VOB
    sb.append(" -log  ").append(logOption); // disables  update log
    sb.append(' ');
    sb.append(agent.getCheckoutDirName()); // pathame for view
    return sb.toString();
  }
}
