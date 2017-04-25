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
package org.parabuild.ci.webservice.templ;

import java.io.Serializable;

/**
 *
 * @noinspection UnusedDeclaration
 */
public final class SourceControlSettingOverride implements Serializable {

  private static final long serialVersionUID = 0L;
  
  private String name = null;
  private String value = null;
  private boolean nativeChangeListNumber = false;


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


  public boolean isNativeChangeListNumber() {
    return nativeChangeListNumber;
  }


  public void setNativeChangeListNumber(final boolean nativeChangeListNumber) {
    this.nativeChangeListNumber = nativeChangeListNumber;
  }


  public String toString() {
    return "SourceControlSettingOverride{" +
            "name='" + name + '\'' +
            ", nativeChangeListNumber=" + nativeChangeListNumber +
            ", value='" + value + '\'' +
            '}';
  }
}