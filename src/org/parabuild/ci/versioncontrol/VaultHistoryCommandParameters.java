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

import java.util.Date;

/**
 * Parameters for Vault history command.
 */
public final class VaultHistoryCommandParameters extends VaultCommandParameters {

  private int rowLimit = Integer.MAX_VALUE;
  private Date beginDate;
  private Date endDate;
  private String repositoryPath;


  public int getRowLimit() {
    return rowLimit;
  }


  public void setRowLimit(final int rowLimit) {
    this.rowLimit = rowLimit;
  }


  public Date getBeginDate() {
    return beginDate;
  }


  public void setBeginDate(final Date beginDate) {
    this.beginDate = beginDate;
  }


  public Date getEndDate() {
    return endDate;
  }


  public void setEndDate(final Date endDate) {
    this.endDate = endDate;
  }


  public String getRepositoryPath() {
    return repositoryPath;
  }


  public void setRepositoryPath(final String repositoryPath) {
    this.repositoryPath = repositoryPath;
  }


  public String toString() {
    return "VaultHistoryCommandParameters{" +
      super.toString() +
      "rowLimit=" + rowLimit +
      ", beginDate=" + beginDate +
      ", endDate=" + endDate +
      ", repositoryPath=" + repositoryPath +
      '}';
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