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

import org.parabuild.ci.common.*;
import org.parabuild.ci.remote.*;

import java.io.IOException;
import org.parabuild.ci.build.AgentFailureException;

/**
 * Subversion command to get version infromation in stdout.
 */
final class SVNVersionCommand extends VersionControlRemoteCommand {

  /**
   * Constructor.
   *
   * @param agent to run command at.
   * @param exePath path to svn executable.
   */
  public SVNVersionCommand(final Agent agent, final String exePath) throws IOException, AgentFailureException {
    super(agent, true);
    super.setCommand(StringUtils.putIntoDoubleQuotes(exePath) + " -q --version");

    // set and register remote current dir in the signature
    super.setCurrentDirectory(agent.getCheckoutDirName());
    super.signatureRegistry.register(remoteCurrentDir);
  }
}
