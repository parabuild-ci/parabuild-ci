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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.ResultConfigProperty;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.AbstractInput;
import viewtier.ui.CheckBox;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract build result configuration panel. This class realizes Strategy
 * pattern.
 * <p/>
 * Abstract build result config panel consists of common elements
 * including name of the result and path. Implementors may add there
 * own properties in their constructors.
 * <p/>
 * Implementing classes should take care about implementing
 * needed methods and adding there own functionality.
 *
 * @see ResultConfigProperty
 */
public abstract class AbstractResultConfigPanel extends MessagePanel implements Validatable, Saveable {

  private static final Log log = LogFactory.getLog(AbstractResultConfigPanel.class);

  private static final String CAPTION_DESCRIPTION = "Description: ";
  private static final String CAPTION_FAIL_IF_NOT_FOUND = " Fail if not found: ";
  private static final String CAPTION_IGNORE_TIMESTAMP = "Ignore  timestamp: ";
  private static final String CAPTION_PUBLISH = "Publish to: "; // NOPMD
  private static final String CAPTION_RESULT_PATH = "Result  path: ";
  private static final String CAPTION_SHELL_VARIABLE = "Shell variable: ";
  private static final String CAPTION_TYPE = "Result  type:";
  private static final long serialVersionUID = -4770511697994028153L;

  protected final ConfigurationManager cm = ConfigurationManager.getInstance();
  protected int resultConfigID = ResultConfig.UNSAVED_ID;
  protected int buildID = BuildConfig.UNSAVED_ID;

  // Captions
  private final CommonFieldLabel lbShellVariable = new CommonFieldLabel(CAPTION_SHELL_VARIABLE);


  // fields
  private final AutopublishDropdown flPublish = new AutopublishDropdown();  // NOPMD
  private final CheckBox flFailIfNotFound = new CheckBox();
  private final CheckBox flIgnoreTimestamp = new CheckBox();
  private final CommonField flDescr = new CommonField(60, 35);
  private final AbstractInput flPath;
  private final CommonField flShellVariable = new CommonField(60, 35);
  private final ResultTypeDropDown flResultType = new ResultTypeDropDown();

  // GI
  private final GridIterator gridIter = new GridIterator(super.getUserPanel(), 4);
  private final List errors = new ArrayList(5);

  // property handling support
  protected final PropertyToInputMap inputMap = new PropertyToInputMap(false, makePropertyHandler()); // strict map


  /**
   * Creates panel. Caption for path field is set to default {@link #CAPTION_RESULT_PATH}
   *
   * @param enableContentBorder
   */
  protected AbstractResultConfigPanel(final boolean enableContentBorder) {
    this(enableContentBorder, CAPTION_RESULT_PATH, new CommonField(250, 40));
  }


  protected AbstractResultConfigPanel(final boolean enableContentBorder, final String pathCaption, final AbstractInput flPath) {
    super(enableContentBorder);
    super.showHeaderDivider(true);

    this.flPath = flPath;
    this.lbShellVariable.setAlignY(Layout.TOP);
    this.flShellVariable.setAlignY(Layout.TOP);

    final CommonFieldLabel lbPath = new CommonFieldLabel(pathCaption);
    lbPath.setAlignY(Layout.TOP);

    flResultType.setEditable(false); // read only, serves as a display label for build type
    flResultType.setFont(Pages.FONT_COMMON_LABEL);
    gridIter.addPair(new CommonFieldLabel(CAPTION_TYPE), flResultType);
    gridIter.addPair(new CommonFieldLabel(CAPTION_PUBLISH), flPublish);
    gridIter.addPair(new CommonFieldLabel(CAPTION_DESCRIPTION), new RequiredFieldMarker(flDescr));
    gridIter.addPair(new CommonFieldLabel(CAPTION_IGNORE_TIMESTAMP), new CommonFlow(flIgnoreTimestamp, new CommonFieldLabel(CAPTION_FAIL_IF_NOT_FOUND), flFailIfNotFound));
    gridIter.addPair(lbPath, new RequiredFieldMarker(this.flPath));
    gridIter.addPair(lbShellVariable, flShellVariable);

    // hide shell var if publishing is not enabled
    if (!SystemConfigurationManagerFactory.getManager().isPublishingCommandsEnabled()) {
      lbShellVariable.setVisible(false);
      flShellVariable.setVisible(false);
    }
  }


  /**
   * Inheriting class should set log type at construction time.
   *
   * @param type result type to set.
   */
  public final void setResultType(final byte type) {
    flResultType.setCode(type);
  }


  /**
   * Sets value of the result description field.
   *
   * @param descr description
   */
  protected final void setResultDecription(final String descr) {
    flDescr.setValue(descr);
  }


  /**
   * Returns build ID
   */
  public final int getBuildID() {
    return buildID;
  }


  public final void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Returns loaded result config ID. Returns ResultConfig.UNSAVED_ID
   * if not loaded/new.
   */
  public final int getResultConfigID() {
    return resultConfigID;
  }


  /**
   * Returns current error list.
   */
  protected final List getErrors() {
    return errors;
  }


  /**
   * Returns grid iterator
   */
  public final GridIterator getGridIter() {
    return gridIter;
  }


