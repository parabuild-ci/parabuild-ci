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

import viewtier.ui.Component;
import viewtier.ui.Flow;

/**
 * Convenience class with constructors adding components to Flow.
 */
public final class CommonFlow extends Flow {

  private static final long serialVersionUID = 4441588577571839955L;


  public CommonFlow(final Component c1, final Component c2) {
    add(c1).add(c2);
  }


  public CommonFlow(final Component c1, final Component c2, final Component c3) {
    add(c1).add(c2).add(c3);
  }


  public CommonFlow(final Component c1, final Component c2, final Component c3, final Component c4) {
    add(c1).add(c2).add(c3).add(c4);
  }


  public CommonFlow(final Component c1, final Component c2, final Component c3, final Component c4, final Component c5) {
    add(c1).add(c2).add(c3).add(c4).add(c5);
  }


  public CommonFlow(final Component c1, final Component c2, final Component c3, final Component c4, final Component c5, final Component c6, final Component c7) {
    add(c1).add(c2).add(c3).add(c4).add(c5).add(c6).add(c7);
  }
}
