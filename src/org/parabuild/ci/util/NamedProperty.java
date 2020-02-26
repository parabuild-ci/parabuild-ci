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

/**
 * A value object to define a named property.
 *
 * @see NamedPropertyStringGenerator
 */
public final class NamedProperty {

  private final String propertyName;
  private final boolean mandatory;
  private final boolean strictValue;
  private final boolean dynamic;


  /**
   * Creates NamedProperty.
   *
   * @param propertyName
   * @param mandatory
   * @param strictValue
   * @param dynamic if true the property changes its value with next build run
   */
  public NamedProperty(final String propertyName, final boolean mandatory, final boolean strictValue, final boolean dynamic) {
    this.dynamic = dynamic;
    this.propertyName = ArgumentValidator.validateArgumentNotBlank(propertyName, "property name").trim().toLowerCase();
    this.mandatory = mandatory;
    this.strictValue = strictValue;
  }


  public String getPropertyName() {
    return propertyName;
  }


  public boolean isMandatory() {
    return mandatory;
  }


  public boolean isStrictValue() {
    return strictValue;
  }


  public boolean isDynamic() {
    return dynamic;
  }


  public String toString() {
    return "NamedProperty{" +
      "propertyName='" + propertyName + '\'' +
      ", mandatory=" + mandatory +
      ", stictValue=" + strictValue +
      ", dynamic=" + dynamic +
      '}';
  }
}
