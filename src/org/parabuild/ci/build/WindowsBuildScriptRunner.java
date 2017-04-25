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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentEnvironment;

/**
 * Windows implementation of the build script runner
 */
public final class WindowsBuildScriptRunner extends AbstractBuildScriptRunner {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(WindowsBuildScriptRunner.class); // NOPMD


  public WindowsBuildScriptRunner(final Agent agent) {
    super(agent);
  }


  protected String makeCommand(final String scriptFileName) throws IOException, AgentFailureException {
    final String command;
    if (agent.systemType() == AgentEnvironment.SYSTEM_TYPE_WINNT) {
      command = makeNTCommand(scriptFileName);
    } else if (agent.systemType() == AgentEnvironment.SYSTEM_TYPE_WIN95) {
      command = makeWin95Command(scriptFileName);
    } else {
      command = makeNTCommand(scriptFileName);
    }
    return command;
  }


  private String makeWin95Command(final String scriptFileName) {
    return "command /C " + StringUtils.putIntoDoubleQuotes(scriptFileName);
  }


  private String makeNTCommand(final String scriptFileName) {
    return "cmd /C " + StringUtils.putIntoDoubleQuotes(scriptFileName);
  }
}
