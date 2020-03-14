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

import org.parabuild.ci.common.PropertyToInputMap;
import org.parabuild.ci.object.BuildConfigAttribute;


public final class BuildAttributeHandler implements PropertyToInputMap.PropertyHandler<BuildConfigAttribute> {

  private static final long serialVersionUID = 8137627325321368045L;


  public BuildConfigAttribute makeProperty(final String propertyName) {
    final BuildConfigAttribute prop = new BuildConfigAttribute();
    prop.setPropertyName(propertyName);
    return prop;
  }


  /**
   * Sets the value of the property from the given String.
   */
  @Override
  public void setPropertyValue(final BuildConfigAttribute property, final String propertyValue) {
    property.setPropertyValue(propertyValue);
  }


  /**
   * Returns string value from the given property.
   */
  @Override
  public String getPropertyValue(final BuildConfigAttribute property) {
    return property.getPropertyValue();
  }


  /**
   * Returns string name from the given property.
   */
  @Override
  public String getPropertyName(final BuildConfigAttribute property) {
    return property.getPropertyName();
  }
}
