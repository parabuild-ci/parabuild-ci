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
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * Executes starteam history command.
 * <p/>
 * <code>
 * <p/>
 * stcmd hist -?
 * StarTeam 8.0 Command Line Interface, Build 8.0.58
 * Copyright (c) 2003-2004 Borland Software Corporation. All rights reserved.
 * Using ini file: C:\Documents and Settings\All Users\Application Data\Borland\StarTeam\ConnectionManager.ini
 * <p/>
 * Usage for StarTeam 8.0 files history command:
 * <p/>
 * stcmd hist -?  : Show this usage message
 * <p/>
 * stcmd hist -p project [-pwdfile filename] [-cmp] [-encrypt [RC4, RC2_ECB, RC2_CBC, RC2_CFB]] [-cfgl label | -cfgd date | -cfgp promotion state] [-is
 * ] [-csf] [-filter filter] [-q] [-x] [-stop] [-rp directory | -fp directory] [-?] [-h] [-help] [-nologo] [files...]
 * <p/>
 * -p project : "username:password@host:port/project/view/folder/"
 * -pwdfile filename : Read password from a file
 * -cmp : compress data transfer
 * -encrypt [RC4, RC2_ECB, RC2_CBC, RC2_CFB] : Encrypt connection
 * -cfgl label : Specifies view configuration label
 * -cfgd date : Valid Date and Time Formats:
 * "3/6/06 12:03 AM"
 * "Mar 6, 2006 12:03:38 AM"
 * "March 6, 2006 12:03:38 AM PST"
 * "Monday, March 6, 2006 12:03:38 AM PST"
 * <p/>
 * -cfgp promotion state : Specifies view configuration promotion state
 * -is : Apply to all sub folders
 * -csf : treat folder names as case-sensitive
 * -filter filter : Status filter: e.g. "CM" would apply command only to
 * files with status "Current" or "Modified".
 * M=Modified, C=Current, O=Out-of-Date, N=Not-in-View,
 * I=Missing, G=Merge, U=Unknown
 * -q : Suppress progress reporting
 * -x : Bypass error messages
 * -stop : Halt execution after first error, default is to keep running
 * -rp directory : Override the view's working directory
 * -fp directory : Override the specified folder's working directory
 * -? : Invoke help
 * -h : Invoke help
 * -help : Invoke help
 * -nologo : Suppress copyright message
 * </code>
 * <p/>
 * <code>
 * stcmd hist -p test_user:test_password@localhost:49201/test_project -is -nologo
 * </code>
 */
public final class StarTeamHistoryCommand extends StarTeamCommand {

  private final StarTeamCommandParameters parameters;


  public StarTeamHistoryCommand(final Agent agent, final StarTeamCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
  }


  /**
   * Returns StarTeam command, such a "hist"
   */
  protected String starTeamCommand() {
    return "hist";
  }


  /**
   * Returns arguments to pass to StarTeam executable including
   * StarTeam command and it args.
   */
  protected String starTeamCommandArguments() throws IOException, AgentFailureException {
    final StringBuilder historyOptions = new StringBuilder(100);
    historyOptions.append(" -is"); // Apply to all sub folders
//    historyOptions.append(" -rp ").append(StringUtils.putIntoDoubleQuotes(projectToWorkingDir(agent, parameters.getProject())));
    historyOptions.append(" -fp ").append(StringUtils.putIntoDoubleQuotes(projectToWorkingDir(agent, parameters.getProject())));
    return historyOptions.toString();
  }
}
