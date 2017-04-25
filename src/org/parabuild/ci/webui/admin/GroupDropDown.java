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

import java.util.*;

import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.*;

/**
 * Lists connection security levels
 */
public final class GroupDropDown extends CodeNameDropDown {


  /**
   * Constructor. Populates the dropdown with a list of build
   * groups from database.
   */
  public GroupDropDown() {
    addCodeNamePair(Group.UNSAVED_ID, "Select group:");
    final List groups = SecurityManager.getInstance().getGroupList();
    for (final Iterator i = groups.iterator(); i.hasNext();) {
      final Group group = (Group)i.next();
      final String groupName = group.getName();
      // skip admin (stupid from security point of view)
      if (groupName.equals(Group.SYSTEM_ADMIN_GROUP)) {
        continue;
      }
      addCodeNamePair(group.getID(), groupName);
    }
    setCode(Group.UNSAVED_ID);
  }
}
