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
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.process.RemoteCommand;
import org.parabuild.ci.process.TimeoutCallback;
import org.parabuild.ci.remote.Agent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class incorporates behaviour common for all version
 * control commands. All version control commands should inherit
 * this class.
 */
public class VersionControlRemoteCommand extends RemoteCommand {

  private static final Log log = LogFactory.getLog(VersionControlRemoteCommand.class);

  /**
   * Maximum number of lines to be accumulated from stderr.
   */
  private static final int MAX_ACCUMULATED_ERROR_LINES = 10;

  /**
   * An StderrLineProcessorto process stderr output.
   */
  private StderrLineProcessor stderrLineProcessor = new DefaultStderrLineProcessor();

  private boolean respectErrorCode = false;

  private boolean showCurrentDirectoryInError = false;


  /**
   * Creates VersionControlRemoteCommand that uses system-wide
   * timeout for version control commands
   *
   * @param agent
   * @param addAutomaticSignatureToEnvironment
   *
   */
  protected VersionControlRemoteCommand(final Agent agent, final boolean addAutomaticSignatureToEnvironment) throws AgentFailureException {
      super(agent, addAutomaticSignatureToEnvironment, getRetryTimes(), getRetryIntervalSecs(), getIgnoreErrorPatterns());
      setTimeoutSecs(SystemConfigurationManagerFactory.getManager().getSystemVCSTimeout() * 60);
      setTimeoutCallback(new VersionControlTimeoutCallback(this));
      if (addAutomaticSignatureToEnvironment) {
        try {
          addEnvironment("PARABUILD_CHECKOUT_DIR", agent.getCheckoutDirName());
        } catch (IOException e) {
          IoUtils.ignoreExpectedException(e);
        }
      }
    showCurrentDirectoryInError = true;
  }


  /**
   * Helper method to remove unencrypted password from a String
   * containing command.
   */
  public static String removePasswordFromDebugString(final String string) {
    if (StringUtils.isBlank(string)) return string;
    final Pattern pattern = Pattern.compile("[-]+[pPyY][\\w]*[\\W]*[\\w]*[\\W]?");
    final Matcher matcher = pattern.matcher(string);
    return matcher.replaceAll("");
  }


  /**
   * If set to true will fail if the error code returned by
   * command execution is not zero.
   *
   * @param respectErrorCode
   */
  protected void setRespectErrorCode(final boolean respectErrorCode) {
    this.respectErrorCode = respectErrorCode;
  }


  protected static void appendCommand(final StringBuffer cmd, final String name, final String value) {
    cmd.append(' ').append(name);
    cmd.append(' ').append(value);
  }


  protected void appendCommand(final StringBuffer cmd, final String name, final int value) {
    appendCommand(cmd, name, Integer.toString(value));
  }


  protected static void appendCommand(final StringBuffer cmd, final String name) {
    cmd.append(' ').append(name);
  }


  protected static void appendCommandIfNotBlank(final StringBuffer cmd, final String name, final String value) {
    if (!StringUtils.isBlank(value)) {
      cmd.append(' ').append(name);
      cmd.append(' ').append(value);
    }
  }


