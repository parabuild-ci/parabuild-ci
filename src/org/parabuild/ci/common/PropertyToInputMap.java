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
package org.parabuild.ci.common;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This map is used to simplify access to fields via their
 * corresponding property names
 */
public final class PropertyToInputMap<T> implements Serializable {

  private static final long serialVersionUID = 2416070308102742329L; // NOPMD

  private final HashMap<String, HasInputValue> propertyNameToInputMap = new HashMap<String, HasInputValue>(5);
  private final HashMap<String, T> propertyNameToPropertyObjectMap = new HashMap<String, T>(5);
  private boolean updateOnlyFromEditableFields = false;
  private boolean loadUnmappedProperties = true;
  private PropertyHandler<T> propertyHandler;


  /**
   * Creates PropertyToInput map. Validation for acceptability of
   * the property name will not be done.
   */
  public PropertyToInputMap(final PropertyHandler<T> propertyHandler) {
    this.propertyHandler = propertyHandler;
  }


  /**
   * Creates PropertyToInput map. If strict == true, property
   * names will be validated that are accepted by the map.
   */
  public PropertyToInputMap(final boolean loadUnmappedProperties, final PropertyHandler<T> propertyHandler) {
    this.propertyHandler = propertyHandler;
    this.loadUnmappedProperties = loadUnmappedProperties;
  }


  public PropertyToInputMap() {
  }


  /**
   * Binds property name to input
   *
   * @param propertyName to associate with input
   * @param input        propertyName will be associated with
   */
  public void bindPropertyNameToInput(final String propertyName, final HasInputValue input) {
    propertyNameToInputMap.put(propertyName, input);
  }


  private void setInputValue(final String propertyName, final String value) {

    final HasInputValue input = propertyNameToInputMap.get(propertyName);
    if (input != null) {
      input.setInputValue(value);
    }
  }


  /**
   * Returns properties list updated with values from input
   * fields. If a property wasn't in the original list, it will
   * be added to the list.
   * <p/>
   * Process of making new properties and setting their values is
   * handled buy custom implementation of the PropertyHandler
   * interface.
   *
   * @see PropertyHandler
   */
  public List<T> getUpdatedProperties() {

    final List<T> result = new ArrayList<T>(11);
    // traverse property name to input map
    for (final Map.Entry<String, HasInputValue> stringHasInputValueEntry : propertyNameToInputMap.entrySet()) {
      // get prop name and associated input
      final String propName = stringHasInputValueEntry.getKey();
      final HasInputValue input = stringHasInputValueEntry.getValue();
      if (updateOnlyFromEditableFields && !input.isInputEditable()) {
        continue;
      }
      // lookup property value, if any
      T property = propertyNameToPropertyObjectMap.get(propName);
      if (property == null) {
        // there is no property in the resulting list,
        // create new one and set values
        property = propertyHandler.makeProperty(propName);
      }
      // set value
      final String value = input.getInputValue();
      propertyHandler.setPropertyValue(property, value);
      result.add(property);
    }

    // add properties that were listed but weren't bound/mapped
    for (final Map.Entry<String, T> stringTEntry : propertyNameToPropertyObjectMap.entrySet()) {
      if (!propertyNameToInputMap.containsKey(stringTEntry.getKey())) {
        result.add(stringTEntry.getValue());
      }
    }
    return result;
  }


  /**
   * Sets properties list and sets inputs values from this list
   *
   * @param props the properties list.
   */
  public void setProperties(final List<T> props) {
    for (final T property : props) {
      final String propertyName = propertyHandler.getPropertyName(property);
      // don't load if it's unmapped
      if (!loadUnmappedProperties && propertyNameToInputMap.get(propertyName) == null) {
        continue;
      }
      final String propertyValue = propertyHandler.getPropertyValue(property);
      propertyNameToPropertyObjectMap.put(propertyName, property);
      setInputValue(propertyName, propertyValue);
    }
  }


  /**
   * Sets a a flag controlling updates from fields to
   * properties.
   *
   * @param updateOnlyFromEditableFields If true {@link #getUpdatedProperties()}
   *                                     will return values only for editable fields.
   */
  public void setUpdateOnlyFromEditableFields(final boolean updateOnlyFromEditableFields) {
    this.updateOnlyFromEditableFields = updateOnlyFromEditableFields;
  }


  /**
   * @noinspection PublicInnerClass
   */
  public interface PropertyHandler<T> extends Serializable {

    /**
     * Creates new property using given propertyName and abstract
     * input as value source. Implementing class should assign
     * property name and value from property to the new
     * property.
     *
     * @return new property instance created using given propertyName and
     * abstract input as value source.
     */
    T makeProperty(String propertyName);


    /**
     * Implementing class should set value of the property from
     * given String
     */
    void setPropertyValue(T property, String propertyValue);


    /**
     * Implementing class should return string value from the
     * given property.
     */
    String getPropertyValue(T property);


    /**
     * Implementing class should return string name from the
     * given property.
     */
    String getPropertyName(T property);
  }


  public String toString() {
    return "PropertyToInputMap{" +
            "propertyNameToInputMap=" + propertyNameToInputMap +
            ", propertyNameToPropertyObjectMap=" + propertyNameToPropertyObjectMap +
            ", loadUnmappedProperties=" + loadUnmappedProperties +
            ", propertyHandler=" + propertyHandler +
            ", updateOnlyFromEditableFields=" + updateOnlyFromEditableFields +
            '}';
  }
}
