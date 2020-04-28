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

/**
 * This label carries a style for page header - big, blue and with a border at
 * the bottom.
 */
public final class PageHeaderLabel extends Label {

  private static final long serialVersionUID = -7893171188419164939L;


  public PageHeaderLabel() {
    this("");
  }


  public PageHeaderLabel(final String string) {
    super(string);
    setBackground(Pages.COLOR_PAGE_HEADER_BACKGROUND);
    setForeground(Pages.COLOR_PAGE_HEADER_FOREGROUND);
    setBorder(Border.BOTTOM, 1, Pages.COLOR_PAGE_HEADER_FOREGROUND);
    setFont(Pages.FONT_PAGE_HEADER);
  }
}