  protected static void appendCommandIfNotBlankQuoted(final StringBuffer cmd, final String name, final String value) {
    if (!StringUtils.isBlank(value)) {
      cmd.append(' ').append(name);
      cmd.append(' ').append(StringUtils.putIntoDoubleQuotes(value));
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
    super.postExecute(resultCode);
//    if (log.isDebugEnabled()) log.debug("analyze error log");
    BufferedReader reader = null;
    try {
      if (getStderrFile().exists() && getStderrFile().length() > 0) {
        final StringBuffer message = new StringBuffer(500);
        reader = new BufferedReader(new FileReader(getStderrFile()));
        String line = reader.readLine();
        int index = 0;
        while (line != null && index < MAX_ACCUMULATED_ERROR_LINES) {
          final int code = stderrLineProcessor.processLine(index, line);
          switch (code) {
            case StderrLineProcessor.RESULT_ADD_TO_ERRORS:
              message.append('\n').append("  ").append(line);
              break;
            case StderrLineProcessor.RESULT_IGNORE: // NOPMD
              // do nothing
              break;
            default:
              log.warn("Unexpected code: " + code);
              break;
          }
          line = reader.readLine();
          index++;
        }

        // trow exception if there are any accumulated messages
        if (message.length() > 0) {
          throw new IOException("Errors while executing command \"" + removePasswordFromDebugString(getCommand()) + ". \nMessage: " + message + '.' + (showCurrentDirectoryInError ? "" : " \nCurrent directory: " + remoteCurrentDir));
        }
      }

      // if we are hear it means nothing has happened
      if (respectErrorCode && resultCode != 0) {
        throw new IOException("Error while executing comand \"" + removePasswordFromDebugString(getCommand()) + "\". The command returned non-zero error code: " + resultCode);
      }
    } finally {
      IoUtils.closeHard(reader);
    }
  }


  /**
   * Sets error output processor. If not set, {@link StderrLineProcessor} is used.
   *
   * @param stderrLineProcessor to set.
   */
  public final void setStderrLineProcessor(final StderrLineProcessor stderrLineProcessor) {
    this.stderrLineProcessor = stderrLineProcessor;
  }


  /**
   * Returns system level retry times on error.
   *
   * @return system level retry times on error.
   */
  private static int getRetryTimes() {
    return SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.RETRY_VCS_COMMAND_TIMES, 1);
  }


  /**
   * Returns system level retry interval on error.
   *
   * @return system level retry interval on error.
   */
  private static int getRetryIntervalSecs() {
    return SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.RETRY_VCS_COMMAND_INTERVAL, 10);
  }


  /**
   * Returns system level retry patterns on error.
   *
   * @return system level retry patterns on error.
   */
  private static List getIgnoreErrorPatterns() {
    return StringUtils.multilineStringToList(SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.RETRY_VCS_COMMAND_PATTERNS, SystemProperty.DEFAULT_RETRY_VCS_COMMAND_PATTERNS));
  }


  /**
   * Version control's command timeout callback. Responsible for
   * reporting timeouts and hangs when a VCS command is
   * executed.
   */
  private static final class VersionControlTimeoutCallback implements TimeoutCallback {

    private final VersionControlRemoteCommand commandToReportOn;


    /**
     * Constructor.
     *
     * @param commandToReportOn VersionControlRemoteCommand that
     *                          this timeout callback will report on in case of time out.
     */
    public VersionControlTimeoutCallback(final VersionControlRemoteCommand commandToReportOn) {
      this.commandToReportOn = commandToReportOn;
    }


    /**
     * This callback method is called when watched command is
     * timed out but before watchdog tries to kill command.
     */
    public void commandTimedOut() {
      final Error error = new Error();
      error.setErrorLevel(Error.ERROR_LEVEL_ERROR);
      error.setBuildID(commandToReportOn.agent.getActiveBuildID());
      error.setHostName(commandToReportOn.getAgentHost().getHost());
      error.setDescription("Version control command timed out");
      error.setDetails("Version control command \"" + removePasswordFromDebugString(commandToReportOn.getCommand()) + "\" has not exited after " + commandToReportOn.getTimeoutSecs() + " seconds. Parabuild will try to stop the command. System may require immediate attention of a build administrator.");
      error.setPossibleCause("Version control system has become unavailable or the timeout value is set too low.");
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    }


    /**
     * This callback method is called when watched command is
     * identified as hung.
     */
    public void commandHung() {
      final Error error = new Error();
      error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
      error.setBuildID(commandToReportOn.agent.getActiveBuildID());
      error.setHostName(commandToReportOn.getAgentHost().getHost());
      error.setDescription("Version control command hung");
      error.setDetails("Version control command \"" + removePasswordFromDebugString(commandToReportOn.getCommand()) + "\" hung after " + commandToReportOn.getTimeoutSecs() + " seconds timeout. System attempted and failed to stop the command. System requires immediate attention of a build administrator. The command should be stopped manually.");
      error.setPossibleCause("Version control system has become unavailable or time out value is set too low.");
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    }
  }


  /**
   * If true a error message will show the current directory.
   *
   * @param showCurrentDirectoryInError
   */
  protected final void setShowCurrentDirectoryInError(final boolean showCurrentDirectoryInError) {
    this.showCurrentDirectoryInError = showCurrentDirectoryInError;
  }
}
