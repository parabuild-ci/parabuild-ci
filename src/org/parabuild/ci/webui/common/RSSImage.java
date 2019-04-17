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

import viewtier.cdk.*;

/**
 * This component implements GoF Strategy pattern.
 * <p/>
 * Inhereting classes shouls implement
 */
public final class RSSImage extends CustomComponent {

  private static final long serialVersionUID = -1439600642491719000L;
  private final int buildID;


  public RSSImage() {
    this(-1);
  }


  public RSSImage(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Oveloaded CustomComponent's render
   */
  public void render(final RenderContext ctx) {
    final String url = "/parabuild/build/status/feed.xml" + (buildID == -1 ? "" : '?' + Pages.PARAM_BUILD_ID + '=' + buildID);
    ctx.getWriter().print("<a href=\"" + url + "\"><img src=\"/parabuild/images/RSS.gif\" border=\"0\" hspace=\"0\" vspace=\"0\"/></a>");
  }
}
