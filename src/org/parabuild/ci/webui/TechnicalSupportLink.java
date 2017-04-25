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
import viewtier.cdk.CustomComponent;
import viewtier.cdk.RenderContext;

import java.io.PrintWriter;

/**
 * TechnicalSupportLink is a custom leaf component responsible for
 * displaying tech support link.
 * <p/>
 * This companent is introduced because as of 03/29/2004
 * viewtier.ui.Link component doesn't support targets to open a
 * link in a new window. When this finctionality is available,
 * this component should go away.
 */
public final class TechnicalSupportLink extends CustomComponent {

  private static final long serialVersionUID = 6624350767591397872L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(TechnicalSupportLink.class); // NOPMD

  public static final String SUPPORT_FORUM_URL = "http://forums.parabuildci.org/viewforum.php?f=1";
  public static final String SUPPORT_FORUM_CAPTION = "viewtier.parabuild.support";


  /**
   * Oveloaded CustomComponent's render
   */
  public void render(final RenderContext ctx) {
    final PrintWriter pw = ctx.getWriter();
    pw.println("<a style=\"font-family: sans-serif; font-size: 12; font-weight: bold; color: #000080;\" href=\"" + SUPPORT_FORUM_URL + "\" target=\"_new\">" + SUPPORT_FORUM_CAPTION + "</a>");
  }
}
