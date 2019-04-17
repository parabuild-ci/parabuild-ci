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

import java.util.*;

/**
 * Bolded common link to be reused in Parabuild application
 */
public final class CommonBoldLink extends CommonLink {

  private static final long serialVersionUID = 4334793031791932617L;


  public CommonBoldLink(final String caption, final String url) {
    super(caption, url);
    this.setStyle();
  }


  public CommonBoldLink(final String caption, final String url, final Properties properties) {
    super(caption, url, properties);
    this.setStyle();
  }


  public CommonBoldLink(final String caption, final String url, final String paramName, final String paramValue) {
    super(caption, url, paramName, paramValue);
    this.setStyle();
  }


  public CommonBoldLink(final String caption, final String url, final String paramName, final int paramValue) {
    this(caption, url, paramName, Integer.toString(paramValue));
    this.setStyle();
  }


  public CommonBoldLink(final String caption, final String url, final String paramName1, final int paramValue1, final String paramName2, final int paramValue2) {
    super(caption, url, paramName1, paramValue1, paramName2, paramValue2);
    this.setStyle();
  }


  public CommonBoldLink(final String caption, final String url, final String paramName1, final int paramValue1, final String paramName2, final String paramValue2) {
    super(caption, url, paramName1, Integer.toString(paramValue1), paramName2, paramValue2);
    this.setStyle();
  }


  /**
   * Sets FG color for common link. This method is for reuse by
   * constructors.
   */
  private void setStyle() {
    setFont(Pages.FONT_COMMON_BOLD_LINK);
  }
}
