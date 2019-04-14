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

/**
 * AutomaticScheduleSettingsPanel holds setting for "Automatic"
 * build type.
 */
public final class AutomaticScheduleSettingsPanel extends ScheduleSettingsPanel {

  private static final long serialVersionUID = -8801504979595049851L; // NOPMD

  private final SimpleAutomaticScheduleSettingsPanel simpleSettingsPanel = new SimpleAutomaticScheduleSettingsPanel();
  private final TimedAutomaticScheduleSettingsPanel timedSettingsPanel = new TimedAutomaticScheduleSettingsPanel();


  public AutomaticScheduleSettingsPanel() {
    add(simpleSettingsPanel);
//    add(WebuiUtils.makePanelDivider());
//    add(timedSettingsPanel);
  }


  public void setBuildID(final int buildID) {
    simpleSettingsPanel.setBuildID(buildID);
    timedSettingsPanel.setBuildID(buildID);
  }


  public boolean validate() {
    return simpleSettingsPanel.validate() && timedSettingsPanel.validate();
  }


  public boolean save() {
    return simpleSettingsPanel.save() && timedSettingsPanel.save();
  }


  public void load(final BuildConfig buildConfig) {
    simpleSettingsPanel.load(buildConfig);
    timedSettingsPanel.load(buildConfig);
  }
}
