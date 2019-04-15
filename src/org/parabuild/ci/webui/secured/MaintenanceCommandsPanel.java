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
package org.parabuild.ci.webui.secured;

import java.util.*;

import viewtier.ui.*;

/**
 * Shows panel containinig maintenance commands.
 */
public abstract class MaintenanceCommandsPanel extends Panel {

  private static final long serialVersionUID = 8847002358862257794L;


  protected MaintenanceCommandsPanel() {
  }


  /**
   * Sets command parameters.
   *
   * @param params command parameters to set.
   */
  public abstract void setParameters(Properties params);


  public abstract int commandsAvailable();
}
