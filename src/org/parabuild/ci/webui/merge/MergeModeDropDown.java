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
 * Defines merge modes.
 */
final class MergeModeDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 7879856123262423376L;
  
  private static final String CAPTION_MERGE = " Merge ";
  private static final String CAPTION_NAG = " Nag ";


  public MergeModeDropDown() {
    addCodeNamePair(MergeConfiguration.MERGE_MODE_MERGE, CAPTION_MERGE);
    addCodeNamePair(MergeConfiguration.MERGE_MODE_NAG, CAPTION_NAG);
    setCode(MergeConfiguration.MERGE_MODE_NAG);
  }
}
