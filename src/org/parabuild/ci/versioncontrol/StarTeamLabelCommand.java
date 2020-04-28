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
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.util.StringUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Starteam command to execute checkout command.
 * By default no locks are placed.
 */
public class StarTeamLabelCommand extends StarTeamCommand {

  private final StarTeamLabelCommandParameters parameters;


  public StarTeamLabelCommand(final Agent agent, final StarTeamLabelCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
  }


  /**
   * Returns arguments to pass to StarTeam executable including
   * StarTeam command and it args.
   */
  protected String starTeamCommandArguments() throws IOException, AgentFailureException {
    /*

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
        "3/7/06 11:14 PM"
        "Mar 7, 2006 11:14:55 PM"
        "March 7, 2006 11:14:55 PM PST"
        "Tuesday, March 7, 2006 11:14:55 PM PST"

    -vp promotion : Promotion state
    -vl label : Version label
    -d description : Label description
    -b : Use as build label
    -r : Use as revision label
    -f : frozen

    */
    final StringBuilder checkoutOptions = new StringBuilder(100);
    checkoutOptions.append(" -vd ").append(formatLabelDate(parameters.getLabelDate()));
    checkoutOptions.append(" -nl ").append(parameters.getLabel());
    return checkoutOptions.toString();
  }


  /**
   * Helper method to format checkout date.
   *
   * @param date Date to format
   * @return String reperesentation of the checkout date
   */
  private String formatLabelDate(final Date date) throws IOException, AgentFailureException {
    final StarTeamDateFormat starTeamDateFormat = new StarTeamDateFormat(agent.defaultLocale());
    return StringUtils.putIntoDoubleQuotes(starTeamDateFormat.formatInput(date));
  }


  /**
   * Returns StarTeam command.
   */
  protected String starTeamCommand() {
    return "label";
  }
}
