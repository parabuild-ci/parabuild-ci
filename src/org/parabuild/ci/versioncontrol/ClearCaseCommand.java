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
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * ClearCaseCommand class is responsible for executing ClearCase
 * commands. It creates an infrastrcuture for specific ClearCase
 * commands.
 */
abstract class ClearCaseCommand extends VersionControlRemoteCommand {

  private static final Log log = LogFactory.getLog(ClearCaseCommand.class);

  private final String cleartoolExePath;


  public ClearCaseCommand(final Agent agent, final String exePath) throws IOException, AgentFailureException {
    // REVIEWME: simeshev@parabuilci.org -> temporarily turned off the process signature to work on #740
    super(agent, false);

    // set env
    //Map envToAdd = new HashMap(5);
    //envToAdd.put("PARABUILD_CHECKOUT_DIR", agent.getCheckoutDirName());

    // set and register remote current dir in the signature
    super.setCurrentDirectory(agent.getCheckoutDirName());
    super.signatureRegistry.register(remoteCurrentDir);

    // set exe path
    ArgumentValidator.validateArgumentNotBlank(exePath, "path to cleartool executable");
    this.cleartoolExePath = exePath.trim();
    setStderrLineProcessor(new AbstractClearCaseStderrProcessor("") {
      protected int doProcessLine(final int index, final String line) {
        return RESULT_ADD_TO_ERRORS;
      }
    });
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected abstract String getExeArguments() throws IOException, AgentFailureException;


  /**
   * Callback method - this method is called right after call to
   * execute.
   * <p/>
   * Analyzes log for known errors
   *
   * @param resultCode - execute command result code. /**
   *                   Analyzes log for known errors
   * @throws IOException if there are errors contains
   *                     error descripting
   */
  protected void postExecute(final int resultCode) throws IOException, AgentFailureException {
    try {
      super.postExecute(resultCode);
    } finally {
      // DELETEME: when debugging is done
      // print stdout
      BufferedReader br = null;
      try {
        log.debug("command: " + getCommand());
        log.debug("stdout:");
        if (getStdoutFile() != null && getStdoutFile().exists()) {
          if (log.isDebugEnabled()) {
            br = new BufferedReader(new FileReader(getStdoutFile()));
            String line = null;
            while ((line = br.readLine()) != null) {
              log.debug("   line: " + line);
            }
          }
        }
      } catch (final Exception e) {
        log.warn("Error while finalizing command execution", e);
      } finally {
        IoUtils.closeHard(br);
      }
    }
  }


  /**
   * Callback method - this method is called before execute.
   */
  protected void preExecute() throws IOException, AgentFailureException {
    validateRemoteCurrentDir();

    // compose command
    final ClearCaseCommandLineFactory factory = new ClearCaseCommandLineFactory(agent);
    final StringBuffer remoteCommand = factory.makeCommandLine(cleartoolExePath, getExeArguments());
    if (log.isDebugEnabled() && StringUtils.systemPropertyEquals("parabuild.cccmdd.enabled", "true"))
      log.debug("command: " + remoteCommand);
    // execute
    super.setCommand(remoteCommand.toString());
  }


  private void validateRemoteCurrentDir() throws IOException {
    if (StringUtils.isBlank(remoteCurrentDir)) {
      throw new IOException("Error accessing ClearCase: Current directory is undefined.");
    }
  }
}
