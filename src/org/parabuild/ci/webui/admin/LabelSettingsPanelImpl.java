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
import org.apache.commons.logging.*;

import org.parabuild.ci.build.*;
import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * LabelSettingsPanel holds setting for build labeling.
 */
public final class LabelSettingsPanelImpl extends LabelSettingsPanel {

  private static final long serialVersionUID = -5331587976055985098L; // NOPMD

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(LabelSettingsPanelImpl.class); // NOPMD

  private static final String LABEL_RB_GROUP = "label_strategy";

  private static final String CAPTION_DELETE_LABELS_OLDER_THAN = "Delete labels older than ";
  private static final String CAPTION_NO_LABEL = "No label";
  private static final String CAPTION_CUSTOM = "Custom: ";

  private final PropertyToInputMap propertyToInputMap = new PropertyToInputMap(true, makePropertyHandler()); // strict map
  private final Field flCustomLabel = new CommonField(80, 80);
  private final RadioButton rbCustom = new RadioButton();
  private final RadioButton rbNoLabel = new RadioButton();
  private final CheckBox cbDeleteOld = new CheckBox();
  private final Field flDaysOld = new CommonField(3, 3);

  private int buildID = BuildConfig.UNSAVED_ID;
  private final boolean labelDeletingEnabled;


  /**
   * Constructor
   *
   * @param enableLabelDeleting
   */
  public LabelSettingsPanelImpl(final boolean enableLabelDeleting) {
    super("Build Label");
    this.labelDeletingEnabled = enableLabelDeleting;

    // layout
    final GridIterator gi = new GridIterator(super.getUserPanel(), 2);
    gi.addPair(rbNoLabel, new BoldCommonLabel(CAPTION_NO_LABEL));
    gi.addPair(rbCustom, (new Flow()).add(new Label(CAPTION_CUSTOM)).add(flCustomLabel));
    if (enableLabelDeleting) {
      gi.addPair(cbDeleteOld, new CommonFlow(new BoldCommonLabel(CAPTION_DELETE_LABELS_OLDER_THAN), flDaysOld, new CommonLabel(" days")));
    }

    // bind props to fields
    propertyToInputMap.bindPropertyNameToInput(LabelProperty.LABEL_CUSTOM_VALUE, flCustomLabel);
    propertyToInputMap.bindPropertyNameToInput(LabelProperty.LABEL_DELETE_ENABLED, cbDeleteOld);
    propertyToInputMap.bindPropertyNameToInput(LabelProperty.LABEL_DELETE_OLD_DAYS, flDaysOld);

    // set rb group
    rbNoLabel.setGroupName(LABEL_RB_GROUP);
    rbCustom.setGroupName(LABEL_RB_GROUP);

    // set default selection
    rbNoLabel.setSelected(true);
    rbCustom.setSelected(false);
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
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    super.clearMessage();
    final List errors = new ArrayList(11);

    if (rbNoLabel.isSelected()) return true;

    // anything at all selected
    if (!rbCustom.isSelected()) {
      showErrorMessage("Please selecte build label type");
      return false;
    }

    // validate custom not blank
    if (WebuiUtils.isBlank(flCustomLabel)) {
      showErrorMessage("Custom label can not be blank.");
      return false;
    }

    // label delete days
    if (cbDeleteOld.isChecked()) {
      WebuiUtils.validateFieldValidNonNegativeInteger(errors, "Number of days", flDaysOld);
    }

    // template is valid - parse custom
    if (errors.isEmpty()) {

      // create label name generator and set test values
      final BuildLabelNameGenerator nameGenerator = new BuildLabelNameGenerator();
      nameGenerator.setBuildName("test_name");
      nameGenerator.setBuildNumber(0);
      nameGenerator.setChangeListNumber("0");
      nameGenerator.setBuildTimestamp(new Date());
      nameGenerator.setLabelTemplate(flCustomLabel.getValue());

      // validate
      if (!nameGenerator.isTemplateValid()) {
        showErrorMessage("Custom label is not well-formed. Custom label can contain " +
          "only alphanumberic characters, characters \"_\", and the following " +
          "properties: ${build.name}, ${build.number}, ${build.date}, ${build.timestamp} and ${changelist.number}");
        return false;
      }

      // letter as a first character
      try {
        final String testName = nameGenerator.generateLabelName();
        if (!StringUtils.isFirstLetter(testName)) {
          showErrorMessage("Label name may start only with a letter");
          return false;
        }
      } catch (final BuildException e) {
        showErrorMessage("Custom label is invalid");
        return false;
      }

      // validate duplication
      try {
        final LabelTemplateFinder labelTemplateFinder = new LabelTemplateFinder();
        labelTemplateFinder.setBuildID(buildID);
        labelTemplateFinder.setTemplate(flCustomLabel.getValue());
        if (labelTemplateFinder.find()) {
          showErrorMessage("This custom label conflicts with the label defined for build \"" + labelTemplateFinder.getFoundBuildName() + '\"');
          return false;
        }
      } catch (final BuildException e) {
        showErrorMessage("Error has occured while validaing label: " + StringUtils.toString(e));
      }
    }

    if (!errors.isEmpty()) {
      super.showErrorMessage(errors);
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

    final List settings = propertyToInputMap.getUpdatedProperties();

    // get type property
    LabelProperty lp = getLabelTypeFromList(settings);
    if (lp == null) {
      // not defined, init
      lp = new LabelProperty();
      lp.setPropertyName(LabelProperty.LABEL_TYPE);
      lp.setPropertyValue(LabelProperty.LABEL_TYPE_NONE);
      settings.add(lp);
    }

    // set type property
    setTypePropertyFromRadioButton(lp);

    // save all
    ConfigurationManager.getInstance().saveLabelSettings(buildID, settings);
    return true;
  }


  /**
   * Sets type property value from type radio button
   *
   * @param typeProperty
   */
  private void setTypePropertyFromRadioButton(final LabelProperty typeProperty) {
    if (rbCustom.isSelected()) {
      typeProperty.setPropertyValue(LabelProperty.LABEL_TYPE_CUSTOM);
    } else {
      typeProperty.setPropertyValue(LabelProperty.LABEL_TYPE_NONE);
    }
  }


  /**
   * Sets type radio button from the list
   *
   * @param settings
   */
  private void setTypeRadioButton(final List settings) {
    // fallback by default to no label
    rbNoLabel.setSelected(true);
    final LabelProperty lp = getLabelTypeFromList(settings);
    if (lp == null) return;
    if (lp.getPropertyValue().equals(LabelProperty.LABEL_TYPE_CUSTOM)) {
      rbCustom.setSelected(true);
      rbNoLabel.setSelected(false);
    } else if (lp.getPropertyValue().equals(LabelProperty.LABEL_TYPE_NONE)) {
      rbNoLabel.setSelected(true);
      rbCustom.setSelected(false);
    }
  }


  /**
   * @param settings
   *
   * @return null if undefined
   */
  private static LabelProperty getLabelTypeFromList(final List settings) {
    for (final Iterator iter = settings.iterator(); iter.hasNext();) {
      final LabelProperty lp = (LabelProperty)iter.next();
      if (lp.getPropertyName().equals(LabelProperty.LABEL_TYPE)) {
        return lp;
      }
    }
    return null;
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    buildID = buildConfig.getBuildID();
    final List settings = ConfigurationManager.getInstance().getLabelSettings(buildConfig.getBuildID());
    propertyToInputMap.setProperties(settings);
    setTypeRadioButton(settings);
  }


  /**
   * Factory method to create LabelProperty handler to be used by
   * propertyToInputMap
   *
   * @return implementation of PropertyToInputMap.PropertyHandler
   *
   * @see PropertyToInputMap.PropertyHandler
   */
  private static PropertyToInputMap.PropertyHandler makePropertyHandler() {
    return new PropertyToInputMap.PropertyHandler() {
      private static final long serialVersionUID = 1617182798604100495L;


      public Object makeProperty(final String propertyName) {
        final LabelProperty prop = new LabelProperty();
        prop.setPropertyName(propertyName);
        return prop;
      }


      public void setPropertyValue(final Object property, final String propertyValue) {
        ((LabelProperty)property).setPropertyValue(propertyValue);
      }


      public String getPropertyValue(final Object property) {
        return ((LabelProperty)property).getPropertyValue();
      }


      public String getPropertyName(final Object property) {
        return ((LabelProperty)property).getPropertyName();
      }
    };
  }


  boolean isLabelDeletingEnabled() {
    return labelDeletingEnabled;
  }
}
