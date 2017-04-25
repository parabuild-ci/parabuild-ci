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

import org.parabuild.ci.common.StringUtils;

/**
 * Immutable Accurev command parameters.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 12, 2009 5:47:03 PM
 */
final class AccurevCommandParameters {

  private final int port;
  private final String depot;
  private final String exePath;
  private final String host;
  private final String password;
  private final String path;
  private final String stream;
  private final String user;
  private byte kind;
  private byte eolType;


  AccurevCommandParameters(final String exePath, final String host, final String password,
                           final int port, final String stream, final String user, final String path,
                           final String depot, final byte kind, final byte eolType) {
    this.kind = kind;
    this.eolType = eolType;
    this.depot = depot;
    this.exePath = exePath;
    this.host = host;
    this.password = password;
    this.path = path;
    this.port = port;
    this.stream = stream;
    this.user = user;
  }


  public String getExePath() {
    return exePath;
  }


  public String getHost() {
    return host;
  }


  public int getPort() {
    return port;
  }


  public String getUser() {
    return user;
  }


  public String getPassword() {
    return password;
  }


  public String getStream() {
    return stream;
  }


  public String getPath() {
    return path;
  }


  public String toString() {
    return "AccurevCommandParameters{}";
  }


  public String getDepot() {
    return depot;
  }


  public boolean isStreamSet() {
    return !StringUtils.isBlank(stream);
  }


  public byte getKind() {
    return kind;
  }


  public byte getEolType() {
    return eolType;
  }



  public String getBackingStream() {
    if (isStreamSet()) {
      return stream;
    }
    return depot;
  }
}