  /**
   * Loads result configuration
   *
   * @param resultConfig
   */
  public final void load(final ResultConfig resultConfig) {
    buildID = resultConfig.getBuildID();
    resultConfigID = resultConfig.getID();
    flDescr.setValue(resultConfig.getDescription());
    flPath.setValue(resultConfig.getPath());
    flPublish.setGroupID(resultConfig.getAutopublishGroupID());
    flFailIfNotFound.setChecked(resultConfig.isFailIfNotFound());
    flIgnoreTimestamp.setChecked(resultConfig.isIgnoreTimestamp());
    flShellVariable.setValue(resultConfig.getShellVariable());
    loadProperties(resultConfig);
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should display a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public final boolean save() {
    if (buildID == BuildConfig.UNSAVED_ID) {
      showErrorMessage("Build is not defined for this result");
      return false;
    }
    if (log.isDebugEnabled()) log.debug("saving resultConfigID: " + resultConfigID);
    final ResultConfig resultConfig;
    if (resultConfigID == ResultConfig.UNSAVED_ID) {
      // create new
      resultConfig = new ResultConfig();
      resultConfig.setBuildID(buildID);
      resultConfig.setType(getResultType());
    } else {
      // get from db
      resultConfig = (ResultConfig) cm.getObject(ResultConfig.class, resultConfigID);
    }
    resultConfig.setDescription(flDescr.getValue());
    resultConfig.setPath(flPath.getValue().trim());
    resultConfig.setAutopublishGroupID(flPublish.getGroupID());
    resultConfig.setFailIfNotFound(flFailIfNotFound.isChecked());
    resultConfig.setIgnoreTimestamp(flIgnoreTimestamp.isChecked());
    resultConfig.setShellVariable(flShellVariable.getValue());
    cm.saveObject(resultConfig);
    return saveProperties(resultConfig);
  }


  /**
   * @return result type
   */
  private byte getResultType() {
    return (byte) flResultType.getCode();
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public final boolean validate() {
    super.clearMessage();
    errors.clear();

    // basic validation
    if (getResultType() <= 0) {
      errors.add("Result type is not defined for this result");
    }
    if (WebuiUtils.isBlank(flDescr)) {
      errors.add("Please provide result description");
    }
    validatePath(errors, flPath);

    if (!WebuiUtils.isBlank(flShellVariable)) {
      WebuiUtils.validateFieldStrict(errors, CAPTION_SHELL_VARIABLE, flShellVariable);
    }

    // call implementor's validateProperties first
    if (validateProperties() && errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }


  /**
   * Some result setting panels may want to overwrite this method.
   *
   * @param errors
   * @param flPath
   * @see URLResultConfigPanel
   */
  protected void validatePath(final List errors, final AbstractInput flPath) {
    if (WebuiUtils.isBlank(flPath)) {
      this.errors.add("Please provide path to result file(s) relative to source line root");
    }
  }


  /**
   * Loads properties associated with the given resultConfig.
   *
   * @param resultConfig for which to load properties.
   */
  public void loadProperties(final ResultConfig resultConfig) {
    final List props = cm.getResultConfigProperties(resultConfig.getID());
    inputMap.setProperties(props);
  }


  /**
   * Saves result configuration properties.
   * <p/>
   * This method should be implemented by extending class.
   *
   * @param resultConfig - Result config for which result properties are
   *                     being saved.
   * @return true if valid
   * @see ResultConfig
   * @see ResultConfigProperty
   */
  public boolean saveProperties(final ResultConfig resultConfig) {
    cm.saveResultConfigProperties(resultConfig.getID(), inputMap.getUpdatedProperties());
    return true;
  }


  /**
   * Validates result configuration properties.
   * <p/>
   * This method should be implemented by extending class.
   *
   * @return true if valid
   * @see ResultConfigProperty
   */
  public abstract boolean validateProperties();


  /**
   * Factory method to create ResultConfigProperty handler to be
   * used by propertyToInputMap
   *
   * @return implementation of PropertyToInputMap.PropertyHandler
   * @see PropertyToInputMap.PropertyHandler
   */
  private static PropertyToInputMap.PropertyHandler<ResultConfigProperty> makePropertyHandler() {
    return new PropertyToInputMap.PropertyHandler<ResultConfigProperty>() {
      private static final long serialVersionUID = -8287032735096311211L;


      public ResultConfigProperty makeProperty(final String propertyName) {
        final ResultConfigProperty prop = new ResultConfigProperty();
        prop.setName(propertyName);
        return prop;
      }


      public void setPropertyValue(final ResultConfigProperty property, final String propertyValue) {
        property.setValue(propertyValue);
      }


      public String getPropertyValue(final ResultConfigProperty property) {
        return property.getValue();
      }


      public String getPropertyName(final ResultConfigProperty property) {
        return property.getName();
      }
    };
  }


  public String toString() {
    return "AbstractResultConfigPanel{" +
            "cm=" + cm +
            ", resultConfigID=" + resultConfigID +
            ", buildID=" + buildID +
            ", lbShellVariable=" + lbShellVariable +
            ", flPublish=" + flPublish +
            ", flFailIfNotFound=" + flFailIfNotFound +
            ", flIgnoreTimestamp=" + flIgnoreTimestamp +
            ", flDescr=" + flDescr +
            ", flPath=" + flPath +
            ", flResultType=" + flResultType +
            ", flShellVariable=" + flShellVariable +
            ", gridIter=" + gridIter +
            ", errors=" + errors +
            ", inputMap=" + inputMap +
            '}';
  }
}
