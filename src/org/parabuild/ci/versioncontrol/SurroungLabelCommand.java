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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * Executes Surround's label report command.
 */
final class SurroungLabelCommand extends SurroundCommand {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(SurroungLabelCommand.class); // NOPMD

  private final String label;
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
   * @param label    label to set
   * @throws IOException
   */
  public SurroungLabelCommand(final Agent agent, final String exePath,
                              final String user, final String password, final String address, final int port, final String branch,
                              final String repository, final String label) throws IOException, AgentFailureException {

    super(agent, exePath, user, password, address, port);
    this.branch = branch;
    this.repository = repository;
    this.label = label;
    ArgumentValidator.validateArgumentNotBlank(this.branch, "branch");
    ArgumentValidator.validateArgumentNotBlank(this.repository, "repository");
    ArgumentValidator.validateArgumentNotBlank(this.label, "label");
  }


  /**
   * Returns arguments to pass to Surround executable including
   * Surround command and it args.
   */
  protected String getSurroundCommandArguments() {
    final StringBuilder cmd = new StringBuilder(100);
    cmd.append(" label /");
    cmd.append(" \"-p").append(repository).append('\"'); // repository path
    cmd.append(' ').append(StringUtils.putIntoDoubleQuotes("-b" + branch));
    cmd.append(" -l").append(label);
    cmd.append(" -r"); // recursive
    cmd.append(" -o"); // replace current label
    cmd.append(" -c-"); // no comments
    return cmd.toString();
  }
}
