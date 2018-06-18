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

import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;

import java.util.Properties;

/**
 * Specialized flow that holds a command link and a label
 * annotation following the link.
 */
public class CommonCommandLinkWithImage extends Flow {

  private CommonLink commandLink = null;
  private static final long serialVersionUID = -7359475602236040393L;


  /**
   * Constructor.
   *
   * @param caption    command caption.
   * @param url        command URL.
   */
  public CommonCommandLinkWithImage(final String caption, final String url) {
    commandLink = new CommonCommandLink(caption, url);
    add(WebuiUtils.makeBlueBulletSquareImage16x16());
    add(commandLink);
  }


  /**
   * Constructor.
   *
   * @param caption
   * @param url
   * @param properties
   */
  public CommonCommandLinkWithImage(final String caption,
                                    final String url,
                                    final Properties properties) {
    this(caption, url);
    setParameters(properties);
  }


  /**
   * Sets command parameters.
   *
   * @param params command parameters to set.
   */
  public void setParameters(final Properties params) {
    commandLink.setParameters(params);
  }


  public String toString() {
    return "CommonCommandLinkWithImage{" +
            "commandLink=" + commandLink +
            "} " + super.toString();
  }
}
