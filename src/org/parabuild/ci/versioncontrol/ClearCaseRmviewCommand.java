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
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * Executes ClearCase rmvew command.
 */
final class ClearCaseRmviewCommand extends ClearCaseCommand {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ClearCaseRmviewCommand.class); // NOPMD


  public ClearCaseRmviewCommand(final Agent agent, final String exePath, final String ignoreLines) throws IOException, AgentFailureException {
    super(agent, exePath);
    super.setCurrentDirectory(agent.getCheckoutDirHome());
    super.setStderrLineProcessor(new AbstractClearCaseStderrProcessor(ignoreLines) {
      protected int doProcessLine(final int index, final String line) {
//        if (line.startsWith("cleartool: Error: Unable to find view by uuid")) return RESULT_IGNORE;
//        if (line.startsWith("cleartool: Error: Unable to remove view")) return RESULT_IGNORE;
//        if (line.startsWith("cleartool: Error: Unable to establish connection to snapshot view") && line.endsWith("ClearCase object not found")) return RESULT_IGNORE;
//        if (line.startsWith("cleartool: Warning: Unable to open snapshot view ")) return RESULT_IGNORE;
        return RESULT_ADD_TO_ERRORS;
      }
    });
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected String getExeArguments() throws IOException, AgentFailureException {
    final String checkoutDirName = agent.getCheckoutDirName();
    final StringBuilder sb = new StringBuilder(100);
    sb.append(" rmview ");
    sb.append(" -force ");
//    sb.append(" -tag ").append(viewTag);
    sb.append(agent.isWindows() ? StringUtils.putIntoDoubleQuotes(checkoutDirName) : checkoutDirName);
    return sb.toString();
  }
}
