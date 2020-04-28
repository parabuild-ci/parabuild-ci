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
package org.parabuild.ci.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.ValidationException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * NamedPropertyStringGenerator is responsible for generating
 * string from templates containing properties. A typical
 * template is "Step ${step.name} was ${step.result}."
 */
public final class NamedPropertyStringGenerator {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(NamedPropertyStringGenerator.class); // NOPMD

//  private static final String DATE_FORMAT = "yyyyMMdd";
  private static final String TIME_STAMP_FORMAT = "yyyyMMddHHmmss";

  private final Map propertyDefinitions = new HashMap(3);
  private final Map propertyValues = new HashMap(11);
  private final boolean strictResultRequired;
  private final String template;


  /**
   *
   * @param namedProperties
   *
   * @param template
   * @param strictOutput - if true an output should be a strict value.
   *
   * @throws IllegalArgumentException if a duplicate named
   *  property is found in namedProperties.
   */
  public NamedPropertyStringGenerator(final NamedProperty[] namedProperties, final String template, final boolean strictOutput) throws IllegalArgumentException {
    for (int i = 0; i < namedProperties.length; i++) {
      final NamedProperty namedProperty = namedProperties[i];
      final String key = namedProperty.getPropertyName().toLowerCase();
      if (propertyDefinitions.containsKey(key)) throw new IllegalArgumentException("Duplicate property defintion: " + namedProperty.toString());
      propertyDefinitions.put(key, namedProperty);
    }
    this.template = ArgumentValidator.validateArgumentNotBlank(template, "template");
    this.strictResultRequired = strictOutput;
  }


  /**
   * Generates string.
   *
   * @return
   * @throws ValidationException
   */
  public String generate() throws ValidationException {

    validate();

    try {

      // resolve
      // TODO: replaceProperties should not be throwing BuildException
      final String result = NamedPropertyUtils.replaceProperties(template, propertyValues);

      // valide if result is strinct
      validateIsStrictResult(result);

      return result;
    } catch (final BuildException e) {
      throw new ValidationException(StringUtils.toString(e));
    }
  }


  /**
   * Validates property values.
   *
   * @throws ValidationException if manadatory properties are not set.
   */
  private void validateMandatoryProperties() throws ValidationException {
    // validate required properties are present
    for (final Iterator i = propertyDefinitions.values().iterator(); i.hasNext();) {
      final NamedProperty namedProperty = (NamedProperty)i.next();
//      if (log.isDebugEnabled()) log.debug("namedProperty: " + namedProperty);
      if (namedProperty.isMandatory() && !propertyValues.containsKey(namedProperty.getPropertyName())) {
        throw new ValidationException("Mandatory property \"" + namedProperty.getPropertyName() + "\" is not set.");
      }
    }
  }


  private void validateIsStrictResult(final String result) throws ValidationException {
    if (strictResultRequired) {
      if (!StringUtils.isValidStrictName(result)) {
        throw new ValidationException("Result \"" + result + "\" created from template \"" + template + "\" is not a valid strict string.");
      }
    }
  }


  public void validate() throws ValidationException {
    final List properties = new ArrayList(7);
    final List fragments = new ArrayList(7);
    try {
      NamedPropertyUtils.parsePropertyString(template, fragments, properties);
    } catch (final BuildException e) {
      throw new ValidationException(StringUtils.toString(e));
    }

    // check that prop names the template are supported
    for (final Iterator i = properties.iterator(); i.hasNext();) {
      final String key = ((String)i.next()).trim().toLowerCase();
      // TODO: add a list of supported properties.
      if (!propertyDefinitions.containsKey(key)) throw new ValidationException("Property \"" + key + "\" is not supported");
    }

    validateMandatoryProperties();

    for (int i = 0, n = fragments.size(); i < n; i++) {
      validateIsStrictResult((String)fragments.get(i));
    }
  }


  public boolean isTemplateStatic() {
    try {
      final ArrayList properties = new ArrayList(3);
      NamedPropertyUtils.parsePropertyString(template, new ArrayList(7), properties);
      for (final Iterator i = properties.iterator(); i.hasNext();) {
        final String propertyName = ((String)i.next()).toLowerCase();
        final NamedProperty namedPropery = (NamedProperty)propertyDefinitions.get(propertyName);
        if (namedPropery.isDynamic()) return false;
      }
    } catch (final Exception e) {
      // return static because it is safer to assume that it is
      // static
      return true;
    }
    return true;
  }


  public static SimpleDateFormat getTimeStampFormatter() {
    return new SimpleDateFormat(TIME_STAMP_FORMAT, Locale.US);
  }


  /**
   * Sets property value in the internal map. Property name is used as a key.
   *
   * @param name to set
   * @param value to set
   */
  public void setPropertyValue(final String name, final String value) {
    ArgumentValidator.validateArgumentNotBlank(name, "property name");
    ArgumentValidator.validateArgumentNotBlank(value, "property value for property \"" + name + '\"');
    propertyValues.put(name.trim().toLowerCase(), value);
  }


  /**
   * @return true if trict result required is set
   */
  public boolean isStrictResultRequired() {
    return strictResultRequired;
  }


  /**
   * Sets integer property value.
   *
   * @param name to set
   * @param value to set
   */
  public void setPropertyValue(final String name, final int value) {
    setPropertyValue(name, Integer.toString(value));
  }


  public String toString() {
    return "NamedPropertyStringGenerator{" +
      "propertyDefinitions=" + propertyDefinitions +
      ", propertyValues=" + propertyValues +
      ", strictResultRequired=" + strictResultRequired +
      ", template='" + template + '\'' +
      '}';
  }
}
