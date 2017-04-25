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
package org.parabuild.ci.webui.admin;

import viewtier.ui.*;

/**
 * Dropdown to show available http protocols (http/https)
 */
public final class HTTPProtocolDropdown extends DropDown {

  private static final long serialVersionUID = -3638947672551947334L; // NOPMD

  private static final String[] PROTOCOLS = new String[]{
    "http://",
    "https://",
  };


  public HTTPProtocolDropdown() {
    for (int i = 0; i < PROTOCOLS.length; i++) {
      super.addItem(PROTOCOLS[i]);
    }
  }


  /**
   * @return an array of available protocols.
   */
  public static String[] getProtocols() {
    final String[] result = new String[PROTOCOLS.length];
    System.arraycopy(PROTOCOLS, 0, result, 0, PROTOCOLS.length);
    return result;
  }
}
