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
 * Vault LABEL command
 */
final class VaultLabelCommand extends VaultCommand {

  private final VaultLabelCommandParameters parameters;


  /**
   * Creates VaultLabelCommand that uses system-wide timeout
   * for version control commands
   *
   * @param agent
   */
  public VaultLabelCommand(final Agent agent, final String exePath, final VaultLabelCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, exePath, parameters);
    this.parameters = parameters;
  }


  /**
   * Returns arguments to pass to Vault executable including Vault
   * command and it args.
   */
  protected String getExeArguments() {
    final StringBuilder args = new StringBuilder(100);
    args.append(" label");
    args.append(' ').append(StringUtils.putIntoDoubleQuotes(parameters.getRepositoryPath()));
    args.append(' ').append(parameters.getLabel());
    args.append(' ').append(Integer.toString(parameters.getVersion()));
    return args.toString();
  }
}

/*
D:\mor2\dev\bt>vault help label
<vault>
<usage>
SourceGear Vault Command Line Client 3.1.6.3619
Copyright (c) 2003-2005 SourceGear LLC. All Rights Reserved.

usage: vault.exe LABEL [options] repositorypath labelname [version]

Applies label to version of repositorypath, which can
be used later for GETLABEL requests.  If no version
is specified, the current version is labelled.

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
      RepositoryVO to connect to

This is a list of possible options:
  -comment commentstring
      Checkin comment
</usage>
<result success="yes" />
</vault>
*/