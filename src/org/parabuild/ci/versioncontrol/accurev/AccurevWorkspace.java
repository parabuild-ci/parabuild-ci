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
package org.parabuild.ci.versioncontrol.accurev;

/**
 * AccurevWorkspace
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 13, 2009 2:28:53 PM
 */
final class AccurevWorkspace {

  private final String name;
  private final String storage;
  private final String depot;
  private final String host;


  AccurevWorkspace(final String name, final String storage, final String depot, final String host) {
    this.name = name;
    this.storage = storage;
    this.depot = depot;
    this.host = host;
  }


  public String getName() {
    return name;
  }


  public String getStorage() {
    return storage;
  }


  public String getDepot() {
    return depot;
  }


  public String getHost() {
    return host;
  }


  public String toString() {
    return "AccurevWorkspace{" +
            "name='" + name + '\'' +
            ", storage='" + storage + '\'' +
            ", depot='" + depot + '\'' +
            ", host='" + host + '\'' +
            '}';
  }
}
