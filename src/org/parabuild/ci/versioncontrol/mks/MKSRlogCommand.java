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
package org.parabuild.ci.versioncontrol.mks;

import java.io.IOException;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * Created by simeshev on Apr 18, 2006 at 3:42:16 PM
 */
class MKSRlogCommand extends MKSCommand {

  private final MKSRlogCommandParameters parameters;


  /**
   * Creates MKSCommand that uses system-wide
   * timeout for version control commands
   *
   * @param agent
   */
  MKSRlogCommand(final Agent agent, final MKSRlogCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
  }


  protected String mksCommand() {
    return "rlog";
  }


  protected String mksCommandArguments() {
    final StringBuffer result = new StringBuffer(100);
    appendCommand(result, "-P", StringUtils.putIntoDoubleQuotes(parameters.getProject()));
    appendCommand(result, "-R");
//    final MKSDateFormat dateFormat = new MKSDateFormat(agent.defaultLocale());
//    appendCommand(result, StringUtils.putIntoDoubleQuotes("--range=" +  dateFormat.formatInput(parameters.getDate())));
    return result.toString();
  }
}
