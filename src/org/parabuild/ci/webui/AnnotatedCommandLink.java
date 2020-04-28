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

import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;

import java.util.Properties;


/**
 * Specialized flow that holds a command link and a label
 * annotation following the link.
 */
public final class AnnotatedCommandLink extends Flow {

  private static final long serialVersionUID = -2140340277225295856L;
  private CommonLink commandLink;


  /**
   * Constructor.
   *
   * @param caption    command caption.
   * @param url        command URL.
   * @param annotation command annotation.
   */
  public AnnotatedCommandLink(final String caption, final String url, final String annotation) {
    this(caption, url, annotation, false);
  }


  /**
   * Constructor.
   *
   * @param caption    command caption.
   * @param url        command URL.
   * @param annotation command annotation.
   */
  public AnnotatedCommandLink(final String caption, final String url, final String annotation, final boolean addImage) {
    commandLink = new CommonLink(caption, url);
    if (addImage) {
      add(WebuiUtils.makeBlueBulletSquareImage16x16());
    }
    add(commandLink);
    if (!StringUtils.isBlank(annotation)) {
      add(new CommonLabel(" - " + annotation));
    }
  }


  /**
   * Sets command parameters.
   *
   * @param params command parameters to set.
   */
  public void setParameters(final Properties params) {
    commandLink.setParameters(params);
  }
}
