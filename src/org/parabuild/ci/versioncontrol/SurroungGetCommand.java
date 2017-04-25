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
import java.util.Date;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * Executes Surround's history report command. This class will
 * get content of Surround repository into the subdirectory with
 * the same name as the repsitory path.
 * <p/>
 * Note that surround does not clean up directories a get
 * operation when a time stamp is used, so whena sync done there
 * generally is need to clean up the whole working directory.
 */
final class SurroungGetCommand extends SurroundCommand {

  private final Date timeStamp;
  private final String branch;
  private final String repository;


  /**
   * Constructor.
   *
   * @param agent
   * @param exePath
   * @param user
   * @param password
   * @param address
   * @param port
   * @param branch
   * @param repository - path relative to the Surround root.
   * @param timeStamp  date to strat logging from. If null, will
   *                   get latest.
   * @throws IOException
   */
  public SurroungGetCommand(final Agent agent, final String exePath,
                            final String user, final String password, final String address, final int port,
                            final String branch, final String repository, final Date timeStamp) throws IOException, AgentFailureException {
    //
    super(agent, exePath, user, password, address, port);
    this.branch = branch;
    this.repository = repository;
    this.timeStamp = timeStamp;
    ArgumentValidator.validateArgumentNotBlank(this.branch, "branch");
    ArgumentValidator.validateArgumentNotBlank(this.repository, "repository");
  }


  /**
   * Returns arguments to pass to Surround executable including
   * Surround command and it args.
   */
  protected String getSurroundCommandArguments() throws IOException, AgentFailureException {
    final String workingDir = agent.getCheckoutDirName() + adjustRepositoryPath(repository);

    final StringBuffer cmd = new StringBuffer(100);
    cmd.append(" get /");
    cmd.append(" \"-p").append(repository).append('\"'); // repository path
    cmd.append(" \"-d").append(workingDir).append('\"'); // working dir
    cmd.append(' ').append(StringUtils.putIntoDoubleQuotes("-b" + branch));
    cmd.append(" -e"); // writable
    cmd.append(" -i"); // include removed files when getting files by label or timestamp
    //cmd.append(" -q"); // quiet
    cmd.append(" -r"); // recursive
    cmd.append(" -tmodify"); // set time stamp to modified
    cmd.append(" -wreplace"); // replace writable
    if (timeStamp != null) {
      cmd.append(" -s").append(getOutputDateFormatter().format(timeStamp));
    }
    return cmd.toString();
  }


  private String adjustRepositoryPath(final String repo) throws IOException, AgentFailureException {

    return agent.isWindows() ? '\\' + repo.replace('/', '\\') : '/' + repo;
  }
}
