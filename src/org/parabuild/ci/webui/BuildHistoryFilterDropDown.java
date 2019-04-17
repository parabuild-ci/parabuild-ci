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
package org.parabuild.ci.webui;

import org.parabuild.ci.webui.common.*;

/**
 * Drop down to show list of filtering for build history options.
 */
final class BuildHistoryFilterDropDown extends CodeNameDropDown {

  public static final byte CODE_ALL = 0;
  public static final byte CODE_SUCCESSFUL = 1;
  public static final byte CODE_FAILED = 2;
  private static final long serialVersionUID = -6275345863285573686L;


  public BuildHistoryFilterDropDown() {
    setName("build_history_filter_drop_down");
    addCodeNamePair(CODE_ALL, "All");
    addCodeNamePair(CODE_SUCCESSFUL, "Successful");
    addCodeNamePair(CODE_FAILED, "Failed");
  }
}
