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
package org.parabuild.ci.merge;

import org.parabuild.ci.object.BranchChangeList;

import java.util.Date;

/**
 * Implementation of {@link MergeReport}
 */
final class MergeReportImpl implements MergeReport {

  public static final String STRING_STATUS_INTEGRATED = "Integrated";
  public static final String STRING_STATUS_NOT_INTEGRATED = "Not Integrated";
  public static final String STRING_STATUS_UNKNOWN = "Unknown";

  private final byte status;
  private final Date date;
  private final int changeListID;
  private final Integer branchChangeListID;
  private final String description;
  private final String number;
  private final String user;


  MergeReportImpl(final Byte status, final Integer branchChangeListID, final int changeListID, final String number, final String user, final Date date, final String description) {
    this.branchChangeListID = branchChangeListID;
    this.changeListID = changeListID;
    this.date = date;
    this.description = description;
    this.number = number;
    this.status = status;
    this.user = user;
  }


  public String getStringStatus() {
    if (status == BranchChangeList.MERGE_STATUS_MERGED) {
      return STRING_STATUS_INTEGRATED;
    } else if (status == BranchChangeList.MERGE_STATUS_NOT_MERGED) {
      return STRING_STATUS_NOT_INTEGRATED;
    } else {
      return STRING_STATUS_UNKNOWN;
    }
  }


  public byte getStatus() {
    return status;
  }


  public int getChangeListID() {
    return changeListID;
  }


  public int getBranchChangeListID() {
    return branchChangeListID;
  }


  public String getNumber() {
    return number;
  }


  public String getUser() {
    return user;
  }


  public Date getDate() {
    return date;
  }


  public String getDescription() {
    return description;
  }


  public String toString() {
    return "MergeReportImpl{" +
      "status=" + status +
      ", number='" + number + '\'' +
      ", user='" + user + '\'' +
      ", date=" + date +
      ", description='" + description + '\'' +
      '}';
  }
}
