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
package org.parabuild.ci.webui.merge;

import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * Lists available options for branch view.
 */
final class BranchViewSourceDropdown extends CodeNameDropDown {

  private static final String CAPTION_BRANCH_SPEC = "Branch spec";

  public static final byte BRANCH_VIEW_SOURCE_BRANCH_NAME = MergeConfiguration.BRANCH_VIEW_SOURCE_BRANCH_NAME;
  public static final byte BRANCH_VIEW_SOURCE_DIRECT = MergeConfiguration.BRANCH_VIEW_SOURCE_DIRECT;
  private static final long serialVersionUID = -1808561960684803358L;


  /**
   * Default constructor.
   */
  public BranchViewSourceDropdown() {
    addCodeNamePair(BRANCH_VIEW_SOURCE_BRANCH_NAME, CAPTION_BRANCH_SPEC);
  }
}
