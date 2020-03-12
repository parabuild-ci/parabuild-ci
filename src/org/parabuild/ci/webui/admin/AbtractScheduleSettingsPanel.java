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

import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonCheckBox;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection FieldCanBeLocal
 */
public abstract class AbtractScheduleSettingsPanel extends ScheduleSettingsPanel { // NOPMD - "This abstract class does not have any abstract methods"

  private static final String CAPTION_CLEAN_CHECKOUT_IF_BROKEN = "Clean checkout if broken:";
  private static final String CAPTION_CLEAN_CHECKOUT_INTERVAL = "Clean checkout, builds:";
  private static final String CAPTION_CLEAN_CHECKOUT_ON_AGENT_CHANGE = "Clean checkout on agent change: ";
  private static final String CAPTION_RESET_BUILD_NUMBER = "Set next build number: ";
  private static final String CAPTION_REBUILD_IF_BROKEN = "Rebuild if broken: ";
  private static final String CAPTION_STICKY_AGENT = "Sticky agent: ";
  private static final long serialVersionUID = -538200368430972999L;

  protected int buildID = BuildConfig.UNSAVED_ID;
  protected final PropertyToInputMap propertyToInputMap = new PropertyToInputMap(false, makeSchedulePropertyHandler());

  private final CommonCheckBox flCleanCheckoutIfBroken = new CommonCheckBox(); // NOPMD
  private final CommonCheckBox flCleanCheckoutOnAgentChange = new CommonCheckBox(); // NOPMD
  private final CommonCheckBox flRebuildIfBroken = new CommonCheckBox(); // NOPMD
  private final CommonCheckBox flStickyAgent = new CommonCheckBox(); // NOPMD
  private final CommonField flCleanCheckout = new CommonField(4, 5); // NOPMD
  private final Field flResetBuildNumber = new BuildNumberField(); // NOPMD

  private final CommonFieldLabel lbCleanCheckoutIfBroken = new CommonFieldLabel(CAPTION_CLEAN_CHECKOUT_IF_BROKEN);  // NOPMD
  private final CommonFieldLabel lbCleanCheckoutInterval = new CommonFieldLabel(CAPTION_CLEAN_CHECKOUT_INTERVAL);  // NOPMD
  private final CommonFieldLabel lbCleanCheckoutOnAgentChange = new CommonFieldLabel(CAPTION_CLEAN_CHECKOUT_ON_AGENT_CHANGE);  // NOPMD
  private final CommonFieldLabel lbRebuildIfBroken = new CommonFieldLabel(CAPTION_REBUILD_IF_BROKEN);  // NOPMD
  private final CommonFieldLabel lbResetBuildNumber = new CommonFieldLabel(CAPTION_RESET_BUILD_NUMBER);  // NOPMD
  private final CommonFieldLabel lbStickyAgent = new CommonFieldLabel(CAPTION_STICKY_AGENT);  // NOPMD
  private final RequiredFieldMarker cleanCheckoutMarker = new RequiredFieldMarker(flCleanCheckout);

  protected final GridIterator gridIterator;


  /**
   * Creates message panel with title displayed
   */
  public AbtractScheduleSettingsPanel(final String title) {
    super(title);
    gridIterator = new GridIterator(super.getUserPanel(), 2);

    // layout
    gridIterator.addPair(lbCleanCheckoutInterval, cleanCheckoutMarker);
    gridIterator.addPair(lbCleanCheckoutIfBroken, flCleanCheckoutIfBroken);
    gridIterator.addPair(lbRebuildIfBroken, flRebuildIfBroken);
    gridIterator.addPair(lbCleanCheckoutOnAgentChange, flCleanCheckoutOnAgentChange);
    gridIterator.addPair(lbStickyAgent, flStickyAgent);
    gridIterator.addPair(lbResetBuildNumber, flResetBuildNumber);

    // property map for batch property load
    propertyToInputMap.bindPropertyNameToInput(ScheduleProperty.AUTO_CLEAN_CHECKOUT, flCleanCheckout);
    propertyToInputMap.bindPropertyNameToInput(ScheduleProperty.AUTO_CLEAN_CHECKOUT_IF_BROKEN, flCleanCheckoutIfBroken);
    propertyToInputMap.bindPropertyNameToInput(ScheduleProperty.CLEAN_CHECKOUT_ON_AGENT_CHANGE, flCleanCheckoutOnAgentChange);
    propertyToInputMap.bindPropertyNameToInput(ScheduleProperty.STICKY_AGENT, flStickyAgent);
    propertyToInputMap.bindPropertyNameToInput(ScheduleProperty.AUTO_REBUILD_IF_BROKEN, flRebuildIfBroken);

    // Set defaults
    flCleanCheckout.setValue("9999");
  }


