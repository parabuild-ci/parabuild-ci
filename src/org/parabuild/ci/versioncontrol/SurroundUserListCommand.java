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

import java.io.IOException;

/**
 * Lists Surround users to stdout file.
 */
final class SurroundUserListCommand extends SurroundCommand {

  /**
   * Constructor.
   */
  public SurroundUserListCommand(final Agent agent, final String exePath,
                                 final String user, final String password, final String address, final int port) throws IOException, AgentFailureException {
    super(agent, exePath, user, password, address, port);
  }


  protected String getSurroundCommandArguments() {
    return " lsuser -e "; // list users including e-mails
  }
}
