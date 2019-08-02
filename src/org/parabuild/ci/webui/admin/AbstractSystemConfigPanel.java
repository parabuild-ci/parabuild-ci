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

import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;

import java.util.List;

/**
 * Panel to be used when editing flat system properties
 */
public abstract class AbstractSystemConfigPanel extends MessagePanel implements Validatable, Saveable { // NOPMD - "This abstract class does not have any abstract methods"

  private static final long serialVersionUID = 1124106183098619030L; // NOPMD

  protected final PropertyToInputMap inputMap;


  /**
   * Creates message panel without title.
   */
  public AbstractSystemConfigPanel() {
    inputMap = new PropertyToInputMap(false, makePropertyHandler()); // strict map
  }


  /**
   * Sets system properties supplied as a map.
   * Keys are defined by SystemProperty class.
   *
   * @param properties
   */
  public final void setSystemProperties(final List properties) {
    inputMap.setProperties(properties);
  }


  /**
   * Returns system properties
   */
  public final List getSystemProperties() {
    return inputMap.getUpdatedProperties();
  }


  /**
   * Factory method to create SystemProperty handler to be used by propertyToInputMap
   *
   * @return implementation of PropertyToInputMap.PropertyHandler
   * @see PropertyToInputMap.PropertyHandler
   */
  private static PropertyToInputMap.PropertyHandler makePropertyHandler() {
    return new PropertyToInputMap.PropertyHandler() {
      private static final long serialVersionUID = 4389392135587742283L;


      public Object makeProperty(final String propertyName) {
        final SystemProperty prop = new SystemProperty();
        prop.setPropertyName(propertyName);
        return prop;
      }


      public void setPropertyValue(final Object property, final String propertyValue) {
        ((SystemProperty) property).setPropertyValue(propertyValue);
      }


      public String getPropertyValue(final Object property) {
        return ((SystemProperty) property).getPropertyValue();
      }


      public String getPropertyName(final Object property) {
        return ((SystemProperty) property).getPropertyName();
      }
    };
  }


  /**
   * When called, the panel should switch to the
   * corresponding mode.
   *
   * @param viewMode to set.
   */
  public abstract void setMode(final byte viewMode);


  /**
   * Requests to load panel's data
   */
  public abstract void load();


  public boolean save() {
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    systemCM.saveSystemProperties(getSystemProperties());
    return true;
  }


  public String toString() {
    return "AbstractSystemConfigPanel{" +
            "inputMap=" + inputMap +
            '}';
  }
}
