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

import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * This class lists avialable build groups.
 */
final class MergeDisplayGroupDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 6739170664126590175L;

  /**
   * System-wide group to show all builds.
   */
  private static final String GROUP_ALL = "ALL";

  /**
   * System-wide group to show broken builds only.
   * @noinspection UnusedDeclaration
   */
  private static final String GROUP_STUCK = "STUCK"; // NOPMD

  /**
   * System-wide group to show building builds only.
   * @noinspection UnusedDeclaration
   */
  private static final String GROUP_MERGING = "MERGING"; // NOPMD


  /**
   * System-wide group to show all builds.
   */
  public static final int GROUP_ID_ALL = -1;

  /**
   * System-wide group to show broken builds only.
   */
  public static final int GROUP_ID_STUCK = -2;

  /**
   * System-wide group to show building builds only.
   */
  public static final int GROUP_ID_MERGING = -3;


  public MergeDisplayGroupDropDown() {

    // add pre-defined groups
    addCodeNamePair(GROUP_ID_ALL, GROUP_ALL);

// REVIEWME: simeshev@parabuilci.org -> uncomment when is implemented.
//    addCodeNamePair(GROUP_ID_STUCK, GROUP_STUCK);
//    addCodeNamePair(GROUP_ID_MERGING, GROUP_MERGING);

    // populate with groups
// TODO:
//    final List displayGroups = DisplayGroupManager.getInstance().getAllDisplayGroups();
//    for (final Iterator i = displayGroups.iterator(); i.hasNext();) {
//      final DisplayGroup displayGroup = (DisplayGroup)i.next();
//      addCodeNamePair(displayGroup.getID(), displayGroup.getName());
//    }
  }
}
