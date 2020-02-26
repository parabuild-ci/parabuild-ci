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
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.Agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * StarTeamCommand class is responsible for executing StarTeam
 * commands
 */
abstract class StarTeamCommand extends VersionControlRemoteCommand {

  private static final Log log = LogFactory.getLog(StarTeamCommand.class);

  private static final int MAX_ERROR_LINES_TO_REPORT = 10;
  private static final String STR_ENCYPT_FLAG = "-encrypt";
  private final StarTeamCommandParameters parameters;


  public StarTeamCommand(final Agent agent, final StarTeamCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, true);
    super.setCurrentDirectory(agent.getCheckoutDirName());
    super.signatureRegistry.register(remoteCurrentDir);
    this.parameters = parameters;
  }


  /**
   * Returns arguments to pass to StarTeam executable including
   * StarTeam command and it args.
   */
  protected abstract String starTeamCommandArguments() throws IOException, AgentFailureException;


  /**
   * Returns StarTeam command, such a "hist"
   */
  protected abstract String starTeamCommand();


  /**
   * Callback method - this method is called before execute.
   */
  protected void preExecute() throws IOException, AgentFailureException {
    if (StringUtils.isBlank(remoteCurrentDir)) {
      throw new IOException("Error accessing StarTeam: Current directory is undefined.");
    }

    // create project dir
    //
    // NOTE: vimeshev - we use dir name because the
    // directory has not been created yet.
    agent.mkdirs(projectToWorkingDirName(agent, parameters.getProject()));

    // compose command
    final StringBuilder remoteCommand = new StringBuilder(100);
    remoteCommand.append(parameters.getExePath());
    remoteCommand.append(' ');
    remoteCommand.append(starTeamCommand());
    remoteCommand.append(" -stop"); // Halt execution after first error, default is to keep running
    remoteCommand.append(" -x"); // Bypass error messages
    remoteCommand.append(" -nologo"); // Suppress copyright message
    remoteCommand.append(" -p ").append(StringUtils.putIntoDoubleQuotes(parametersToProject(parameters)));
    remoteCommand.append(' ').append(encriptionToOption(parameters.getEncryption()));
    remoteCommand.append(' ').append(starTeamCommandArguments());

    if (log.isDebugEnabled() && StringUtils.systemPropertyEquals("parabuild.starteamcmdd.enabled", "true")) {
      log.debug("command: " + removePasswordFromDebugString(remoteCommand.toString()));
    }
    // execute
    super.setCommand(remoteCommand.toString());
  }


  /**
   * Utility method to calculate
   *
   * @param agent
   * @param project
   * @return
   * @throws IOException
   */
  public static String projectToWorkingDir(final Agent agent, final String project) throws IOException, AgentFailureException {
    // NOTE: vimeshev - 2006-03-08 - we expect that project
    // starts with "/". This means there could be stuations when
    // a path is not a valid OS path because project can contain
    // a view name, and the view name might contain some bad shit.
    return agent.getFileDescriptor(projectToWorkingDirName(agent, project)).getCanonicalPath();
  }


  private static String projectToWorkingDirName(final Agent agent, final String project) throws IOException, AgentFailureException {
    if (project.charAt(0) == '/' || project.charAt(0) == '\\') {
      return agent.getCheckoutDirName() + project;
    } else {
      return agent.getCheckoutDirName() + agent.getSystemProperty("file.separator") + project;
    }
  }


  /**
   * Callback method.
   * <p/>
   * Here we analyze log(s) for errors.
   */
  protected void postExecute(final int resultCode) throws IOException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("analyze StarTeam error log");
    // StarTeam returns non-zero RC if something is wrong
    if (resultCode == 0) return;

    // NOTE: vimeshev - 08/17/2005 - StarTeam uses non-zero return code to
    // report errors and writes error messages to stdout instead of stderr.
    if (getStderrFile().exists() && getStderrFile().length() > 0) {
      super.postExecute(resultCode);
    } else {
      if (getStdoutFile().exists() && getStdoutFile().length() > 0) {
        // process stderr content
        throw new IOException("Errors while executing StarTeam command: " + extractErrorMessage(getStdoutFile()));
      } else {
        // everything is empty
        throw new IOException("Unknown error while executing StarTeam command, no message was provided");
      }
    }
  }


  private StringBuffer extractErrorMessage(final File logFile) throws IOException {
    BufferedReader reader = null;
    try {
      final StringBuffer errorMessage = new StringBuffer(100);
      reader = new BufferedReader(new FileReader(logFile));
      String line = reader.readLine();
      if (line != null) {
        if (line.startsWith("A connection to the StarTeam server could not be established.")) {
          errorMessage.append("Could not connect to StarTeam server using address \"").append(parameters.getAddress()).append("\" and port \"").append(parameters.getPort()).append("\".");
        } else {
          if (line.startsWith("Either the username or password you entered is not valid")) {
            errorMessage.append("Could not connect to StarTeam server using configured user name and password");
          } else {
            // read unindentified error message.
            for (int i = 0; i < MAX_ERROR_LINES_TO_REPORT && line != null; i++) {
              errorMessage.append('\n').append("  ").append(line);
              line = reader.readLine();
              if (log.isDebugEnabled()) log.debug("line = " + line);
            }
          }
        }
      }
      return errorMessage;
    } finally {
      IoUtils.closeHard(reader);
    }
  }


  private static final String parametersToProject(final StarTeamCommandParameters parameters) {
    final StringBuilder sb = new StringBuilder(100);
    sb.append(parameters.getUser());
    sb.append(':');
    sb.append(parameters.getPassword());
    sb.append('@');
    sb.append(parameters.getAddress());
    sb.append(':');
    sb.append(parameters.getPort());
    sb.append(parameters.getProject());
    return StringUtils.putIntoDoubleQuotes(sb.toString());
  }


  private static final String encriptionToOption(final int encryption) {
    final StringBuilder sb = new StringBuilder(100);
    switch (encryption) {
      case SourceControlSetting.STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_ECB:
        sb.append(STR_ENCYPT_FLAG).append(' ').append("RC2_ECB");
        break;
      case SourceControlSetting.STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_CF:
        sb.append(STR_ENCYPT_FLAG).append(' ').append("RC2_CFB");
        break;
      case SourceControlSetting.STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_CBC:
        sb.append(STR_ENCYPT_FLAG).append(' ').append("RC2_CBC");
        break;
      case SourceControlSetting.STARTEAM_ENCRYPTION_RSA_R4_STREAM_CIPHER:
        sb.append(STR_ENCYPT_FLAG).append(' ').append("RC4");
        break;
      default:
        // do nothing
        break;
    }
    return sb.toString();
  }
  /*

==================================================================================
D:\mor2\dev\bt\3rdparty\starteam2005r2>stcmd list -p
StarTeam 8.0 Command Line Interface, Build 8.0.58
Copyright (c) 2003-2004 Borland Software Corporation. All rights reserved.
Using ini file: C:\Documents and Settings\All Users\Application Data\Borland\StarTeam\ConnectionManager.ini

Invalid command line specification:
option requires argument: -p project : "username:password@host:port/project/view/folder/"



 Usage for StarTeam 8.0 list files command:

  stcmd list -?  : Show this usage message

  stcmd list -p project [-pwdfile filename] [-cmp] [-encrypt [RC4, RC2_ECB, RC2_CBC, RC2_CFB]] [-cfgl label | -cfgd date | -cfgp promotion state] [-is
] [-csf] [-filter filter] [-q] [-x] [-stop] [-rp directory | -fp directory] [-?] [-h] [-help] [-nologo] [-cf] [-short] [files...]

    -p project : "username:password@host:port/project/view/folder/"
    -pwdfile filename : Read password from a file
    -cmp : compress data transfer
    -encrypt [RC4, RC2_ECB, RC2_CBC, RC2_CFB] : Encrypt connection
    -cfgl label : Specifies view configuration label
    -cfgd date : Valid Date and Time Formats:
        "3/6/06 12:21 AM"
        "Mar 6, 2006 12:21:46 AM"
        "March 6, 2006 12:21:46 AM PST"
        "Monday, March 6, 2006 12:21:46 AM PST"

    -cfgp promotion state : Specifies view configuration promotion state
    -is : Apply to all sub folders
    -csf : treat folder names as case-sensitive
    -filter filter : Status filter: e.g. "CM" would apply command only to
                     files with status "Current" or "Modified".
                     M=Modified, C=Current, O=Out-of-Date, N=Not-in-View,
                     I=Missing, G=Merge, U=Unknown
    -q : Suppress progress reporting
    -x : Bypass error messages
    -stop : Halt execution after first error, default is to keep running
    -rp directory : Override the view's working directory
    -fp directory : Override the specified folder's working directory
    -? : Invoke help
    -h : Invoke help
    -help : Invoke help
    -nologo : Suppress copyright message
    -cf : Include StarTeam child folders in listing
    -short : short listing format


D:\mor2\dev\bt\3rdparty\starteam2005r2>stcmd list -p test_user:test_password@localhost:49201/test_project -is -nologo -short
C D:\projectory\test_starteam\test_cvs_change_log_with_outside_branch.txt
C D:\projectory\test_starteam\second_sourceline\src\readme.txt
C D:\projectory\test_starteam\sourceline\alwaysvalid\src\readme.txt
==================================================================================
                                                                                                                                                 00:24
  stcmd co -p project [-pwdfile filename] [-cmp] [-encrypt [RC4, RC2_ECB, RC2_CBC, RC2_CFB]] [-cfgl label | -cfgd date | -cfgp promotion state] [-is]
[-csf] [-filter filter] [-q] [-x] [-stop] [-rp directory | -fp directory] [-?] [-h] [-help] [-nologo] [-l | -u | -nel] [-ro | -rw] [-vn number | -vd d
ate | -vl label] [-f NCO] [-o | -i | -merge] [-ts] [-eol [off | on | cr | lf | crlf]] [-fs] [-hook executable] [-dryrun | -alwaysprompt | -neverprompt
 | -conflictprompt] [-mpxCacheAgentThreads number] [-useMPXCacheAgent [ "host:port" | autolocate ]] [files...]

    -p project : "username:password@host:port/project/view/folder/"
    -pwdfile filename : Read password from a file
    -cmp : compress data transfer
    -encrypt [RC4, RC2_ECB, RC2_CBC, RC2_CFB] : Encrypt connection
    -cfgl label : Specifies view configuration label
    -cfgd date : Valid Date and Time Formats:
        "3/6/06 12:23 AM"
        "Mar 6, 2006 12:23:57 AM"
        "March 6, 2006 12:23:57 AM PST"
        "Monday, March 6, 2006 12:23:57 AM PST"

    -cfgp promotion state : Specifies view configuration promotion state
    -is : Apply to all sub folders
    -csf : treat folder names as case-sensitive
    -filter filter : Status filter: e.g. "CM" would apply command only to
                     files with status "Current" or "Modified".
                     M=Modified, C=Current, O=Out-of-Date, N=Not-in-View,
                     I=Missing, G=Merge, U=Unknown
    -q : Suppress progress reporting
    -x : Bypass error messages
    -stop : Halt execution after first error, default is to keep running
    -rp directory : Override the view's working directory
    -fp directory : Override the specified folder's working directory
    -? : Invoke help
    -h : Invoke help
    -help : Invoke help
    -nologo : Suppress copyright message
    -l : Lock the file
    -u : Unlock the file
    -nel : Non-exclusively lock the file
    -ro : Set file read-only after operation
    -rw : Set file read-write after operation
    -vn number : Version number
    -vd date : Valid Date and Time Formats:
        "3/6/06 12:23 AM"
        "Mar 6, 2006 12:23:57 AM"
        "March 6, 2006 12:23:57 AM PST"
        "Monday, March 6, 2006 12:23:57 AM PST"

    -vl label : Version label
    -f NCO : All files needing checkout
    -o : Force checkout
    -i : prompt user to confirm checkout of modified files
    -ts : Set file time stamp to checkout time
    -eol [off | on | cr | lf | crlf] : Specify end-of-line conversion to use on text files (on=platform default)
    -fs : Don't update status
    -merge : merge files on checkout
    -hook executable : uses specfied merge tool
    -dryrun : don't commit merge changes
    -alwaysprompt : always prompt to save merged changes
    -neverprompt : always save merged changes
    -conflictprompt : only prompt to save merge results when no merge conflicts
    -mpxCacheAgentThreads number : Number of threads to be used in MPX Cache Agent. The default value is 3.
    -useMPXCacheAgent [ "host:port" | autolocate ] : Enables checkout from an MPX Cache Agent. Use "host:port" for a specific Cache Agent, or "autoloc
ate" to automatically find the nearest Cache Agent on the network.

 ========================================================================================================

 D:\mor2\dev\bt\3rdparty\starteam2005r2>stcmd label -?
StarTeam 8.0 Command Line Interface, Build 8.0.58
Copyright (c) 2003-2004 Borland Software Corporation. All rights reserved.
Using ini file: C:\Documents and Settings\All Users\Application Data\Borland\StarTeam\ConnectionManager.ini

 Usage for StarTeam 8.0 create new label:

  stcmd label -?  : Show this usage message

  stcmd label -p project [-pwdfile filename] [-cmp] [-encrypt [RC4, RC2_ECB, RC2_CBC, RC2_CFB]] [-q] [-x] [-stop] [-?] [-h] [-help] [-nologo] -nl labe
l [-vd date | -vp promotion | -vl label] [-d description] [-b | -r] [-f]

    -p project : "username:password@host:port/project/view/folder/"
    -pwdfile filename : Read password from a file
    -cmp : compress data transfer
    -encrypt [RC4, RC2_ECB, RC2_CBC, RC2_CFB] : Encrypt connection
    -q : Suppress progress reporting
    -x : Bypass error messages
    -stop : Halt execution after first error, default is to keep running
    -? : Invoke help
    -h : Invoke help
    -help : Invoke help
    -nologo : Suppress copyright message
    -nl label : New label name
    -vd date : Valid Date and Time Formats:
        "3/6/06 12:24 AM"
        "Mar 6, 2006 12:24:39 AM"
        "March 6, 2006 12:24:39 AM PST"
        "Monday, March 6, 2006 12:24:39 AM PST"

    -vp promotion : Promotion state
    -vl label : Version label
    -d description : Label description
    -b : Use as build label
    -r : Use as revision label
    -f : frozen

========================================================================================================
Using the StarTeam CLI, you can run stcmd update-status $st_opts
"$STARTEAM_URL" -cmp $ST_DEST and then stcmd delete-local
$st_opts "$STARTEAM_URL" -cmp $ST_DEST -filter N > /dev/null .
========================================================================================================

  */


  protected static String makeViewConfigDateOption(final Agent agent, final Date date) throws IOException, AgentFailureException {
    return StringUtils.putIntoDoubleQuotes(new StarTeamDateFormat(agent.defaultLocale()).formatInput(date));
  }
}
