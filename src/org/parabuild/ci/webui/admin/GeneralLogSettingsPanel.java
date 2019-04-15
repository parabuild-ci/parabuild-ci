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

import java.util.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * Log packing setting panel
 */
public final class GeneralLogSettingsPanel extends MessagePanel implements Loadable, Validatable, Saveable {

  private static final long serialVersionUID = -2003109916576534327L; // NOPMD

  private static final String CAPTION_COMPRESS_BUILD_LOGS_OLDER_THAN = "Compress build logs older than: ";
  private static final String CAPTION_DAYS = " days";

  private final PropertyToInputMap inputMap = new PropertyToInputMap(false, new BuildAttributeHandler()); // strict map
  private final Field flPackDays = new CommonField(3, 3);
  private int buildID = BuildConfig.UNSAVED_ID;


  /**
   * Constructor
   */
  public GeneralLogSettingsPanel() {
    super("General Settings");
    // layout
    final GridIterator gi = new GridIterator(super.getUserPanel(), 2);
    gi.addPair((new Flow()).add(new CommonFieldLabel(CAPTION_COMPRESS_BUILD_LOGS_OLDER_THAN)), (new Flow()).add(flPackDays).add(new Label(CAPTION_DAYS)));
    // bind props to fields
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.LOG_PACK_DAYS, flPackDays);
    // set default selection
    flPackDays.setValue("30");
  }


  /**
   * Sets build ID this label belongs to
   *
   * @param buildID int to set
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();
    final List errors = new ArrayList();
    validateNumberField(errors, flPackDays, " days ");
    // show errors if any
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }
    return true;
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
    // save all
    final List attrs = inputMap.getUpdatedProperties();
    ConfigurationManager.getInstance().saveBuildAttributes(buildID, attrs);
    return true;
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    buildID = buildConfig.getBuildID();
    inputMap.setProperties(ConfigurationManager.getInstance().getBuildAttributes(buildConfig.getBuildID()));
  }


  /**
   * Genereric check if field is a positive integer
   */
  private void validateNumberField(final List errors, final Field field, final String descr) {
    // check for blank
    WebuiUtils.validateFieldNotBlank(errors, "Number of " + descr, field);

    // check for format.
    if (!StringUtils.isValidInteger(field.getValue())) {
      errors.add("Number of " + descr + " is invalid. It should be a positive integer value.");
      return;
    }

    // check value
    final int value = Integer.parseInt(field.getValue());
    if (value <= 0) {
      errors.add("Number of " + descr + " too small. It should be a positive integer value.");
    }
  }
}
