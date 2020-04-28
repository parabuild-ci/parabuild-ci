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

import org.parabuild.ci.object.Merge;
import org.parabuild.ci.object.MergeChangeList;

import java.util.Date;

/**
 * Merge queue report.
 */
public interface MergeQueueReport {

  /**
   * @return an ID of {@link MergeChangeList}
   */
  int getMergeChangeListID();


  /**
   * @return result of {@link MergeChangeList}
   *
   * @see MergeChangeList#RESULT_CONFLICTS
   * @see MergeChangeList#RESULT_NOT_MERGED
   * @see MergeChangeList#RESULT_NOTHING_TO_MERGE
   * @see MergeChangeList#RESULT_SUCCESS
   */
  byte getMergeChangeListResultID();


  /**
   * @return result as a string
   */
  String getStringMergeChangeListResult();


  /**
   * @return result description.
   */
  String getMergeChangeListResultDescription();


  int getChangeListID();


  String getChangeListNumber();


  String getUser();


  Date getCreatedAt();


  String getDescription();


  /**
   * @return merge ID
   */
  int getMergeID();


  /**
   * @return {@link Merge} result ID
   *
   * @see Merge#RESULT_CANNOT_VALIDATE
   * @see Merge#RESULT_CONFLICTS
   * @see Merge#RESULT_NOT_MERGED
   * @see Merge#RESULT_VALIDATION_FAILED
   */
  byte getMergeResultID();


  /**
   * @return String representation of {@link Merge} result ID
   *
   * @see Merge#RESULT_CANNOT_VALIDATE
   * @see Merge#RESULT_CONFLICTS
   * @see Merge#RESULT_NOT_MERGED
   * @see Merge#RESULT_VALIDATION_FAILED
   */
  String getStringMergeResult();


  /**
   * @return optional validation build run.
   */
  Integer getTargetBuildRunID();


  /**
   * @return true if merge verified
   */
  boolean isValidated();
}
