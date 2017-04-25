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
package org.parabuild.ci.webui;

import java.io.*;

import org.parabuild.ci.services.TailUpdate;
import viewtier.cdk.CustomComponent;
import viewtier.cdk.RenderContext;
import viewtier.util.XMLEncoder;

/**
 * The component to hold a log tail. This is pretty much a
 * 100% width box with ID "loglines".
 */
final class TailComponent extends CustomComponent {

  private TailUpdate update = null;
  private int tailBufferSize = 0;


  public TailComponent(final int tailBufferSize) {
    this.tailBufferSize = tailBufferSize;
  }


  public void show(final TailUpdate update) {
    this.update = update;
  }


  public void render(final RenderContext renderContext) {

    // NOTE: simeshev@parabuilci.org - 2007-07-20 - it is
    // critical that HTML code generated below does not have
    // any unneeded spaces between tags, or line breaks.
    // They produce redundant nodes in the DOM mode that
    // screw up the way how tail.js handles scrolling.

    final PrintWriter writer = renderContext.getWriter();
    writer.print("<table class=\"tail\"><tbody id=\"loglines\">");

    if (update != null) {

      final String[] logLines = update.getLogLines();
      final int linesToShow = Math.min(logLines.length, tailBufferSize);

      // add blank lines if any
      addBlankLines(writer, tailBufferSize - linesToShow);

      final int offset = logLines.length - linesToShow;
      for (int i = 0; i < linesToShow; i++) {
        writeLine(writer, logLines[offset + i]);
      }
    } else {
      addBlankLines(writer, tailBufferSize);
    }
    writer.print("</tbody></table>");
  }


  private void addBlankLines(final PrintWriter writer, final int blankLineCount) {
    for (int i = 0; i < blankLineCount; i++) {
      writeLine(writer, "&nbsp;");
    }
  }


  private void writeLine(final PrintWriter writer, final String logLine) {
    writer.print("<tr><td class=\"logLine\">");
    writer.print(XMLEncoder.encode(logLine));
    writer.print("</td></tr>");
  }
}
