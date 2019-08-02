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

import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.CheckBox;
import viewtier.ui.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * AutomaticScheduleSettingsPanel holds setting for "Automatic"
 * build type.
 */
public final class SimpleAutomaticScheduleSettingsPanel extends AbstractScheduleSettingsPanel {

  private static final long serialVersionUID = -8801504979595049851L; // NOPMD

  private static final String DEFAULT_POLL_INTERVAL_STR = Integer.toString(ScheduleProperty.DEFAULT_POLL_INTERVAL);
  private static final String DEFAULT_COOLDOWN_INTERVAL_STR = Integer.toString(ScheduleProperty.DEFAULT_COOLDOWN_INTERVAL);

  private static final String CAPTION_BUILD_CHANGES_ONE_BY_ONE = "Build changes one-by-one";
  private static final String CAPTION_POLL_INTERVAL = "Poll interval, seconds: ";
  private static final String CAPTION_COOLDOWN_INTERVAL = "Cool-down interval, seconds: ";
  private static final String CAPTION_RUN_IF_NO_CHANGES = "Run if no changes: ";
  private static final String CAPTION_SERIALIZE = "Serialize this build: ";

  private final Field flPollInterval = new Field(6, 7);
  private final Field flCooldownInterval = new Field(6, 7);
  private final CheckBox flBuildOneByOne = new CheckBox("build-one-by-one");
  private final CheckBox flRunIfNoChanges = new CheckBox("run-if-no-changes");
  private final CheckBox flSerialize = new CheckBox("serialize-this-build");


  /**
   * Creates message panel without title.
   */
  public SimpleAutomaticScheduleSettingsPanel() {
    super("Automatic Schedule");

    // layout
    super.gridIterator.addPair(new CommonFieldLabel(CAPTION_POLL_INTERVAL), new RequiredFieldMarker(flPollInterval));
    final boolean advancedSelected = SystemConfigurationManagerFactory.getManager().isAdvancedConfigurationMode();
    if (advancedSelected) {
      super.gridIterator.addPair(new CommonFieldLabel(CAPTION_COOLDOWN_INTERVAL), flCooldownInterval);
    }
    super.gridIterator.addPair(new CommonFieldLabel(CAPTION_BUILD_CHANGES_ONE_BY_ONE), flBuildOneByOne);
    super.gridIterator.addPair(new CommonFieldLabel(CAPTION_RUN_IF_NO_CHANGES), flRunIfNoChanges);
    super.gridIterator.addPair(new CommonFieldLabel(CAPTION_SERIALIZE), flSerialize);

    // set dafaut values
    flPollInterval.setValue(DEFAULT_POLL_INTERVAL_STR);
    flCooldownInterval.setValue(DEFAULT_COOLDOWN_INTERVAL_STR);
    setCleanCheckoutInterval(10);

    // property map for batch property load
    propertyToInputMap.bindPropertyNameToInput(ScheduleProperty.AUTO_POLL_INTERVAL, flPollInterval);
    propertyToInputMap.bindPropertyNameToInput(ScheduleProperty.AUTO_COOLDOWN_INTERVAL, flCooldownInterval);
    propertyToInputMap.bindPropertyNameToInput(ScheduleProperty.RUN_IF_NO_CHANGES, flRunIfNoChanges);
    propertyToInputMap.bindPropertyNameToInput(ScheduleProperty.AUTO_BUILD_ONE_BY_ONE, flBuildOneByOne);
    propertyToInputMap.bindPropertyNameToInput(ScheduleProperty.SERIALIZE, flSerialize);

    setWidth("100%");
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public final boolean validate() {

    // call super first
    final boolean commonValid = super.validate();

    // validate ours
    final List errors = new ArrayList(1);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_POLL_INTERVAL, flPollInterval);
    WebuiUtils.validateFieldValidNonNegativeInteger(errors, CAPTION_POLL_INTERVAL, flPollInterval);
    if (!WebuiUtils.isBlank(flCooldownInterval)) {
      WebuiUtils.validateFieldValidNonNegativeInteger(errors, SimpleAutomaticScheduleSettingsPanel.CAPTION_COOLDOWN_INTERVAL, flCooldownInterval);
    }

    // check if any errors
    if (commonValid && errors.isEmpty()) {
      return true;
    }

    // show errors
    showErrorMessage(errors);
    return false;
  }


  @Override
  public String toString() {
    return "SimpleAutomaticScheduleSettingsPanel{" +
            "flPollInterval=" + flPollInterval +
            ", flCooldownInterval=" + flCooldownInterval +
            ", flBuildOneByOne=" + flBuildOneByOne +
            ", flRunIfNoChanges=" + flRunIfNoChanges +
            ", flSerialize=" + flSerialize +
            "} " + super.toString();
  }
}
