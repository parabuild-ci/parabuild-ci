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
package org.parabuild.ci.versioncontrol;

/**
 * Common parameters used to run StarTeam commands
 */
class StarTeamCommandParameters {

  private String exePath;
  private String user;
  private String password;
  private String address;
  private String project;
  private int port = 49201;
  private byte encryption;


  final String getExePath() {
    return exePath;
  }


  final void setExePath(final String exePath) {
    this.exePath = exePath;
  }


  final String getUser() {
    return user;
  }


  final void setUser(final String user) {
    this.user = user;
  }


  final String getPassword() {
    return password;
  }


  final void setPassword(final String password) {
    this.password = password;
  }


  final String getAddress() {
    return address;
  }


  final void setAddress(final String address) {
    this.address = address;
  }


  final int getPort() {
    return port;
  }


  final void setPort(final int port) {
    this.port = port;
  }


  final byte getEncryption() {
    return encryption;
  }


  final void setEncryption(final byte encryption) {
    this.encryption = encryption;
  }


  final String getProject() {
    return project;
  }


  final void setProject(final String project) {
    this.project = project;
  }
}
