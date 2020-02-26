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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;
import java.util.Date;

/**
 * Starteam command to execute checkout command.
 * By default no locks are placed.
 */
public class StarTeamCheckoutCommand extends StarTeamCommand {

  private final StarTeamCheckoutCommandParameters parameters;


  public StarTeamCheckoutCommand(final Agent agent, final StarTeamCheckoutCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
  }


  /**
   * Returns arguments to pass to StarTeam executable including
   * StarTeam command and it args.
   */
  protected String starTeamCommandArguments() throws IOException, AgentFailureException {
    /*
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

    */
    final StringBuilder checkoutOptions = new StringBuilder(100);
    checkoutOptions.append(" -is"); // Apply to all sub folders
    checkoutOptions.append(" -ro"); // Set file read-only after operation
//    checkoutOptions.append(" -o"); // Force checkout
    checkoutOptions.append(" -u"); // Unlock the file
    checkoutOptions.append(" -eol ").append(eolToOption(parameters));
//    checkoutOptions.append(" -rp ").append(StringUtils.putIntoDoubleQuotes(projectToWorkingDir(agent, parameters.getProject())));
    checkoutOptions.append(" -fp ").append(StringUtils.putIntoDoubleQuotes(projectToWorkingDir(agent, parameters.getProject())));
    if (parameters.getDate() != null) {
      checkoutOptions.append(" -vd ").append(formatCheckoutDate(parameters.getDate()));
    }
    return checkoutOptions.toString();
  }


  /**
   * Helper method to format checkout date.
   *
   * @param date Date to format
   * @return String reperesentation of the checkout date
   */
  private String formatCheckoutDate(final Date date) throws IOException, AgentFailureException {
    final StarTeamDateFormat starTeamDateFormat = new StarTeamDateFormat(agent.defaultLocale());
    return StringUtils.putIntoDoubleQuotes(starTeamDateFormat.formatInput(date));
  }


  /**
   * Helper method to trasform EOL option to a String suitable
   * for use in StarTeam command line.
   *
   * @param parameters to transform
   * @return EOL option in a String format suitable for use in
   *         StarTeam command line.
   */
  private static String eolToOption(final StarTeamCheckoutCommandParameters parameters) {
    if (parameters.getEolConversion() == VCSAttribute.STARTEAM_EOL_CR) return "cr";
    if (parameters.getEolConversion() == VCSAttribute.STARTEAM_EOL_CRLF) return "crlf";
    if (parameters.getEolConversion() == VCSAttribute.STARTEAM_EOL_LF) return "lf";
    if (parameters.getEolConversion() == VCSAttribute.STARTEAM_EOL_OFF) return "off";
    if (parameters.getEolConversion() == VCSAttribute.STARTEAM_EOL_ON) return "on";
    return "on";
  }


  /**
   * Returns StarTeam command.
   */
  protected String starTeamCommand() {
    return "co";
  }
}
