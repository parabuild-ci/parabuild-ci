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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;

/**
 * Access dropdown contains a list of build VCS types
 */
public final class WatchLevelDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 1175596430066030886L; // NOPMD


  public WatchLevelDropDown() {
    super.addCodeNamePair(BuildWatcher.LEVEL_SUCCESS, "Build results only");
    super.addCodeNamePair(BuildWatcher.LEVEL_BROKEN, "Broken builds only");
    super.addCodeNamePair(BuildWatcher.LEVEL_SYSTEM_ERROR, "All");
    super.setCode(BuildWatcher.LEVEL_BROKEN);
  }
}
