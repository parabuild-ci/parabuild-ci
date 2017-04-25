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

import viewtier.ui.*;

/**
 * This class makes components that are subject to security
 * control.
 */
public final class SecuredComponentFactory {

  private static final SecuredComponentFactory instance = new SecuredComponentFactory();

  public static SecuredComponentFactory getInstance() {
    return instance;
  }


  /**
   * Singleton factory.
   */
  private SecuredComponentFactory() {
  }


  /**
   * Makes a Flow containng build commands links. The content of
   * the flow is defined by user's credentials.
   *
   * @return
   */
  public BuildCommandsLinks makeBuildCommandsLinks() {
    return new BuildCommandsLinksImpl();
  }


  public MaintenanceCommandsPanel makeMaintenanceCommandsPanel() {
    return new MaintenanceCommandsPanelImpl();
  }
}
