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

import java.util.*;

import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * This class lists available build groups.
 */
public final class DisplayGroupDropDown extends CodeNameDropDown {

  /**
   * System-wide group to show all builds.
   */
  private static final String GROUP_ALL = "ALL";

  /**
   * System-wide group to show broken builds only.
   */
  private static final String GROUP_BROKEN = "BROKEN";

  /**
   * System-wide group to show building builds only.
   */
  private static final String GROUP_BUILDING = "BUILDING";


  /**
   * System-wide group to show inactive builds only.
   */
  private static final String GROUP_INACTIVE = "INACTIVE";


  /**
   * System-wide group to show scheduled builds only.
   */
  private static final String GROUP_SCHEDULED = "SCHEDULED";
  private static final long serialVersionUID = 6566360631387031578L;


  public DisplayGroupDropDown() {
    this(PROHIBIT_NONEXISTING_CODES);
  }


  public DisplayGroupDropDown(final boolean allowNonexistingCodes) {
    this(allowNonexistingCodes, true);
  }

  public DisplayGroupDropDown(final boolean allowNonexistingCodes, final boolean addStatusGroups) {
    super(allowNonexistingCodes);

    if (addStatusGroups) {
      // add pre-defined groups
      addCodeNamePair(DisplayGroup.DISPLAY_GROUP_ID_ALL, GROUP_ALL);
      addCodeNamePair(DisplayGroup.DISPLAY_GROUP_ID_BROKEN, GROUP_BROKEN);
      addCodeNamePair(DisplayGroup.DISPLAY_GROUP_ID_BUILDING, GROUP_BUILDING);
      addCodeNamePair(DisplayGroup.DISPLAY_GROUP_ID_SCHEDULED, GROUP_SCHEDULED);
      addCodeNamePair(DisplayGroup.DISPLAY_GROUP_ID_INACTIVE, GROUP_INACTIVE);
    }

    // populate with groups
    for (final Iterator iter = DisplayGroupManager.getInstance().getAllDisplayGroups().iterator(); iter.hasNext();) {
      final DisplayGroup displayGroup = (DisplayGroup)iter.next();
      addCodeNamePair(displayGroup.getID(), displayGroup.getName());
    }
  }
}
