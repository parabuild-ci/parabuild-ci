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


/**
 * Common table header label
 */
public final class TableHeaderLabel extends CommonLabel {

  private static final long serialVersionUID = -6860244024023194477L; // NOPMD


  /**
   * Constructor
   */
  public TableHeaderLabel() {
    setHeaderCommonAttrs();
  }


  /**
   * Constructor
   *
   * @param s String label title
   */
  public TableHeaderLabel(final String s) {
    super(s);
    setHeaderCommonAttrs();
  }


  /**
   * Constructor
   *
   * @param s String label title
   * @param width int width
   */
  public TableHeaderLabel(final String s, final int width) {
    super(s);
    setWidth(width);
    setHeaderCommonAttrs();
  }


  public TableHeaderLabel(final String s, final String width) {
    super(s);
    setWidth(width);
    setHeaderCommonAttrs();
  }


  public TableHeaderLabel(final String s, final String width, final int alignX) {
    super(s);
    setWidth(width);
    setHeaderCommonAttrs();
    setAlignX(alignX);
  }


  public TableHeaderLabel(final String s, final int width, final int alignX) {
    super(s);
    setWidth(width);
    setHeaderCommonAttrs();
    setAlignX(alignX);
  }


  /**
   * helper method to set up common attribute
   */
  private void setHeaderCommonAttrs() {
    setPadding(2);
    setFont(Pages.FONT_TABLE_HEADER);
  }
}

