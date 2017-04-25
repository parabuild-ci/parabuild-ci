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
package org.parabuild.ci.webui.admin.usermanagement;

import java.util.*;

import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * List of commands available for a parcticular user in a users
 * table.
 */
public final class UserCommandsFlow extends Flow {

  private Link lnkEdit = null;
  private Link lnkDelete = null;


  /**
   * Constructor.
   *
   * @param userID ID to use to compose command links.
   */
  public UserCommandsFlow(final int userID) {
    this();
    setUserID(userID);
  }


  /**
   * Constructor.
   */
  public UserCommandsFlow() {
    lnkEdit = new CommonLink("Edit", Pages.ADMIN_EDIT_USER);
    lnkDelete = new CommonLink("Delete", Pages.ADMIN_DELETE_USER);
    this.add(lnkEdit);
    this.add(new Label(" | "));
    this.add(lnkDelete);
  }


  /**
   * Sets user ID
   *
   * @param userID to set
   */
  public void setUserID(final int userID) {
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_USER_ID, Integer.toString(userID));
    lnkDelete.setParameters(param);
    lnkEdit.setParameters(param);
  }
}
