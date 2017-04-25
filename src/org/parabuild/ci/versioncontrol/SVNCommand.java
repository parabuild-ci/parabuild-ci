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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * SVNCommand class is responsible for executing SVN commands
 *
 * @noinspection MethodMayBeStatic
 */
abstract class SVNCommand extends VersionControlRemoteCommand {

  private static final Log LOG = LogFactory.getLog(SVNCommand.class); // NOPMD

  private String url = null;
  private String password = null;
  private String user = null;
  private String exePath = null;
  private boolean interactive = false;
  private boolean addTrustServerCert = false;


  protected SVNCommand(final Agent agent, final String exePath, final String url) throws IOException, AgentFailureException {
    super(agent, true);

    // set and register remote current dir in the signature
    super.setCurrentDirectory(agent.getCheckoutDirName());
    super.signatureRegistry.register(remoteCurrentDir);

    // validate
    ArgumentValidator.validateArgumentNotBlank(exePath, "path to svn executable");
    ArgumentValidator.validateArgumentNotBlank(url, "SVN depot URL");

    // set params
    this.exePath = exePath.trim();
    this.url = url.trim();
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   *
   * @return additional exe arguments.
   * @throws IOException           if I/O error occured.
   * @throws AgentFailureException if agent failed.
   */
  protected abstract String getExeArguments() throws IOException, AgentFailureException;


  public final void setPassword(final String password) {
    this.password = password;
  }


  public final void setUser(final String user) {
    this.user = user;
  }


  public final void setAddTrustServerCert(final boolean value) {
    this.addTrustServerCert = value;
  }


  public final String getUrl() {
    return url;
  }


  /**
   * Sets if this command interactive.
   *
   * @param interactive if interactive should be set
   */
  public final void setInteractive(final boolean interactive) {
    this.interactive = interactive;
  }


  /**
   * Returns true if this command interactive. Default is false and non-interactive flag is passed in svn command line.
   *
   * @return true if this command interactive. Default is false and non-interactive flag is passed in svn command line.
   */
  public final boolean isInteractive() {
    return interactive;
  }


  /**
   * Callback method - this method is called right after call to
   * execute.
   * <p/>
   * Analyzes log for known errors
   *
   * @param resultCode - execute command result code. /**
   *                   Analyzes log for known errors
   * @throws IOException if there are errors contains error
   * @noinspection HardcodedLineSeparator
   */
  protected final void postExecute(final int resultCode) throws IOException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("analyze error log");
    }
    BufferedReader reader = null;
    try {
      if (getStderrFile().exists() && getStderrFile().length() > 0) {
        final StringBuffer message = new StringBuffer(100);
        reader = new BufferedReader(new FileReader(getStderrFile()));
        String line = reader.readLine();
        while (line != null) {
          // -- No path in the depot. In this case when calling
          //  "checkout" second_sourceline exists and
          //  second_sourceline/src don't:
          //
          // svn: URL 'svn://localhost:11111/test/second_sourceline/src' doesn't exist
          //
          // -- When updating a directory to a change list number at
          //  that the path did not exist:
          //
          // svn: Cannot replace a directory from within
          //
          // -- Unknown host when called svn://blah_blah_never_existed:11111/
          //
          // svn: Unknown hostname 'blah_blah_never_existed'
          //
          // -- Inaccessible port when connecting to svn://localhost:1234/test/sourceline
          //
          // svn: Can't connect to host 'localhost'
          if (LOG.isDebugEnabled()) {
            LOG.debug("line: " + line);
          }
          message.append('\n').append("  ").append(line);
          line = reader.readLine();
        }
        if (interactive) {
          final String marker = "(R)eject, accept (t)emporarily or accept (p)ermanently?";
          final int markerIndex = message.indexOf(marker);
          if (markerIndex >= 0) {
            // This is message in part is from our attempt to accept the certificate
            // See if there is any thing else
            final int start = markerIndex + marker.length();
            final String leftover = message.substring(start);
            if (leftover.length() > 0) {
              throw new IOException("Errors while executing SVN command: " + leftover + ", command: " + removePasswordFromDebugString(getCommand()));
            } else {
              // No messages aside from the marker
              return;
            }
          }
        }
        // REVIEWME: simeshev@parabuilci.org -> remove "command" part when #734 is fixed.
        throw new IOException("Errors while executing SVN command: " + message + ", command: " + removePasswordFromDebugString(getCommand()));
      }
    } finally {
      IoUtils.closeHard(reader);
    }
  }


  /**
   * Callback method - this method is called before execute.
   */
  protected final void preExecute() throws IOException, AgentFailureException {
    validateRemoteCurrentDir();

    // compose environment
    super.addEnvironment("SVNPORT", StringUtils.putIntoDoubleQuotes(url));
    super.addEnvironment("SVNUSER", user);
    super.addEnvironment("SVNPASSWD", StringUtils.putIntoDoubleQuotes(password));

    // compose command
    final StringBuffer remoteCommand = new StringBuffer(100);
    remoteCommand.append(exePath);
    if (!interactive) {
      remoteCommand.append(' ');
      remoteCommand.append("--non-interactive");
    }
    if (addTrustServerCert) {
      remoteCommand.append(' ');
      remoteCommand.append("--trust-server-cert");
    }
    if (!StringUtils.isBlank(user)) {
      remoteCommand.append(' ');
      remoteCommand.append("--username");
      remoteCommand.append(' ');
      remoteCommand.append(user);
      if (!StringUtils.isBlank(password)) {
        remoteCommand.append(' ');
        remoteCommand.append("--password");
        remoteCommand.append(' ');
        remoteCommand.append(password);
      }
    }
    remoteCommand.append(' ');
    remoteCommand.append(getExeArguments());


    if (LOG.isDebugEnabled() && StringUtils.systemPropertyEquals("parabuild.svncmdd.enabled", "true")) {
      LOG.debug("command: " + remoteCommand);
    }
    // execute
    super.setCommand(remoteCommand.toString());
  }


  private void validateRemoteCurrentDir() throws IOException {
    if (StringUtils.isBlank(remoteCurrentDir)) {
      throw new IOException("Error accessing SVN: Current directory is undefined.");
    }
  }


  protected final String getNOption(final RepositoryPath depotPath) {
    if (depotPath.getOptions().isEmpty()) {
      return " ";
    }
    return ' ' + (String) depotPath.getOptions().get(0) + ' '; // That should be N option
  }


  public String toString() {
    return "SVNCommand{" +
            "exePath='" + exePath + '\'' +
            ", interactive=" + interactive +
            ", password='" + password + '\'' +
            ", url='" + url + '\'' +
            ", user='" + user + '\'' +
            "} " + super.toString();
  }
}
