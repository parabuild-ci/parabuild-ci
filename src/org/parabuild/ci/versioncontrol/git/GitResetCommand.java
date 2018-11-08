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
package org.parabuild.ci.versioncontrol.git;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;

/**
 * GitCloneCommand clones a Git repository.
 */
final class GitResetCommand extends GitCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(GitResetCommand.class); // NOPMD

  private final String password;
  private final String pathToGitExe;
  private final String user;
  private String hash;


  GitResetCommand(final Agent agent, final String pathToGitExe, final String repository,
                  final String user, final String password, final String hash) throws AgentFailureException, IOException {
    super(agent, pathToGitExe, repository);
    this.password = password;
    this.pathToGitExe = pathToGitExe;
    this.user = user;
    this.hash = hash;
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected final String getExeArguments() {
    final StringBuilder result = new StringBuilder(200);
    result.append(' ').append("reset");
    result.append(' ').append("--hard");
    result.append(' ').append(hash);
    return result.toString();
  }


  public String toString() {
    return "GitCloneCommand{" +
            "password='" + password + '\'' +
            ", pathToGitExe='" + pathToGitExe + '\'' +
            ", user='" + user + '\'' +
            "} " + super.toString();
  }
}