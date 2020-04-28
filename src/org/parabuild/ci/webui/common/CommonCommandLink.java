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

import viewtier.ui.Parameters;

import java.util.Properties;

/**
 * Common link to be reused in Parabuild application
 */
public class CommonCommandLink extends CommonLink {

  private static final long serialVersionUID = 1083133912281979260L;


  public CommonCommandLink(final String s, final String s1) {
    this(s, s1, false);
  }


  public CommonCommandLink(final String s, final String s1, final boolean openInNewWindow) {
    super(s, s1);
    if (openInNewWindow) setTarget("_blank");
    setCommonCommandLinkFont();
  }


  public CommonCommandLink(final String s, final String s1, final Properties properties) {
    super(s, s1, properties);
    setCommonCommandLinkFont();
  }


  public CommonCommandLink(final String s, final String s1, final Parameters parameters) {
    this(s, s1, WebuiUtils.parametersToProperties(parameters));
  }


  public CommonCommandLink(final String s, final String s1, final String s2, final String s3) {
    super(s, s1, s2, s3);
    setCommonCommandLinkFont();
  }

  public CommonCommandLink(final String caption, final String url, final String param1, final int value1, final String param2, final String value2) {
    super(caption, url, param1, value1, param2, value2);
    setCommonCommandLinkFont();
  }


  /**
   * Sets FG color for common link. This method is for reuse by
   * constructors.
   */
  private void setCommonCommandLinkFont() {
    setFont(Pages.FONT_COMMON_MENU);
  }
}
