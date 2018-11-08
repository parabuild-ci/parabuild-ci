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
 * Starteam command to execute delete local command command
 * to delete unknown local files at given date.
 */
public final class StarTeamDeleteLocalCommand extends StarTeamCommand {

  private final StarTeamDeleteLocalCommandParameters parameters;


  public StarTeamDeleteLocalCommand(final Agent agent, final StarTeamDeleteLocalCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
    super.setStderrLineProcessor(new StderrLineProcessor() {
      public int processLine(final int index, final String line) {
        if (StringUtils.isBlank(line)
                || line.startsWith("Error occurred:")
                || line.startsWith("The reference view is no longer available. Its root folder has been deleted from the parent view.")) {
          return RESULT_IGNORE;
        } else {
          return RESULT_ADD_TO_ERRORS;
        }
      }
    });
  }


  /**
   * Returns arguments to pass to StarTeam executable including
   * StarTeam command and it args.
   */
  protected String starTeamCommandArguments() throws IOException, AgentFailureException {
    /*

stcmd delete-local -p "projectSpecifier"
[-pwdfile "filePath"] [-cmp] [-csf]
[-encrypt encryptionType]
[-is] [-q] [-x] [-stop]
[-rp "folderPath" | -fp "folderPath" ]
[-cfgl "labelName" | -cfgp "stateName" | -cfgd
"asOfDate"]
[-filter "fileStatus"] [files...]
-cfgd Configures the view as of the specified date/time. Examples
include:
"12/29/01 10:52 AM"
"December 29, 2001 10:52:00 AM PST"
"Monday, December 29, 2001 10:52:00 oclock AM
PST"
-cfgl Configures the view using the specified label. Without -cfgl
or -cfgp or -cfgd, the view�s current configuration is used.
-cfgp Configures the view using the specified promotion state.
-filter Specifies a string of one or more characters, each of which
represents a file status. Never include spaces or other
whitespace in this string. Only files that currently have the
specified statuses will be deleted.
The letters used to represent the statuses are:
The letters used to represent the statuses are:
C for Current, M for Modified, G for Merge, O for Out of Date,
I for Missing, and U for Unknown.


    */
    final StringBuilder sb = new StringBuilder(100);
//    sb.append(" -rp ").append(StringUtils.putIntoDoubleQuotes(projectToWorkingDir(agent, parameters.getProject())));
    sb.append(" -fp ").append(StringUtils.putIntoDoubleQuotes(projectToWorkingDir(agent, parameters.getProject())));
    sb.append(" -cfgd ").append(makeViewConfigDateOption(agent, parameters.getConfigDate()));
    sb.append(" -is");
    sb.append(" -filter ").append('N');
    return sb.toString();
  }


  /**
   * Returns StarTeam command.
   */
  protected String starTeamCommand() {
    return "delete-local";
  }
}
