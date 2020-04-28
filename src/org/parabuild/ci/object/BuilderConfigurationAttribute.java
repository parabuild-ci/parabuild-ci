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
package org.parabuild.ci.object;

/**
 * BuilderConfigurationAttribute
 * <p/>
 *
 * @author Slava Imeshev
 * @since Sep 28, 2008 2:02:47 PM
 */
public final class BuilderConfigurationAttribute {

  private String name;
  private String value;


  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  public String getValue() {
    return value;
  }


  public void setValue(final String value) {
    this.value = value;
  }


  public String toString() {
    return "BuilderConfigurationAttribute{" +
            "name='" + name + '\'' +
            ", value='" + value + '\'' +
            '}';
  }
}
