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

import viewtier.ui.Link;

import java.util.Properties;

/**
 * Common link to be reused in Parabuild application
 */
public class CommonLink extends Link {

  private static final long serialVersionUID = -8400861127430651405L;


  /**
   * Constructor.
   *
   * @param alignX X-axis alignment
   */
  public CommonLink(final int alignX) {
    super("", "");
    this.setCommonLinkStyle();
    this.setAlignX(alignX);
  }


  public CommonLink(final String caption, final String url) {
    super(caption, url);
    this.setCommonLinkStyle();
  }


  public CommonLink(final String caption, final String url, final Properties properties) {
    super(caption, url, properties);
    this.setCommonLinkStyle();
  }


  public CommonLink(final String caption, final String url, final String paramName, final String paramValue) {
    super(caption, url, paramName, paramValue);
    this.setCommonLinkStyle();
  }


  public CommonLink(final String caption, final String url, final String paramName, final int paramValue) {
    this(caption, url, paramName, Integer.toString(paramValue));
  }


  public CommonLink(final String caption, final String url, final String paramName1, final int paramValue1,
                    final String paramName2, final int paramValue2) {
    this(caption, url, paramName1, Integer.toString(paramValue1), paramName2, paramValue2);
  }


  public CommonLink(final String caption, final String url, final String paramName1, final String paramValue1,
                    final String paramName2, final int paramValue2) {
    this(caption, url, paramName1, paramValue1, paramName2, Integer.toString(paramValue2));
  }


  public CommonLink(final String caption, final String url, final String paramName1, final int paramValue1,
                    final String paramName2, final String paramValue2) {
    this(caption, url, paramName1, Integer.toString(paramValue1), paramName2, paramValue2);
  }


  public CommonLink(final String caption, final String url, final String paramName1, final String paramValue1,
                    final String paramName2, final String paramValue2) {
    super(caption, url);
    final Properties params = new Properties();
    params.setProperty(paramName1, paramValue1);
    params.setProperty(paramName2, paramValue2);
    setParameters(params);
    setCommonLinkStyle();
  }


  public CommonLink(final String caption, final String url, final String paramName, final Integer paramValue) {
    this(caption, url, paramName, paramValue.intValue());
  }


  /**
   * Sets FG color for common link. This method is for reuse by
   * constructors.
   */
  private void setCommonLinkStyle() {
    setPadding(4);
    setFont(Pages.FONT_COMMON_LINK);
    setForeground(Pages.COLOR_COMMON_LINK_FG);
  }
}
