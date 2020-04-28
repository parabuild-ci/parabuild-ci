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

import viewtier.ui.Border;
import viewtier.ui.Label;
import viewtier.ui.Layout;

/**
 * This label carries a style for page section header - smaller than
 * PageHeaderLabel , blue and with a border at the bottom.
 */
public final class PageSectionLabel extends Label {

  private static final long serialVersionUID = 3070057858943247983L;


  public PageSectionLabel() {
    this("");
  }


  public PageSectionLabel(final String string) {
    super(string);
    setAlignX(Layout.LEFT);
    setBackground(Pages.COLOR_PAGE_SECTION_BACKGROUND);
    setForeground(Pages.COLOR_PAGE_SECTION_FOREGROUND);
    setFont(Pages.FONT_PAGE_SECTION);
    setBorder(Border.BOTTOM, 1, Pages.COLOR_PAGE_SECTION_FOREGROUND);
  }
}
