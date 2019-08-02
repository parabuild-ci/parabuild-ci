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
package org.parabuild.ci.webui.admin.appearance;

import org.parabuild.ci.webui.admin.system.AbstractSystemConfigurationPage;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.ConversationalTierlet;

/**
 * This page is responsible for editing Parabuild's e-mail system
 * properties.
 */
public final class AppearanceConfigurationPage extends AbstractSystemConfigurationPage implements ConversationalTierlet {

  private static final long serialVersionUID = -7355330766753424778L; // NOPMD
  private static final String APPEARANCE = "Appearance";


  /**
   * Constructor
   */
  public AppearanceConfigurationPage() {
    super(Pages.ADMIN_APPEARANCE_CONFIGURATION, new AppearanceConfigPanel());
    setTitle(makeTitle(APPEARANCE));
  }
}
