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

import org.parabuild.ci.object.User;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * InstantMessagingTypeDropdown shows a list of user IM types
 */
public final class InstantMessagingTypeDropdown extends CodeNameDropDown {

  private static final long serialVersionUID = 7210592889010278271L; // NOPMD

  public static final String NAME_NONE = "None    ";
  public static final String NAME_JABBER = "Jabber  ";


  public InstantMessagingTypeDropdown() {
    addCodeNamePair(User.IM_TYPE_NONE, NAME_NONE);
    addCodeNamePair(User.IM_TYPE_JABBER, NAME_JABBER);
    setCode(User.IM_TYPE_NONE);
  }


  public InstantMessagingTypeDropdown(final String fieldName) {
    this();
    setName(fieldName);
  }
}
