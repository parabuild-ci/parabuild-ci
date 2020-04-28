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
package org.parabuild.ci.versioncontrol.mks;

/**
 * Class to hold common MKS command paramters.
 */
class MKSCommandParameters {

  private int port = 7001;
  private String host;
  private String exePath;
  private String user;
  private String project;
  private String password;


  public String getProject() {
    return project;
  }


  public void setProject(final String project) {
    this.project = project;
  }


  public String getDevelopmentPath() {
    return developmentPath;
  }


  public void setDevelopmentPath(final String developmentPath) {
    this.developmentPath = developmentPath;
  }


  private String developmentPath;


  public String getUser() {
    return user;
  }


  public void setUser(final String user) {
    this.user = user;
  }


  public String getPassword() {
    return password;
  }


  public void setPassword(final String password) {
    this.password = password;
  }


  public String getExePath() {
    return exePath;
  }


  public void setExePath(final String exePath) {
    this.exePath = exePath;
  }


  public String getHost() {
    return host;
  }


  public void setHost(final String host) {
    this.host = host;
  }


  public int getPort() {
    return port;
  }


  public void setPort(final int port) {
    this.port = port;
  }


  /**
   * @return name-only parth of the project path.
   *
   * @see #getProject()
   */
  public final String getProjectName() {
    final String projectPath = project.replace('\\', '/');
    return projectPath.substring(projectPath.lastIndexOf('/') + 1);
  }
}
