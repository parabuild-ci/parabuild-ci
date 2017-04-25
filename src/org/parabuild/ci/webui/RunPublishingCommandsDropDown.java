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

import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * Dropdown list to select a kind of publishing command to run.
 */
public class RunPublishingCommandsDropDown extends CodeNameDropDown {

  private static final String CAPTION_NOT_SELECTED = "Not selected";
  private static final String CAPTION_RUN_COMMANDS = "Run commands";

  /**
   * No commands selected.
   */
  public static final int CODE_NOT_SELECTED = -1;

  /**
   * User selected to run commands
   */
  public static final int CODE_RUN_COMMANDS = 0;


  /**
   * Constructor.
   */
  public RunPublishingCommandsDropDown() {
    addCodeNamePair(CODE_NOT_SELECTED, CAPTION_NOT_SELECTED);
    addCodeNamePair(CODE_RUN_COMMANDS, CAPTION_RUN_COMMANDS);
  }
}
