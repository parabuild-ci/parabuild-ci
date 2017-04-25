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

import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 *
 */
public final class PreviousNextLinks extends Flow {


  public PreviousNextLinks(final Component cmpPrevious, final Component cmpCurrent, final Component cmpNext) {
    final Component cmpNext1 = cmpNext == null ? new CommonLabel("Next") : cmpNext;
    final Component cmpPrevious1 = cmpPrevious == null ? new CommonLabel("Previous") : cmpPrevious;
    this.add(cmpPrevious1);
    this.add(new Label(" | "));
    if (cmpCurrent != null) {
      this.add(cmpCurrent);
      this.add(new Label(" | "));
    }
    this.add(cmpNext1);
  }


  public PreviousNextLinks() {
    this(null, null, null);
  }
}
