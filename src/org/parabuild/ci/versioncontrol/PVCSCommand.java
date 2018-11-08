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
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * Base class for PVCS commands
 */
abstract class PVCSCommand extends VersionControlRemoteCommand {

  private static final Log log = LogFactory.getLog(VaultDepotPathParser.class);

  private final String exePath;
  private final PVCSCommandParameters parameters;


  /**
   * Creates VersionControlRemoteCommand that uses
   * system-wide timeout for version control commands
   *
   * @param agent
   */
  protected PVCSCommand(final Agent agent, final PVCSCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, true);

    // set and register remote current dir in the signature
    super.setCurrentDirectory(agent.getCheckoutDirName());
    super.signatureRegistry.register(remoteCurrentDir);

    // set exe path
    this.exePath = ArgumentValidator.validateArgumentNotBlank(parameters.getPathToClient(), "path to pcli executable").trim();
    this.parameters = parameters;
  }


  /**
   * Returns arguments to pass to PVCS executable with "run"
   * command.
   */
  protected abstract String getRunArguments() throws IOException, AgentFailureException;


  /**
   * Callback method - this method is called before
   * execute.
   */
  protected void preExecute() throws IOException, AgentFailureException {
    super.preExecute();

    // compose commnon command line
    final StringBuffer cmd = new StringBuffer(200);
    cmd.append(StringUtils.putIntoDoubleQuotes(exePath)).append(' ');
    appendCommand(cmd, "run", "");
    appendCommand(cmd, "-q", ""); // quiet
    appendCommand(cmd, "-n", ""); // say no to everything

    // request command-specific details
    cmd.append(' ').append(getRunArguments());
    setCommand(cmd.toString());
//    if (log.isDebugEnabled()) log.debug("cmd.toString(): " + removePasswordFromDebugString(cmd.toString()));
  }


  /**
   * Callback method - this method is called right after
   * call to execute.
   * <p/>
   * This method can be overriden by children to accomodate
   * post-execute processing such as command log analisys
   * e.t.c.
   *
   * @param resultCode - execute command result code.
   */
  protected void postExecute(final int resultCode) throws IOException {
    if (log.isDebugEnabled()) log.debug("analyze error log");
    if (log.isDebugEnabled()) log.debug("resultCode: " + resultCode);
    if (resultCode == 0) return; // do noting
    final StringBuilder message = new StringBuilder(100);
    // traverse stderr
//    if (log.isDebugEnabled()) log.debug("IoUtils.fileToString(getStdoutFile()): " + IoUtils.fileToString(getStdoutFile()));
//    if (log.isDebugEnabled()) log.debug("IoUtils.fileToString(getStderrFile()): " + IoUtils.fileToString(getStderrFile()));
    if (getStderrFile().exists() && getStderrFile().length() > 0) {
      BufferedReader reader = null;
      try {

        reader = new BufferedReader(new FileReader(getStderrFile()));
        reader.readLine(); // skip header
        reader.readLine();
        String line = reader.readLine();
        while (line != null) {
          if (line.startsWith("Warning:")) {
            if (line.endsWith("Did not check out the file because no revision is in the target date range.")) {
              // do nothing
              line = reader.readLine();
              continue;
            }
          } else if (line.contains("Could not find a revision named * in the archive")) {
            // do nothing
            line = reader.readLine();
            continue;
          }
//          if (log.isDebugEnabled()) log.debug("line: " + line);
          message.append('\n').append("  ").append(line);
          line = reader.readLine();
        }
      } finally {
        IoUtils.closeHard(reader);
      }
    }

    // traverse stdout
    if (getStdoutFile().exists() && getStdoutFile().length() > 0) {
      BufferedReader reader = null;
      try {
        // traverse stdout
        reader = new BufferedReader(new FileReader(getStdoutFile()));
        String line = reader.readLine();
        boolean addNextLineToMessage = false;
        while (line != null) {
          if (addNextLineToMessage) {
            message.append('\n').append(line);
            addNextLineToMessage = false; // reset flag
          } else if (line.contains("This command requires one or more projects or versioned items to get")) {
            message.append('\n').append("Project path \"").append(parameters.getProject()).append("\" is invalid or doesn't exist.");
            break;
          } else if (line.contains("[Error]")) {
            addNextLineToMessage = true; // set flag
          }
          line = reader.readLine();
        }

      } finally {
        IoUtils.closeHard(reader);
      }
    }

    // throw exception if there is something to say
    if (message.length() > 0) {
      // REVIEWME: simeshev@parabuilci.org -> remove "command" part when #734 is fixed.
      throw new IOException("Errors while executing PVCS command: " + message + ", command: " + removePasswordFromDebugString(getCommand()));
    }
  }


  protected final String makeUserAndPasswordOption() {
    final StringBuilder args = new StringBuilder(100);
    if (!StringUtils.isBlank(parameters.getUser())) {
      args.append("-id").append(parameters.getUser());
      if (!StringUtils.isBlank(parameters.getPassword())) {
        args.append(':').append(parameters.getPassword());
      }
    }
    return args.toString();
  }
}
