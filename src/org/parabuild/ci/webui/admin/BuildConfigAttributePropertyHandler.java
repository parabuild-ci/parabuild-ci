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


final class BuildConfigAttributePropertyHandler implements PropertyToInputMap.PropertyHandler<BuildConfigAttribute> {

  private static final long serialVersionUID = 999666865186383972L;


  public BuildConfigAttribute makeProperty(final String propertyName) {
    final BuildConfigAttribute prop = new BuildConfigAttribute();
    prop.setPropertyName(propertyName);
    return prop;
  }


  public void setPropertyValue(final BuildConfigAttribute property, final String propertyValue) {
    property.setPropertyValue(propertyValue);
  }


  public String getPropertyValue(final BuildConfigAttribute property) {
    return property.getPropertyValue();
  }


  public String getPropertyName(final BuildConfigAttribute property) {
    return property.getPropertyName();
  }
}
