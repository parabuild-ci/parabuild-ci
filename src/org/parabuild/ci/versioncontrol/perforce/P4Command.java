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
package org.parabuild.ci.versioncontrol.perforce;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.VersionControlRemoteCommand;

import java.io.IOException;

/**
 * P4Command class is responsible for executing P4 commands
 */
public class P4Command extends VersionControlRemoteCommand {

  private static final Log log = LogFactory.getLog(P4Command.class);

  private static final String UNEXPECTED_ERROR_WHILE_EXECUTING = "Unexpected error while executing ";
  private static final String UNKNOWN_ERROR_WHILE_EXECUTING = "Unknown error while executing ";
  private static final String DEFAULT_COMMAND_DESCRIPTION = "Perforce command";

  public static final String P4PORT = "P4PORT";
  public static final String P4CLIENT = "P4CLIENT";
  public static final String P4USER = "P4USER";
  public static final String P4PASSWD = "P4PASSWD";
  private static final String P4CONFIG = "P4CONFIG";

  private String p4Port = null;
  private String p4Password = null;
  private String p4User = null;
  private String p4Client = null;
  private String p4Exe = null;
  private String p4Options = "";
  private boolean clientRequired = true;
  private String commandDescription = DEFAULT_COMMAND_DESCRIPTION;
  private boolean addPasswordEnvironemntVariable = true;


  public P4Command(final Agent agent) throws AgentFailureException {
    super(agent, true);
    super.signatureRegistry.register(remoteCurrentDir);
  }


  public void setP4Client(final String p4Client) {
    this.p4Client = p4Client;
  }


  public void setP4Options(final String p4Options) {
    this.p4Options = p4Options;
  }


  /**
   * False if execute method should not check if P4CLIENT is
   * provided
   */
  public void setClientRequired(final boolean clientRequired) {
    this.clientRequired = clientRequired;
  }


  /**
   * Sets P4 command properties from P4Properties
   *
   * @param props
   */
  public void setP4All(final P4Properties props) {
    p4Port = props.getP4Port();
    p4User = props.getP4User();
    p4Password = props.getP4Password();
    p4Exe = props.getP4Exe();
    addPasswordEnvironemntVariable = !(props.getAuthenticationMode() == SourceControlSetting.P4_AUTHENTICATION_MODE_VALUE_P4LOGIN);
  }


  /**
   * Callback method - this method is called before execute.
   */
  protected void preExecute() throws IOException {
    // validate state
    if (StringUtils.isBlank(p4Exe)) {
      throw new IOException("Error accessing P4: P4 executable is undefined.");
    }
    if (clientRequired && StringUtils.isBlank(p4Client)) {
      throw new IOException("Error accessing P4: P4 client is undefined.");
    }
    if (StringUtils.isBlank(p4Port)) {
      throw new IOException("Error accessing P4: P4 port is undefined.");
    }
    if (StringUtils.isBlank(p4User)) {
      throw new IOException("Error accessing P4: P4 user is undefined.");
    }
    if (remoteCurrentDir == null) {
      throw new IOException("Error accessing P4: Current directory is undefined.");
    }

    // compose environment
    super.addEnvironment(P4PORT, p4Port);
    super.addEnvironment(P4CLIENT, clientRequired ? p4Client : "");
    super.addEnvironment(P4USER, p4User);
    super.addEnvironment(P4CONFIG, "");
    super.addEnvironment(P4PASSWD, addPasswordEnvironemntVariable ? p4Password : "");
//    if (log.isDebugEnabled() && StringUtils.systemPropertyEquals("parabuild.p4cmdd.enabled", "true")) log.debug("command: " + getCommand());
    if (log.isDebugEnabled()) {
      log.debug("command: " + getCommand());
    }
  }


  /**
   * Callback method - this method is called right after call to
   * execute.
   * <p/>
   * This method can be overriden by children to accomodate
   * post-execute processing such as command log analisys e.t.c.
   *
   * @param resultCode - execute command result code.
   */
  protected void postExecute(final int resultCode) throws IOException, AgentFailureException {
    try {
      super.postExecute(resultCode);

      // analyze the result for errors
      final P4CommandLogAnalyzer logAnalyzer = new P4CommandLogAnalyzer();
      logAnalyzer.setUnexpectedError(UNEXPECTED_ERROR_WHILE_EXECUTING + commandDescription);
      logAnalyzer.setUnknownError(UNKNOWN_ERROR_WHILE_EXECUTING + commandDescription + ". No error message was provided by p4.");
      logAnalyzer.validate(getStdoutFile(), getStderrFile());
    } catch (BuildException e) {
      throw IoUtils.createIOException(e);
    }
  }


  public final void setExeArguments(final String args) {
    final StringBuffer command = new StringBuffer(200);
    command.append(p4Exe);
    command.append(' ').append(p4Options);
    command.append(" -p ").append(p4Port);
    command.append(" -u ").append(p4User);
    command.append(clientRequired ? " -c " + p4Client : "");
    command.append(addPasswordEnvironemntVariable ? " -P " + StringUtils.putIntoDoubleQuotes(p4Password) : "");
    command.append(' ').append(ArgumentValidator.validateArgumentNotBlank(args, "arguments"));
    super.setCommand(command.toString());
  }


  /**
   * Sets optional command description. It is used to form error messages. The default value is
   */
  public void setDescription(final String commandDescription) {
    this.commandDescription = commandDescription;
  }


  public String toString() {
    return "P4Command{" +
            "p4Port='" + p4Port + '\'' +
            ", p4Password='" + p4Password + '\'' +
            ", p4User='" + p4User + '\'' +
            ", p4Client='" + p4Client + '\'' +
            ", p4Exe='" + p4Exe + '\'' +
            ", p4Options='" + p4Options + '\'' +
            ", clientRequired=" + clientRequired +
            ", commandDescription='" + commandDescription + '\'' +
            ", addPasswordEnvironemntVariable=" + addPasswordEnvironemntVariable +
            "} " + super.toString();
  }
}
