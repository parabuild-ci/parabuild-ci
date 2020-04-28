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

import viewtier.ui.Font;


/**
 * Footer link is used by BasePage to create
 * common footer links
 *
 * @see BasePage
 */
public final class FooterLink extends CommonLink {

  private static final long serialVersionUID = -3002705558202646104L;


  public FooterLink(final String s, final String s1) {
    super(s, s1);
    setFont(new Font(Font.SansSerif, Font.Plain, 11));
  }
}
