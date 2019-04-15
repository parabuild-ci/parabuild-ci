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
 * This label is used as a separator between buttons, like "save"/"cancel"
 */
public final class ButtonSeparator extends Label {

  private static final long serialVersionUID = 7712311873801235873L;


  public ButtonSeparator() {
    super("  ");
  }
}
