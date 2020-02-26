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
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;

/**
 * Vault GET command
 */
final class VaultGetCommand extends VaultCommand {

  private final VaultGetCommandParameters parameters;


  /**
   * Creates VaultGetCommand that uses system-wide timeout
   * for version control commands
   *
   * @param agent
   */
  public VaultGetCommand(final Agent agent, final String exePath, final VaultGetCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, exePath, parameters);
    this.parameters = parameters;
  }


  /**
   * Returns arguments to pass to Vault executable including Vault
   * command and it args.
   */
  protected String getExeArguments() throws IOException, AgentFailureException {
    final String repositoryPath = parameters.getRepositoryPath();
    final String stringDestPath = agent.getCheckoutDirName() + '/' + repositoryPath.substring(2);
    agent.mkdirs(stringDestPath);
    final RemoteFileDescriptor fileDescriptor = agent.getFileDescriptor(stringDestPath);
    final StringBuffer args = new StringBuffer(100);
    args.append(" get");
    args.append(parameters.isMakeWriteable() ? " -makewritable" : " -makereadonly");
    appendCommand(args, "-backup", "no");
    appendCommand(args, "-merge", "overwrite");
    appendCommand(args, "-performdeletions", "removeworkingcopy");
    appendCommand(args, "-setfiletime", "checkin");
    appendCommand(args, "-destpath", StringUtils.putIntoDoubleQuotes(fileDescriptor.getCanonicalPath()));
    args.append(' ').append(StringUtils.putIntoDoubleQuotes(repositoryPath));
    return args.toString();
  }
}

/*
D:\mor2\dev\bt>vault help get
<vault>
<usage>
SourceGear Vault Command Line Client 3.1.6.3619
Copyright (c) 2003-2005 SourceGear LLC. All Rights Reserved.

usage: vault.exe GET [options] repositorypath ...

GET will retrieve the latest version of files or folders in the repository
to the currently defined working folder.  Use SETWORKINGFOLDER if there is
no working folder, or -destpath to retrieve files to a non-working folder.

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
  -backup [yes|no]
      Whether to backup locally modified files before overwriting.  If not
        specified, the user's default value is used.
  -destpath localfolder
      Instead of retrieving files to the currently defined working folder, use
      this folder.  Note this does not update state information and you cannot
      checkin files from a non-working folder.
  -makewritable
      Make all files writable after retrieval
  -makereadonly
      Make all files read-only after retrieval
  -merge [automatic|later|overwrite]
      The action to take when updating a local file with new content.

      automatic*        - attempt to merge changes from the server
      later             - do not overwrite an existing, modified file
      overwrite         - overwrite the local file with the server's file

      * - only applies to GET and GETWILDCARD commands

  -nocloaks
      Performs actions on all folders even if they were previously cloaked
  -norecursive
      Do not act recursively on folders
  -performdeletions [donotremoveworkingcopy|removeworkingcopy|removeworkingcopyifunmodified]
      When getting a folder, this option controls whether files deleted in the
      repository are deleted in the working folder.  The default is
      donotremoveworkingcopy.  This option only applies to GET, GETWILDCARD
      and CHECKOUT commands
  -setfiletime checkin|current|modification
      Sets the time of the local file.

      checkin           - use the last checkin time
      current           - use the current system time
      modification      - use the file's last modified time
  -verbose
      Turn on verbose mode
</usage>
<result success="yes" />
</vault>
*/