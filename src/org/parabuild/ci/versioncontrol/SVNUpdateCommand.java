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
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.StringUtils;

import java.io.IOException;

/**
 * Subversion update command.
 */
final class SVNUpdateCommand extends SVNCommand {

  private final RepositoryPath depotPath;
  private final String changeListNumber;
  private final boolean ignoreExternals;


  /**
   * Constructor.
   *
   * @param agent     to run command at.
   * @param exePath   path to svn executable.
   * @param url       SVN depot URL
   * @param depotPath relative path at SVN depot.
   * @throws IOException
   */
  SVNUpdateCommand(final Agent agent, final String exePath, final String url, final RepositoryPath depotPath,
                   final String changeListNumber, final boolean ignoreExternals)
          throws IOException, AgentFailureException {

    super(agent, exePath, url);
    this.changeListNumber = ArgumentValidator.validateArgumentNotBlank(changeListNumber, "change list number");
    this.depotPath = (RepositoryPath) ArgumentValidator.validateArgumentNotNull(depotPath, "depot path");
    this.ignoreExternals = ignoreExternals;
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected final String getExeArguments() throws IOException, AgentFailureException {

    final SVNWorkingDirPathFactory pathFactory = new SVNWorkingDirPathFactory(agent);
    final String workingDir = StringUtils.putIntoDoubleQuotes(pathFactory.makeWorkingDirPath(depotPath.getPath()));
    final StringBuilder result = new StringBuilder(200);
    result.append(" update ");
    result.append(ignoreExternals ? " --ignore-externals " : "");
    result.append(getNOption(depotPath));
    result.append("-r");
    result.append(changeListNumber);
    result.append(' ');
    result.append(workingDir);
    return result.toString();
  }


  public String toString() {
    return "SVNUpdateCommand{" +
            "depotPath=" + depotPath +
            ", changeListNumber='" + changeListNumber + '\'' +
            ", ignoreExternals=" + ignoreExternals +
            "} " + super.toString();
  }
}


