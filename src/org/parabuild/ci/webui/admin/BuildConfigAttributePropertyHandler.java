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

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;


final class BuildConfigAttributePropertyHandler implements PropertyToInputMap.PropertyHandler {

  public Object makeProperty(final String propertyName) {
    final BuildConfigAttribute prop = new BuildConfigAttribute();
    prop.setPropertyName(propertyName);
    return prop;
  }


  public void setPropertyValue(final Object property, final String propertyValue) {
    ((BuildConfigAttribute)property).setPropertyValue(propertyValue);
  }


  public String getPropertyValue(final Object property) {
    return ((BuildConfigAttribute)property).getPropertyValue();
  }


  public String getPropertyName(final Object property) {
    return ((BuildConfigAttribute)property).getPropertyName();
  }
}
