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
 * A Perforce Branch Specification
 */
final class P4BranchViewImpl implements P4BranchView {

  private final String branch;
  private final String update;
  private final String access;
  private final String owner;
  private final String description;
  private final String options;
  private final String view;


  /**
   * Constructor.
   *
   * @param branch
   * @param update
   * @param access
   * @param owner
   * @param description
   * @param options
   * @param view
   */
  public P4BranchViewImpl(final String branch, final String update,
    final String access, final String owner, final String description,
    final String options, final String view) {

    this.branch = branch;
    this.update = update;
    this.access = access;
    this.owner = owner;
    this.description = description;
    this.options = options;
    this.view = view;
  }


  /**
   * @return The branch name
   */
  public String branch() {
    return branch;
  }


  /**
   * @return The date this specification was last modified.
   */
  public String update() {
    return update;
  }


  /**
   * @return The date of the last 'integrate' using this branch.
   */
  public String access() {
    return access;
  }


  /**
   * @return The user who created this branch.
   */
  public String owner() {
    return owner;
  }


  /**
   * @return A short description of the branch (optional).
   */
  public String description() {
    return description;
  }


  /**
   * @return Branch update options: [un]locked [in]direct.
   */
  public String options() {
    return options;
  }


  /**
   * @return Lines to map source depot files to target depot files.
   */
  public String view() {
    return view;
  }


  public String toString() {
    return "P4BranchViewImpl{" +
      "branch='" + branch + '\'' +
      ", update='" + update + '\'' +
      ", access='" + access + '\'' +
      ", owner='" + owner + '\'' +
      ", description='" + description + '\'' +
      ", options='" + options + '\'' +
      ", view='" + view + '\'' +
      '}';
  }
}
