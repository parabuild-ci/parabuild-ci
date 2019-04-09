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
package org.parabuild.ci.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentEnvironment;

import java.io.IOException;

/**
 * Unix implementation of the build script runner
 */
public final class UnixBuildScriptRunner extends AbstractBuildScriptRunner {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(UnixBuildScriptRunner.class); // NOPMD


  public UnixBuildScriptRunner(final Agent agent) {
    super(agent);
  }


  /**
   * Executes build script
   *
   * @param scriptFileName to execute
   */
  public String makeCommand(final String scriptFileName) throws IOException, AgentFailureException {
    String command = null;
    if (agent.systemType() == AgentEnvironment.SYSTEM_TYPE_CYGWIN) {
      command = makeCYGWINCommand(scriptFileName);
    } else {
      command = makeUnixCommand(scriptFileName);
    }
    return command;
  }


  String pathVarName() {
    return "PATH";
  }


  private String makeCYGWINCommand(final String scriptFileName) {
    return "sh " + scriptFileName;
  }


  private String makeUnixCommand(final String scriptFileName) {
    return "sh " + scriptFileName;
  }
}
