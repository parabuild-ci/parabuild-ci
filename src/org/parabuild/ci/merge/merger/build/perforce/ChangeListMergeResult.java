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
package org.parabuild.ci.merge.merger.build.perforce;

import java.util.Collections;
import java.util.List;

/**
 * This value object is returned after an attempt to merge a
 * change list.
 */
final class ChangeListMergeResult {

  private final byte code;
  private final String description;
  private final List conflicts;


  ChangeListMergeResult(final byte resultCode, final String description, final List conflicts) {
    this.code = resultCode;
    this.description = description;
    this.conflicts = conflicts;
  }


  ChangeListMergeResult(final byte resultCode, final String description) {
    this.code = resultCode;
    this.description = description;
    this.conflicts = Collections.emptyList();
  }


  public byte getCode() {
    return code;
  }


  public String getDescription() {
    return description;
  }


  public List getConflicts() {
    return conflicts;
  }


  public String toString() {
    return "ChangeListMergeResult{" +
      "code=" + code +
      ", description='" + description + '\'' +
      ", conflicts=" + conflicts +
      '}';
  }
}
