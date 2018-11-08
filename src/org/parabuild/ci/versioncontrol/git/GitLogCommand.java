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
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.RepositoryPath;

import java.io.IOException;

/**
 * GitLogCommand executes Git log command using the following format:
 * <pre>
 *   git log --name-status "--format=pArAbIlDb%hpArAbIlDs%anpArAbIlDs%aepArAbIlDs%cipArAbIlD%spArAbIlDs"
 * </pre>
 * This command uses string <i>pArAbIlDb</i> as a marker of a beginning of a commit description
 * and string <i>pArAbIlDs</i> as a separator between format placeholders.
 *
 * @author Slava Imeshev
 * @since Jan 24, 2010 2:15:00 PM
 */
final class GitLogCommand extends GitCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(GitLogCommand.class); // NOPMD

  private final int maxChangeLists;
  private final RepositoryPath repositoryPath;
  private final String changeListNumberFrom;
  private final String changeListNumberTo;
  private final String password;
  private final String pathToGitExe;
  private final String user;
  private final boolean useLimitOption;
  private static final String DEL = GitTextChangeLogParser.CHANGE_LIST_DELIMITER;
  private static final String SEP = GitTextChangeLogParser.FIELD_SEPARATOR;


  GitLogCommand(final Agent agent, final String pathToGitExe, final String repository, final RepositoryPath repositoryPath,
                final String changeListNumberFrom, final String changeListNumberTo, final int maxChangeLists,
                final String user, final String password, final boolean useLimitOption) throws AgentFailureException, IOException {
    super(agent, pathToGitExe, repository);
    this.changeListNumberFrom = changeListNumberFrom;
    this.changeListNumberTo = changeListNumberTo;
    this.maxChangeLists = maxChangeLists;
    this.password = password;
    this.pathToGitExe = pathToGitExe;
    this.repositoryPath = repositoryPath;
    this.user = user;
    this.useLimitOption = useLimitOption;
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected final String getExeArguments() throws IOException, AgentFailureException {

    if (LOG.isDebugEnabled()) {
      LOG.debug("maxChangeLists: " + maxChangeLists);
    }
    final boolean applyLimitOption = useLimitOption && maxChangeLists != Integer.MAX_VALUE;
    if (LOG.isDebugEnabled()) {
      LOG.debug("applyLimitOption: " + applyLimitOption);
    }

    //
    final String indicator = agent.isWindows() ? "%%" : "%";

    //
    final StringBuilder result = new StringBuilder(200);
    result.append(' ').append("log");
    result.append(' ').append("--name-status");

    result.append(' ').append(StringUtils.putIntoDoubleQuotes("--format=" + DEL + indicator + 'h' + SEP + indicator + "an" + SEP + indicator + "ae" + SEP + indicator + "ci" + SEP + indicator + 's' + SEP));
    result.append(' ').append(applyLimitOption ? '-' + Integer.toString(maxChangeLists) : "");
//    result.append(' ').append(StringUtils.putIntoDoubleQuotes("--git-dir=" + url));
    result.append(' ').append(makeRevisionRange());
    if (!"/".equals(repositoryPath.getPath())) {
      result.append(' ').append("--");
      result.append(' ').append(StringUtils.putIntoDoubleQuotes('/' + repositoryPath.getPath()));
    }

    //noinspection ControlFlowStatementWithoutBraces
    if (LOG.isDebugEnabled()) LOG.debug("result: " + result); // NOPMD
    return result.toString();
  }


  /**
   * Helper method to compose revision range.
   *
   * @return a String containing a revision range
   */
  private String makeRevisionRange() {
    if (StringUtils.isBlank(changeListNumberFrom) && StringUtils.isBlank(changeListNumberTo)) {
      return "";
    } else {
      return changeListNumberFrom + ".." + changeListNumberTo;
    }
  }


  public String toString() {
    return "GitLogCommand{" +
            "changeListNumberFrom='" + changeListNumberFrom + '\'' +
            ", changeListNumberTo='" + changeListNumberTo + '\'' +
            ", maxChangeLists=" + maxChangeLists +
            ", password='" + password + '\'' +
            ", pathToGitExe='" + pathToGitExe + '\'' +
            ", repositoryPath=" + repositoryPath +
            ", user='" + user + '\'' +
            "} " + super.toString();
  }
}
