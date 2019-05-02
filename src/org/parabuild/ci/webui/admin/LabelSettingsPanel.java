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

import org.parabuild.ci.webui.common.*;
import org.parabuild.ci.object.*;

/**
 * Defines LabelSettingsPanel
 */
public abstract class LabelSettingsPanel extends MessagePanel implements Loadable, Validatable, Saveable {

  private static final long serialVersionUID = 8756851553363124008L;


  public LabelSettingsPanel(final String title) {
    super(title);
  }


  /**
   * Sets build ID this label belongs to
   *
   * @param buildID int to set
   */
  public abstract void setBuildID(int buildID);


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public abstract boolean validate();


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should display a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public abstract boolean save();


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public abstract void load(BuildConfig buildConfig);


  abstract boolean isLabelDeletingEnabled();
}
