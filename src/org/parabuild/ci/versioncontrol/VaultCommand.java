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
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * Base class for all vault commands.
 */
abstract class VaultCommand extends VersionControlRemoteCommand {

  private static final Log log = LogFactory.getLog(VaultDepotPathParser.class);

  private final VaultCommandParameters parameters;
  private final String exePath;


  /**
   * Creates VersionControlRemoteCommand that uses system-wide
   * timeout for version control commands
   *
   * @param agent
   */
  public VaultCommand(final Agent agent, final String exePath, final VaultCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, true);
    this.parameters = parameters;

    // set and register remote current dir in the signature
    super.setCurrentDirectory(agent.getCheckoutDirName());
    super.signatureRegistry.register(remoteCurrentDir);

    // set exe path
    ArgumentValidator.validateArgumentNotBlank(exePath, "path to Vault.exe executable");
    this.exePath = exePath.trim();
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected abstract String getExeArguments() throws IOException, AgentFailureException;


  /**
   * Callback method - this method is called before execute.
   */
  protected void preExecute() throws IOException, AgentFailureException {
    super.preExecute();

    // compose commnon command line
    final StringBuffer cmd = new StringBuffer(200);
    cmd.append(StringUtils.putIntoDoubleQuotes(exePath));
    if (parameters.getUseSSL()) appendCommand(cmd, " -ssl", "");
    appendCommand(cmd, "-host", StringUtils.putIntoDoubleQuotes(parameters.getHost()));
    appendCommand(cmd, "-user", StringUtils.putIntoDoubleQuotes(parameters.getUser()));
    appendCommand(cmd, "-password", StringUtils.putIntoDoubleQuotes(parameters.getPassword()));
    appendCommand(cmd, "-repository", StringUtils.putIntoDoubleQuotes(parameters.getRepository()));
    appendCommandIfNotBlank(cmd, "-proxyserever", parameters.getProxyServer());
    appendCommandIfNotBlank(cmd, "-proxyport", parameters.getProxyPort());
    appendCommandIfNotBlank(cmd, "-proxyuser", parameters.getProxyUser());
    appendCommandIfNotBlank(cmd, "-proxypassword", StringUtils.isBlank(parameters.getProxyPassword()) ? null : StringUtils.putIntoDoubleQuotes(parameters.getProxyPassword()));
    appendCommandIfNotBlank(cmd, "-proxydomain", parameters.getProxyDomain());

    // request command-specific details
    cmd.append(' ').append(getExeArguments());
    setCommand(cmd.toString());
    if (log.isDebugEnabled()) log.debug("cmd.toString(): " + removePasswordFromDebugString(cmd.toString()));
  }


}

/*
  -host host
      Hostname of the vault server to connect to. Can also use
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
*/