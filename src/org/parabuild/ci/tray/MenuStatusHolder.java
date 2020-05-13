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
package org.parabuild.ci.tray;

import javax.swing.*;

/**
 * Composite to hold menu status, menu item and associated build
 * status for quicj access.
 */
final class MenuStatusHolder {

  private BuildStatus buildStatus;
  private final JMenuItem menuItem;
  private MenuStatus menuStatus;


  public MenuStatusHolder(final BuildStatus buildStatus, final MenuStatus menuStatus, final JMenuItem menuItem) {
    this.buildStatus = buildStatus;
    this.menuItem = menuItem;
    this.menuStatus = menuStatus;
  }


  public MenuStatus getMenuStatus() {
    return menuStatus;
  }


  public void setMenuStatus(final MenuStatus menuStatus) {
    this.menuStatus = menuStatus;
  }


  public BuildStatus getBuildStatus() {
    return buildStatus;
  }


  public void setBuildStatus(final BuildStatus buildStatus) {
    this.buildStatus = buildStatus;
  }


  public JMenuItem getMenuItem() {
    return menuItem;
  }
}
