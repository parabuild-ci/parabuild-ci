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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;

/**
 * Panel to hold build security settings.
 */
public class SecuritySettingsPanel extends MessagePanel implements Loadable, Validatable, Saveable {

  private static final long serialVersionUID = -136056053329469432L;


  public SecuritySettingsPanel() {
    super(false);
    showHeaderDivider(true);
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load
   *                    configuration for.
   */
  public void load(final BuildConfig buildConfig) {
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not
   * valid, a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    return true;
  }


  /**
   * When called, component should save it's content. This method should
   * return <code>true</code> when content of a component is saved successfully.
   * If not, a component should dispaly a error message in it's area and return
   * <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    return true;
  }
}