  /**
   * Clean check out interval
   */
  public final void setCleanCheckoutInterval(final int value) {
    flCleanCheckout.setValue(Integer.toString(value));
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();
    final ArrayList errors = new ArrayList(1);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_CLEAN_CHECKOUT_INTERVAL, flCleanCheckout);
    WebuiUtils.validateFieldValidNonNegativeInteger(errors, CAPTION_CLEAN_CHECKOUT_INTERVAL, flCleanCheckout);

    try {
      final NextBuildNumberResetter buildNumberResetter = new NextBuildNumberResetter(buildID);
      buildNumberResetter.validate(flResetBuildNumber.getValue());
    } catch (final ValidationException e) {
      errors.add(e.getMessage());
    }

    // check if any errors
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }
    return true;
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should display a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    ArgumentValidator.validateBuildIDInitialized(buildID);
    final List settings = propertyToInputMap.getUpdatedProperties();
    ConfigurationManager.getInstance().saveScheduleSettings(buildID, settings);

    // reset build number
    try {
      final NextBuildNumberResetter buildNumberResetter = new NextBuildNumberResetter(buildID);
      buildNumberResetter.reset(flResetBuildNumber.getValue());
    } catch (final ValidationException e) {
      showErrorMessage(StringUtils.toString(e));
      return false;
    }
    return true;
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    buildID = buildConfig.getBuildID();
    propertyToInputMap.setProperties(ConfigurationManager.getInstance().getScheduleSettings(buildConfig.getBuildID()));

    // REVIEWME: simeshev@parabuilci.org - This is a hack - Right now Accurev does not support clean up (rather, cleans up every time).
    if (buildConfig.getSourceControl() == VersionControlSystem.SCM_ACCUREV) {
      flCleanCheckout.setValue("1");
      flCleanCheckoutIfBroken.setChecked(true);
      flCleanCheckoutOnAgentChange.setChecked(true);
      flCleanCheckoutOnAgentChange.setVisible(false);
      flRebuildIfBroken.setVisible(false);
      flCleanCheckout.setVisible(false);
      flCleanCheckoutIfBroken.setVisible(false);
      lbCleanCheckoutIfBroken.setVisible(false);
      lbCleanCheckoutInterval.setVisible(false);
      lbRebuildIfBroken.setVisible(false);
      cleanCheckoutMarker.setVisible(false);
    }
  }


  /**
   * Hides caption and field for build number resetter.
   *
   * @see ParallelScheduleSettingsPanel
   */
  protected final void hideResetBuildNumber() {
    lbResetBuildNumber.setVisible(false);
    flResetBuildNumber.setVisible(false);
  }


  /**
   * Factory method to create PropertyHandler for
   * ScheduleProperty
   *
   * @see PropertyToInputMap.PropertyHandler
   */
  private static PropertyToInputMap.PropertyHandler makeSchedulePropertyHandler() {
    return new PropertyToInputMap.PropertyHandler() {
      private static final long serialVersionUID = -2836566826434463672L;


      public Object makeProperty(final String propertyName) {
        final ScheduleProperty prop = new ScheduleProperty();
        prop.setPropertyName(propertyName);
        return prop;
      }


      public void setPropertyValue(final Object property, final String propertyValue) {
        ((ScheduleProperty) property).setPropertyValue(propertyValue);
      }


      public String getPropertyValue(final Object property) {
        return ((ScheduleProperty) property).getPropertyValue();
      }


      public String getPropertyName(final Object property) {
        return ((ScheduleProperty) property).getPropertyName();
      }
    };
  }


  public String toString() {
    return "AbtractScheduleSettingsPanel{" +
            "buildID=" + buildID +
            ", propertyToInputMap=" + propertyToInputMap +
            ", flCleanCheckoutIfBroken=" + flCleanCheckoutIfBroken +
            ", flCleanCheckout=" + flCleanCheckout +
            ", flResetBuildNumber=" + flResetBuildNumber +
            ", lbCleanCheckoutInterval=" + lbCleanCheckoutInterval +
            ", lbCleanCheckoutIfBroken=" + lbCleanCheckoutIfBroken +
            ", lbResetBuildNumber=" + lbResetBuildNumber +
            ", gridIterator=" + gridIterator +
            "} " + super.toString();
  }
}
