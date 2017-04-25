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
package org.parabuild.ci.versioncontrol.perforce;

/**
 * Typed client name.
 */
public final class ClientNameImpl implements ClientName {

  private final String value;


  /**
   * Constuctor.
   *
   * @param value
   */
  public ClientNameImpl(final String value) {
    this.value = value;
  }


  /**
   * @return name
   */
  public String getValue() {
    return value;
  }


  public String toString() {
    return "ClientNameImpl{" +
      "value='" + value + '\'' +
      '}';
  }
}
