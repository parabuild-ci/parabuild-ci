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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import viewtier.cdk.CustomComponent;
import viewtier.cdk.RenderContext;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This component implements GoF Strategy pattern.
 *
 * Inhereting classes shouls implement
 */
public abstract class AbstractXSLRendererComponent extends CustomComponent {

  private static final Log log = LogFactory.getLog(AbstractXSLRendererComponent.class);


  /**
   * Returns XML source to transform.
   */
  protected abstract StreamSource xmlSource() throws IOException;


  /**
   * Returns XML source to transform.
   */
  protected abstract StreamSource xslSource() throws IOException;


  /**
   * Oveloaded CustomComponent's render
   */
  public final void render(final RenderContext ctx) {
    StreamSource xslSource = null;
    StreamSource xmlSource = null;
    try {
      xslSource = xslSource();
      xmlSource = xmlSource();
      final Transformer transformer = TransformerFactory.newInstance().newTransformer(xslSource);
      final PrintWriter pw = ctx.getWriter();
      transformer.transform(xmlSource, new StreamResult(pw));
    } catch (Exception e) {
      showUnexpectedErrorMsg(ctx, e);
    } finally {
      closeHard(xslSource);
      closeHard(xmlSource);
    }
  }


  private void showUnexpectedErrorMsg(final RenderContext ctx, final Exception e) {
    try {
      final PrintWriter pw = ctx.getWriter();
      pw.println("There was an unexpected error while rendering log.  The error was reported to the build administrator.");
      log.error("Error getting log", e);
    } catch (Exception e1) {
      IoUtils.ignoreExpectedException(e1);
    }
  }


  /**
   * Helper method to close StreamSource
   * @param source
   */
  private void closeHard(final StreamSource source) {
    try {
      if (source != null) {
        if (source.getInputStream() != null) IoUtils.closeHard(source.getInputStream());
        if (source.getReader() != null) IoUtils.closeHard(source.getReader());
      }
    } catch (Exception e) {
      IoUtils.ignoreExpectedException(e);
    }
  }
}
