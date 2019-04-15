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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * Vault VERSIONHISTORY command
 */
final class VaultVersionHistoryCommand extends VaultCommand {

  private final VaultVersionHistoryCommandParameters historyParams;


  /**
   * Creates VaultHistoryCommand that uses system-wide timeout
   * for version control commands
   *
   * @param agent
   */
  public VaultVersionHistoryCommand(final Agent agent, final String exePath, final VaultVersionHistoryCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, exePath, parameters);
    this.historyParams = parameters;
  }


  /**
   * Returns arguments to pass to Vault executable including
   * Vault command and it args.
   */
  protected String getExeArguments() throws IOException, AgentFailureException {
    final VaultDateFormat format = new VaultDateFormat(agent.defaultLocale());
    final String beginDateString = historyParams.getBeginDate() != null ? StringUtils.putIntoDoubleQuotes(format.formatInput(historyParams.getBeginDate())) : null;
    final String endDateString = historyParams.getEndDate() != null ? StringUtils.putIntoDoubleQuotes(format.formatInput(historyParams.getEndDate())) : null;
    final StringBuffer args = new StringBuffer(100);
    args.append(" versionhistory");
    appendCommand(args, "-rowlimit", historyParams.getRowLimit());
    appendCommandIfNotBlank(args, "-begindate", beginDateString);
    appendCommandIfNotBlank(args, "-enddate", endDateString);
    args.append(' ').append(historyParams.getRepositoryPath());
    return args.toString();
  }
}
/*
D:\mor2\dev\bt>vault help versionhistory
<vault>
<usage>
SourceGear Vault Command Line Client 3.1.6.3619
Copyright (c) 2003-2005 SourceGear LLC. All Rights Reserved.

usage: vault.exe VERSIONHISTORY [options] repositoryfolder

VERSIONHISTORY will display all versions of a folder in the
repository specified by repositoryfolder.

Server and authentication information is specified by:
  -host host
      Hostname of the vault server to connect to. Can also use "-server"
  -ssl
      Enables SSL for server connection
  -user username
      Username to use when connecting to server.
  -password password
      Password to use when connecting to server
  -proxyserver proxyserver
      Server name or url for the proxy to use when connecting.
  -proxyport proxyport
      Port to use to connect to the proxy.
  -proxyuser proxyuser
      Username for proxy authentication.
  -proxypassword proxypassword
      Password for proxy authentication.
  -proxydomain proxydomain
      Domain for proxy authentication.
  -repository repositoryname
      Repository to connect to

This is a list of possible options:
  -rowlimit limitnumber
      Limits the number of rows returned for a history query to
      limitnumber
  -begindate local date [ time]
      Date to begin history at
  -enddate local date [ time]
      Date to end history display at
  -beginversion versionnumber
      The version number from which version history should begin.
</usage>
<result success="yes" />
</vault>
*/