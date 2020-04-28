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
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Vault history command
 */
final class VaultHistoryCommand extends VaultCommand {

  private final VaultHistoryCommandParameters historyParams;


  /**
   * Creates VaultHistoryCommand that uses system-wide timeout
   * for version control commands
   *
   * @param agent
   */
  public VaultHistoryCommand(final Agent agent, final String exePath, final VaultHistoryCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, exePath, parameters);
    this.historyParams = parameters;
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected String getExeArguments() throws IOException, AgentFailureException {
    final DateFormat format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.MEDIUM, agent.defaultLocale());
    final String beginDateString = historyParams.getBeginDate() != null ? format.format(historyParams.getBeginDate()) : null;
    final String endDateString = historyParams.getEndDate() != null ? format.format(historyParams.getEndDate()) : null;
    final StringBuffer args = new StringBuffer(100);
    args.append(" history");
    appendCommand(args, "-excludeactions", "label,snapshot,branch");
    appendCommandIfNotBlank(args, "-begindate", beginDateString == null ? null : StringUtils.putIntoDoubleQuotes(beginDateString));
    appendCommandIfNotBlank(args, "-rowlimit", historyParams.getRowLimit() == Integer.MAX_VALUE ? "" : Integer.toString(historyParams.getRowLimit()));
    appendCommandIfNotBlank(args, "-enddate", endDateString);
    args.append(' ').append(historyParams.getRepositoryPath());
    return args.toString();
  }
}

/*
This is a list of possible options:
  -rowlimit limitnumber
      Limits the number of rows returned for a history query to
      limitnumber
  -datesort [asc | desc]
      Sort the history results in ascending or descending date order.
  -begindate local date [ time]
      Date to begin history at
  -beginlabel labelstring
      A Label that was applied to the target of the history query.
        The date of this label will be used to determine the
        start point of the history query.
  -enddate local date [ time]
      Date to end history display at
  -endlabel labelstring
      A Label that was applied to the target of the history query.
        The date of this label will be used to determine the
        end point of the history query.
  -norecursive
      Do not act recursively on folders
  -excludeactions action,action,...
      A comma-separated list of actions that will be excluded from
        the history query.  Valid actions to exclude are:
        add, branch, checkin, create, delete, label, move, obliterate, pin,
        propertychange, rename, rollback, share, snapshot, undelete
  -excludeusers user,user,...
      A comma-separated list of actions that will be excluded from
        the history query.
*/