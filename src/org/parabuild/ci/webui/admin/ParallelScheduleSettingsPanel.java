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

import org.parabuild.ci.object.*;

/**
 * Parallel schedule allow for basic settings defined by the
 * {@link AbtractScheduleSettingsPanel}.
 *
 * As parallel build have the same build numvber as their
 * lad, setting build number for parallel builds is
 * disabled.
 */
public final class ParallelScheduleSettingsPanel extends ScheduleSettingsPanel {


  /**
   * Creates message panel with title displayed
   */
  public ParallelScheduleSettingsPanel() {
    super("Schedule Settings");
  }


  public void setBuildID(final int buildID) {
    // Do nothing
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    return true;    // Do nothing
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should dispaly a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    return true;     // Do nothing
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    // Do nothing
  }
}
