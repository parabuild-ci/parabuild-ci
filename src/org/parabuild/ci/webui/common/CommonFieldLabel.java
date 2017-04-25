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
 * Common component to use as a edit field label. This label is
 * aligned to the right.
 */
public final class CommonFieldLabel extends CommonLabel {

  private static final long serialVersionUID = 5176370647855881638L; // NOPMD


  public CommonFieldLabel() {
    init();
  }


  public CommonFieldLabel(final String s) {
    super(s);
    init();
  }


  public CommonFieldLabel(final String s, final Color foreground) {
    super(s);
    init();
    setForeground(foreground);
  }


  public CommonFieldLabel(final String s, final int width) {
    super(s, width);
    init();
  }


  private void init() {
    setFont(Pages.FONT_COMMON_BOLD_LABEL);
    setAlignX(Layout.RIGHT);
  }
}
