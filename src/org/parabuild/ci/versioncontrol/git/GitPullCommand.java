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
final class GitPullCommand extends GitCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(GitPullCommand.class); // NOPMD

  private final String password;
  private final String pathToGitExe;
  private final String user;


  GitPullCommand(final Agent agent, final String pathToGitExe, final String repository,
                 final String user, final String password) throws AgentFailureException, IOException {
    super(agent, pathToGitExe, repository);
    this.password = password;
    this.pathToGitExe = pathToGitExe;
    this.user = user;
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected final String getExeArguments() {
    final StringBuilder result = new StringBuilder(200);
    result.append(' ').append("pull");
    return result.toString();
  }


  public String toString() {
    return "GitPullCommand{" +
            "password='" + password + '\'' +
            ", pathToGitExe='" + pathToGitExe + '\'' +
            ", user='" + user + '\'' +
            "} " + super.toString();
  }
}