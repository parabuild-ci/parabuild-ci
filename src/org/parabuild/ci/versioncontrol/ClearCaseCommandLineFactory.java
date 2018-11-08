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
import org.parabuild.ci.process.RemoteCommand;
import org.parabuild.ci.remote.Agent;

/**
 * Composes ClearCase commands line.
 */
final class ClearCaseCommandLineFactory {

  private static final Log log = LogFactory.getLog(ClearCaseCommandLineFactory.class);

  private final Agent agent;


  /**
   * Constructor.
   *
   * @param agent to prepare commands for.
   */
  public ClearCaseCommandLineFactory(final Agent agent) {
    this.agent = agent;
  }


  public StringBuffer makeCommandLine(final String cleartoolExePathToUse, final String exeArguments) throws IOException, AgentFailureException {
    // TODELETE: when debugging is done
    if (log.isDebugEnabled()) log.debug("cleartoolExePathToUse: " + cleartoolExePathToUse);
    if (log.isDebugEnabled()) log.debug("exeArguments: " + exeArguments);
    // END TODELETE
    final StringBuffer remoteCommand = new StringBuffer(100);
    if (agent.isWindows()) {
      remoteCommand.append(StringUtils.putIntoDoubleQuotes(cleartoolExePathToUse));
      remoteCommand.append(' ');
      remoteCommand.append(exeArguments);
    } else {
      final StringBuilder shSubcommand = new StringBuilder(100);
      shSubcommand.append(StringUtils.removeDoubleQuotes(cleartoolExePathToUse));
      shSubcommand.append(' ');
      shSubcommand.append(exeArguments);
      // finish
      remoteCommand.append(RemoteCommand.STR_SH_C);
      remoteCommand.append(' ');
      remoteCommand.append(StringUtils.putIntoDoubleQuotes(shSubcommand.toString()));
    }
    // TODELETE: when debugging is done
    if (log.isDebugEnabled()) log.debug("remoteCommand: " + remoteCommand);
    // END TODELETE
    return remoteCommand;
  }
}
