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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Field;
import viewtier.ui.Flow;
import viewtier.ui.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Log retention setting panel
 */
public final class LogRetentionPanel extends MessagePanel implements Loadable, Validatable, Saveable {

  private static final long serialVersionUID = -2003109916576534327L; // NOPMD

  private static final String CAPTION_KEEP_BUILD_LOGS_FOR = "Keep build logs for: ";
  private static final String CAPTION_DAYS = " days";

  private final PropertyToInputMap propertyToInputMap = new PropertyToInputMap(false, new BuildConfigAttributePropertyHandler()); // strict map


  private final CommonField flRetentionDays = new CommonField(4, 4);
  private int buildID = BuildConfig.UNSAVED_ID;


  /**
   * Constructor
   */
  public LogRetentionPanel() {
    super("Log archive");
    // layout
    final GridIterator gi = new GridIterator(super.getUserPanel(), 2);
    gi.addPair(new Label(CAPTION_KEEP_BUILD_LOGS_FOR), (new Flow()).add(flRetentionDays).add(new Label(CAPTION_DAYS)));
    // bind props to fields
    propertyToInputMap.bindPropertyNameToInput(BuildConfigAttribute.LOG_RETENTION_DAYS, flRetentionDays);
    // set default selection
    flRetentionDays.setValue("364");
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
   * Batch set of properties
   *
   * @param settings List of SourceControlSetting objects
   */
  public void setSettings(final List settings) {
    propertyToInputMap.setProperties(settings);
  }


  /**
   * Return modified properties
   */
  public List getSettings() {
    return propertyToInputMap.getUpdatedProperties();
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not
   * valid, a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();
    final List errors = new ArrayList(11);
    validateNumberField(errors, flRetentionDays, " days ");
    // show errors if any
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }
    return true;
  }


  /**
   * When called, component should save it's content. This method should
   * return <code>true</code> when content of a component is saved successfully.
   * If not, a component should display a error message in it's area and return
   * <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    // save all
    ConfigurationManager.getInstance().saveBuildAttributes(buildID, propertyToInputMap.getUpdatedProperties());
    return true;
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load
   * configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    buildID = buildConfig.getBuildID();
    propertyToInputMap.setProperties(ConfigurationManager.getInstance().getBuildAttributes(buildConfig.getBuildID()));
  }


  /**
   * Genereric check if field is a positive integer
   */
  private static void validateNumberField(final List errors, final Field field, final String descr) {
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
