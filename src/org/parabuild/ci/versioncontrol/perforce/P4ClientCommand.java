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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.remote.Agent;

import java.io.ByteArrayInputStream;
import java.io.IOException;


final class P4ClientCommand extends P4Command {

  private final String clientSpec;


  public P4ClientCommand(final Agent agent, final P4Properties props, final String clientSpec) throws AgentFailureException {
    super(agent);
    setP4All(props);
    setClientRequired(false);
    setP4Options("-s");
    setExeArguments(" client -i  ");
    setDescription("client command");
    this.clientSpec = clientSpec;
  }


  /**
   * Callback method - this method is called before execute.
   */
  protected void preExecute() throws IOException {
    super.preExecute();
    setInputStream(new ByteArrayInputStream(clientSpec.getBytes()));
  }
}


