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

import java.io.*;
import java.util.*;

import org.parabuild.ci.object.Merge;
import org.parabuild.ci.object.MergeChangeList;

/**
 * Merge queue report.
 */
public final class MergeQueueReportImpl implements MergeQueueReport, Serializable {

  private static final long serialVersionUID = -1961237974196089324L;

  private final boolean validated;
  private final byte mergeChangeListResultID;
  private final byte mergeResultID;
  private final Date createdAt;
  private final int changeListID;
  private final int mergeChangeListID;
  private final int mergeID;
  private final Integer targetBuildRunID;
  private final String changeListNumber;
  private final String description;
  private final String mergeChangeListResultDescription;
  private final String user;


  public MergeQueueReportImpl(final int mergeID, final byte mergeResultID, final int mergeChangeListID,
                              final byte mergeChangeListResult, final String mergeChangeListResultDescription,
                              final int changeListID, final String changeListNumber, final String user,
                              final Date createdAt, final String description, final Integer targetBuildRunID,
                              final boolean validated) {

    this.changeListID = changeListID;
    this.changeListNumber = changeListNumber;
    this.createdAt = createdAt;
    this.description = description;
    this.mergeChangeListID = mergeChangeListID;
    this.mergeChangeListResultDescription = mergeChangeListResultDescription;
    this.mergeChangeListResultID = mergeChangeListResult;
    this.mergeID = mergeID;
    this.mergeResultID = mergeResultID;
    this.targetBuildRunID = targetBuildRunID;
    this.user = user;
    this.validated = validated;
  }


  /**
   * @return merge ID
   */
  public int getMergeID() {
    return mergeID;
  }


  /**
   * @return {@link Merge} result ID
   *
   * @see Merge#RESULT_CANNOT_VALIDATE
   * @see Merge#RESULT_CONFLICTS
   * @see Merge#RESULT_NOT_MERGED
   * @see Merge#RESULT_VALIDATION_FAILED
   */
  public byte getMergeResultID() {
    return mergeResultID;
  }


  /**
   * @return String representation of {@link Merge} result ID
   *
   * @see Merge#RESULT_CANNOT_VALIDATE
   * @see Merge#RESULT_CONFLICTS
   * @see Merge#RESULT_NOT_MERGED
   * @see Merge#RESULT_VALIDATION_FAILED
   */
  public String getStringMergeResult() {
    if (mergeResultID == Merge.RESULT_CANNOT_VALIDATE) {
      return "Cannot validate";
    } else if (mergeResultID == Merge.RESULT_CONFLICTS) {
      return "Merge conflicts";
    } else if (mergeResultID == Merge.RESULT_NOT_MERGED) {
      return "Not merged";
    } else if (mergeResultID == Merge.RESULT_VALIDATION_FAILED) {
      return "Validation failed";
    }
    return "Unknown mergeResultID code";
  }


  /**
   * @return optional validation build run.
   */
  public Integer getTargetBuildRunID() {
    return targetBuildRunID;
  }


  /**
   * @return true if merge verified
   */
  public boolean isValidated() {
    return validated;
  }


  public int getMergeChangeListID() {
    return mergeChangeListID;
  }


  public byte getMergeChangeListResultID() {
    return mergeChangeListResultID;
  }


  public String getStringMergeChangeListResult() {
    if (mergeChangeListResultID == MergeChangeList.RESULT_SUCCESS) {
      return "Success";
    } else if (mergeChangeListResultID == MergeChangeList.RESULT_NOT_MERGED) {
      return "Not merged";
    } else if (mergeChangeListResultID == MergeChangeList.RESULT_NOTHING_TO_MERGE) {
      return "Nothing to merge";
    } else if (mergeChangeListResultID == MergeChangeList.RESULT_CONFLICTS) {
      return "Conflicts";
    }
    return "Unknown mergeChangeListResultID code";
  }


  public String getMergeChangeListResultDescription() {
    return mergeChangeListResultDescription;
  }


  public int getChangeListID() {
    return changeListID;
  }


  public String getChangeListNumber() {
    return changeListNumber;
  }


  public String getUser() {
    return user;
  }


  public Date getCreatedAt() {
    return createdAt;
  }


  public String getDescription() {
    return description;
  }


  public String toString() {
    return "MergeQueueReportImpl{" +
      "mergeChangeListResultID=" + mergeChangeListResultID +
      ", mergeResultID=" + mergeResultID +
      ", createdAt=" + createdAt +
      ", changeListID=" + changeListID +
      ", mergeChangeListID=" + mergeChangeListID +
      ", mergeID=" + mergeID +
      ", targetBuildRunID=" + targetBuildRunID +
      ", changeListNumber='" + changeListNumber + '\'' +
      ", description='" + description + '\'' +
      ", mergeChangeListResultDescription='" + mergeChangeListResultDescription + '\'' +
      ", user='" + user + '\'' +
      '}';
  }
}
