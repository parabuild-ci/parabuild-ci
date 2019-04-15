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

import viewtier.ui.Color;

import java.util.Properties;

/**
 */
public final class CommonHeaderLink extends CommonLink {


  private static final long serialVersionUID = 6154052712344065955L;


  public CommonHeaderLink(final String s, final String s1) {
    super(s, s1);
    setForeground(Color.White);
    setFont(Pages.FONT_HEADER_LINK);
  }


  public void setDisplayGroupID(final int displayGroupID) {
    final Properties properties = new Properties();
    properties.setProperty(Pages.PARAM_GROUP_ID, Integer.toString(displayGroupID));
    setParameters(properties);
  }
}

