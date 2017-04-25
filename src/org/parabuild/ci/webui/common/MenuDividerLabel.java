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
package org.parabuild.ci.webui.common;

import viewtier.ui.*;

/**
 * This label is used to divide horizontal menu items.
 */
public class MenuDividerLabel extends Label {

  private static final long serialVersionUID = 8922646537393343230L; // NOPMD

  private static final String STRING_HARD_MENU_DIVIDER = "&nbsp;&nbsp;|&nbsp;&nbsp;";
  private static final String STRING_SOFT_MENU_DIVIDER = " |&nbsp;";


  /**
   * Constructor. Creates menu divider label with hard divider.
   */
  public MenuDividerLabel() {
    this(true);
  }


  /**
   * Constructor.
   *
   * @param hardSeparator if true, a hard divider is used. If
   * false, as soft divider is used.
   */
  public MenuDividerLabel(final boolean hardSeparator) {
    super(getSeparatorString(hardSeparator));
  }


  private static String getSeparatorString(final boolean hardSeparator) {
    return hardSeparator ? STRING_HARD_MENU_DIVIDER : STRING_SOFT_MENU_DIVIDER;
  }


  public void setHardSeparator(final boolean hardSeparator) {
    setText(getSeparatorString(hardSeparator));
  }
}
