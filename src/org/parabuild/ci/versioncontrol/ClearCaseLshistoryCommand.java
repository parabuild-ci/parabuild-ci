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
import java.util.Date;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.clearcase.ClearCaseLshistoryCommandStderrProcessor;

/**
 * Executes ClearCase lshistory command.
 */
final class ClearCaseLshistoryCommand extends ClearCaseCommand {

  /**
   * ClearCase field separator.
   */
  public static final String FIELD_SEPARATOR = "~F~";

  /**
   * ClearCase end of query.
   */
  public static final String END_OF_QUERY = "~Q~";

  /**
   * Date to start with.
   */
  private final Date startDate;

  /**
   * Branch to request changes for.
   */
  private final String branch;


  /**
   * Constructor
   *
   * @param agent           agent
   * @param exePath         String absoluteDirPath to cleartool
   * @param absoluteDirPath
   * @param branch
   * @param startDate       Date to start with.
   * @param ignoreLines
   * @throws IOException
   */
  ClearCaseLshistoryCommand(final Agent agent, final String exePath, final String absoluteDirPath, final String branch, final Date startDate, final String ignoreLines) throws IOException, AgentFailureException {
    super(agent, exePath);
    this.startDate = startDate;
    this.branch = branch;
    this.setCurrentDirectory(absoluteDirPath);
    super.setStderrLineProcessor(new ClearCaseLshistoryCommandStderrProcessor(ignoreLines));
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected String getExeArguments() throws IOException, AgentFailureException {
    final String stringSinceDate = ClearCaseConstants.getHistoryDateFormat().format(startDate);
    final StringBuilder sb = new StringBuilder(100);
    final String pcnt = agent.isWindows() ? "%%" : "%";
    sb.append(" lshistory ");
    sb.append(" -nco "); // Excludes checkout version events
    sb.append(" -recurse  "); // Processes the entire subtree below any directory element encountered.
    sb.append(StringUtils.isBlank(this.branch) ? "" : " -branch " + this.branch);
    sb.append(" -since  ").append(stringSinceDate);
    sb.append(" -fmt ").append(pcnt).append('u');
    sb.append(FIELD_SEPARATOR);
    sb.append(pcnt).append("Nd");
    sb.append(FIELD_SEPARATOR);
    sb.append(pcnt).append("En");
    sb.append(FIELD_SEPARATOR);
    sb.append(pcnt).append("Vn");
    sb.append(FIELD_SEPARATOR);
    sb.append(pcnt).append('o');
    sb.append(FIELD_SEPARATOR);
    sb.append(pcnt).append(".1024Nc");
    sb.append(END_OF_QUERY);
    sb.append("\\n");
//    sb.append(agent.getCheckoutDirName());
    return sb.toString();
  }


  public String toString() {
    return "ClearCaseLshistoryCommand{" +
            "startDate=" + startDate +
            ", branch='" + branch + '\'' +
            '}';
  }
}
