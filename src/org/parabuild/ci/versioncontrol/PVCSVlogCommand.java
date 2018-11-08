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

import java.io.IOException;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 */
final class PVCSVlogCommand extends PVCSCommand {

  private final PVCSVlogCommandParameters parameters;


  /**
   * Creates VersionControlRemoteCommand that uses
   * system-wide timeout for version control commands.
   */
  protected PVCSVlogCommand(final Agent agent, final PVCSVlogCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
  }


  /**
   * Returns arguments to pass to PVCS executable with "run"
   * command.
   */
  protected String getRunArguments() throws IOException, AgentFailureException {
    final PVCSDateFormat format = new PVCSDateFormat(agent.defaultLocale());

    // compose
    final StringBuilder args = new StringBuilder(100);
    args.append(" vlog ");
    args.append("-z "); // subdirs
    args.append("\"-pr").append(parameters.getRepository()).append("\" ");
    if (parameters.getStartDate() != null) {
      args.append("\"-ds").append(format.formatInput(parameters.getStartDate())).append("\" ");
    }
    if (parameters.getEndDate() != null) {
      args.append("\"-de").append(format.formatInput(parameters.getEndDate())).append("\" ");
    }
    if (!StringUtils.isBlank(parameters.getBranch())) {
      args.append("\"-v").append(parameters.getBranch()).append("\" ");
    }
    if (!StringUtils.isBlank(parameters.getPromotionGroup())) {
      args.append("\"-g").append(parameters.getPromotionGroup()).append("\" ");
    }
    args.append(' ').append(makeUserAndPasswordOption());
    args.append(' ').append(parameters.getProject());
    return args.toString();
  }
}
