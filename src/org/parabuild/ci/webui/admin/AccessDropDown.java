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
import viewtier.ui.*;

/**
 * Access dropdown contains a list of build access types
 */
public final class AccessDropDown extends DropDown {

  private static final long serialVersionUID = -8761619396888895504L; // NOPMD


  public AccessDropDown() {
    super.addItem(accessToString(BuildConfig.ACCESS_PRIVATE));
    super.addItem(accessToString(BuildConfig.ACCESS_PUBLIC));
    this.setAccessType(BuildConfig.ACCESS_PRIVATE);
  }


  /**
   * Helper method
   *
   * @param access
   * @return
   */
  private String accessToString(final int access) {
    if (access == BuildConfig.ACCESS_PRIVATE) return "Private";
    if (access == BuildConfig.ACCESS_PUBLIC) return "Public";
    return "Public";
  }


  /**
   * Returns selected build access type
   *
   * @return int access type
   */
  public int getAccessType() {
    if (getSelection() == 0) return BuildConfig.ACCESS_PRIVATE;
    return BuildConfig.ACCESS_PUBLIC;
  }


  /**
   * Sets selected access type
   *
   * @param access type to select
   */
  public final void setAccessType(final int access) {
    super.setSelection(accessToString(access));
  }
}
