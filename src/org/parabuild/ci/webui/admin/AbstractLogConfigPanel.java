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
import org.parabuild.ci.common.PropertyToInputMap;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.LogConfigProperty;
import org.parabuild.ci.webui.common.CommonCheckBox;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract log configuration panel. This class realizes Strategy
 * pattern.
 * <p/>
 * Abstract Log config panel consists of common elements
 * including name of the log and path. Implementors may add there
 * own properties in their constructors.
 * <p/>
 * Implementing classes should take care about implementing
 * needed methods and adding there own functionality.
 *
 * @noinspection FieldCanBeLocal
 * @see LogConfigProperty
 */
public abstract class AbstractLogConfigPanel extends MessagePanel implements Validatable, Saveable {

  private static final Log log = LogFactory.getLog(AbstractLogConfigPanel.class);
  private static final long serialVersionUID = -8826234013978092186L;
  //
  protected final ConfigurationManager cm = ConfigurationManager.getInstance();
  protected int logConfigID = LogConfig.UNSAVED_ID;
  protected int buildID = BuildConfig.UNSAVED_ID;

  private static final String NAME_TYPE = "Log  type:";
  private static final String NAME_DESCRIPTION = "Description:";
  private static final String NAME_LOG_PATH = "Log  path:";
  private static final String CAPTION_IGNORE_TIMESTAMP = "Ignore  timestamp:";

  // fields
  private final CommonField flDescr = new CommonField(60, 20);
  private final CommonField flPath = new CommonField(250, 45);
  private final LogTypeDropDown flLogType = new LogTypeDropDown();
  private final CommonCheckBox flIgnoreTimestamp = new CommonCheckBox();

  // GI
  private final GridIterator gridIter = new GridIterator(super.getUserPanel(), 4);
  private final List errors = new ArrayList(2);

  // property handling support
  protected final PropertyToInputMap inputMap = new PropertyToInputMap(false, makePropertyHandler()); // strict map


  public AbstractLogConfigPanel(final boolean enableContentBorder) {
    super(enableContentBorder);
    super.showHeaderDivider(true);
    flLogType.setEditable(false); // read only, serves as a display label for build type
    flLogType.setFont(Pages.FONT_COMMON_LABEL);
    gridIter.addPair(new CommonFieldLabel(NAME_TYPE), flLogType);
    gridIter.addPair(new CommonFieldLabel(CAPTION_IGNORE_TIMESTAMP), flIgnoreTimestamp);
    gridIter.addPair(new CommonFieldLabel(NAME_DESCRIPTION), new RequiredFieldMarker(flDescr));
    gridIter.addPair(new CommonFieldLabel(NAME_LOG_PATH), new RequiredFieldMarker(flPath));
    inputMap.bindPropertyNameToInput(LogConfigProperty.ATTR_IGNORE_TIMESTAMP, flIgnoreTimestamp);
  }


  /**
   * Inheriting class should set log type at construction time.
   *
   * @param type log type to set.
   */
  public final void setLogType(final byte type) {
    flLogType.setCode(type);
  }


  /**
   * Sets value of the log description field.
   *
   * @param descr description
   */
  protected final void setLogDescription(final String descr) {
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
   * Returns loaded log config ID. Returns LogConfig.UNSAVED_ID
   * if not loaded/new.
   */
  public final int getLogConfigID() {
    return logConfigID;
  }


  /**
   * Returns current error list.
   */
  public final List getErrors() {
    return errors;
  }


  /**
   * Returns grid iterator
   */
  public final GridIterator getGridIter() {
    return gridIter;
  }


  /**
   * Loads log configuration
   *
   * @param logConfig
   */
  public final void load(final LogConfig logConfig) {
    buildID = logConfig.getBuildID();
    logConfigID = logConfig.getID();
    flDescr.setValue(logConfig.getDescription());
    flPath.setValue(logConfig.getPath().trim());
    loadProperties(logConfig);
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
      showErrorMessage("Build is not defined for this log");
      return false;
    }
    if (log.isDebugEnabled()) log.debug("saving logConfigID: " + logConfigID);
    final LogConfig logConfig;
    if (logConfigID == LogConfig.UNSAVED_ID) {
      // create new
      logConfig = new LogConfig();
      logConfig.setBuildID(buildID);
      logConfig.setType(getLogType());
    } else {
      // get from db
      logConfig = cm.getLogConfig(logConfigID);
    }
    logConfig.setDescription(flDescr.getValue());
    logConfig.setPath(flPath.getValue().trim());
    cm.saveObject(logConfig);
    return saveProperties(logConfig);
  }


  /**
   * @return log type
   */
  private byte getLogType() {
    return (byte) flLogType.getCode();
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
    if (getLogType() <= (byte) 0) {
      errors.add("Log type is not defined for this log");
    }
    if (WebuiUtils.isBlank(flDescr)) {
      errors.add("Please provide log description");
    }
    if (WebuiUtils.isBlank(flPath)) {
      errors.add("Please provide path to log file(s) relative to source line root");
    }

    // call implementor's validateProperties first
    if (validateProperties() && errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }


  /**
   * Loads properties associated with the given logConfig.
   *
   * @param logConfig for which to load properties.
   */
  public void loadProperties(final LogConfig logConfig) {
    final List props = cm.getLogConfigProperties(logConfig.getID());
    inputMap.setProperties(props);
  }


  /**
   * Saves log configuration properties.
   * <p/>
   * This method should be implemented by extending class.
   *
   * @param logConfig - Log config for which log properties are
   *                  being saved.
   * @return true if valid
   * @see LogConfig
   * @see LogConfigProperty
   */
  public boolean saveProperties(final LogConfig logConfig) {
    cm.saveLogConfigProperties(logConfig.getID(), inputMap.getUpdatedProperties());
    return true;
  }


  /**
   * Validates log configuration properties.
   * <p/>
   * This method should be implemented by extending class.
   *
   * @return true if valid
   * @see LogConfigProperty
   */
  public abstract boolean validateProperties();


  /**
   * Factory method to create LogConfigProperty handler to be
   * used by propertyToInputMap
   *
   * @return implementation of PropertyToInputMap.PropertyHandler
   * @see PropertyToInputMap.PropertyHandler
   */
  private static PropertyToInputMap.PropertyHandler<LogConfigProperty> makePropertyHandler() {
    return new PropertyToInputMap.PropertyHandler<LogConfigProperty>() {
      private static final long serialVersionUID = -7559077787808194605L;


      public LogConfigProperty makeProperty(final String propertyName) {
        final LogConfigProperty prop = new LogConfigProperty();
        prop.setName(propertyName);
        return prop;
      }


      public void setPropertyValue(final LogConfigProperty property, final String propertyValue) {
        property.setValue(propertyValue);
      }


      public String getPropertyValue(final LogConfigProperty property) {
        return property.getValue();
      }


      public String getPropertyName(final LogConfigProperty property) {
        return property.getName();
      }
    };
  }


  public String toString() {
    return "AbstractLogConfigPanel{" +
            "cm=" + cm +
            ", logConfigID=" + logConfigID +
            ", buildID=" + buildID +
            ", flDescr=" + flDescr +
            ", flPath=" + flPath +
            ", flLogType=" + flLogType +
            ", gridIter=" + gridIter +
            ", errors=" + errors +
            ", inputMap=" + inputMap +
            '}';
  }
}
