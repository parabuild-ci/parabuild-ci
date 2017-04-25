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

/**
 * This panel is used in case a build configuration, backed by a
 * manual schedule, doesn't have alterable parameters.
 */
final class EmptyManualScheduleStartParametersPanel extends ManualScheduleStartParametersPanel {

  public EmptyManualScheduleStartParametersPanel() {
    super("");
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  public void setEditMode(final int mode) {
    // do nothing because this is an empty panel
  }


  /**
   * Loads the panel content acording to the build ID provided in
   * the construtor.
   */
  public void load(final int buildID) {
    // do nothing
  }
}
