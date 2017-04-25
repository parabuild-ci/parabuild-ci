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
package org.parabuild.ci.webui.admin.email;

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;

/**
 * A drop down to show message priority selection.
 */
final class MessagePriorityDropDown extends CodeNameDropDown {

  private static final String CAPTION_NORMAL = "Normal";
  private static final String CAPTION_HIGH = "High";


  /**
   * Constructor
   */
  MessagePriorityDropDown() {
    addCodeNamePair((int) SystemProperty.MESSAGE_PRIORITY_NORMAL, CAPTION_NORMAL);
    addCodeNamePair((int) SystemProperty.MESSAGE_PRIORITY_HIGH, CAPTION_HIGH);
  }
}
