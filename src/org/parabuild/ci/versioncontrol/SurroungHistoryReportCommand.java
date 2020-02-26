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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * Executes Surround's history report command.
 */
final class SurroungHistoryReportCommand extends SurroundCommand {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(SurroungHistoryReportCommand.class); // NOPMD

  private final Date startDate;
  private final String branch;
  private final String repository;
  private final Date endDate;


  /**
   * Constructor.
   *
   * @param agent
   * @param exePath
   * @param user
   * @param password
   * @param address
   * @param port
   * @param startDate date to strat logging from. If null, will
   *                  log all changes.
   * @param endDate
   * @throws IOException
   */
  public SurroungHistoryReportCommand(final Agent agent, final String exePath,
                                      final String user, final String password, final String address, final int port, final String branch,
                                      final String repository, final Date startDate, final Date endDate) throws IOException, AgentFailureException {

    super(agent, exePath, user, password, address, port);
    this.branch = branch;
    this.repository = repository;
    this.startDate = startDate;
    this.endDate = adjustEndDate(endDate);
    ArgumentValidator.validateArgumentNotBlank(this.branch, "branch");
    ArgumentValidator.validateArgumentNotBlank(this.repository, "repository");
  }


  /**
   * Returns arguments to pass to Surround executable including
   * Surround command and it args.
   */
  protected String getSurroundCommandArguments() {
    final StringBuilder cmd = new StringBuilder(100);
    cmd.append(" rh ").append(StringUtils.putIntoDoubleQuotes(repository));
    cmd.append(' ').append(StringUtils.putIntoDoubleQuotes("-b" + branch));
    cmd.append(" -fSpaces");
    cmd.append(" -qSummary");
    cmd.append(" -x0"); // no diffs
    if (startDate != null) {
      final SimpleDateFormat outputDateFormatter = getOutputDateFormatter();
      cmd.append(" -d").append(outputDateFormatter.format(startDate));
      cmd.append('-').append(outputDateFormatter.format(endDate));
    }
    return cmd.toString();
  }


  public String toString() {
    return "SurroungHistoryReportCommand{" +
            "startDate=" + startDate +
            ", branch='" + branch + '\'' +
            ", repository='" + repository + '\'' +
            ", endDate=" + endDate +
            '}';
  }


  /**
   * Helper method to adust end date if null.
   *
   * @param endDate
   */
  private static Date adjustEndDate(final Date endDate) {
    if (endDate == null) { // set end date to "unlimited" if null
      final Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(Calendar.YEAR, 9999);
      return calendar.getTime();
    } else {
      return endDate;
    }
  }
}
