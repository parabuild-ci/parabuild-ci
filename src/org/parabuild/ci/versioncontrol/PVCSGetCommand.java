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
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;

/**
 */
final class PVCSGetCommand extends PVCSCommand {

  private final PVCSGetCommandParameters parameters;


  /**
   * Creates VersionControlRemoteCommand that uses
   * system-wide timeout for version control commands.
   */
  protected PVCSGetCommand(final Agent agent, final PVCSGetCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
  }


  /**
   * Returns arguments to pass to PVCS executable with "run"
   * command.
   */
  protected String getRunArguments() throws IOException, AgentFailureException {
    return getRunArguments(agent, parameters, makeUserAndPasswordOption(), false);
  }


  /**
   * Resusable helper method.
   *
   * @param agent
   * @param parameters
   * @param userAndPasswordOption
   * @param displayMode
   * @return
   * @throws IOException
   */
  static String getRunArguments(final Agent agent, final PVCSGetCommandParameters parameters,
                                final String userAndPasswordOption, final boolean displayMode) throws IOException, AgentFailureException {
    // prepare checkout dir
    final String stringDestPath = agent.getCheckoutDirName() + '/' + parameters.getProject();
    agent.mkdirs(stringDestPath);
    final RemoteFileDescriptor fileDescriptor = agent.getFileDescriptor(stringDestPath);

    // compose
    final StringBuilder args = new StringBuilder(100);
    args.append(" get ");
    args.append("-o "); // override
    args.append("-qe "); // quiet on non-existing entities
    args.append("-z "); // subdirs
    args.append("-u "); // update
    args.append("\"-pr").append(parameters.getRepository()).append("\" ");
    if (!displayMode) {
      args.append("\"-a").append(fileDescriptor.getCanonicalPath()).append("\" ");
    }
    if (parameters.getDate() != null) {
      final PVCSDateFormat format = new PVCSDateFormat(agent.defaultLocale());
      args.append("\"-d").append(format.formatLongInput(parameters.getDate())).append("\" ");
    }
    if (!StringUtils.isBlank(parameters.getBranch())) {
      args.append("\"-v").append(parameters.getBranch()).append("\" ");
    }
    if (!StringUtils.isBlank(parameters.getPromotionGroup())) {
      args.append("\"-g").append(parameters.getPromotionGroup()).append("\" ");
    }
    if (!StringUtils.isBlank(userAndPasswordOption)) {
      args.append(' ').append(userAndPasswordOption);
    }
    args.append(' ').append(StringUtils.putIntoDoubleQuotes(parameters.getProject()));
    return args.toString();
  }
}
