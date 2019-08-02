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

import viewtier.cdk.CustomComponent;
import viewtier.cdk.RenderContext;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * This component presents a Java applet
 */
public final class AppletComponent extends CustomComponent {

  private static final long serialVersionUID = 372357917563237405L;
  private final String name;
  private final String codebase;
  private final String code;
  private final String archive;
  private final String width;
  private final String height;
  private final Properties parameters;


  public AppletComponent(final String name, final String codebase, final String code, final String archive, final String width, final String height, final Properties parameters) {
    this.name = name;
    this.codebase = codebase;
    this.code = code;
    this.archive = archive;
    this.width = width;
    this.height = height;
    this.parameters = parameters;
  }


  /**
   * Overloaded CustomComponent's render
   */
  public void render(final RenderContext ctx) {
    final PrintWriter writer = ctx.getWriter();
    writer.print("<applet name=\"" + name + "\" codebase=\"" + codebase + "\" code=\"" + code + "\" archive=\"" + archive + "\" width=\"" + width + '\"' + " height=\"" + height + "\">");
    for (final Iterator iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
      final Map.Entry entry = (Map.Entry)iterator.next();
      writer.println("<param name=\"" + entry.getKey() + '\"' + " value=\"" + entry.getValue() + "\"/>");
    }
    writer.println("<div>Your browser doesn't support applets</div>");
    writer.println("</applet>");
  }
}
