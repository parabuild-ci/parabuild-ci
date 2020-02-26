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
package org.parabuild.ci.versioncontrol.perforce;

import java.io.IOException;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.remote.Agent;

/**
 * This class retrieves a branch view according to the
 * branch view name.
 */
final class P4BranchViewRetriever {

  private final P4Properties properties;
  private final Agent agent;


  public P4BranchViewRetriever(final Agent agent, final P4Properties properties) {
    this.properties = new P4Properties(properties);
    this.agent = agent;
  }


  /**
   * Retrieves branch view.
   *
   * @param branchViewName
   * @return {@link String} containing a branch view.
   */
  public P4BranchView retrieveBranchView(final String branchViewName) throws IOException, CommandStoppedException, AgentFailureException {
    P4Command command = null;
    try {
      command = new P4Command(agent);
      command.setP4All(properties);
      command.setClientRequired(false);
      command.setP4Options("-s");
      command.setCurrentDirectory(agent.getTempDirName());
      command.setExeArguments(" branch -o  " + branchViewName);
      command.setDescription("branch command");
      command.execute();
      final P4BranchViewParser branchViewParser = new P4BranchViewParserImpl();
      return branchViewParser.parse(command.getStdoutFile());
    } finally {
      if (command != null) {
        command.cleanup();
      }
    }
  }
}
