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
package org.parabuild.ci.merge.merger;

import java.text.*;

/**
 * Generates a description for a chage list that is a result
 * of the merge.
 */
public final class MergedChangeListDescriptionGenerator {

  /**
   * Generates a description for a change list that contains
   * results of a merge operation.
   *
   *
   *
   * @param description original description of the
   *  change list that we merge. May contain a marker. A
   *  marker can be removed.
   * @param number
   * @param user
   * @param branchViewName
   * @param reverseBranchView
   * @return description for a change list that contains
   *  results of a merge operation.
   */
  public String generateDescription(final String description, final String number,
    final String user, final String marker, final boolean deleteMarker,
    final String branchViewName, final boolean reverseBranchView) {

    // delete marker if needed
    final String preprocessedDescription;
    final int markerIndex = description.indexOf(marker);
    if (deleteMarker && markerIndex >= 0) {
      preprocessedDescription = new StringBuffer(description).replace(markerIndex, markerIndex + marker.length(), "").toString();
    } else {
      preprocessedDescription = description;
    }

    // make result description
    final MessageFormat messageFormat = new MessageFormat(" (Automerge: Integed change list # {0} by {1} using {2} branch view {3})");
    final String descriptionToAdd = messageFormat.format(new Object[]{number, user, reverseBranchView ? "reverse" : "", branchViewName});
    final StringBuffer result = new StringBuffer((int)(description.length() * 1.2));
    result.append(preprocessedDescription);
    result.append(descriptionToAdd);
    return result.toString();
  }
}
