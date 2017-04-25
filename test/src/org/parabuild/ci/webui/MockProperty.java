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
package org.parabuild.ci.webui;

/**
 *
 * Mock property class used by SATestPropertyToInputMap
 *
 * @see SATestPropertyToInputMap
 */
public class MockProperty {

  private String propertyName = null;
  private String propertyValue = null;


  public String getPropertyName() {
    return propertyName;
  }


  public void setPropertyName(final String propertyName) {
    this.propertyName = propertyName;
  }


  public String getPropertyValue() {
    return propertyValue;
  }


  public void setPropertyValue(final String propertyValue) {
    this.propertyValue = propertyValue;
  }
}
