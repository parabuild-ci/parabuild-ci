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

import viewtier.ui.Font;
import viewtier.ui.Label;


/**
 * Common application label
 */
public class CommonLabel extends Label {

  private static final long serialVersionUID = -8349682673818341818L; // NOPMD


  /**
   * Constructor
   */
  public CommonLabel() {
  }


  /**
   * Constructor
   */
  public CommonLabel(final int alignX) {
    setAlignX(alignX);
  }


  /**
   * Constructor
   *
   * @param s String label title
   */
  public CommonLabel(final String s) {
    super(s);
  }


  /**
   * Constructor
   *
   * @param s String label title
   */
  public CommonLabel(final String s, final Font font) {
    super(s);
    setFont(font);
  }


  /**
   * Constructor
   *
   * @param s String label title
   * @param width int width
   */
  public CommonLabel(final String s, final int width) {
    super(s);
    setWidth(width);
    setCommonAttrs();
  }


  /**
   * Constructor
   */
  public CommonLabel(final StringBuffer s) {
    this(s.toString());
  }


  /**
   * helper method to set up common attribute
   */
  private void setCommonAttrs() {
    setPadding(4);
  }
}
