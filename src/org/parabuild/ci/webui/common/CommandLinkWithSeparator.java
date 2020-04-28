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

import viewtier.ui.Flow;
import viewtier.ui.Link;

import java.util.Properties;


/**
 * This class presents a link with heading doublespace
 * separator
 */
public final class CommandLinkWithSeparator extends Flow {

  private static final long serialVersionUID = -5990770164884868879L; // NOPMD

  private final MenuDividerLabel lbSeparator = new MenuDividerLabel();
  private Link lnk;


  /**
   * Constructor
   */
  public CommandLinkWithSeparator(final Link lnk) {
    this.lnk = lnk;
    add(lnk);
    add(lbSeparator);
  }


  /**
   * Sets link parameters
   */
  public void setParameters(final Properties params) {
    lnk.setParameters(params);
  }


  /**
   * Hides CommandLink's separator.
   */
  public void hideSeparator() {
    lbSeparator.setVisible(false);
  }
}