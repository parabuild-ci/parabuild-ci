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

import viewtier.ui.*;

/**
 */
public final class RequiredFieldMarker extends Flow {

  private static final long serialVersionUID = 7712141512060941155L; // NOPMD

  /**
   * Contructor. Adds a component and a trailing red-colored star label
   */
  public RequiredFieldMarker(final Component comp) {

    final Label lbRedStarTrailer = new Label("  *");

    // set red color
    lbRedStarTrailer.setForeground(Color.Red);
    this.setAlignY(Layout.CENTER);

    // add component and a trailer
    add(comp);
    add(lbRedStarTrailer);
  }
}

