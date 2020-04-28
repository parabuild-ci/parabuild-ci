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
import org.parabuild.ci.util.StringUtils;

import java.io.IOException;

/**
 */
final class PVCSRunCommand extends PVCSCommand {

  private final PVCSRunCommandParameters parameters;
  private String tempFile = null;


  /**
   * Creates VersionControlRemoteCommand that uses
   * system-wide timeout for version control commands.
   */
  protected PVCSRunCommand(final Agent agent, final PVCSRunCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
  }


  /**
   * Returns arguments to pass to PVCS executable with "run"
   * command.
   */
  protected String getRunArguments() throws IOException, AgentFailureException {
    tempFile = agent.createTempFile("scm", ".pcli", parameters.getScriptContent());

    // compose
    final StringBuilder args = new StringBuilder(100);
    args.append(" \"-s").append(tempFile).append('\"');
    args.append(' ').append(makeUserAndPasswordOption());
    return args.toString();
  }


  /**
   * Removes all output and temporary files
   */
  public void cleanup() throws AgentFailureException {
    if (agent != null && StringUtils.isBlank(tempFile)) {
      agent.deleteTempFileHard(tempFile);
    }
    super.cleanup();
  }
}
