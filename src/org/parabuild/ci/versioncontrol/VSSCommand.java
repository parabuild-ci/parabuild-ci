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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * VSSCommand is a helper class to execute VSS commands and to
 * return resulting files  for stdout and stderr.
 * <p/>
 * VSSCommand accepts "meaningful" part of the whole command line
 * and uses information provided in properties to compose the
 * "prefix".
 */
final class VSSCommand extends VersionControlRemoteCommand {

  private static final Log log = LogFactory.getLog(VSSCommand.class);

  private String execPath = null;
  private String password = null;
  private String userName = null;
  private String vssCommand = null;


  /**
   * Constructor, accepts StringBuffer as a command
   *
   * @param agent      to execute the command against.
   * @param execPath   path to SS executable
   * @param userName   VSS user name
   * @param password   VSS password
   * @param vssCommand VSS part of the command
   * @throws IOException if agent checkout dir does not
   *                     exist.
   */
  public VSSCommand(final Agent agent, final String execPath, final String databasePath,
                    final String userName, final String password, final String vssCommand) throws IOException, AgentFailureException {
    super(agent, true);
    if (StringUtils.isBlank(password)) throw new IllegalArgumentException("VSS password is not set");
    if (StringUtils.isBlank(userName)) throw new IllegalArgumentException("VSS user name is not set");
    this.execPath = execPath;
    this.userName = userName;
    this.password = password;
    this.vssCommand = vssCommand;
    setCurrentDirectory(agent.getCheckoutDirName());
    setInputStream(new ByteArrayInputStream("\n\n".getBytes())); // this will create remote file to hold two line breaks that will
    setStderrLineProcessor(new StderrLineProcessor() { // TODO: Move error processing from VSS to a StderrLineProcessor specific to VSS


      public int processLine(final int index, final String line) {
        return StderrLineProcessor.RESULT_IGNORE;
      }
    });
    super.addEnvironment("PARABUILD_CHECKOUT_DIR", this.agent.getCheckoutDirName());
    super.addEnvironment("SSDIR", databasePath);
    super.signatureRegistry.register(this.agent.getCheckoutDirName());
  }


  /**
   * Callback method - this method is called before execute.
   */
  protected void preExecute() throws IOException, AgentFailureException {

    // pre-validation
    if (remoteCurrentDir == null) throw new IllegalArgumentException("VSS checkout directory can not be null");
    if (!agent.checkoutDirExists()) {
      throw new IllegalArgumentException("VSS checkout directory \"" + remoteCurrentDir + "\" doesn't exist");
    }

    // create wrapper
    // REVIEWME: simeshev@parabuilci.org -> should be a factory
    final String fullCommand = makeFullCommand();
    setCommand(fullCommand);

    // post-validation
    if (StringUtils.isBlank(getCommand())) throw new IllegalArgumentException("VSS command is blank");
    if (log.isDebugEnabled() && StringUtils.systemPropertyEquals("parabuild.vsscmdd.enabled", "true"))
      log.debug("command: " + fullCommand);
  }


  /**
   * Creates full command. Full command is a command ready to
   * execute.
   *
   * @return resulting command ready to execute.
   */
  private String makeFullCommand() {
    if (StringUtils.isBlank(vssCommand)) throw new IllegalStateException("VSS command is not set");
    final StringBuffer result = new StringBuffer(100);
    result.append(execPath);
    result.append(' ');
    result.append(vssCommand);
    result.append(" -Y").append(userName).append(',').append(password); // name,password
    result.append(" -I-N"); // don not ask for input
    // result.append(" -C-"); // no comment is used
    // result.append(" -S"); // enable smart mode
    // result.append(" -W"); // local files are made writable
    return result.toString();
  }


  /**
   * Removes temp files
   */
  public final void cleanup() throws AgentFailureException {
    super.cleanup();
  }
}
