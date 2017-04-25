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
package org.parabuild.ci.tray;

/**
 *
 */
final class StatusMapKey {

  private String hostPort = null;
  private Integer buildID = null;


  public StatusMapKey(final String hostPort, final Integer buildID) {
    this.hostPort = hostPort;
    this.buildID = buildID;
  }


  public StatusMapKey(final String hostPort, final int buildID) {
    this(hostPort, new Integer(buildID));
  }


  public String getHostPort() {
    return hostPort;
  }


  public Integer getBuildID() {
    return buildID;
  }


  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final StatusMapKey key = (StatusMapKey)o;

    if (!buildID.equals(key.buildID)) return false;
    if (!hostPort.equals(key.hostPort)) return false;

    return true;
  }


  public int hashCode() {
    int result = hostPort.hashCode();
    result = 29 * result + buildID.hashCode();
    return result;
  }
}


