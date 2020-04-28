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
 */
class PVCSCommandParameters {

  private String user;
  private String password;
  private String pathToClient;
  private String repository;
  private String project;
  private String branch;
  private String label;
  private String promotionGroup;


  public PVCSCommandParameters() {
  }


  /**
   * Copy constructor.
   */
  public PVCSCommandParameters(final PVCSCommandParameters parameters) {
    this.branch = parameters.branch;
    this.label = parameters.label;
    this.password = parameters.password;
    this.pathToClient = parameters.pathToClient;
    this.project = parameters.project;
    this.promotionGroup = parameters.promotionGroup;
    this.repository = parameters.repository;
    this.user = parameters.user;
  }


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


  public String getPathToClient() {
    return pathToClient;
  }


  public void setPathToClient(final String pathToClient) {
    this.pathToClient = pathToClient;
  }


  public String getRepository() {
    return repository;
  }


  public void setRepository(final String repository) {
    this.repository = repository;
  }


  public String getProject() {
    return project;
  }


  public void setProject(final String project) {
    this.project = project;
  }


  public String getBranch() {
    return branch;
  }


  public void setBranch(final String branch) {
    this.branch = branch;
  }


  public String getLabel() {
    return label;
  }


  public void setLabel(final String label) {
    this.label = label;
  }


  public String getPromotionGroup() {
    return promotionGroup;
  }


  public void setPromotionGroup(final String promotionGroup) {
    this.promotionGroup = promotionGroup;
  }
}
