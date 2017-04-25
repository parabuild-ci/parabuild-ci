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

import viewtier.cdk.CustomComponent;
import viewtier.cdk.RenderContext;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Text;

import java.io.PrintWriter;

final class FieldWithButtonPanel extends Panel {

  private final ExpandButton expandButton;
  private final Text field;


  FieldWithButtonPanel(final String caption, final Text field) {
    this.field = field;
    expandButton = new ExpandButton(caption, this.field.getName());
    expandButton.setAlignY(Layout.TOP);
    add(field, new Layout(0, 0, 1, 1));
    add(expandButton, new Layout(1, 0, 1, 1));
  }


  public void setEditable(final boolean editable) {
    field.setEditable(editable);
    expandButton.setVisible(editable);
  }


  private static final class ExpandButton extends CustomComponent {

    private final String caption;
    private final String fieldName;


    /**
     * Constructor.
     *
     * @param caption   modal dialog caption.
     * @param fieldName input field name to edit content of
     */
    public ExpandButton(final String caption, final String fieldName) {
      this.caption = caption;
      this.fieldName = fieldName;
    }


    public void render(final RenderContext renderContext) {
      final PrintWriter writer = renderContext.getWriter();
      writer.println("<span title=\"Expand input area\">\n" +
              "\t<button class=\"popup-button\" type=\"button\" onclick=\"popupModal('" + caption + "','" + fieldName + "');\" >\n" +
              "\t\t<img src=\"/parabuild/scripts/windows-js/images/edit-button-10x10.gif\" alt=\"\" />\n" +
              "\t</button>\n" +
              "</span>");
    }
  }

}
