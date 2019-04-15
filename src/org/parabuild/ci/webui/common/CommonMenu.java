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

import java.util.*;

import viewtier.ui.*;

/**
 * Common Parabuild Menu
 */
public class CommonMenu extends Menu {

  private static final long serialVersionUID = 323168199332179670L;


  /**
   * Constructor
   */
  public CommonMenu(final String s) {
    super(s);
    setCommonMenuForeground();
  }


  /**
   * Constructor
   */
  public CommonMenu(final String s, final Properties properties) {
    super(s, properties);
    setCommonMenuForeground();
  }


  /**
   * Sets FG color for common menu. This method is for reuse by
   * constructors.
   */
  private void setCommonMenuForeground() {
    setForeground(Pages.COLOR_COMMON_LINK_FG);
    setFont(Pages.FONT_COMMON_MENU);
  }
}
