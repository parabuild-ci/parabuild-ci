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

import viewtier.ui.Window;

/**
 * Alters apperance of the Window object to show log tail.
 */
final class TailWindowActivatorImpl implements TailWindowActivator {

  private final Window window;


  public TailWindowActivatorImpl(final Window window) {
    this.window = window;
  }


  /**
   * Caller shoud be showing the log tail panel before calling this method.
   *


   @param activeBuildID
    * @param lastTimeStamp
   */
  public void activate(final int activeBuildID, final int tailWindowSize, final long lastTimeStamp) {
    window.addScriptPath("/parabuild/dwr/interface/Tail.js");
    window.addScriptPath("/parabuild/dwr/engine.js");
    window.addScriptPath("/parabuild/scripts/tail.js");
    window.addStylePath("/parabuild/styles/tail.css");
    window.addScript("      var activeBuildID = " + activeBuildID + ";\n" +
      "      var MAX_LINE_COUNT = " + tailWindowSize + ";\n" +
      "      var TAIL_REFRESH_INTERVAL = 2000;" +
      "      var lastTimeStamp = " + lastTimeStamp + ';'
    );
    window.setOnLoad("startTail();");
    window.setOnUnload("stopTail();");
  }
}
