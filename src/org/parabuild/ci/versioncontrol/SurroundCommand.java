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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * SurroundCommand class is responsible for executing Surround
 * commands
 */
abstract class SurroundCommand extends VersionControlRemoteCommand {

  private static final Log log = LogFactory.getLog(SurroundCommand.class);

  private static final int MAX_ERROR_LINES_TO_REPORT = 10;

  private final String address;
  private final String password;
  private final String user;
  private final String exePath;
  private final int port;


  public SurroundCommand(final Agent agent, final String exePath, final String user, final String password, final String address, final int port) throws IOException, AgentFailureException {
    super(agent, true);

    // set and register remote current dir in the signature
    super.setCurrentDirectory(agent.getCheckoutDirName());
    super.signatureRegistry.register(remoteCurrentDir);

    // set params
    this.address = address.trim();
    this.exePath = exePath.trim();
    this.port = port;
    this.user = user.trim();
    this.password = StringUtils.isBlank(password) ? "" : password.trim();

    // validate
    ArgumentValidator.validateArgumentNotBlank(this.exePath, "path to sscm executable");
    ArgumentValidator.validateArgumentNotBlank(this.address, "Surround address");
    ArgumentValidator.validateArgumentNotBlank(this.user, "Surround user");
    ArgumentValidator.validateArgumentGTZero(this.port, "Surround port");
  }


  /**
   * Returns arguments to pass to Surround executable including
   * Surround command and it args.
   */
  protected abstract String getSurroundCommandArguments() throws IOException, AgentFailureException;


  /**
   * Callback method - this method is called before execute.
   */
  protected final void preExecute() throws IOException, AgentFailureException {
    validateRemoteCurrentDir();

    // compose command
    final StringBuilder remoteCommand = new StringBuilder(100);
    remoteCommand.append(exePath);
    remoteCommand.append(' ');
    remoteCommand.append(getSurroundCommandArguments());
    if (!StringUtils.isBlank(user)) {
      remoteCommand.append(" -y");
      remoteCommand.append(user);
      if (!StringUtils.isBlank(password)) {
        remoteCommand.append(':');
        remoteCommand.append(password);
      }
    }
    remoteCommand.append(" -z");
    remoteCommand.append(address);
    remoteCommand.append(':');
    remoteCommand.append(Integer.toString(port));
    remoteCommand.append(' ');


    if (log.isDebugEnabled() && StringUtils.systemPropertyEquals("parabuild.surroundcmdd.enabled", "true"))
      log.debug("command: " + removePasswordFromDebugString(remoteCommand.toString()));
    // execute
    super.setCommand(remoteCommand.toString());
  }


  /**
   * Callback method.
   * <p/>
   * Here we analyze log(s) for errors.
   */
  protected final void postExecute(final int resultCode) throws IOException {
    if (log.isDebugEnabled()) log.debug("analyze surround error log");
    // Surround returns non-zero RC if something is wrong
    if (resultCode == 0) return;

    // NOTE: vimeshev - 08/17/2005 - Surround uses non-zero return code to
    // report errors and writes error messages to stdout instead of stderr.
    if (getStderrFile().exists() && getStderrFile().length() > 0) {
      throw new IOException("Errors while executing Surround command: " + extractErrorMessage(getStderrFile()));
    } else if (getStdoutFile().exists() && getStdoutFile().length() > 0) {
      // process stderr content
      throw new IOException("Errors while executing Surround command: " + extractErrorMessage(getStdoutFile()));
    } else {
      // everything is empty
      throw new IOException("Unknown error while executing Surround command, no message was provided");
    }
  }


  private StringBuffer extractErrorMessage(final File logFile) throws IOException {
    BufferedReader reader = null;
    try {
      final StringBuffer errorMessage = new StringBuffer(100);
      reader = new BufferedReader(new FileReader(logFile));
      String line = reader.readLine();
      if (line != null) {
        if (line.startsWith("A connection to the Surround SCM server could not be established.")) {
          errorMessage.append("Could not connect to Surround SCM server using address \"").append(address).append("\" and port \"").append(Integer.toString(port)).append("\".");
        } else if (line.startsWith("Either the username or password you entered is not valid")) {
          errorMessage.append("Could not connect to Surround SCM server using configured user name and password");
        } else {
          // read unindentified error message.
          for (int i = 0; i < MAX_ERROR_LINES_TO_REPORT && line != null; i++) {
            errorMessage.append('\n').append("  ").append(line);
            line = reader.readLine();
            if (log.isDebugEnabled()) log.debug("line = " + line);
          }
        }
      }
      return errorMessage;
    } finally {
      IoUtils.closeHard(reader);
    }
  }


  private void validateRemoteCurrentDir() throws IOException {
    if (StringUtils.isBlank(remoteCurrentDir)) {
      throw new IOException("Error accessing Surround: Current directory is undefined.");
    }
  }


  public static SimpleDateFormat getOutputDateFormatter() {
    return new SimpleDateFormat("yyyyMMddHH:mm:ss", Locale.US);
  }
}
