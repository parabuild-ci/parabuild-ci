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

import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import viewtier.ui.Field;

import java.util.List;
import java.util.ArrayList;

/**
 * AutomaticScheduleSettingsPanel holds setting for "Automatic"
 * build type.
 */
public final class TimedAutomaticScheduleSettingsPanel extends ScheduleSettingsPanel {

  private static final long serialVersionUID = -8801504979595049851L; // NOPMD
  private final ScheduleItemsTable tblScheduleItems = new ScheduleItemsTable(false);  // NOPMD


  public TimedAutomaticScheduleSettingsPanel() {
    super();
    tblScheduleItems.setTitle("Optional Timed Schedule");
    add(tblScheduleItems);
  }


  public void setBuildID(final int buildID) {
    tblScheduleItems.setBuildID(buildID);
  }


  public boolean validate() {
    return tblScheduleItems.validate();
  }


  public boolean save() {
    return tblScheduleItems.save();
  }


  public void load(final BuildConfig buildConfig) {
    final List scheduleItems = ConfigurationManager.getInstance().getScheduleItems(buildConfig.getBuildID());
    tblScheduleItems.setScheduleItems(scheduleItems);
  }
}
